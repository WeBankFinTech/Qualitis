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
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Divided rule into same task if they have the same datasource
 * @author howeye
 */
public class SameDataSourceTaskDivider extends AbstractTaskDivider {

    private static final FastDateFormat TASK_TIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private static final Logger LOGGER = LoggerFactory.getLogger(SameDataSourceTaskDivider.class);

    private Random random = new Random();

    @Override
    public List<DataQualityTask> divide(List<Rule> rules, String applicationId, String createTime, String partition, Date date, String database, Integer threshold) throws ArgumentException {
        LOGGER.info("Start to divide rules into tasks, according to if datasource of the rule is the same or not");
        if (null == rules || rules.isEmpty()) {
            throw new ArgumentException("Argument of rules can not be null or empty");
        }

        LOGGER.info("Start to classify rules by datasource");
        Map<String, List<Rule>> sameDataSourceRule = new HashMap<>(4);
        for (Rule rule : rules) {
            String key = getKey(rule);
            if (sameDataSourceRule.containsKey(key)) {
                sameDataSourceRule.get(key).add(rule);
            } else {
                List<Rule> tmp = new ArrayList<>();
                tmp.add(rule);
                sameDataSourceRule.put(key, tmp);
            }
        }
        LOGGER.info("Succeed to classify rules by datasource. result: {}", sameDataSourceRule);

        List<DataQualityTask> result = new ArrayList<>();
        for (String key : sameDataSourceRule.keySet()) {
            List<Rule> ruleList = sameDataSourceRule.get(key);
            List<Long> ruleIdList = ruleList.stream().map(Rule::getId).collect(Collectors.toList());
            LOGGER.info("Start to divide rules: {} into a task.", ruleIdList);

            List<RuleTaskDetail> ruleTaskDetails = new ArrayList<>();
            for (Rule rule : ruleList) {
                String nonce = RandomStringUtils.randomNumeric(6);
                String tableName = generateTable(date, rule, nonce);
                String midTableName = database + "." + tableName;
                if (rule.getChildRule() != null) {
                    nonce = RandomStringUtils.randomNumeric(6);
                    tableName = generateTable(date, rule, nonce);
                    midTableName += "," + database + "." + tableName;
                }

                if (ruleTaskDetails.size() < threshold) {
                    ruleTaskDetails.add(new RuleTaskDetail(rule, midTableName));
                } else {
                    List<RuleTaskDetail> ruleTaskDetailCopy = new ArrayList<>();
                    ruleTaskDetailCopy.addAll(ruleTaskDetails);
                    DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetailCopy);
                    result.add(tmp);
                    ruleTaskDetails = new ArrayList<>();
                }
            }
            if (ruleTaskDetails.size() > 0) {
                DataQualityTask tmp = new DataQualityTask(applicationId, createTime, partition, ruleTaskDetails);
                result.add(tmp);
                LOGGER.info("Succeed to divide rules: {} into a task {}", ruleIdList, tmp);
            }
        }
        LOGGER.info("Succeed to divide all rules into tasks. result: {}", result);

        return result;
    }

    private String generateTable(Date date, Rule rule, String nonce) {
        return "mid_application_" + rule.getId() + "_" + TASK_TIME_FORMAT.format(date) + "_" + nonce;
    }

    private String getKey(Rule rule) throws ArgumentException {
        if (rule.getRuleDataSources().size() != 0) {
            RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
            return ruleDataSource.getClusterName() + "." + ruleDataSource.getDbName();
        }

        throw new ArgumentException("Error! Rule variables miss data");
    }

}
