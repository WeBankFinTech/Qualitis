package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.UnionWayEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceEnvService;
import com.webank.wedatasphere.qualitis.rule.util.DatasourceEnvUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 14:14
 */
@Service
public class AddRuleRequestBuilder implements AddRequestBuilder {
    private AddRuleRequest addRuleRequest;
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

    private LinkisConfig linkisConfig;

    private static Integer TWO = 2;
    private static Integer THREE = 3;

    private static Integer FOUR = 4;
    private static final String COMMA = ".";

    private static final Pattern DATA_SOURCE_NAME = Pattern.compile("\\.\\(NAME=[\\u4E00-\\u9FA5A-Za-z0-9_]+\\{[\\u4E00-\\u9FA5A-Za-z0-9_,]+\\}\\)");


    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>();

    static {
        ALERT_LEVEL_CODE.put("CRITICAL", 1);
        ALERT_LEVEL_CODE.put("MAJOR", 2);
        ALERT_LEVEL_CODE.put("MINOR", 3);
        ALERT_LEVEL_CODE.put("WARNING", 4);
        ALERT_LEVEL_CODE.put("INFO", 5);
    }

    private RuleGroupDao ruleGroupDao;

    private RuleMetricDao ruleMetricDao;

    private LinkisDataSourceEnvService linkisDataSourceEnvService;

    private LinkisDataSourceDao linkisDataSourceDao;

