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

package com.webank.wedatasphere.qualitis.translator;

import com.webank.wedatasphere.qualitis.converter.SqlTemplateConverter;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.converter.SqlTemplateConverter;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Generate scala code of connecting mysql and save data into mysql
 * Example：
 * val prop = new java.util.Properties;
 * prop.setProperty("user", "*****");
 * prop.setProperty("password", "*****");
 * tmp1.selectExpr("count(*) as value", "'QUALITIS_20190117094607_649214' as task_id", "'Long' as result_type", "'26' as rule_id", "'2019-01-17 09:46:07' as create_time").write.mode(org.apache.spark.sql.SaveMode.Append).jdbc("jdbc:mysql://host:port/*****", "application_task_result", prop);,
 * @author howeye
 */
@Configuration
@ConditionalOnProperty(name = "task.persistent.type", havingValue = "jdbc")
public class JdbcTranslator extends AbstractTranslator {

    @Value("${task.persistent.username}")
    private String mysqlUsername;
    @Value("${task.persistent.password}")
    private String mysqlPassword;
    @Value("${task.persistent.address}")
    private String mysqlAddress;
    @Value("${task.persistent.tableName}")
    private String resultTableName;

    private static final String PROP_VARIABLE_NAME = "prop";
    private static final String STATISTICS_VALUE_FIELD_NAME = "value";
    private static final String STATISTICS_APPLICATION_ID_FIELD_NAME = "application_id";
    private static final String STATISTICS_RULE_ID_FIELD_NAME = "rule_id";
    private static final String STATISTICS_RESULT_FILED_TYPE = "result_type";
    private static final String STATISTICS_CREATE_TIME = "create_time";

    private static final String STATISTICS_VALUE_PLACEHOLDER = "${VALUE}";
    private static final String STATISTICS_APPLICATION_ID_PLACEHOLDER = "${APPLICATION_ID}";
    private static final String STATISTICS_RULE_ID_PLACEHOLDER = "${RULE_ID}";
    private static final String STATISTICS_RESULT_TYPE_PLACEHOLDER = "${RESULT_TYPE}";
    private static final String STATISTICS_CREATE_TIME_PLACEHOLDER = "${CREATE_TIME}";

    private static final String DECLARE_PROP_SENTENCE = "val " + PROP_VARIABLE_NAME + " = new java.util.Properties;";
    private String usernamePropSentence;
    private String passwordPropSentence;
    private String statisticsAndSaveResultTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTranslator.class);

    /**
     * Initial statement
     */
    @PostConstruct
    public void init() {
        usernamePropSentence = PROP_VARIABLE_NAME + ".setProperty(\"user\", \"" + mysqlUsername + "\");";
        passwordPropSentence = PROP_VARIABLE_NAME + ".setProperty(\"password\", \"" + mysqlPassword + "\");";
        statisticsAndSaveResultTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_VALUE_PLACEHOLDER + " as " + STATISTICS_VALUE_FIELD_NAME + "\", \"'" +
                STATISTICS_APPLICATION_ID_PLACEHOLDER + "' as " + STATISTICS_APPLICATION_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RESULT_TYPE_PLACEHOLDER + "' as " + STATISTICS_RESULT_FILED_TYPE + "\", \"'" +
                STATISTICS_RULE_ID_PLACEHOLDER + "' as " + STATISTICS_RULE_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_CREATE_TIME_PLACEHOLDER + "' as " + STATISTICS_CREATE_TIME +
                "\").write.mode(org.apache.spark.sql.SaveMode.Append).jdbc(\"" + mysqlAddress + "\", \"" + resultTableName + "\", "
                + PROP_VARIABLE_NAME + ");";
    }

    /**
     * Generate statistic statement and save mysql statement
     * @param ruleId
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param count
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public List<String> persistenceTranslate(Long ruleId, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
                                             String createTime, Integer count) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> list = new ArrayList<>();
        list.addAll(getStatisticsAndSaveSentence(ruleId, templateStatisticsInputMetas, applicationId, ruleVariables, createTime, count));
        return list;
    }

    @Override
    public List<String> getInitSentence() {
        return Arrays.asList(getPropSentence(), getUsernamePropSentence(), getPasswordPropSentence());
    }

    /**
     * Replace all place holder in sql, and generate save mysql statement
     * @param ruleId
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param count
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> getStatisticsAndSaveSentence(Long ruleId, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
                                                      String createTime, Integer count) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> list = new ArrayList<>();
        for (TemplateStatisticsInputMeta s : templateStatisticsInputMetas) {
            String funcName = s.getFuncName();
            String value = getValue(ruleVariables, s);
            String persistSentence = statisticsAndSaveResultTemplate
                    .replace(STATISTICS_VALUE_PLACEHOLDER, funcName + "(" + value + ")")
                    .replace(STATISTICS_APPLICATION_ID_PLACEHOLDER, applicationId)
                    .replace(STATISTICS_RESULT_TYPE_PLACEHOLDER, s.getResultType())
                    .replace(STATISTICS_CREATE_TIME_PLACEHOLDER, createTime)
                    .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, getVariable(count))
                    .replace(STATISTICS_RULE_ID_PLACEHOLDER, ruleId + "");
            list.add(persistSentence);
            LOGGER.info("Succeed to get persist sentence. sentence: {}", persistSentence);
        }
        return list;
    }

    /**
     * Get argument value from statistics step
     * @param ruleVariables
     * @param templateStatisticsInputMeta
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private String getValue(List<RuleVariable> ruleVariables, TemplateStatisticsInputMeta templateStatisticsInputMeta) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        if (templateStatisticsInputMeta.getValueType().equals(StatisticsValueTypeEnum.FIXED_VALUE.getCode())) {
            return templateStatisticsInputMeta.getValue();
        } else {
            for (RuleVariable ruleVariable : ruleVariables) {
                if (!ruleVariable.getInputActionStep().equals(InputActionStepEnum.STATISTICS_ARG.getCode())) {
                    throw new RuleVariableNotSupportException("Action_step of rule_variable " + ruleVariable.getInputActionStep() + " does not support");
                }

                if (ruleVariable.getTemplateStatisticsInputMeta().equals(templateStatisticsInputMeta)) {
                    return ruleVariable.getValue();
                }
            }
        }

        throw new RuleVariableNotFoundException("Rule_variable of statistics_arg: [" + templateStatisticsInputMeta.getId() + "] does not exist");
    }


    private String getPasswordPropSentence() {
        return passwordPropSentence;
    }

    private String getUsernamePropSentence() {
        return usernamePropSentence;
    }

    private String getPropSentence() {
        return DECLARE_PROP_SENTENCE;
    }

    /**
     * Get tmp variable
     * @param count
     * @return
     */
    private String getVariable(Integer count) {
        return "tmp" + count;
    }
}
