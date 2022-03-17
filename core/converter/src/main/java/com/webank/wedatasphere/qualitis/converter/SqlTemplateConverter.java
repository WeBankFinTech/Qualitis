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

package com.webank.wedatasphere.qualitis.converter;

import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.config.OptimizationConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.constant.RuleConstraintEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.translator.AbstractTranslator;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SQL Template Converter, can convert task into sql code
 * example：
 * val tmp1 = spark.sql("select * from bdp_test_ods_mask.asd where (fdgdfg) and (new_clum_mask is null)");
 * tmp1.write.saveAsTable("qualitishduser05_tmp_safe.mid_application_26_20190117094607_649214");
 * @author howeye
 */
@Component
public class SqlTemplateConverter extends AbstractTemplateConverter {
    @Autowired
    private AbstractTranslator abstractTranslator;
    @Autowired
    private OptimizationConfig optimizationConfig;

    /**
     * For 2149 template mid input meta special solve.
     */
    private static final String EN_LINE_PRIMARY_REPEAT = "Field Replace Null Concat";
    private static final String CN_LINE_PRIMARY_REPEAT = "替换空字段拼接";
    private static final String MESSAGE_LINE_PRIMARY_REPEAT = "{&FIELD_REPLACE_NULL_CONCAT}";

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(".*\\$\\{(.*)}.*");
    private static final Pattern AGGREGATE_FUNC_PATTERN = Pattern.compile("[a-zA-Z]+\\([0-9a-zA-Z_]+\\)");

    private static final String SAVE_MID_TABLE_NAME_PLACEHOLDER = "${TABLE_NAME}";
    private static final String SPARK_SQL_TEMPLATE_PLACEHOLDER = "${SQL}";
    public static final String VARIABLE_NAME_PLACEHOLDER = "${VARIABLE}";
    private static final String FILTER_PLACEHOLDER = "${filter}";
    private static final String FILTER_LEFT_PLACEHOLDER = "${filter_left}";
    private static final String FILTER_RIGHT_PLACEHOLDER = "${filter_right}";
    private static final Integer SINGLE_RULE = 1;
    private static final Integer CUSTOM_RULE = 2;
    private static final Integer MUL_SOURCE_RULE = 3;
    /**
     * Multi table solve.
     */
    private static final Long MUL_SOURCE_ACCURACY_TEMPLATE_ID = 17L;
    private static final Long MUL_SOURCE_COMMON_TEMPLATE_ID = 19L;

    /**
     * Dpm properties.
     */
    private static final String DPM = "dpm";
    /**
     * Cluster type end with this, it is new links version.
     */
    private static final String LINKIS_ONE_VERSION = "1.0";

    private static final String SPARK_SQL_TEMPLATE = "val " + VARIABLE_NAME_PLACEHOLDER + " = spark.sql(\"" + SPARK_SQL_TEMPLATE_PLACEHOLDER + "\");";
    private static final String SPARK_MYSQL_TEMPLATE = "val " + VARIABLE_NAME_PLACEHOLDER + " = spark.read.format(\"jdbc\").option(\"driver\",\"${JDBC_DRIVER}\").option(\"url\",\"jdbc:mysql://${MYSQL_IP}:${MYSQL_PORT}/\").option(\"user\",\"${MYSQL_USER}\").option(\"password\",\"${MYSQL_PASSWORD}\").option(\"query\",\"${SQL}\").load();";
    private static final String IF_EXIST = "if (spark.catalog.tableExists(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\")) {";
    private static final String ELSE_EXIST = "} else {";
    private static final String END_EXIST = "}";

    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
        + ".write.mode(\"append\").partitionBy(\"qualitis_partition_key\").format(\"hive\").saveAsTable(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\");";
    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
        + ".write.mode(\"overwrite\").insertInto(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\");";

