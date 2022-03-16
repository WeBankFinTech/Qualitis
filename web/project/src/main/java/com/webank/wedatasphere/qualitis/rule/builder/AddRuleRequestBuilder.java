package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Project project;
    private String ruleName;
    private String userName;


    private static Integer TWO = 2;
    private static Integer THREE = 3;

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

    private RuleMetricDao ruleMetricDao;

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
        addRuleRequest.setProjectId(project.getId());
        addRuleRequest.setRuleTemplateId(template.getId());
    }

    private void solveDatasource(String datasource) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>(1);
        List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>(1);
        DataSourceRequest dataSourceRequest = new DataSourceRequest();
        String clusterName;
        String database;
        String table;
        String col;
        String filter;

        String[] datasourceStrs = datasource.split(SpecCharEnum.COLON.getValue());
        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String dbAndTable = datasourceStrs[0];
        String dataSourceId = "";
        Matcher matcherId = DATA_SOURCE_ID.matcher(dbAndTable.toUpperCase());
        String dataSourceName = "";
        Matcher matcherName = DATA_SOURCE_NAME.matcher(dbAndTable.toUpperCase());

        while (matcherId.find()) {
            String group = matcherId.group();
            dataSourceId = group.replace(".(", "").replace(")", "").split("=")[1];
            int startIndex = dbAndTable.toUpperCase().indexOf(group);
            String replaceStr = dbAndTable.substring(startIndex, startIndex + group.length());
            dbAndTable = dbAndTable.replace(replaceStr, "");
        }
        if (StringUtils.isBlank(dataSourceId)) {
            while (matcherName.find()) {
                String group = matcherName.group();
                int startIndex = dbAndTable.toUpperCase().indexOf(group);
                String replaceStr = dbAndTable.substring(startIndex, startIndex + group.length());
                dataSourceName = replaceStr.replace(".(", "").replace(")", "").split("=")[1];
                dbAndTable = dbAndTable.replace(replaceStr, "");
            }
        }
        String[] datasources = dbAndTable.split(SpecCharEnum.PERIOD.getValue());
        filter = datasourceStrs[1];
        if (datasources.length != FOUR && datasources.length != THREE) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        clusterName = datasources[0];
        database = datasources[1];
        table = datasources[2];
        col = datasources.length >= FOUR ? datasources[3] : "";

        List<ColumnInfoDetail> cols = metaDataClient.getColumnInfo(clusterName, database, table, userName);

        if (StringUtils.isNotBlank(dataSourceId)) {
            LOGGER.info("Find data source connect. Data source ID: " + dataSourceId);
            dataSourceRequest.setLinkisDataSourceId(Long.parseLong(dataSourceId));
            GeneralResponse<Map> response = metaDataClient.getDataSourceInfoDetail(clusterName, userName, dataSourceRequest.getLinkisDataSourceId(), null);
            cols = metaDataClient.getColumnsByDataSource(clusterName, userName, dataSourceRequest.getLinkisDataSourceId(), database, table).getContent();
            Map dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            dataSourceRequest.setLinkisDataSourceName(dataSourceInfoName);
            dataSourceRequest.setLinkisDataSourceType(dataSourceInfoType);
        } else if (StringUtils.isNotBlank(dataSourceName)) {
            LOGGER.info("Find data source connect. Data source name: " + dataSourceName);
            GeneralResponse<Map> response = metaDataClient.getDataSourceInfoDetailByName(clusterName, userName, dataSourceName);

            Map dataSourceInfo = ((Map) response.getData().get("info"));
            String dataSourceInfoName = (String) dataSourceInfo.get("dataSourceName");
            String dataSourceInfoType = (String) ((Map) dataSourceInfo.get("dataSourceType")).get("name");
            Integer currentDataSourceId = (Integer) dataSourceInfo.get("id");
            dataSourceRequest.setLinkisDataSourceId(currentDataSourceId.longValue());
            cols = metaDataClient.getColumnsByDataSource(clusterName, userName, currentDataSourceId.longValue(), database, table).getContent();

            dataSourceRequest.setLinkisDataSourceName(dataSourceInfoName);
            dataSourceRequest.setLinkisDataSourceType(dataSourceInfoType);
        }

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
                    String type = cols.stream().filter( field -> field.getFieldName().equals(colName)).map(ColumnInfoDetail::getDataType).collect(Collectors.joining());
                    DataSourceColumnRequest dataSourceColumnRequest = new DataSourceColumnRequest(colName, type);
                    dataSourceColumnRequests.add(dataSourceColumnRequest);
                }
            }

            dataSourceRequest.setBlackList(blackList);
        }

        dataSourceRequest.setClusterName(clusterName);
        dataSourceRequest.setDbName(database);
        dataSourceRequest.setTableName(table);
        dataSourceRequest.setColNames(dataSourceColumnRequests);

        dataSourceRequest.setFilter(filter);
        dataSourceRequest.setProxyUser(userName);

        dataSourceRequests.add(dataSourceRequest);

        this.addRuleRequest.setDatasource(dataSourceRequests);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String regxOrRangeOrEnum, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
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

    private void templateArgumentSetting(List<TemplateArgumentRequest> templateArgumentRequests, String regxOrRange) {
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), regxOrRange));
            }
        }
        addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
    }

    @Override
    public AddRequestBuilder basicInfoWithDataSource(String datasource, String condition1, String condition2, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
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
    public AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String param1, String param2, boolean deleteFailCheckResult,
        boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return null;
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
        templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMetaList.get(0).getId(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), condition1));
        templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMetaList.get(1).getId(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), condition2));

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
    public AddRequestBuilder addRuleMetricWithCheck(String ruleMetricName, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) throws UnExpectedRequestException{
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
    public AddRequestBuilder mouthFlux(String value) {
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
    public AddRequestBuilder mouthFlux(double value) {
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
    public AbstractAddRequest returnRequest() {
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