    private MetaDataClient metaDataClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddRuleRequestBuilder.class);

    public AddRuleRequestBuilder() {
        addRuleRequest = new AddRuleRequest();
    }

    public AddRuleRequestBuilder(RuleMetricDao ruleMetricDao, MetaDataClient metaDataClient) {
        addRuleRequest = new AddRuleRequest();
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
    }

    public AddRuleRequestBuilder(RuleMetricDao ruleMetricDao, RuleGroupDao ruleGroupDao, LinkisDataSourceEnvService linkisDataSourceEnvService, MetaDataClient metaDataClient, LinkisConfig linkisConfig) {
        addRuleRequest = new AddRuleRequest();
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
        this.ruleGroupDao = ruleGroupDao;
        this.linkisConfig = linkisConfig;
        this.linkisDataSourceEnvService = linkisDataSourceEnvService;
    }

    public AddRuleRequestBuilder(RuleMetricDao ruleMetricDao, RuleGroupDao ruleGroupDao, LinkisDataSourceEnvService linkisDataSourceEnvService, LinkisDataSourceDao linkisDataSourceDao, MetaDataClient metaDataClient, LinkisConfig linkisConfig) {
        addRuleRequest = new AddRuleRequest();
        this.metaDataClient = metaDataClient;
        this.linkisDataSourceEnvService = linkisDataSourceEnvService;
        this.linkisDataSourceDao = linkisDataSourceDao;
        this.ruleMetricDao = ruleMetricDao;
        this.ruleGroupDao = ruleGroupDao;
        this.linkisConfig = linkisConfig;
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
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue
        , String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        // Datasource
        solveDatasource(datasource);

        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();
        addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
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
    public AddRequestBuilder updateDataSourceWithDcnNums(String dcnNums) throws Exception {
        // Get datasource env array
        Long linkisDataSourceId = this.addRuleRequest.getDatasource().stream().filter(dataSourceRequest -> dataSourceRequest.getLinkisDataSourceId() != null).map(dataSourceRequest -> dataSourceRequest.getLinkisDataSourceId()).iterator().next();
        List<LinkisDataSourceEnv> linkisDataSourceEnvs = linkisDataSourceEnvService.queryAllEnvs(linkisDataSourceId);
        // Filter with conditions
        linkisDataSourceEnvs = linkisDataSourceEnvs.stream().filter(linkisDataSourceEnv -> dcnNums.contains(linkisDataSourceEnv.getDcnNum())).collect(Collectors.toList());
        // Reset env request
        resetDatasourceEnv(linkisDataSourceEnvs, QualitisConstants.CMDB_KEY_DCN_NUM);
        return this;
    }

    private void resetDatasourceEnv(List<LinkisDataSourceEnv> linkisDataSourceEnvs, String rangeType) {
        List<DataSourceRequest> dataSourceRequests = this.addRuleRequest.getDatasource();
        List<DataSourceEnvRequest> dataSourceEnvRequests = new ArrayList<>(linkisDataSourceEnvs.size());

        for (LinkisDataSourceEnv linkisDataSourceEnv : linkisDataSourceEnvs) {
            DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest(linkisDataSourceEnv);
            dataSourceEnvRequests.add(dataSourceEnvRequest);
        }

        dataSourceRequests = dataSourceRequests.stream().map(dataSourceRequest -> {
            dataSourceRequest.setDataSourceEnvRequests(dataSourceEnvRequests);
            dataSourceRequest.setDcnRangeType(rangeType);
            return dataSourceRequest;
        }).collect(Collectors.toList());

        this.addRuleRequest.setDatasource(dataSourceRequests);
    }

    @Override
    public AddRequestBuilder updateDataSourceWithLogicAreas(String logicAreas) throws Exception {
        List<String> logicAreaList = Arrays.asList(StringUtils.split(logicAreas, SpecCharEnum.COMMA.getValue()));
        // Get datasource env array
        Long linkisDataSourceId = this.addRuleRequest.getDatasource().stream().filter(dataSourceRequest -> dataSourceRequest.getLinkisDataSourceId() != null).map(dataSourceRequest -> dataSourceRequest.getLinkisDataSourceId()).iterator().next();
        List<LinkisDataSourceEnv> linkisDataSourceEnvs = linkisDataSourceEnvService.queryAllEnvs(linkisDataSourceId);
        // Filter with conditions
        linkisDataSourceEnvs = linkisDataSourceEnvs.stream().filter(linkisDataSourceEnv -> logicAreaList.contains(linkisDataSourceEnv.getLogicArea())).collect(Collectors.toList());
        // Reset env request
        resetDatasourceEnv(linkisDataSourceEnvs, QualitisConstants.CMDB_KEY_LOGIC_AREA);
        return this;
    }

    private void initAlarm(List<AlarmConfigRequest> alarmVariable, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) {
        addRuleRequest.setAlarm(true);
        AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
        alarmConfigRequest.setUploadRuleMetricValue(uploadRuleMetricValue);
        alarmConfigRequest.setUploadAbnormalValue(uploadAbnormalValue);
        alarmConfigRequest.setOutputMetaId(template.getTemplateOutputMetas().iterator().next().getId());
        alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.FIXED_VALUE.getCode());
        alarmConfigRequest.setCompareType(CompareTypeEnum.EQUAL.getCode());
        alarmConfigRequest.setThreshold(0.0);
        alarmVariable.add(alarmConfigRequest);

        setUploadRuleMetricValue(uploadRuleMetricValue);
        setUploadAbnormalValue(uploadAbnormalValue);
        addRuleRequest.setAlarmVariable(alarmVariable);
        addRuleRequest.setUploadAbnormalValue(uploadAbnormalValue);
        addRuleRequest.setUploadRuleMetricValue(uploadRuleMetricValue);
    }

    private void taskSetting(boolean deleteFailCheckResult, boolean abortOnFailure, String execParams) {
        addRuleRequest.setDeleteFailCheckResult(deleteFailCheckResult);
        addRuleRequest.setAbortOnFailure(abortOnFailure);
        if (StringUtils.isNotBlank(execParams)) {
            addRuleRequest.setSpecifyStaticStartupParam(true);
            addRuleRequest.setStaticStartupParam(execParams);
        } else {
            addRuleRequest.setSpecifyStaticStartupParam(false);
        }
    }

    private void alertSetting(String alertInfo) throws UnExpectedRequestException {
        if (StringUtils.isNotBlank(alertInfo)) {
            String[] alertStrs = alertInfo.split(SpecCharEnum.COLON.getValue());
            String alertLevel = alertStrs[0].toUpperCase();
            if (ALERT_LEVEL_CODE.keySet().contains(alertLevel)) {
                addRuleRequest.setAlert(true);
                addRuleRequest.setAlertLevel(ALERT_LEVEL_CODE.get(alertLevel));
                addRuleRequest.setAlertReceiver(alertStrs[1].trim());
            } else {
                throw new UnExpectedRequestException("Please check alert level name. Alert name: " + alertLevel);
            }

        } else {
            addRuleRequest.setAlert(false);
        }
    }

    private void automaticProjectRuleSetting() {
        addRuleRequest.setRuleName(ruleName);
        addRuleRequest.setRuleDetail(ruleDetail);
        addRuleRequest.setRuleCnName(ruleCnName);
        addRuleRequest.setProjectId(project.getId());
        addRuleRequest.setRuleTemplateId(template.getId());
    }

    private void solveDatasource(String datasource) throws Exception {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>(1);
        List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>(1);
        DataSourceRequest dataSourceRequest = new DataSourceRequest();
        String clusterName, database, table, col, filter;

        String[] datasourceStrs = datasource.split(SpecCharEnum.COLON.getValue());
        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        StringBuilder dbAndTable = new StringBuilder(datasourceStrs[0]);

        StringBuilder dataSourceId = new StringBuilder();
        StringBuilder dataSourceName = new StringBuilder();
        StringBuilder envsStringBuffer = new StringBuilder();
        DatasourceEnvUtil.getDatasourceIdOrName(dataSourceId, dataSourceName, envsStringBuffer, dbAndTable);
        String[] datasources = dbAndTable.toString().split(SpecCharEnum.PERIOD.getValue());
        filter = datasourceStrs[1];

        if (datasources.length != FOUR && datasources.length != THREE) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        clusterName = datasources[0];
        database = datasources[1];
        table = datasources[2];
        col = datasources.length >= FOUR ? datasources[3] : "";

        List<ColumnInfoDetail> cols = new ArrayList<>();
        try {
            if (StringUtils.isEmpty(envsStringBuffer.toString()) && StringUtils.isEmpty(dataSourceName.toString()) && StringUtils.isEmpty(dataSourceId.toString())) {
                cols = metaDataClient.getColumnInfo(clusterName, database, table, StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
            }
        } catch (Exception e) {
            throw new UnExpectedRequestException(e.getMessage(), 500);
        }

        getDataSourceRequest(dataSourceRequests, dataSourceColumnRequests, dataSourceRequest, clusterName, database, table, col, filter, dataSourceId, dataSourceName, cols, envsStringBuffer);

        this.addRuleRequest.setDatasource(dataSourceRequests);
    }

    private void getDataSourceRequest(List<DataSourceRequest> dataSourceRequests, List<DataSourceColumnRequest> dataSourceColumnRequests, DataSourceRequest dataSourceRequest
            , String clusterName, String database, String table, String col, String filter, StringBuilder dataSourceId, StringBuilder dataSourceName, List<ColumnInfoDetail> cols, StringBuilder envsStringBuffer) throws Exception {

        String realUserName = StringUtils.isNotBlank(proxyUser) ? proxyUser : userName;
        String realClusterName = this.linkisConfig.getDatasourceCluster();

        StringBuilder dataSourceType = new StringBuilder();
        List<DataSourceEnvRequest> dataSourceEnvRequests = new ArrayList<>();
        DatasourceEnvUtil.constructDatasourceAndEnv(dataSourceId, dataSourceName, dataSourceType, dataSourceEnvRequests, envsStringBuffer, linkisConfig.getDatasourceAdmin(), realClusterName, metaDataClient, database, table, cols);

        if (StringUtils.isNotEmpty(dataSourceId.toString())) {
            dataSourceRequest.setLinkisDataSourceId(Long.parseLong(dataSourceId.toString()));
            if (StringUtils.isEmpty(envsStringBuffer.toString())) {
                LOGGER.info("Start to get all envs.");
                List<LinkisDataSourceEnv> linkisDataSourceEnvs = linkisDataSourceEnvService.queryAllEnvs(Long.parseLong(dataSourceId.toString()));
                for (LinkisDataSourceEnv linkisDataSourceEnv : linkisDataSourceEnvs) {
                    DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest(linkisDataSourceEnv);
                    dataSourceEnvRequests.add(dataSourceEnvRequest);
                }
            }
        }
        dataSourceRequest.setDataSourceEnvRequests(dataSourceEnvRequests);
        dataSourceRequest.setLinkisDataSourceType(dataSourceType.toString());
        dataSourceRequest.setLinkisDataSourceName(dataSourceName.toString());
        // For one or more fields
        if (StringUtils.isBlank(col)) {
            LOGGER.info("Table count check.");
        } else {
            boolean blackList = false;
            List<String> colsInDatasource = new ArrayList<>();
            if (col.startsWith(SpecCharEnum.MINUS.getValue())) {
                blackList = true;
                col = col.replace(SpecCharEnum.MINUS.getValue(), "");
            }
            if (col.contains(SpecCharEnum.LEFT_BRACKET.getValue()) && col.contains(SpecCharEnum.RIGHT_BRACKET.getValue())) {
                col = col.replace(SpecCharEnum.LEFT_BRACKET.getValue(), "").replace(SpecCharEnum.RIGHT_BRACKET.getValue(), "");
                for (String currentCol : col.split(SpecCharEnum.COMMA.getValue())) {
                    colsInDatasource.add(currentCol);
                }
            } else {
                colsInDatasource.add(col);
            }
            if (blackList) {
                for (ColumnInfoDetail columnInfoDetail : cols) {
                    if (colsInDatasource.contains(columnInfoDetail.getFieldName())) {
                        continue;
                    } else {
                        DataSourceColumnRequest dataSourceColumnRequest = new DataSourceColumnRequest(columnInfoDetail.getFieldName(), columnInfoDetail.getDataType());
                        dataSourceColumnRequests.add(dataSourceColumnRequest);
                    }
                }
            } else {
                for (String colName : colsInDatasource) {
                    String type = cols.stream().filter(field -> field.getFieldName().equals(colName)).map(ColumnInfoDetail::getDataType).collect(Collectors.joining());
                    DataSourceColumnRequest dataSourceColumnRequest = new DataSourceColumnRequest(colName, type);
                    dataSourceColumnRequests.add(dataSourceColumnRequest);
                }
            }

            dataSourceRequest.setBlackList(blackList);
        }

        dataSourceRequest.setDbName(database);
        dataSourceRequest.setTableName(table);
        dataSourceRequest.setClusterName(clusterName);
        dataSourceRequest.setColNames(dataSourceColumnRequests);
        dataSourceRequest.setDcnRangeType(QualitisConstants.DCN_RANGE_TYPE_ALL);
        dataSourceRequest.setProxyUser(realUserName);
        dataSourceRequest.setFilter(filter);
        dataSourceRequests.add(dataSourceRequest);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String regxOrRangeOrEnum, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        // Datasource
        solveDatasource(datasource);

        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();

        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();
        templateArgumentSetting(templateArgumentRequests, regxOrRangeOrEnum);
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
    public AddRequestBuilder basicInfoWithDataSource(String datasource, Long standardValueVersionId, List<TemplateArgumentRequest> templateArgumentRequests
            , boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        // Datasource
        solveDatasource(datasource);

        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        if (standardValueVersionId != null) {
            addRuleRequest.setStandardValueVersionId(standardValueVersionId);
        }
        addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
        // Alert info.
        alertSetting(alertInfo);

        // Task running info.
        taskSetting(deleteFailCheckResult, abortOnFailure, execParams);
        // Init alarm properties.
        List<AlarmConfigRequest> alarmVariable = new ArrayList<>(1);

        initAlarm(alarmVariable, uploadRuleMetricValue, uploadAbnormalValue);
        return this;
    }

    private void templateArgumentSetting(List<TemplateArgumentRequest> templateArgumentRequests, String regxOrRangeOrEnum) {
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), regxOrRangeOrEnum));
            }
        }
        addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String condition1, String condition2, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        // Datasource
        solveDatasource(datasource);

        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();
        templateArgumentSetting(templateArgumentRequests, condition1, condition2);
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
    public AddRequestBuilder basicInfoWithDataSourceAndCluster(String cluster, String datasource, String dbAndTable, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String param1, String param2, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return null;
    }

    @Override
    public AddRequestBuilder basicInfoWithoutDataSource(String cluster, String datasource, String param1, String param2, String param3, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        return this;
    }

    private void templateArgumentSetting(List<TemplateArgumentRequest> templateArgumentRequests, String condition1, String condition2) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetaList = new ArrayList<>(2);
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                templateMidTableInputMetaList.add(templateMidTableInputMeta);
            }
        }
        Collections.sort(templateMidTableInputMetaList, new Comparator<TemplateMidTableInputMeta>() {
            @Override
            public int compare(TemplateMidTableInputMeta templateMidTableInputMetaFront, TemplateMidTableInputMeta templateMidTableInputMetaBack) {
                return Long.compare(templateMidTableInputMetaFront.getId(), templateMidTableInputMetaBack.getId());
            }
        });
        templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMetaList.get(0).getId(), templateMidTableInputMetaList.get(0).getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), condition1));
        templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMetaList.get(1).getId(), templateMidTableInputMetaList.get(1).getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), condition2));

        addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
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
        addRuleRequest.setRuleMetricNamesForBdpClient(ruleMetricNames);
        setRuleMetricEnCode(en);
        return this;
    }

    @Override
    public AddRequestBuilder addExecutionParameter(String executionParameterName) throws UnExpectedRequestException {
        addRuleRequest.setExecutionParametersName(executionParameterName);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCompleteEvent(String alarmEvents) {
        addRuleRequest.setExecutionCompleted(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCheckSuccessEvent(String alarmEvents) {
        addRuleRequest.setVerificationSuccessful(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCheckFailedEvent(String alarmEvents) {
        addRuleRequest.setVerificationFailed(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetricWithCheck(String ruleMetricName, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) throws UnExpectedRequestException {
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
                addRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(String value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
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
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 6:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 7:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.SEASON_RING_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 8:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.MONTH_RING_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 9:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.WEEK_RING_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 10:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.DAY_RING_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 11:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.HOUR_RING_GROWTH.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            case 12:
                newAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.YEAR_ON_YEAR.getCode());
                newAlarmConfigRequest.setCompareType(compareType);
                break;
            default:
                addRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(double value) {
        addRuleRequest.setAlarm(true);
        List<AlarmConfigRequest> alarmConfigRequests = addRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AbstractCommonRequest returnRequest() {
        if (addRuleRequest.getAlarmVariable().size() >= TWO) {
            addRuleRequest.getAlarmVariable().remove(0);
        }
        return this.addRuleRequest;
    }

    public Template getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(Template template) {
        this.template = template;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
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
        addRuleRequest.setAbnormalCluster(infos[0]);
        addRuleRequest.setAbnormalDatabase(infos[1]);
        addRuleRequest.setAbnormalProxyUser(StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
        return this;
    }

    @Override
    public AddRequestBuilder disable() throws UnExpectedRequestException {
        addRuleRequest.setRuleEnable(false);
        return this;
    }

    @Override
    public AddRequestBuilder unionAll() throws UnExpectedRequestException {
        addRuleRequest.setUnionWay(UnionWayEnum.COLLECT_AFTER_CALCULATE.getCode());
        return this;
    }

    @Override
    public AddRequestBuilder unionWay(int unionWay) throws UnExpectedRequestException {
        addRuleRequest.setUnionWay(unionWay);
        return this;
    }

    @Override
    public AddRequestBuilder withGroup(String ruleGroupName) throws UnExpectedRequestException {
        RuleGroup ruleGroup = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
        if (ruleGroup != null) {
            addRuleRequest.setRuleGroupId(ruleGroup.getId());
        } else {
            RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
            addRuleRequest.setRuleGroupId(savedRuleGroup.getId());
        }
        addRuleRequest.setRuleGroupName(ruleGroupName);
        return this;
    }


    @Override
    public AddRequestBuilder moveToGroup(String ruleGroupName) throws UnExpectedRequestException {
        RuleGroup ruleGroup = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
        if (ruleGroup != null) {
            addRuleRequest.setNewRuleGroupId(ruleGroup.getId());
        } else {
            RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
            addRuleRequest.setNewRuleGroupId(savedRuleGroup.getId());
        }
        addRuleRequest.setRuleGroupName(ruleGroupName);
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
        return this;
    }

    @Override
    public AddRequestBuilder joinType(String joinType) {
        return this;
    }

    @Override
    public AddRequestBuilder addUdfs(String udfNames) throws UnExpectedRequestException {
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
