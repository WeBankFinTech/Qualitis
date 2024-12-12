package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.UnionWayEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.function.dao.LinkisUdfDao;
//import com.webank.wedatasphere.qualitis.function.entity.LinkisUdf;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceEnvService;
import com.webank.wedatasphere.qualitis.rule.util.DatasourceEnvUtil;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import org.apache.commons.collections.CollectionUtils;
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
public class AddCustomRuleRequestBuilder implements AddRequestBuilder {
    private AddCustomRuleRequest addCustomRuleRequest;
    private boolean deleteFailCheckResult;
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

    private RuleGroupDao ruleGroupDao;
//    private LinkisUdfDao linkisUdfDao;
    private RuleMetricDao ruleMetricDao;
    private MetaDataClient metaDataClient;
    private SubDepartmentPermissionService subDepartmentPermissionService;
    private LinkisConfig linkisConfig;
    private LinkisDataSourceEnvService linkisDataSourceEnvService;

    private static Integer FOUR = 4;
    private static final String COMMA = ".";

    private static final Pattern DATA_SOURCE_ID = Pattern.compile("\\.\\(ID=[0-9]+\\)");
    private static final Pattern DATA_SOURCE_NAME = Pattern.compile("\\.\\(NAME=[\\u4E00-\\u9FA5A-Za-z0-9_]+\\)");

    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>();

    static {
        ALERT_LEVEL_CODE.put("CRITICAL", 1);
        ALERT_LEVEL_CODE.put("MAJOR", 2);
        ALERT_LEVEL_CODE.put("MINOR", 3);
        ALERT_LEVEL_CODE.put("WARNING", 4);
        ALERT_LEVEL_CODE.put("INFO", 5);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AddCustomRuleRequestBuilder.class);

    public AddCustomRuleRequestBuilder() {
        addCustomRuleRequest = new AddCustomRuleRequest();
    }

    public AddCustomRuleRequestBuilder(RuleMetricDao ruleMetricDao, MetaDataClient metaDataClient) {
        addCustomRuleRequest = new AddCustomRuleRequest();
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
    }

    public AddCustomRuleRequestBuilder(RuleMetricDao ruleMetricDao, RuleGroupDao ruleGroupDao, MetaDataClient metaDataClient,
                                       SubDepartmentPermissionService subDepartmentPermissionService, LinkisConfig linkisConfig, LinkisDataSourceEnvService linkisDataSourceEnvService) {
        this.subDepartmentPermissionService = subDepartmentPermissionService;
        addCustomRuleRequest = new AddCustomRuleRequest();
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
        this.ruleGroupDao = ruleGroupDao;
//        this.linkisUdfDao = linkisUdfDao;
        this.linkisConfig = linkisConfig;
        this.linkisDataSourceEnvService = linkisDataSourceEnvService;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean abortOnFailure, String alertInfo)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        String realClusterName = this.linkisConfig.getDatasourceCluster();
        // Automatic generate rule name and project.
        automaticProjectRuleSetting();
        // Alert info.
        alertSetting(alertInfo);
        addCustomRuleRequest.setSqlCheckArea(sql);
        addCustomRuleRequest.setSaveMidTable(false);
        StringBuilder dataSourceId = new StringBuilder();
        StringBuilder dataSourceName = new StringBuilder();
        StringBuilder dataSourceType = new StringBuilder();
        StringBuilder envsStringBuffer = new StringBuilder();
        StringBuilder clusterBuffer = new StringBuilder(cluster);
        List<DataSourceEnvRequest> dataSourceEnvRequests = new ArrayList<>();
        DatasourceEnvUtil.getDatasourceIdOrName(dataSourceId, dataSourceName, envsStringBuffer, clusterBuffer);

        addCustomRuleRequest.setClusterName(clusterBuffer.toString());

