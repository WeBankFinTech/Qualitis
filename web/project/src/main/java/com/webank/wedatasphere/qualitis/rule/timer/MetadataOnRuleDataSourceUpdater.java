package com.webank.wedatasphere.qualitis.rule.timer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.RuleClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableMetadataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableTagInfo;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-02-27 15:29
 * @description 获取来自DMS的元数据子字段(子系统、开发部门、业务部门、表标签)，更新到RuleDataSource
 */
@Component
public class MetadataOnRuleDataSourceUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataOnRuleDataSourceUpdater.class);

    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private RuleClient ruleClient;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    public MetadataOnRuleDataSourceUpdater() {
        initScheduledTask();
    }

    private final long FIXED_INTERVAL_SECONDS = 10L;

    private final Integer MAX_TASK_SIZE = 1000;

    private final ScheduledExecutorService bossThreadDaemon = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder()
                    .namingPattern("MetadataOnRuleDataSourceUpdater-Boss-Thread-%d")
                    .daemon(true)
                    .build());

    private final LinkedBlockingQueue<MetadataOnRuleDataSourceTask> TASK_QUEUE = new LinkedBlockingQueue<>(MAX_TASK_SIZE);

    private Cache<String, RuleDataSource> ruleDataSourceLocalCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();

    @Autowired
    @Qualifier("dmsWorkerThreadPool")
    private ThreadPoolExecutor dmsWorkerThreadPool;

    private void initScheduledTask() {
        bossThreadDaemon.scheduleAtFixedRate(() -> {
            Map<String, String> clusterNameAndTypeMap = null;
            if (!TASK_QUEUE.isEmpty()) {
                LOGGER.info("Ready to execute task in the queue, size: {}", TASK_QUEUE.size());
                clusterNameAndTypeMap = getClusterNameAndTypeMap();
            }
            while (!TASK_QUEUE.isEmpty()) {
                MetadataOnRuleDataSourceTask ruleDataSourceTask = TASK_QUEUE.poll();
                dispatchTask(ruleDataSourceTask, clusterNameAndTypeMap);
            }
        }, FIXED_INTERVAL_SECONDS, FIXED_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void submit(List<RuleDataSource> ruleDataSources, String loginUser) {
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (Objects.isNull(ruleDataSource.getDatasourceType())
                    || StringUtils.isEmpty(ruleDataSource.getClusterName())
                    || StringUtils.isEmpty(ruleDataSource.getDbName())
                    || StringUtils.isEmpty(ruleDataSource.getTableName())) {
                continue;
            }
            boolean status = TASK_QUEUE.offer(new MetadataOnRuleDataSourceTask(ruleDataSource, loginUser));
            if (!status) {
                LOGGER.warn("The Queue is fulled, the task be rejected, ruleDataSourceId: {}", ruleDataSource.getId());
            }
        }
    }

    private void dispatchTask(MetadataOnRuleDataSourceTask ruleDataSourceTask, Map<String, String> clusterNameAndTypeMap) {
        CompletableFuture<RuleDataSource> completableFuture = CompletableFuture.supplyAsync(() ->
                this.getMetadataFieldsFromLocalCache(ruleDataSourceTask, clusterNameAndTypeMap), dmsWorkerThreadPool);
        completableFuture.whenComplete((ruleDataSource, throwable) -> {
            if (checkIfUpdate(ruleDataSource)) {
                clearNullString(ruleDataSource);
                LOGGER.info("Ready to update metadata, ruleDataSourceId: {}", ruleDataSourceTask.getRuleDataSourceId());
                ruleDataSourceDao.updateMetadataFields(ruleDataSourceTask.getRuleDataSourceId()
                        , ruleDataSource.getSubSystemId(), ruleDataSource.getSubSystemName()
                        , ruleDataSource.getDepartmentCode(), ruleDataSource.getDepartmentName()
                        , ruleDataSource.getDevDepartmentName(), ruleDataSource.getTagCode(), ruleDataSource.getTagName());
            }
        });
    }

    public void executeBatchUpdate(List<MetadataOnRuleDataSourceTask> ruleDataSourceTasks, Map<String, String> clusterNameAndTypeMap) {
        try {
            LOGGER.info("Ready to update metadata, total counts: {}", ruleDataSourceTasks.size());
            for (MetadataOnRuleDataSourceTask ruleDataSourceTask : ruleDataSourceTasks) {
                dispatchTask(ruleDataSourceTask, clusterNameAndTypeMap);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute update metadata ", e);
        }
    }

    private String cacheKey(MetadataOnRuleDataSourceTask ruleDataSource) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ruleDataSource.getDatasourceType());
        stringBuilder.append(ruleDataSource.getClusterName());
        stringBuilder.append(ruleDataSource.getDbName());
        stringBuilder.append(ruleDataSource.getTableName());
        return stringBuilder.toString();
    }

    private RuleDataSource getMetadataFieldsFromLocalCache(MetadataOnRuleDataSourceTask ruleDataSourceTask, Map<String, String> clusterNameAndTypeMap) {
        String cacheKey = this.cacheKey(ruleDataSourceTask);
        try {
            return ruleDataSourceLocalCache.get(cacheKey, () -> getMetadataFieldsFromDms(ruleDataSourceTask, clusterNameAndTypeMap));
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to get metadata from local cache ", e);
        }
        return new RuleDataSource();
    }

    private RuleDataSource getMetadataFieldsFromDms(MetadataOnRuleDataSourceTask ruleDataSourceTask, Map<String, String> clusterNameAndTypeMap) {
        LOGGER.info("Ready to request DMS for metadata, ruleDataSourceId: {}", ruleDataSourceTask.getRuleDataSourceId());
        String sourceType = TemplateDataSourceTypeEnum.getMessage(ruleDataSourceTask.getDatasourceType());
        String clusterType = clusterNameAndTypeMap.get(ruleDataSourceTask.getClusterName());
        String clusterName = ruleDataSourceTask.getClusterName();
        String dbName = ruleDataSourceTask.getDbName();
        String tableName = ruleDataSourceTask.getTableName();

        RuleDataSource resultValue = new RuleDataSource();
        try {
            TableTagInfo tableTagInfo = ruleClient.getTableTag(sourceType, clusterType, dbName, tableName, ruleDataSourceTask.getUsername());
            resultValue.setTagCode(tableTagInfo.getTagCode());
            resultValue.setTagName(tableTagInfo.getTagName());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get tag from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (ResourceAccessException e) {
            LOGGER.error("Failed to get tag from dms, loginUser:{}, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", ruleDataSourceTask.getUsername(), sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get tag from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get tag from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        }

        try {
            TableMetadataInfo tableMetadataInfo = ruleClient.getMetaData(sourceType, clusterType, dbName, tableName, ruleDataSourceTask.getUsername());
            List<TableMetadataInfo.SubSystem> subSystems = tableMetadataInfo.getSubSystemSet();
            if (CollectionUtils.isNotEmpty(subSystems)) {
                TableMetadataInfo.SubSystem subSystem = subSystems.get(0);
                resultValue.setSubSystemId(subSystem.getFid());
                resultValue.setSubSystemName(subSystem.getName());
            }
            resultValue.setDevDepartmentName(tableMetadataInfo.getDevDept());
            resultValue.setDepartmentName(tableMetadataInfo.getBusDept());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get metadata from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (ResourceAccessException e) {
            LOGGER.error("Failed to get metadata from dms, loginUser:{}, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", ruleDataSourceTask.getUsername(), sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get metadata from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get metadata from dms, sourceType:{}, clusterType:{}, cluster:{}, db:{}, table:{}, errorMsg: {}", sourceType, clusterType, clusterName, dbName, tableName, e.getMessage());
        }

        return resultValue;
    }

    private Map<String, String> getClusterNameAndTypeMap() {
        List<ClusterInfo> clusterInfoList = clusterInfoDao.findAllClusterInfo(0, 500);
        return clusterInfoList.stream().collect(Collectors.toMap(ClusterInfo::getClusterName, ClusterInfo::getClusterType, (oldVal, newVal) -> oldVal));
    }

    private boolean checkIfUpdate(RuleDataSource ruleDataSource) {
        return isNotEmpty(ruleDataSource.getSubSystemName())
                || isNotEmpty(ruleDataSource.getDepartmentName())
                || isNotEmpty(ruleDataSource.getDevDepartmentName())
                || isNotEmpty(ruleDataSource.getTagCode());
    }

    private final String nullStr = "null";
    private boolean isNotEmpty(String name) {
        return StringUtils.isNotEmpty(name) && !nullStr.equals(name);
    }

    private void clearNullString(RuleDataSource ruleDataSource) {
        if (nullStr.equals(ruleDataSource.getDepartmentName())) {
            ruleDataSource.setDepartmentName(null);
        }
        if (nullStr.equals(ruleDataSource.getDevDepartmentName())) {
            ruleDataSource.setDevDepartmentName(null);
        }
    }

}
