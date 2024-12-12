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
import com.webank.wedatasphere.qualitis.constant.OptTypeEnum;
import com.webank.wedatasphere.qualitis.converter.SqlTemplateConverter;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate scala code of connecting mysql and save data into mysql
 * Example：
 * val prop = new java.util.Properties;
 * prop.setProperty("user", "*****");
 * prop.setProperty("password", "*****");
 * tmp1.selectExpr("count(*) as value", "'QUALITIS_20190117094607_649214' as task_id", "'Long' as result_type", "'26' as rule_id", "'2019-01-17 09:46:07' as create_time").write.mode(org.apache.spark.sql.SaveMode.Append).jdbc("jdbc:mysql://host:port/*****", "application_task_result", prop);,
 *
 * @author howeye
 */
@Configuration
@ConditionalOnProperty(name = "task.persistent.type", havingValue = "jdbc")
public class JdbcTranslator extends AbstractTranslator {
    @Value("${task.persistent.username}")
    private String mysqlUsername;
    @Value("${task.persistent.password}")
    private String mysqlSecret;
    @Value("${task.persistent.address}")
    private String mysqlAddress;
    @Value("${task.persistent.tableName}")
    private String resultTableName;
    @Value("${task.new_value.tableName}")
    private String newValueTableName;
    @Value("${task.new_value.save}")
    private String newValueTableSave;
    @Value("${task.persistent.encrypt: false}")
    private Boolean isEncrypt;

    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private TaskDataSourceConfig taskDataSourceConfig;

    private static final String PROP_VARIABLE_NAME = "prop";
    private static final String STATISTICS_VALUE_FIELD_NAME = "value";
    private static final String STATISTICS_RULE_ID_FIELD_NAME = "rule_id";
    private static final String STATISTICS_APPLICATION_ID_FIELD_NAME = "application_id";
    private static final String STATISTICS_RULE_METRIC_ID_FIELD_NAME = "rule_metric_id";
    private static final String STATISTICS_RUN_DATE_FIELD_NAME = "run_date";
    private static final String STATISTICS_ENV_NAME_FIELD_NAME = "env_name";
    private static final String STATISTICS_RESULT_FILED_TYPE = "result_type";
    private static final String STATISTICS_CREATE_TIME = "create_time";
    private static final String STATISTICS_STATUS = "status";
    private static final String STATISTICS_VERSION = "version";
    private static final String STATISTICS_RULE_VERSION = "rule_version";
    private static final String STATISTICS_CREATE_USER = "create_user";
    private static final String STATISTICS_RESULT_VALUE = "result_value";
    private static final String STATISTICS_CUSTOM_COLUMN = "custom_column";
    private static final String STATISTICS_CONCAT_NEW_REAL_COLUMN = "${NEW_REAL_COLUMN}";
    private static final String STATISTICS_CONCAT_WS = "concat_ws(',',${NEW_REAL_COLUMN})";

    private static final String STATISTICS_VALUE_PLACEHOLDER = "${VALUE}";
    private static final String STATISTICS_RULE_ID_PLACEHOLDER = "${RULE_ID}";
    private static final String STATISTICS_VERSION_PLACEHOLDER = "${VERSION}";
    private static final String STATISTICS_RUN_DATE_PLACEHOLDER = "${RUN_DATE}";
    private static final String STATISTICS_ENV_NAME_PLACEHOLDER = "${ENV_NAME}";
    private static final String STATISTICS_APPLICATION_ID_PLACEHOLDER = "${APPLICATION_ID}";
    private static final String STATISTICS_RULE_METRIC_ID_PLACEHOLDER = "${RULE_METRIC_ID}";
    private static final String STATISTICS_RESULT_TYPE_PLACEHOLDER = "${RESULT_TYPE}";
    private static final String STATISTICS_CREATE_TIME_PLACEHOLDER = "${CREATE_TIME}";
    private static final String STATISTICS_CREATE_USER_PLACEHOLDER = "${CREATE_USER}";
    private static final String STATISTICS_RULE_VERSION_PLACEHOLDER = "${RULE_VERSION}";

    private static final String DECLARE_PROP_SENTENCE = "val " + PROP_VARIABLE_NAME + " = new java.util.Properties;";
    private static final Integer ONE = 1;


