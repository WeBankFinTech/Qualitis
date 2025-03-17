package com.webank.wedatasphere.qualitis.rule.timer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.webank.wedatasphere.qualitis.client.config.DataMapConfig;
import com.webank.wedatasphere.qualitis.constants.ThreadPoolConstant;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.ImsMetric;
import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import com.webank.wedatasphere.qualitis.entity.MetricExtInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.table.SearchMetadataInfo;
import com.webank.wedatasphere.qualitis.pool.exception.ThreadPoolNotFoundException;
import com.webank.wedatasphere.qualitis.pool.manager.AbstractThreadPoolManager;
import com.webank.wedatasphere.qualitis.rule.constant.MetricClassEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.MetricExtInfoDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-02-27 15:29
 * @description 获取来自DMS的元数据子字段(子系统 、 开发部门 、 业务部门 、 表标签)，更新到RuleDataSource
 */
@Component
public class MetadataSyncUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataSyncUpdater.class);

    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private DataStandardClient dataStandardClient;
    @Autowired
    private MetricExtInfoDao metricExtInfoDao;
    @Autowired
    private AbstractThreadPoolManager threadPoolManager;
    private ThreadPoolExecutor dmsWorkerThreadPool;
    @Autowired
    private DataMapConfig dataMapConfig;

    @PostConstruct
    public void init() throws ThreadPoolNotFoundException {
        dmsWorkerThreadPool = threadPoolManager.getThreadPool(ThreadPoolConstant.DMS_METADATA_RULE_DATASOURCE_UPDATER);
    }

    private Cache<String, SearchMetadataInfo> metadataInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();

    private Cache<String, String> dbIdCache = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();

    private Cache<String, String> tableIdCache = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();

    public void submitRuleDataSourceTask(List<RuleDataSource> ruleDataSources, String loginUser) {
        Map<String, String> clusterNameAndTypeMap = getClusterNameAndTypeMap();
        List<MetadataSyncTask> syncTasks = ruleDataSources.stream().filter(ruleDataSource -> {
            if (Objects.isNull(ruleDataSource.getDatasourceType())
                    || StringUtils.isEmpty(ruleDataSource.getClusterName())
                    || StringUtils.isEmpty(ruleDataSource.getDbName())
                    || StringUtils.isEmpty(ruleDataSource.getTableName())) {
                return false;
            }
            return true;
        }).map(ruleDataSource -> {
            MetadataSyncTask metadataSyncTask = new MetadataSyncTask(ruleDataSource, loginUser);
            metadataSyncTask.setClusterType(clusterNameAndTypeMap.get(ruleDataSource.getClusterName()));
            return metadataSyncTask;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(syncTasks)) {
            return;
        }

        try {
            LOGGER.info("Ready to update metadata, total counts: {}", syncTasks.size());
            for (MetadataSyncTask syncTask : syncTasks) {
                dispatchRuleDataSourceTask(syncTask);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute update metadata ", e);
        }
    }

    private void dispatchRuleDataSourceTask(MetadataSyncTask ruleDataSourceTask) {
        CompletableFuture<Optional<SearchMetadataInfo>> completableFuture = CompletableFuture.supplyAsync(() ->
                this.getMetadataTableFromLocalCache(ruleDataSourceTask), dmsWorkerThreadPool);
        completableFuture.whenComplete((searchMetadataInfoOptional, throwable) -> {
            if (searchMetadataInfoOptional.isPresent()) {
                SearchMetadataInfo searchMetadataInfo = searchMetadataInfoOptional.get();
                LOGGER.info("Ready to update metadata, ruleDataSourceId: {}", ruleDataSourceTask.getDataId());
                clearNullString(searchMetadataInfo);
                ruleDataSourceDao.updateMetadataFields(ruleDataSourceTask.getDataId()
                        , null, searchMetadataInfo.getSubsystemName()
                        , null, searchMetadataInfo.getDepartment()
                        , null, searchMetadataInfo.getTagCodes(), searchMetadataInfo.getTagNames());
            }
        });
    }

    private Optional<SearchMetadataInfo> getMetadataTableFromDms(MetadataSyncTask syncTask) {
        LOGGER.info("Ready to request DMS for metadata, ruleDataSourceId: {}", syncTask.getDataId());
        String sourceType = TemplateDataSourceTypeEnum.getMessage(syncTask.getDatasourceType());
        String clusterType = syncTask.getClusterType();
        String dbName = syncTask.getDbName();
        String tableName = syncTask.getTableName();

        try {
            return metaDataClient.getTableMetaData(sourceType, clusterType, dbName, tableName, dataMapConfig.getSpecialProxyUser());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        } catch (ResourceAccessException e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        }

        return Optional.empty();
    }

    public void submitImsMetric(List<ImsMetric> imsMetrics, Map<Long, ImsMetricCollect> collectMap, Map<String, String> clusterNameAndTypeMap, String loginUser) {
        List<MetadataSyncTask> syncTasks = imsMetrics.stream()
                .filter(imsMetric -> collectMap.containsKey(imsMetric.getMetricCollectId()))
                .map(imsMetric -> {
                    ImsMetricCollect imsMetricCollect = collectMap.get(imsMetric.getMetricCollectId());
                    MetadataSyncTask metadataSyncTask = new MetadataSyncTask(imsMetricCollect, imsMetricCollect.getCreateUser());
                    metadataSyncTask.setDataId(imsMetric.getId());
                    metadataSyncTask.setClusterType(clusterNameAndTypeMap.get(imsMetricCollect.getClusterName()));
                    return metadataSyncTask;
                }).collect(Collectors.toList());

        try {
            LOGGER.info("Ready to update metadata, total counts: {}", syncTasks.size());
            for (MetadataSyncTask syncTask : syncTasks) {
                dispatchImsMetricTask(syncTask);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute update metadata ", e);
        }
    }

    private void dispatchImsMetricTask(MetadataSyncTask imsMetricTask) {
        CompletableFuture<Optional<SearchMetadataInfo>> completableFuture = CompletableFuture.supplyAsync(() ->
                this.getMetadataFieldFromLocalCache(imsMetricTask), dmsWorkerThreadPool);
        completableFuture.whenComplete((searchMetadataInfoOptional, throwable) -> {
            if (searchMetadataInfoOptional.isPresent()) {
                SearchMetadataInfo searchMetadataInfo = searchMetadataInfoOptional.get();
                LOGGER.info("Ready to update metadata, metric_id: {}", imsMetricTask.getDataId());
                if (StringUtils.isNotBlank(searchMetadataInfo.getFieldComment())) {
                    MetricExtInfo metricExtInfoInDb = metricExtInfoDao.get(imsMetricTask.getDataId(), MetricClassEnum.IMS_METRIC);
                    if (metricExtInfoInDb == null) {
                        metricExtInfoInDb = new MetricExtInfo();
                        metricExtInfoInDb.setMetricId(imsMetricTask.getDataId());
                        metricExtInfoInDb.setMetricClass(MetricClassEnum.IMS_METRIC.getCode());
                    }
                    metricExtInfoInDb.setImsmetricDesc(searchMetadataInfo.getFieldComment());
                    metricExtInfoDao.save(metricExtInfoInDb);
                }
            }
        });
    }

    private Optional<SearchMetadataInfo> getMetadataTableFromLocalCache(MetadataSyncTask ruleDataSourceTask) {
        try {
            SearchMetadataInfo searchMetadataInfo = metadataInfoCache.get(ruleDataSourceTask.getCacheKey(), () -> {
                Optional<SearchMetadataInfo> searchMetadataInfoOptional = getMetadataTableFromDms(ruleDataSourceTask);
                return searchMetadataInfoOptional.orElseGet(null);
            });
            return Optional.ofNullable(searchMetadataInfo);
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to get metadata from local cache ", e);
        }
        return Optional.empty();
    }

    private Optional<SearchMetadataInfo> getMetadataFieldFromLocalCache(MetadataSyncTask ruleDataSourceTask) {
        try {
            SearchMetadataInfo searchMetadataInfo = metadataInfoCache.get(ruleDataSourceTask.getCacheKey(), () -> {
                Optional<SearchMetadataInfo> searchMetadataInfoOptional = getMetadataFieldFromDms(ruleDataSourceTask);
                return searchMetadataInfoOptional.orElseGet(null);
            });
            return Optional.ofNullable(searchMetadataInfo);
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to get metadata from local cache ", e);
        }
        return Optional.empty();
    }

    private Optional<SearchMetadataInfo> getMetadataFieldFromDms(MetadataSyncTask syncTask) {
        LOGGER.info("Ready to request DMS for metadata, metric_id: {}", syncTask.getDataId());
        String sourceType = TemplateDataSourceTypeEnum.getMessage(syncTask.getDatasourceType());
        String clusterType = syncTask.getClusterType();
        String dbName = syncTask.getDbName();
        String tableName = syncTask.getTableName();
        String colName = syncTask.getColName();

        try {
            String dbId = getDbId(clusterType, dbName, syncTask.getUsername());
            String tableId = getTableId(clusterType, dbId, tableName, syncTask.getUsername());
            List<Map<String, Object>> responseList = metaDataClient.getDmsFieldData(sourceType, clusterType, dbId, tableId, colName, dataMapConfig.getSpecialProxyUser());
            Optional<Map<String, Object>> fieldMap = responseList.stream().filter(responseMap -> dbName.equals(MapUtils.getString(responseMap, "dbCode"))
                    && tableName.equals(MapUtils.getString(responseMap, "datasetName"))
                    && colName.equals(MapUtils.getString(responseMap, "fieldName"))).findFirst();
            if (fieldMap.isPresent()) {
                String fieldComment = MapUtils.getString(fieldMap.get(), "comment");
                if (StringUtils.isNotBlank(fieldComment)) {
                    SearchMetadataInfo searchMetadataInfo = new SearchMetadataInfo();
                    searchMetadataInfo.setFieldComment(fieldComment);
                    searchMetadataInfo.setClusterName(syncTask.getClusterName());
                    searchMetadataInfo.setDbName(dbName);
                    searchMetadataInfo.setTbName(tableName);
                    searchMetadataInfo.setFieldName(colName);
                    return Optional.of(searchMetadataInfo);
                } else {
                    return Optional.empty();
                }
            }
            return Optional.empty();
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        } catch (ResourceAccessException e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get metadata from dms, syncTask:{}, errorMsg: {}", syncTask, e.getMessage());
        }

        return Optional.empty();
    }

    private String getDbId(String clusterType, String dbName, String username) throws ExecutionException {
        return dbIdCache.get(clusterType + dbName, () -> {
            Map<String, Object> dbResp = dataStandardClient.getDatabase(dbName, username);
            String dbId = (String) ((List<Map<String, Object>>) dbResp.get("content")).stream()
                    .filter(map -> clusterType.contains((String) map.get("value")) && dbName.equals(map.get("name")))
                    .iterator().next().get("id");
            return dbId;
        });
    }

    private String getTableId(String clusterType, String dbId, String tableName, String username) throws ExecutionException {
        return tableIdCache.get(clusterType + tableName, () -> {
            Map<String, Object> tableResp = dataStandardClient.getDataset(dbId, tableName, 1, 10, username);
            Integer tableId = ((Double) ((List<Map<String, Object>>) tableResp.get("content")).stream()
                    .filter(map -> clusterType.contains((String) map.get("clusterType"))
                            && BigDecimal.valueOf(Double.parseDouble(dbId)).compareTo(BigDecimal.valueOf((Double) map.get("dbId"))) == 0
                            && tableName.equals(map.get("datasetName")))
                    .iterator().next().get("datasetId")).intValue();
            return tableId.toString();
        });
    }

    private Map<String, String> getClusterNameAndTypeMap() {
        List<ClusterInfo> clusterInfoList = clusterInfoDao.findAllClusterInfo(0, 500);
        return clusterInfoList.stream().collect(Collectors.toMap(ClusterInfo::getClusterName, ClusterInfo::getClusterType, (oldVal, newVal) -> oldVal));
    }

    private final String nullStr = "null";

    private void clearNullString(SearchMetadataInfo searchMetadataInfo) {
        if (nullStr.equals(searchMetadataInfo.getDepartment())) {
            searchMetadataInfo.setDepartment(null);
        }
        if (nullStr.equals(searchMetadataInfo.getSubsystemName())) {
            searchMetadataInfo.setSubsystemName(null);
        }
        if (nullStr.equals(searchMetadataInfo.getTbName())) {
            searchMetadataInfo.setTbName(null);
        }
        if (nullStr.equals(searchMetadataInfo.getTagCodes())) {
            searchMetadataInfo.setTagCodes(null);
        }
        if (nullStr.equals(searchMetadataInfo.getTagNames())) {
            searchMetadataInfo.setTagNames(null);
        }
        if (nullStr.equals(searchMetadataInfo.getFieldName())) {
            searchMetadataInfo.setFieldName(null);
        }
        if (nullStr.equals(searchMetadataInfo.getFieldComment())) {
            searchMetadataInfo.setFieldComment(null);
        }
    }

}
