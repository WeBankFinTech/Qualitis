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

import com.webank.wedatasphere.qualitis.config.TaskDataSourceConfig;
import com.webank.wedatasphere.qualitis.converter.SqlTemplateConverter;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

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
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private TaskDataSourceConfig taskDataSourceConfig;
    private static final String PROP_VARIABLE_NAME = "prop";
    private static final String STATISTICS_VALUE_FIELD_NAME = "value";
    private static final String STATISTICS_APPLICATION_ID_FIELD_NAME = "application_id";
    private static final String STATISTICS_RULE_ID_FIELD_NAME = "rule_id";
    private static final String STATISTICS_RULE_METRIC_ID_FIELD_NAME = "rule_metric_id";
    private static final String STATISTICS_RUN_DATE_FIELD_NAME = "run_date";
    private static final String STATISTICS_RESULT_FILED_TYPE = "result_type";
    private static final String STATISTICS_CREATE_TIME = "create_time";

    private static final String STATISTICS_VALUE_PLACEHOLDER = "${VALUE}";
    private static final String STATISTICS_APPLICATION_ID_PLACEHOLDER = "${APPLICATION_ID}";
    private static final String STATISTICS_RULE_ID_PLACEHOLDER = "${RULE_ID}";
    private static final String STATISTICS_RULE_METRIC_ID_PLACEHOLDER = "${RULE_METRIC_ID}";
    private static final String STATISTICS_RUN_DATE_PLACEHOLDER = "${RUN_DATE}";
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
        String password = taskDataSourceConfig.getPassword();
        passwordPropSentence = PROP_VARIABLE_NAME + ".setProperty(\"password\", \"" + password + "\");";
        statisticsAndSaveResultTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_VALUE_PLACEHOLDER + " as " + STATISTICS_VALUE_FIELD_NAME + "\", \"'" +
                STATISTICS_APPLICATION_ID_PLACEHOLDER + "' as " + STATISTICS_APPLICATION_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RESULT_TYPE_PLACEHOLDER + "' as " + STATISTICS_RESULT_FILED_TYPE + "\", \"'" +
                STATISTICS_RULE_ID_PLACEHOLDER + "' as " + STATISTICS_RULE_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RULE_METRIC_ID_PLACEHOLDER + "' as " + STATISTICS_RULE_METRIC_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RUN_DATE_PLACEHOLDER + "' as " + STATISTICS_RUN_DATE_FIELD_NAME + "\", \"'" +
                STATISTICS_CREATE_TIME_PLACEHOLDER + "' as " + STATISTICS_CREATE_TIME +
                "\").write.mode(org.apache.spark.sql.SaveMode.Append).jdbc(\"" + mysqlAddress + "\", \"" + resultTableName + "\", "
                + PROP_VARIABLE_NAME + ");";
    }

    /**
     * Generate statistic statement and save mysql statement
     * @param ruleId
     * @param ruleMetricMaps
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param count
     * @param runDate
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public List<String> persistenceTranslate(Long ruleId, Map<String, Long> ruleMetricMaps, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas
        , String applicationId, List<RuleVariable> ruleVariables, String createTime, Integer count, String runDate) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> list = new ArrayList<>();
        list.addAll(getStatisticsAndSaveSentence(ruleId, ruleMetricMaps, templateStatisticsInputMetas, applicationId, ruleVariables, createTime, count, runDate));
        return list;
    }

    @Override
    public List<String> getInitSentence() {
        return Arrays.asList(getDriver(), getPropSentence(), getUsernamePropSentence(), getPasswordPropSentence());
    }

    private String getDriver() {
        return "import java.sql.{Connection, DriverManager}";
    }

    /**
     * Replace all place holder in sql, and generate save mysql statement
     * @param ruleId
     * @param ruleMetricMap
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param count
     * @param runDate
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> getStatisticsAndSaveSentence(Long ruleId, Map<String, Long> ruleMetricMap,
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
        String createTime, Integer count, String runDate) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> list = new ArrayList<>();
        if (StringUtils.isBlank(runDate)) {
            sentenceWithoutRunDate(templateStatisticsInputMetas, ruleVariables, list, applicationId, createTime, count, ruleId, ruleMetricMap);
        } else {
            sentenceWithRunDate(templateStatisticsInputMetas, ruleVariables, list, applicationId, createTime, count, ruleId, ruleMetricMap, runDate);

        }

        return list;
    }

    private void sentenceWithRunDate(Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, List<RuleVariable> ruleVariables,
        List<String> list, String applicationId, String createTime, Integer count, Long ruleId, Map<String, Long> ruleMetricMap, String runDate)
        throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        Date runRealDate = null;
        try {
            runRealDate = new SimpleDateFormat("yyyyMMdd").parse(runDate);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuleVariableNotSupportException("{&FAILED_TO_PARSE_RUN_DATE}");
        }
        list.add("var connection: Connection = null");
        list.add("classOf[com.mysql.jdbc.Driver]");
        list.add("try {");
        list.add("\tconnection = DriverManager.getConnection(\"" + mysqlAddress + "\", " + PROP_VARIABLE_NAME + ")");
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
            persistSentence = persistSentence.replace(STATISTICS_RUN_DATE_PLACEHOLDER, runRealDate.getTime() + "");
            StringBuffer selectSql = new StringBuffer();
            StringBuffer deleteSql = new StringBuffer();
            String varName = s.getName().replace("{", "").replace("}", "").replace("&", "");
            if (ruleMetricMap.get(value) != null) {
                persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.get(value) + "");
                selectSql.append("val selectSql").append("_").append(varName)
                    .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                    .append(" and rule_metric_id = ").append(ruleMetricMap.get(value))
                    .append(" and (run_date = ").append(runRealDate.getTime())
                    .append(")) qualitis_tmp_table\"");
                deleteSql.append("val deleteSql").append("_").append(varName)
                    .append(" = \"delete from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                    .append(" and rule_metric_id = ").append(ruleMetricMap.get(value))
                    .append(" and (run_date = ").append(runRealDate.getTime())
                    .append(")\"");
            } else {
                if (CollectionUtils.isNotEmpty(ruleMetricMap.values())) {
                    persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.values().iterator().next() + "");
                    selectSql.append("val selectSql").append("_").append(varName)
                        .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append(ruleMetricMap.values().iterator().next())
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")) qualitis_tmp_table\"");
                    deleteSql.append("val deleteSql").append("_").append(varName)
                        .append(" = \"delete from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append(ruleMetricMap.values().iterator().next())
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")\"");
                } else {
                    persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, "-1");
                    selectSql.append("val selectSql").append("_").append(varName)
                        .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append("-1")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")) qualitis_tmp_table\"");
                    deleteSql.append("val deleteSql").append("_").append(varName)
                        .append(" = \"delete from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append("-1")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")\"");
                }
            }
            list.add(selectSql.toString());
            // Judge the existence of task result with rule ID, rule metric ID, run date.
            list.add("val resultDF" + "_" + varName + " = spark.read.jdbc(\"" + mysqlAddress + "\", selectSql" + "_" + varName + ", prop)");
            list.add("val lines" + "_" + varName + " = resultDF" + "_" + varName + ".count()");
            list.add("if (lines" + "_" + varName + " >= 1) {");
            // Delete the exist task result before insert.
            list.add(deleteSql.toString());
            list.add("connection.createStatement().executeUpdate(deleteSql" + "_" + varName + ")");
            list.add("}");
            list.add(persistSentence);
            LOGGER.info("Succeed to get persist sentence. sentence: {}", persistSentence);
        }
        list.add("} catch {");
        list.add("case e: Exception => println(\"JDBC operations failed because of \", e.getMessage())");
        list.add("} finally {");
        list.add("\tconnection.close()");
        list.add("}");
    }

    private void sentenceWithoutRunDate(Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, List<RuleVariable> ruleVariables, List<String> list
        , String applicationId, String createTime, Integer count, Long ruleId, Map<String, Long> ruleMetricMap) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
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

            if (ruleMetricMap.get(value) != null) {
                persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.get(value) + "");
            } else {
                if (CollectionUtils.isNotEmpty(ruleMetricMap.values())) {
                    persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.values().iterator().next() + "");
                } else {
                    persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, "-1");
                }
            }
            persistSentence = persistSentence.replace(STATISTICS_RUN_DATE_PLACEHOLDER, "-1");
            list.add(persistSentence);
            LOGGER.info("Succeed to get persist sentence. sentence: {}", persistSentence);
        }
    }

    /**
     * Get argument value from statistics step
     * @param ruleVariables
     * @param templateStatisticsInputMeta
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private String getValue(List<RuleVariable> ruleVariables, TemplateStatisticsInputMeta templateStatisticsInputMeta)
        throws RuleVariableNotSupportException, RuleVariableNotFoundException {
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

        throw new RuleVariableNotFoundException("Rule_variable of statistics_arg: [" + templateStatisticsInputMeta.getId() + "] {&DOES_NOT_EXIST}");
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
