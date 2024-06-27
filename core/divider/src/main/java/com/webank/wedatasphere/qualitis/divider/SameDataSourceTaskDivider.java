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

package com.webank.wedatasphere.qualitis.divider;

import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Divided rule into same task if they have the same datasource
 * @author howeye
 */
public class SameDataSourceTaskDivider extends AbstractTaskDivider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SameDataSourceTaskDivider.class);

    private static final String SPLIT_BY_TABLE = "table";
    private static final String SPLIT_BY_NONE = "merge";


    @Override
    public List<DataQualityTask> divide(String clusterName, Integer datasourceIndex, List<Rule> rules, String applicationId,
        String createTime, String partition,
        Date date, Map<Long, Map<String, Object>> ruleReplaceInfo
        , Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user, Integer threshold, String splitBy, String startupParam) throws ArgumentException {
        LOGGER.info("Start to classify rules by datasource");
        Map<String, List<Rule>> sameDataSourceRule = new HashMap<>(4);
        Map<String, String> keyUsers = new HashMap<>(2);
        for (Rule rule : rules) {
            StringBuilder realUser = new StringBuilder();
            if (StringUtils.isEmpty(splitBy) && StringUtils.isNotEmpty(rule.getExecutionParametersName())) {
                String concurrentcyGranularity = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId()).getConcurrencyGranularity();
                if (StringUtils.isNotEmpty(concurrentcyGranularity) && concurrentcyGranularity.contains(":")) {
                    splitBy = concurrentcyGranularity.split(":")[1];
                }
            }
            String key = getKey(rule, user, realUser, partition, splitBy);

            if (ruleReplaceInfo.get(rule.getId()).get("qualitis_startup_param") != null && StringUtils.isNotEmpty((String) ruleReplaceInfo.get(rule.getId()).get("qualitis_startup_param"))) {
                key = key + ":" + ruleReplaceInfo.get(rule.getId()).get("qualitis_startup_param");
            }

            if (sameDataSourceRule.containsKey(key)) {
                sameDataSourceRule.get(key).add(rule);
            } else {
                List<Rule> tmp = new ArrayList<>();
                tmp.add(rule);
                sameDataSourceRule.put(key, tmp);
                keyUsers.put(key, realUser.toString());
            }
        }
        LOGGER.info("Succeed to classify rules by datasource maybe contains static params. Result: {}", sameDataSourceRule.keySet().stream().collect(Collectors.joining(",")));

        List<DataQualityTask> result = new ArrayList<>();
        handleSameDataSourceRule(datasourceIndex, applicationId, createTime, keyUsers, partition, ruleReplaceInfo, dataSourceMysqlConnect, threshold, sameDataSourceRule, result, startupParam);
        LOGGER.info("Succeed to divide all rules into tasks. Result: {}", result);
        return result;
    }

    private void handleSameDataSourceRule(Integer datasourceIndex, String applicationId, String createTime
        , Map<String, String> keyUsers, String partition, Map<Long, Map<String, Object>> ruleReplaceInfo
        , Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, Integer threshold, Map<String, List<Rule>> sameDataSourceRule,
        List<DataQualityTask> result, String startupParam) throws ArgumentException {

        for (String key : sameDataSourceRule.keySet()) {
            List<Rule> ruleList = sameDataSourceRule.get(key);

            Rule currentRule = ruleList.iterator().next();
            RuleDataSource currentRuleDataSource = currentRule.getRuleDataSources().iterator().next();

            StringBuilder dynamicParam = new StringBuilder();

            Map<String, Object> info = ruleReplaceInfo.get(currentRule.getId());

            if (info != null && info.keySet().contains("qualitis_startup_param")) {
                dynamicParam.append((String) info.get("qualitis_startup_param"));
            }

            List<Long> ruleIdList = ruleList.stream().map(Rule::getId).collect(Collectors.toList());
            LOGGER.info("Start to divide rules: {} into a task.", ruleIdList);
            LOGGER.info("Start to divide rules. Key: {}", key);
            String proxyUser = keyUsers.get(key);
            LOGGER.info("Divide rules executed by {}", proxyUser);
            List<RuleTaskDetail> ruleTaskDetails = new ArrayList<>();

            for (Rule rule : ruleList) {
                String tableName = generateTable(rule);
                String database = (String) ruleReplaceInfo.get(rule.getId()).get("qualitis_abnormal_database");

                String midTableName = StringUtils.isNotEmpty(database) ? (database + "." + tableName) : "";

                LOGGER.info("Rule detail list size is: {}", ruleTaskDetails.size());
                if (ruleTaskDetails.size() < threshold) {
                    LOGGER.info("Adding rules in rule detail list");
                } else {
                    List<RuleTaskDetail> ruleTaskDetailCopy = new ArrayList<>();
                    ruleTaskDetailCopy.addAll(ruleTaskDetails);
                    DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetailCopy);
                    checkAndSaveStartupParamAndShareData(tmp, dynamicParam, startupParam, proxyUser, key, partition, currentRuleDataSource, dataSourceMysqlConnect, "");
                    if (datasourceIndex != null) {
                        tmp.setIndex(datasourceIndex);
                    }
                    result.add(tmp);
                    ruleTaskDetails = new ArrayList<>();
                    LOGGER.info("Create new rule detail list");
                }
                ruleTaskDetails.add(new RuleTaskDetail(rule, midTableName));
            }
            if (ruleTaskDetails.size() > 0) {
                DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetails);
                checkAndSaveStartupParamAndShareData(tmp, dynamicParam, startupParam, proxyUser, key, partition, currentRuleDataSource, dataSourceMysqlConnect, "");
                if (datasourceIndex != null) {
                    tmp.setIndex(datasourceIndex);
                }
                result.add(tmp);
                LOGGER.info("Succeed to divide rules: {} into a task {}", ruleIdList, tmp);
            }
        }
    }

    private void checkAndSaveStartupParamAndShareData(DataQualityTask tmp, StringBuilder dynamicParam, String startupParam, String proxyUser
        , String key, String partition, RuleDataSource ruleDataSource, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String columns) {
        // 调整优先级，页面表单提交的参数>执行参数模板配置的动态引擎配置
        if (StringUtils.isNotBlank(dynamicParam.toString())) {
            tmp.setStartupParam(dynamicParam.toString());
        }

        if (StringUtils.isNotBlank(startupParam)) {
            tmp.setStartupParam(startupParam);
        }

        if (StringUtils.isNotBlank(proxyUser)) {
            LOGGER.info("Start to divide rules. Proxy user: {}", proxyUser);
            tmp.setUser(proxyUser);
        }
        if (StringUtils.isEmpty(partition)) {
            partition = ruleDataSource.getFilter();
        }
        String partOfKey = ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "." + partition;
        if (StringUtils.isEmpty(ruleDataSource.getFileId()) && StringUtils.isEmpty(ruleDataSource.getFileHashValue()) && key.contains(partOfKey)) {
            LOGGER.info("A merge data quality task, should set share data");
            tmp.setColumnShare(columns);
            tmp.setFilterShare(partition);
            tmp.setDbShare(ruleDataSource.getDbName());
            tmp.setTableShare(ruleDataSource.getTableName());
            if (dataSourceMysqlConnect != null && dataSourceMysqlConnect.get(ruleDataSource.getId()) != null) {
                tmp.setConnectShare(dataSourceMysqlConnect.get(ruleDataSource.getId()));
            }
        }
    }

    private String generateTable(Rule rule) {
        StringBuilder name = new StringBuilder();
        name.append(rule.getProject().getName()).append("_")
            .append(rule.getName());

        return name.toString();
    }

    private String getKey(Rule rule, String user, StringBuilder realUser, String partition, String splitBy) {
        List<RuleDataSource> ruleDataSourceList = rule.getRuleDataSources().stream().filter(dataSource -> (StringUtils.isNotBlank(dataSource.getDbName()) && StringUtils.isNotBlank(dataSource.getTableName())) || StringUtils.isNotEmpty(dataSource.getCollectSql())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ruleDataSourceList)) {
            RuleDataSource ruleDataSource = ruleDataSourceList.iterator().next();
            String proxyUser = ruleDataSource.getProxyUser();
            if (StringUtils.isNotBlank(proxyUser)) {
                user = proxyUser;
            }
            realUser.append(user);

            if (RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(rule.getRuleType()) || RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                String dataSourceKey = ruleDataSource.getClusterName() + "." + user;
                return rule.getId()  + "." + dataSourceKey;
            } else {
                if (StringUtils.isEmpty(partition)) {
                    partition = ruleDataSource.getFilter();
                }
                String envNames = ".";
                List<RuleDataSourceEnv> ruleDataSourceEnvs = ruleDataSource.getRuleDataSourceEnvs();
                if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
                    envNames = envNames + ruleDataSourceEnvs.stream().map(ruleDataSourceEnv -> ruleDataSourceEnv.getEnvName()).collect(Collectors.joining("_")) + envNames;
                }
                if (SPLIT_BY_NONE.equals(splitBy)) {
                    return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "." + partition + envNames + proxyUser;
                }
                if (SPLIT_BY_TABLE.equals(splitBy)) {
                    return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "." + proxyUser;
                }
                return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + proxyUser;
            }
        } else {
            realUser.append(user);
            return rule.getId().toString();
        }
    }

}
