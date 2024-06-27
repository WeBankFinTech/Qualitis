/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.RuleClient;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.DataMapResultInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDatasourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceCountRepository;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceCount;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvMappingRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.timer.MetadataOnRuleDataSourceTask;
import com.webank.wedatasphere.qualitis.rule.timer.MetadataOnRuleDataSourceUpdater;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class RuleDataSourceServiceImpl implements RuleDataSourceService {

    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private RuleDataSourceDao ruleDatasourceDao;

    @Autowired
    private RuleTemplateDao ruleTemplateDao;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RuleDataSourceRepository ruleDataSourceRepository;

    @Autowired
    private RuleClient ruleClient;

    @Autowired
    private RuleDataSourceCountRepository ruleDataSourceCountRepository;

    @Autowired
    private RuleDatasourceEnvDao ruleDataSourceEnvDao;

    @Autowired
    private MetadataOnRuleDataSourceUpdater metadataOnRuleDataSourceUpdater;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleDataSourceServiceImpl.class);

    @Value("${rule.datasource.max-size:1000}")
    private Integer ruleDatasourceMaxSize;
    @Value("${rule.datasource.per-size:500}")
    private Integer ruleDatasourcePerSize;
    private AtomicBoolean isEndedStatusOnSyncMetadata = new AtomicBoolean(Boolean.TRUE);
    private final static Integer SYNC_METADATA_STATUS_REPEATED_SUBMIT = 2;
    private final static Integer SYNC_METADATA_STATUS_SUCCESS = 1;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<RuleDataSource> checkAndSaveRuleDataSource(List<DataSourceRequest> requests, Rule rule, RuleGroup ruleGroup, boolean cs, String loginUser)
            throws UnExpectedRequestException {
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        List<RuleDataSourceEnv> ruleDataSourceEnvs = new ArrayList<>();
        for (DataSourceRequest request : requests) {
            RuleDataSource newRuleDataSource = new RuleDataSource();
            String fileId = request.getFileId();
            // For fps file check.
            boolean fps = false;
            if (StringUtils.isNotBlank(fileId)) {
                newRuleDataSource.setFileId(fileId);
                newRuleDataSource.setFileTableDesc(request.getFileTablesDesc());
                newRuleDataSource.setFileDelimiter(SpecCharEnum.STAR.getValue().equals(request.getFileDelimiter()) ? " " : request.getFileDelimiter());
                newRuleDataSource.setFileType(request.getFileType());
                if (Objects.isNull(request.getFileHeader())) {
                    newRuleDataSource.setFileHeader(Boolean.FALSE);
                } else {
                    newRuleDataSource.setFileHeader(request.getFileHeader());
                }
                newRuleDataSource.setFileHashValue(request.getFileHashValues());
                newRuleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.FPS.getCode());
                fps = true;
            }
            // Check Arguments
            Boolean isTableRowsConsistency = (Objects.isNull(rule) || Objects.isNull(rule.getTemplate())) ? false: QualitisConstants.isTableRowsConsistency(rule.getTemplate().getEnName());
            Boolean isCustomColumnConsistence = (Objects.isNull(rule) || Objects.isNull(rule.getTemplate())) ? false: QualitisConstants.isCustomColumnConsistence(rule.getTemplate().getEnName());
            Boolean isTableStructureConsistent = (Objects.isNull(rule) || Objects.isNull(rule.getTemplate())) ? false: QualitisConstants.isTableStructureConsistent(rule.getTemplate().getEnName());
            if (!isTableRowsConsistency && !isCustomColumnConsistence) {
                DataSourceRequest.checkRequest(request, cs, fps, isTableStructureConsistent);
            }
            if (StringUtils.isNotBlank(request.getLinkisDataSourceType())) {
                newRuleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.getCode(request.getLinkisDataSourceType()));
                newRuleDataSource.setLinkisDataSourceVersionId(request.getLinkisDataSourceVersionId());
                newRuleDataSource.setLinkisDataSourceName(request.getLinkisDataSourceName());
                newRuleDataSource.setLinkisDataSourceId(request.getLinkisDataSourceId());
                saveRuleDataSourceEnvs(request.getDataSourceEnvRequests(), newRuleDataSource, ruleDataSourceEnvs);
            } else {
                newRuleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.getCode(request.getType()));
            }
            newRuleDataSource.setClusterName(request.getClusterName());
            newRuleDataSource.setTableName(request.getTableName());
            newRuleDataSource.setDbName(request.getDbName());
            String filter = StringUtils.remove(StringUtils.isNotEmpty(request.getFilter()) ? request.getFilter() : "true", "\n");
            newRuleDataSource.setFilter(filter);
            // Saved as: field1: type1, field2: type2
            newRuleDataSource.setBlackColName(request.getBlackList());
            newRuleDataSource.setProxyUser(request.getProxyUser());
            String joinCols = joinColNames(request.getColNames());
            newRuleDataSource.setColName(joinCols);
            if (rule != null) {
                newRuleDataSource.setRule(rule);

                if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
                    newRuleDataSource.setDatasourceIndex(request.getDatasourceIndex());
                }
                newRuleDataSource.setProjectId(rule.getProject().getId());
            }
            if (ruleGroup != null) {
                newRuleDataSource.setRuleGroup(ruleGroup);
                newRuleDataSource.setProjectId(ruleGroup.getProjectId());
            }
            newRuleDataSource.setCollectSql(request.getCollectSql());
            String dcnRangeType = StringUtils.isNotBlank(request.getDcnRangeType())?request.getDcnRangeType():QualitisConstants.DCN_RANGE_TYPE_ALL;
            newRuleDataSource.setDcnRangeType(dcnRangeType);

            ruleDataSources.add(newRuleDataSource);
            LOGGER.info("Succeed to save rule_datasource. rule_datasource: {}", newRuleDataSource);
        }

        List<RuleDataSource> ruleDataSourceList = ruleDatasourceDao.saveAllRuleDataSource(ruleDataSources);
        LOGGER.info("Succeed to save all rule datasource.");
        if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
            ruleDataSourceEnvDao.saveAllRuleDataSourceEnv(ruleDataSourceEnvs);
            LOGGER.info("Succeed to save all rule datasource env.");
        }

        // add some fields from dms
        metadataOnRuleDataSourceUpdater.submit(ruleDataSources, loginUser);
        return ruleDataSourceList;
    }

    private void saveRuleDataSourceEnvs(List<DataSourceEnvRequest> dataSourceEnvRequests, RuleDataSource newRuleDataSource, List<RuleDataSourceEnv> ruleDataSourceEnvs) {
        if (CollectionUtils.isNotEmpty(dataSourceEnvRequests)) {
            LOGGER.info("Start to add rule datasource envs. Requests: " + Arrays.toString(dataSourceEnvRequests.toArray()));
            for (DataSourceEnvRequest request : dataSourceEnvRequests) {
                RuleDataSourceEnv ruleDataSourceEnv = new RuleDataSourceEnv(request.getEnvId(), request.getEnvName(), newRuleDataSource);
                ruleDataSourceEnvs.add(ruleDataSourceEnv);
            }
        }
    }

    private void saveRuleDataSourceEnvMappings(List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests, RuleDataSource newRuleDataSource, List<RuleDataSourceEnv> ruleDataSourceEnvs) {
        if (CollectionUtils.isNotEmpty(dataSourceEnvMappingRequests)) {
            LOGGER.info("Start to add rule datasource env mappings. Requests: " + Arrays.toString(dataSourceEnvMappingRequests.toArray()));
            for (DataSourceEnvMappingRequest request : dataSourceEnvMappingRequests) {
                for (DataSourceEnvRequest dataSourceEnvRequest : request.getDataSourceEnvRequests()) {
                    RuleDataSourceEnv ruleDataSourceEnv = new RuleDataSourceEnv(dataSourceEnvRequest.getEnvId(), dataSourceEnvRequest.getEnvName(), newRuleDataSource);
                    ruleDataSourceEnv.setDbAndTable(request.getDbName() + "." + request.getDbAliasName());
                    ruleDataSourceEnvs.add(ruleDataSourceEnv);
                }
            }
        }
    }

    private String joinColNames(List<DataSourceColumnRequest> colNames) {
        List<String> joinColTypeList = new ArrayList<>();
        // Sort with column name.
        if (!CollectionUtils.isEmpty(colNames)) {
            Collections.sort(colNames, new Comparator<DataSourceColumnRequest>() {
                @Override
                public int compare(DataSourceColumnRequest front, DataSourceColumnRequest back) {
                    return front.getColumnName().compareTo(back.getColumnName());
                }
            });
            for (DataSourceColumnRequest col : colNames) {
                joinColTypeList.add(col.getColumnName() + ":" + col.getDataType());
            }
        }
        return String.join("|", joinColTypeList);
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleDatasourceDao.deleteByRule(rule);
    }

    @Override
    public void deleteByRuleGroup(RuleGroup ruleGroup) {
        ruleDataSourceRepository.deleteByRuleGroup(ruleGroup);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public List<RuleDataSource> checkAndSaveCustomRuleDataSource(String clusterName, String fileId, String fpsTableDesc, String fileDb
        , String fileTable, String fileDelimiter, String fileType, Boolean fileHeader, String proxyUser, String fileHashValues, String loginUser
        , Rule savedRule, boolean cs, boolean fps, boolean sqlCheck, Long linkisDataSourceId, Long linkisDataSourceVersionId, String linkisDataSourceName
        , String linkisDataSourceType, List<DataSourceEnvRequest> dataSourceEnvRequests, List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests, String dcnRangeType) {

        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        List<RuleDataSourceEnv> ruleDataSourceEnvs = new ArrayList<>();
        if (fps && StringUtils.isNotBlank(fileDb) && StringUtils.isNotBlank(fileTable)) {
            RuleDataSource ruleDataSource = new RuleDataSource();
            ruleDataSource.setClusterName(clusterName);
            ruleDataSource.setDbName(fileDb);
            ruleDataSource.setRule(savedRule);
            ruleDataSource.setProxyUser(proxyUser);
            String uuid = UuidGenerator.generate();
            ruleDataSource.setProjectId(savedRule.getProject().getId());
            ruleDataSource.setDatasourceIndex(QualitisConstants.ORIGINAL_INDEX);
            LOGGER.info("Start to prepare fps params in rule datasource service.");
            ruleDataSource.setFileId(fileId);
            ruleDataSource.setFileTableDesc(fpsTableDesc);
            ruleDataSource.setFileDelimiter(SpecCharEnum.STAR.getValue().equals(fileDelimiter) ? " " : fileDelimiter);
            ruleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.FPS.getCode());
            ruleDataSource.setFileType(fileType);
            ruleDataSource.setDcnRangeType(StringUtils.isNotBlank(dcnRangeType)?dcnRangeType:QualitisConstants.DCN_RANGE_TYPE_ALL);
            if (Objects.isNull(fileHeader)) {
                ruleDataSource.setFileHeader(Boolean.FALSE);
            } else {
                ruleDataSource.setFileHeader(fileHeader);
            }
            ruleDataSource.setFileHashValue(fileHashValues);
            Template templateInDb = savedRule.getTemplate();
            if (!(ExcelTypeEnum.XLSX.getValue().equals(ruleDataSource.getFileType()) || ExcelTypeEnum.XLS.getValue().equals(ruleDataSource.getFileType()))) {
                // Update template midaction when register temp table except excel file.
                templateInDb.setMidTableAction(templateInDb.getMidTableAction().replace(fileDb + ".", ""));
            }
            templateInDb.setMidTableAction(templateInDb.getMidTableAction().replace(fileTable, fileTable.concat("_").concat(uuid)));
            ruleTemplateDao.saveTemplate(templateInDb);
            fileTable = fileTable.concat("_").concat(uuid);
            ruleDataSource.setTableName(fileTable);
            ruleDataSources.add(ruleDataSource);

            LOGGER.info("Start to save fps file rule datasource.");
        } else {
            RuleDataSource ruleDataSource = new RuleDataSource();
            ruleDataSource.setClusterName(clusterName);
            ruleDataSource.setProxyUser(proxyUser);
            ruleDataSource.setRule(savedRule);
            if (StringUtils.isNotBlank(linkisDataSourceType)) {
                ruleDataSource.setLinkisDataSourceId(linkisDataSourceId);
                ruleDataSource.setLinkisDataSourceName(linkisDataSourceName);
                ruleDataSource.setLinkisDataSourceVersionId(linkisDataSourceVersionId);
                ruleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.getCode(linkisDataSourceType));
                saveRuleDataSourceEnvs(dataSourceEnvRequests, ruleDataSource, ruleDataSourceEnvs);
                saveRuleDataSourceEnvMappings(dataSourceEnvMappingRequests, ruleDataSource, ruleDataSourceEnvs);
            } else {
                ruleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.HIVE.getCode());
            }
            ruleDataSource.setProjectId(savedRule.getProject().getId());
            ruleDataSource.setDatasourceIndex(QualitisConstants.ORIGINAL_INDEX);
            ruleDataSource.setDcnRangeType(StringUtils.isNotBlank(dcnRangeType)?dcnRangeType:QualitisConstants.DCN_RANGE_TYPE_ALL);
            ruleDataSources.add(ruleDataSource);
            LOGGER.info("Start to save custom rule datasource with cluster name and proxy user.");
        }

        if (CollectionUtils.isEmpty(ruleDataSources)) {
            return ruleDataSources;
        }

        List<RuleDataSource> ruleDataSourceList = ruleDatasourceDao.saveAllRuleDataSource(ruleDataSources);
        if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
            ruleDataSourceEnvDao.saveAllRuleDataSourceEnv(ruleDataSourceEnvs);
            LOGGER.info("Succeed to save all rule datasource env.");
        }

        // add some fields from dms
        metadataOnRuleDataSourceUpdater.submit(ruleDataSources, loginUser);
        return ruleDataSourceList;
    }

    private List<DataSourceColumnRequest> saveCustomColumn(String clusterName, String db, String table, String loginUser, String csId, String nodeName, String funcContent,
                                                           boolean fps, boolean cs) throws Exception {
        List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>();
        List<ColumnInfoDetail> cols = new ArrayList<>();
        if (cs) {
            GetUserTableByCsIdRequest getUserTableByCsIdRequest = new GetUserTableByCsIdRequest();
            getUserTableByCsIdRequest.setClusterName(clusterName);
            getUserTableByCsIdRequest.setLoginUser(loginUser);
            getUserTableByCsIdRequest.setCsId(csId);
            getUserTableByCsIdRequest.setNodeName(nodeName);
            DataInfo<CsTableInfoDetail> csTableInfoDetails = metaDataClient.getTableByCsId(getUserTableByCsIdRequest);
            if (csTableInfoDetails.getTotalCount() == 0 && !fps) {
                LOGGER.info("Cannot find context service table with existed rules!");
                throw new UnExpectedRequestException("Table in sql {&DOES_NOT_EXIST}");
            }
            for (CsTableInfoDetail csTableInfoDetail : csTableInfoDetails.getContent()) {
                if (csTableInfoDetail.getTableName().equals(table)) {
                    GetUserColumnByCsRequest getUserColumnByCsRequest = new GetUserColumnByCsRequest();
                    getUserColumnByCsRequest.setClusterName(clusterName);
                    getUserColumnByCsRequest.setContextKey(csTableInfoDetail.getContextKey());
                    getUserColumnByCsRequest.setCsId(csId);
                    getUserColumnByCsRequest.setLoginUser(loginUser);
                    cols = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                    if (CollectionUtils.isEmpty(cols) && !fps) {
                        throw new UnExpectedRequestException("Table in sql {&DOES_NOT_EXIST}");
                    }
                }
            }
        } else {
            cols = metaDataClient.getColumnInfo(clusterName, db, table, loginUser);
            if (CollectionUtils.isEmpty(cols) && !fps) {
                throw new UnExpectedRequestException("Table in sql {&DOES_NOT_EXIST}");
            }

        }
        // Save column info in use.
        for (ColumnInfoDetail detail : cols) {
            if (funcContent != null && funcContent.contains(detail.getFieldName())) {
                dataSourceColumnRequests.add(new DataSourceColumnRequest(detail.getFieldName(), detail.getDataType()));
            }
        }
        return dataSourceColumnRequests;
    }

    /**
     * Check if cluster name supported
     *
     * @param submittedClusterName
     * @throws UnExpectedRequestException
     */
    @Override
    public void checkDataSourceClusterSupport(String submittedClusterName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(submittedClusterName)) {
            return;
        }
        Set<String> submittedClusterNames = new HashSet<>();
        submittedClusterNames.add(submittedClusterName);
        checkDataSourceClusterSupport(submittedClusterNames);
    }

    /**
     * Check if cluster name supported
     * return if there is no cluster config
     *
     * @param submittedClusterNames
     * @throws UnExpectedRequestException
     */
    @Override
    public void checkDataSourceClusterSupport(Set<String> submittedClusterNames) throws UnExpectedRequestException {
        if (submittedClusterNames == null || submittedClusterNames.isEmpty()) {
            return;
        }
        List<ClusterInfo> clusters = clusterInfoDao.findAllClusterInfo(0, Integer.MAX_VALUE);
        if (clusters == null || clusters.isEmpty()) {
            LOGGER.info("Failed to find cluster info config. End to check the limitation of cluster info.");
            return;
        }
        Set<String> supportClusterNames = new HashSet<>();
        for (ClusterInfo info : clusters) {
            supportClusterNames.add(info.getClusterName());
        }
        Set<String> unSupportClusterNameSet = new HashSet<>();
        for (String clusterName : submittedClusterNames) {
            if (!supportClusterNames.contains(clusterName)) {
                unSupportClusterNameSet.add(clusterName);
            }
        }
        if (unSupportClusterNameSet.size() > 0) {
            throw new UnExpectedRequestException(String.format("{&NOT_SUPPORT_CLUSTER_NAME}:%s,{&ONLY_SUPPORT_CLUSTER_NAME_ARE}:%s", unSupportClusterNameSet, submittedClusterNames.toString()));
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleDataSource checkAndSaveFileRuleDataSource(DataSourceRequest request, Rule rule, boolean cs, String loginUser) throws UnExpectedRequestException {
        DataSourceRequest.checkRequest(request, cs, "File rule datasource request");
        RuleDataSource newRuleDataSource = new RuleDataSource();
        newRuleDataSource.setClusterName(request.getClusterName());
        newRuleDataSource.setDbName(request.getDbName());
        newRuleDataSource.setTableName(request.getTableName());
        if (request.getFilter() != null) {
            newRuleDataSource.setFilter(request.getFilter());
        } else {
            newRuleDataSource.setFilter("");
        }
        newRuleDataSource.setColName("");
        newRuleDataSource.setProxyUser(request.getProxyUser());
        newRuleDataSource.setRule(rule);
        newRuleDataSource.setProjectId(rule.getProject().getId());
        String dcnRangeType = StringUtils.isNotBlank(request.getDcnRangeType())?request.getDcnRangeType():QualitisConstants.DCN_RANGE_TYPE_ALL;
        newRuleDataSource.setDcnRangeType(dcnRangeType);

        RuleDataSource ruleDataSourceInDb = ruleDatasourceDao.saveRuleDataSource(newRuleDataSource);

        metadataOnRuleDataSourceUpdater.submit(Arrays.asList(newRuleDataSource), loginUser);

        return ruleDataSourceInDb;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void updateRuleDataSourceCount(Rule ruleInDb, Integer varyAmount) {
        Set<RuleDataSource> ruleDataSources = ruleInDb.getRuleDataSources();
        if (ruleDataSources == null || ruleDataSources.isEmpty()) {
            LOGGER.error("Rule does not have ruledatasource.");
            return;
        }
        Long userId = null;
        if (StringUtils.isNotBlank(ruleInDb.getCreateUser())) {
            userId = userDao.findByUsername(ruleInDb.getCreateUser()).getId();
        }
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            StringBuilder datasourceName = new StringBuilder();
            if (StringUtils.isEmpty(ruleDataSource.getTableName()) || StringUtils.isEmpty(ruleDataSource.getColName())) {
                continue;
            }

            if (StringUtils.isNotBlank(ruleDataSource.getColName())) {
                String[] cols = ruleDataSource.getColName().split(SpecCharEnum.VERTICAL_BAR.getValue());
                for (String col : cols) {
                    datasourceName.append(ruleDataSource.getClusterName()).append("-").append(ruleDataSource.getDbName()).append("-")
                            .append(ruleDataSource.getTableName()).append("-").append(col);
                    if (userId != null) {
                        try {
                            updateInLock(datasourceName, userId, varyAmount, ruleInDb);
                        } catch (Exception e) {
                            LOGGER.error("Rule datasource count not be update. " + e.getMessage(), e);
                        }

                        datasourceName.delete(0, datasourceName.length());
                    }
                }
            }
        }
    }

    @Override
    public GeneralResponse<DataMapResultInfo<String>> syncMetadata(String userName) {
        LOGGER.info("Ready to sync metadata, loginUser: {}", userName);
        Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(1);
        if (isRepeatSubmitOnSyncMetadata()) {
            dataMap.put("type", SYNC_METADATA_STATUS_REPEATED_SUBMIT);
            return new GeneralResponse(ResponseStatusConstants.OK, "failed",
                    new DataMapResultInfo(ResponseStatusConstants.OK, "已在同步，请勿重复提交", dataMap));
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Map<String, String> clusterNameAndTypeMap = getClusterNameAndTypeMap();
        int maxSize = ruleDatasourceMaxSize >= 0 ? ruleDatasourceMaxSize: Long.valueOf(ruleDataSourceRepository.count()).intValue();
        int perSize = ruleDatasourcePerSize < maxSize ? ruleDatasourcePerSize: maxSize;
        int totalPage = maxSize / perSize + 1;
        try {
            executor.execute(() -> {
                isEndedStatusOnSyncMetadata.set(Boolean.FALSE);
                try {
                    for (int page = 0; page < totalPage; page++) {
                        List<RuleDataSource> ruleDataSourceList = ruleDatasourceDao.findAllWithPage(page, perSize).getContent();
                        if (CollectionUtils.isEmpty(ruleDataSourceList)) {
                            LOGGER.info("List of RuleDataSource is empty");
                            break;
                        }
                        LOGGER.info("Query data from qualitis_rule_datasource, page: {}, count: {}", page, ruleDataSourceList.size());
                        List<MetadataOnRuleDataSourceTask> metadataOnRuleDataSourceTaskList = ruleDataSourceList.stream().map(ruleDataSource -> new MetadataOnRuleDataSourceTask(ruleDataSource, userName)).collect(Collectors.toList());
                        metadataOnRuleDataSourceUpdater.executeBatchUpdate(metadataOnRuleDataSourceTaskList, clusterNameAndTypeMap);
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            LOGGER.warn("Thread was interrupted", e);
                        }
                    }
                } finally {
                    isEndedStatusOnSyncMetadata.set(Boolean.TRUE);
                    LOGGER.info("Finished to sync metadata to qualitis_rule_datasource ");
                }
            });
        } finally {
            executor.shutdown();
        }

        dataMap.put("type", SYNC_METADATA_STATUS_SUCCESS);
        return new GeneralResponse(ResponseStatusConstants.OK, "success", new DataMapResultInfo(ResponseStatusConstants.OK, "正在同步", dataMap));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void updateInLock(StringBuilder datasourceName, Long userId, Integer varyAmount, Rule ruleInDb) {
        RuleDataSourceCount ruleDataSourceCount = ruleDataSourceCountRepository.findByDatasourceNameAndUserId(datasourceName.toString(), userId);
        if (ruleDataSourceCount == null) {
            if (varyAmount > 0) {
                ruleDataSourceCount = new RuleDataSourceCount(datasourceName.toString(), userId);
                ruleDataSourceCountRepository.save(ruleDataSourceCount);
            } else {
                LOGGER.warn("Rule datasource does not have related rules.");
            }
        } else {
            ruleDataSourceCount.setDatasourceCount(ruleDataSourceCount.getDatasourceCount() + varyAmount >= 0 ? ruleDataSourceCount.getDatasourceCount() + varyAmount : 0);
            ruleDataSourceCountRepository.save(ruleDataSourceCount);
            LOGGER.info("Rule [{}] count of datasource is: {}", ruleInDb.getName(), ruleDataSourceCount.getDatasourceCount());
        }
    }

    private Map<String, String> getClusterNameAndTypeMap() {
        List<ClusterInfo> clusterInfoList = clusterInfoDao.findAllClusterInfo(0, 500);
        return clusterInfoList.stream().collect(Collectors.toMap(ClusterInfo::getClusterName, ClusterInfo::getClusterType, (oldVal, newVal) -> oldVal));
    }

    private boolean isRepeatSubmitOnSyncMetadata(){
        return !isEndedStatusOnSyncMetadata.get();
    }

}
