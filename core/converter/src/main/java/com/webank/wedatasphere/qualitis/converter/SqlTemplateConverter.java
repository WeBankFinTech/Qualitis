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

//import com.webank.bsp.dpc.entity.AccountInfoSys;
//import com.webank.bsp.dpc.util.AccountInfoObtainer;

import com.webank.wedatasphere.qualitis.EngineTypeEnum;
import com.webank.wedatasphere.qualitis.LocalConfig;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.FpsColumnInfo;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.config.DpmConfig;
import com.webank.wedatasphere.qualitis.config.FpsConfig;
import com.webank.wedatasphere.qualitis.config.TaskDataSourceConfig;
import com.webank.wedatasphere.qualitis.constant.*;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.constant.RuleConstraintEnum;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.rule.constant.*;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.translator.AbstractTranslator;
import com.webank.wedatasphere.qualitis.util.*;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Value("${task.persistent.dbName}")
    private String resultDbName;
    @Value("${task.persistent.tableName}")
    private String resultTableName;
    @Value("${task.new_value.tableName}")
    private String newValueTableName;

    @Autowired
    private StandardValueVersionDao standardValueVersionDao;
    @Autowired
    private DataStandardClient dataStandardClient;
    @Value("${linkis.sql.communalTableName:common_table}")
    private String commonTableName;

    @Value("${intellect.check.project_name:}")
    private String intellectCheckProjectName;

    @Value("${intellect.check.fields_count_project_name:}")
    private String intellectCheckFieldsProjectName;

    @Value("${intellect.check.table_collect_template_name}")
    private String intellectCheckTableTemplateName;

    @Value("${intellect.check.enum_collect_template_name}")
    private String intellectCheckEnumTemplateName;

    @Value("${intellect.check.origin_collect_template_name}")
    private String intellectCheckOriginTemplateName;

    @Value("${intellect.check.total_collect_template_name}")
    private String intellectCheckTotalTemplateName;

    @Value("${task.execute.trino_column_size:100}")
    private Integer trinoColumnSize;

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(".*\\$\\{(.*)}.*");
    private static final Pattern AGGREGATE_FUNC_PATTERN = Pattern.compile("[a-zA-Z]+\\([0-9a-zA-Z_]+\\)");
    private static final String FRONT_HALF = "[ `~!@#$%^&*()+=|{}':;',\\[\\]";
    private static final String POSTERIOR_HALF = "<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
    private static final Pattern MID_TABLE_NAME_PATTERN = Pattern.compile(FRONT_HALF + POSTERIOR_HALF);

    private static final String SAVE_MID_TABLE_NAME_PLACEHOLDER = "${TABLE_NAME}";
    private static final String SPARK_SQL_TEMPLATE_PLACEHOLDER = "${SQL}";
    public static final String VARIABLE_NAME_PLACEHOLDER = "${VARIABLE}";
    private static final String DATABASE_PLACEHOLDER = "${database}";
    private static final String TABLE_PLACEHOLDER = "${table}";
    private static final String FILTER_PLACEHOLDER = "${filter}";
    private static final String FILTER_LEFT_PLACEHOLDER = "${filter_left}";
    private static final String FILTER_RIGHT_PLACEHOLDER = "${filter_right}";
    private static final Integer SINGLE_RULE = 1;
    private static final Integer CUSTOM_RULE = 2;
    private static final Integer MUL_SOURCE_RULE = 3;

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
     * @param runToday
     * @param clusterType
     * @param dataSourceMysqlConnect
     * @param user
     * @param leftCols
     * @param rightCols
     * @param complexCols
     * @param createUser
     * @param projectId
     * @return
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     */
    @Override
    public DataQualityJob convert(DataQualityTask dataQualityTask, Date date, String setFlag, Map<String, String> execParams, String runDate
            , String runToday, String clusterType, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user, List<String> leftCols, List<String> rightCols, List<String> complexCols, String createUser, Long projectId) throws Exception {

        boolean withSpark = Boolean.FALSE.equals(taskDataSourceConfig.getHiveSortUdfOpen());
        LOGGER.info("Start to convert template to actual code, task: " + dataQualityTask);
        if (null == dataQualityTask || dataQualityTask.getRuleTaskDetails().isEmpty()) {
            throw new DataQualityTaskException("Task can not be null or empty");
        }
        DataQualityJob job = new DataQualityJob();
        job.setTaskId(dataQualityTask.getTaskId());
        job.setIndex(dataQualityTask.getIndex());

        if (StringUtils.isNotBlank(setFlag)) {
            LOGGER.info("Start to solve with set flag. Spark set conf string: {}", setFlag);
            String[] setStrs = setFlag.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : setStrs) {
                job.getJobCode().add("spark.sql(\"SET " + str + "\")");
            }
            LOGGER.info("Finish to solve with set flag.");
        }
        String engineType = EngineTypeEnum.DEFAULT_ENGINE.getMessage();
        String startupParam = dataQualityTask.getStartupParam();
        String queueName = "";
        boolean engineReUse = true;
        boolean midTableReUse = true;
        int unionWay = 0;
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
                if ("union_way".equals(key)) {
                    unionWay = Integer.valueOf(value);
                    startupParam = startupParam.replace("union_way=" + value, "");
                }

                if (QualitisConstants.QUALITIS_ENGINE_TYPE.equals(key) && !EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(value)) {
                    engineType = value;
                    startupParam = startupParam.replace(QualitisConstants.QUALITIS_ENGINE_TYPE + SpecCharEnum.EQUAL.getValue() + value, "");
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
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_UNION_WAY)) {
            unionWay = Integer.valueOf(execParams.getOrDefault(QualitisConstants.QUALITIS_UNION_WAY, UnionWayEnum.NO_COLLECT_CALCULATE.getCode().toString()));
        }
        if (execParams.keySet().contains(QualitisConstants.QUALITIS_ENGINE_TYPE) && !EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(execParams.get(QualitisConstants.QUALITIS_ENGINE_TYPE))) {
            engineType = execParams.get(QualitisConstants.QUALITIS_ENGINE_TYPE);
        }
        List<String> initSentence = abstractTranslator.getInitSentence();
        if (! EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) || withSpark) {
            job.getJobCode().addAll(initSentence);
            job.getJobCode().add("spark.sql(\"SET spark.sql.hive.convertMetastoreOrc=false\")");
        }

        List<String> envNames = new ArrayList<>();
        boolean shareConnect = CollectionUtils.isNotEmpty(dataQualityTask.getConnectShare());
        List<String> communalSentence = new ArrayList<>();
        if (! EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) || withSpark) {
            communalSentence = getCommunalSentence(dataQualityTask, envNames, runDate, runToday, engineType);
            job.getJobCode().addAll(communalSentence);
        }

        int count = 0;
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            if (Objects.nonNull(ruleTaskDetail.getRule().getUnionWay())) {
                unionWay = ruleTaskDetail.getRule().getUnionWay();
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
            if (CollectionUtils.isEmpty(complexCols) && CollectionUtils.isEmpty(fpsCodes) && !withSpark && taskDataSourceConfig.getMysqlsecOpen()) {
                boolean generated = false;
                if (EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(engineType) && QualitisConstants.MULTI_SOURCE_FULL_TEMPLATE_NAME.equals(ruleTaskDetail.getRule().getTemplate().getEnName())
                        && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(ruleTaskDetail.getRule().getTemplate().getTemplateType())
                        && CollectionUtils.isEmpty(dataSourceMysqlConnect.keySet())) {
                    job.getJobCode().clear();
                    // Hql
                    List<String> codes = generateShellSqlByTask(ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), dataQualityTask.getCreateTime(), new StringBuilder(dataQualityTask.getPartition()), count, runDate, runToday, currentRuleLeftCols, currentRuleRightCols, complexCols, queueName, createUser);
                    job.setEngineType(EngineTypeEnum.DEFAULT_ENGINE.getMessage());
                    job.getJobCode().addAll(codes);
                    generated = true;
                } else if (EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) && ! QualitisConstants.isAcrossCluster(ruleTaskDetail.getRule().getTemplate().getEnName())) {
                    // Tsql
                    List<String> codes = generateTrinoSqlByTask(job, ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), dataQualityTask.getCreateTime(), new StringBuilder(dataQualityTask.getPartition()), execParams, runDate, runToday, currentRuleLeftCols, currentRuleRightCols, complexCols, queueName, createUser);
                    job.setEngineType(EngineTypeEnum.TRINO_ENGINE.getMessage());
                    job.getJobCode().addAll(codes);
                    generated = true;
                }
                if (generated) {
                    continue;
                }
            }
            String sharePart = "";
            if (StringUtils.isNotEmpty(dataQualityTask.getDbShare()) && StringUtils.isNotEmpty(dataQualityTask.getTableShare())) {
                sharePart = dataQualityTask.getDbShare() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + dataQualityTask.getTableShare();
            }
            List<String> codes = generateSparkSqlByTask(job, ruleTaskDetail.getRule(), date, dataQualityTask.getApplicationId(), ruleTaskDetail.getMidTableName()
                    , dataQualityTask.getCreateTime(), new StringBuilder(dataQualityTask.getPartition()), execParams, runDate, runToday, dataSourceMysqlConnect, user, midTableReUse
                    , unionWay, currentRuleLeftCols, currentRuleRightCols, complexCols, createUser, shareConnect, sharePart);
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
        job.setStartupParam(startupParam);
        job.setEngineReuse(engineReUse);
        return job;
    }

    private List<String> getCommunalSentence(DataQualityTask dataQualityTask, List<String> envNames, String runDate, String runToday, String engineType) throws UnExpectedRequestException {
        List<String> sqlList = new ArrayList<>();
        if (StringUtils.isEmpty(dataQualityTask.getDbShare()) || StringUtils.isEmpty(dataQualityTask.getTableShare())) {
            return sqlList;
        }
        // 自定义规则不使用公共缓存
        try {
            Rule rule = dataQualityTask.getRuleTaskDetails().get(0).getRule();
            if(compareProjectName(rule)){
                return sqlList;
            }
            if(StringUtils.isNotBlank(intellectCheckFieldsProjectName) &&
                    intellectCheckFieldsProjectName.equals(rule.getProject().getName())){
                return sqlList;
            }

        } catch (Exception e) {
            LOGGER.error("compare projectName error. can't get rule info");
        }
        List<String> columnList = new ArrayList<>();
        String filterPart = dataQualityTask.getFilterShare();
        if (StringUtils.isNotEmpty(filterPart) && StringUtils.isNotEmpty(runDate)) {
            filterPart = filterPart.replace("${run_date}", runDate);
//            filterPart = filterPart.replace("${run_date_std}", runDate);
        }
        if (StringUtils.isNotEmpty(filterPart) && StringUtils.isNotEmpty(runToday)) {
            filterPart = filterPart.replace("${run_today}", runToday);
//            filterPart = filterPart.replace("${run_today_std}", runToday);
        }

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
                // Remove ip of dcn
                envName = envName.split(SpecCharEnum.MINUS.getValue())[0];

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
            , String runDate, String runToday, List<String> leftCols, List<String> rightCols, List<String> complexCols, String queueName, String createUser) throws Exception {

        List<String> sqlList = new ArrayList<>();

        // Get input meta from template
        List<RuleVariable> inputMetaRuleVariables = rule.getRuleVariables().stream().filter(ruleVariable -> ruleVariable.getInputActionStep().equals(InputActionStepEnum.TEMPLATE_INPUT_META.getCode())).collect(Collectors.toList());
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n", " ");
        Map<String, Long> ruleMetricMap = collectRuleMetric(rule);
        Map<String, String> dbTableMap = new HashMap<>(4);
        Map<String, String> filters = new HashMap<>(2);
        StringBuilder realColumn = new StringBuilder();

        templateMidTableAction = getMultiDatasourceFiltesAndUpdateMidTableAction(rule, templateMidTableAction, date, filters, leftCols, rightCols, complexCols, null, runDate, runToday, false);
        replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realColumn, dbTableMap, date, rule.getStandardValueVersionId(), createUser, "", runDate, runToday, EngineTypeEnum.DEFAULT_ENGINE.getMessage());

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
        String hiveSql = "count_result_" + count + "=`hive -e \"" + setQueue.toString() + createFunc.toString() + "select count(1) as diff_count from (select line_md5, count(1) as md5_count from (select md5(concat_ws(''," + leftConcat.toString() + ")) as line_md5 from " + dbTableMap.get("left_database") + dbTableMap.get("left_table") + " where " + filters.get("left_table") + ") left_tmp group by left_tmp.line_md5) qulaitis_left_tmp ${contrast_type} (select line_md5, count(1) as md5_count from (select md5(concat_ws(''," + rightConcat.toString() + ")) as line_md5 from " + dbTableMap.get("right_database") + dbTableMap.get("right_table") + " where " + filters.get("right_table") + ") right_tmp group by right_tmp.line_md5) qulaitis_right_tmp ON (qulaitis_left_tmp.line_md5 = qulaitis_right_tmp.line_md5 AND qulaitis_left_tmp.md5_count = qulaitis_right_tmp.md5_count) where (qulaitis_left_tmp.line_md5 is null AND qulaitis_left_tmp.md5_count is null) OR (qulaitis_right_tmp.line_md5 is null AND qulaitis_right_tmp.md5_count is null) ${outer_filter};\"`";
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
            if (StringUtils.isNotBlank(runToday)) {
                runDate = runToday;
            } else {
                runDate = "-1";
            }
        }
        String insertSql = "sql_" + count + "=\"INSERT INTO qualitis_application_task_result (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) VALUES('" + applicationId + "', '" + createTime + "', 'Long', " + rule.getId() + ", $count_value_" + count + ", -1, " + runDate + ", '" + ruleVersion + "');\"";
        if (CollectionUtils.isNotEmpty(ruleMetricMap.values())) {
            insertSql = "sql_" + count + "=\"INSERT INTO qualitis_application_task_result (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) VALUES('" + applicationId + "', '" + createTime + "', 'Long', " + rule.getId() + ", $count_value_" + count + ", " + ruleMetricMap.values().iterator().next() + ", " + runDate + ", '" + ruleVersion + "');\"";
        }
        sqlList.add(insertSql);
        sqlList.add("result=\"$($MYSQL -e\"$sql_" + count + "\")\"");
        return sqlList;
    }

    private List<String> generateTrinoSqlByTask(DataQualityJob job, Rule rule, Date date, String applicationId, String createTime, StringBuilder partition, Map<String, String> execParams, String runDate, String runToday, List<String> leftCols, List<String> rightCols, List<String> complexCols, String queueName, String createUser) throws UnExpectedRequestException, ConvertException, MetaDataAcquireFailedException {
        List<String> sqlList = new ArrayList<>();
        Map<String, String> filters = new HashMap<>(2);

        // Collect rule metric and build in save sentence sql.
        Map<String, Long> ruleMetricMap = collectRuleMetric(rule);

        // Get SQL from template after remove '\n'
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n", " ");
        // trino compatibility handling start
        LOGGER.info("Before handle trino schema: {}", templateMidTableAction);
        templateMidTableAction = templateMidTableAction.replace("${fields} not regexp '${regexp}'", "regexp_like(cast(${fields} as varchar), '${regexp}')");
        templateMidTableAction = templateMidTableAction.replace("trim(${fields}) = ''", "trim(cast(${fields} as varchar)) = ''");
        templateMidTableAction = templateMidTableAction.replace("concat_ws(',',${fields})", "concat_ws(',',cast(${fields} as varchar))");
        LOGGER.info("After handle trino schema: {}", templateMidTableAction);
        // trino compatibility handling end

        templateMidTableAction = templateMidTableAction.replace("${database}.${table}", "hive.${database}.${table}");
        templateMidTableAction = templateMidTableAction.replace("${left_database}.${left_table}", "hive.${left_database}.${left_table}");
        templateMidTableAction = templateMidTableAction.replace("${right_database}.${right_table}", "hive.${right_database}.${right_table}");

        if (MUL_SOURCE_RULE.intValue() == rule.getRuleType()) {
            if (QualitisConstants.MULTI_SOURCE_ACCURACY_TEMPLATE_NAME.equals(rule.getTemplate().getEnName())) {
                templateMidTableAction = templateMidTableAction.replace("hive.${left_database}.${left_table}", "(select * from " + "hive.${left_database}.${left_table} where " + "${filter_left})");
                templateMidTableAction = templateMidTableAction.replace("hive.${right_database}.${right_table}", "(select * from " + "hive.${right_database}.${right_table} where " + "${filter_right})");
                templateMidTableAction = "SELECT * from (" + templateMidTableAction + ") tmp3 where tmp3.compare_result = 0";
            }
            templateMidTableAction = getMultiDatasourceFiltesAndUpdateMidTableAction(rule, templateMidTableAction, date, filters, leftCols, rightCols, complexCols, job.getIndex(),
                    runDate, runToday, true);
        } else if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = customMidTableActionUpdate(rule, templateMidTableAction, date, execParams, partition, ruleMetricMap, runDate, runToday, EngineTypeEnum.TRINO_ENGINE.getMessage());
        }

        if (Boolean.TRUE.equals(rule.getTemplate().getFilterFields()) && RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType())) {
            String filterColName = rule.getRuleDataSources().stream().filter(dataSource -> dataSource.getDatasourceIndex() == null).iterator().next().getColName();
            List<String> filterColNameList = new ArrayList<>();
            if (StringUtils.isNotEmpty(filterColName)) {
                StringBuilder repeat = new StringBuilder();
                String[] realColumns = filterColName.split(SpecCharEnum.VERTICAL_BAR.getValue());
                for (String column : realColumns) {
                    String[] colInfo = column.split(SpecCharEnum.COLON.getValue());
                    String colName = colInfo[0];
                    filterColNameList.add("cast(" + colName + " as varchar)");
                }
                repeat.append("select md5, count(1) as md5_count from (select md5(cast(concat_ws('', ").append(Strings.join(filterColNameList, ',')).append(") as varbinary)) as md5 from (" + templateMidTableAction + ") template_source) template_group_source group by template_group_source.md5 having count(*) > 1");
                templateMidTableAction = repeat.toString();
            }
        }

        // Get statistics meta
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = rule.getTemplate().getStatisticAction();
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
        // Get template sql and replace all replaceholders
        String midTableAction = replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realColumn, dbTableMap, date, rule.getStandardValueVersionId(), createUser, "", runDate, runToday, EngineTypeEnum.TRINO_ENGINE.getMessage());

        if (QualitisConstants.SINGLE_CLUSTER_CUSTOM_TEMPLATE_NAME.equals(rule.getTemplate().getEnName())) {
            midTableAction = addHivePrefixToTables(midTableAction);
            midTableAction = "SELECT * from (" + midTableAction + ") tmp3 where tmp3.compare_result = 0";
        }

        StringBuilder trinoSql = new StringBuilder();
        trinoSql.append("insert into mysql").append(SpecCharEnum.PERIOD_NO_ESCAPE.getValue()).append(resultDbName).append(SpecCharEnum.PERIOD_NO_ESCAPE.getValue()).append(resultTableName);
        trinoSql.append(" (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) ");

        String ruleVersion = rule.getWorkFlowVersion() == null ? "" : rule.getWorkFlowVersion();

        if (StringUtils.isEmpty(runDate)) {
            if (StringUtils.isNotBlank(runToday)) {
                runDate = runToday;
            } else {
                runDate = "-1";
            }
        }
        Long ruleMetricId = -1L;

        if (ruleMetricMap != null && ruleMetricMap.size() > 0) {
            for (String key : ruleMetricMap.keySet()) {
                if (null != ruleMetricMap.get(key)) {
                    ruleMetricId = ruleMetricMap.get(key);
                }

                if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
                    key = key.replace("-", "_");
                    StringBuilder customTrinoSql = new StringBuilder(trinoSql.toString());
                    customTrinoSql.append("select ")
                            .append("'").append(applicationId).append("', ")
                            .append("'").append(createTime).append("', ")
                            .append("'").append("Long").append("', ")
                            .append(rule.getId()).append(", ")
                            .append("CAST(").append(key).append(" AS VARCHAR)").append(", ")
                            .append(ruleMetricId).append(", ")
                            .append(runDate).append(", ")
                            .append("'").append(ruleVersion).append("'")
                            .append(" from ").append("(")
                            .append(midTableAction)
                            .append(") tmp;");
                    sqlList.add(customTrinoSql.toString());
                }
            }
            if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
                return sqlList;
            }
        }

        trinoSql.append("select ")
                .append("'").append(applicationId).append("', ")
                .append("'").append(createTime).append("', ")
                .append("'").append("Long").append("', ")
                .append(rule.getId()).append(", ")
                .append("${value}").append(", ")
                .append(ruleMetricId).append(", ")
                .append(runDate).append(", ")
                .append("'").append(ruleVersion).append("'")
                .append(" from ").append("(")
                .append(midTableAction)
                .append(") tmp;");

        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : templateStatisticsInputMetas) {
            String functionName = templateStatisticsInputMeta.getFuncName();
            String value = templateStatisticsInputMeta.getValue();

            String tsql = trinoSql.toString().replace("${value}", "CAST(" + functionName + "(" + value + ")" + " AS VARCHAR)");
            sqlList.add(tsql);
        }

        // New value save
        Set<TemplateMidTableInputMeta> templateMidTableInputMetas = rule.getTemplate().getTemplateMidTableInputMetas();
        boolean saveNewValue = templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> Boolean.TRUE.equals(templateMidTableInputMeta.getWhetherNewValue()));
        if (saveNewValue) {
            StringBuilder basicNewValueSql = new StringBuilder();
            basicNewValueSql.append("insert into mysql").append(SpecCharEnum.PERIOD_NO_ESCAPE.getValue()).append(resultDbName).append(SpecCharEnum.PERIOD_NO_ESCAPE.getValue()).append(newValueTableName);
            basicNewValueSql.append(" (rule_id, status, create_user, rule_version, create_time, result_value) ");

            boolean numRangeNewValue = templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));
            boolean enumListNewValue = templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.LIST.getCode().equals(templateMidTableInputMeta.getInputType()) || TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));

            if (numRangeNewValue) {
                StringBuilder numRangeNewValueSql = new StringBuilder(basicNewValueSql.toString());
                numRangeNewValueSql.append("select ")
                                   .append(rule.getId()).append(", ")
                                   .append("1, ")
                                   .append("'").append(createUser).append("', ")
                                   .append("'").append(ruleVersion).append("', ")
                                   .append("'").append(createTime).append("', ")
                                   .append("CAST(custom_column AS VARCHAR)")
                                   .append(" from ").append("(")
                                   .append(midTableAction)
                                   .append(") tmp;");
                sqlList.add(numRangeNewValueSql.toString());
            }

            if (enumListNewValue) {
                StringBuilder numRangeNewValueSql = new StringBuilder(basicNewValueSql.toString());
                numRangeNewValueSql.append("select ")
                        .append(rule.getId()).append(", ")
                        .append("1, ")
                        .append("'").append(createUser).append("', ")
                        .append("'").append(ruleVersion).append("', ")
                        .append("'").append(createTime).append("', ")
                        .append("concat_ws(',', cast(" + realColumn.toString() + " as varchar))")
                        .append(" from ").append("(")
                        .append(midTableAction)
                        .append(") tmp;");
                sqlList.add(numRangeNewValueSql.toString());
            }
        }


        return sqlList;
    }

    private String addHivePrefixToTables(String midTableAction) throws ConvertException{
        try {
            Statement statement = CCJSqlParserUtil.parse(midTableAction);
            statement.accept(new StatementVisitorAdapter() {
                @Override
                public void visit(Select select) {
                    select.getSelectBody().accept(new SelectVisitorAdapter() {
                        @Override
                        public void visit(PlainSelect plainSelect) {
                            processFromItem(plainSelect.getFromItem());
                            if (plainSelect.getJoins() != null) {
                                plainSelect.getJoins().forEach(join -> processFromItem(join.getRightItem()));
                            }
                        }

                        @Override
                        public void visit(SetOperationList setOpList) {
                            for (SelectBody selectBody : setOpList.getSelects()) {
                                selectBody.accept(this);
                            }
                        }

                        @Override
                        public void visit(WithItem withItem) {
                            withItem.getSubSelect().getSelectBody().accept(this);
                        }
                    });
                }
            });
            return statement.toString();
        } catch (JSQLParserException e) {
            throw new ConvertException("Your sql cannot be parsed to get db and table, sql: " + midTableAction);
        }
    }

    private void processFromItem(FromItem fromItem) {
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            modifyTable(table);
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            subSelect.getSelectBody().accept(new SelectVisitorAdapter() {
                @Override
                public void visit(PlainSelect plainSelect) {
                    processFromItem(plainSelect.getFromItem());
                    if (plainSelect.getJoins() != null) {
                        plainSelect.getJoins().forEach(join -> processFromItem(join.getRightItem()));
                    }
                }

                @Override
                public void visit(SetOperationList setOpList) {
                    for (SelectBody selectBody : setOpList.getSelects()) {
                        selectBody.accept(this);
                    }
                }

                @Override
                public void visit(WithItem withItem) {
                    withItem.getSubSelect().getSelectBody().accept(this);
                }
            });
        }
    }

    private void modifyTable(Table table) {
        String originalTableName = table.getFullyQualifiedName();
        if (originalTableName.contains(".")) {
            String[] parts = originalTableName.split("\\.", 2);
            String dbName = parts[0];
            String tableName = parts[1];
            table.setSchemaName("hive." + dbName);
            table.setName(tableName);
        }
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
     * @param unionWay
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
            , StringBuilder partition, Map<String, String> execParams, String runDate, String runToday, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user
            , boolean midTableReUse, int unionWay, List<String> leftCols, List<String> rightCols, List<String> complexCols
            , String createUser, boolean shareConnect, String shareFromPart)
            throws ConvertException, RuleVariableNotSupportException, RuleVariableNotFoundException, UnExpectedRequestException, MetaDataAcquireFailedException {
        List<String> sqlList = new ArrayList<>();
        Map<String, String> filters = new HashMap<>(2);

        // Collect rule metric and build in save sentence sql.
        Map<String, Long> ruleMetricMap = collectRuleMetric(rule);

        // Get SQL from template after remove '\n'
        String templateMidTableAction = rule.getTemplate().getMidTableAction().replace("\n", " ");
        // Replace execution variable parameters
        if(compareProjectName(rule) || intellectCheckFieldsProjectName.equals(rule.getProject().getName())) {
            if (!MapUtils.isEmpty(execParams)) {
                for (Map.Entry< String, String > entry : execParams.entrySet()) {
                    String expressKey = entry.getKey();
                    String expressValue = entry.getValue();
                    templateMidTableAction = templateMidTableAction.replace("${" + expressKey + "}", expressValue);
                }
                if (execParams.containsKey("partition_attr") && execParams.containsKey("partition_day")) {
                    String filter = "";
                    if (execParams.get("partition_day").contains(",")) {
                        filter = execParams.get("partition_attr") + " in (" + execParams.get("partition_day") + ")";
                    } else {
                        filter = execParams.get("partition_attr") + " = " + execParams.get("partition_day");
                    }
                    templateMidTableAction = templateMidTableAction.replace("${partition_filter}", filter);
                }
            }
        }
        String templateEnName = StringUtils.isNotEmpty(rule.getTemplate().getEnName()) ? rule.getTemplate().getEnName() : "defaultCheckDF";

        if (MUL_SOURCE_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = getMultiDatasourceFiltesAndUpdateMidTableAction(rule, templateMidTableAction, date, filters, leftCols, rightCols, complexCols, job.getIndex(),
                    runDate, runToday, false);
        } else if (CUSTOM_RULE.intValue() == rule.getRuleType()) {
            templateMidTableAction = customMidTableActionUpdate(rule, templateMidTableAction, date, execParams, partition, ruleMetricMap, runDate, runToday, EngineTypeEnum.SPARK_ENGINE.getMessage());
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
        // Get template sql and replace all replaceholders
        String midTableAction = replaceVariable(templateMidTableAction, inputMetaRuleVariables, partition.toString(), realColumn, dbTableMap, date, rule.getStandardValueVersionId(), createUser, shareFromPart, runDate, runToday, EngineTypeEnum.SPARK_ENGINE.getMessage());

        // Prepare for multiple rule
        List<Map<String, Object>> sourceConnect = new ArrayList<>();
        List<Map<String, Object>> targetConnect = new ArrayList<>();
        prepareDecrptedConnectParamForMultipleRule(sourceConnect, targetConnect, dataSourceMysqlConnect, rule);

        Map<String, String> selectResult = new LinkedHashMap<>(rule.getRuleDataSources().size());
        String result = checkRuleNameWhetherContainSpecialCharacters(rule.getName());
        String partOfVariableName = templateEnName.replace(" ", "") + SpecCharEnum.EQUAL.getValue() + result;

        formatEnvNameForSpark(sourceConnect);
        formatEnvNameForSpark(targetConnect);

        handleRuleSelectSql(rule, midTableName, partition, partOfVariableName, runDate, runToday, dataSourceMysqlConnect, sqlList, filters, dbTableMap, midTableAction, sourceConnect, targetConnect, selectResult, midTableReUse, unionWay, leftCols, rightCols, complexCols, shareConnect, shareFromPart, execParams, job.getIndex(), job.getEngineType());

        Set<TemplateMidTableInputMeta> templateMidTableInputMetas = rule.getTemplate().getTemplateMidTableInputMetas();
        boolean saveNewValue = templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> Boolean.TRUE.equals(templateMidTableInputMeta.getWhetherNewValue()));
        boolean numRangeNewValue = saveNewValue && templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));
        boolean enumListNewValue = saveNewValue && templateMidTableInputMetas.stream().anyMatch(templateMidTableInputMeta -> TemplateInputTypeEnum.LIST.getCode().equals(templateMidTableInputMeta.getInputType()) || TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType()));
        sqlList.addAll(saveStatisticAndSaveMySqlSentence(rule.getWorkFlowVersion() != null ? rule.getWorkFlowVersion() : "", rule.getId(), ruleMetricMap, rule.getTemplate().getStatisticAction(), applicationId, job.getTaskId(), statisticsRuleVariables, createTime, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, user, realColumn, enumListNewValue, numRangeNewValue, selectResult, unionWay));
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

    private void formatEnvNameForSpark(List<Map<String, Object>> sourceConnect) {
        if (CollectionUtils.isEmpty(sourceConnect)) {
            return;
        }
        for (Map<String, Object> connectMap: sourceConnect) {
            if (connectMap.containsKey("envName")) {
                String envNameForSpark = sparkEnvNameAdapter((String) connectMap.get("envName"));
                connectMap.put("envName", envNameForSpark);
            }
        }
    }

    /**
     * before: UR1-10.108.192.127-15202(epccmaindb_G-DCN_9F1_set_2)
     * after: UR11010819212715202
     * @param envName
     * @return
     */
    private String sparkEnvNameAdapter(String envName) {
        if (StringUtils.isBlank(envName)) {
            return envName;
        }
        String input = envName;
        if (input.indexOf("(") != -1 && input.indexOf(")") != -1) {
            input = StringUtils.substring(input, 0, input.indexOf("("));
        }
        // Remove special characters
        return input.replaceAll("[^a-zA-Z0-9_]", "");
    }


    private void handleRuleSelectSql(Rule rule, String midTableName, StringBuilder partition, String partOfVariableName, String runDate, String runToday, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect
            , List<String> sqlList, Map<String, String> filters, Map<String, String> dbTableMap, String midTableAction, List<Map<String, Object>> sourceConnect, List<Map<String, Object>> targetConnect
            , Map<String, String> selectResult, boolean midTableReUse, int unionWay, List<String> leftCols, List<String> rightCols, List<String> complexCols, boolean shareConnect, String shareFromPart
            , Map<String, String> execParams, Integer dataSourceIndex, String engineType) throws UnExpectedRequestException {
        String templateEnName = rule.getTemplate().getEnName();
        boolean systemCompareTemplate = (QualitisConstants.MULTI_SOURCE_ACCURACY_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType())) || (QualitisConstants.MULTI_SOURCE_FULL_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()))
                || (QualitisConstants.MULTI_CLUSTER_CUSTOM_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()))
                || (QualitisConstants.SINGLE_CLUSTER_CUSTOM_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()))
                || (QualitisConstants.MULTI_SOURCE_ACROSS_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()))
                || (QualitisConstants.SINGLE_SOURCE_ACROSS_TEMPLATE_NAME.equals(templateEnName) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType()));


        if ((UnionWayEnum.COLLECT_AFTER_CALCULATE.getCode().equals(unionWay) || UnionWayEnum.NO_COLLECT_CALCULATE.getCode().equals(unionWay))
                && CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect) && sourceConnect.size() != targetConnect.size()) {
            throw new UnExpectedRequestException("Source envs'size can not be different from target envs'size.");
        }

        boolean unionAllForSaveResult = UnionWayEnum.COLLECT_AFTER_CALCULATE.getCode().equals(unionWay);
        if (systemCompareTemplate && dbTableMap.size() > 0) {
            if (QualitisConstants.MULTI_SOURCE_ACCURACY_TEMPLATE_NAME.equals(templateEnName)) {
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , sourceConnect, targetConnect, selectResult));
                    } else {
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
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , sourceConnect, targetConnect, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(); sourceIterator.hasNext(); ) {
                            Map<String, Object> sourceConnectMap = sourceIterator.next();
                            String sourceEnvName = (String) sourceConnectMap.get("envName");
                            if (StringUtils.isEmpty(sourceEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName, sourceIterator.next(), null, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(targetConnect) && CollectionUtils.isEmpty(sourceConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , sourceConnect, targetConnect, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> targetIterator = targetConnect.iterator(); targetIterator.hasNext(); ) {
                            Map<String, Object> targetConnectMap = targetIterator.next();
                            String targetEnvName = (String) targetConnectMap.get("envName");
                            if (StringUtils.isEmpty(targetEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + targetEnvName, null, targetIterator.next(), selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , sourceConnect, targetConnect, selectResult));
                    } else {
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], null, null, selectResult));
                    }
                    if ((StringUtils.isNotEmpty(rule.getAbnormalDatabase())) && ! MID_TABLE_NAME_PATTERN.matcher(midTableName).find()) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, midTableReUse));
                    }
                }
            } else if (QualitisConstants.MULTI_SOURCE_FULL_TEMPLATE_NAME.equals(rule.getTemplate().getEnName())) {
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
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getSpecialTransformSqlList(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], partition.toString(), filters, Strings.join(columns, ',')
                                , sourceConnect, targetConnect, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(), targetIterator = targetConnect.iterator(); sourceIterator.hasNext() && targetIterator.hasNext(); ) {
                            Map<String, Object> sourceConnectMap = sourceIterator.next();
                            Map<String, Object> targetConnectMap = targetIterator.next();
                            String sourceEnvName = (String) sourceConnectMap.get("envName");
                            String targetEnvName = (String) targetConnectMap.get("envName");
                            if (StringUtils.isEmpty(sourceEnvName) || StringUtils.isEmpty(targetEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName + targetEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                    , sourceConnectMap, targetConnectMap, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getSpecialTransformSqlList(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], partition.toString(), filters, Strings.join(columns, ',')
                                , sourceConnect, null, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(); sourceIterator.hasNext(); ) {
                            Map<String, Object> sourceConnectMap = sourceIterator.next();
                            String sourceEnvName = (String) sourceConnectMap.get("envName");
                            if (StringUtils.isEmpty(sourceEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                    , sourceIterator.next(), null, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getSpecialTransformSqlList(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], partition.toString(), filters, Strings.join(columns, ',')
                                , null, targetConnect, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> targetIterator = targetConnect.iterator(); targetIterator.hasNext(); ) {
                            Map<String, Object> targetConnectMap = targetIterator.next();
                            String targetEnvName = (String) targetConnectMap.get("envName");
                            if (StringUtils.isEmpty(targetEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + targetEnvName, partition.toString(), filters, Strings.join(columns, ',')
                                    , null, targetIterator.next(), rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    sqlList.addAll(getSpecialTransformSql(dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], partition.toString(), filters, Strings.join(columns, ',')
                                , null, null, rule.getContrastType(), leftCols, rightCols, complexCols, selectResult));

                    if (StringUtils.isNotEmpty(midTableName) && ! MID_TABLE_NAME_PATTERN.matcher(midTableName).find()) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, midTableReUse));
                    }
                }
            } else if (QualitisConstants.MULTI_CLUSTER_CUSTOM_TEMPLATE_NAME.equals(rule.getTemplate().getEnName()) || QualitisConstants.SINGLE_CLUSTER_CUSTOM_TEMPLATE_NAME.equals(rule.getTemplate().getEnName())) {
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceCustomFromSqlList(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] , sourceConnect, targetConnect, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(), targetIterator = targetConnect.iterator(); sourceIterator.hasNext() && targetIterator.hasNext(); ) {
                            Map<String, Object> sourceConnectMap = sourceIterator.next();
                            Map<String, Object> targetConnectMap = targetIterator.next();
                            String sourceEnvName = (String) sourceConnectMap.get("envName");
                            String targetEnvName = (String) targetConnectMap.get("envName");
                            if (StringUtils.isEmpty(sourceEnvName) || StringUtils.isEmpty(targetEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getMultiSourceCustomFromSql(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName + targetEnvName, sourceConnectMap, targetConnectMap, selectResult));
                        }
                    }

                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceCustomFromSqlList(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] , sourceConnect, targetConnect, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> sourceIterator = sourceConnect.iterator(); sourceIterator.hasNext(); ) {
                            Map<String, Object> sourceConnectMap = sourceIterator.next();
                            String sourceEnvName = (String) sourceConnectMap.get("envName");
                            if (StringUtils.isEmpty(sourceEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getMultiSourceCustomFromSql(midTableAction, dbTableMap
                                    , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + sourceEnvName
                                    , sourceConnectMap, null, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else if (CollectionUtils.isNotEmpty(targetConnect) && CollectionUtils.isEmpty(sourceConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceCustomFromSqlList(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] , sourceConnect, targetConnect, selectResult));
                    } else {
                        for (Iterator<Map<String, Object>> targetIterator = targetConnect.iterator(); targetIterator.hasNext(); ) {
                            Map<String, Object> targetConnectMap = targetIterator.next();
                            String targetEnvName = (String) targetConnectMap.get("envName");
                            if (StringUtils.isEmpty(targetEnvName)) {
                                continue;
                            }
                            sqlList.addAll(getMultiSourceCustomFromSql(midTableAction, dbTableMap
                                    , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + targetEnvName
                                    , null, targetConnectMap, selectResult));
                        }
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceCustomFromSqlList(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] , sourceConnect, targetConnect, selectResult));
                    } else {
                        sqlList.addAll(getMultiSourceCustomFromSql(midTableAction, dbTableMap, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], null, null, selectResult));
                    }
                    if (StringUtils.isNotEmpty(midTableName) && ! MID_TABLE_NAME_PATTERN.matcher(midTableName).find()) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, midTableReUse));
                    }
                }
            } else if (QualitisConstants.MULTI_SOURCE_ACROSS_TEMPLATE_NAME.equals(rule.getTemplate().getEnName()) || QualitisConstants.SINGLE_SOURCE_ACROSS_TEMPLATE_NAME.equals(rule.getTemplate().getEnName()) ) {
                sqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                if (QualitisConstants.LEFT_INDEX.equals(dataSourceIndex) && CollectionUtils.isNotEmpty(sourceConnect)) {
                    getTableRowsWithEnvs(partOfVariableName, sourceConnect, midTableAction, sqlList);
                } else if (QualitisConstants.RIGHT_INDEX.equals(dataSourceIndex) && CollectionUtils.isNotEmpty(targetConnect)) {
                    getTableRowsWithEnvs(partOfVariableName, targetConnect, midTableAction, sqlList);
                } else {
                    sqlList.add(getSparkSqlSentence(midTableAction, partOfVariableName, "", "", "", RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())));
                }
                String variableFormer = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                formatSchema(sqlList, partOfVariableName, variableFormer, variableLatter);
            } else {
                if (CollectionUtils.isNotEmpty(sourceConnect) && CollectionUtils.isNotEmpty(targetConnect)) {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , sourceConnect, targetConnect, selectResult));
                    } else {
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
                    }
                    String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                    unionAllSaveResult(lastVariable, selectResult, sqlList, unionAllForSaveResult);
                } else {
                    if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(unionWay)) {
                        sqlList.addAll(getMultiSourceAccuracyFromSqlList(midTableAction, dbTableMap, filters
                                , partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]
                                , null, null, selectResult));
                    } else {
                        sqlList.addAll(getMultiSourceAccuracyfromSql(midTableAction, dbTableMap, filters, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], null, null, selectResult));
                    }
                    if (StringUtils.isNotEmpty(midTableName) && ! MID_TABLE_NAME_PATTERN.matcher(midTableName).find()) {
                        sqlList.addAll(getSaveMidTableSentenceSettings());
                        sqlList.addAll(getSaveMidTableSentence(midTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, midTableReUse));
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
            sqlList.addAll(generateSparkSqlAndSaveSentence(midTableAction, midTableName, rule, partOfVariableName, decryptedMysqlInfo, runDate, runToday, selectResult, midTableReUse, unionAllForSaveResult, filterFields.toString(), tableEnvs, shareConnect, shareFromPart, execParams));
        }
    }

    private void getTableRowsWithEnvs(String partOfVariableName, List<Map<String, Object>> connect, String midTableAction, List<String> sqlList) {
        List<String> varList = new ArrayList<>(connect.size());
        String prefix = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
        for (Iterator<Map<String, Object>> connectIterator = connect.iterator(); connectIterator.hasNext(); ) {
            Map<String, Object> connectMap = connectIterator.next();
            String envName = (String) connectMap.get("envName");
            if (StringUtils.isEmpty(envName)) {
                continue;
            }

            String host = (String) connectMap.get("host");
            String port = (String) connectMap.get("port");
            String user = (String) connectMap.get("username");
            String pwd = (String) connectMap.get("password");
            String dataType = (String) connectMap.get("dataType");
            String var = prefix + "_" + envName;
            String str = SPARK_MYSQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, midTableAction).replace(VARIABLE_NAME_PLACEHOLDER, var)
                .replace("${JDBC_DRIVER}", JDBC_DRIVER.get(dataType))
                .replace("${MYSQL_IP}", host)
                .replace("${MYSQL_PORT}", port)
                .replace("${MYSQL_USER}", user)
                .replace("${MYSQL_PASSWORD}", pwd);

            sqlList.add(str);
            varList.add(var);
        }
        StringBuilder stringBuilder = new StringBuilder("val " + prefix + " = ");
        boolean firstVar = true;
        for (String varName : varList) {
            if (firstVar) {
                stringBuilder.append(varName);
                firstVar = false;
            } else {
                stringBuilder.append(".unionAll(").append(varName).append(")");
            }
        }
        sqlList.add(stringBuilder.toString());
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
                    currentConnectParams.put("needDecode", "false");
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
                                              StringBuilder partition, Map<String, Long> ruleMetricMap, String runDate, String runToday, String engineType) throws UnExpectedRequestException {
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
        if (StringUtils.isNotBlank(runDate)) {
            templateMidTableAction = templateMidTableAction.replace("${run_date}", runDate);
//            templateMidTableAction = templateMidTableAction.replace("${run_date_std}", runDate);
        }
        if (StringUtils.isNotBlank(runToday)) {
            templateMidTableAction = templateMidTableAction.replace("${run_today}", runToday);
//            templateMidTableAction = templateMidTableAction.replace("${run_today_std}", runToday);
        }

        templateMidTableAction = DateExprReplaceUtil.replaceRunDate(date, templateMidTableAction);

        Set<String> ruleMetricNames = ruleMetricMap.keySet();
        for (String ruleMetricName : ruleMetricNames) {
            String cleanRuleMetricName = ruleMetricName.replace("-", "_");
            templateMidTableAction = templateMidTableAction.replace(ruleMetricName, cleanRuleMetricName);
        }
        templateMidTableAction = MyStringEscaper.escapeStringForQuotes(templateMidTableAction);
        if (EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType)) {
            Set<RuleDataSource> ruleDataSourceSet = rule.getRuleDataSources();
            if (CollectionUtils.isNotEmpty(ruleDataSourceSet)) {
                for (RuleDataSource ruleDataSource : ruleDataSourceSet) {
                    if (StringUtils.isNotBlank(ruleDataSource.getDbName()) && StringUtils.isNotBlank(ruleDataSource.getTableName())) {
                        templateMidTableAction = templateMidTableAction.replace(SpecCharEnum.EMPTY.getValue() + ruleDataSource.getDbName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + ruleDataSource.getTableName()
                        , SpecCharEnum.EMPTY.getValue() + "hive." + ruleDataSource.getDbName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + ruleDataSource.getTableName());
                    }
                }
            }
        }
        return templateMidTableAction;
    }

    private String getMultiDatasourceFiltesAndUpdateMidTableAction(Rule rule, String templateMidTableAction, Date date, Map<String, String> filters, List<String> leftCols, List<String> rightCols, List<String> complexCols, Integer datasourceIndex, String runDate, String runToday, boolean trino) throws UnExpectedRequestException {
        Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
        if (datasourceIndex != null) {
            return handleWithAcrossClusterDatasource(templateMidTableAction, rule.getRuleDataSources(), date, datasourceIndex, runDate, runToday);
        }
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            String filter = ruleDataSource.getFilter();
            if (StringUtils.isNotEmpty(filter) && StringUtils.isNotBlank(runDate)) {
                filter = filter.replace("${run_date}", runDate);
//                filter = filter.replace("${run_date_std}", runDate);
            }
            if (StringUtils.isNotEmpty(filter) && StringUtils.isNotBlank(runToday)) {
                filter = filter.replace("${run_today}", runToday);
//                filter = filter.replace("${run_today_std}", runToday);
            }

            if (ruleDataSource.getDatasourceIndex().equals(0)) {
                filter = DateExprReplaceUtil.replaceFilter(date, filter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_LEFT_PLACEHOLDER, filter);
                filters.put("left_table", filter);
            } else {
                filter = DateExprReplaceUtil.replaceFilter(date, filter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_RIGHT_PLACEHOLDER, filter);
                filters.put("right_table", filter);
            }
        }

        StringBuilder leftConcat = new StringBuilder();
        List<List<String>> leftConcatList = new ArrayList<>();
        for (String col : leftCols) {
            if (trino) {
                if (CollectionUtils.isEmpty(leftConcatList)) {
                    List<String> leftSubConcatList = new ArrayList<>();
                    leftSubConcatList.add("coalesce(cast(" + col + " as varchar), '')");
                    leftConcatList.add(leftSubConcatList);
                    continue;
                }

                if (leftConcatList.get(leftConcatList.size() - 1).size() < trinoColumnSize) {
                    leftConcatList.get(leftConcatList.size() - 1).add("coalesce(cast(" + col + " as varchar), '')");
                } else {
                    List<String> leftSubConcatList = new ArrayList<>();
                    leftSubConcatList.add("coalesce(cast(" + col + " as varchar), '')");
                    leftConcatList.add(leftSubConcatList);
                }

                continue;
            }
            if (Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) && CollectionUtils.isNotEmpty(complexCols) && complexCols.contains(col)) {
                leftConcat.append("nvl(").append(taskDataSourceConfig.getHiveSortUdf()).append("(").append(col).append("),''),");
                continue;
            }
            leftConcat.append("nvl(cast(").append(col).append(" as string),''),");
        }
        if (StringUtils.isNotBlank(leftConcat.toString())) {
            leftConcat.deleteCharAt(leftConcat.length() - 1);
        }
        StringBuilder leftTrinoConcat = new StringBuilder();
        if (CollectionUtils.isNotEmpty(leftConcatList)) {
            if (leftConcatList.size() == 1) {
                leftTrinoConcat.append("md5(cast(concat(");
                leftTrinoConcat.append(StringUtils.join(leftConcatList.iterator().next(), SpecCharEnum.COMMA.getValue()));
                leftTrinoConcat.append(") as varbinary)) as line_md5");
            } else {
                leftTrinoConcat.append("concat(");
                for (List<String> subLeftTrinoConcat : leftConcatList) {
                    StringBuilder innerLeftTrinoConcat = new StringBuilder();
                    if (subLeftTrinoConcat.size() == 1) {
                        innerLeftTrinoConcat.append("md5(cast(").append(StringUtils.join(subLeftTrinoConcat, SpecCharEnum.COMMA.getValue())).append(" as varbinary))").append(SpecCharEnum.COMMA.getValue());
                    } else {
                        innerLeftTrinoConcat.append("md5(cast(concat(").append(StringUtils.join(subLeftTrinoConcat, SpecCharEnum.COMMA.getValue())).append(") as varbinary))").append(SpecCharEnum.COMMA.getValue());
                    }
                    leftTrinoConcat.append(innerLeftTrinoConcat.toString());
                }
                leftTrinoConcat.deleteCharAt(leftTrinoConcat.length() - 1);
                leftTrinoConcat.append(") as line_md5");
            }
        }

        StringBuilder rightConcat = new StringBuilder();
        List<List<String>> rightConcatList = new ArrayList<>();
        for (String col : rightCols) {
            if (trino) {
                if (CollectionUtils.isEmpty(rightConcatList)) {
                    List<String> rightSubConcatList = new ArrayList<>();
                    rightSubConcatList.add("coalesce(cast(" + col + " as varchar), '')");
                    rightConcatList.add(rightSubConcatList);
                    continue;
                }

                if (rightConcatList.get(rightConcatList.size() - 1).size() < trinoColumnSize) {
                    rightConcatList.get(rightConcatList.size() - 1).add("coalesce(cast(" + col + " as varchar), '')");
                } else {
                    List<String> rightSubConcatList = new ArrayList<>();
                    rightSubConcatList.add("coalesce(cast(" + col + " as varchar), '')");
                    rightConcatList.add(rightSubConcatList);
                }
                continue;
            }
            if (Boolean.TRUE.equals(taskDataSourceConfig.getHiveSortUdfOpen()) && CollectionUtils.isNotEmpty(complexCols) && complexCols.contains(col)) {
                rightConcat.append("nvl(").append(taskDataSourceConfig.getHiveSortUdf()).append("(").append(col).append("),''),");
                continue;
            }
            rightConcat.append("nvl(cast(").append(col).append(" as string),''),");
        }
        if (StringUtils.isNotBlank(rightConcat.toString())) {
            rightConcat.deleteCharAt(rightConcat.length() - 1);
        }
        StringBuilder rightTrinoConcat = new StringBuilder();
        if (CollectionUtils.isNotEmpty(rightConcatList)) {
            if (rightConcatList.size() == 1) {
                rightTrinoConcat.append("md5(cast(concat(");
                rightTrinoConcat.append(StringUtils.join(rightConcatList.iterator().next(), SpecCharEnum.COMMA.getValue()));
                rightTrinoConcat.append(") as varbinary)) as line_md5");
            } else {
                rightTrinoConcat.append("concat(");
                for (List<String> subrightTrinoConcat : rightConcatList) {
                    StringBuilder innerrightTrinoConcat = new StringBuilder();
                    if (subrightTrinoConcat.size() == 1) {
                        innerrightTrinoConcat.append("md5(cast(").append(StringUtils.join(subrightTrinoConcat, SpecCharEnum.COMMA.getValue())).append(" as varbinary))").append(SpecCharEnum.COMMA.getValue());
                    } else {
                        innerrightTrinoConcat.append("md5(cast(concat(").append(StringUtils.join(subrightTrinoConcat, SpecCharEnum.COMMA.getValue())).append(") as varbinary))").append(SpecCharEnum.COMMA.getValue());
                    }
                    rightTrinoConcat.append(innerrightTrinoConcat.toString());
                }
                rightTrinoConcat.deleteCharAt(rightTrinoConcat.length() - 1);
                rightTrinoConcat.append(") as line_md5");
            }
        }

        templateMidTableAction = templateMidTableAction.replace("${left_columns}", StringUtils.isNotBlank(leftTrinoConcat.toString()) ? leftTrinoConcat.toString() : leftConcat.toString());
        templateMidTableAction = templateMidTableAction.replace("${right_columns}", StringUtils.isNotBlank(rightTrinoConcat.toString()) ? rightTrinoConcat.toString() : rightConcat.toString());
        if (QualitisConstants.MULTI_SOURCE_FULL_TEMPLATE_NAME.equals(rule.getTemplate().getEnName()) && RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode().equals(rule.getTemplate().getTemplateType())) {
            return templateMidTableAction;
        }
        if (CollectionUtils.isEmpty(rule.getRuleDataSourceMappings())) {
            return templateMidTableAction;
        }
        List<RuleDataSourceMapping> ruleDataSourceMappings = rule.getRuleDataSourceMappings().stream()
                .filter(ruleDataSourceMapping -> ruleDataSourceMapping.getMappingType() != null && ruleDataSourceMapping.getMappingType().equals(MappingTypeEnum.MATCHING_FIELDS.getCode())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ruleDataSourceMappings)) {
            StringBuilder compareColumns = new StringBuilder();
            int indexCol = 1;
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                if (StringUtils.isNotEmpty(ruleDataSourceMapping.getLeftStatement())) {
                    compareColumns.append(ruleDataSourceMapping.getLeftStatement()).append(" AS ").append("col" + indexCol).append(", ");
                    indexCol++;
                }
                if (StringUtils.isNotEmpty(ruleDataSourceMapping.getRightStatement())) {
                    compareColumns.append(ruleDataSourceMapping.getRightStatement()).append(" AS ").append("col" + indexCol).append(", ");
                    indexCol++;
                }
            }

            int index = templateMidTableAction.indexOf("CASE WHEN");

            templateMidTableAction = new StringBuffer(templateMidTableAction).insert(index, compareColumns.toString()).toString();
        }
        return templateMidTableAction;
    }

    private static String handleWithAcrossClusterDatasource(String templateMidTableAction, Set<RuleDataSource> ruleDataSources, Date date,
                                                            Integer datasourceIndex, String runDate, String runToday) throws UnExpectedRequestException {
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            String filter = ruleDataSource.getFilter();
            if (datasourceIndex.equals(ruleDataSource.getDatasourceIndex()) && StringUtils.isNotBlank(filter)) {
                if (StringUtils.isNotBlank(runDate)) {
                    filter = filter.replace("${run_date}", runDate);
//                    filter = filter.replace("${run_date_std}", runDate);
                }
                if (StringUtils.isNotBlank(runToday)) {
                    filter = filter.replace("${run_today}", runToday);
//                    filter = filter.replace("${run_today_std}", runToday);
                }

                filter = DateExprReplaceUtil.replaceFilter(date, filter);
                templateMidTableAction = templateMidTableAction.replace(FILTER_PLACEHOLDER, filter);
                if (StringUtils.isNotEmpty(ruleDataSource.getDbName())) {
                    templateMidTableAction = templateMidTableAction.replace(DATABASE_PLACEHOLDER, ruleDataSource.getDbName());
                } else {
                    templateMidTableAction = templateMidTableAction.replace(DATABASE_PLACEHOLDER + SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), "");
                }
                if (StringUtils.isNotEmpty(ruleDataSource.getTableName())) {
                    templateMidTableAction = templateMidTableAction.replace(TABLE_PLACEHOLDER, ruleDataSource.getTableName());
                }
                return templateMidTableAction;
            }
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

    private List<String> getMultiSourceAccuracyFromSqlList(String midTableAction, Map<String, String> dbTableMap, Map<String, String> filters, String partOfOriginVariableName
            , List<Map<String, Object>> sourceConnects, List<Map<String, Object>> targetConnects, Map<String, String> selectResult) {
        // sql
        List<String> transformSql = new ArrayList<>();
        StringBuilder envName = new StringBuilder();
        List<String> sourceSqlVariableNameList = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        sourceSql.append("select *").append(" from ")
                .append(dbTableMap.get("left_database")).append(dbTableMap.get("left_table"))
                .append(" where ").append(filters.get("left_table"));
        StringBuilder targetSql = new StringBuilder();
        targetSql.append("select *").append(" from ")
                .append(dbTableMap.get("right_database")).append(dbTableMap.get("right_table"))
                .append(" where ").append(filters.get("right_table"));

//         connect to databases with envs' connection info: spark.read.format("jdbc").option("driver","com.mysql.jdbc.Driver").option
        if (CollectionUtils.isNotEmpty(sourceConnects)) {
            for (Map<String, Object> sourceConnect: sourceConnects) {
                String sourceEnvName = (String) sourceConnect.get("envName");
                String partOfVariableName = partOfOriginVariableName + sourceEnvName;
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
                sourceSqlVariableNameList.add("originalDFLeft_" + partOfVariableName);
            }
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfOriginVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
            sourceSqlVariableNameList.add("originalDFLeft_" + partOfOriginVariableName);
        }
//        merge multi-environment with unionAll
        String sourceCollectSqlName = "originalDFLeft_" + partOfOriginVariableName;
        StringBuilder sourceCollectSql = new StringBuilder("val " + sourceCollectSqlName + " = ");
        sourceCollectSql.append(sourceSqlVariableNameList.get(0));
        for (int i = 1; i < sourceSqlVariableNameList.size(); i++) {
            sourceCollectSql.append(".union(" + sourceSqlVariableNameList.get(i) + ")");
        }
        transformSql.add(sourceCollectSql.toString());
//         register to a temporary table with multi-environment
        transformSql.add(sourceCollectSqlName + ".registerTempTable(\"tmp1_" + partOfOriginVariableName + dbTableMap.get("left_table") + "\")");

        List<String> targetSqlVariableNameList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetConnects)) {
            for (Map<String, Object> targetConnect : targetConnects) {
                String targetEnvName = (String) targetConnect.get("envName");
                String partOfVariableName = partOfOriginVariableName + targetEnvName;
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
                targetSqlVariableNameList.add("originalDFRight_" + partOfVariableName);
            }
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfOriginVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
            targetSqlVariableNameList.add("originalDFRight_" + partOfOriginVariableName);
        }