    private static final String FPS_SOURCE = "val {TMP_SOURCE} = \"\"\"{\"path\":\"/apps-data/hadoop/{CLUSTER_TYPE}/{USER}/fps/{FPS_TALBE}{FPS_TYPE}\",\"pathType\":\"hdfs\",\"encoding\":\"utf-8\",\"fieldDelimiter\":\"\",\"hasHeader\":{FILE_HEADER},\"sheet\":\"{SHEET_NAME}\",\"quote\":\"\",\"escapeQuotes\":false}\"\"\"";
    private static final String FPS_DESTINATION = "val {TMP_DESTINATION} = \"\"\"{\"database\":\"{FPS_DB}\",\"tableName\":\"{FPS_TALBE}\",\"importData\":false,\"isPartition\":false,\"partition\":\"\",\"partitionValue\":\"\",\"isOverwrite\":true,\"columns\":{COLUMN_LIST}}\"\"\"";
    private static final String FPS_IMPORT = "com.webank.wedatasphere.linkis.engine.imexport.LoadData.loadDataToTable(spark,{TMP_SOURCE},{TMP_DESTINATION})";
    private static final String FPS_DROP_TABLE = "spark.sql(\"drop table {FPS_DB}.{FPS_TALBE}\")";
    private static final String FPS_TO_HIVE_WITH_HEADER = "val {DF} =spark.read.option(\"header\", \"true\").option(\"delimiter\", \"{DELIMITER}\").option(\"inferSchema\", \"true\").csv(\"{HDFS_PREFIX}{FPS_FILE_PATH}\")";
    private static final String FPS_FILE_PATH = "/apps-data/hadoop/{CLUSTER_TYPE}/{USER}/fps/";
    private static final String IMPORT_SCHEMA = "import org.apache.spark.sql.types._";
    private static final String CONSTRUCT_SCHEMA = "val {SCHEMA} = new StructType()";
    private static final String CONSTRUCT_FIELD = ".add(\"{FIELD_NAME}\", {FIELD_TYPE}, true)";
    private static final String FPS_TO_HIVE_WITH_SCHEMA = "val {DF} = spark.read.option(\"delimiter\", \"{DELIMITER}\").schema({SCHEMA}).csv(\"{HDFS_PREFIX}{FPS_FILE_PATH}\")";
    private static final String DF_REGISTER = "{DF}.registerTempTable(\"{FILE_NAME}\")";

    /**
     * Common static field.
     */
    private static final String AND = "and";

    private static final Map<String, String> FILE_TYPE_SUFFIX  = new HashMap<String, String>(4){{
        put(".txt","text");
        put(".csv", "csv");
        put(".xlsx", "excel");
        put(".xls", "excel");
    }};

    private static final Map<String, String> JDBC_DRIVER  = new HashMap<String, String>(4){{
        put("mysql","com.mysql.jdbc.Driver");
        put("tdsql", "com.mysql.jdbc.Driver");
        put("oracle", "oracle.jdbc.driver.OracleDriver");
        put("sqlserver", "com.microsoft.jdbc.sqlserver.SQLServerDriver");
    }};

    private static final Map<String, String> DATE_FORMAT  = new HashMap<String, String>(4){{
        put("1","yyyyMMdd");
        put("2", "yyyy-MM-dd");
        put("3", "yyyy.MM.dd");
        put("4", "yyyy/MM/dd");
    }};

