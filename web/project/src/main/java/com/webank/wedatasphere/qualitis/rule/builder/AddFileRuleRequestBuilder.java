package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class AddFileRuleRequestBuilder implements AddRequestBuilder {
    private AddFileRuleRequest addFileRuleRequest;
    private Template template;
    private String ruleDetail;
    private Project project;
    private String ruleName;
    private String userName;

    private boolean deleteFailCheckResult;
    private boolean uploadRuleMetricValue;
    private boolean uploadAbnormalValue;
    private String ruleMetricEnCode;

    private Integer fileOutPut;

    private static Integer TWO = 2;
    private static Integer THREE = 3;

    private static Integer FOUR = 4;
    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>(){{
        put("CRITICAL", 1);
        put("MAJOR", 2);
        put("MINOR", 3);
        put("WARNING", 4);
        put("INFO", 5);
    }};

    private RuleMetricDao ruleMetricDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddFileRuleRequestBuilder.class);

    public AddFileRuleRequestBuilder() {
        addFileRuleRequest = new AddFileRuleRequest();
    }

    public AddFileRuleRequestBuilder(RuleMetricDao ruleMetricDao) {
        this.ruleMetricDao = ruleMetricDao;
        addFileRuleRequest = new AddFileRuleRequest();
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String regxOrRangeOrEnum, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean abortOnFailure, String alertInfo)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Datasource
        solveDatasource(datasource);

        // Use automatic generate rule name and project.
        automaticProjectRuleSetting();

        // Task running info.
        taskSetting(abortOnFailure);
        // Init alarm properties.
        List<FileAlarmConfigRequest> alarmVariable = new ArrayList<>(1);

        initAlarm(alarmVariable);
        return this;
    }

    private void initAlarm(List<FileAlarmConfigRequest> alarmVariable) {
        addFileRuleRequest.setAlarm(true);
        addFileRuleRequest.setAlarmVariable(alarmVariable);

    }

    private void taskSetting(boolean abortOnFailure) {
        addFileRuleRequest.setAbortOnFailure(abortOnFailure);
    }

    private void automaticProjectRuleSetting() {
        addFileRuleRequest.setRuleName(ruleName);
        addFileRuleRequest.setRuleDetail(ruleDetail);
        addFileRuleRequest.setProjectId(project.getId());
    }

    private void solveDatasource(String datasource) throws UnExpectedRequestException {
        List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>();
        DataSourceRequest dataSourceRequest = new DataSourceRequest();
        String clusterName;
        String database;
        String table;
        String filter;

        String[] datasourceStrs = datasource.split(SpecCharEnum.COLON.getValue());
        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String[] datasources = datasourceStrs[0].split(SpecCharEnum.PERIOD.getValue());
        filter = datasourceStrs.length >= TWO ? datasourceStrs[1] : "";
        if (datasources.length != FOUR && datasources.length != THREE) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        clusterName = datasources[0];
        database = datasources[1];
        table = datasources[2];

        dataSourceRequest.setClusterName(clusterName);
        dataSourceRequest.setDbName(database);
        dataSourceRequest.setTableName(table);
        dataSourceRequest.setColNames(dataSourceColumnRequests);

        dataSourceRequest.setFilter(filter);
        dataSourceRequest.setProxyUser(userName);

        this.addFileRuleRequest.setDatasource(dataSourceRequest);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return this;
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String regxOrRangeOrEnum, boolean deleteFailCheckResult,
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

        List<String> existRuleMetricNames = addFileRuleRequest.getRuleMetricNamesForBdpClient();
        if (CollectionUtils.isNotEmpty(existRuleMetricNames)) {
            existRuleMetricNames.add(ruleMetricName);
        } else {
            List<String> ruleMetricNames = new ArrayList<>(1);
            ruleMetricNames.add(ruleMetricName);
            addFileRuleRequest.setRuleMetricNamesForBdpClient(ruleMetricNames);
        }

        setRuleMetricEnCode(en);
        setUploadAbnormalValue(uploadAbnormalValue);
        setUploadRuleMetricValue(uploadRuleMetricValue);
        setDeleteFailCheckResult(deleteFailCheckResult);
        return this;
    }

    public FileAlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, String value) {
        FileAlarmConfigRequest newAlarmConfigRequest = new FileAlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setDeleteFailCheckResult(getDeleteFailCheckResult());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
        newAlarmConfigRequest.setRuleMetricEnCode(getRuleMetricEnCode());
        newAlarmConfigRequest.setFileOutputName(fileOutPut);

        if (value.contains(SpecCharEnum.EMPTY.getValue())) {
            String[] fullSize = value.split(SpecCharEnum.EMPTY.getValue());

            newAlarmConfigRequest.setThreshold(Double.parseDouble(fullSize[0]));
            newAlarmConfigRequest.setFileOutputUnit(FileOutputUnitEnum.fileOutputUnitCode(fullSize[1].toUpperCase()));
        } else {
            newAlarmConfigRequest.setThreshold(Double.parseDouble(value));
        }

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
                addFileRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(String value) {
        List<FileAlarmConfigRequest> alarmVariable = addFileRuleRequest.getAlarmVariable();
        alarmVariable.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmVariable);
        addFileRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(String value) {
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        addFileRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder mouthFlux(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(String value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    public FileAlarmConfigRequest commonAlarmSetting(Integer checkTemplateEnum, Integer compareType, double value) {
        FileAlarmConfigRequest newAlarmConfigRequest = new FileAlarmConfigRequest();

        newAlarmConfigRequest.setUploadAbnormalValue(getUploadAbnormalValue());
        newAlarmConfigRequest.setDeleteFailCheckResult(getDeleteFailCheckResult());
        newAlarmConfigRequest.setUploadRuleMetricValue(getUploadRuleMetricValue());
        newAlarmConfigRequest.setRuleMetricEnCode(getRuleMetricEnCode());
        newAlarmConfigRequest.setFileOutputName(fileOutPut);
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
                addFileRuleRequest.setAlarm(false);
                LOGGER.info("");
        }

        return newAlarmConfigRequest;
    }

    @Override
    public AddRequestBuilder fixValueEqual(double value) {
        List<FileAlarmConfigRequest> alarmVariable = addFileRuleRequest.getAlarmVariable();
        alarmVariable.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmVariable);
        addFileRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueNotEqual(double value) {
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        addFileRuleRequest.setAlarm(true);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fixValueMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder mouthFlux(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekFlux(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayFlux(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_FLUCTUATION.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearNotEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.NOT_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreThan(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearLessOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.SMALLER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder hourOnHourMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HOUR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder dayOnDayMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.DAY_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder weekOnWeekMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.WEEK_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder monthOnMonthMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.MONTH_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder seasonOnSeasonMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.SEASON_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AddRequestBuilder yearOnYearMoreOrEqual(double value) {
        addFileRuleRequest.setAlarm(true);
        List<FileAlarmConfigRequest> alarmConfigRequests = addFileRuleRequest.getAlarmVariable();
        alarmConfigRequests.add(commonAlarmSetting(CheckTemplateEnum.YEAR_ON_YEAR.getCode(), CompareTypeEnum.BIGGER_EQUAL.getCode(), value));
        addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
        return this;
    }

    @Override
    public AbstractAddRequest returnRequest() {
        return addFileRuleRequest;
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

    public Integer getFileOutPut() {
        return fileOutPut;
    }

    public void setfileoutput(Integer fileOutPut) {
        this.fileOutPut = fileOutPut;
    }
}