//        merge multi-environment
        String targetCollectSqlName = "originalDFRight_" + partOfOriginVariableName;
        StringBuilder targetCollectSql = new StringBuilder("val " + targetCollectSqlName + " = ");
        targetCollectSql.append(targetSqlVariableNameList.get(0));
        for (int i = 1; i < targetSqlVariableNameList.size(); i++) {
            targetCollectSql.append(".union(" + targetSqlVariableNameList.get(i) + ")");
        }
        transformSql.add(targetCollectSql.toString());
//        register to temporary table
        transformSql.add(targetCollectSqlName + ".registerTempTable(\"tmp2_" + partOfOriginVariableName + dbTableMap.get("right_table") + "\")");

//        compare left database and right databases' result with their temporary table
        String commonJoin = midTableAction
                .replace(dbTableMap.get("left_database") + dbTableMap.get("left_table") + " ", "tmp1_" + partOfOriginVariableName + dbTableMap.get("left_table") + " ")
                .replace(dbTableMap.get("right_database") + dbTableMap.get("right_table") + " ", "tmp2_" + partOfOriginVariableName + dbTableMap.get("right_table") + " ");
        String variableFormer = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfOriginVariableName);
        String joinSql = "val " + variableFormer + " = spark.sql(\"" + commonJoin + "\")";
        transformSql.add(joinSql);
        String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfOriginVariableName);
        // Select compare_result = 0
        transformSql.add("val " + variableLatter + " = " + variableFormer + ".where(" + variableFormer + "(\"compare_result\") === 0)");
        selectResult.put(variableLatter, envName.toString());

        return transformSql;
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

    private List<String> getMultiSourceCustomFromSqlList(String midTableAction, Map<String, String> dbTableMap, String partOfOriginVariableName
            , List<Map<String, Object>> sourceConnects, List<Map<String, Object>> targetConnects, Map<String, String> selectResult) {
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();
        StringBuilder envName = new StringBuilder();
        List<String> sourceSqlVariableNameList = new ArrayList<>();

        String leftCollectSql = dbTableMap.get("left_collect_sql");
        String rightCollectSql = dbTableMap.get("right_collect_sql");

        sourceSql.append(leftCollectSql);
        targetSql.append(rightCollectSql);

//         spark.read.format("jdbc").option("driver","com.mysql.jdbc.Driver").option
        if (CollectionUtils.isNotEmpty(sourceConnects)) {
            for (Map<String, Object> sourceConnect: sourceConnects) {
                String sourceEnvName = (String) sourceConnect.get("envName");
                String partOfVariableName = partOfOriginVariableName + sourceEnvName;
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
                sourceSqlVariableNameList.add("originalDFLeft_" + partOfVariableName);
            }
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFLeft_" + partOfOriginVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sourceSql.toString()));
            sourceSqlVariableNameList.add("originalDFLeft_" + partOfOriginVariableName);
        }
//        合并环境
        String sourceCollectSqlName = "originalDFLeft_" + partOfOriginVariableName;
        StringBuilder sourceCollectSql = new StringBuilder("val " + sourceCollectSqlName + " = ");
        sourceCollectSql.append(sourceSqlVariableNameList.get(0));
        for (int i = 1; i < sourceSqlVariableNameList.size(); i++) {
            sourceCollectSql.append(".union(" + sourceSqlVariableNameList.get(i) + ")");
        }
        transformSql.add(sourceCollectSql.toString());
//        注册hive临时表
        transformSql.add(sourceCollectSqlName + ".registerTempTable(\"tmp1_" + partOfOriginVariableName + dbTableMap.get("left_table") + "\")");

        List<String> targetSqlVariableNameList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targetConnects)) {
            for (Map<String, Object> targetConnect : targetConnects) {
                String targetEnvName = (String) targetConnect.get("envName");
                String partOfVariableName = partOfOriginVariableName + targetEnvName;
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
                targetSqlVariableNameList.add("originalDFRight_" + partOfVariableName);
            }
        } else {
            transformSql.add(SPARK_SQL_TEMPLATE.replace(VARIABLE_NAME_PLACEHOLDER, "originalDFRight_" + partOfOriginVariableName).replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, targetSql.toString()));
            targetSqlVariableNameList.add("originalDFRight_" + partOfOriginVariableName);
        }
