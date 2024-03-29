package com.webank.wedatasphere.qualitis.rule.builder;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.ContrastTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.MappingTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.util.DatasourceEnvUtil;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 14:14
 */
@Service
public class AddMultiRuleRequestBuilder implements AddRequestBuilder {

    private AddMultiSourceRuleRequest addMultiSourceRuleRequest;
    private boolean uploadRuleMetricValue;
    private boolean uploadAbnormalValue;
    private String ruleMetricEnCode;
    private Template template;
    private String ruleDetail;
    private String ruleCnName;
    private Project project;
    private String ruleName;
    private String userName;

    private String proxyUser;

    private String datasourceCluster;

    private static Integer TWO = 2;

    private static Integer FOUR = 4;
    private static final String COMMA = ".";

    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>();
    private static final Map<String, Integer> OPERATION_CODE = new LinkedHashMap<String, Integer>();

    static {
        ALERT_LEVEL_CODE.put("CRITICAL", 1);
        ALERT_LEVEL_CODE.put("MAJOR", 2);
        ALERT_LEVEL_CODE.put("MINOR", 3);
        ALERT_LEVEL_CODE.put("WARNING", 4);
        ALERT_LEVEL_CODE.put("INFO", 5);

        OPERATION_CODE.put("!=", 2);
        OPERATION_CODE.put(">=", 4);
        OPERATION_CODE.put("<=", 6);
        OPERATION_CODE.put("=", 1);
        OPERATION_CODE.put(">", 3);
        OPERATION_CODE.put("<", 5);
    }

    private static final String AND = "and";
    private static final String TMP_1 = "tmp1.";
    private static final String TMP_2 = "tmp2.";

    private static final Pattern STATEMENT_ELEMENT = Pattern.compile("(tmp1\\.[0-9a-zA-Z_]+|tmp2\\.[0-9a-zA-Z_]+)");

