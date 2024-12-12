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

import com.webank.wedatasphere.qualitis.EngineTypeEnum;
import com.webank.wedatasphere.qualitis.LocalConfig;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.FpsColumnInfo;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.config.DpmConfig;
import com.webank.wedatasphere.qualitis.config.FpsConfig;
import com.webank.wedatasphere.qualitis.config.TaskDataSourceConfig;
import com.webank.wedatasphere.qualitis.constant.OptTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.constant.RuleConstraintEnum;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.rule.constant.*;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.translator.AbstractTranslator;
import com.webank.wedatasphere.qualitis.util.CryptoUtils;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.QualitisCollectionUtils;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL Template Converter, can convert task into sql code
 * example：
 * val tmp1 = spark.sql("select * from bdp_test_ods_mask.asd where (fdgdfg) and (new_clum_mask is null)");
 * tmp1.write.saveAsTable("qualitishduser05_tmp_safe.mid_application_26_20190117094607_649214");
 *
 * @author howeye
 */
@Component
public class SqlTemplateConverter extends AbstractTemplateConverter {
    @Autowired
    private TaskDataSourceConfig taskDataSourceConfig;
    @Autowired
    private AbstractTranslator abstractTranslator;
    @Autowired
    private LocalConfig localConfig;
    @Autowired
    private DpmConfig dpmConfig;
    @Autowired
    private FpsConfig fpsConfig;

    @Autowired
    private DataStandardClient dataStandardClient;
    @Value("${linkis.sql.communalTableName:common_table}")
    private String commonTableName;

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
    private static final Long MUL_SOURCE_FULL_TEMPLATE_ID = 20L;
    /**
     * Dpm properties.
     */
    private static final String DPM = "dpm";

    private static final String SPARK_SQL_TEMPLATE = "var " + VARIABLE_NAME_PLACEHOLDER + " = spark.sql(\"" + SPARK_SQL_TEMPLATE_PLACEHOLDER + "\")";
    private static final String SPARK_MYSQL_TEMPLATE = "val " + VARIABLE_NAME_PLACEHOLDER + " = spark.read.format(\"jdbc\").option(\"driver\",\"${JDBC_DRIVER}\").option(\"url\",\"jdbc:mysql://${MYSQL_IP}:${MYSQL_PORT}/\").option(\"user\",\"${MYSQL_USER}\").option(\"password\",\"${MYSQL_PASSWORD}\").option(\"query\",\"${SQL}\").load()";
    private static final String IF_EXIST = "if (spark.catalog.tableExists(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\")) {";
    private static final String ELSE_EXIST = "} else {";
    private static final String END_EXIST = "}";

    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
            + ".write.mode(\"append\").partitionBy(\"qualitis_partition_key\").format(\"hive\").saveAsTable(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\")";
    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
            + ".write.mode(\"overwrite\").insertInto(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\")";

    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE_WITH_ENV = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
            + ".withColumn(\"qualitis_partition_key_env\", lit(\"${QUALITIS_PARTITION_KEY_ENV}\"))"
            + ".write.mode(\"append\").partitionBy(\"qualitis_partition_key\", \"qualitis_partition_key_env\").format(\"hive\").saveAsTable(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\");";
    private static final String SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION_WITH_ENV = VARIABLE_NAME_PLACEHOLDER + ".withColumn(\"qualitis_partition_key\", lit(\"${QUALITIS_PARTITION_KEY}\"))"
            + ".withColumn(\"qualitis_partition_key_env\", lit(\"${QUALITIS_PARTITION_KEY_ENV}\"))"
            + ".write.mode(\"overwrite\").insertInto(\"" + SAVE_MID_TABLE_NAME_PLACEHOLDER + "\")";

    private static final String FPS_SOURCE = "val {TMP_SOURCE} = \"\"\"{\"path\":\"/apps-data/{USER}/fps{DATA_NOW}/{FPS_TALBE}{FPS_TYPE}\",\"pathType\":\"hdfs\",\"encoding\":\"utf-8\",\"fieldDelimiter\":\"\",\"hasHeader\":{FILE_HEADER},\"sheet\":\"{SHEET_NAME}\",\"quote\":\"\",\"escapeQuotes\":false}\"\"\"";
    private static final String FPS_DESTINATION = "val {TMP_DESTINATION} = \"\"\"{\"database\":\"{FPS_DB}\",\"tableName\":\"{FPS_TALBE}\",\"importData\":false,\"isPartition\":false,\"partition\":\"\",\"partitionValue\":\"\",\"isOverwrite\":true,\"columns\":{COLUMN_LIST}}\"\"\"";
    private static final String FPS_IMPORT_PREFIX = "org.apache.linkis.engineplugin.spark.imexport.LoadData.loadDataToTable";
    private static final String FPS_IMPORT = FPS_IMPORT_PREFIX + "(spark,{TMP_SOURCE},{TMP_DESTINATION})";
    private static final String FPS_DROP_TABLE = "spark.sql(\"drop table {FPS_DB}.{FPS_TALBE}\")";
    private static final String FPS_TO_HIVE_WITH_HEADER = "val {DF} =spark.read.option(\"header\", \"true\").option(\"delimiter\", \"{DELIMITER}\").option(\"inferSchema\", \"true\").csv(\"{HDFS_PREFIX}{FPS_FILE_PATH}\")";
    private static final String FPS_FILE_PATH = "/apps-data/{USER}/fps{DATA_NOW}/";
    private static final String IMPORT_SCHEMA = "import org.apache.spark.sql.types._";
    private static final String CONSTRUCT_SCHEMA = "val {SCHEMA} = new StructType()";
    private static final String CONSTRUCT_FIELD = ".add(\"{FIELD_NAME}\", {FIELD_TYPE}, true)";
    private static final String FPS_TO_HIVE_WITH_SCHEMA = "val {DF} = spark.read.option(\"delimiter\", \"{DELIMITER}\").schema({SCHEMA}).csv(\"{HDFS_PREFIX}{FPS_FILE_PATH}\")";
    private static final String DF_REGISTER = "{DF}.registerTempTable(\"{FILE_NAME}\")";

    private static final Map<String, String> FILE_TYPE_SUFFIX = new HashMap<String, String>(4);

    private static final Map<String, String> JDBC_DRIVER = new HashMap<String, String>(4);

    private static final Map<String, String> DATE_FORMAT = new HashMap<String, String>(4);

    private static final Map<String, String> FIELD_TYPE = new HashMap<String, String>(12);

    static {
        FILE_TYPE_SUFFIX.put(".txt", "text");
        FILE_TYPE_SUFFIX.put(".csv", "csv");
        FILE_TYPE_SUFFIX.put(".xlsx", "excel");
        FILE_TYPE_SUFFIX.put(".xls", "excel");

        JDBC_DRIVER.put("mysql", "com.mysql.jdbc.Driver");
        JDBC_DRIVER.put("tdsql", "com.mysql.jdbc.Driver");
        JDBC_DRIVER.put("oracle", "oracle.jdbc.driver.OracleDriver");
        JDBC_DRIVER.put("sqlserver", "com.microsoft.jdbc.sqlserver.SQLServerDriver");

        DATE_FORMAT.put("1", "yyyyMMdd");
        DATE_FORMAT.put("2", "yyyy-MM-dd");
        DATE_FORMAT.put("3", "yyyy.MM.dd");
        DATE_FORMAT.put("4", "yyyy/MM/dd");

        FIELD_TYPE.put("tinyint", "ByteType");
        FIELD_TYPE.put("smallint", "ShortType");
        FIELD_TYPE.put("int", "IntegerType");
        FIELD_TYPE.put("bigint", "LongType");
        FIELD_TYPE.put("double", "DoubleType");
        FIELD_TYPE.put("float", "FloatType");
        FIELD_TYPE.put("decimal", "DecimalType(38,24)");
        FIELD_TYPE.put("string", "StringType");
        FIELD_TYPE.put("char", "StringType");
        FIELD_TYPE.put("varchar", "StringType");
        FIELD_TYPE.put("timestamp", "TimestampType");
        FIELD_TYPE.put("date", "DateType");
        FIELD_TYPE.put("boolean", "BooleanType");

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlTemplateConverter.class);