//        合并环境
        String targetCollectSqlName = "originalDFRight_" + partOfOriginVariableName;
        StringBuilder targetCollectSql = new StringBuilder("val " + targetCollectSqlName + " = ");
        targetCollectSql.append(targetSqlVariableNameList.get(0));
        for (int i = 1; i < targetSqlVariableNameList.size(); i++) {
            targetCollectSql.append(".union(" + targetSqlVariableNameList.get(i) + ")");
        }
        transformSql.add(targetCollectSql.toString());
//        注册hive临时表
        transformSql.add(targetCollectSqlName + ".registerTempTable(\"tmp2_" + partOfOriginVariableName + dbTableMap.get("right_table") + "\")");

        String commonJoin = midTableAction
                .replaceFirst("\\Q" + dbTableMap.get("left_collect_sql") + "\\E", "tmp1_" + partOfOriginVariableName + dbTableMap.get("left_table"))
                .replaceFirst("\\Q" + dbTableMap.get("right_collect_sql") + "\\E", "tmp2_" + partOfOriginVariableName + dbTableMap.get("right_table"));

        String variableFormer = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfOriginVariableName);
        String joinSql = "val " + variableFormer + " = spark.sql(\"" + commonJoin + "\")";
        transformSql.add(joinSql);
        String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfOriginVariableName);
        // Select compare_result = 0
        transformSql.add("val " + variableLatter + " = " + variableFormer + ".where(" + variableFormer + "(\"compare_result\") === 0)");
        selectResult.put(variableLatter, envName.toString());
        return transformSql;
    }

    /**
     *
     * @param midTableAction
     * @param dbTableMap
     * @param partOfVariableName templateEnName + sourceEnvName + targetEnvName
     * @param sourceConnect
     * @param targetConnect
     * @param selectResult
     * @return
     */
    private List<String> getMultiSourceCustomFromSql(String midTableAction, Map<String, String> dbTableMap, String partOfVariableName
            , Map<String, Object> sourceConnect, Map<String, Object> targetConnect, Map<String, String> selectResult) {
        // Solve partition, value, hash value
        List<String> transformSql = new ArrayList<>();
        StringBuilder sourceSql = new StringBuilder();
        StringBuilder targetSql = new StringBuilder();
        StringBuilder envName = new StringBuilder();

        sourceSql.append(dbTableMap.get("left_collect_sql"));
        targetSql.append(dbTableMap.get("right_collect_sql"));

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
        String tmpTemplateName1 = "tmp1_" + partOfVariableName;
        String tmpTemplateName2 = "tmp2_" + partOfVariableName;

        transformSql.add("originalDFLeft_" + partOfVariableName + ".registerTempTable(\"" + tmpTemplateName1 + "\")");
        transformSql.add("originalDFRight_" + partOfVariableName + ".registerTempTable(\"" + tmpTemplateName2 + "\")");

//        To replace respectively the repeating part between left sql and right sql
        String commonJoin = midTableAction
                    .replaceFirst("\\Q" + dbTableMap.get("left_collect_sql") + "\\E", tmpTemplateName1)
                    .replaceFirst("\\Q" + dbTableMap.get("right_collect_sql") + "\\E", tmpTemplateName2);

        String variableFormer = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfVariableName);
        String joinSql = "val " + variableFormer + " = spark.sql(\"" + commonJoin + "\")";
        transformSql.add(joinSql);
        String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName);
        // Select compare_result = 0
        transformSql.add("val " + variableLatter + " = " + variableFormer + ".where(" + variableFormer + "(\"compare_result\") === 0)");
        selectResult.put(variableLatter, envName.toString());
        return transformSql;
    }

    private List<String> getSpecialTransformSqlList(Map<String, String> dbTableMap, String partOfOriginalVariableName, String filter, Map<String, String> filters, String columns
            , List<Map<String, Object>> sourceConnects, List<Map<String, Object>> targetConnects, Integer contrastType, List<String> leftCols, List<String> rightCols, List<String> complexCols, Map<String, String> selectResult) {
        List<String> transformSql = new ArrayList<>();
        StringBuilder envName = new StringBuilder();

        StringBuilder tmpRegisterTableLeft = new StringBuilder();
        StringBuilder tmpRegisterTableRight = new StringBuilder();
        tmpRegisterTableLeft.append("md5_table_left_" + partOfOriginalVariableName);
        tmpRegisterTableRight.append("md5_table_right_" + partOfOriginalVariableName);

//        sql
        if (CollectionUtils.isNotEmpty(sourceConnects)) {
            List<String> unionLeftVariableNameList = new ArrayList<>();
            for (Map<String, Object> sourceConnect: sourceConnects) {
                StringBuilder sourceSql = new StringBuilder();
                String partOfVariableName = (String) sourceConnect.get("envName");
                if (StringUtils.isNotBlank(columns)) {
                    sourceSql.append("select ").append(columns);
                } else {
                    if (taskDataSourceConfig.getHiveSortUdfOpen() && CollectionUtils.isNotEmpty(complexCols)) {
                        List<String> leftColsReal = new ArrayList<>(leftCols.size());
                        sourceSql.append("select ");
                        for (String col : leftCols) {
                            if (complexCols.contains(col)) {
                                leftColsReal.add(taskDataSourceConfig.getHiveSortUdf() + "(" + col + ")");
                            } else {
                                leftColsReal.add(col);
                            }
                        }
                        sourceSql.append(String.join(",", leftColsReal));
                    } else {
                        sourceSql.append("select *");
                    }
                }
                sourceSql.append(" from ").append(dbTableMap.get("left_database")).append(dbTableMap.get("left_table")).append(" where ").append(filters.get("left_table"));
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

//                na.fill
                transformSql.add("val fillNullDFLeft_" + partOfVariableName + " = originalDFLeft_" + partOfVariableName + ".na.fill(UUID)");
                transformSql.add("val qualitis_names_left_" + partOfVariableName + " = fillNullDFLeft_" + partOfVariableName + ".schema.fieldNames");
                transformSql.add("val fillNullWithFullLineWithHashDF_left_" + partOfVariableName + " = fillNullDFLeft_" + partOfVariableName + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
                transformSql.add("val qualitis_names_left_" + partOfVariableName + "_buffer = qualitis_names_left_" + partOfVariableName + ".toBuffer");
                transformSql.add("val finalDF_left_" + partOfVariableName + " = fillNullWithFullLineWithHashDF_left_" + partOfVariableName + ".drop(qualitis_names_left_" + partOfVariableName + ":_*)");
                unionLeftVariableNameList.add("finalDF_left_" + partOfVariableName);
            }
            String sourceCollectSqlName = "collect_fillNullDFLeft_" + partOfOriginalVariableName;
            if (unionLeftVariableNameList.size() > 1) {
                StringBuilder sourceCollectSql = new StringBuilder("val " + sourceCollectSqlName + " = ");
                sourceCollectSql.append(unionLeftVariableNameList.get(0));
                for (int i = 1; i < unionLeftVariableNameList.size(); i++) {
                    sourceCollectSql.append(".union(" + unionLeftVariableNameList.get(i) + ")");
                }
                transformSql.add(sourceCollectSql.toString());
            } else {
                sourceCollectSqlName = unionLeftVariableNameList.get(0);
            }
            transformSql.add(sourceCollectSqlName + ".registerTempTable(\"" + tmpRegisterTableLeft + "\")");
        }

        if (CollectionUtils.isNotEmpty(targetConnects)) {
            List<String> unionRightVariableNameList = new ArrayList<>();
            for (Map<String, Object> targetConnect: targetConnects) {
                StringBuilder targetSql = new StringBuilder();
                String partOfVariableName = (String) targetConnect.get("envName");
                if (StringUtils.isNotBlank(columns)) {
                    targetSql.append("select ").append(columns);
                } else {
                    if (taskDataSourceConfig.getHiveSortUdfOpen() && CollectionUtils.isNotEmpty(complexCols)) {
                        List<String> rightColsReal = new ArrayList<>(rightCols.size());
                        targetSql.append("select ");
                        for (String col : leftCols) {
                            if (complexCols.contains(col)) {
                                rightColsReal.add(taskDataSourceConfig.getHiveSortUdf() + "(" + col + ")");
                            } else {
                                rightColsReal.add(col);
                            }
                        }
                        targetSql.append(String.join(",", rightColsReal));
                    } else {
                        targetSql.append("select *");
                    }
                }
                targetSql.append(" from ").append(dbTableMap.get("right_database")).append(dbTableMap.get("right_table")).append(" where ").append(filters.get("right_table"));
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

//                na.fill
                transformSql.add("val fillNullDFRight_" + partOfVariableName + " = originalDFRight_" + partOfVariableName + ".na.fill(UUID)");
                transformSql.add("val qualitis_names_right_" + partOfVariableName + " = fillNullDFRight_" + partOfVariableName + ".schema.fieldNames");
                transformSql.add("val fillNullWithFullLineWithHashDF_right_" + partOfVariableName + " = fillNullDFRight_" + partOfVariableName + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"qualitis_full_line_hash_value\", md5(to_json(struct($\"*\"))))");
                transformSql.add("val qualitis_names_right_" + partOfVariableName + "_buffer = qualitis_names_right_" + partOfVariableName + ".toBuffer");
                transformSql.add("val finalDF_right_" + partOfVariableName + " = fillNullWithFullLineWithHashDF_right_" + partOfVariableName + ".drop(qualitis_names_right_" + partOfVariableName + ":_*)");
                unionRightVariableNameList.add("finalDF_right_" + partOfVariableName);
            }

            String targetCollectSqlName = "collect_fillNullDFRight_" + partOfOriginalVariableName;
            if (unionRightVariableNameList.size() > 1) {
                StringBuilder targetCollectSql = new StringBuilder("val " + targetCollectSqlName + " = ");
                targetCollectSql.append(unionRightVariableNameList.get(0));
                for (int i = 1; i < unionRightVariableNameList.size(); i++) {
                    targetCollectSql.append(".union(" + unionRightVariableNameList.get(i) + ")");
                }
                transformSql.add(targetCollectSql.toString());
            } else {
                targetCollectSqlName = unionRightVariableNameList.get(0);
            }
            transformSql.add(targetCollectSqlName + ".registerTempTable(\"" + tmpRegisterTableRight + "\")");
        }

        String originalVariableName = getVariableNameByRule(OptTypeEnum.ORIGINAL_STATISTIC_DF.getMessage(), partOfOriginalVariableName);
        String joinSql = "val " + originalVariableName + " = spark.sql(\"SELECT qulaitis_left_tmp.qualitis_full_line_hash_value as left_full_hash_line, qulaitis_left_tmp.qualitis_mul_db_accuracy_num as left_full_line_num, qulaitis_right_tmp.qualitis_full_line_hash_value as right_full_hash_line, qulaitis_right_tmp.qualitis_mul_db_accuracy_num as right_full_line_num FROM (SELECT qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM " + tmpRegisterTableLeft.toString() + " WHERE true group by qualitis_full_line_hash_value) qulaitis_left_tmp ${contrast_type} (SELECT qualitis_full_line_hash_value, count(1) as qualitis_mul_db_accuracy_num FROM " + tmpRegisterTableRight.toString() + " WHERE true group by qualitis_full_line_hash_value) qulaitis_right_tmp ON (qulaitis_left_tmp.qualitis_full_line_hash_value = qulaitis_right_tmp.qualitis_full_line_hash_value AND qulaitis_left_tmp.qualitis_mul_db_accuracy_num = qulaitis_right_tmp.qualitis_mul_db_accuracy_num) WHERE (qulaitis_right_tmp.qualitis_full_line_hash_value is null AND qulaitis_right_tmp.qualitis_mul_db_accuracy_num is null) OR (qulaitis_left_tmp.qualitis_full_line_hash_value is null AND qulaitis_left_tmp.qualitis_mul_db_accuracy_num is null) ${outer_filter}\")";
        joinSql = joinSql.replace("${contrast_type}", ContrastTypeEnum.getJoinType(contrastType));
        if (StringUtils.isNotEmpty(filter)) {
            joinSql = joinSql.replace("${outer_filter}", "AND (" + filter + ")");
        } else {
            joinSql = joinSql.replace("${outer_filter}", "");
        }
        transformSql.add(joinSql);
        String statisticVariableName = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfOriginalVariableName);
        if (fpsConfig.getLightweightQuery()) {
            String leftVariableName = getVariableNameByRule(OptTypeEnum.LEFT_JOIN_STATISTIC_DF.getMessage(), partOfOriginalVariableName);
            String rightVariableName = getVariableNameByRule(OptTypeEnum.RIGHT_JOIN_STATISTIC_DF.getMessage(), partOfOriginalVariableName);

            transformSql.add(originalVariableName + ".registerTempTable(\"md5_table_total_" + partOfOriginalVariableName + "\")");
            String joinSqlWithLeft = "val " + leftVariableName + " = spark.sql(\"\"\"SELECT \"left\" as source, " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_value as full_line, md5_table_total_" + partOfOriginalVariableName + ".left_full_line_num FROM " + tmpRegisterTableLeft.toString() + " full outer join md5_table_total_" + partOfOriginalVariableName + " on " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_hash_value = md5_table_total_" + partOfOriginalVariableName + ".left_full_hash_line where " + tmpRegisterTableLeft.toString() + ".qualitis_full_line_hash_value is not null and md5_table_total_" + partOfOriginalVariableName + ".left_full_hash_line is not null\"\"\")";
            String joinSqlWithRight = "val " + rightVariableName + " = spark.sql(\"\"\"SELECT \"right\" as source, " + tmpRegisterTableRight.toString() + ".qualitis_full_line_value as full_line, md5_table_total_" + partOfOriginalVariableName + ".right_full_line_num FROM " + tmpRegisterTableRight.toString() + " full outer join md5_table_total_" + partOfOriginalVariableName + " on " + tmpRegisterTableRight.toString() + ".qualitis_full_line_hash_value = md5_table_total_" + partOfOriginalVariableName + ".right_full_hash_line where " + tmpRegisterTableRight.toString() + ".qualitis_full_line_hash_value is not null and md5_table_total_" + partOfOriginalVariableName + ".right_full_hash_line is not null\"\"\")";

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
        fullLineToHashLine(transformSql, partOfVariableName, tmpRegisterTableLeft, tmpRegisterTableRight);
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

    private void fullLineToHashLine(List<String> transformSql, String partOfVariableName, StringBuilder tmpRegisterTableLeft, StringBuilder tmpRegisterTableRight) {
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
        , Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas, String applicationId, Long taskId,
        List<RuleVariable> ruleVariables, String createTime
        , String partOfVariableName, String runDate,String runToday,String user, StringBuilder realColumn, boolean enumListNewValue, boolean numRangeNewValue,
        Map<String, String> selectResult, int unionWay) throws RuleVariableNotSupportException, RuleVariableNotFoundException {
        return abstractTranslator.persistenceTranslate(workFlowVersion, ruleId, ruleMetricIds, templateStatisticsInputMetas, applicationId, taskId, ruleVariables, createTime
                , partOfVariableName, runDate, runToday, user, realColumn, enumListNewValue, numRangeNewValue, selectResult, unionWay);
    }

    public static String jointVariableName(String prefix, String suffixes) {
        return ScalaCodeConstant.jointVariableName(prefix, suffixes);
    }

    public static String stateValInitString(String fieldName,String fieldValue) {
        return ScalaCodeConstant.stateValString(fieldName, fieldValue);
    }

    public static String stateVarInitString(String fieldName,String fieldValue) {
        return ScalaCodeConstant.stateVarString(fieldName, fieldValue);
    }

    public static String stateValInitNumber(String fieldName, Object fieldValue) {
        return ScalaCodeConstant.stateValNumber(fieldName, fieldValue);
    }

    public static String stateVarInitNumber(String fieldName, Object fieldValue) {
        return ScalaCodeConstant.stateVarNumber(fieldName, fieldValue);
    }

    public static String stateNumber(String fieldName, Object fieldValue) {
        return ScalaCodeConstant.stateNumber(fieldName, fieldValue);
    }

    public static String stateString(String fieldName, Object fieldValue) {
        return ScalaCodeConstant.stateString(fieldName, fieldValue);
    }

    public static String stateVarInitEmptyString(String fieldName) {
        return ScalaCodeConstant.stateVarString(fieldName, "");
    }

    private RuleDataSource getRuleDataSource(Rule rule){
        // dataSources info
        Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
        Iterator<RuleDataSource> it = ruleDataSources.iterator();
        RuleDataSource ruleDataSource = new RuleDataSource();
        while(it.hasNext()){
            ruleDataSource = it.next();
        }
        return ruleDataSource;
    }

    private long initExecParams(List<String> sparkSqlList, Map<String, String> execParams, String partitionAttr){
        if(MapUtils.isEmpty(execParams)){
            return 0;
        }
        long dataTime = 0;
        for (Map.Entry<String, String> entry : execParams.entrySet()) {
            String entryKey = String.valueOf(entry.getKey());
            String entryValue = String.valueOf(entry.getValue());
            if ("partition_day".equals(entryKey)) {
                dataTime = DateExprReplaceUtil.getDateTimeSeconds(entryValue);
            }
            if (StringUtils.isNotBlank(partitionAttr) && "partition_attr".equals(entryKey)) {
                sparkSqlList.add(stateValInitString(partitionAttr, entryValue));
            }
        }
        return dataTime;
    }

    private void analyseFieldsCountCode(List< String> sparkSqlList, Rule rule, String partOfVariableName, Map< String, String> execParams,String variableFormer) {
        if(StringUtils.isBlank(intellectCheckFieldsProjectName) ||! intellectCheckFieldsProjectName.equals(rule.getProject().getName())){
            return ;
        }
        String variableNamePrefix = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
        String partitionAttr = jointVariableName(variableNamePrefix, "partition_attr");
        String values = jointVariableName(variableNamePrefix, "values");
        String array = jointVariableName(variableNamePrefix, "array");
        RuleDataSource ruleDataSource = getRuleDataSource(rule);
        int datasourceTypeInt = ruleDataSource.getDatasourceType();
        String databaseNameStr = ruleDataSource.getDbName();
        String tableNameStr = ruleDataSource.getTableName();
        String proxyUserStr = ruleDataSource.getProxyUser();
        long dataTime = initExecParams(sparkSqlList, execParams, partitionAttr);
        Long ruleId = rule.getId();

        String rolName = "";
        Set<RuleVariable> variableSet = rule.getRuleVariables();
        for (RuleVariable ruleVariable : variableSet) {
            String placeholder = ruleVariable.getTemplateMidTableInputMeta().getPlaceholder();
            if("fields".equals(placeholder) || "${fields}".equals(placeholder)){
                rolName = ruleVariable.getValue();
            }
        }
        String nowTime = DateUtils.now();
        sparkSqlList.add(stateValInitNumber(array,variableFormer + ".collect"));
        sparkSqlList.add(stateValInitNumber(values, array+ "(0)(0).toString"));

        sparkSqlList.add(stateValInitNumber("conn",abstractTranslator.getDataSourceConn()));
        sparkSqlList.add(stateVarInitNumber("stmt", "conn.prepareStatement(\"\",1)"));
        String analyseFieldsUpsertSql = "\"INSERT INTO qualitis_imsmetric_fields_analyse (rule_id, analyse_type, datasource_type," +
                "database_name,table_name,field_name,value,data_date,create_time,update_time,datasource_user,partition_attrs,remark) VALUES %s " +
                "ON DUPLICATE KEY UPDATE value = values(value) , update_time = values(update_time)\"";
        String analyseFieldsUpsertSqlValue = "(" + ruleId +", 1, " + datasourceTypeInt + ",'" + databaseNameStr + "','"
                + tableNameStr + "','" + rolName +"',%s," + dataTime  + ",'" + nowTime + "','" + nowTime+ "','" + proxyUserStr +"','%s','') ";
        String finalSql = String.format(analyseFieldsUpsertSql, analyseFieldsUpsertSqlValue);
        sparkSqlList.add("stmt.executeUpdate(" + finalSql + ".format("+values+"," +partitionAttr+"))");
    }

    private void handleCustomRuleCode(List<String> sparkSqlList, Rule rule, String partOfVariableName,Map<String, String> execParams) {
        if(compareProjectName(rule)){
            String variableNamePrefix = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
            String ageing = jointVariableName(variableNamePrefix, "ageing");
            String partitionAttr = jointVariableName(variableNamePrefix, "partition_attr");
            // dataSources info
            Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
            Iterator<RuleDataSource> it = ruleDataSources.iterator();
            RuleDataSource ruleDataSource = new RuleDataSource();
            while(it.hasNext()){
                ruleDataSource = it.next();
            }
            int datasourceTypeInt = ruleDataSource.getDatasourceType();
            String databaseNameStr = ruleDataSource.getDbName();
            String tableNameStr = ruleDataSource.getTableName();
            String proxyUserStr = ruleDataSource.getProxyUser();

            // init exec params
            int enumMaxLength = 1000;
            int batchInsertSize = 1000;
            long dataTime = 0;
            if(!execParams.isEmpty()) {
                for (Map.Entry<String, String> entry : execParams.entrySet()) {
                    String entryKey = String.valueOf(entry.getKey());
                    String entryValue = String.valueOf(entry.getValue());
                    if ("partition_day".equals(entryKey)) {
                        dataTime = DateExprReplaceUtil.getDateTimeSeconds(entryValue);
                    }
                    if ("ageing".equals(entryKey)) {
                        sparkSqlList.add(stateValInitString(ageing, entryValue));
                    }
                    if ("partition_attr".equals(entryKey)) {
                        sparkSqlList.add(stateValInitString(partitionAttr, entryValue));
                    }
                    if ("enum_max_length".equals(entryKey)) {
                        try {
                            if(StringUtils.isNotBlank(entryValue)){
                                enumMaxLength = Integer.valueOf(entryValue);
                            }
                        } catch (Exception e) {
                            LOGGER.error("exec param error");
                        }
                    }
                    if ("batch_insert_size".equals(entryKey)) {
                        try {
                            if(StringUtils.isNotBlank(entryValue)){
                                batchInsertSize = Integer.valueOf(entryValue);
                            }
                        } catch (Exception e) {
                            LOGGER.error("exec param error");
                        }
                    }
                }
            }
            String rolName = "";
            Set<RuleVariable> variableSet = rule.getRuleVariables();
            for (RuleVariable ruleVariable : variableSet) {
                String placeholder = ruleVariable.getTemplateMidTableInputMeta().getPlaceholder();
                if("fields".equals(placeholder) || "${fields}".equals(placeholder)){
                    rolName = ruleVariable.getValue();
                }
            }

            // add spark sql
            int metricTypeInt = 1;
            String templateEnName = rule.getTemplate().getEnName();
            if(intellectCheckTableTemplateName.equals(templateEnName)){
                metricTypeInt = MetricTypeEnum.TABLE_STATISTICS.getCode();
            }else if(intellectCheckEnumTemplateName.equals(templateEnName)){
                metricTypeInt = MetricTypeEnum.ENUM_STATISTICS.getCode();
            }else if(intellectCheckOriginTemplateName.equals(templateEnName)){
                metricTypeInt = MetricTypeEnum.ORIGIN_STATISTICS.getCode();
            }else{
                throw new RuntimeException(ScalaCodeConstant.NOT_SUPPORTED_METRIC_TYPE);
            }

            String datasourceType = jointVariableName(variableNamePrefix, "datasource_type");
            String databaseName = jointVariableName(variableNamePrefix, "database_name");
            String tableName = jointVariableName(variableNamePrefix, "table_name");
            String metricType = jointVariableName(variableNamePrefix, "metric_type");
            String attrName = jointVariableName(variableNamePrefix, "attr_name");
            String rowkeyName = jointVariableName(variableNamePrefix, "rowkey_name");
            String proxyUser = jointVariableName(variableNamePrefix, "proxy_user");
            String dataArray = jointVariableName(variableNamePrefix, "array");
            String errorList = jointVariableName(variableNamePrefix, "error_list");
            String nowTime = jointVariableName(variableNamePrefix, "now_time");
            String insertList = jointVariableName(variableNamePrefix, "insert_list");
            String metricValue = jointVariableName(variableNamePrefix,"value");
            String metricId = jointVariableName(variableNamePrefix,"metric_id");
            String calcType = jointVariableName(variableNamePrefix,"calcType");
            String errorMsg =  jointVariableName(variableNamePrefix,"error_msg");
            String rowvalueenumName = jointVariableName(variableNamePrefix, "rowvalueenum_name");
            String dsDateTime = jointVariableName(variableNamePrefix, "ds_date_time");
            String querySql = jointVariableName(variableNamePrefix, "query_sql");
            String insertSql = jointVariableName(variableNamePrefix, "insert_sql");
            String insertCommonData = jointVariableName(variableNamePrefix, "insert_common_data");
            String insertData = jointVariableName(variableNamePrefix, "insert_data");
            String groupbyattrNames = jointVariableName(variableNamePrefix, "groupbyattr_names");
            String identifyRs = jointVariableName(variableNamePrefix, "identify_rs");
            String metricIdRs = jointVariableName(variableNamePrefix, "metricid_rs");
            String splitList = jointVariableName(variableNamePrefix, "split_list");

            sparkSqlList.add("import scala.math.BigDecimal");
            sparkSqlList.add(stateValInitNumber("conn",abstractTranslator.getDataSourceConn()));
            sparkSqlList.add(stateVarInitNumber("stmt", "conn.prepareStatement(\"\",1)"));
            sparkSqlList.add(stateValInitNumber(dataArray,variableNamePrefix + ".collect"));
            sparkSqlList.add(stateValInitNumber(datasourceType, datasourceTypeInt));
            sparkSqlList.add(stateValInitString(databaseName, databaseNameStr));
            sparkSqlList.add(stateValInitString(tableName, tableNameStr));
            sparkSqlList.add(stateValInitString(proxyUser, proxyUserStr));
            sparkSqlList.add(stateValInitNumber(metricType, metricTypeInt));
            sparkSqlList.add(stateVarInitString(attrName, rolName));
            sparkSqlList.add(stateVarInitEmptyString(rowkeyName + " : String"));
            sparkSqlList.add(stateVarInitEmptyString(rowvalueenumName +  " : String"));
            sparkSqlList.add(stateVarInitEmptyString(groupbyattrNames +  " : String"));
            sparkSqlList.add(stateValInitString(nowTime, DateUtils.now()));
            sparkSqlList.add(stateVarInitNumber(calcType,0));
            sparkSqlList.add(stateVarInitNumber(metricId,0.0));
            sparkSqlList.add(stateVarInitNumber(metricValue , "BigDecimal(\"0.0\")"));
            sparkSqlList.add(stateVarInitNumber(insertList + " : List[String]" ,"List()"));
            sparkSqlList.add(stateVarInitNumber(errorList + " : List[String]" ,"List()"));
            sparkSqlList.add(stateValInitString(insertCommonData, ScalaCodeConstant.INSERT_DATA_COMMON_SQL));
            sparkSqlList.add(stateValInitString(insertData, ScalaCodeConstant.getInsertDataSql(metricTypeInt, ruleDataSource, nowTime)));

            sparkSqlList.add(stateVarInitEmptyString(errorMsg));
            sparkSqlList.add(ScalaCodeConstant.errorMsg(errorMsg, databaseName, tableName, attrName, rowkeyName, rowvalueenumName, calcType));
            if(MetricTypeEnum.ORIGIN_STATISTICS.getCode() == metricTypeInt) {
                sparkSqlList.add(stateValInitString(querySql, ScalaCodeConstant.getQueryIdentifySql(metricTypeInt, ruleDataSource, attrName)));
                sparkSqlList.add(stateValInitString(insertSql, ScalaCodeConstant.getInsertIdentifySql(metricTypeInt, ruleDataSource, attrName, nowTime, ageing, partitionAttr)));
                sparkSqlList.add("import java.text.SimpleDateFormat");
                sparkSqlList.add(stateVarInitNumber("sdf", "new SimpleDateFormat(\"yyyyMMdd HH:mm:ss\")"));
                sparkSqlList.add("try{");
                sparkSqlList.add("for(i <- 0 to " + dataArray + ".length - 1){");
                sparkSqlList.add("try{");
                sparkSqlList.add(stateNumber(metricValue, "BigDecimal(" + dataArray + "(i)(0).toString)"));
                sparkSqlList.add(stateNumber(calcType, dataArray + "(i)(1).toString.toInt"));
                sparkSqlList.add(stateNumber(rowkeyName,dataArray + "(i)(2).toString"));
                sparkSqlList.add(ScalaCodeConstant.errorMsg(errorMsg, databaseName, tableName, attrName, rowkeyName, rowvalueenumName, calcType));
                sparkSqlList.add(stateValInitNumber(dsDateTime,"sdf.parse(" + dataArray + "(i)(3).toString + \" 00:00:00\").getTime/1000"));
                sparkSqlList.add(stateVarInitNumber(identifyRs, "stmt.executeQuery(" + querySql + ".format(" + rowkeyName + "))"));
                sparkSqlList.add("if(" + identifyRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, identifyRs + ".getInt(\"metric_id\")"));
                sparkSqlList.add("}else{");
                sparkSqlList.add(stateNumber("stmt", "conn.prepareStatement(" + insertSql + ".format(" + rowkeyName + "), 1)"));
                sparkSqlList.add("stmt.executeUpdate()");
                sparkSqlList.add(stateVarInitNumber(metricIdRs, "stmt.getGeneratedKeys()"));
                sparkSqlList.add("if(" + metricIdRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, metricIdRs + ".getInt(1)"));
                sparkSqlList.add("}");
                sparkSqlList.add("}");
                sparkSqlList.add(stateNumber(insertList, insertData + ".format(" + metricId + "," + metricValue + "," + dsDateTime +") +: " + insertList));
                sparkSqlList.add("} catch {");
            }else if(MetricTypeEnum.TABLE_STATISTICS.getCode() == metricTypeInt) {
                sparkSqlList.add(stateValInitString(querySql, ScalaCodeConstant.getQueryIdentifySql(metricTypeInt, ruleDataSource, attrName)));
                sparkSqlList.add(stateValInitString(insertSql, ScalaCodeConstant.getInsertIdentifySql(metricTypeInt, ruleDataSource, attrName, nowTime, ageing, partitionAttr)));
                sparkSqlList.add("try{");
                sparkSqlList.add("for(i <- 0 to " + dataArray + ".length - 1){");
                sparkSqlList.add("for(j <- 0 to " + dataArray + "(i).length - 1){");
                sparkSqlList.add("try{");
                // 无数据补零
                sparkSqlList.add("try{");
                sparkSqlList.add("if(j == 0){");
                sparkSqlList.add(stateNumber(calcType,1));
                sparkSqlList.add("}else if(j == 1){");
                sparkSqlList.add(stateNumber(calcType,5));
                sparkSqlList.add("}");
                sparkSqlList.add(stateNumber(metricValue, "BigDecimal(" + dataArray + "(i)(j).toString)"));
                sparkSqlList.add("} catch {");
                sparkSqlList.add("case e: Exception => {");
                sparkSqlList.add(stateNumber(metricValue, "BigDecimal(\"0.0\")"));
                sparkSqlList.add("}}");
                sparkSqlList.add(ScalaCodeConstant.errorMsg(errorMsg, databaseName, tableName, attrName, rowkeyName, rowvalueenumName, calcType));
                sparkSqlList.add(stateVarInitNumber(identifyRs, "stmt.executeQuery(" + querySql + ".format(" + calcType + "))"));
                sparkSqlList.add("if(" + identifyRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, identifyRs + ".getInt(\"metric_id\")"));
                sparkSqlList.add("}else{");
                sparkSqlList.add(stateNumber("stmt", "conn.prepareStatement(" + insertSql + ".format(" + calcType + "), 1)"));
                sparkSqlList.add("stmt.executeUpdate()");
                sparkSqlList.add(stateVarInitNumber(metricIdRs, "stmt.getGeneratedKeys()"));
                sparkSqlList.add("if(" + metricIdRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, metricIdRs + ".getInt(1)"));
                sparkSqlList.add("}");
                sparkSqlList.add("}");
                sparkSqlList.add(stateNumber(insertList, insertData + ".format(" + metricId + "," + metricValue + "," + dataTime +") +: " + insertList));
                sparkSqlList.add("} catch {");

            }else if(MetricTypeEnum.ENUM_STATISTICS.getCode() == metricTypeInt){
                sparkSqlList.add(stateValInitString(querySql, ScalaCodeConstant.getQueryIdentifySql(metricTypeInt, ruleDataSource, attrName)));
                sparkSqlList.add(stateValInitString(insertSql, ScalaCodeConstant.getInsertIdentifySql(metricTypeInt, ruleDataSource, attrName, nowTime, ageing, partitionAttr)));


                sparkSqlList.add("if(" + dataArray + ".length > " + enumMaxLength + "){");
                sparkSqlList.add("throw new RuntimeException(\"枚举类型规则计算数据量已超过最大长度，请检查分组字段\")");
                sparkSqlList.add("}");
                sparkSqlList.add("try{");
                sparkSqlList.add("for(i <- 0 to " + dataArray + ".length - 1){");
                sparkSqlList.add("for(j <- 0 to " + dataArray + "(i).length - 2){");
                sparkSqlList.add("try{");
                sparkSqlList.add("if(j == 0){");
                sparkSqlList.add(stateNumber(calcType,5));
                sparkSqlList.add("}else if(j == 1){");
                sparkSqlList.add(stateNumber(calcType,8));
                sparkSqlList.add("}");
                sparkSqlList.add(ScalaCodeConstant.errorMsg(errorMsg, databaseName, tableName, attrName, rowkeyName, rowkeyName, calcType));
                sparkSqlList.add("try{");
                sparkSqlList.add(stateNumber(rowvalueenumName,dataArray + "(i)(2).toString"));
                sparkSqlList.add("} catch {");
                sparkSqlList.add("case e: Exception => {");
                sparkSqlList.add(stateNumber(rowvalueenumName,"\"NULL\""));
//                sparkSqlList.add(stateNumber(errorList, "(" + errorMsg + "+\",name为空,\") +: " + errorList));
                sparkSqlList.add("}");
                sparkSqlList.add("}");
                sparkSqlList.add("try{");
                sparkSqlList.add(stateNumber(metricValue, "BigDecimal(" + dataArray + "(i)(j).toString)"));
                sparkSqlList.add("} catch {");
                sparkSqlList.add("case e: Exception =>  throw  new NullPointerException(\"metric_value值为空\")");
                sparkSqlList.add("}");
                sparkSqlList.add(ScalaCodeConstant.errorMsg(errorMsg, databaseName, tableName, attrName, rowkeyName, rowvalueenumName, calcType));
                sparkSqlList.add(stateVarInitNumber(identifyRs, "stmt.executeQuery(" + querySql + ".format(" + rowvalueenumName + "," + calcType +"))"));
                sparkSqlList.add("if(" + identifyRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, identifyRs + ".getInt(\"metric_id\")"));
                sparkSqlList.add("}else{");
                sparkSqlList.add(stateNumber("stmt", "conn.prepareStatement(" + insertSql + ".format(" + calcType + "," + rowvalueenumName +"), 1)"));
                sparkSqlList.add("stmt.executeUpdate()");
                sparkSqlList.add(stateVarInitNumber(metricIdRs, "stmt.getGeneratedKeys()"));
                sparkSqlList.add("if(" + metricIdRs + ".next()){");
                sparkSqlList.add(stateNumber(metricId, metricIdRs + ".getInt(1)"));
                sparkSqlList.add("}");
                sparkSqlList.add("}");
                sparkSqlList.add(stateNumber(insertList, insertData + ".format(" + metricId + "," + metricValue + "," + dataTime +") +: " + insertList));
                sparkSqlList.add("} catch {");

            }

            sparkSqlList.add(ScalaCodeConstant.exceptionCollect("NullPointerException", errorList, "指标数据为空", errorMsg));
            sparkSqlList.add(ScalaCodeConstant.exceptionCollect("NumberFormatException", errorList, "指标数据为空", errorMsg));
            sparkSqlList.add(ScalaCodeConstant.exceptionCollect("Exception", errorList, "scala执行异常", errorMsg));
            sparkSqlList.add("}");
            sparkSqlList.add("}");
            if(MetricTypeEnum.TABLE_STATISTICS.getCode() == metricTypeInt || MetricTypeEnum.ENUM_STATISTICS.getCode() == metricTypeInt) {
                sparkSqlList.add("}");
            }

            sparkSqlList.add("try{");
            sparkSqlList.add(insertList + ".grouped(" + batchInsertSize + ").toList.foreach { " + splitList + " =>");
            sparkSqlList.add("stmt.executeUpdate(" + insertCommonData + ".format(" + splitList + ".reverse.mkString(\",\")))");
            sparkSqlList.add("}");
            sparkSqlList.add("}catch{");
            sparkSqlList.add(ScalaCodeConstant.exceptionCollect("Exception", errorList, "写入指标数据异常", errorMsg));
            sparkSqlList.add("}");
            sparkSqlList.add("if(!" + errorList + ".isEmpty){");
            sparkSqlList.add("if(" + metricType + " == 1 && "  + errorList + ".reverse.mkString(\"\\n\").indexOf(\"指标数据为空:\") != -1" +  "){");
            sparkSqlList.add(variableNamePrefix + " = " + variableNamePrefix + ".na.drop()");
            sparkSqlList.add("}else{");
            sparkSqlList.add("throw new RuntimeException(" + errorList + ".reverse.mkString(\"\\n\"))");
            sparkSqlList.add("}");
            sparkSqlList.add("}");
            sparkSqlList.add("}catch{");
            sparkSqlList.add("case e: Exception => throw e");
            sparkSqlList.add("}finally{");
            sparkSqlList.add("stmt.close()");
            sparkSqlList.add("conn.close()");
            sparkSqlList.add("}");

        }
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
     * @param runToday
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
            , String runDate, String runToday, Map<String, String> selectResult, boolean midTableReUse, boolean unionAllForSaveResult, String filterFields, List<List<String>> tableEnvs, boolean shareConnect, String shareFromPart, Map<String, String> execParams) {
        String sparkSqlSentence;
        List<String> sparkSqlList = new ArrayList<>();
        boolean linePrimaryRepeat = QualitisConstants.isRepeatDataCheck(rule.getTemplate().getEnName());
        if (CollectionUtils.isEmpty(connParamMaps)) {

            if (CollectionUtils.isNotEmpty(tableEnvs)) {
                List<List<String>> sqlReplaceStrLists = QualitisCollectionUtils.getDescartes(tableEnvs);

                for (List<String> subList : sqlReplaceStrLists) {
                    StringBuilder envName = new StringBuilder();
                    for (String replaceStr : subList) {
                        String[] subStrs = replaceStr.split(SpecCharEnum.COLON.getValue());
                        envName.append("[").append(subStrs[2].split(SpecCharEnum.MINUS.getValue())[0]).append("]");
                        String registerTable = subStrs[1];
                        String realTable = subStrs[0];

                        sql = sql.replace(realTable, registerTable);
                    }
                    String partOfVariableNameWithEnv = partOfVariableName + envName.toString().replace("[", "").replace("]", "");
                    sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                    sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableNameWithEnv, "", "", "", RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType()));
                    sparkSqlList.add(sparkSqlSentence);

                    String variableFormer = getVariableNameByRule(partOfVariableNameWithEnv.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableNameWithEnv.split(SpecCharEnum.EQUAL.getValue())[1]);
                    String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableNameWithEnv.split(SpecCharEnum.EQUAL.getValue())[1]);
                    formatSchema(sparkSqlList, partOfVariableName, variableFormer, variableLatter);

                    selectResult.put(variableLatter, envName.toString());
                }
                String lastVariable = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "Last");
                unionAllSaveResult(lastVariable, selectResult, sparkSqlList, unionAllForSaveResult);
            } else {
                if (linePrimaryRepeat) {
                    sparkSqlList.add("val UUID = java.util.UUID.randomUUID.toString");
                }
                if(compareProjectName(rule)){
                    sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableName, filterFields);
                }else if(StringUtils.isNotBlank(intellectCheckFieldsProjectName) && intellectCheckFieldsProjectName.equals(rule.getProject().getName())){
                    sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableName, filterFields);
                }else{
                    sparkSqlSentence = getSparkSqlSentence(sql, partOfVariableName, filterFields, shareFromPart, "", RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType()));
                }
                LOGGER.info("Succeed to generate spark sql. sentence: {}", sparkSqlSentence);
                sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + " 的校验查询代码");
                sparkSqlList.add(sparkSqlSentence);
                // 特殊处理规则生成scala代码
                handleCustomRuleCode(sparkSqlList, rule, partOfVariableName,execParams);

                if (linePrimaryRepeat) {
                    handleLinePrimaryRepeat(sparkSqlList, partOfVariableName);
                }

                String variableFormer = getVariableNameByRule(partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[0], partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                String variableLatter = getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);
                formatSchema(sparkSqlList, partOfVariableName, variableFormer, variableLatter);
                analyseFieldsCountCode(sparkSqlList, rule, partOfVariableName,execParams, variableFormer);

                if (StringUtils.isNotEmpty(saveTableName) && ! MID_TABLE_NAME_PATTERN.matcher(saveTableName).find()) {
                    sparkSqlList.addAll(getSaveMidTableSentenceSettings());
                    sparkSqlList.addAll(getSaveMidTableSentence(saveTableName, partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1], runDate, runToday, midTableReUse));
                    LOGGER.info("Succeed to generate spark sql. sentence.");
                }
            }

            return sparkSqlList;
        } else {
            boolean saveMidTable = StringUtils.isNotEmpty(saveTableName) && ! MID_TABLE_NAME_PATTERN.matcher(saveTableName).find();
            // Repeat with envs. When polymerization, repeat one more time.
            selectResult.putAll(getSparkSqlSententceWithMysqlConnParams(rule, sql, partOfVariableName, connParamMaps, sparkSqlList, linePrimaryRepeat,  saveMidTable, saveTableName, runDate, runToday, midTableReUse, unionAllForSaveResult, filterFields, shareConnect, shareFromPart));
        }
        return sparkSqlList;
    }

    private void handleLinePrimaryRepeat(List<String> sparkSqlList, String fullName) {
        String suffix = fullName.split(SpecCharEnum.EQUAL.getValue())[1];
        sparkSqlList.add("val fillNullDF_" + suffix + " = " + getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], suffix) + ".na.fill(UUID)");
        sparkSqlList.add("val fillNullWithFullLineWithHashDF_" + suffix + " = fillNullDF_" + suffix + ".withColumn(\"qualitis_full_line_value\", to_json(struct($\"*\"))).withColumn(\"md5\", md5(to_json(struct($\"*\"))))");
        sparkSqlList.add("fillNullWithFullLineWithHashDF_" + suffix + ".registerTempTable(\"tmp_table_" + suffix + "\")");
        sparkSqlList.add("val " + getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], suffix) + " = spark.sql(\"select md5, count(1) as md5_count from tmp_table_" + suffix + " group by md5 having count(*) > 1\")");
    }

    private Map<String, String> getSparkSqlSententceWithMysqlConnParams(Rule rule, String sql, String partOfVariableName, List<Map<String, Object>> connParamMaps, List<String> sparkSqlList
            , boolean linePrimaryRepeat, Boolean saveMidTable, String saveTableName, String runDate, String runToday, boolean midTableReUse, boolean unionAllForSaveResult, String filterFields, boolean shareConnect, String shareFromPart) {
        Map<String, String> selectResult = new HashMap<>(connParamMaps.size());
        for (Map<String, Object> connParams : connParamMaps) {
            String envName = ((String) connParams.get("envName")).split(SpecCharEnum.MINUS.getValue())[0];
            if (StringUtils.isEmpty(envName)) {
                continue;
            }
            String tmpVariableName = partOfVariableName + envName;
            String variableFormer = getVariableNameByRule(tmpVariableName.split(SpecCharEnum.EQUAL.getValue())[0], tmpVariableName.split(SpecCharEnum.EQUAL.getValue())[1]);

            if (shareConnect) {
                sparkSqlList.add("// 生成规则 " + partOfVariableName.split(SpecCharEnum.EQUAL.getValue())[1] + "，在环境 " + envName + " 的校验查询代码");
                sparkSqlList.add(getSparkSqlSentence(sql, tmpVariableName, filterFields, shareFromPart, SpecCharEnum.BOTTOM_BAR.getValue() + envName, RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())));
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
            sparkSqlList.addAll(getSaveMidTableSentence(saveTableName, runDate, runToday, midTableReUse, selectResult));
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

    private List<String> getSaveMidTableSentence(String saveMidTableName, String runDate, String runToday, boolean midTableReUse, Map<String, String> selectResult) {
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
            saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION_WITH_ENV, value, key, saveMidTableName, runDate, runToday, date, format));
        }
        saveSqls.add(ELSE_EXIST);
        for (Map.Entry<String, String> entry : selectResult.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE_WITH_ENV, value, key, saveMidTableName, runDate, runToday, date, format));
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
            String str1 = "val "  + "schemas_" + prefix + " = " + variableFormer + ".schema.fields.map(f => f.name).toList";
            String str2 = "val replacedSchemas_" + prefix + " = schemas_" + prefix + ".map(s => s.replaceAll(\"[()]\", \"\")).toList";
            String str3 = "val " + variableLatter + " = " + variableFormer + ".toDF(" + "replacedSchemas_" + prefix + ": _*)";
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

    private List<String> getSaveMidTableSentence(String saveMidTableName, Integer count, String runDate, String runToday, boolean midTableReUse) {
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

        saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION, "", getVariableName(count), saveMidTableName, runDate, runToday, date, format));
        saveSqls.add(ELSE_EXIST);

        saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE, "", getVariableName(count), saveMidTableName, runDate, runToday, date, format));
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private List<String> getSaveMidTableSentence(String saveMidTableName, String partOfVariableName, String runDate, String runToday, boolean midTableReUse) {
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

        saveSqls.addAll(parsefirstHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_INSERT_OVERWRITE_PARTITION, "", getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName), saveMidTableName, runDate, runToday, date, format));
        saveSqls.add(ELSE_EXIST);

        saveSqls.addAll(parseSecondHalf(SAVE_MID_TABLE_SENTENCE_TEMPLATE_CREATE, "", getVariableNameByRule(OptTypeEnum.STATISTIC_DF.getMessage(), partOfVariableName), saveMidTableName, runDate, runToday, date, format));
        saveSqls.add(END_EXIST);
        return saveSqls;
    }

    private List<String> parsefirstHalf(String saveMidTableSentenceTemplateInsertOverwritePartition, String envName, String val, String saveMidTableName
            , String runDate, String runToday, Date date, SimpleDateFormat format) {
        List<String> saveSqls = new ArrayList<>();
        String result = getLastRunTime(runDate, runToday, date, format);
        if (StringUtils.isEmpty(envName) && StringUtils.isEmpty(val)) {
            saveSqls.add(saveMidTableSentenceTemplateInsertOverwritePartition
                    .replace("${QUALITIS_PARTITION_KEY}", result)
                    .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        } else {
            saveSqls.add(saveMidTableSentenceTemplateInsertOverwritePartition
                    .replace("${QUALITIS_PARTITION_KEY}", result)
                    .replace("${QUALITIS_PARTITION_KEY_ENV}", envName)
                    .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        }

        return saveSqls;
    }

    public String getLastRunTime(String runDate, String runToday, Date date, SimpleDateFormat format) {
        if (StringUtils.isNotBlank(runDate)) {
            return runDate;
        }
        return StringUtils.isNotBlank(runToday) ? runToday : format.format(date);
    }

    private List<String> parseSecondHalf(String saveMidTableSentenceTemplateCreate, String envName, String val, String saveMidTableName, String runDate, String runToday
            , Date date, SimpleDateFormat format) {
        List<String> saveSqls = new ArrayList<>();
        String result = getLastRunTime(runDate, runToday, date, format);
        if (StringUtils.isEmpty(envName) && StringUtils.isEmpty(val)) {
            saveSqls.add(
                    saveMidTableSentenceTemplateCreate.replace("${QUALITIS_PARTITION_KEY}", result)
                            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        } else {
            saveSqls.add(
                    saveMidTableSentenceTemplateCreate.replace("${QUALITIS_PARTITION_KEY}", result)
                            .replace("${QUALITIS_PARTITION_KEY_ENV}", envName)
                            .replace(SAVE_MID_TABLE_NAME_PLACEHOLDER, saveMidTableName).replace(VARIABLE_NAME_PLACEHOLDER, val));
        }
        return saveSqls;
    }

    private String getSparkSqlSentence(String sql, String fullName, String filterFields, String shareFromPart, String envName, boolean isCustomRule) {
        if (! isCustomRule) {
            sql = sql.replace("\"", "\\\"");
        }

        if (StringUtils.isNotEmpty(shareFromPart)) {
            sql = sql.replace(shareFromPart, commonTableName + envName);
        }
        String str = SPARK_SQL_TEMPLATE.replace(SPARK_SQL_TEMPLATE_PLACEHOLDER, sql);
        if (StringUtils.isNotEmpty(filterFields)) {
            str += filterFields;
        }
        return str.replace(VARIABLE_NAME_PLACEHOLDER, getVariableNameByRule(fullName.split(SpecCharEnum.EQUAL.getValue())[0], fullName.split(SpecCharEnum.EQUAL.getValue())[1]));
    }

    private String getSparkSqlSentence(String sql, String fullName, String filterFields) {
        sql = sql.replace("\"", "\\\"");
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
     * @param realColumn
     * @param dbTableMap             for pick up source db.table & target db.table
     * @param date
     * @param standardValueVersionId
     * @param shareFromPart
     * @param runDate
     * @param runToday
     * @param engineType
     * @return
     * @throws ConvertException
     */
    private String replaceVariable(String template, List<RuleVariable> variables, String filter, StringBuilder realColumn, Map<String, String> dbTableMap, Date date, Long standardValueVersionId, String createUser, String shareFromPart, String runDate, String runToday, String engineType) throws ConvertException, UnExpectedRequestException, MetaDataAcquireFailedException {

        String sqlAction = template;

        if (StringUtils.isNotEmpty(shareFromPart)) {
            sqlAction = sqlAction.replace(FILTER_PLACEHOLDER, "true");
        }
        if (StringUtils.isNotBlank(filter)) {
            if (StringUtils.isNotBlank(runDate)) {
                filter = filter.replace("${run_date}", runDate);
//                filter = filter.replace("${run_date_std}", runDate);
            }
            if (StringUtils.isNotBlank(runToday)) {
                filter = filter.replace("${run_today}", runToday);
//                filter = filter.replace("${run_today_std}", runToday);
            }

            String tmpfilter = DateExprReplaceUtil.replaceFilter(date, filter);
            sqlAction = sqlAction.replace(FILTER_PLACEHOLDER, tmpfilter);
            LOGGER.info("Succeed to replace {} into {}", FILTER_PLACEHOLDER, tmpfilter);
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
            } else if ("left_collect_sql".equals(midInputMetaPlaceHolder)) {
                String value = ruleVariable.getValue();
                if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(runDate)) {
                    value = value.replace("${run_date}", runDate);
//                    value = value.replace("${run_date_std}", runDate);
                }
                if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(runToday)) {
                    value = value.replace("${run_today}", runToday);
//                    value = value.replace("${run_today_std}", runToday);
                }
                ruleVariable.setOriginValue(DateExprReplaceUtil.replaceRunDate(date, value));
                dbTableMap.put("left_collect_sql", ruleVariable.getOriginValue());
            } else if ("right_collect_sql".equals(midInputMetaPlaceHolder)) {
                String value = ruleVariable.getValue();
                if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(runDate)) {
                    value = value.replace("${run_date}", runDate);
//                    value = value.replace("${run_date_std}", runDate);
                }
                if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(runToday)) {
                    value = value.replace("${run_today}", runToday);
//                    value = value.replace("${run_today_std}", runToday);
                }
                ruleVariable.setOriginValue(DateExprReplaceUtil.replaceRunDate(date, value));
                dbTableMap.put("right_collect_sql", ruleVariable.getOriginValue());
            } else if (TemplateInputTypeEnum.FIELD.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType()) && Boolean.TRUE.equals(ruleVariable.getTemplateMidTableInputMeta().getFieldMultipleChoice())) {
                realColumn.append(ruleVariable.getValue());
            } else if (TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType()) && standardValueVersionId != null) {
                LOGGER.info("Start to check current standard value version is or not the most new version. Version ID: " + standardValueVersionId);
                StandardValueVersion standardValueVersion = standardValueVersionDao.findById(standardValueVersionId);
                // DMS 实时数据同步  如何保证拉取全量的dms的"编码取值" size临时采用Integer.MAX_VALUE
                // 1.没有编码，提交异常返回; 2.有编码，但编码为空串，覆盖
                if (standardValueVersion != null) {
                    LOGGER.info("Start to Real time synchronization of DMS encoding values");
                    if (StringUtils.isNotBlank(standardValueVersion.getCode())) {
                        Map<String, Object> standardCodeTable = dataStandardClient.getStandardCodeTable(0, Integer.MAX_VALUE, createUser, standardValueVersion.getCode());
                        List<Map<String, Object>> encodingValueInfo = (List<Map<String, Object>>) standardCodeTable.get("content");
                        if (CollectionUtils.isEmpty(encodingValueInfo)) {
                            throw new UnExpectedRequestException("{&GET_ENCODING_VALUE_FROM_DATASHAPIS_IS_EMPTY}");
                        }

                        StringBuilder temp = new StringBuilder();
                        for (Map<String, Object> map : encodingValueInfo) {
                            if (map.get("codeTableValue") == null) {
                                continue;
                            }

                            if (!"".equals(map.get("codeTableValue").toString())) {
                                temp.append("'" + map.get("codeTableValue").toString() + "'").append(SpecCharEnum.COMMA.getValue());
                            }
                        }

                        String result = temp != null && temp.length() > 0 ? temp.deleteCharAt(temp.length() - 1).toString() : "";
                        standardValueVersion.setContent(result);
                        standardValueVersionDao.saveStandardValueVersion(standardValueVersion);
                        if (StringUtils.isNotBlank(result) && EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) && "standard_value".equals(midInputMetaPlaceHolder) && ! result.contains("'") && ! result.contains("\"")) {
                            result = StringUtils.join(Arrays.asList(result.split(SpecCharEnum.COMMA.getValue())).stream().map(ele -> "'" + ele + "'").collect(Collectors.toList()), SpecCharEnum.COMMA.getValue());
                        }
                        sqlAction = sqlAction.replaceAll(placeHolder, result);
                    } else {
                        String result = standardValueVersion.getContent();
                        if (StringUtils.isNotBlank(result) && EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) && "standard_value".equals(midInputMetaPlaceHolder) && ! result.contains("'") && ! result.contains("\"")) {
                            result = StringUtils.join(Arrays.asList(result.split(SpecCharEnum.COMMA.getValue())).stream().map(ele -> "'" + ele + "'").collect(Collectors.toList()), SpecCharEnum.COMMA.getValue());
                        }
                        sqlAction = sqlAction.replaceAll(placeHolder, result);
                    }
                    continue;
                }
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
                if (placeHolder.contains("left_collect_sql") || placeHolder.contains("right_collect_sql")) {
                    sqlAction = sqlAction.replaceAll(placeHolder, ruleVariable.getOriginValue());
                } else {
                    String ruleVariableValue = ruleVariable.getValue();
                    if (StringUtils.isNotBlank(ruleVariableValue) && EngineTypeEnum.TRINO_ENGINE.getMessage().equals(engineType) && "enumerated_list".equals(midInputMetaPlaceHolder) && ! ruleVariableValue.contains("'") && ! ruleVariableValue.contains("\"")) {
                        ruleVariableValue = StringUtils.join(Arrays.asList(ruleVariableValue.split(SpecCharEnum.COMMA.getValue())).stream().map(ele -> "'" + ele + "'").collect(Collectors.toList()), SpecCharEnum.COMMA.getValue());
                    }

                    sqlAction = sqlAction.replaceAll(placeHolder, ruleVariableValue);
                }
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

    /**
     * check rule name does it contain special characters and replace it to hash value
     *
     * @param ruleName
     * @return
     */
    private String checkRuleNameWhetherContainSpecialCharacters(String ruleName) {
        String resultRuleName = MID_TABLE_NAME_PATTERN.matcher(ruleName).find() ?
            MID_TABLE_NAME_PATTERN.matcher(ruleName).replaceAll("") + SpecCharEnum.BOTTOM_BAR.getValue() + generateShortHash(ruleName).toLowerCase() : ruleName;
        if (StringUtils.isNotBlank(resultRuleName)) {
            return resultRuleName;
        } else {
            return ruleName;
        }
    }

    public static String generateShortHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            String base64Encoded = Base64.getEncoder().encodeToString(hash);
            return base64Encoded.substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "";
    }

    private boolean compareProjectName(Rule rule){
        return intellectCheckProjectName.equals(rule.getProject().getName());
    }

}
