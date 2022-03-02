package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private Project project;
    private String ruleName;
    private String userName;

    private RuleMetricDao ruleMetricDao;
    private MetaDataClient metaDataClient;

    private static Integer FOUR = 4;

    private static final Pattern DATA_SOURCE_ID = Pattern.compile("\\.\\(ID=[0-9]+\\)");
    private static final Pattern DATA_SOURCE_NAME = Pattern.compile("\\.\\(NAME=[\\u4E00-\\u9FA5A-Za-z0-9_]+\\)");

    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>(){{
        put("CRITICAL", 1);
        put("MAJOR", 2);
        put("MINOR", 3);
        put("WARNING", 4);
        put("INFO", 5);
    }};

    private static final Logger LOGGER = LoggerFactory.getLogger(AddCustomRuleRequestBuilder.class);

    public AddCustomRuleRequestBuilder() {
        addCustomRuleRequest = new AddCustomRuleRequest();
    }

    public AddCustomRuleRequestBuilder(RuleMetricDao ruleMetricDao, MetaDataClient metaDataClient) {
        addCustomRuleRequest = new AddCustomRuleRequest();
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean abortOnFailure, String alertInfo)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();
        // Alert info.
        alertSetting(alertInfo);
        addCustomRuleRequest.setSqlCheckArea(sql);
        addCustomRuleRequest.setSaveMidTable(false);
        String dataSourceId = "";
        Matcher matcherId = DATA_SOURCE_ID.matcher(cluster.toUpperCase());
        String dataSourceName = "";
        Matcher matcherName = DATA_SOURCE_NAME.matcher(cluster.toUpperCase());

        while (matcherId.find()) {
            String group = matcherId.group();
            dataSourceId = group.replace(".(", "").replace(")", "").split("=")[1];
            int startIndex = cluster.toUpperCase().indexOf(group);
            String replaceStr = cluster.substring(startIndex, startIndex + group.length());
            cluster = cluster.replace(replaceStr, "");
        }
        if (StringUtils.isBlank(dataSourceId)) {
            while (matcherName.find()) {
                String group = matcherName.group();
                int startIndex = cluster.toUpperCase().indexOf(group);
                String replaceStr = cluster.substring(startIndex, startIndex + group.length());
                dataSourceName = replaceStr.replace(".(", "").replace(")", "").split("=")[1];
                cluster = cluster.replace(replaceStr, "");
            }
        }

        addCustomRuleRequest.setClusterName(cluster);
        if (StringUtils.isNotBlank(dataSourceId)) {
            LOGGER.info("Find data source connect. Data source ID: " + dataSourceId);
            addCustomRuleRequest.setLinkisDataSourceId(Long.parseLong(dataSourceId));
            GeneralResponse<Map> response = metaDataClient.getDataSourceInfoDetail(cluster, userName, addCustomRuleRequest.getLinkisDataSourceId(), null);
            Map dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            addCustomRuleRequest.setLinkisDataSourceName(dataSourceInfoName);
            addCustomRuleRequest.setLinkisDataSourceType(dataSourceInfoType);
        } else if (StringUtils.isNotBlank(dataSourceName)) {
            LOGGER.info("Find data source connect. Data source name: " + dataSourceName);
            GeneralResponse<Map> response = metaDataClient.getDataSourceInfoDetailByName(cluster, userName, dataSourceName);
            Map dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            Integer currentDataSourceId = (Integer) dataSourceInfo.get("id");
            addCustomRuleRequest.setLinkisDataSourceId(currentDataSourceId.longValue());

            addCustomRuleRequest.setLinkisDataSourceName(dataSourceInfoName);
            addCustomRuleRequest.setLinkisDataSourceType(dataSourceInfoType);
        }
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
        addCustomRuleRequest.setProjectId(project.getId());
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
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String condition1, String condition2, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String param1, String param2, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder addRuleMetric(String ruleMetricName) throws UnExpectedRequestException {
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
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(String value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder mouthFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(String value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
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
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(double value) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        addCustomRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder mouthFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(double value) {
        addCustomRuleRequest.setAlarm(true);
        List<CustomAlarmConfigRequest> alarmConfigRequests = addCustomRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AbstractAddRequest returnRequest() {
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
