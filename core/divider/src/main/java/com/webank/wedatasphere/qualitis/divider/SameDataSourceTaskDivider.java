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
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Divided rule into same task if they have the same datasource
 * @author howeye
 */
public class SameDataSourceTaskDivider extends AbstractTaskDivider {
    @Autowired
    private UserDao userDao;

    private static final FastDateFormat TASK_TIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private static final Logger LOGGER = LoggerFactory.getLogger(SameDataSourceTaskDivider.class);

    @Override
    public List<DataQualityTask> divide(List<Rule> rules, String applicationId, String createTime, String partition, Date date, String database, String user
        , Integer threshold)
        throws ArgumentException {
        LOGGER.info("Start to classify rules by datasource");
        Map<String, List<Rule>> sameDataSourceRule = new HashMap<>(4);
        for (Rule rule : rules) {
            String key = getKey(rule, user);
            // Rules without specific execution parameters can be split into the same task, and rules with execution parameters must be treated as a separate task.
            Boolean specifyStaticStartupParam = (rule.getSpecifyStaticStartupParam() != null && rule.getSpecifyStaticStartupParam());
            if (sameDataSourceRule.containsKey(key) && ! specifyStaticStartupParam) {
                sameDataSourceRule.get(key).add(rule);
            } else if (specifyStaticStartupParam) {
                List<Rule> tmp = new ArrayList<>();
                tmp.add(rule);
                sameDataSourceRule.put(UUID.randomUUID().toString().replace("-", "") + "." + key, tmp);
            } else {
                List<Rule> tmp = new ArrayList<>();
                tmp.add(rule);
                sameDataSourceRule.put(key, tmp);
            }
        }
        LOGGER.info("Succeed to classify rules by datasource. Result: {}", sameDataSourceRule);
        List<DataQualityTask> result = new ArrayList<>();
        for (String key : sameDataSourceRule.keySet()) {
            List<Rule> ruleList = sameDataSourceRule.get(key);
            String ruleStartup = ruleList.stream().map(Rule::getStaticStartupParam)
                .filter(staticStartupParam -> StringUtils.isNotBlank(staticStartupParam))
                .collect(Collectors.joining());
            List<Long> ruleIdList = ruleList.stream().map(Rule::getId).collect(Collectors.toList());
            LOGGER.info("Start to divide rules: {} into a task.", ruleIdList);
            LOGGER.info("Start to divide rules. Key: {}", key);
            String[] keys = key.split("\\.");
            String proxyUser = keys[keys.length - 1];

            List<RuleTaskDetail> ruleTaskDetails = new ArrayList<>();
            if (StringUtils.isNotBlank(proxyUser) && database.contains("_ind")) {
                database = proxyUser.concat("_ind");
            }
            for (Rule rule : ruleList) {
                String tableName = generateTable(rule);
                String midTableName = database + "." + tableName;

                if (ruleTaskDetails.size() < threshold) {
                    ruleTaskDetails.add(new RuleTaskDetail(rule, midTableName));
                } else {
                    List<RuleTaskDetail> ruleTaskDetailCopy = new ArrayList<>();
                    ruleTaskDetailCopy.addAll(ruleTaskDetails);
                    DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetailCopy);
                    if (StringUtils.isNotBlank(ruleStartup)) {
                        tmp.setStartupParam(ruleStartup);
                    }
                    if (StringUtils.isNotBlank(proxyUser)) {
                        LOGGER.info("Start to divide rules. Proxy user: {}", proxyUser);
                        tmp.setUser(proxyUser);
                    }
                    result.add(tmp);
                    ruleTaskDetails = new ArrayList<>();
                }
            }
            if (ruleTaskDetails.size() > 0) {
                DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetails);
                if (StringUtils.isNotBlank(ruleStartup)) {
                    tmp.setStartupParam(ruleStartup);
                }
                if (StringUtils.isNotBlank(proxyUser)) {
                    tmp.setUser(proxyUser);
                }
                result.add(tmp);
                LOGGER.info("Succeed to divide rules: {} into a task {}", ruleIdList, tmp);
            }
        }
        LOGGER.info("Succeed to divide all rules into tasks. result: {}", result);
        return result;
    }

    private String generateTable(Rule rule) {
        StringBuffer name = new StringBuffer();
        name.append(rule.getProject().getName()).append("_")
            .append(rule.getName());

        return name.toString();
    }

    private String getKey(Rule rule, String user) throws ArgumentException {
        if (rule.getRuleDataSources().size() != 0) {
            List<RuleDataSource> ruleDataSourceList = rule.getRuleDataSources().stream().filter(dataSource -> StringUtils.isNotBlank(dataSource.getDbName())).collect(
                Collectors.toList());
            RuleDataSource ruleDataSource;
            if (CollectionUtils.isNotEmpty(ruleDataSourceList)) {
                ruleDataSource = ruleDataSourceList.iterator().next();
            } else {
                ruleDataSource = rule.getRuleDataSources().iterator().next();
            }
            String proxyUser = ruleDataSource.getProxyUser();
            if (StringUtils.isNotBlank(proxyUser)) {
                return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + proxyUser;
            }
            return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName() + "." + user;

        }

        throw new ArgumentException("Error! Rule variables miss data");
    }

}