    private RuleGroupDao ruleGroupDao;
    private RuleMetricDao ruleMetricDao;
    private MetaDataClient metaDataClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddMultiRuleRequestBuilder.class);

    public AddMultiRuleRequestBuilder() {
        addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
    }

    public AddMultiRuleRequestBuilder(RuleMetricDao ruleMetricDao, MetaDataClient metaDataClient) {
        this.ruleMetricDao = ruleMetricDao;
        this.metaDataClient = metaDataClient;
        addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
    }

    public AddMultiRuleRequestBuilder(RuleMetricDao ruleMetricDao, MetaDataClient metaDataClient, String datasourceCluster) {
        this.ruleMetricDao = ruleMetricDao;
        this.metaDataClient = metaDataClient;
        this.datasourceCluster = datasourceCluster;
        addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
    }

    public AddMultiRuleRequestBuilder(RuleMetricDao ruleMetricDao, RuleGroupDao ruleGroupDao, MetaDataClient metaDataClient, String datasourceCluster) {
        this.ruleGroupDao = ruleGroupDao;
        this.ruleMetricDao = ruleMetricDao;
        this.metaDataClient = metaDataClient;
        this.datasourceCluster = datasourceCluster;
        addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean abortOnFailure, String alertInfo)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
                                                     boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        // Init template argument.
        templateArgumentSettingInit();
        // Datasource
        StringBuilder clusterStringBuffer = new StringBuilder(cluster);
        solveDatasource(clusterStringBuffer, datasource);

        addMultiSourceRuleRequest.setClusterName(clusterStringBuffer.toString());
        addMultiSourceRuleRequest.setLoginUser(userName);
        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        // Alert info.
        alertSetting(alertInfo);
        // Task running info.
        taskSetting(deleteFailCheckResult, abortOnFailure, execParams);

        // Init alarm properties.
        List<AlarmConfigRequest> alarmVariable = new ArrayList<>(1);
        initAlarm(alarmVariable, uploadRuleMetricValue, uploadAbnormalValue);
        return this;
    }

    private void templateArgumentSettingInit() {
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>(4);
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.CONTRAST_TYPE.getCode())) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), String.valueOf(ContrastTypeEnum.FULL_OUTER_JOIN.getCode())));
                addMultiSourceRuleRequest.setContrastType(ContrastTypeEnum.FULL_OUTER_JOIN.getCode());
            }
            if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode())) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), "true"));
            }
        }
        addMultiSourceRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource,
                                                     List<TemplateArgumentRequest> templateArgumentRequests,
                                                     boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure,
                                                     String execParams) throws Exception {
        return this;
    }

    private void initAlarm(List<AlarmConfigRequest> alarmVariable, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) {
        AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
        addMultiSourceRuleRequest.setAlarm(true);
        alarmConfigRequest.setUploadAbnormalValue(uploadAbnormalValue);
        alarmConfigRequest.setUploadRuleMetricValue(uploadRuleMetricValue);
        alarmConfigRequest.setOutputMetaId(template.getTemplateOutputMetas().iterator().next().getId());
        alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FIXED_VALUE.getCode());
        alarmConfigRequest.setCompareType(CompareTypeEnum.EQUAL.getCode());
        alarmConfigRequest.setThreshold(0.0);
        alarmVariable.add(alarmConfigRequest);
        setUploadAbnormalValue(uploadAbnormalValue);
        setUploadRuleMetricValue(uploadRuleMetricValue);
        addMultiSourceRuleRequest.setAlarmVariable(alarmVariable);
        addMultiSourceRuleRequest.setUploadAbnormalValue(uploadAbnormalValue);
        addMultiSourceRuleRequest.setUploadRuleMetricValue(uploadRuleMetricValue);
    }

    private void taskSetting(boolean deleteFailCheckResult, boolean abortOnFailure, String execParams) {
        addMultiSourceRuleRequest.setDeleteFailCheckResult(deleteFailCheckResult);
        addMultiSourceRuleRequest.setAbortOnFailure(abortOnFailure);
        if (StringUtils.isNotBlank(execParams)) {
            addMultiSourceRuleRequest.setSpecifyStaticStartupParam(true);
            addMultiSourceRuleRequest.setStaticStartupParam(execParams);
        } else {
            addMultiSourceRuleRequest.setSpecifyStaticStartupParam(false);
        }
    }

    private void alertSetting(String alertInfo) throws UnExpectedRequestException {
        if (StringUtils.isNotBlank(alertInfo)) {
            String[] alertStrs = alertInfo.split(SpecCharEnum.COLON.getValue());
            String alertLevel = alertStrs[0].toUpperCase();
            if (ALERT_LEVEL_CODE.keySet().contains(alertLevel)) {
                addMultiSourceRuleRequest.setAlert(true);
                addMultiSourceRuleRequest.setAlertLevel(ALERT_LEVEL_CODE.get(alertLevel));
                addMultiSourceRuleRequest.setAlertReceiver(alertStrs[1].trim());
            } else {
                throw new UnExpectedRequestException("Please check alert level name. Alert name: " + alertLevel);
            }

        } else {
            addMultiSourceRuleRequest.setAlert(false);
        }
    }

    private void automaticProjectRuleSetting() {
        addMultiSourceRuleRequest.setRuleName(ruleName);
        addMultiSourceRuleRequest.setRuleDetail(ruleDetail);
        addMultiSourceRuleRequest.setRuleCnName(ruleCnName);
        addMultiSourceRuleRequest.setProjectId(project.getId());
        addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(template.getId());
    }

    private Map<String, List<ColumnInfoDetail>> solveDatasource(StringBuilder cluster, String datasource) throws Exception {
        MultiDataSourceConfigRequest source;
        MultiDataSourceConfigRequest target;

        String[] datasourceStrs = datasource.split(SpecCharEnum.VERTICAL_BAR.getValue());
        String sourcStr = datasourceStrs[0];
        String targetStr = datasourceStrs[1];
        StringBuilder clusterBuffer = new StringBuilder(cluster);

        StringBuilder dataSourceId = new StringBuilder();
        StringBuilder dataSourceName = new StringBuilder();
        StringBuilder envsStringBuffer = new StringBuilder();
        DatasourceEnvUtil.getDatasourceIdOrName(dataSourceId, dataSourceName, envsStringBuffer, clusterBuffer);
        target = toRequest(targetStr, dataSourceId, dataSourceName);
        source = toRequest(sourcStr, dataSourceId, dataSourceName);
        addMultiSourceRuleRequest.setTarget(target);
        addMultiSourceRuleRequest.setSource(source);

        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }

        List<ColumnInfoDetail> leftCols;
        List<ColumnInfoDetail> rightCols;
        try {
            leftCols = metaDataClient.getColumnInfo(cluster.toString(), source.getDbName(), source.getTableName(), StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
            rightCols = metaDataClient.getColumnInfo(cluster.toString(), target.getDbName(), target.getTableName(), StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
        } catch (Exception e) {
            throw new UnExpectedRequestException("Datasource metadata api is invalid.", 500);
        }

        Map<String, List<ColumnInfoDetail>> colsMap = new HashMap<>(2);
        colsMap.put(TMP_1, leftCols);
        colsMap.put(TMP_2, rightCols);

        return colsMap;
    }

    private MultiDataSourceConfigRequest toRequest(String dataSourcStr, StringBuilder dataSourceId, StringBuilder dataSourceName) throws Exception {
        String realUserName = StringUtils.isNotBlank(proxyUser) ? proxyUser : userName;
        String realClusterName = this.datasourceCluster;

        String[] datasourceStrs = dataSourcStr.split(SpecCharEnum.COLON.getValue());
        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String[] datasources = datasourceStrs[0].split(SpecCharEnum.PERIOD.getValue());
        String filter = datasourceStrs[1];
        if (datasources.length != TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String database = datasources[0];
        String table = datasources[1];
        StringBuilder dataSourceType = new StringBuilder();
        StringBuilder envsStringBuffer = new StringBuilder();
        List<DataSourceEnvRequest> dataSourceEnvRequests = new ArrayList<>();
        DatasourceEnvUtil.constructDatasourceAndEnv(dataSourceId, dataSourceName, dataSourceType, dataSourceEnvRequests, envsStringBuffer, realUserName, realClusterName, metaDataClient, "", "", new ArrayList<ColumnInfoDetail>());

        if (StringUtils.isNotEmpty(dataSourceId.toString()) || StringUtils.isNotEmpty(dataSourceName.toString())) {
            return new MultiDataSourceConfigRequest(database, table, filter, Long.parseLong(dataSourceId.toString()), dataSourceName.toString(), dataSourceType.toString());
        }

        return new MultiDataSourceConfigRequest(database, table, filter, realUserName);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String mappings, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        // Init template argument.
        templateArgumentSettingInit();
        // Datasource
        StringBuilder clusterStringBuffer = new StringBuilder(cluster);
        Map<String, List<ColumnInfoDetail>> colsMap = solveDatasource(clusterStringBuffer, datasource);
        // Mapping to join columns
        solveMapping(mappings, colsMap, MappingTypeEnum.CONNECT_FIELDS.getCode());
        solveMapping(mappings, colsMap, MappingTypeEnum.MATCHING_FIELDS.getCode());

        addMultiSourceRuleRequest.setClusterName(clusterStringBuffer.toString());
        addMultiSourceRuleRequest.setLoginUser(userName);
        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        // Alert info.
        alertSetting(alertInfo);

        // Task running info.
        taskSetting(deleteFailCheckResult, abortOnFailure, execParams);
        // Init alarm properties.
        List<AlarmConfigRequest> alarmVariable = new ArrayList<>(1);

        initAlarm(alarmVariable, uploadRuleMetricValue, uploadAbnormalValue);
        return this;
    }

    private void solveMapping(String mappings, Map<String, List<ColumnInfoDetail>> colsMap, Integer mappingType) {
        List<MultiDataSourceJoinConfigRequest> requests = new ArrayList<>();
        String[] mappingStrs = mappings.toLowerCase().split(AND);

        for (String currentMapping : mappingStrs) {
            List<MultiDataSourceJoinColumnRequest> left = new ArrayList<>();
            List<MultiDataSourceJoinColumnRequest> right = new ArrayList<>();

            MultiDataSourceJoinConfigRequest multiDataSourceJoinConfigRequest = new MultiDataSourceJoinConfigRequest();

            for (Map.Entry<String, Integer> entry : OPERATION_CODE.entrySet()) {
                if (currentMapping.contains(entry.getKey().toString())) {
                    multiDataSourceJoinConfigRequest.setOperation((Integer) entry.getValue());
                    String[] statements = currentMapping.split(entry.getKey().toString());
                    String leftStatement = statements[0];
                    String rightStatement = statements[1];
                    multiDataSourceJoinConfigRequest.setLeftStatement(leftStatement);
                    multiDataSourceJoinConfigRequest.setRightStatement(rightStatement);

                    // Statement to request
                    parseStatementColumns(colsMap.get(TMP_1), left, leftStatement);
                    parseStatementColumns(colsMap.get(TMP_2), right, rightStatement);

                    multiDataSourceJoinConfigRequest.setLeft(left);
                    multiDataSourceJoinConfigRequest.setRight(right);
                    break;
                }
            }

            requests.add(multiDataSourceJoinConfigRequest);
        }
        List<TemplateArgumentRequest> templateArgumentRequests= Lists.newArrayList();

        if (MappingTypeEnum.CONNECT_FIELDS.getCode().equals(mappingType)) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.CONNECT_FIELDS.getCode());
            templateArgumentRequest.setArgumentValue(CustomObjectMapper.transObjectToJson(requests));
            templateArgumentRequests.add(templateArgumentRequest);
        } else if (MappingTypeEnum.MATCHING_FIELDS.getCode().equals(mappingType)) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode());
            templateArgumentRequest.setArgumentValue(CustomObjectMapper.transObjectToJson(requests));
            templateArgumentRequests.add(templateArgumentRequest);
        }
        addMultiSourceRuleRequest.getTemplateArgumentRequests().addAll(templateArgumentRequests);
    }

    private void parseStatementColumns(List<ColumnInfoDetail> cols, List<MultiDataSourceJoinColumnRequest> joinColumnRequests, String statement) {
        Matcher m = STATEMENT_ELEMENT.matcher(statement);

        while (m.find()) {
            String element = m.group();
            String colNameWithOutTmp = element.replace("tmp1.", "").replace("tmp2.", "");
            String type = cols.stream().filter(field -> field.getFieldName().equals(colNameWithOutTmp)).map(ColumnInfoDetail::getDataType).collect(Collectors.joining());
            joinColumnRequests.add(new MultiDataSourceJoinColumnRequest(element, type));
        }
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String mappings, String compareCols, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        // Init template argument.
        templateArgumentSettingInit();
        // Datasource
        StringBuilder clusterStringBuffer = new StringBuilder(cluster);
        Map<String, List<ColumnInfoDetail>> colsMap = solveDatasource(clusterStringBuffer, datasource);
        // Mapping to join columns
        solveMapping(mappings, colsMap, MappingTypeEnum.CONNECT_FIELDS.getCode());
        solveMapping(compareCols, colsMap, MappingTypeEnum.MATCHING_FIELDS.getCode());

        addMultiSourceRuleRequest.setClusterName(clusterStringBuffer.toString());
        addMultiSourceRuleRequest.setLoginUser(userName);
        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        // Alert info.
        alertSetting(alertInfo);

        // Task running info.
        taskSetting(deleteFailCheckResult, abortOnFailure, execParams);
        // Init alarm properties.
        List<AlarmConfigRequest> alarmVariable = new ArrayList<>(1);

        initAlarm(alarmVariable, uploadRuleMetricValue, uploadAbnormalValue);
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetric(String ruleMetricName) throws UnExpectedRequestException {
        RuleMetric ruleMetricInDb = ruleMetricDao.findByName(ruleMetricName);

        if (ruleMetricInDb != null) {
            setRuleMetricEnCode(ruleMetricInDb.getEnCode());
            return this;
        }

        String[] infos = ruleMetricName.split(SpecCharEnum.BOTTOM_BAR.getValue());
        if (infos.length != FOUR) {
            throw new UnExpectedRequestException("The metric name does not meet specifications");
        }
        String en = infos[2];
        List<String> ruleMetricNames = new ArrayList<>(1);
        ruleMetricNames.add(ruleMetricName);
        addMultiSourceRuleRequest.setRuleMetricNamesForBdpClient(ruleMetricNames);
        setRuleMetricEnCode(en);
        return this;
    }

    @Override
    public AddRequestBuilder addExecutionParameter(String executionParameterName) throws UnExpectedRequestException {
        addMultiSourceRuleRequest.setExecutionParametersName(executionParameterName);
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetricWithCheck(String ruleMetricName, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) {
        return this;
    }

    public AlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, String value) {
        AlarmConfigRequest newAlarmConfigRequest = new AlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
        newAlarmConfigRequest.setOutputMetaId(template.getTemplateOutputMetas().iterator().next().getId());
        newAlarmConfigRequest.setRuleMetricEnCode(getRuleMetricEnCode());
        newAlarmConfigRequest.setThreshold(Double.parseDouble(value));

        switch (checkTemplateEnum) {
            case 1:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.MONTH_FLUCTUATION.getCode());
                break;
            case 2:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.WEEK_FLUCTUATION.getCode());
                break;
            case 3:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.DAY_FLUCTUATION.getCode());
                break;
            case 4:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FIXED_VALUE.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 5:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode());
                break;
            case 6:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode());
                break;
            case 7:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.SEASON_RING_GROWTH.getCode());
                break;
            case 8:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.MONTH_RING_GROWTH.getCode());
                break;
            case 9:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.WEEK_RING_GROWTH.getCode());
                break;
            case 10:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.DAY_RING_GROWTH.getCode());
                break;
            case 11:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HOUR_RING_GROWTH.getCode());
                break;
            case 12:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.YEAR_ON_YEAR.getCode());
                break;
            default:
                addMultiSourceRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(String value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    public AlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, double value) {
        AlarmConfigRequest newAlarmConfigRequest = new AlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
        newAlarmConfigRequest.setOutputMetaId(template.getTemplateOutputMetas().iterator().next().getId());
        newAlarmConfigRequest.setRuleMetricEnCode(getRuleMetricEnCode());
        newAlarmConfigRequest.setThreshold(value);

        switch (checkTemplateEnum) {
            case 1:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.MONTH_FLUCTUATION.getCode());
                break;
            case 2:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.WEEK_FLUCTUATION.getCode());
                break;
            case 3:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.DAY_FLUCTUATION.getCode());
                break;
            case 4:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FIXED_VALUE.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 5:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode());
                break;
            case 6:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode());
                break;
            case 7:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.SEASON_RING_GROWTH.getCode());
                break;
            case 8:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.MONTH_RING_GROWTH.getCode());
                break;
            case 9:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.WEEK_RING_GROWTH.getCode());
                break;
            case 10:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.DAY_RING_GROWTH.getCode());
                break;
            case 11:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HOUR_RING_GROWTH.getCode());
                break;
            case 12:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.YEAR_ON_YEAR.getCode());
                break;
            default:
                addMultiSourceRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        if (CollectionUtils.isEmpty(alarmConfigRequests)) {
            LOGGER.warn("Alarm config is null. Create a new one.");
            alarmConfigRequests = new ArrayList<>();
        }
        LOGGER.info("The fix value is: {}", value);
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(double value) {
        addMultiSourceRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addMultiSourceRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AbstractCommonRequest returnRequest() {
        if (addMultiSourceRuleRequest.getAlarmVariable().size() >= TWO) {
            addMultiSourceRuleRequest.getAlarmVariable().remove(0);
        }
        return this.addMultiSourceRuleRequest;
    }

    public Template getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Override
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    @Override
    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    @Override
    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    @Override
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public AddRequestBuilder setAbnormalDb(String clusterAndDbName) throws UnExpectedRequestException {
        if (StringUtils.isEmpty(clusterAndDbName) || !clusterAndDbName.contains(COMMA)) {
            throw new UnExpectedRequestException("Abnormal cluster and database is illegal.");
        }
        String[] infos = clusterAndDbName.split(SpecCharEnum.PERIOD.getValue());
        addMultiSourceRuleRequest.setAbnormalCluster(infos[0]);
        addMultiSourceRuleRequest.setAbnormalDatabase(infos[1]);
        addMultiSourceRuleRequest.setAbnormalProxyUser(StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
        return this;
    }

    @Override
    public AddRequestBuilder disable() throws UnExpectedRequestException {
        addMultiSourceRuleRequest.setRuleEnable(false);
        return this;
    }

    @Override
    public AddRequestBuilder unionAll() throws UnExpectedRequestException {
        addMultiSourceRuleRequest.setUnionAll(true);
        return this;
    }

    @Override
    public AddRequestBuilder withGroup(String ruleGroupName) throws UnExpectedRequestException {
        RuleGroup ruleGroup = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
        if (ruleGroup != null) {
            addMultiSourceRuleRequest.setRuleGroupId(ruleGroup.getId());
        } else {
            RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
            addMultiSourceRuleRequest.setRuleGroupId(savedRuleGroup.getId());
        }
        return this;
    }

    @Override
    public AddRequestBuilder save() {
        return this;
    }

    @Override
    public AddRequestBuilder async() {
        return this;
    }

    @Override
    public AddRequestBuilder filter(String filter) {
        for (TemplateArgumentRequest templateArgumentRequest : addMultiSourceRuleRequest.getTemplateArgumentRequests()) {
            if (TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode().equals(templateArgumentRequest.getArgumentType())) {
                templateArgumentRequest.setArgumentValue(filter);
                break;
            }
        }
        return this;
    }

    @Override
    public AddRequestBuilder joinType(String joinType) {
        addMultiSourceRuleRequest.setContrastType(ContrastTypeEnum.getCode(joinType));
        for (TemplateArgumentRequest templateArgumentRequest : addMultiSourceRuleRequest.getTemplateArgumentRequests()) {
            if (TemplateInputTypeEnum.CONTRAST_TYPE.getCode().equals(templateArgumentRequest.getArgumentType())) {
                templateArgumentRequest.setArgumentValue(String.valueOf(ContrastTypeEnum.getCode(joinType)));
                break;
            }
        }
        return this;
    }

    @Override
    public AddRequestBuilder envMapping(String envName, String dbAndTableName, String dbAliasName) throws UnExpectedRequestException {
        return this;
    }

    public RuleMetricDao getRuleMetricDao() {
        return ruleMetricDao;
    }

    public void setRuleMetricDao(RuleMetricDao ruleMetricDao) {
        this.ruleMetricDao = ruleMetricDao;
    }

    public MetaDataClient getMetaDataClient() {
        return metaDataClient;
    }

    public void setMetaDataClient(MetaDataClient metaDataClient) {
        this.metaDataClient = metaDataClient;
    }

    public boolean getUploadRuleMetricValue() {
        return uploadRuleMetricValue;
    }

    public void setUploadRuleMetricValue(boolean uploadRuleMetricValue) {
        this.uploadRuleMetricValue = uploadRuleMetricValue;
    }

    public boolean getUploadAbnormalValue() {
        return uploadAbnormalValue;
    }

    public void setUploadAbnormalValue(boolean uploadAbnormalValue) {
        this.uploadAbnormalValue = uploadAbnormalValue;
    }

    public String getRuleMetricEnCode() {
        return ruleMetricEnCode;
    }

    public void setRuleMetricEnCode(String ruleMetricEnCode) {
        this.ruleMetricEnCode = ruleMetricEnCode;
    }
}
