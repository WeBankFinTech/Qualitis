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

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceCountRepository;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceCount;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;

import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private RuleDataSourceCountRepository ruleDataSourceCountRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleDataSourceServiceImpl.class);
    private static final Integer ORIGINAL_INDEX = -1;


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<RuleDataSource> checkAndSaveRuleDataSource(List<DataSourceRequest> requests, Rule rule, boolean cs, String loginUser)
        throws UnExpectedRequestException {
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        for (DataSourceRequest request : requests) {
            RuleDataSource newRuleDataSource = new RuleDataSource();
            // Check Arguments
            DataSourceRequest.checkRequest(request, cs, false);
            if (StringUtils.isNotBlank(request.getLinkisDataSourceType())) {
                newRuleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.getCode(request.getLinkisDataSourceType()));
                newRuleDataSource.setLinkisDataSourceVersionId(request.getLinkisDataSourceVersionId());
                newRuleDataSource.setLinkisDataSourceName(request.getLinkisDataSourceName());
                newRuleDataSource.setLinkisDataSourceId(request.getLinkisDataSourceId());
            }
            newRuleDataSource.setClusterName(request.getClusterName());
            newRuleDataSource.setTableName(request.getTableName());
            newRuleDataSource.setDbName(request.getDbName());
            newRuleDataSource.setFilter(request.getFilter());
            // Saved as: field1: type1, field2: type2
            newRuleDataSource.setBlackColName(request.getBlackList());
            newRuleDataSource.setProxyUser(request.getProxyUser());
            String joinCols = joinColNames(request.getColNames());
            newRuleDataSource.setColName(joinCols);
            newRuleDataSource.setRule(rule);
            if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
               newRuleDataSource.setDatasourceIndex(request.getDatasourceIndex());
            }
            newRuleDataSource.setProjectId(rule.getProject().getId());
            ruleDataSources.add(newRuleDataSource);
            LOGGER.info("Succeed to save rule_datasource. rule_datasource: {}", newRuleDataSource);
        }
        LOGGER.info("Succeed to save all rule_datasource.");
        return ruleDatasourceDao.saveAllRuleDataSource(ruleDataSources);
    }

    private String joinColNames(List<DataSourceColumnRequest> colNames) {
        List<String> joinColTypeList = new ArrayList<>();
        // Sort with column name.
        if (! CollectionUtils.isEmpty(colNames)) {
            Collections.sort(colNames, new Comparator<DataSourceColumnRequest>() {
                @Override
                public int compare(DataSourceColumnRequest front, DataSourceColumnRequest back) {
                    return front.getColumnName().compareTo(back.getColumnName());
                }
            });
            for (DataSourceColumnRequest col : colNames){
                joinColTypeList.add(col.getColumnName() + ":" + col.getDataType());
            }
        }
        return String.join("|", joinColTypeList);
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleDataSourceRepository.deleteByRule(rule);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public List<RuleDataSource> checkAndSaveCustomRuleDataSource(String clusterName, String proxyUser, String loginUser, Rule savedRule, boolean cs
        , boolean sqlCheck, Long linkisDataSourceId, Long linkisDataSourceVersionId, String linkisDataSourceName, String linkisDataSourceType) {

        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        RuleDataSource ruleDataSource = new RuleDataSource();
        ruleDataSource.setDatasourceIndex(ORIGINAL_INDEX);
        ruleDataSource.setClusterName(clusterName);
        ruleDataSource.setProxyUser(proxyUser);
        ruleDataSource.setRule(savedRule);
        if (StringUtils.isNotBlank(linkisDataSourceType)) {
            ruleDataSource.setLinkisDataSourceId(linkisDataSourceId);
            ruleDataSource.setLinkisDataSourceName(linkisDataSourceName);
            ruleDataSource.setLinkisDataSourceVersionId(linkisDataSourceVersionId);
            ruleDataSource.setDatasourceType(TemplateDataSourceTypeEnum.getCode(linkisDataSourceType));
        }

        ruleDataSources.add(ruleDataSource);
        LOGGER.info("Start to save custom rule datasource with cluster name and proxy user.");
        if (CollectionUtils.isEmpty(ruleDataSources)) {
            return ruleDataSources;
        }
        return ruleDatasourceDao.saveAllRuleDataSource(ruleDataSources);
    }

    private List<DataSourceColumnRequest> saveCustomColumn(String clusterName, String db, String table, String loginUser, String csId, String nodeName, String funcContent,
            boolean fps, boolean cs) throws UnExpectedRequestException, MetaDataAcquireFailedException {
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
     * @param submittedClusterName
     * @throws UnExpectedRequestException
     */
    @Override
    public void checkDataSourceClusterSupport(String submittedClusterName)  throws UnExpectedRequestException {
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
     * @param submittedClusterNames
     * @throws UnExpectedRequestException
     */
    @Override
    public void checkDataSourceClusterSupport(Set<String> submittedClusterNames)  throws UnExpectedRequestException {
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
            if (! supportClusterNames.contains(clusterName)) {
                unSupportClusterNameSet.add(clusterName);
            }
        }
        if (unSupportClusterNameSet.size() > 0) {
            throw new UnExpectedRequestException(String.format("{&NOT_SUPPORT_CLUSTER_NAME}:%s,{&ONLY_SUPPORT_CLUSTER_NAME_ARE}:%s", unSupportClusterNameSet, submittedClusterNames.toString()));
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleDataSource checkAndSaveFileRuleDataSource(DataSourceRequest request, Rule rule, boolean cs) throws UnExpectedRequestException {
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
        return ruleDatasourceDao.saveRuleDataSource(newRuleDataSource);
    }

    @Override
    public void updateRuleDataSourceCount(Rule ruleInDb, Integer varyAmount) {
        Set<RuleDataSource> ruleDataSources = ruleInDb.getRuleDataSources();
        if (ruleDataSources == null || ruleDataSources.isEmpty()) {
            LOGGER.error("Rule does not have ruledatasource.");
            return;
        }
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            StringBuffer datasourceName = new StringBuffer();
            if (StringUtils.isEmpty(ruleDataSource.getTableName()) || StringUtils.isEmpty(ruleDataSource.getColName())) {
                continue;
            }
            if (StringUtils.isNotBlank(ruleDataSource.getColName())) {
                String[] cols = ruleDataSource.getColName().split(SpecCharEnum.VERTICAL_BAR.getValue());
                for (String col : cols) {
                    datasourceName.append(ruleDataSource.getClusterName()).append("-").append(ruleDataSource.getDbName()).append("-")
                        .append(ruleDataSource.getTableName()).append("-").append(col);
                    Long userId = userDao.findByUsername(ruleInDb.getCreateUser()).getId();
                    RuleDataSourceCount ruleDataSourceCount = ruleDataSourceCountRepository.findByDatasourceNameAndUserId(datasourceName.toString(), userId);
                    if (ruleDataSourceCount == null) {
                        if (varyAmount > 0) {
                            ruleDataSourceCount = new RuleDataSourceCount(datasourceName.toString(), userId);
                            ruleDataSourceCountRepository.save(ruleDataSourceCount);
                        } else {
                            LOGGER.error("Rule datasource ddes not have related rules.");
                        }
                    } else {
                        ruleDataSourceCount.setDatasourceCount(ruleDataSourceCount.getDatasourceCount() + varyAmount);
                        ruleDataSourceCountRepository.save(ruleDataSourceCount);
                        LOGGER.info("Rule [{}] count of datasource is: {}", ruleInDb.getName(), ruleDataSourceCount.getDatasourceCount());
                    }
                    datasourceName.delete(0, datasourceName.length());
                }
            }
        }
    }
}