    /**
     * Convert task into scala code
     *
     * @param dataQualityTask
     * @param date
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param clusterType
     * @param dataSourceMysqlConnect
     * @param user
     * @param leftCols
     * @param rightCols
     * @param complexCols
     * @param createUser
     * @return
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public DataQualityJob convert(DataQualityTask dataQualityTask, Date date, String setFlag, Map<String, String> execParams, String runDate
            , String clusterType, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user, List<String> leftCols, List<String> rightCols, List<String> complexCols, String createUser) throws Exception {

        boolean withSpark = CollectionUtils.isNotEmpty(complexCols) && Boolean.FALSE.equals(taskDataSourceConfig.getHiveSortUdfOpen());
        LOGGER.info("Start to convert template to actual code, task: " + dataQualityTask);
        if (null == dataQualityTask || dataQualityTask.getRuleTaskDetails().isEmpty()) {
            throw new DataQualityTaskException("Task can not be null or empty");
        }
        DataQualityJob job = new DataQualityJob();

        if (StringUtils.isNotBlank(setFlag)) {
            LOGGER.info("Start to solve with set flag. Spark set conf string: {}", setFlag);
            String[] setStrs = setFlag.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : setStrs) {
                job.getJobCode().add("spark.sql(\"set " + str + "\")");
            }
            LOGGER.info("Finish to solve with set flag.");
        }
        String engineType = EngineTypeEnum.DEFAULT_ENGINE.getMessage();
        String startupParam = dataQualityTask.getStartupParam();
        String queueName = "";
        boolean engineReUse = true;
        boolean midTableReUse = true;
        boolean unionAllForSaveResult = false;
        if (StringUtils.isNotBlank(startupParam)) {
            String[] startupParams = startupParam.split(SpecCharEnum.DIVIDER.getValue());

            for (String param : startupParams) {
                if (StringUtils.isEmpty(param)) {
                    continue;
                }
                String[] paramStrs = param.split("=");
                if (paramStrs.length < 2) {
                    continue;
                }
                String key = paramStrs[0];
                String value = paramStrs[1];
                if ("engine_reuse".equals(key)) {
                    if ("true".equals(value)) {
                        engineReUse = true;
                        startupParam = startupParam.replace("engine_reuse=true", "");
                    } else {
                        engineReUse = false;
                        startupParam = startupParam.replace("engine_reuse=false", "");
                    }
                }
                if ("mid_table_reuse".equals(key)) {
                    if ("true".equals(value)) {
                        midTableReUse = true;
                        startupParam = startupParam.replace("mid_table_reuse=true", "");
                    } else {
                        midTableReUse = false;
                        startupParam = startupParam.replace("mid_table_reuse=false", "");
                    }
                }
                if ("union_all_save".equals(key)) {
                    if ("true".equals(value)) {
                        unionAllForSaveResult = true;
                        startupParam = startupParam.replace("union_all_save=true", "");
                    } else {
                        unionAllForSaveResult = false;
                        startupParam = startupParam.replace("union_all_save=false", "");
                    }
                }

                if ("qualitis.linkis.engineType".equals(key) && !EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(value)) {
                    engineType = value;
                }

                if ("wds.linkis.rm.yarnqueue".equals(key)) {
                    queueName = value;
                }
            }
        }
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_ENGINE_REUSE) && Boolean.FALSE.equals(Boolean.parseBoolean(execParams.get(QualitisConstants.QUALITIS_ENGINE_REUSE)))) {
            engineReUse = false;
        }
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_MID_TABLE_REUSE) && Boolean.FALSE.equals(Boolean.parseBoolean(execParams.get(QualitisConstants.QUALITIS_MID_TABLE_REUSE)))) {
            midTableReUse = false;
        }
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_UNION_ALL_SAVE) && Boolean.TRUE.equals(Boolean.parseBoolean(execParams.get(QualitisConstants.QUALITIS_UNION_ALL_SAVE)))) {
            unionAllForSaveResult = true;
        }
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_ENGINE_TYPE) && !EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(execParams.get(QualitisConstants.QUALITIS_ENGINE_TYPE))) {
            engineType = EngineTypeEnum.SPARK_ENGINE.getMessage();
        }
        List<String> initSentence = abstractTranslator.getInitSentence();
        job.getJobCode().addAll(initSentence);

        List<String> envNames = new ArrayList<>();
        boolean shareConnect = CollectionUtils.isNotEmpty(dataQualityTask.getConnectShare());
        List<String> communalSentence = getCommunalSentence(dataQualityTask, envNames);
        job.getJobCode().addAll(communalSentence);

        int count = 0;
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            if (Boolean.TRUE.equals(ruleTaskDetail.getRule().getUnionAll())) {
                unionAllForSaveResult = true;
            }
            // Get current rule left cols and right cols.
            List<String> currentRuleLeftCols = new ArrayList<>();
            List<String> currentRuleRightCols = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(leftCols) && CollectionUtils.isNotEmpty(rightCols)) {
                currentRuleLeftCols = leftCols.stream().filter(col -> col.startsWith(ruleTaskDetail.getRule().getId() + SpecCharEnum.MINUS.getValue())).map(col -> col.replace(ruleTaskDetail.getRule().getId() + SpecCharEnum.MINUS.getValue(), "")).collect(
                        Collectors.toList());
                currentRuleRightCols = leftCols.stream().filter(col -> col.startsWith(ruleTaskDetail.getRule().getId() + SpecCharEnum.MINUS.getValue())).map(col -> col.replace(ruleTaskDetail.getRule().getId() + SpecCharEnum.MINUS.getValue(), "")).collect(
                        Collectors.toList());
            }
            count += 100;
            // Fps register dataframe.
            List<String> fpsCodes = generateTempHiveTable(ruleTaskDetail.getRule(), dataQualityTask.getUser());

            job.getJobCode().addAll(fpsCodes);
            // Handle hive engine task depend on startup param(qualitis.linkis.engineType=shell,spark(default))
            if (CollectionUtils.isEmpty(fpsCodes) && !withSpark && taskDataSourceConfig.getMysqlsecOpen() && EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(engineType) && MUL_SOURCE_FULL_TEMPLATE_ID.equals(ruleTaskDetail.getRule().getTemplate().getId()) && CollectionUtils.isEmpty(dataSourceMysqlConnect.keySet())) {
                job.getJobCode().clear();
                // Hql
                List<String> codes = generateShellSqlByTask(ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), dataQualityTask.getCreateTime(), new StringBuilder(dataQualityTask.getPartition()), count, runDate, currentRuleLeftCols, currentRuleRightCols, complexCols, queueName, createUser);
                job.setEngineType(EngineTypeEnum.DEFAULT_ENGINE.getMessage());
                job.getJobCode().addAll(codes);
                continue;
            }

            List<String> codes = generateSparkSqlByTask(job, ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), ruleTaskDetail.getMidTableName()
                , dataQualityTask.getCreateTime(), new StringBuilder(dataQualityTask.getPartition()), execParams, runDate, dataSourceMysqlConnect, user, midTableReUse
                , unionAllForSaveResult, currentRuleLeftCols, currentRuleRightCols, complexCols, createUser, shareConnect, dataQualityTask.getDbShare() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + dataQualityTask.getTableShare());
            job.setEngineType(EngineTypeEnum.SPARK_ENGINE.getMessage());
            job.getJobCode().addAll(codes);

            if (fpsCodes.contains(FPS_IMPORT_PREFIX)) {
                job.getJobCode().addAll(dropHiveTable(ruleTaskDetail.getRule()));
            }
            LOGGER.info("Succeed to convert rule into code. rule id: {}, rule name: {}, codes: {}", ruleTaskDetail.getRule().getId(), ruleTaskDetail.getRule().getName(), codes);
        }

        if (CollectionUtils.isNotEmpty(communalSentence)) {
            if (CollectionUtils.isNotEmpty(envNames)) {
                for (String envName : envNames) {
                    job.getJobCode().add("spark.catalog.uncacheTable(\"" + commonTableName + SpecCharEnum.BOTTOM_BAR.getValue() + envName + "\")");
                }
            } else {
                job.getJobCode().add("spark.catalog.uncacheTable(\"" + commonTableName + "\")");
            }
        }
        LOGGER.info("Succeed to convert all rule into actual scala code.");
        job.setTaskId(dataQualityTask.getTaskId());
        job.setStartupParam(startupParam);
        job.setEngineReuse(engineReUse);
        return job;
    }

    private List<String> getCommunalSentence(DataQualityTask dataQualityTask, List<String> envNames) throws UnExpectedRequestException {
        List<String> sqlList = new ArrayList<>();
        if (StringUtils.isEmpty(dataQualityTask.getDbShare()) || StringUtils.isEmpty(dataQualityTask.getTableShare())) {
            return sqlList;
        }
        List<String> columnList = new ArrayList<>();

        String filterPart = dataQualityTask.getFilterShare();
        String fromPart = dataQualityTask.getDbShare() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + dataQualityTask.getTableShare();

        String selectPart = "*";
        if (StringUtils.isNotEmpty(dataQualityTask.getColumnShare())) {
            String[] columns = dataQualityTask.getColumnShare().split(SpecCharEnum.VERTICAL_BAR.getValue());
            for (String col : columns) {
                String colName = col.split(SpecCharEnum.COLON.getValue())[0];
                if (! columnList.contains(colName)) {
                    columnList.add(colName);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(columnList)) {
            selectPart = String.join(SpecCharEnum.COMMA.getValue(), columnList);
        }
        if (CollectionUtils.isNotEmpty(dataQualityTask.getConnectShare())) {
            List<Map<String, Object>> connParamMaps = decryptMysqlInfo(dataQualityTask.getConnectShare());
            for (Map<String, Object> connParams : connParamMaps) {
                String envName = (String) connParams.get("envName");
                if (StringUtils.isEmpty(envName)) {
                    continue;
                }
                envNames.add(envName);
                String host = (String) connParams.get("host");
                String port = (String) connParams.get("port");
                String pwd = (String) connParams.get("password");
                String user = (String) connParams.get("username");
                String dataType = (String) connParams.get("dataType");
                String sql = "select " + selectPart + " from " + fromPart + " where " + filterPart;
                String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
                sqlList.add(str.replace(VARIABLE_NAME_PLACEHOLDER, "communalDf_" + envName));
                sqlList.add("communalDf_" + envName + ".cache()");
                sqlList.add("communalDf_" + envName + ".createOrReplaceTempView(\"" + commonTableName + "_" + envName + "\")");
                sqlList.add("spark.catalog.cacheTable(\"" + commonTableName + "_" + envName + "\")");
            }
        } else {
            sqlList.add("val communalDf = spark.sql(\"select " + selectPart + " from " + fromPart + " where " + filterPart + "\")");
            sqlList.add("communalDf.cache()");
            sqlList.add("communalDf.createOrReplaceTempView(\"" + commonTableName + "\")");
            sqlList.add("spark.catalog.cacheTable(\"" + commonTableName + "\")");
        }
        return sqlList;
    }

    private List<String> generateShellSqlByTask(Rule rule, Date date, String applicationId, String createTime, StringBuilder partition, int count
            , String runDate, List<String> leftCols, List<String> rightCols, List<String> complexCols, String queueName, String createUser) throws Exception {

        List<String> sqlList = new ArrayList<>();

        // Get input meta from template
        List<RuleVariable> inputMetaRuleVariables = rule.getRuleVariables().stream().filter(ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())).collect(Collectors.toList());
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n", " ");
        Map<String, Long> ruleMetricMap = collectRuleMetric(rule);
        Map<String, String> dbTableMap = new HashMap<>(4);
        Map<String, String> filters = new HashMap<>(2);
        StringBuilder realFilter = new StringBuilder();
        StringBuilder realColumn = new StringBuilder();

        templateMidTableAction = getMultiDatasourceFiltesAndUpdateMidTableAction(rule, templateMidTableAction, date, filters);
        replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realFilter, realColumn, dbTableMap, date, createUser);

        // If partition is not specified, replace with filter in rule configuration.
        if (StringUtils.isBlank(partition.toString())) {
            fillPartitionWithRuleConfiguration(partition, rule, templateMidTableAction, inputMetaRuleVariables);
        }

        StringBuilder createFunc = new StringBuilder();
        if (Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) && CollectionUtils.isNotEmpty(complexCols)) {
            createFunc.append("CREATE TEMPORARY FUNCTION ").append(taskDataSourceConfig.getHiveSortUdf()).append(" AS '")
                    .append(taskDataSourceConfig.getHiveSortUdfClassPath()).append("' USING JAR '")
                    .append(taskDataSourceConfig.getHiveSortUdfLibPath()).append("';");
        }

        StringBuilder leftConcat = new StringBuilder();
        for (String col : leftCols) {
            if (Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) && CollectionUtils.isNotEmpty(complexCols) && complexCols.contains(col)) {
                leftConcat.append("nvl(").append(taskDataSourceConfig.getHiveSortUdf()).append("(").append(col).append("),''),");
                continue;
            }
            leftConcat.append("nvl(cast(").append(col).append(" as string),''),");
        }
        leftConcat.deleteCharAt(leftConcat.length() - 1);
        StringBuilder rightConcat = new StringBuilder();
        for (String col : rightCols) {
            if (Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) && CollectionUtils.isNotEmpty(complexCols) && complexCols.contains(col)) {
                rightConcat.append("nvl(").append(taskDataSourceConfig.getHiveSortUdf()).append("(").append(col).append("),''),");
                continue;
            }
            rightConcat.append("nvl(cast(").append(col).append(" as string),''),");
        }
        rightConcat.deleteCharAt(rightConcat.length() - 1);
        StringBuilder setQueue = new StringBuilder();
        if (StringUtils.isNotEmpty(queueName)) {
            setQueue.append("set mapreduce.job.queuename=").append(queueName).append(";");
        }
        String hiveSql = "count_result_" + count + "=`hive -S -e \"" + setQueue.toString() + createFunc.toString() + "select count(1) as diff_count from (select line_md5, count(1) as md5_count from (select md5(concat_ws(''," + leftConcat.toString() + ")) as line_md5 from " + dbTableMap.get("left_database") + dbTableMap.get("left_table") + " where " + filters.get("left_table") + ") left_tmp group by left_tmp.line_md5) qulaitis_left_tmp ${contrast_type} (select line_md5, count(1) as md5_count from (select md5(concat_ws(''," + rightConcat.toString() + ")) as line_md5 from " + dbTableMap.get("right_database") + dbTableMap.get("right_table") + " where " + filters.get("right_table") + ") right_tmp group by right_tmp.line_md5) qulaitis_right_tmp ON (qulaitis_left_tmp.line_md5 = qulaitis_right_tmp.line_md5 AND qulaitis_left_tmp.md5_count = qulaitis_right_tmp.md5_count) where (qulaitis_left_tmp.line_md5 is null AND qulaitis_left_tmp.md5_count is null) OR (qulaitis_right_tmp.line_md5 is null AND qulaitis_right_tmp.md5_count is null) ${outer_filter};\"`";
        hiveSql = hiveSql.replace("${contrast_type}", ContrastTypeEnum.getJoinType(rule.getContrastType()));
        if (StringUtils.isNotEmpty(partition.toString())) {
            hiveSql = hiveSql.replace("${outer_filter}", "AND (" + partition.toString() + ")");
        } else {
            hiveSql = hiveSql.replace("${outer_filter}", "");
        }
        sqlList.add(hiveSql);
        String toArr = "arr_" + count + "=(${count_result_" + count + "// /})";
        sqlList.add(toArr);
        String getLastIndex = "lastIndex_" + count + "=$((${#arr_" + count + "[@]}-1))";
        sqlList.add(getLastIndex);
        String getCountValue = "count_value_" + count + "=`echo ${arr_" + count + "[lastIndex_" + count + "]}`";
        sqlList.add(getCountValue);
        String mysqlConn = "MYSQL=\"" + taskDataSourceConfig.getMysqlsec() + "\"";
        sqlList.add(mysqlConn);
        String ruleVersion = rule.getWorkFlowVersion() == null ? "" : rule.getWorkFlowVersion();
        if (StringUtils.isEmpty(runDate)) {
            runDate = "-1";
        }
        String insertSql = "sql_" + count + "=\"INSERT INTO qualitis_application_task_result (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) VALUES('" + applicationId + "', '" + createTime + "', 'Long', " + rule.getId() + ", $count_value_" + count + ", -1, " + runDate + ", '" + ruleVersion + "');\"";
        if (CollectionUtils.isNotEmpty(ruleMetricMap.values())) {
            insertSql = "sql_" + count + "=\"INSERT INTO qualitis_application_task_result (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) VALUES('" + applicationId + "', '" + createTime + "', 'Long', " + rule.getId() + ", $count_value_" + count + ", " + ruleMetricMap.values().iterator().next() + ", " + runDate + ", '" + ruleVersion + "');\"";
        }
        sqlList.add(insertSql);
        sqlList.add("result=\"$($MYSQL -e\"$sql_" + count + "\")\"");
        return sqlList;
    }

    private List<String> dropHiveTable(Rule rule) {
        List<String> codes = new ArrayList<>();
        LOGGER.info("Drop fps temp table after select.");
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
                codes.add(FPS_DROP_TABLE.replace("{FPS_DB}", ruleDataSource.getDbName()).replace("{FPS_TALBE}", ruleDataSource.getTableName()));
            }
        }
        return codes;
    }

    /**
     * Fps file to hive persistant table. Construct code by call linkis api of 'HDFS file to hive'.
     *
     * @param ruleDataSource
     * @param user
     * @param partOfVariableName
     * @param count
     * @return
     * @throws IOException
     */
    private List<String> generateHiveTable(RuleDataSource ruleDataSource, String user, String partOfVariableName, int count) {
        List<String> res = new ArrayList<>(4);
        if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
            LOGGER.info("Start to generate fps to hive code");

            String sourcePrefix = "tmpSource_";
            String destinationPrefix = "tmpDestination_";

            String source = FPS_SOURCE.replace("{TMP_SOURCE}", sourcePrefix + partOfVariableName + count)
                    .replace("{DATA_NOW}", DateUtils.now("yyyyMMdd"))
                    .replace("{FPS_TALBE}", ruleDataSource.getTableName())
                    .replace("{FPS_TYPE}", ruleDataSource.getFileType())
                    .replace("{SHEET_NAME}", ruleDataSource.getFileSheetName())
                    .replace("{FILE_DELIMITER}", ruleDataSource.getFileDelimiter())
                    .replace("{FILE_HEADER}", ruleDataSource.getFileHeader().toString().toLowerCase());

            source = source.replace("{USER}", user);

            LOGGER.info("Finish to concat hive source: " + source);
            List<FpsColumnInfo> fpsColumnInfos = new ArrayList<>();
            String columnInfo = ruleDataSource.getFileTableDesc();
            int index = 0;
            for (String column : columnInfo.split(SpecCharEnum.COMMA.getValue())) {
                String name = column.split(":")[0];
                String type = column.split(":")[1];
                if (type.contains("date")) {
                    String[] dateType = type.split("_");
                    FpsColumnInfo fpsColumnInfo = new FpsColumnInfo(name, index, "", dateType[0], DATE_FORMAT.get(dateType[1]));
                    fpsColumnInfos.add(fpsColumnInfo);
                    index++;
                    continue;
                }
                FpsColumnInfo fpsColumnInfo = new FpsColumnInfo(name, index, "", type, "");
                fpsColumnInfos.add(fpsColumnInfo);
                index++;
            }
            String destination = FPS_DESTINATION.replace("{TMP_DESTINATION}", destinationPrefix + partOfVariableName + count)
                    .replace("{FPS_DB}", ruleDataSource.getDbName())
                    .replace("{FPS_TALBE}", ruleDataSource.getTableName())
                    .replace("{COLUMN_LIST}", CustomObjectMapper.transObjectToJson(fpsColumnInfos));
            LOGGER.info("hive destination: " + destination);
            res.add(source);
            res.add(destination);
            res.add(FPS_IMPORT.replace("{TMP_SOURCE}", sourcePrefix + partOfVariableName + count).replace("{TMP_DESTINATION}", destinationPrefix + partOfVariableName + count));
        }
        return res;
    }

    /**
     * Fps file to hive temp table with spark session.
     *
     * @return
     */
    public List<String> generateTempHiveTable(Rule rule, String user) {
        List<String> res = new ArrayList<>(4);
        String partOfVariableName = rule.getProject().getName() + SpecCharEnum.BOTTOM_BAR.getValue() + rule.getName();
        int count = 0;
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
                if (".xlsx".equals(ruleDataSource.getFileType()) || ".xls".equals(ruleDataSource.getFileType())) {
                    res.addAll(generateHiveTable(ruleDataSource, user, partOfVariableName, count));
                    count ++;
                    continue;
                }
                String dfPrefix = "tmpDF_";
                String varDf = dfPrefix + partOfVariableName;
                String realPath = FPS_FILE_PATH.replace("{DATA_NOW}", DateUtils.now("yyyyMMdd"))
                        .concat(ruleDataSource.getTableName()).concat(ruleDataSource.getFileType());
                realPath = realPath.replace("{USER}", user);
                if (ruleDataSource.getFileHeader()) {
                    res.add(FPS_TO_HIVE_WITH_HEADER.replace("{DF}", varDf)
                            .replace("{DELIMITER}", ruleDataSource.getFileDelimiter())
                            .replace("{HDFS_PREFIX}", fpsConfig.getHdfsPrefix())
                            .replace("{FPS_FILE_PATH}", realPath)
                    );
                } else {
                    res.add(IMPORT_SCHEMA);
                    String varSchema = "tmpSchema_" + partOfVariableName;
                    StringBuilder schemaCode = new StringBuilder(CONSTRUCT_SCHEMA.replace("{SCHEMA}", varSchema));
                    for (String column : ruleDataSource.getFileTableDesc().split(SpecCharEnum.COMMA.getValue())) {
                        String name = column.split(":")[0];
                        String type = column.split(":")[1];
                        schemaCode.append(CONSTRUCT_FIELD.replace("{FIELD_NAME}", name).replace("{FIELD_TYPE}", FIELD_TYPE.get(type)));
                    }
                    res.add(schemaCode.toString());
                    res.add(FPS_TO_HIVE_WITH_SCHEMA.replace("{DF}", varDf)
                            .replace("{DELIMITER}", ruleDataSource.getFileDelimiter())
                            .replace("{SCHEMA}", varSchema)
                            .replace("{HDFS_PREFIX}", fpsConfig.getHdfsPrefix())
                            .replace("{FPS_FILE_PATH}", realPath)
                    );
                }
                res.add(DF_REGISTER.replace("{DF}", varDf).replace("{FILE_NAME}", ruleDataSource.getTableName()));
            }
        }
        return res;
    }

    /**
     * Convert task into scala code
     *
     * @param job
     * @param rule
     * @param date
     * @param applicationId
     * @param midTableName
     * @param createTime
     * @param partition
     * @param execParams
     * @param runDate
     * @param dataSourceMysqlConnect
     * @param midTableReUse
     * @param unionAllForSaveResult
     * @param leftCols
     * @param rightCols
     * @param complexCols
     * @param shareConnect
     * @param shareFromPart
     * @return
     * @throws ConvertException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    private List<String> generateSparkSqlByTask(DataQualityJob job, Rule rule, Date date, String applicationId, String midTableName, String createTime
        , StringBuilder partition, Map<String, String> execParams, String runDate, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user
        , boolean midTableReUse, boolean unionAllForSaveResult, List<String> leftCols, List<String> rightCols, List<String> complexCols, String createUser, boolean shareConnect, String shareFromPart) throws ConvertException, RuleVariableNotSupportException, RuleVariableNotFoundException, UnExpectedRequestException, MetaDataAcquireFailedException {
        List<String> sqlList = new ArrayList<>();
        Map<String, String> filters = new HashMap<>(2);

        // Collect rule metric and build in save sentence sql.
        Map<String, Long> ruleMetricMap = collectRuleMetric(rule);

        // Get SQL from template after remove '\n'
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n", " ");
        String templateEnName = StringUtils.isNotEmpty(rule.getTemplate().getEnName()) ? rule.getTemplate().getEnName() : "defaultCheckDF";

        if (MUL_SOURCE_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = getMultiDatasourceFiltesAndUpdateMidTableAction(rule, templateMidTableAction, date, filters);
        } else if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = customMidTableActionUpdate(rule, templateMidTableAction, date, execParams, partition, ruleMetricMap);
            templateEnName = "customCheckDF";
        }

        // Get statistics meta
        List<RuleVariable> statisticsRuleVariables = rule.getRuleVariables().stream().filter(ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.STATISTICS_ARG.getCode())).collect(Collectors.toList());
        // Get select input meta
        List<RuleVariable> inputMetaRuleVariables = rule.getRuleVariables().stream().filter(ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())).collect(Collectors.toList());

        // If partition is not specified, replace with filter from rule datasource.
        if (StringUtils.isBlank(partition.toString())) {
            fillPartitionWithRuleConfiguration(partition, rule, templateMidTableAction, inputMetaRuleVariables);
        }

        // Get dbs and tables
        Map<String, String> dbTableMap = new HashMap<>(4);
        // Get column and filter
        StringBuilder realColumn = new StringBuilder();
        StringBuilder realFilter = new StringBuilder();
        // Get template sql and replace all replaceholders
        String midTableAction = replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realFilter, realColumn, dbTableMap, date, createUser);

        // Prepare for multiple rule
        List<Map<String, Object>> sourceConnect = new ArrayList<>();
        List<Map<String, Object>> targetConnect = new ArrayList<>();
        prepareDecrptedConnectParamForMultipleRule(sourceConnect, targetConnect, dataSourceMysqlConnect, rule);

        Map<String, String> selectResult = new LinkedHashMap<>(rule.getRuleDataSources().size());
        String partOfVariableName = templateEnName.replace(" ", "") + SpecCharEnum.EQUAL.getValue() + rule.getName();
        handleRuleSelectSql(rule, midTableName, partition, partOfVariableName, runDate, dataSourceMysqlConnect, sqlList, filters, dbTableMap, midTableAction, sourceConnect, targetConnect, selectResult, midTableReUse, unionAllForSaveResult, leftCols, rightCols, complexCols, shareConnect, shareFromPart);

        Set<TemplateMidTableInputMeta> templateMidTableInputMetas = rule.getTemplate().getTemplateMidTableInputMetas();
        boolean saveNewValue = templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> Boolean.TRUE.equals(templateMidTableInputMeta.getWhetherNewValue()));
        boolean numRangeNewValue = saveNewValue && templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));
        boolean enumListNewValue = saveNewValue && templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.LIST.getCode().equals(templateMidTableInputMeta.getInputType()) || TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));
        sqlList.addAll(saveStatisticAndSaveMySqlSentence(rule.getWorkFlowVersion() != null ? rule.getWorkFlowVersion() : "", rule.getId(), ruleMetricMap, rule.getTemplate().getStatisticAction(), applicationId, statisticsRuleVariables, createTime, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, user, realColumn, enumListNewValue, numRangeNewValue, selectResult, unionAllForSaveResult));
        job.setResultNum(selectResult.size());
        return sqlList;
    }

    private void prepareDecrptedConnectParamForMultipleRule(List<Map<String, Object>> sourceConnect, List<Map<String, Object>> targetConnect, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, Rule rule)
        throws UnExpectedRequestException {
        if (dataSourceMysqlConnect != null && dataSourceMysqlConnect.size() > 0) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                // Handle multiple datasource connect params.
                List<Map<String, Object>> connectParams = dataSourceMysqlConnect.get(ruleDataSource.getId());
                if (connectParams == null) {
                    continue;
                }
                if (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(0)) {
                    // If mysql sec, decrypt password and user name.
                    sourceConnect.addAll(decryptMysqlInfo(connectParams));
                }
                if (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(1)) {
                    // If mysql sec, decrypt password and user name.
                    targetConnect.addAll(decryptMysqlInfo(connectParams));
                }
            }
        }
    }

    private Map<String, Long> collectRuleMetric(Rule rule) {
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
        return ruleMetricMap;
    }

    private void handleRuleSelectSql(Rule rule, String midTableName, StringBuilder partition, String partOfVariableName, String runDate, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect
        , List<String> sqlList, Map<String, String> filters, Map<String, String> dbTableMap, String midTableAction, List<Map<String, Object>> sourceConnect, List<Map<String, Object>> targetConnect
        , Map<String, String> selectResult, boolean midTableReUse, boolean unionAllForSaveResult, List<String> leftCols, List<String> rightCols, List<String> complexCols, boolean shareConnect, String shareFromPart) throws UnExpectedRequestException {

        boolean systemCompareTemplate = rule.getTemplate().getId().longValue() == MUL_SOURCE_ACCURACY_TEMPLATE_ID.longValue() || rule.getTemplate().getId().longValue() == MUL_SOURCE_FULL_TEMPLATE_ID.longValue();
        if (systemCompareTemplate && dbTableMap.size() > 0) {
            if (rule.getTemplate().getId().longValue() == MUL_SOURCE_ACCURACY_TEMPLATE_ID.longValue()) {
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(), targetIterator = targetConnect.iterator(); sourceIterator.hasNext() && targetIterator.hasNext(); ) {
                        Map<String, Object> sourceConnectMap = sourceIterator.next();
                        Map<String, Object> targetConnectMap = targetIterator.next();
                        String sourceEnvName = (String) sourceConnectMap.get("envName");
                        String targetEnvName = (String) targetConnectMap.get("envName");
                        if (StringUtils.isEmpty(sourceEnvName) || StringUtils.isEmpty(targetEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName + targetEnvName, sourceConnectMap, targetConnectMap, selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(); sourceIterator.hasNext(); ) {
                        Map<String, Object> sourceConnectMap = sourceIterator.next();
                        String sourceEnvName = (String) sourceConnectMap.get("envName");
                        if (StringUtils.isEmpty(sourceEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName, sourceIterator.next(), null, selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(targetConnect) && CollectionUtils.isEmpty(sourceConnect)) {
                    for (Iterator<Map<String, Object>> targetIterator = targetConnect.iterator(); targetIterator.hasNext(); ) {
                        Map<String, Object> targetConnectMap = targetIterator.next();
                        String targetEnvName = (String) targetConnectMap.get("envName");
                        if (StringUtils.isEmpty(targetEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + targetEnvName, null, targetIterator.next(), selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], null, null, selectResult));

                    if (Boolean.TRUE.equals(rule.getTemplate().getSaveMidTable())) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, midTableReUse));
                    }
                }
            } else if (rule.getTemplate().getId().longValue() == MUL_SOURCE_FULL_TEMPLATE_ID.longValue()) {
                sqlList.add("val UUID = java.util.UUID.randomUUID.toString");
                // Import sql function.
                sqlList.addAll(getImportSql());
                List<String> columns = new ArrayList<>();
                String columnsInfo = rule.getRuleDataSources().stream().filter(ruleDataSource -> QualitisConstants.LEFT_INDEX.equals(ruleDataSource.getDatasourceIndex())).iterator().next().getColName();

                if (StringUtils.isNotEmpty(columnsInfo)) {
                    String[] realColumns = columnsInfo.split(SpecCharEnum.VERTICAL_BAR.getValue());
                    for (String column : realColumns) {
                        String[] colInfo = column.split(SpecCharEnum.COLON.getValue());
                        String colName = colInfo[0];
                        String colType = colInfo[1];
                        boolean needSort = Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) &&
                                (colType.toLowerCase().startsWith(QualitisConstants.MAP_TYPE) || colType.toLowerCase()
                                        .startsWith(QualitisConstants.ARRAY_TYPE) || colType.toLowerCase().startsWith(QualitisConstants.STRUCT_TYPE));
                        if (needSort) {
                            columns.add(taskDataSourceConfig.getHiveSortUdf() + "(" + colName + ")");
                        } else {
                            columns.add(colName);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(), targetIterator = targetConnect.iterator(); sourceIterator.hasNext() && targetIterator.hasNext(); ) {
                        Map<String, Object> sourceConnectMap = sourceIterator.next();
                        Map<String, Object> targetConnectMap = targetIterator.next();
                        String sourceEnvName = (String) sourceConnectMap.get("envName");
                        String targetEnvName = (String) targetConnectMap.get("envName");
                        if (StringUtils.isEmpty(sourceEnvName) || StringUtils.isEmpty(targetEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName + targetEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                , sourceIterator.next(), targetIterator.next(), rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(); sourceIterator.hasNext(); ) {
                        Map<String, Object> sourceConnectMap = sourceIterator.next();
                        String sourceEnvName = (String) sourceConnectMap.get("envName");
                        if (StringUtils.isEmpty(sourceEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                , sourceIterator.next(), null, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> targetIterator = targetConnect.iterator(); targetIterator.hasNext(); ) {
                        Map<String, Object> targetConnectMap = targetIterator.next();
                        String targetEnvName = (String) targetConnectMap.get("envName");
                        if (StringUtils.isEmpty(targetEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + targetEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                , null, targetIterator.next(), rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    }

                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], partition.toString(), filters, Strings.join(columns, ',')
                            , null, null, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));

                    if (Boolean.TRUE.equals(rule.getTemplate().getSaveMidTable())) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, midTableReUse));
                    }
                }
            } else {
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(), targetIterator = targetConnect.iterator(); sourceIterator.hasNext() && targetIterator.hasNext(); ) {
                        Map<String, Object> sourceConnectMap = sourceIterator.next();
                        Map<String, Object> targetConnectMap = targetIterator.next();
                        String sourceEnvName = (String) sourceConnectMap.get("envName");
                        String targetEnvName = (String) targetConnectMap.get("envName");
                        if (StringUtils.isEmpty(sourceEnvName) || StringUtils.isEmpty(targetEnvName)) {
                            continue;
                        }
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName + targetEnvName, sourceIterator.next(), targetIterator.next(), selectResult));
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], null, null, selectResult));

                    if (Boolean.TRUE.equals(rule.getTemplate().getSaveMidTable())) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, midTableReUse));
                    }
                }
            }
        } else {
            // Generate select statement and save into hive database
            List<List<String>> tableEnvs;
            RuleDataSource ruleDataSource;
            StringBuilder filterFields = new StringBuilder();
            if (RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode().equals(rule.getRuleType())) {
                tableEnvs = null;
                ruleDataSource = rule.getRuleDataSources().stream().filter(dataSource -> dataSource.getDatasourceIndex() == null).iterator().next();
                if (Boolean.TRUE.equals(rule.getTemplate().getFilterFields())) {
                    String filterColName = ruleDataSource.getColName();
                    List<String> filterColNameList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(filterColName)) {
                        String[] realColumns = filterColName.split(SpecCharEnum.VERTICAL_BAR.getValue());
                        for (String column : realColumns) {
                            String[] colInfo = column.split(SpecCharEnum.COLON.getValue());
                            String colName = colInfo[0];
                            filterColNameList.add("\"" + colName + "\"");
                        }
                        filterFields.append(".select(").append(Strings.join(filterColNameList, ',')).append(")");
                    }
                }
            } else {
                ruleDataSource = rule.getRuleDataSources().stream().filter(dataSource -> dataSource.getDatasourceIndex() != null).iterator().next();
                List<RuleDataSource> parsedRuleDataSource = rule.getRuleDataSources().stream().filter(dataSource -> dataSource.getDatasourceIndex() == null).collect(Collectors.toList());

                tableEnvs = new ArrayList<>(parsedRuleDataSource.size());
                midTableAction = preSelectEnvsSql(ruleDataSource, parsedRuleDataSource, tableEnvs, dataSourceMysqlConnect, sqlList, midTableAction);
            }
            List<Map<String, Object>> decryptedMysqlInfo;

            if (shareConnect) {
                decryptedMysqlInfo = dataSourceMysqlConnect.get(ruleDataSource.getId());
            } else {
                decryptedMysqlInfo = decryptMysqlInfo(dataSourceMysqlConnect.get(ruleDataSource.getId()));
            }
            sqlList.addAll(generateSparkSqlAndSaveSentence(midTableAction, midTableName, rule, partOfVariableName, decryptedMysqlInfo, runDate, selectResult, midTableReUse, unionAllForSaveResult, filterFields.toString(), tableEnvs, shareConnect, shareFromPart));
        }
    }

    private String preSelectEnvsSql(RuleDataSource ruleDataSource, List<RuleDataSource> parsedRuleDataSource, List<List<String>> tableEnvs, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect
            , List<String> sqlList, String midTableAction) throws UnExpectedRequestException {

        List<RuleDataSourceEnv> ruleDataSourceEnvs = ruleDataSource.getRuleDataSourceEnvs();
        if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
            Set<RuleDataSourceEnv> ruleDataSourceEnvsForSelect = ruleDataSourceEnvs.stream().filter(ruleDataSourceEnv -> StringUtils.isNotEmpty(ruleDataSourceEnv.getDbAndTable())).collect(Collectors.toSet());

            if (CollectionUtils.isNotEmpty(ruleDataSourceEnvsForSelect)) {

                List<Map<String, Object>> decryptedMysqlInfo = decryptMysqlInfo(dataSourceMysqlConnect.get(ruleDataSource.getId()));

                LOGGER.info("Start to replace env mappings with register temp table.");

                for (RuleDataSource currentRuleDataSource : parsedRuleDataSource) {
                    String dbAliasName = currentRuleDataSource.getDbName();
                    String tableName = currentRuleDataSource.getTableName();

                    List<RuleDataSourceEnv> currentRuleDataSourceEnvs = ruleDataSourceEnvsForSelect.stream().filter(ruleDataSourceEnv ->
                            currentRuleDataSource.getDbName().equals(ruleDataSourceEnv.getDbAndTable().split(SpecCharEnum.PERIOD.getValue())[1]))
                            .collect(Collectors.toList());

                    List<String> varEnvs = new ArrayList<>(currentRuleDataSourceEnvs.size());
                    for (RuleDataSourceEnv ruleDataSourceEnv : currentRuleDataSourceEnvs) {
                        String envName = ruleDataSourceEnv.getEnvName();
                        String tmp = "select * from " + ruleDataSourceEnv.getDbAndTable().split(SpecCharEnum.PERIOD.getValue())[0] + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + tableName;
                        String tmpTableName = ruleDataSourceEnv.getDbAndTable().split(SpecCharEnum.PERIOD.getValue())[0] + tableName + "_" + ruleDataSource.getId() + "_" + ruleDataSourceEnv.getId();
                        Map<String, Object> connParams = decryptedMysqlInfo.stream().filter(map -> envName.equals(map.get("envName"))).iterator().next();
                        String host = (String) connParams.get("host");
                        String port = (String) connParams.get("port");
                        String pwd = (String) connParams.get("password");
                        String user = (String) connParams.get("username");
                        String dataType = (String) connParams.get("dataType");
                        String selectStr = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, tmp)
                                .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                                .replace("${MYSQL_IP}", host)
                                .replace("${MYSQL_PORT}", port)
                                .replace("${MYSQL_USER}", user)
                                .replace("${MYSQL_PASSWORD}", pwd)
                                .replace(VARIABLE_NAME_PLACEHOLDER, tmpTableName);

                        String tmpTable = "qualitis_tmp_table_" + tmpTableName;
                        String regStr = tmpTableName + ".registerTempTable(\"" + tmpTable + "\")";
                        LOGGER.info("Select sql [{}] added to sql list for env.", selectStr);
                        LOGGER.info("Reg sql [{}] added to sql list for env.", regStr);
                        sqlList.add(selectStr);
                        sqlList.add(regStr);

                        varEnvs.add(dbAliasName + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + tableName + SpecCharEnum.COLON.getValue() + tmpTable + SpecCharEnum.COLON.getValue() + envName);
                    }

                    tableEnvs.add(varEnvs);
                }

                dataSourceMysqlConnect.remove(ruleDataSource.getId());
                LOGGER.info("Finished to replace env mappings with register temp table. Sql: " + midTableAction);
                sqlList.add("Qualitis System Code Dividing Line");
            }
        }

        return midTableAction;
    }

    private List<Map<String, Object>> decryptMysqlInfo(List<Map<String, Object>> connParamMaps) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(connParamMaps)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> connParamMapsReal = new ArrayList<>(connParamMaps.size());
        for (Map<String, Object> currentConnectParams : connParamMaps) {
            if (DPM.equals(currentConnectParams.get("authType"))) {
                connParamMapsReal.add(getUserNameAndPassword(currentConnectParams));
            } else {
                String password = (String) currentConnectParams.get("password");
                if (currentConnectParams.get("needDecode") != null && "false".equals((String) currentConnectParams.get("needDecode"))) {
                    currentConnectParams.put("password", password);
                } else {
                    currentConnectParams.put("password", CryptoUtils.decode(password));
                }
                connParamMapsReal.add(currentConnectParams);
            }
        }

        return connParamMapsReal;
    }

    private Map<String, Object> getUserNameAndPassword(Map<String, Object> connectParams) throws UnExpectedRequestException {
//        String appId = (String) connectParams.get("appid");
//        String objectId = (String) connectParams.get("objectid");
//        String timestamp = (String) connectParams.get("timestamp");
//
//        String dk = (String) connectParams.get("dk");
//        String datasourceInf = LocalNetwork.getNetCardName();
//        AccountInfoObtainer obtainer = new AccountInfoObtainer(dpmConfig.getDatasourceServer(), dpmConfig.getDatasourcePort(), datasourceInf);
//        obtainer.init();
//        try {
//            AccountInfoSys accountInfoSys = obtainer.getAccountInfo_system(dk, timestamp, dpmConfig.getDatasourceSystemAppId()
//                    , dpmConfig.getDatasourceSystemAppKey(), appId, objectId);
//            String userName = accountInfoSys.getName();
//            String passwordTest = accountInfoSys.getPassword();
//
//            connectParams.put("username", userName);
//            connectParams.put("password", passwordTest);
//        } catch (AccountInfoObtainException e) {
//            LOGGER.error(e.getMessage(), e);
//            throw new UnExpectedRequestException("{&FAILED_TO_GET_USERNAME_PASSWORD}", 500);
//        }
        return connectParams;
    }

    private String customMidTableActionUpdate(Rule rule, String templateMidTableAction, Date date, Map<String, String> execParams,
                                              StringBuilder partition, Map<String, Long> ruleMetricMap) throws UnExpectedRequestException {
        if (StringUtils.isNotBlank(rule.getCsId())) {
            templateMidTableAction = templateMidTableAction.replace(RuleConstraintEnum.CUSTOM_DATABASE_PREFIS.getValue().concat(SpecCharEnum.PERIOD.getValue()), "");
        }
        if (StringUtils.isNotBlank(partition.toString())) {
            templateMidTableAction = templateMidTableAction.replace("${filter}", partition.toString());
        } else if (StringUtils.isNotBlank(rule.getWhereContent())) {
            templateMidTableAction = templateMidTableAction.replace("${filter}", rule.getWhereContent());
        }
        for (Map.Entry<String, String> entry : execParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            templateMidTableAction = templateMidTableAction.replace("${" + key + "}", value);
        }
        templateMidTableAction = DateExprReplaceUtil.replaceRunDate(date, templateMidTableAction);

        Set<String> ruleMetricNames = ruleMetricMap.keySet();
        for (String ruleMetricName : ruleMetricNames) {
            String cleanRuleMetricName = ruleMetricName.replace("-", "_");
            templateMidTableAction = templateMidTableAction.replace(ruleMetricName, cleanRuleMetricName);
        }

        return templateMidTableAction;
    }

    private String getMultiDatasourceFiltesAndUpdateMidTableAction(Rule rule, String templateMidTableAction, Date date, Map<String, String> filters) throws UnExpectedRequestException {
        Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (ruleDataSource.getDatasourceIndex().equals(0)) {
                String leftFilter = ruleDataSource.getFilter();
                leftFilter = DateExprReplaceUtil.replaceFilter(date, leftFilter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_LEFT_PLACEHOLDER, leftFilter);
                filters.put("left_table", leftFilter);
            } else {
                String rightFilter = ruleDataSource.getFilter();
                rightFilter = DateExprReplaceUtil.replaceFilter(date, rightFilter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_RIGHT_PLACEHOLDER, rightFilter);
                filters.put("right_table", rightFilter);
            }
        }
        if (rule.getTemplate().getId().longValue() != MUL_SOURCE_ACCURACY_TEMPLATE_ID.longValue()) {
            return templateMidTableAction;
        }
        List<RuleDataSourceMapping> ruleDataSourceMappings = rule.getRuleDataSourceMappings().stream()
                .filter(ruleDataSourceMapping -> ruleDataSourceMapping.getMappingType() != null && ruleDataSourceMapping.getMappingType().equals(MappingTypeEnum.MATCHING_FIELDS.getCode())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ruleDataSourceMappings)) {
            StringBuilder compareColumns = new StringBuilder();
            int indexCol = 1;
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                compareColumns.append(ruleDataSourceMapping.getLeftStatement()).append(" AS ").append("col" + indexCol).append(", ");
                indexCol++;
                compareColumns.append(ruleDataSourceMapping.getRightStatement()).append(" AS ").append("col" + indexCol).append(", ");
                indexCol++;
            }

            int index = templateMidTableAction.indexOf("CASE WHEN");

            templateMidTableAction = new StringBuffer(templateMidTableAction).insert(index, compareColumns.toString()).toString();
        }
        return templateMidTableAction;
    }

    private String fillPartitionWithRuleConfiguration(StringBuilder partition, Rule rule, String templateMidTableAction, List<RuleVariable> inputMetaRuleVariables) {
        if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode())) {
            partition.append(new ArrayList<>(rule.getRuleDataSources()).get(0).getFilter());
        } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
            // Replace placeholder.
            if (StringUtils.isNotEmpty(rule.getWhereContent())) {
                partition.append(rule.getWhereContent());
            }
        } else if (rule.getTemplate().getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            // Replace placeholder.
            List<RuleVariable> filterVariable = inputMetaRuleVariables.stream().filter(
                    r -> r.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode())
            ).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filterVariable)) {
                partition.append(filterVariable.iterator().next().getValue());
            }
        }
        return templateMidTableAction;
    }

    private List<String> getMultiSourceAccuracyfromSql(String midTableAction, Map<String, String> dbTableMap, Map<String, String> filters, String partOfVariableName
            , Map<String, Object> sourceConnect, Map<String, Object> targetConnect, Map<String, String> selectResult) {
        // Solve partition, value, hash value
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();

        StringBuilder envName = new StringBuilder();

        sourceSql.append("select *").append(" from ")
                .append(dbTableMap.get("left_database")).append(dbTableMap.get("left_table"))
                .append(" where ").append(filters.get("left_table"));
        targetSql.append("select *").append(" from ")
                .append(dbTableMap.get("right_database")).append(dbTableMap.get("right_table"))
                .append(" where ").append(filters.get("right_table"));

        if (sourceConnect != null && sourceConnect.size() > 0) {
            String host = (String) sourceConnect.get("host");
            String port = (String) sourceConnect.get("port");
            String user = (String) sourceConnect.get("username");
            String pwd = (String) sourceConnect.get("password");
            String dataType = (String) sourceConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfVariableName)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
            envName.append("[").append((String) sourceConnect.get("envName")).append("]");
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
        }
        if (targetConnect != null && targetConnect.size() > 0) {
            String host = (String) targetConnect.get("host");
            String port = (String) targetConnect.get("port");
            String user = (String) targetConnect.get("username");
            String pwd = (String) targetConnect.get("password");
            String dataType = (String) targetConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfVariableName)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
            envName.append("[").append((String) targetConnect.get("envName")).append("]");
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
        }
        transformSql.add("originalDFLeft_" + partOfVariableName + ".registerTempTable(\"tmp1_" + partOfVariableName + dbTableMap.get("left_table") + "\")");
        transformSql.add("originalDFRight_" + partOfVariableName + ".registerTempTable(\"tmp2_" + partOfVariableName + dbTableMap.get("right_table") + "\")");
        String commonJoin = midTableAction
                .replace(dbTableMap.get("left_database") + dbTableMap.get("left_table") + " ", "tmp1_" + partOfVariableName + dbTableMap.get("left_table") + " ")
                .replace(dbTableMap.get("right_database") + dbTableMap.get("right_table") + " ", "tmp2_" + partOfVariableName + dbTableMap.get("right_table") + " ");
        String variableFormer = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfVariableName);
        String joinSql = "val " + variableFormer + " = spark.sql(\"" + commonJoin + "\")";
        transformSql.add(joinSql);
        String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName);
        // Select compare_result = 0
        transformSql.add("val " + variableLatter + " = " + variableFormer + ".where(" + variableFormer + "(\"compare_result\") === 0)");
        selectResult.put(variableLatter, envName.toString());
        return transformSql;
    }

    private List<String> getSpecialTransformSql(Map<String, String> dbTableMap, String partOfVariableName, String filter, Map<String, String> filters, String columns
            , Map<String, Object> sourceConnect, Map<String, Object> targetConnect, Integer contrastType, List<String> leftCols, List<String> rightCols, List<String> complexCols, Map<String, String> selectResult) {

        // Solve partition, value, hash value
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();
        StringBuilder envName = new StringBuilder();
        handleSourceAndTargetSql(dbTableMap, filters, columns, sourceConnect, targetConnect, transformSql, sourceSql, targetSql, leftCols, rightCols, complexCols, envName, partOfVariableName);
        // Full line to MD5 with dataframe api transformation.
        StringBuilder tmpRegisterTableLeft = new StringBuilder();
        StringBuilder tmpRegisterTableRight = new StringBuilder();
        fuleLineToHashLine(transformSql, partOfVariableName, tmpRegisterTableLeft, tmpRegisterTableRight);
        String originalVariableName = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfVariableName);
        String joinSql = "val " + originalVariableName + " = spark.sql(\"SELECT qulaitis_left_tmp.qualitis_full_line_hash_value as left_full_hash_line, qulaitis_left_tmp.qualitis_mul_db_accuracy_num as left_full_line_num, qulaitis_right_tmp.qualitis_full_line_hash_value as right_full_hash_line, qulaitis_right_tmp.qualitis_mul_db_accuracy_num as right_full_line_num FROM (SELECT qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM " + tmpRegisterTableLeft.toString() + " WHERE true group by qualitis_full_line_hash_value) qulaitis_left_tmp ${contrast_type} (SELECT qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM " + tmpRegisterTableRight.toString() + " WHERE true group by qualitis_full_line_hash_value) qulaitis_right_tmp ON (qulaitis_left_tmp.qualitis_full_line_hash_value = qulaitis_right_tmp.qualitis_full_line_hash_value AND qulaitis_left_tmp.qualitis_mul_db_accuracy_num = qulaitis_right_tmp.qualitis_mul_db_accuracy_num) WHERE (qulaitis_right_tmp.qualitis_full_line_hash_value is null AND qulaitis_right_tmp.qualitis_mul_db_accuracy_num is null) OR (qulaitis_left_tmp.qualitis_full_line_hash_value is null AND qulaitis_left_tmp.qualitis_mul_db_accuracy_num is null) ${outer_filter}\")";
        joinSql = joinSql.replace("${contrast_type}", ContrastTypeEnum.getJoinType(contrastType));
        if (StringUtils.isNotEmpty(filter)) {
            joinSql = joinSql.replace("${outer_filter}", "AND (" + filter + ")");
        } else {
            joinSql = joinSql.replace("${outer_filter}", "");
        }
        transformSql.add(joinSql);
        String statisticVariableName = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName);
        if (fpsConfig.getLightweightQuery()) {
            String leftVariableName = getVariableNameByRule(OptTypeEnum.LEFT_JOIN_STATISTIC_DF.getMessage(), partOfVariableName);
            String rightVariableName = getVariableNameByRule(OptTypeEnum.RIGHT_JOIN_STATISTIC_DF.getMessage(), partOfVariableName);

            transformSql.add(originalVariableName + ".registerTempTable(\"md5_table_total_" + partOfVariableName + "\")");
            String joinSqlWithLeft = "val " + leftVariableName + " = spark.sql(\"\"\"SELECT \"left\" as source, " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_value as full_line, md5_table_total_" + partOfVariableName + ".left_full_line_num FROM " + tmpRegisterTableLeft.toString() + " full outer join md5_table_total_" + partOfVariableName + " on " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_hash_value = md5_table_total_" + partOfVariableName + ".left_full_hash_line where " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_hash_value is not null and md5_table_total_" + partOfVariableName + ".left_full_hash_line is not null\"\"\")";
            String joinSqlWithRight = "val " + rightVariableName + " = spark.sql(\"\"\"SELECT \"right\" as source, " + tmpRegisterTableRight.toString() + ".qualitis_full_line_value as full_line, md5_table_total_" + partOfVariableName + ".right_full_line_num FROM " + tmpRegisterTableRight.toString() + " full outer join md5_table_total_" + partOfVariableName + " on " + tmpRegisterTableRight.toString() + ".qualitis_full_line_hash_value = md5_table_total_" + partOfVariableName + ".right_full_hash_line where " + tmpRegisterTableRight.toString() + ".qualitis_full_line_hash_value is not null and md5_table_total_" + partOfVariableName + ".right_full_hash_line is not null\"\"\")";

            transformSql.add(joinSqlWithLeft);
            transformSql.add(joinSqlWithRight);

            transformSql.add("val " + statisticVariableName + " = " + leftVariableName + ".union(" + rightVariableName + ")");
        } else {
            transformSql.add("val " + statisticVariableName + " = " + originalVariableName);
        }
        if (StringUtils.isNotEmpty(envName)) {
            selectResult.put(statisticVariableName, envName.toString());
        }

        return transformSql;
    }

    private void handleSourceAndTargetSql(Map<String, String> dbTableMap, Map<String, String> filters, String columns, Map<String, Object> sourceConnect, Map<String, Object> targetConnect
        , List<String> transformSql, StringBuilder sourceSql, StringBuilder targetSql, List<String> leftCols, List<String> rightCols, List<String> complexCols, StringBuilder envName, String partOfVariableName) {
        if (StringUtils.isNotBlank(columns)) {
            sourceSql.append("select ").append(columns);
            targetSql.append("select ").append(columns);
        } else {
            if (taskDataSourceConfig.getHiveSortUdfOpen() && CollectionUtils.isNotEmpty(complexCols)) {
                List<String> leftColsReal = new ArrayList<>(leftCols.size());
                List<String> rightColsReal = new ArrayList<>(rightCols.size());
                sourceSql.append("select ");
                targetSql.append("select ");
                for (String col : leftCols) {
                    if (complexCols.contains(col)) {
                        leftColsReal.add(taskDataSourceConfig.getHiveSortUdf() + "(" + col + ")");
                    } else {
                        leftColsReal.add(col);
                    }
                }
                sourceSql.append(String.join(",", leftColsReal));

                for (String col : rightCols) {
                    if (complexCols.contains(col)) {
                        rightColsReal.add(taskDataSourceConfig.getHiveSortUdf() + "(" + col + ")");
                    } else {
                        rightColsReal.add(col);
                    }
                }
                targetSql.append(String.join(",", rightColsReal));
            } else {
                sourceSql.append("select *");
                targetSql.append("select *");
            }
        }
        sourceSql.append(" from ").append(dbTableMap.get("left_database")).append(dbTableMap.get("left_table")).append(" where ").append(filters.get("left_table"));
        targetSql.append(" from ").append(dbTableMap.get("right_database")).append(dbTableMap.get("right_table")).append(" where ").append(filters.get("right_table"));

        if (sourceConnect != null && sourceConnect.size() > 0) {
            String host = (String) sourceConnect.get("host");
            String port = (String) sourceConnect.get("port");
            String user = (String) sourceConnect.get("username");
            String pwd = (String) sourceConnect.get("password");
            String dataType = (String) sourceConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfVariableName)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);

            envName.append("[").append((String) sourceConnect.get("envName")).append("]");
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
        }
        if (targetConnect != null && targetConnect.size() > 0) {
            String host = (String) targetConnect.get("host");
            String port = (String) targetConnect.get("port");
            String user = (String) targetConnect.get("username");
            String pwd = (String) targetConnect.get("password");
            String dataType = (String) targetConnect.get("dataType");
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()).replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfVariableName)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
            transformSql.add(str);
            envName.append("[").append((String) targetConnect.get("envName")).append("]");
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
        }
    }

    private void fuleLineToHashLine(List<String> transformSql, String partOfVariableName, StringBuilder tmpRegisterTableLeft, StringBuilder tmpRegisterTableRight) {
        transformSql.add("val fillNullDFLeft_" + partOfVariableName + " = originalDFLeft_" + partOfVariableName + ".na.fill(UUID)");
        transformSql.add("val qualitis_names_left_" + partOfVariableName + " = fillNullDFLeft_" + partOfVariableName + ".schema.fieldNames");
        transformSql.add("val fillNullWithFullLineWithHashDF_left_" + partOfVariableName + " = fillNullDFLeft_" + partOfVariableName + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
        transformSql.add("val qualitis_names_left_" + partOfVariableName + "_buffer = qualitis_names_left_" + partOfVariableName + ".toBuffer");

        transformSql.add("val fillNullDFRight_" + partOfVariableName + " = originalDFRight_" + partOfVariableName + ".na.fill(UUID)");
        transformSql.add("val qualitis_names_right_" + partOfVariableName + " = fillNullDFRight_" + partOfVariableName + ".schema.fieldNames");
        transformSql.add("val fillNullWithFullLineWithHashDF_right_" + partOfVariableName + " = fillNullDFRight_" + partOfVariableName + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
        transformSql.add("val qualitis_names_right_" + partOfVariableName + "buffer = qualitis_names_right_" + partOfVariableName + ".toBuffer");

        transformSql.add("val finalDF_left_" + partOfVariableName + " = fillNullWithFullLineWithHashDF_left_" + partOfVariableName + ".drop(qualitis_names_left_" + partOfVariableName + ":_*)");
        transformSql.add("val finalDF_right_" + partOfVariableName + " = fillNullWithFullLineWithHashDF_right_" + partOfVariableName + ".drop(qualitis_names_right_" + partOfVariableName + ":_*)");

        tmpRegisterTableLeft.append("md5_table_left_" + partOfVariableName);
        tmpRegisterTableRight.append("md5_table_right_" + partOfVariableName);
        transformSql.add("finalDF_left_" + partOfVariableName + ".registerTempTable(\"" + tmpRegisterTableLeft.toString() + "\")");
        transformSql.add("finalDF_right_" + partOfVariableName + ".registerTempTable(\"" + tmpRegisterTableRight.toString() + "\")");
    }

    private List<String> getImportSql() {
        List<String> imports = new ArrayList<>();
        imports.add("import org.apache.spark.sql.types._");
        imports.add("import org.apache.spark.sql.functions._");
        return imports;
    }

    private List<String> saveStatisticAndSaveMySqlSentence(String workFlowVersion, Long ruleId, Map<String, Long> ruleMetricIds
            , Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, List<RuleVariable> ruleVariables, String createTime
            , String partOfVariableName, String runDate, String user, StringBuilder realColumn, boolean enumListNewValue, boolean numRangeNewValue,
                                                           Map<String, String> selectResult, boolean unionAllForSaveResult) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        return abstractTranslator.persistenceTranslate(workFlowVersion, ruleId, ruleMetricIds, templateStatisticsInputMetas, applicationId, ruleVariables, createTime
                , partOfVariableName, runDate, user, realColumn, enumListNewValue, numRangeNewValue, selectResult, unionAllForSaveResult);
    }

    /**
     * Generate scala code of select statement and save into hive database
     *
     * @param sql
     * @param saveTableName
     * @param rule
     * @param partOfVariableName
     * @param connParamMaps
     * @param runDate
     * @param selectResult
     * @param midTableReUse
     * @param unionAllForSaveResult
     * @param filterFields
     * @param tableEnvs
     * @param shareConnect
     * @param shareFromPart
     * @return
     */
    private List<String> generateSparkSqlAndSaveSentence(String sql, String saveTableName, Rule rule, String partOfVariableName, List<Map<String, Object>> connParamMaps
        , String runDate, Map<String, String> selectResult, boolean midTableReUse, boolean unionAllForSaveResult, String filterFields, List<List<String>> tableEnvs, boolean shareConnect, String shareFromPart) {
        String sparkSqlSentence;
        List<String> sparkSqlList = new ArrayList<>();
        boolean linePrimaryRepeat = QualitisConstants.EXPECT_LINES_NOT_REPEAT_ID.equals(rule.getTemplate().getId()) || QualitisConstants.EXPECT_DATA_NOT_REPEAT_ID.equals(rule.getTemplate().getId());
        if (CollectionUtils.isEmpty(connParamMaps)) {

            if (CollectionUtils.isNotEmpty(tableEnvs)) {
                List<List<String>> sqlReplaceStrLists = QualitisCollectionUtils.getDescartes(tableEnvs);

                for (List<String> subList : sqlReplaceStrLists) {
                    StringBuilder envName = new StringBuilder();
                    for (String replaceStr : subList) {
                        String[] subStrs = replaceStr.split(SpecCharEnum.COLON.getValue());
                        envName.append("[").append(subStrs[2]).append("]");
                        String registerTable = subStrs[1];
                        String realTable = subStrs[0];

                        sql = sql.replace(realTable, registerTable);
                    }
                    String partOfVariableNameWithEnv = partOfVariableName + envName.toString().replace("[", "").replace("]", "");
                    sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                    sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableNameWithEnv, "", "", "");
                    sparkSqlList.add(sparkSqlSentence);

                    String variableFormer = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                    String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                    formatSchema(sparkSqlList, partOfVariableName, variableFormer, variableLatter);

                    selectResult.put(variableLatter, envName.toString());
                }
                String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                unionAllSaveResult(lastVariable, selectResult, sparkSqlList, unionAllForSaveResult);
            } else {
                if (linePrimaryRepeat) {
                    sparkSqlList.add("val UUID = java.util.UUID.randomUUID.toString");
                }
                sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableName, filterFields, shareFromPart, "");
                LOGGER.info("Succeed to generate spark sql. sentence: {}", sparkSqlSentence);
                sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                sparkSqlList.add(sparkSqlSentence);

                if (linePrimaryRepeat) {
                    handleLinePrimaryRepeat(sparkSqlList, partOfVariableName);
                }

                String variableFormer = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                formatSchema(sparkSqlList, partOfVariableName, variableFormer, variableLatter);

                if (Boolean.TRUE.equals(rule.getTemplate().getSaveMidTable())) {
                    sparkSqlList.addAll(getSaveMidTableSentenceSettings());
                    sparkSqlList.addAll(getSaveMidTableSentence(saveTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, midTableReUse));
                    LOGGER.info("Succeed to generate spark sql. sentence.");
                }
            }

            return sparkSqlList;
        } else {
            // Repeat with envs. When polymerization, repeat one more time.
            selectResult.putAll(getSparkSqlSententceWithMysqlConnParams(sql, partOfVariableName, connParamMaps, sparkSqlList, linePrimaryRepeat, rule.getTemplate().getSaveMidTable(), saveTableName, runDate, midTableReUse, unionAllForSaveResult, filterFields, shareConnect, shareFromPart));
        }
        return sparkSqlList;
    }

    private void handleLinePrimaryRepeat(List<String> sparkSqlList, Integer count) {
        sparkSqlList.add("val fillNullDF_" + count + " = " + getVariableName(count) + ".na.fill(UUID)");
        sparkSqlList.add("val fillNullWithFullLineWithHashDF_" + count + " = fillNullDF_" + count + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"md5\", md5(to_json(struct($\"*\"))))");
        sparkSqlList.add("fillNullWithFullLineWithHashDF_" + count + ".registerTempTable(\"tmp_table_" + count + "\")");
        sparkSqlList.add("val " + getVariableName(count) + " = spark.sql(\"select md5, count(1) as md5_count from tmp_table_" + count + " group by md5 having count(*) > 1\")");
    }

    private void handleLinePrimaryRepeat(List<String> sparkSqlList, String fullName) {
        String suffix = fullName.split(SpecCharEnum.EQUAL.getValue())[1];
        sparkSqlList.add("val fillNullDF_" + suffix + " = " + getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], suffix) + ".na.fill(UUID)");
        sparkSqlList.add("val fillNullWithFullLineWithHashDF_" + suffix + " = fillNullDF_" + suffix + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"md5\", md5(to_json(struct($\"*\"))))");
        sparkSqlList.add("fillNullWithFullLineWithHashDF_" + suffix + ".registerTempTable(\"tmp_table_" + suffix + "\")");
        sparkSqlList.add("val " + getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], suffix) + " = spark.sql(\"select md5, count(1) as md5_count from tmp_table_" + suffix + " group by md5 having count(*) > 1\")");
    }

    private Map<String, String> getSparkSqlSententceWithMysqlConnParams(String sql, String partOfVariableName, List<Map<String, Object>> connParamMaps, List<String> sparkSqlList
        , boolean linePrimaryRepeat, Boolean saveMidTable, String saveTableName, String runDate, boolean midTableReUse, boolean unionAllForSaveResult, String filterFields, boolean shareConnect, String shareFromPart) {
        Map<String, String> selectResult = new HashMap<>(connParamMaps.size());
        for (Map<String, Object> connParams : connParamMaps) {
            String envName = (String) connParams.get("envName");
            if (StringUtils.isEmpty(envName)) {
                continue;
            }
            String tmpVariableName = partOfVariableName + envName;
            String variableFormer = getVariableNameByRule(tmpVariableName.split(SpecCharEnum.EQUAL.getValue())[0], tmpVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);

            if (shareConnect) {
                sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "，在环境 " + envName + " 的校验查询代码");
                sparkSqlList.add(getSparkSqlSentence(sql, tmpVariableName, filterFields, shareFromPart, SpecCharEnum.BOTTOM_BAR.getValue() + envName));
            } else {

                String tmp = sql.replace("\"", "\\\"");
                String host = (String) connParams.get("host");
                String port = (String) connParams.get("port");
                String pwd = (String) connParams.get("password");
                String user = (String) connParams.get("username");
                String dataType = (String) connParams.get("dataType");
                String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, tmp)
                    .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                    .replace("${MYSQL_IP}", host)
                    .replace("${MYSQL_PORT}", port)
                    .replace("${MYSQL_USER}", user)
                    .replace("${MYSQL_PASSWORD}", pwd);
                sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                sparkSqlList.add(str.replace(VARIABLE_NAME_PLACEHOLDER, variableFormer));
            }
            if (linePrimaryRepeat) {
                handleLinePrimaryRepeat(sparkSqlList, tmpVariableName);
            }

            String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), tmpVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
            selectResult.put(variableLatter, (String) connParams.get("envName"));
            formatSchema(sparkSqlList, tmpVariableName, variableFormer, variableLatter);
        }
        String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
        unionAllSaveResult(lastVariable, selectResult, sparkSqlList, unionAllForSaveResult);
        LOGGER.info("Succeed to generate spark sql. sentence.");

        if (saveMidTable) {
            sparkSqlList.addAll(getSaveMidTableSentenceSettings());
            sparkSqlList.addAll(getSaveMidTableSentence(saveTableName, runDate, midTableReUse, selectResult));
        }

        return selectResult;
    }

    private void unionAllSaveResult(String lastVariable, Map<String, String> selectResult, List<String> sparkSqlList, boolean unionAllForSaveResult) {
        if (selectResult.size() > 1 && unionAllForSaveResult) {
            StringBuilder saveUnion = new StringBuilder();
            boolean firstVar = true;
            for (String varName : selectResult.keySet()) {
                if (firstVar) {
                    saveUnion.append("val ").append(lastVariable).append(" = ").append(varName);
                    firstVar = false;
                } else {
                    saveUnion.append(".unionAll(").append(varName).append(")");
                }
            }

            selectResult.clear();
            selectResult.put(lastVariable, QualitisConstants.UNION_ALL);
            sparkSqlList.add(saveUnion.toString());
            int index = sparkSqlList.indexOf("Qualitis System Code Dividing Line");
            while (index != -1) {
                sparkSqlList.remove(index);
                index = sparkSqlList.indexOf("Qualitis System Code Dividing Line");
            }
        } else {
            sparkSqlList.add("Qualitis System Code Dividing Line");
        }
    }

    private List<String> getSaveMidTableSentence(String saveMidTableName, String runDate, boolean midTableReUse, Map<String, String> selectResult) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date monday = calendar.getTime();
        Date date = new Date();

        String beforeDay = format.format(monday);

        List<String> saveSqls = new ArrayList<>();


        if (!midTableReUse) {
            saveSqls.add("spark.sql(\"DROP TABLE IF EXISTS " + saveMidTableName + "\")");
        }

        saveSqls.add(IF_EXIST.replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName));

        // Delete 7 days before partition
        String getPartitions = "val partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + " = spark.sql(\"select qualitis_partition_key from " + saveMidTableName + " where (qualitis_partition_key < " + beforeDay + ")\").map(f=>f.getString(0)).collect.toList";
        saveSqls.add(getPartitions);
        String foreachDrop = "partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + ".foreach(f => spark.sql(\"alter table " + saveMidTableName + " drop if exists partition (qualitis_partition_key=\" + f + \")\"))";
        saveSqls.add(foreachDrop);
        for (Map.Entry<String, String> entry : selectResult.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION_WITH_ENV, value, key, saveMidTableName, runDate, date, format));
        }
        saveSqls.add(ELSE_EXIST);
        for (Map.Entry<String, String> entry : selectResult.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE_WITH_ENV, value, key, saveMidTableName, runDate, date, format));
        }
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private void formatSchema(List<String> sparkSqlList, String variableFormer, String variableLatter) {
        String str1 = "val schemas = " + variableFormer + ".schema.fields.map(f => f.name).toList";
        String str2 = "val newSchemas = schemas.map(s => s.replaceAll(\"[()]\", \"\")).toList";
        String str3 = "val " + variableLatter + " = " + variableFormer + ".toDF(newSchemas: _*)";
        sparkSqlList.add(str1);
        sparkSqlList.add(str2);
        sparkSqlList.add(str3);
    }

    private void formatSchema(List<String> sparkSqlList, String prefix, String variableFormer, String variableLatter) {
        if (QualitisConstants.BDAP.equals(localConfig.getCluster())) {
            prefix = prefix.split(SpecCharEnum.EQUAL.getValue())[1];
            String str1 = "val " + prefix + "_schemas = " + variableFormer + ".schema.fields.map(f => f.name).toList";
            String str2 = "val " + prefix + "_replacedSchemas = " + prefix + "_schemas.map(s => s.replaceAll(\"[()]\", \"\")).toList";
            String str3 = "val " + variableLatter + " = " + variableFormer + ".toDF(" + prefix + "_replacedSchemas: _*)";
            sparkSqlList.add(str1);
            sparkSqlList.add(str2);
            sparkSqlList.add(str3);
        } else {
            sparkSqlList.add("val " + variableLatter + " = " + variableFormer);
        }
    }

    private List<String> getSaveMidTableSentenceSettings() {
        List<String> settings = new ArrayList<>();
        settings.add("spark.sqlContext.setConf(\"hive.exec.dynamic.partition\", \"true\")");
        settings.add("spark.sqlContext.setConf(\"hive.exec.dynamic.partition.mode\", \"nonstrict\")");

        settings.add("spark.conf.set(\"spark.sql.sources.partitionOverwriteMode\",\"dynamic\")");
        return settings;
    }

    private List<String> getSaveMidTableSentence(String saveMidTableName, Integer count, String runDate, boolean midTableReUse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date monday = calendar.getTime();
        Date date = new Date();

        String beforeDay = format.format(monday);

        List<String> saveSqls = new ArrayList<>();

        if (!midTableReUse) {
            saveSqls.add("spark.sql(\"DROP TABLE IF EXISTS " + saveMidTableName + "\")");
        }

        saveSqls.add(IF_EXIST.replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName));

        // Delete 7 days before partition
        String getPartitions = "val partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + " = spark.sql(\"select qualitis_partition_key from " + saveMidTableName + " where (qualitis_partition_key < " + beforeDay + ")\").map(f=>f.getString(0)).collect.toList";
        saveSqls.add(getPartitions);
        String foreachDrop = "partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + ".foreach(f => spark.sql(\"alter table " + saveMidTableName + " drop if exists partition (qualitis_partition_key=\" + f + \")\"))";
        saveSqls.add(foreachDrop);

        saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION, "", getVariableName(count), saveMidTableName, runDate, date, format));
        saveSqls.add(ELSE_EXIST);

        saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE, "", getVariableName(count), saveMidTableName, runDate, date, format));
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private List<String> getSaveMidTableSentence(String saveMidTableName, String partOfVariableName, String runDate, boolean midTableReUse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date monday = calendar.getTime();
        Date date = new Date();

        String beforeDay = format.format(monday);

        List<String> saveSqls = new ArrayList<>();

        if (!midTableReUse) {
            saveSqls.add("spark.sql(\"DROP TABLE IF EXISTS " + saveMidTableName + "\")");
        }

        saveSqls.add(IF_EXIST.replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName));

        // Delete 7 days before partition
        String getPartitions = "val partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + " = spark.sql(\"select qualitis_partition_key from " + saveMidTableName + " where (qualitis_partition_key < " + beforeDay + ")\").map(f=>f.getString(0)).collect.toList";
        saveSqls.add(getPartitions);
        String foreachDrop = "partition_list_" + saveMidTableName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), SpecCharEnum.BOTTOM_BAR.getValue()) + ".foreach(f => spark.sql(\"alter table " + saveMidTableName + " drop if exists partition (qualitis_partition_key=\" + f + \")\"))";
        saveSqls.add(foreachDrop);

        saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION, "", getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName), saveMidTableName, runDate, date, format));
        saveSqls.add(ELSE_EXIST);

        saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE, "", getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName), saveMidTableName, runDate, date, format));
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private List<String> parsefirstHalf(String saveMidTableSentenceTemplateInsertOverwritePartition, String envName, String val, String saveMidTableName
            , String runDate, Date date, SimpleDateFormat format) {
        List<String> saveSqls = new ArrayList<>();

        if (StringUtils.isEmpty(envName) && StringUtils.isEmpty(val)) {
            saveSqls.add(saveMidTableSentenceTemplateInsertOverwritePartition
                    .replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
                    .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        } else {
            saveSqls.add(saveMidTableSentenceTemplateInsertOverwritePartition
                    .replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
                    .replace("${QUALITIS_PARTITION_KEY_ENV}", envName)
                    .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        }

        return saveSqls;
    }

    private List<String> parseSecondHalf(String saveMidTableSentenceTemplateCreate, String envName, String val, String saveMidTableName, String runDate
            , Date date, SimpleDateFormat format) {
        List<String> saveSqls = new ArrayList<>();
        if (StringUtils.isEmpty(envName) && StringUtils.isEmpty(val)) {
            saveSqls.add(
                    saveMidTableSentenceTemplateCreate.replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
                            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        } else {
            saveSqls.add(
                    saveMidTableSentenceTemplateCreate.replace("${QUALITIS_PARTITION_KEY}", StringUtils.isBlank(runDate) ? format.format(date) : runDate)
                            .replace("${QUALITIS_PARTITION_KEY_ENV}", envName)
                            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        }
        return saveSqls;
    }

    private String getSparkSqlSentence(String sql, Integer count, String filterFields) {
        sql = sql.replace("\"", "\\\"");
        String str = SPARK_SQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql);
        if (StringUtils.isNotEmpty(filterFields)) {
            str += filterFields;
        }
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableName(count));
    }

    private String getSparkSqlSentence(String sql, String fullName, String filterFields, String shareFromPart, String envName) {
        sql = sql.replace("\"", "\\\"");

        if (StringUtils.isNotEmpty(shareFromPart)) {
            sql = sql.replace(shareFromPart, commonTableName + envName);
        }
        String str = SPARK_SQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql);
        if (StringUtils.isNotEmpty(filterFields)) {
            str += filterFields;
        }
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], fullName.split(SpecCharEnum.EQUAL.getValue())[1]));
    }

    /**
     * Replace all placeholder of template sql
     *
     * @param template
     * @param variables
     * @param filter
     * @param realFilter
     * @param realColumn
     * @param dbTableMap             for pick up source db.table & target db.table
     * @param date
     * @return
     * @throws ConvertException
     */
    private String replaceVariable(String template, List<RuleVariable> variables, String filter, StringBuilder realFilter, StringBuilder realColumn
            , Map<String, String> dbTableMap, Date date, String createUser) throws ConvertException, UnExpectedRequestException, MetaDataAcquireFailedException {

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
            // GeT left db and table, right db and table.
            if ("left_database".equals(midInputMetaPlaceHolder)) {
                if (StringUtils.isNotBlank(ruleVariable.getValue())) {
                    dbTableMap.put("left_database", ruleVariable.getValue() + ".");
                } else {
                    dbTableMap.put("left_database", "");
                }
            } else if ("left_table".equals(midInputMetaPlaceHolder)) {
                dbTableMap.put("left_table", ruleVariable.getValue());
            } else if ("right_table".equals(midInputMetaPlaceHolder)) {
                dbTableMap.put("right_table", ruleVariable.getValue());
            } else if ("right_database".equals(midInputMetaPlaceHolder)) {
                if (StringUtils.isNotBlank(ruleVariable.getValue())) {
                    dbTableMap.put("right_database", ruleVariable.getValue() + ".");
                } else {
                    dbTableMap.put("right_database", "");
                }
            } else if (TemplateInputTypeEnum.FIELD.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType()) && Boolean.TRUE.equals(ruleVariable.getTemplateMidTableInputMeta().getFieldMultipleChoice())) {
                realColumn.append(ruleVariable.getValue());
            }
            // Fix issue of wedget node in the front.
            if ("\\$\\{fields}".equals(placeHolder)) {
                Matcher matcher = AGGREGATE_FUNC_PATTERN.matcher(ruleVariable.getValue());
                while (matcher.find()) {
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

        // Fix rule history
        String contrastType = "{contrast_type}";
        if (sqlAction.contains(SpecCharEnum.DOLLAR.getValue() + contrastType)) {
            sqlAction = sqlAction.replaceAll("\\$\\{contrast_type}", "FULL OUTER JOIN");
        }

        if (PLACEHOLDER_PATTERN.matcher(sqlAction).matches()) {
            throw new ConvertException("Unable to convert SQL, replacing placeholders failed, still having placeholder. sql: " + sqlAction);
        }

        return sqlAction;
    }

    /**
     * Get tmp variable name
     *
     * @param count
     * @return
     */
    public String getVariableName(Integer count) {
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