        DatasourceEnvUtil.constructDatasourceAndEnv(dataSourceId, dataSourceName, dataSourceType, dataSourceEnvRequests, envsStringBuffer, linkisConfig.getDatasourceAdmin(), realClusterName, metaDataClient, "", "", new ArrayList<ColumnInfoDetail>());
        if (StringUtils.isNotEmpty(dataSourceId.toString())) {
            addCustomRuleRequest.setLinkisDataSourceId(Long.parseLong(dataSourceId.toString()));

            if (StringUtils.isEmpty(envsStringBuffer.toString())) {
                LOGGER.info("Start to get all envs.");
                List<LinkisDataSourceEnv> linkisDataSourceEnvs = linkisDataSourceEnvService.queryAllEnvs(Long.parseLong(dataSourceId.toString()));
                for (LinkisDataSourceEnv linkisDataSourceEnv : linkisDataSourceEnvs) {
                    DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest(linkisDataSourceEnv);
                    dataSourceEnvRequests.add(dataSourceEnvRequest);
                }
            }
        }

        List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests = new ArrayList<>();
        addCustomRuleRequest.setDataSourceEnvMappingRequests(dataSourceEnvMappingRequests);
        addCustomRuleRequest.setLinkisDataSourceType(dataSourceType.toString());
        addCustomRuleRequest.setLinkisDataSourceName(dataSourceName.toString());
        addCustomRuleRequest.setDataSourceEnvRequests(dataSourceEnvRequests);
        taskSetting(abortOnFailure, execParams);
        // Init alarm properties.
        List<CustomAlarmConfigRequest> alarmVariable = new ArrayList<>();
        addCustomRuleRequest.setAlarm(true);
        addCustomRuleRequest.setAlarmVariable(alarmVariable);
        return this;
    }

    private void alertSetting(String alertInfo) throws UnExpectedRequestException {
        if (StringUtils.isNotBlank(alertInfo)) {
            String[] alertStrs = alertInfo.split(SpecCharEnum.COLON.getValue());
            String alertLevel = alertStrs[0].toUpperCase();
            if (ALERT_LEVEL_CODE.keySet().contains(alertLevel)) {
                addCustomRuleRequest.setAlert(true);
                addCustomRuleRequest.setAlertLevel(ALERT_LEVEL_CODE.get(alertLevel));
                addCustomRuleRequest.setAlertReceiver(alertStrs[1].trim());
            } else {
                throw new UnExpectedRequestException("Please check alert level name. Alert name: " + alertLevel);
            }

        } else {
            addCustomRuleRequest.setAlert(false);
        }
    }

    private void automaticProjectRuleSetting() {
        addCustomRuleRequest.setRuleName(ruleName);
        addCustomRuleRequest.setRuleDetail(ruleDetail);
        addCustomRuleRequest.setRuleCnName(ruleCnName);
        addCustomRuleRequest.setProjectId(project.getId());
        addCustomRuleRequest.setProxyUser(StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
    }

    private void taskSetting(boolean abortOnFailure, String execParams) {
        addCustomRuleRequest.setAbortOnFailure(abortOnFailure);
        if (StringUtils.isNotBlank(execParams)) {
            addCustomRuleRequest.setSpecifyStaticStartupParam(true);
            addCustomRuleRequest.setStaticStartupParam(execParams);
        } else {
            addCustomRuleRequest.setSpecifyStaticStartupParam(false);
        }
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
                                                     boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder updateDataSourceWithDcnNums(String dcnNums) throws Exception {
        Long sourceDataSourceId = this.addCustomRuleRequest.getLinkisDataSourceId();
        GetLinkisDataSourceEnvRequest request = new GetLinkisDataSourceEnvRequest();
        request.setLinkisDataSourceId(sourceDataSourceId);
        request.setDcnNums(Arrays.asList(StringUtils.split(dcnNums, SpecCharEnum.COMMA.getValue())));
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvService.queryEnvsInAdvance(request);
        if (CollectionUtils.isEmpty(linkisDataSourceEnvList)) {
            throw new UnExpectedRequestException("Cannot to get Environments by: " + dcnNums);
        }

        List<DataSourceEnvRequest> targetDataSourceEnvRequest = linkisDataSourceEnvList.stream().map(DataSourceEnvRequest::new).collect(Collectors.toList());
        this.addCustomRuleRequest.setDataSourceEnvRequests(targetDataSourceEnvRequest);
        addCustomRuleRequest.setDcnRangeType(QualitisConstants.CMDB_KEY_DCN_NUM);
        return this;
    }

    @Override
    public AddRequestBuilder updateDataSourceWithLogicAreas(String logicAreas) throws Exception {
        Long sourceDataSourceId = this.addCustomRuleRequest.getLinkisDataSourceId();
        GetLinkisDataSourceEnvRequest request = new GetLinkisDataSourceEnvRequest();
        request.setLinkisDataSourceId(sourceDataSourceId);
        request.setLogicAreas(Arrays.asList(StringUtils.split(logicAreas, SpecCharEnum.COMMA.getValue())));
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvService.queryEnvsInAdvance(request);
        if (CollectionUtils.isEmpty(linkisDataSourceEnvList)) {
            throw new UnExpectedRequestException("Cannot to get Environments by: " + logicAreas);
        }

        List<DataSourceEnvRequest> targetDataSourceEnvRequest = linkisDataSourceEnvList.stream().map(DataSourceEnvRequest::new).collect(Collectors.toList());
        this.addCustomRuleRequest.setDataSourceEnvRequests(targetDataSourceEnvRequest);
        addCustomRuleRequest.setDcnRangeType(QualitisConstants.CMDB_KEY_LOGIC_AREA);
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, Long standardValueVersionId,
                                                     List<TemplateArgumentRequest> templateArgumentRequests,
                                                     boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure,
                                                     String execParams) throws Exception {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String condition1, String condition2, boolean deleteFailCheckResult,
                                                     boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
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
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithoutDataSource(String cluster, String datasource, String param1, String param2, String param3, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetric(String ruleMetricName) throws UnExpectedRequestException {
        return this;
    }

    @Override
    public AddRequestBuilder addExecutionParameter(String executionParameterName) throws UnExpectedRequestException {
        addCustomRuleRequest.setExecutionParametersName(executionParameterName);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCompleteEvent(String alarmEvents){
        addCustomRuleRequest.setExecutionCompleted(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCheckSuccessEvent(String alarmEvents){
        addCustomRuleRequest.setVerificationSuccessful(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder alarmWithCheckFailedEvent(String alarmEvents){
        addCustomRuleRequest.setVerificationFailed(alarmEvents);
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetricWithCheck(String ruleMetricName, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) throws UnExpectedRequestException {
        RuleMetric ruleMetricInDb = ruleMetricDao.findByName(ruleMetricName);

        if (ruleMetricInDb != null) {
            setRuleMetricEnCode(ruleMetricInDb.getEnCode());

            setUploadAbnormalValue(uploadAbnormalValue);
            setUploadRuleMetricValue(uploadRuleMetricValue);
            setDeleteFailCheckResult(deleteFailCheckResult);
            return this;
        }
        String[] infos = ruleMetricName.split(SpecCharEnum.BOTTOM_BAR.getValue());
        if (infos.length != FOUR) {
            throw new UnExpectedRequestException("The metric name does not meet specifications");
        }
        String en = infos[2];

        List<String> existRuleMetricNames = addCustomRuleRequest.getRuleMetricNamesForBdpClient();
        if (CollectionUtils.isNotEmpty(existRuleMetricNames)) {
            existRuleMetricNames.add(ruleMetricName);
        } else {
            List<String> ruleMetricNames = new ArrayList<>(1);
            ruleMetricNames.add(ruleMetricName);
            addCustomRuleRequest.setRuleMetricNamesForBdpClient(ruleMetricNames);
        }

        setRuleMetricEnCode(en);

        setUploadAbnormalValue(uploadAbnormalValue);
        setUploadRuleMetricValue(uploadRuleMetricValue);
        setDeleteFailCheckResult(deleteFailCheckResult);

        addCustomRuleRequest.setUploadAbnormalValue(uploadAbnormalValue);
        addCustomRuleRequest.setUploadRuleMetricValue(uploadRuleMetricValue);
        addCustomRuleRequest.setDeleteFailCheckResult(deleteFailCheckResult);
        return this;
    }

    public CustomAlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, String value) {
        CustomAlarmConfigRequest newAlarmConfigRequest = new CustomAlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setDeleteFailCheckResult(getDeleteFailCheckResult());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
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
                addCustomRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(String value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(String value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    public CustomAlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, double value) {
        CustomAlarmConfigRequest newAlarmConfigRequest = new CustomAlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setDeleteFailCheckResult(getDeleteFailCheckResult());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
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
                addCustomRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(double value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(double value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(addCustomRuleRequest.getAlarmVariable());
        return this;
    }

    @Override
    public AbstractCommonRequest returnRequest() {
        return addCustomRuleRequest;
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
        addCustomRuleRequest.setAbnormalCluster(infos[0]);
        addCustomRuleRequest.setAbnormalDatabase(infos[1]);
        addCustomRuleRequest.setAbnormalProxyUser(StringUtils.isNotBlank(proxyUser) ? proxyUser : userName);
        return this;
    }

    @Override
    public AddRequestBuilder disable() throws UnExpectedRequestException {
        addCustomRuleRequest.setRuleEnable(false);
        return this;
    }

    @Override
    public AddRequestBuilder unionAll() throws UnExpectedRequestException {
        addCustomRuleRequest.setUnionWay(UnionWayEnum.COLLECT_AFTER_CALCULATE.getCode());
        return this;
    }

    @Override
    public AddRequestBuilder unionWay(int unionWay) throws UnExpectedRequestException {
        addCustomRuleRequest.setUnionWay(unionWay);
        return this;
    }

    @Override
    public AddRequestBuilder withGroup(String ruleGroupName) throws UnExpectedRequestException {
        RuleGroup ruleGroup = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
        if (ruleGroup != null) {
            addCustomRuleRequest.setRuleGroupId(ruleGroup.getId());
        } else {
            RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
            addCustomRuleRequest.setRuleGroupId(savedRuleGroup.getId());
        }
        addCustomRuleRequest.setRuleGroupName(ruleGroupName);
        return this;
    }

    @Override
    public AddRequestBuilder moveToGroup(String ruleGroupName) throws UnExpectedRequestException {
        RuleGroup ruleGroup = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
        if (ruleGroup != null) {
            addCustomRuleRequest.setNewRuleGroupId(ruleGroup.getId());
        } else {
            RuleGroup savedRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
            addCustomRuleRequest.setNewRuleGroupId(savedRuleGroup.getId());
        }
        addCustomRuleRequest.setRuleGroupName(ruleGroupName);
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
        if (StringUtils.isEmpty(udfNames)) {
            return this;
        }

//        String[] udfNameStrs = udfNames.split(SpecCharEnum.COMMA.getValue());
//        for (String udfName : udfNameStrs) {
//            LinkisUdf linkisUdf =  linkisUdfDao.findByName(udfName);
//            if (linkisUdf == null) {
//                throw new UnExpectedRequestException("Udf {&DOES_NOT_EXIST}");
//            }
//            DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
//                .createUser(linkisUdf.getCreateUser())
//                .devDepartmentId(linkisUdf.getDevDepartmentId())
//                .opsDepartmentId(linkisUdf.getOpsDepartmentId())
//                .build();
//            subDepartmentPermissionService.checkAccessiblePermission(linkisUdf.getId(), TableDataTypeEnum.LINKIS_UDF, dataVisibilityPermissionDto);
//        }
//        addCustomRuleRequest.setLinkisUdfNames(Arrays.asList(udfNameStrs));
        return this;
    }

    @Override
    public AddRequestBuilder envMapping(String envNames, String dbName, String dbAliasName) throws UnExpectedRequestException {
        List<DataSourceEnvRequest> dataSourceEnvRequests = addCustomRuleRequest.getDataSourceEnvRequests();
        if (CollectionUtils.isNotEmpty(dataSourceEnvRequests)) {
            if (StringUtils.isEmpty(envNames) || StringUtils.isEmpty(dbName)) {
                throw new UnExpectedRequestException("Env mapping parameters {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }

            DataSourceEnvMappingRequest dataSourceEnvMappingRequest = new DataSourceEnvMappingRequest();

            dataSourceEnvMappingRequest.setDbName(dbName);
            dataSourceEnvMappingRequest.setDbAliasName(dbAliasName);
            String[] envNameStrs = envNames.split(SpecCharEnum.COMMA.getValue());

            for (String envName : envNameStrs) {
                for (DataSourceEnvRequest dataSourceEnvRequest : dataSourceEnvRequests) {
                    if (dataSourceEnvRequest.getEnvName().equals(envName)) {
                        dataSourceEnvMappingRequest.getDataSourceEnvRequests().add(dataSourceEnvRequest);
                    }
                }
            }
            addCustomRuleRequest.getDataSourceEnvMappingRequests().add(dataSourceEnvMappingRequest);
        }
        return this;
    }

    public boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
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
