package com.webank.wedatasphere.qualitis.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.qualitis.checkalert.dao.repository.CheckAlertWhiteListRepository;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlertWhiteList;
import com.webank.wedatasphere.qualitis.client.AlarmClient;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TemplateFunctionNameEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.WhiteListTypeEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
public class ReportUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

    private static final Gson GSON = new Gson();

    private ReportUtil() {
    }

    /**
     * Get all pass rule metric.
     * @param tasks
     * @param ruleMetricDao
     * @param imsConfig
     * @return
     */
    public static List<ReportBatchInfo> collectTaskResult(List<Task> tasks, TaskResultDao taskResultDao, TaskDataSourceDao taskDataSourceDao,
        RuleMetricDao ruleMetricDao, ImsConfig imsConfig) {
        List<ReportBatchInfo> allReportBatchInfo = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                long collectTimestamp = System.currentTimeMillis();

                // Rule attrs
                String ruleName = StringUtils.isNotBlank(taskRuleSimple.getCnName()) ?taskRuleSimple.getCnName() : taskRuleSimple.getRuleName();

                ReportBatchInfo ruleMetricReportBatchInfo = new ReportBatchInfo();
                ruleMetricReportBatchInfo.setUserAuthKey(imsConfig.getUserAuthKey());
                List<MetricData> metricDatas = new ArrayList<>();

                ReportBatchInfo abnormalReportBatchInfo = new ReportBatchInfo();
                abnormalReportBatchInfo.setUserAuthKey(imsConfig.getUserAuthKey());
                List<MetricData> abnormalMetricDatas = new ArrayList<>();

                // 相关数据源：指标值与异常值
                List<TaskDataSource> taskDataSources = taskDataSourceDao.findByTaskAndRuleId(task, taskRuleSimple.getRuleId());

                // Task result.
                for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
                    // 根据是否上报开关，保存需要上报的指标值
                    if (taskRuleAlarmConfig.getStatus().equals(AlarmConfigStatusEnum.PASS.getCode())
                            && taskRuleAlarmConfig.getUploadRuleMetricValue() != null && taskRuleAlarmConfig.getUploadRuleMetricValue() &&
                            taskRuleAlarmConfig.getRuleMetric() != null) {
                        Long ruleMetricId = taskRuleAlarmConfig.getRuleMetric().getId();
                        List<TaskResult> taskResults = taskResultDao.findByApplicationAndRule(taskRuleSimple.getApplicationId()
                                , taskRuleSimple.getRuleId()).stream().filter(tr -> tr.getRuleMetricId().equals(ruleMetricId))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(taskResults)) {
                            for (TaskResult taskResult : taskResults) {
                                RuleMetric ruleMetric = ruleMetricDao.findById(taskResult.getRuleMetricId());
                                metricDatas
                                        .add(constructMetaData(ruleMetric, taskResult, imsConfig, ruleName, collectTimestamp, taskDataSources));
                            }
                        }
                    }
                    // 根据是否上报异常数据开关，保存需要上报的异常值
                    if (taskRuleAlarmConfig.getStatus().equals(AlarmConfigStatusEnum.NOT_PASS.getCode())
                        && taskRuleAlarmConfig.getUploadAbnormalValue() != null && taskRuleAlarmConfig.getUploadAbnormalValue() && taskRuleAlarmConfig.getRuleMetric() != null) {
                        Long ruleMetricId = taskRuleAlarmConfig.getRuleMetric().getId();
                        List<TaskResult> taskResults = taskResultDao.findByApplicationAndRule(taskRuleSimple.getApplicationId()
                            , taskRuleSimple.getRuleId()).stream().filter(tr -> tr.getRuleMetricId().equals(ruleMetricId)).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(taskResults)) {
                            for (TaskResult taskResult : taskResults) {
                                RuleMetric ruleMetric = ruleMetricDao.findById(taskResult.getRuleMetricId());
                                abnormalMetricDatas.add(constructMetaData(ruleMetric, taskResult, imsConfig, ruleName, collectTimestamp, taskDataSources));
                            }
                        }
                    }

                }
                if (CollectionUtils.isNotEmpty(metricDatas)) {
                    ruleMetricReportBatchInfo.setMetricDataList(metricDatas);
                    allReportBatchInfo.add(ruleMetricReportBatchInfo);
                }
                if (CollectionUtils.isNotEmpty(abnormalMetricDatas)) {
                    abnormalReportBatchInfo.setMetricDataList(abnormalMetricDatas);
                    allReportBatchInfo.add(abnormalReportBatchInfo);
                }

            }
        }
        return allReportBatchInfo;
    }

    private static MetricData constructMetaData(RuleMetric ruleMetric, TaskResult taskResult, ImsConfig imsConfig, String ruleName,
        long collectTimestamp, List<TaskDataSource> taskDataSources) {
        MetricData metricData = new MetricData();
        metricData.setMetricValue(StringUtils.isBlank(taskResult.getValue()) ? "0" : taskResult.getValue());
        metricData.setHostIp(QualitisConstants.QUALITIS_SERVER_HOST);
        if (ruleMetric.getSubSystemId() != null) {
            metricData.setSubsystemId(ruleMetric.getSubSystemId());
        } else {
            metricData.setSubsystemId(imsConfig.getSystemId());
        }
        metricData.setInterfaceName("" + spliceDatabaseAndTable(taskDataSources));
        metricData.setAttrGroup(ruleName);
        metricData.setAttrName(ruleMetric.getName() + (StringUtils.isNotEmpty(taskResult.getEnvName()) ? taskResult.getEnvName() : ""));
        metricData.setCollectTimestamp(collectTimestamp + "");
        return metricData;
    }

    private static String spliceDatabaseAndTable(List<TaskDataSource> taskDataSources) {
        StringBuilder databaseAndTable = new StringBuilder();
        for (TaskDataSource taskDataSource : taskDataSources) {
            if (StringUtils.isNotEmpty(taskDataSource.getDatabaseName())) {
                databaseAndTable.append(taskDataSource.getDatabaseName()).append(SpecCharEnum.BOTTOM_BAR.getValue());
            }
            if (StringUtils.isNotEmpty(taskDataSource.getTableName())) {
                databaseAndTable.append(taskDataSource.getTableName()).append(SpecCharEnum.BOTTOM_BAR.getValue());
            }
            if (StringUtils.isNotEmpty(databaseAndTable.toString())) {
                break;
            }
        }
        return databaseAndTable.length() > 0 ? databaseAndTable.substring(0, databaseAndTable.length() - 1) : "";
    }

    public static void reportTaskResult(ReportBatchInfo reportBatchInfos, AlarmClient alarmClient) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(reportBatchInfos.getMetricDataList())) {
            throw new UnExpectedRequestException("Report metric data {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        alarmClient.report(reportBatchInfos);
    }

    public static List<DgsmData> collectDgsmData(Application application, List<Task> tasks, TaskResultDao taskResultDao, CheckAlertWhiteListRepository checkAlertWhiteListRepository) {
        if (null == application.getRuleSize() || StringUtils.isBlank(application.getRuleIds()) || CollectionUtils.isEmpty(tasks)) {
            LOGGER.warn("No rule to be report to dgsm.");
            return new ArrayList<>();
        }

        List<CheckAlertWhiteList> projectBlackLists = checkAlertWhiteListRepository.findItem(WhiteListTypeEnum.BLACK_DGSM_PROJECT.getCode());
        List<CheckAlertWhiteList> userBlackLists = checkAlertWhiteListRepository.findItem(WhiteListTypeEnum.BLACK_DGSM_USER.getCode());
        List<CheckAlertWhiteList> dbBlackLists = checkAlertWhiteListRepository.findItem(WhiteListTypeEnum.BLACK_DGSM_DB.getCode());

        if (CollectionUtils.isNotEmpty(projectBlackLists)) {
            boolean black = projectBlackLists.stream().map(checkAlertWhiteList -> checkAlertWhiteList.getItem().replace(SpecCharEnum.NOT.getValue(), "")).collect(Collectors.toList()).contains(application.getProjectName());
            if (black) {
                return new ArrayList<>();
            }
        }
        if (CollectionUtils.isNotEmpty(userBlackLists)) {
            boolean black = userBlackLists.stream().map(checkAlertWhiteList -> checkAlertWhiteList.getItem().replace(SpecCharEnum.NOT.getValue(), "")).collect(Collectors.toList()).contains(application.getExecuteUser());
            if (black) {
                return new ArrayList<>();
            }
        }

        List<DgsmData> dgsmDataList = new ArrayList<>(application.getRuleSize());

        List<TaskResult> taskResultList = taskResultDao.findByApplicationId(application.getId());
        Map<Long, List<TaskResult>> taskResultsGroupedByTaskId = taskResultList.stream().filter(taskResult -> null != taskResult.getTaskId()).collect(Collectors.groupingBy(TaskResult::getTaskId));

        for (Task task : tasks) {

            Set<TaskRuleSimple> taskRuleSimpleSet = task.getTaskRuleSimples();

            List<TaskResult> currentTaskResults = taskResultsGroupedByTaskId.get(task.getId());

            Map<Long, List<TaskResult>> taskResultsGroupedByRuleId;

            if (CollectionUtils.isNotEmpty(currentTaskResults)) {
                taskResultsGroupedByRuleId = currentTaskResults.stream().collect(Collectors.groupingBy(TaskResult::getRuleId));
            } else {
                continue;
            }

            Optional<TaskDataSource> taskDataSourceOptional = task.getTaskDataSources().stream().filter(taskDataSource -> ! TemplateDataSourceTypeEnum.HIVE.getCode().equals(taskDataSource.getDatasourceType())).findAny();
            if (taskDataSourceOptional.isPresent()) {
                LOGGER.warn("Current task has not hive datasource to report dgsm. Task ID: {}, Application ID: {}", task.getId(), application.getId());
                continue;
            }

            Map<Long, List<TaskDataSource>> taskDataSourcesGroupedByRuleId = task.getTaskDataSources().stream().collect(Collectors.groupingBy(TaskDataSource::getRuleId));

            for (TaskRuleSimple taskRuleSimple : taskRuleSimpleSet) {
                List<TaskResult> currentTaskRuleSimpleResults = taskResultsGroupedByRuleId.get(taskRuleSimple.getRuleId());
                List<TaskDataSource> taskDataSourceList = taskDataSourcesGroupedByRuleId.get(taskRuleSimple.getRuleId());

                if (CollectionUtils.isNotEmpty(dbBlackLists)) {
                    List<String> blackDbNames = dbBlackLists.stream().map(checkAlertWhiteList -> checkAlertWhiteList.getItem().replace(SpecCharEnum.NOT.getValue(), "")).collect(Collectors.toList());
                    List<String> dbNames = taskDataSourceList.stream().map(taskDataSource -> taskDataSource.getDatabaseName()).collect(Collectors.toList());
                    // 存在交集，方法返回 Collections.disjoint(blackDbNames, dbNames):false
                    if (! Collections.disjoint(blackDbNames, dbNames)) {
                        continue;
                    }
                }
                List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();
                for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleAlarmConfigList) {
                    DgsmData dgsmData = new DgsmData();
                    // 检核规则类型，非空、唯一等模板的名称，可提供映射关系与 DGSM 同步
                    dgsmData.setCheckRuleType(TemplateFunctionNameEnum.getDgsmRuleTypeByEnName(taskRuleSimple.getTemplateEnName()));
                    dgsmData.setCheckRuleDescription(StringUtils.isNotBlank(taskRuleSimple.getRuleDetail()) ? taskRuleSimple.getRuleDetail() : "无规则描述");
                    dgsmData.setCheckRuleCode(taskRuleSimple.getRuleId().toString());
                    // 检核规则内容
                    String checkPartition = taskDataSourceList.stream().map(taskDataSource -> taskDataSource.getFilter()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
                    dgsmData.setCheckRuleContent(new CheckRuleContent(taskRuleAlarmConfig.getThreshold().toString(), gran(taskDataSourceList), range(taskRuleAlarmConfig), rules(CompareTypeEnum.getOperator(taskRuleAlarmConfig.getCompareType()), taskRuleAlarmConfig.getThreshold().toString(), checkPartition)));
                    dgsmData.setBusinessSystem(QualitisConstants.SUB_SYSTEM_ID);
                    dgsmData.setCheckDatabaseName(taskDataSourceList.stream().filter(taskDataSource -> StringUtils.isNotBlank(taskDataSource.getDatabaseName())).map(taskDataSource -> taskDataSource.getDatabaseName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
                    dgsmData.setCheckTableName(taskDataSourceList.stream().filter(taskDataSource -> StringUtils.isNotBlank(taskDataSource.getTableName())).map(taskDataSource -> taskDataSource.getTableName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
                    dgsmData.setCheckFieldName(taskDataSourceList.stream().filter(taskDataSource -> StringUtils.isNotBlank(taskDataSource.getColName())).map(taskDataSource -> {
                        if (taskDataSource.getColName().contains(SpecCharEnum.COLON.getValue())) {
                            return taskDataSource.getColName().split(SpecCharEnum.COLON.getValue())[0];
                        }
                        return taskDataSource.getColName();
                    }).collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
                    dgsmData.setComparisonMethod(CompareTypeEnum.getOperator(taskRuleAlarmConfig.getCompareType()));
                    dgsmData.setAlertType(alertType(task.getAbortOnFailure(), taskRuleSimple.getAlertLevel()));
                    dgsmData.setAlertReceiver(taskRuleSimple.getAlertReceiver());
                    dgsmData.setRegRuleCode(taskRuleSimple.getRegRuleCode());

                    if (CollectionUtils.isNotEmpty(currentTaskRuleSimpleResults)) {
                        Optional<TaskResult> taskResultOptional;
                        if (null != taskRuleAlarmConfig.getRuleMetric()) {
                            taskResultOptional = currentTaskRuleSimpleResults.stream().filter(taskResult -> taskRuleAlarmConfig.getRuleMetric().getId().equals(taskResult.getRuleMetricId())).findFirst();
                        } else {
                            taskResultOptional = currentTaskRuleSimpleResults.stream().findFirst();
                        }
                        if (taskResultOptional.isPresent()) {
                            TaskResult taskResult = taskResultOptional.get();
                            dgsmData.setCheckTime(taskResult.getCreateTime());
                            dgsmData.setRuleExecutionResult(taskResult.getValue());
                        } else {
                            dgsmData.setCheckTime(application.getFinishTime());
                            dgsmData.setRuleExecutionResult("none");
                        }
                    } else {
                        dgsmData.setCheckTime(application.getFinishTime());
                        dgsmData.setRuleExecutionResult("none");
                    }
                    dgsmData.setCheckStatus(status(taskRuleAlarmConfig.getStatus()));
                    dgsmData.setCheckPartition(checkPartition);
                    dgsmDataList.add(dgsmData);
                }
            }
        }

        return dgsmDataList;
    }

    private static String rules(String operator, String value, String checkPartition) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("combine", "");
        map.put("condition", StringUtils.isNotBlank(checkPartition) ? checkPartition : "");
        map.put("operation", operator);
        map.put("value", value);
        mapList.add(map);

        return GSON.toJson(mapList);
    }

    private static String status(Integer status) {
        if (AlarmConfigStatusEnum.PASS.getCode().equals(status)) {
            return "1";
        }

        return "0";
    }

    // 1-告警并断批，2-仅告警
    private static String alertType(Boolean abortOnFailure, Integer alertLevel) {
        if (Boolean.TRUE.equals(abortOnFailure) && null != alertLevel) {
            return "1";
        }
        return "2";
    }

    private static String range(TaskRuleAlarmConfig taskRuleAlarmConfig) {
        if (CheckTemplateEnum.FIXED_VALUE.getCode().equals(taskRuleAlarmConfig.getCheckTemplate())) {
            return "SP";
        }
        return "CP";
    }

    private static String gran(List<TaskDataSource> taskDataSourceList) {
        Set<String> dbAndTables = taskDataSourceList.stream().map(taskDataSource -> taskDataSource.getDatabaseName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + taskDataSource.getTableName()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(dbAndTables) && dbAndTables.size() > 1) {
            return "CT";
        }
        Set<String> columns = taskDataSourceList.stream().map(taskDataSource -> taskDataSource.getColName()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(columns) && columns.size() > 1) {
            return "STMF";
        }
        if (CollectionUtils.isNotEmpty(columns) && columns.size() == 1) {
            return "SF";
        }
        return "ST";
    }
}