    private static final Map<String, String> FIELD_TYPE = new HashMap<String, String>(12){{
        put("tinyint","ByteType");
        put("smallint", "ShortType");
        put("int","IntegerType");
        put("bigint", "LongType");
        put("double", "DoubleType");
        put("float", "FloatType");
        put("decimal", "DecimalType(38,24)");
        put("string", "StringType");
        put("char", "StringType");
        put("varchar", "StringType");
        put("timestamp", "TimestampType");
        put("date", "DateType");
    }};

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlTemplateConverter.class);

    /**
     * Convert task into scala code
     * @param dataQualityTask
     * @param date
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param clusterType
     * @param dataSourceMysqlConnect
     * @return
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public DataQualityJob convert(DataQualityTask dataQualityTask, Date date, String setFlag, Map<String, String> execParams, String runDate
        , String clusterType, Map<Long, Map> dataSourceMysqlConnect)
        throws ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException, IOException, UnExpectedRequestException {

        LOGGER.info("Start to convert template to actual code, task: " + dataQualityTask);
        if (null == dataQualityTask || dataQualityTask.getRuleTaskDetails().isEmpty()) {
            throw new DataQualityTaskException("Task can not be null or empty");
        }
        DataQualityJob job = new DataQualityJob();
        List<String> initSentence = abstractTranslator.getInitSentence();
        job.getJobCode().addAll(initSentence);
        LOGGER.info("Succeed to get init code. codes: " + initSentence);
        if (StringUtils.isNotBlank(setFlag)) {
            LOGGER.info("Start to solve with set flag. Spark set conf string: {}", setFlag);
            String[] setStrs = setFlag.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : setStrs) {
                job.getJobCode().add("spark.sql(\"set " + str + "\")");
            }
            LOGGER.info("Finish to solve with set flag.");
        }

        int count = 0;
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            count++;
            List<String> codes = generateSparkSqlByTask(ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), ruleTaskDetail.getMidTableName()
                , dataQualityTask.getCreateTime(), new StringBuffer(dataQualityTask.getPartition()), execParams, count, runDate, dataSourceMysqlConnect);
            job.getJobCode().addAll(codes);
            LOGGER.info("Succeed to convert rule into code. rule_id: {}, rul_name: {}, codes: {}", ruleTaskDetail.getRule().getId(), ruleTaskDetail.getRule().getName(), codes);
        }
        LOGGER.info("Succeed to convert all rule into actual scala code.");
        job.setTaskId(dataQualityTask.getTaskId());
        job.setStartupParam(dataQualityTask.getStartupParam());
        return job;
    }

    /**
     * Convert task into scala code
     * @param rule
     * @param date
     * @param applicationId
     * @param midTableName
     * @param createTime
     * @param partition
     * @param execParams
     * @param count
     * @param runDate
     * @param dataSourceMysqlConnect
     * @return
     * @throws ConvertException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> generateSparkSqlByTask(Rule rule, Date date, String applicationId, String midTableName, String createTime, StringBuffer partition
        , Map<String, String> execParams, int count, String runDate, Map<Long, Map> dataSourceMysqlConnect)
        throws ConvertException, RuleVariableNotSupportException, RuleVariableNotFoundException, UnExpectedRequestException {

        List<String> sqlList = new ArrayList<>();
        // Collect rule metric and build in save sentence sql.
        List<RuleMetric> ruleMetrics = rule.getAlarmConfigs().stream().map(AlarmConfig::getRuleMetric).distinct().collect(Collectors.toList());
        Map<String, Long> ruleMetricMap = new HashMap<>(ruleMetrics.size());
        if (CollectionUtils.isNotEmpty(ruleMetrics)) {
            LOGGER.info("Start to get rule metric for task result save. Rule metrics: {}", Arrays.toString(ruleMetrics.toArray()));
            for (RuleMetric ruleMetric : ruleMetrics) {
                if (ruleMetric != null) {
                    ruleMetricMap.put(ruleMetric.getName(), ruleMetric.getId());
                }
            }
            LOGGER.info("Finish to get rule metric for task result save.");
        }
        // Get SQL from template after remove '\n'
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n"," ");

        Map<String, String> filters = new HashMap<>(2);
        if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = customMidTableActionUpdate(rule, templateMidTableAction, date, execParams, partition, ruleMetricMap);
        } else if (MUL_SOURCE_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = multiMidTableActionUpdate(rule, templateMidTableAction, date, filters);
        }
        // Get input meta from template
        List<RuleVariable> inputMetaRuleVariables = rule.getRuleVariables().stream().filter(
                ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())).collect(Collectors.toList());

        // If partition is not specified, replace with filter in rule configuration.
        if (StringUtils.isBlank(partition.toString())) {
            templateMidTableAction = fillPartitionWithRuleConfiguration(partition, rule, templateMidTableAction, inputMetaRuleVariables);
        }
        // Get dbs and tables
        Map<String, String> dbTableMap = new HashMap<>(4);
        // Get mappings
        StringBuffer mappings = new StringBuffer();
        StringBuffer realFilter = new StringBuffer();
        // Get SQL From template and replace all replaceholders
        String midTableAction = replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realFilter, dbTableMap, mappings, date);

        Set<TemplateStatisticsInputMeta> templateStatisticsAction = rule.getTemplate().getStatisticAction();
        Map sourceConnect = new HashMap(8);
        Map targetConnect = new HashMap(8);
        if (dataSourceMysqlConnect != null && dataSourceMysqlConnect.size() > 0) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                Map connectParams = dataSourceMysqlConnect.get(ruleDataSource.getId());
                if (connectParams == null) {
                    continue;
                }
                if (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(0)) {
                    // If mysql sec, decrypt password and user name.
                    sourceConnect = dataSourceMysqlConnect.get(ruleDataSource.getId());
                }
                if (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(1)) {
                    // If mysql sec, decrypt password and user name.
                    targetConnect = dataSourceMysqlConnect.get(ruleDataSource.getId());
                }
            }
        }
        sqlList.add("val UUID = java.util.UUID.randomUUID.toString");
        // 跨表规则
        if (RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()) && dbTableMap.size() > 0) {
            // Import sql function.
            sqlList.addAll(getImportSql());
            // Generate UUID.
            // Transform original table.
            Set<String> columns = new HashSet<>();
            if (rule.getTemplate().getId().longValue() == MUL_SOURCE_ACCURACY_TEMPLATE_ID.longValue()) {
                // Get accuracy columns.
                columns = rule.getRuleDataSourceMappings().stream().map(RuleDataSourceMapping::getLeftColumnNames)
                    .map(column -> column.replace("tmp1.", "").replace("tmp2.", "")).collect(Collectors.toSet());
            }
            if (rule.getTemplate().getId().longValue() == MUL_SOURCE_COMMON_TEMPLATE_ID.longValue()) {
                sqlList.addAll(getCommonTransformSql(dbTableMap, mappings, count, partition.toString(), filters, sourceConnect, targetConnect));
            } else {
                sqlList.addAll(getSpecialTransformSql(dbTableMap, count, partition.toString(), filters, Strings.join(columns, ','), sourceConnect, targetConnect));
                if (optimizationConfig.getLightweightQuery()) {
                    count += 3;
                }
            }
            if (rule.getTemplate().getSaveMidTable()) {
                sqlList.addAll(getSaveMidTableSentenceSettings());
                sqlList.addAll(getSaveMidTableSentence(midTableName, count, runDate));
            }
        } else {
            // Generate select statement and save into hive database
            RuleDataSource ruleDataSource = rule.getRuleDataSources().stream().filter(dataSource -> dataSource.getDatasourceIndex() == null).iterator().next();
            Map connParams = dataSourceMysqlConnect.get(ruleDataSource.getId());
            if (connParams != null) {
                connParams = dataSourceMysqlConnect.get(ruleDataSource.getId());
            }

            sqlList.addAll(generateSparkSqlAndSaveSentence(midTableAction, midTableName, rule.getTemplate(), count, connParams, runDate));
            count ++;
        }

        // Generate statistics statement, and save into mysql
        List<RuleVariable> statisticsRuleVariables = rule.getRuleVariables().stream().filter(
            ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.STATISTICS_ARG.getCode())).collect(Collectors.toList());

        sqlList.addAll(saveStatisticAndSaveMySqlSentence(rule.getId(), ruleMetricMap, templateStatisticsAction, applicationId, statisticsRuleVariables
            , createTime, count, runDate));

        return sqlList;
    }

    private String customMidTableActionUpdate(Rule rule, String templateMidTableAction, Date date, Map<String, String> execParams,
        StringBuffer partition, Map<String, Long> ruleMetricMap) throws UnExpectedRequestException {
        if (StringUtils.isNotBlank(rule.getCsId())) {
            templateMidTableAction = templateMidTableAction.replace(RuleConstraintEnum.CUSTOM_DATABASE_PREFIS.getValue().concat(SpecCharEnum.PERIOD.getValue()), "");
        }
        if (StringUtils.isNotBlank(partition.toString())) {
            templateMidTableAction = templateMidTableAction.replace("${filter}", partition.toString());
        } else if (StringUtils.isNotBlank(rule.getWhereContent())){
            templateMidTableAction = templateMidTableAction.replace("${filter}", rule.getWhereContent());
        }
        for (String key : execParams.keySet()) {
            templateMidTableAction = templateMidTableAction.replace("${" + key + "}", execParams.get(key));
        }
        templateMidTableAction = DateExprReplaceUtil.replaceRunDate(date, templateMidTableAction);

        Set<String> ruleMetricNames = ruleMetricMap.keySet();
        for (String ruleMetricName : ruleMetricNames) {
            String cleanRuleMetricName = ruleMetricName.replace("-", "_");
            templateMidTableAction = templateMidTableAction.replace(ruleMetricName, cleanRuleMetricName);
        }

        return templateMidTableAction;
    }

    private String multiMidTableActionUpdate(Rule rule, String templateMidTableAction, Date date, Map<String, String> filters) throws UnExpectedRequestException {
        Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
        if (rule.getParentRule() != null) {
            ruleDataSources = new HashSet<>();
            Set<RuleDataSource> parentRuleDataSources = rule.getParentRule().getRuleDataSources();
            for (RuleDataSource ruleDataSource : parentRuleDataSources) {
                RuleDataSource tmp = new RuleDataSource(ruleDataSource);
                if (tmp.getDatasourceIndex() == 0) {
                    tmp.setDatasourceIndex(1);
                } else {
                    tmp.setDatasourceIndex(0);
                }
                ruleDataSources.add(tmp);
            }
        }
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (ruleDataSource.getDatasourceIndex().equals(0)) {
                String leftFilter = ruleDataSource.getFilter();
                leftFilter = DateExprReplaceUtil.replaceFilter(date, leftFilter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_LEFT_PLACEHOLDER, leftFilter);
                filters.put("source_table", leftFilter);
            } else {
                String rightFilter = ruleDataSource.getFilter();
                rightFilter = DateExprReplaceUtil.replaceFilter(date, rightFilter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_RIGHT_PLACEHOLDER, rightFilter);
                filters.put("target_table", rightFilter);
            }
        }
        return templateMidTableAction;
    }

    private String fillPartitionWithRuleConfiguration(StringBuffer partition, Rule rule, String templateMidTableAction, List<RuleVariable> inputMetaRuleVariables) {
        if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode())) {
            partition.append(new ArrayList<>(rule.getRuleDataSources()).get(0).getFilter());
        } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
            // Replace placeholder.
            if (StringUtils.isNotEmpty(rule.getWhereContent())) {
                partition.append(rule.getWhereContent());
            }
        } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            // Replace placeholder.
            partition.delete(0, partition.length());
            List<RuleVariable> filterVariable = inputMetaRuleVariables.stream().filter(
                r -> r.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())
            ).collect(Collectors.toList());
            if (!filterVariable.isEmpty()) {
                partition.append(filterVariable.get(0).getValue());
            }
        }
        return templateMidTableAction;
    }

    private List<String> getCommonTransformSql(Map<String, String> dbTableMap, StringBuffer mappings, int count, String filter, Map<String, String> filters
        , Map sourceConnect, Map targetConnect) {
        // Solve partition, value, hash value
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();

        sourceSql.append("select *").append(" from ")
            .append(dbTableMap.get("source_db")).append(dbTableMap.get("source_table"))
            .append(" where ").append(filters.get("source_table"));
        targetSql.append("select *").append(" from ")
            .append(dbTableMap.get("target_db")).append(dbTableMap.get("target_table"))
            .append(" where ").append(filters.get("target_table"));
        if (sourceConnect != null && sourceConnect.size() > 0) {
            String host = (String) sourceConnect.get("host");
            String port = (String) sourceConnect.get("port");
            String user = (String) sourceConnect.get("username");
            String pwd = (String) sourceConnect.get("password");
            String dataType = (String) sourceConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDF")
            .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
            .replace("${MYSQL_IP}", host)
            .replace("${MYSQL_PORT}", port)
            .replace("${MYSQL_USER}", user)
            .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDF").replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
        }
        if (targetConnect != null && targetConnect.size() > 0) {
            String host = (String) targetConnect.get("host");
            String port = (String) targetConnect.get("port");
            String user = (String) targetConnect.get("username");
            String pwd = (String) targetConnect.get("password");
            String dataType = (String) sourceConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDF_2")
                .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                .replace("${MYSQL_IP}", host)
                .replace("${MYSQL_PORT}", port)
                .replace("${MYSQL_USER}", user)
                .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDF_2").replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
        }

        transformSql.add("originalDF.registerTempTable(\"tmp1\")");
        transformSql.add("originalDF_2.registerTempTable(\"tmp2\")");
        String commonJoin = "SELECT tmp1.* FROM tmp1 LEFT JOIN tmp2 ON " + mappings.toString() + " WHERE " + filter;
        String variableName1 = getVariableName(count);
        String joinSql = "val " + variableName1 + " = spark.sql(\"" + commonJoin + "\")";

        transformSql.add(joinSql);
        return transformSql;
    }

    private List<String> getSpecialTransformSql(Map<String, String> dbTableMap, int count, String filter, Map<String, String> filters
        , String columns, Map sourceConnect, Map targetConnect) {
        // Solve partition fields.
        List<String> partitionFields = new ArrayList<>();
        if (StringUtils.isNotBlank(filter)) {
            filter = filter.toLowerCase().trim();
            if (filter.contains(AND)) {
                List<String> subPartition = Arrays.asList(filter.split(AND));
                for (String sub : subPartition) {
                    String partitionField = sub.trim().substring(0, sub.indexOf("="));
                    partitionFields.add(partitionField);
                }
            } else {
                String partitionField = filter.substring(0, filter.indexOf("="));
                partitionFields.add(partitionField);
            }
        }

        // Solve partition, value, hash value
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();
        if (CollectionUtils.isNotEmpty(partitionFields)) {
            if (StringUtils.isNotBlank(columns)) {
                sourceSql.append("select ").append(columns);
                targetSql.append("select ").append(columns);
            } else {
                sourceSql.append("select *");
                targetSql.append("select *");
            }
            sourceSql.append(" from ").append(dbTableMap.get("source_db")).append(dbTableMap.get("source_table")).append(" where ").append(filter);
            targetSql.append(" from ").append(dbTableMap.get("target_db")).append(dbTableMap.get("target_table")).append(" where ").append(filter);
        } else {
            if (StringUtils.isNotBlank(columns)) {
                sourceSql.append("select ").append(columns);
                targetSql.append("select ").append(columns);
            } else {
                sourceSql.append("select *");
                targetSql.append("select *");
            }
            sourceSql.append(" from ").append(dbTableMap.get("source_db")).append(dbTableMap.get("source_table")).append(" where ").append(filters.get("source_table"));
            targetSql.append(" from ").append(dbTableMap.get("target_db")).append(dbTableMap.get("target_table")).append(" where ").append(filters.get("target_table"));
        }
        if (sourceConnect != null && sourceConnect.size() > 0) {
            String host = (String) sourceConnect.get("host");
            String port = (String) sourceConnect.get("port");
            String user = (String) sourceConnect.get("username");
            String pwd = (String) sourceConnect.get("password");
            String dataType = (String) sourceConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDF")
                .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                .replace("${MYSQL_IP}", host)
                .replace("${MYSQL_PORT}", port)
                .replace("${MYSQL_USER}", user)
                .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDF").replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
        }
        if (targetConnect != null && targetConnect.size() > 0) {
            String host = (String) targetConnect.get("host");
            String port = (String) targetConnect.get("port");
            String user = (String) targetConnect.get("username");
            String pwd = (String) targetConnect.get("password");
            String dataType = (String) targetConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDF_2")
                .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                .replace("${MYSQL_IP}", host)
                .replace("${MYSQL_PORT}", port)
                .replace("${MYSQL_USER}", user)
                .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDF_2").replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
        }
        // Full line to MD5 with dataframe api transformation.
        fuleLineToHashLine(transformSql, partitionFields);

        String variableName1 = getVariableName(count);
        if (optimizationConfig.getLightweightQuery()) {
            count ++;
            String variableName2 = getVariableName(count);
            count ++;
            String variableName3 = getVariableName(count);
            count ++;
            String variableName4 = getVariableName(count);

            String joinSql = "val " + variableName1 + " = spark.sql(\"SELECT qualitis_tmp1.qualitis_full_line_hash_value as left_full_hash_line, qualitis_tmp1.qualitis_mul_db_accuracy_num as left_full_line_num, qualitis_tmp2.qualitis_full_line_hash_value as right_full_hash_line, qualitis_tmp2.qualitis_mul_db_accuracy_num as right_full_line_num FROM (SELECT qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM md5_table_3 WHERE true group by qualitis_full_line_hash_value) qualitis_tmp1 FULL OUTER JOIN (SELECT qualitis_full_line_hash_value, qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM md5_table_4 WHERE true group by qualitis_full_line_hash_value) qualitis_tmp2 ON (qualitis_tmp1.qualitis_full_line_hash_value = qualitis_tmp2.qualitis_full_line_hash_value AND qualitis_tmp1.qualitis_mul_db_accuracy_num = qualitis_tmp2.qualitis_mul_db_accuracy_num) WHERE ( NOT (qualitis_tmp1.qualitis_full_line_hash_value is null AND qualitis_tmp1.qualitis_mul_db_accuracy_num is null) AND (qualitis_tmp2.qualitis_full_line_hash_value is null AND qualitis_tmp2.qualitis_mul_db_accuracy_num is null)) OR ( NOT (qualitis_tmp2.qualitis_full_line_hash_value is null AND qualitis_tmp2.qualitis_mul_db_accuracy_num is null) AND (qualitis_tmp1.qualitis_full_line_hash_value is null AND qualitis_tmp1.qualitis_mul_db_accuracy_num is null))\")";
            transformSql.add(joinSql);
            transformSql.add(variableName1 + ".registerTempTable(\"md5_table_5\")");

            String joinSqlWithLeft = "val " + variableName2 + " = spark.sql(\"\"\"SELECT \"left\" as source, md5_table_3.qualitis_full_line_value as full_line, md5_table_5.left_full_line_num FROM md5_table_3 full outer join md5_table_5 on md5_table_3.qualitis_full_line_hash_value = md5_table_5.left_full_hash_line where md5_table_3.qualitis_full_line_hash_value is not null and md5_table_5.left_full_hash_line is not null\"\"\")";
            String joinSqlWithRight = "val " + variableName3 + " = spark.sql(\"\"\"SELECT \"right\" as source, md5_table_4.qualitis_full_line_value as full_line, md5_table_5.right_full_line_num FROM md5_table_4 full outer join md5_table_5 on md5_table_4.qualitis_full_line_hash_value = md5_table_5.right_full_hash_line where md5_table_4.qualitis_full_line_hash_value is not null and md5_table_5.right_full_hash_line is not null\"\"\")";

            transformSql.add(joinSqlWithLeft);
            transformSql.add(joinSqlWithRight);
            transformSql.add("val " + variableName4 + "=" + variableName2 + ".union(" + variableName3 + ")");
        } else {
            String joinSql = "val " + variableName1 + " = spark.sql(\"SELECT qualitis_tmp1.qualitis_full_line_value as left_full_line, qualitis_tmp1.qualitis_mul_db_accuracy_num as left_full_line_num, qualitis_tmp2.qualitis_full_line_value as right_full_line, qualitis_tmp2.qualitis_mul_db_accuracy_num as right_full_line_num FROM (SELECT qualitis_full_line_value, qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM md5_table_3 WHERE true group by qualitis_full_line_value, qualitis_full_line_hash_value) qualitis_tmp1 FULL OUTER JOIN (SELECT qualitis_full_line_value, qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM md5_table_4 WHERE true group by qualitis_full_line_value, qualitis_full_line_hash_value) qualitis_tmp2 ON (qualitis_tmp1.qualitis_full_line_hash_value = qualitis_tmp2.qualitis_full_line_hash_value AND qualitis_tmp1.qualitis_mul_db_accuracy_num = qualitis_tmp2.qualitis_mul_db_accuracy_num) WHERE ( NOT (qualitis_tmp1.qualitis_full_line_hash_value is null AND qualitis_tmp1.qualitis_mul_db_accuracy_num is null) AND (qualitis_tmp2.qualitis_full_line_hash_value is null AND qualitis_tmp2.qualitis_mul_db_accuracy_num is null)) OR ( NOT (qualitis_tmp2.qualitis_full_line_hash_value is null AND qualitis_tmp2.qualitis_mul_db_accuracy_num is null) AND (qualitis_tmp1.qualitis_full_line_hash_value is null AND qualitis_tmp1.qualitis_mul_db_accuracy_num is null))\")";

            transformSql.add(joinSql);
        }

        return transformSql;
    }

    private void fuleLineToHashLine(List<String> transformSql, List<String> partitionFields) {
        transformSql.add("val fillNullDF = originalDF.na.fill(UUID)");
        transformSql.add("val qualitis_names = fillNullDF.schema.fieldNames");
        transformSql.add("val fileNullWithFullLineWithHashDF = fillNullDF.withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
        transformSql.add("val qualitis_names_buffer = qualitis_names.toBuffer");

        transformSql.add("val fillNullDF_2 = originalDF_2.na.fill(UUID)");
        transformSql.add("val qualitis_names_2 = fillNullDF_2.schema.fieldNames");
        transformSql.add("val fileNullWithFullLineWithHashDF_2 = fillNullDF_2.withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
        transformSql.add("val qualitis_names_buffer_2 = qualitis_names_2.toBuffer");

        for (String partitionField : partitionFields) {
            transformSql.add("qualitis_names_buffer -= \"" + partitionField + "\"");
            transformSql.add("qualitis_names_buffer_2 -= \"" + partitionField + "\"");
        }
        transformSql.add("val finalDF = fileNullWithFullLineWithHashDF.drop(qualitis_names_buffer:_*)");
        transformSql.add("val finalDF_2 = fileNullWithFullLineWithHashDF_2.drop(qualitis_names_buffer_2:_*)");

        transformSql.add("finalDF.registerTempTable(\"md5_table_3\")");
        transformSql.add("finalDF_2.registerTempTable(\"md5_table_4\")");
    }

    private List<String> getImportSql() {
        List<String> imports = new ArrayList<>();
        imports.add("import org.apache.spark.sql.types._");
        imports.add("import org.apache.spark.sql.functions._");
        return imports;
    }

    private List<String> saveStatisticAndSaveMySqlSentence(Long ruleId, Map<String, Long> ruleMetricIds,
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables,
        String createTime, Integer count, String runDate) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        return abstractTranslator.persistenceTranslate(ruleId, ruleMetricIds, templateStatisticsInputMetas, applicationId, ruleVariables, createTime
            , count, runDate);
    }

    /**
     * Generate scala code of select statement and save into hive database
     * @param sql
     * @param saveTableName
     * @param template
     * @param count
     * @param connParams
     * @param runDate
     * @return
     */
    private List<String> generateSparkSqlAndSaveSentence(String sql, String saveTableName, Template template, Integer count, Map connParams, String runDate) {
        List<String> sparkSqlList = new ArrayList<>();
        String sparkSqlSentence;
        if (connParams == null) {
            sparkSqlSentence = getSparkSqlSentence(sql, count);
        } else {
            sparkSqlSentence = getSparkSqlSententceWithMysqlConnParams(sql, count, connParams);
        }

        sparkSqlList.add(sparkSqlSentence);
        List<String> midTableInputNames = template.getTemplateMidTableInputMetas().stream().map(TemplateMidTableInputMeta::getName).collect(Collectors.toList());

        boolean linePrimaryRepeat = CollectionUtils.isNotEmpty(midTableInputNames) && (midTableInputNames.contains(EN_LINE_PRIMARY_REPEAT) || midTableInputNames.contains(CN_LINE_PRIMARY_REPEAT) || midTableInputNames.contains(MESSAGE_LINE_PRIMARY_REPEAT));
        if (linePrimaryRepeat) {
            sparkSqlList.add("val fillNullDF_" + count + " = " + getVariableName(count) + ".na.fill(UUID)");
            sparkSqlList.add("val fileNullWithFullLineWithHashDF_" + count + " = fillNullDF_" + count + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"md5\", md5(to_json(struct($\"*\"))))");
            sparkSqlList.add("fileNullWithFullLineWithHashDF_" + count + ".registerTempTable(\"tmp_table_" + count + "\")");
            sparkSqlList.add("val " + getVariableName(count) + " = spark.sql(\"select md5, count(1) as md5_count from tmp_table_" + count + " group by md5 having count(*) > 1\")");
        }

        LOGGER.info("Succeed to generate spark sql. sentence: {}", sparkSqlSentence);
        // Fix bug in the workflow between widget node and qualitis node.
        String variableFormer = getVariableName(count);
        count ++;
        String variableLatter = getVariableName(count);
        formatSchema(sparkSqlList, variableFormer, variableLatter);
        // Fix bug end.
        if (template.getSaveMidTable()) {
            sparkSqlList.addAll(getSaveMidTableSentenceSettings());
            sparkSqlList.addAll(getSaveMidTableSentence(saveTableName, count, runDate));
            LOGGER.info("Succeed to generate spark sql. sentence.");
        }
        return sparkSqlList;
    }

    private String getSparkSqlSententceWithMysqlConnParams(String sql, Integer count, Map connParams) {
        sql = sql.replace("\"", "\\\"");
        String host = (String) connParams.get("host");
        String port = (String) connParams.get("port");
        String user = (String) connParams.get("username");
        String pwd = (String) connParams.get("password");
        String dataType = (String) connParams.get("dataType");
        String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql)
            .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
            .replace("${MYSQL_IP}", host)
            .replace("${MYSQL_PORT}", port)
            .replace("${MYSQL_USER}", user)
            .replace("${MYSQL_PASSWORD}", pwd);
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count));
    }

    private void formatSchema(List<String> sparkSqlList, String variableFormer, String variableLatter) {
        String str1 = "val schemas = " + variableFormer + ".schema.fields.map(f => f.name).toList";
        String str2 = "val newSchemas = schemas.map(s => s.replaceAll(\"[()]\", \"\")).toList";
        String str3 = "val " + variableLatter + " = " + variableFormer + ".toDF(newSchemas: _*)";
        sparkSqlList.add(str1);
        sparkSqlList.add(str2);
        sparkSqlList.add(str3);
    }

    private List<String> getSaveMidTableSentenceSettings() {
        List<String> settings = new ArrayList<>();
        settings.add("spark.sqlContext.setConf(\"hive.exec.dynamic.partition\", \"true\")");
        settings.add("spark.sqlContext.setConf(\"hive.exec.dynamic.partition.mode\", \"nonstrict\")");

        settings.add("spark.conf.set(\"spark.sql.sources.partitionOverwriteMode\",\"dynamic\")");
        return settings;
    }

    private List<String> getSaveMidTableSentence(String saveMidTableName, Integer count, String runDate) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        List<String> saveSqls = new ArrayList<>();
        saveSqls.add(IF_EXIST.replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName));
        saveSqls.add(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION.replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count)));
        saveSqls.add(ELSE_EXIST);
        saveSqls.add(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE.replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count)));
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private String getSparkSqlSentence(String sql, Integer count) {
        sql = sql.replace("\"", "\\\"");
        String str = SPARK_SQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql);
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count));
    }

    /**
     * Replace all placeholder of template sql
     * @param template
     * @param variables
     * @param filter
     * @param realFilter
     * @param dbTableMap for pick up source db.table & target db.table
     * @param mappings
     * @param date
     * @return
     * @throws ConvertException
     */
    private String replaceVariable(String template, List<RuleVariable> variables, String filter, StringBuffer realFilter, Map<String, String> dbTableMap
        , StringBuffer mappings, Date date)
        throws ConvertException, UnExpectedRequestException {
        String sqlAction = template;
        if (StringUtils.isNotBlank(filter)) {
            String tmpfilter = DateExprReplaceUtil.replaceFilter(date, filter);
            sqlAction = sqlAction.replace(FILTER_PLACEHOLDER, tmpfilter);
            realFilter.append(tmpfilter);
            LOGGER.info("Succeed to replace {} into {}", FILTER_PLACEHOLDER, tmpfilter);
        } else {
            realFilter.append("true");
        }
        for (RuleVariable ruleVariable : variables) {
            String midInputMetaPlaceHolder = ruleVariable.getTemplateMidTableInputMeta().getPlaceholder();
            String placeHolder = "\\$\\{" + midInputMetaPlaceHolder + "}";
            // GeT source db and table, target db and table.
            if ("source_db".equals(midInputMetaPlaceHolder)) {
                if (StringUtils.isNotBlank(ruleVariable.getValue())) {
                    dbTableMap.put("source_db", ruleVariable.getValue() + ".");
                } else {
                    dbTableMap.put("source_db", "");
                }
            } else if ("source_table".equals(midInputMetaPlaceHolder)) {
                dbTableMap.put("source_table", ruleVariable.getValue());
            } else if ("target_table".equals(midInputMetaPlaceHolder)) {
                dbTableMap.put("target_table", ruleVariable.getValue());
            } else if ("target_db".equals(midInputMetaPlaceHolder)) {
                if (StringUtils.isNotBlank(ruleVariable.getValue())) {
                    dbTableMap.put("target_db", ruleVariable.getValue() + ".");
                } else {
                    dbTableMap.put("target_db", "");
                }
            } else if ("mapping_argument".equals(midInputMetaPlaceHolder)) {
                mappings.append(ruleVariable.getValue());
            }
            // Fix issue of wedget node in the front.
            if ("\\$\\{field}".equals(placeHolder)) {
                Matcher matcher = AGGREGATE_FUNC_PATTERN.matcher(ruleVariable.getValue());
                while(matcher.find()) {
                    String[] funcs = matcher.group().split("\n");
                    for (String func : funcs) {
                        ruleVariable.setValue(ruleVariable.getValue().replace(func, "`" + func + "`"));
                    }
                }
            }
            // Fix replacement issue that db is null when running workflow.
            if (ruleVariable.getValue() == null || "".equals(ruleVariable.getValue())) {
                sqlAction = sqlAction.replaceAll(placeHolder + ".", "");
            } else {
                sqlAction = sqlAction.replaceAll(placeHolder, ruleVariable.getValue());
            }

            LOGGER.info("Succeed to replace {} into {}", placeHolder, ruleVariable.getValue());
        }
        if (PLACEHOLDER_PATTERN.matcher(sqlAction).matches()) {
            throw new ConvertException("Unable to convert SQL, replacing placeholders failed, still having placeholder.");
        }

        return sqlAction;
    }

    /**
     * Get tmp variable name
     * @param count
     * @return
     */
    public String getVariableName(Integer count) {
        return "tmp" + count;
    }
}
