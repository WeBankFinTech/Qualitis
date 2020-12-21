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

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.parser.HiveSqlParser;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;

import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author howeye
 */
@Service
public class RuleDataSourceServiceImpl implements RuleDataSourceService {

    @Autowired
    private RuleDataSourceDao ruleDatasourceDao;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private RuleDataSourceRepository ruleDataSourceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleDataSourceServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<RuleDataSource> checkAndSaveRuleDataSource(List<DataSourceRequest> requests, Rule rule) throws UnExpectedRequestException {
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        for (DataSourceRequest request : requests) {
            // Check Arguments
            DataSourceRequest.checkRequest(request);

            RuleDataSource newRuleDataSource = new RuleDataSource();
            newRuleDataSource.setClusterName(request.getClusterName());
            newRuleDataSource.setDbName(request.getDbName());
            newRuleDataSource.setTableName(request.getTableName());
            newRuleDataSource.setFilter(request.getFilter());
            // Saved as: field1: type1, field2: type2
            newRuleDataSource.setColName(joinColNames(request.getColNames()));
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
        for (DataSourceColumnRequest col : colNames){
            joinColTypeList.add(col.getColumnName() + ":" + col.getDataType());
        }
        return String.join(",", joinColTypeList);
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleDataSourceRepository.deleteByRule(rule);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SemanticException.class, ParseException.class})
    public List<RuleDataSource> checkAndSaveCustomRuleDataSource(String clusterName, Rule savedRule) throws SemanticException, ParseException {
        String midTableAction = savedRule.getTemplate().getMidTableAction();
        midTableAction = midTableAction.replace("${filter}", savedRule.getWhereContent());
        midTableAction = DateExprReplaceUtil.replaceDateExpr(midTableAction);
        HiveSqlParser hiveSqlParser = new HiveSqlParser();
        Map<String, List<String>> dbAndTables = hiveSqlParser.checkSelectSqlAndGetDbAndTable(midTableAction);

        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        for (String db : dbAndTables.keySet()) {
            for (String table : dbAndTables.get(db)) {
                RuleDataSource ruleDataSource = new RuleDataSource();
                ruleDataSource.setClusterName(clusterName);
                ruleDataSource.setProjectId(savedRule.getProject().getId());
                ruleDataSource.setRule(savedRule);
                ruleDataSource.setTableName(table);
                ruleDataSource.setDbName(db);
                ruleDataSources.add(ruleDataSource);
                ruleDataSource.setColName(savedRule.getFunctionContent());
                LOGGER.info("Succeed to save rule_datasource. rule_datasource: {}", ruleDataSources);
            }
        }
        LOGGER.info("Succeed to save all rule datasource: {}");
        return ruleDatasourceDao.saveAllRuleDataSource(ruleDataSources);
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
            if (!supportClusterNames.contains(clusterName)) {
                unSupportClusterNameSet.add(clusterName);
            }
        }
        if (unSupportClusterNameSet.size() > 0) {
            throw new UnExpectedRequestException(String.format("{&NOT_SUPPORT_CLUSTER_NAME}:%s,{&ONLY_SUPPORT_CLUSTER_NAME_ARE}:%s", unSupportClusterNameSet, submittedClusterNames.toString()));
        }
    }
}