    private String usernamePropSentence;
    private String passwordPropSentence;
    private String taskNewVauleTemplate;
    private String statisticsValueTemplate;
    private String taskNumberRangeTemplate;
    private String statisticsAndSaveResultTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTranslator.class);
    private HttpServletRequest httpServletRequest;

    public JdbcTranslator(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Initial statement
     */
    @PostConstruct
    public void init() {
        usernamePropSentence = PROP_VARIABLE_NAME + ".setProperty(\"user\", \"" + mysqlUsername + "\");";
        if (isEncrypt) {
//            String passwordPrivateKey = taskDataSourceConfig.getPrivateKey();
//            try {
//                mysqlSecret = EncryptUtil.decrypt(passwordPrivateKey, taskDataSourceConfig.getPassword());
//            } catch (Exception e) {
//                LOGGER.error("Decrypt mysqlsec password exception.", e);
//            }
        } else {
            mysqlSecret = taskDataSourceConfig.getPassword();
        }
        passwordPropSentence = PROP_VARIABLE_NAME + ".setProperty(\"password\", \"" + mysqlSecret + "\");";
        statisticsAndSaveResultTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_VALUE_PLACEHOLDER + " as " + STATISTICS_VALUE_FIELD_NAME + "\", \"'" +
                STATISTICS_APPLICATION_ID_PLACEHOLDER + "' as " + STATISTICS_APPLICATION_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RESULT_TYPE_PLACEHOLDER + "' as " + STATISTICS_RESULT_FILED_TYPE + "\", \"'" +
                STATISTICS_RULE_ID_PLACEHOLDER + "' as " + STATISTICS_RULE_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_VERSION_PLACEHOLDER + "' as " + STATISTICS_VERSION + "\", \"'" +
                STATISTICS_RULE_METRIC_ID_PLACEHOLDER + "' as " + STATISTICS_RULE_METRIC_ID_FIELD_NAME + "\", \"'" +
                STATISTICS_RUN_DATE_PLACEHOLDER + "' as " + STATISTICS_RUN_DATE_FIELD_NAME + "\", \"'" +
                STATISTICS_ENV_NAME_PLACEHOLDER + "' as " + STATISTICS_ENV_NAME_FIELD_NAME + "\", \"'" +
                STATISTICS_CREATE_TIME_PLACEHOLDER + "' as " + STATISTICS_CREATE_TIME +
                "\").write.mode(org.apache.spark.sql.SaveMode.Append).jdbc(\"" + mysqlAddress + "\", \"" + resultTableName + "\", "
                + PROP_VARIABLE_NAME + ");";
        statisticsValueTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_VALUE_PLACEHOLDER + " as " + STATISTICS_VALUE_FIELD_NAME + "\")" + ".rdd.map(r => r(0)).collect()";
        //初始化新值 spark sql拼接
        taskNewVauleTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_RULE_ID_PLACEHOLDER + " as " + STATISTICS_RULE_ID_FIELD_NAME + "\", \"" +
                ONE + " as " + STATISTICS_STATUS + "\", \"'" +
                STATISTICS_CREATE_USER_PLACEHOLDER + "' as " + STATISTICS_CREATE_USER + "\", \"'" +
                STATISTICS_RULE_VERSION_PLACEHOLDER + "' as " + STATISTICS_RULE_VERSION + "\", \"'" +
                STATISTICS_CREATE_TIME_PLACEHOLDER + "' as " + STATISTICS_CREATE_TIME + "\", \"" +
            STATISTICS_CONCAT_WS + " as " + STATISTICS_RESULT_VALUE +
                "\")." + newValueTableSave + ".jdbc(\"" + mysqlAddress + "\", \"" + newValueTableName + "\", "
                + PROP_VARIABLE_NAME + ");";
        //初始化数值范围 sqark sql拼接
        taskNumberRangeTemplate = SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER + ".selectExpr(\"" +
                STATISTICS_RULE_ID_PLACEHOLDER + " as " + STATISTICS_RULE_ID_FIELD_NAME + "\", \"" +
                ONE + " as " + STATISTICS_STATUS + "\", \"'" +
                STATISTICS_CREATE_USER_PLACEHOLDER + "' as " + STATISTICS_CREATE_USER + "\", \"'" +
                STATISTICS_RULE_VERSION_PLACEHOLDER + "' as " + STATISTICS_RULE_VERSION + "\", \"'" +
                STATISTICS_CREATE_TIME_PLACEHOLDER + "' as " + STATISTICS_CREATE_TIME + "\", \"" +
                STATISTICS_CUSTOM_COLUMN + " as " + STATISTICS_RESULT_VALUE +
                "\")." + newValueTableSave + ".jdbc(\"" + mysqlAddress + "\", \"" + newValueTableName + "\", "
                + PROP_VARIABLE_NAME + ");";
    }

    /**
     * Generate statistic statement and save mysql statement
     *
     * @param ruleId
     * @param ruleMetricMaps
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param partOfVariableName
     * @param runDate
     * @param user
     * @param realColumn
     * @param enumListNewValue
     * @param numRangeNewValue
     * @param selectResult
     * @param unionAllForSaveResult
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public List<String> persistenceTranslate(String workFlowVersion, Long ruleId, Map<String, Long> ruleMetricMaps, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas,
        String applicationId, List<RuleVariable> ruleVariables, String createTime, String partOfVariableName, String runDate, String user, StringBuilder realColumn, boolean enumListNewValue, boolean numRangeNewValue, Map<String, String> selectResult, boolean unionAllForSaveResult) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        List<String> list = new ArrayList<>();
        list.addAll(getStatisticsAndSaveSentence(workFlowVersion,ruleId, ruleMetricMaps, templateStatisticsInputMetas, applicationId, ruleVariables, createTime, partOfVariableName
            , runDate, user, realColumn, enumListNewValue, numRangeNewValue, selectResult, unionAllForSaveResult));
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
     *
     * @param workFlowVersion
     * @param ruleId
     * @param ruleMetricMap
     * @param templateStatisticsInputMetas
     * @param applicationId
     * @param ruleVariables
     * @param createTime
     * @param partOfVariableName
     * @param runDate
     * @param realColumn
     * @param enumListNewValue
     * @param numRangeNewValue
     * @param selectResult
     * @param unionAllForSaveResult
     * @return
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> getStatisticsAndSaveSentence(String workFlowVersion, Long ruleId, Map<String, Long> ruleMetricMap
        , Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables
        , String createTime, String partOfVariableName, String runDate, String user, StringBuilder realColumn, boolean enumListNewValue, boolean numRangeNewValue, Map<String, String> selectResult, boolean unionAllForSaveResult) throws RuleVariableNotSupportException, RuleVariableNotFoundException {

        List<String> list = new ArrayList<>();
        Map<String, Long> newRuleMetricMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(ruleMetricMap.entrySet())) {
            for (Map.Entry<String,Long> entry : ruleMetricMap.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                newRuleMetricMap.put(key.replace("-", "_"), value);
            }
        }
        if (StringUtils.isBlank(runDate)) {
            sentenceWithoutRunDate(workFlowVersion, templateStatisticsInputMetas, ruleVariables, list, applicationId, createTime, partOfVariableName, ruleId, newRuleMetricMap, user, enumListNewValue, numRangeNewValue, realColumn, selectResult, unionAllForSaveResult);
        } else {
            sentenceWithRunDate(workFlowVersion, templateStatisticsInputMetas, ruleVariables, list, applicationId, createTime, partOfVariableName, ruleId, newRuleMetricMap, user, runDate, enumListNewValue, numRangeNewValue, realColumn, selectResult, unionAllForSaveResult);
        }

        return list;
    }

    private void sentenceWithRunDate(String workFlowVersion, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas,
        List<RuleVariable> ruleVariables, List<String> list, String applicationId, String createTime, String partOfVariableName, Long ruleId, Map<String, Long> ruleMetricMap,
        String user, String runDate, boolean enumListNewValue, boolean numRangeNewValue, StringBuilder realColumn, Map<String, String> selectResult, boolean unionAllForSaveResult) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        Date runRealDate;

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
        if (selectResult != null && CollectionUtils.isNotEmpty(selectResult.keySet())) {
            List<String> varList = selectResult.keySet().stream().collect(Collectors.toList());

            for (String variable : varList) {
                // 聚合处理
                if (unionAllForSaveResult) {
                    constructStaticSqlWithRunDate(templateStatisticsInputMetas, ruleVariables, runRealDate, applicationId, createTime, ruleId, variable, selectResult.get(variable), workFlowVersion, ruleMetricMap, list);
                    break;
                }
                constructStaticSqlWithRunDate(templateStatisticsInputMetas, ruleVariables, runRealDate, applicationId, createTime, ruleId, variable, selectResult.get(variable), workFlowVersion, ruleMetricMap, list);
            }
            list.add("} catch {");
            list.add("\tcase e: Exception => println(\"JDBC operations failed because of \", e.getMessage())");
            list.add("} finally {");
            list.add("\tconnection.close()");
            list.add("}");
            // Handle new value
            handleNewValue(workFlowVersion, user, realColumn, createTime, partOfVariableName, ruleId, list, enumListNewValue, numRangeNewValue, "");
            return;
        }

        constructStaticSqlWithRunDate(templateStatisticsInputMetas, ruleVariables, runRealDate, applicationId, createTime, ruleId, getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName), "", workFlowVersion, ruleMetricMap, list);

        list.add("} catch {");
        list.add("\tcase e: Exception => println(\"JDBC operations failed because of \", e.getMessage())");
        list.add("} finally {");
        list.add("\tconnection.close()");
        list.add("}");

        // Handle new value
        handleNewValue(workFlowVersion, user, realColumn, createTime, partOfVariableName, ruleId, list, enumListNewValue, numRangeNewValue, "");
    }

    private void constructStaticSqlWithRunDate(Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, List<RuleVariable> ruleVariables, Date runRealDate
        , String applicationId, String createTime, Long ruleId, String variable, String envName, String workFlowVersion, Map<String, Long> ruleMetricMap, List<String> list) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        for (TemplateStatisticsInputMeta s : templateStatisticsInputMetas) {
            String funcName = s.getFuncName();
            String value = getValue(ruleVariables, s);
            String persistSentence = statisticsAndSaveResultTemplate
                .replace(STATISTICS_VALUE_PLACEHOLDER, funcName + "(" + value + ")")
                .replace(STATISTICS_RESULT_TYPE_PLACEHOLDER, s.getResultType())
                .replace(STATISTICS_APPLICATION_ID_PLACEHOLDER, applicationId)
                .replace(STATISTICS_CREATE_TIME_PLACEHOLDER, createTime)
                .replace(STATISTICS_RULE_ID_PLACEHOLDER, ruleId + "")
                .replace(STATISTICS_ENV_NAME_PLACEHOLDER, envName)
                .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, variable)
                .replace(STATISTICS_VERSION_PLACEHOLDER, workFlowVersion + "");

            persistSentence = persistSentence.replace(STATISTICS_RUN_DATE_PLACEHOLDER, runRealDate.getTime() + "");
            String statisticsValueSentence = "val res_" + variable + " = " + statisticsValueTemplate.replace(STATISTICS_VALUE_PLACEHOLDER, funcName + "(" + value + ")")
                .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, variable);
            String realValueName = "resValue_" + variable;
            String realValueSentence = "val " + realValueName + " = " + "res_" + variable + "(0)";
            StringBuilder selectSql = new StringBuilder();
            StringBuilder updateSql = new StringBuilder();
            persistSentence = judgeRuleMetricMap(ruleId, ruleMetricMap, runRealDate, value, persistSentence, selectSql, updateSql, variable, realValueName, envName);
            list.add("\t" + selectSql.toString());
            // Judge the existence of task result with rule ID, rule metric ID, run date.
            list.add("\tval resultDF" + "_" + variable + " = spark.read.jdbc(\"" + mysqlAddress + "\", selectSql" + "_" + variable + ", prop)");
            list.add("\tval lines" + "_" + variable + " = resultDF" + "_" + variable + ".count()");
            list.add("\tif (lines" + "_" + variable + " >= 1) {");
            // Update the exist task result before insert.
            list.add("\t\t" + statisticsValueSentence);
            list.add("\t\t" + realValueSentence);
            list.add("\t\t" + updateSql.toString());
            list.add("\t\tconnection.createStatement().executeUpdate(updateSql" + "_" + variable + ")");
            int index = persistSentence.indexOf("selectExpr(") + "selectExpr(".length();
            StringBuilder notSaveResultSentence = new StringBuilder(persistSentence);
            list.add("\t\t" + notSaveResultSentence.insert(index, "\"0 as save_result\", ").toString());
            list.add("\t} else {");
            list.add("\t\t" + persistSentence);
            list.add("\t}");
            LOGGER.info("Succeed to get persist sentence. sentence: {}", persistSentence);
        }
    }

    private String judgeRuleMetricMap(Long ruleId, Map<String, Long> ruleMetricMap, Date runRealDate, String value, String persistSentence,
        StringBuilder selectSql, StringBuilder updateSql, String variable, String realValueName, String envName) {
        if (ruleMetricMap.get(value) != null) {
            persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.get(value) + "");
            selectSql.append("val selectSql").append("_").append(variable)
                    .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                    .append(" and rule_metric_id = ").append(ruleMetricMap.get(value))
                    .append(" and save_result = 1")
                    .append(" and env_name = '").append(envName).append("'")
                    .append(" and (run_date = ").append(runRealDate.getTime())
                    .append(")) qualitis_tmp_table\"");
            updateSql.append("val updateSql").append("_").append(variable)
                    .append(" = \"update ").append(resultTableName).append(" set value = \"").append(" + ").append(realValueName).append(" + ").append("\" where rule_id = ").append(ruleId)
                    .append(" and rule_metric_id = ").append(ruleMetricMap.get(value))
                    .append(" and save_result = 1")
                    .append(" and env_name = '").append(envName).append("'")
                    .append(" and (run_date = ").append(runRealDate.getTime())
                    .append(")\"");
        } else {
            if (CollectionUtils.isNotEmpty(ruleMetricMap.values())) {
                persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, ruleMetricMap.values().iterator().next() + "");
                selectSql.append("val selectSql").append("_").append(variable)
                        .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append(ruleMetricMap.values().iterator().next())
                        .append(" and save_result = 1")
                        .append(" and env_name = '").append(envName).append("'")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")) qualitis_tmp_table\"");
                updateSql.append("val updateSql").append("_").append(variable)
                        .append(" = \"update ").append(resultTableName).append(" set value = \"").append(" + ").append(realValueName).append(" + ").append("\" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append(ruleMetricMap.values().iterator().next())
                        .append(" and save_result = 1")
                        .append(" and env_name = '").append(envName).append("'")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")\"");
            } else {
                persistSentence = persistSentence.replace(STATISTICS_RULE_METRIC_ID_PLACEHOLDER, "-1");
                selectSql.append("val selectSql").append("_").append(variable)
                        .append(" = \"(select * from ").append(resultTableName).append(" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append("-1")
                        .append(" and save_result = 1")
                        .append(" and env_name = '").append(envName).append("'")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")) qualitis_tmp_table\"");
                updateSql.append("val updateSql").append("_").append(variable)
                        .append(" = \"update ").append(resultTableName).append(" set value = \"").append(" + ").append(realValueName).append(" + ").append("\" where rule_id = ").append(ruleId)
                        .append(" and rule_metric_id = ").append("-1")
                        .append(" and save_result = 1")
                        .append(" and env_name = '").append(envName).append("'")
                        .append(" and (run_date = ").append(runRealDate.getTime())
                        .append(")\"");
            }
        }
        return persistSentence;
    }

    private void sentenceWithoutRunDate(String workFlowVersion, Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas
        , List<RuleVariable> ruleVariables, List<String> list, String applicationId, String createTime, String partOfVariableName, Long ruleId
        , Map<String, Long> ruleMetricMap, String user, boolean enumListNewValue, boolean numRangeNewValue, StringBuilder realColumn, Map<String, String> selectResult, boolean unionAllForSaveResult) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        if (selectResult != null && CollectionUtils.isNotEmpty(selectResult.keySet())) {
            List<String> varList = selectResult.keySet().stream().collect(Collectors.toList());

            for (String variable : varList) {
                // 聚合处理
                if (unionAllForSaveResult) {
                    constructStaticSql(templateStatisticsInputMetas, ruleVariables, applicationId, createTime, partOfVariableName, ruleId, workFlowVersion, ruleMetricMap, list, variable, selectResult.get(variable));
                    // Handle new value
                    handleNewValue(workFlowVersion, user, realColumn, createTime, partOfVariableName, ruleId, list, enumListNewValue, numRangeNewValue, variable);
                    break;
                }
                constructStaticSql(templateStatisticsInputMetas, ruleVariables, applicationId, createTime, partOfVariableName, ruleId, workFlowVersion, ruleMetricMap, list, variable, selectResult.get(variable));
                // Handle new value
                handleNewValue(workFlowVersion, user, realColumn, createTime, partOfVariableName, ruleId, list, enumListNewValue, numRangeNewValue, variable);
            }
            return;
        }
        constructStaticSql(templateStatisticsInputMetas, ruleVariables, applicationId, createTime, partOfVariableName, ruleId, workFlowVersion, ruleMetricMap, list, "", "");

        // Handle new value
        handleNewValue(workFlowVersion, user, realColumn, createTime, partOfVariableName, ruleId, list, enumListNewValue, numRangeNewValue, "");
    }

    private void constructStaticSql(Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, List<RuleVariable> ruleVariables, String applicationId
        , String createTime, String partOfVariableName, Long ruleId, String workFlowVersion, Map<String, Long> ruleMetricMap, List<String> list, String realVariable, String envName) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        for (TemplateStatisticsInputMeta s : templateStatisticsInputMetas) {
            String funcName = s.getFuncName();
            String value = getValue(ruleVariables, s);
            String persistSentence = statisticsAndSaveResultTemplate
                .replace(STATISTICS_VALUE_PLACEHOLDER, funcName + "(" + value + ")")
                .replace(STATISTICS_RESULT_TYPE_PLACEHOLDER, s.getResultType())
                .replace(STATISTICS_APPLICATION_ID_PLACEHOLDER, applicationId)
                .replace(STATISTICS_CREATE_TIME_PLACEHOLDER, createTime)
                .replace(STATISTICS_RULE_ID_PLACEHOLDER, ruleId + "")
                .replace(STATISTICS_ENV_NAME_PLACEHOLDER, envName + "")
                .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, StringUtils.isNotBlank(realVariable) ? realVariable : getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName))
                .replace(STATISTICS_VERSION_PLACEHOLDER, workFlowVersion + "");

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

    private void handleNewValue(String workFlowVersion, String user, StringBuilder realColumn, String createTime, String partOfVariableName, Long ruleId,
        List<String> list, boolean enumListNewValue, boolean numRangeNewValue, String realVariable) {
        //枚举值 新值替换
        if (enumListNewValue) {
            String newValueDisplace = taskNewVauleTemplate
                .replace(STATISTICS_CREATE_USER_PLACEHOLDER, user)
                .replace(STATISTICS_CONCAT_NEW_REAL_COLUMN, realColumn.toString())
                .replace(STATISTICS_CREATE_TIME_PLACEHOLDER, createTime)
                .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, StringUtils.isNotBlank(realVariable) ? realVariable : getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName))
                .replace(STATISTICS_RULE_ID_PLACEHOLDER, ruleId + "")
                .replace(STATISTICS_RULE_VERSION_PLACEHOLDER, workFlowVersion + "");
            list.add(newValueDisplace);
            LOGGER.info("Succeed to get newValleDisplace. Vaule: {}", newValueDisplace);
        }

        //数值范围 新值替换
        if (numRangeNewValue) {
            String replacementFor = taskNumberRangeTemplate
                .replace(STATISTICS_CREATE_USER_PLACEHOLDER, user)
                .replace(STATISTICS_CREATE_TIME_PLACEHOLDER, createTime)
                .replace(SqlTemplateConverter.VARIABLE_NAME_PLACEHOLDER, StringUtils.isNotBlank(realVariable) ? realVariable : getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName))
                .replace(STATISTICS_RULE_ID_PLACEHOLDER, ruleId + "")
                .replace(STATISTICS_RULE_VERSION_PLACEHOLDER, workFlowVersion + "");
            list.add(replacementFor);
            LOGGER.info("Succeed to get replacementFor. Vaule: {}", replacementFor);
        }
    }

    /**
     * Get argument value from statistics step
     *
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
                if (! ruleVariable.getInputActionStep().equals(InputActionStepEnum.STATISTICS_ARG.getCode())) {
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
     *
     * @param count
     * @return
     */
    private String getVariable(Integer count) {
        return "tmp" + count;
    }

    /**
     * Get tmp variable name
     *
     * @param optPhase
     * @param partOfVariableName
     * @return
     */
    public String getVariableNameByRule(String optPhase, String partOfVariableName) {
        return optPhase + "Of" + partOfVariableName;
    }
}
