package com.webank.wedatasphere.qualitis.util;

import com.webank.wedatasphere.qualitis.client.AlarmClient;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.MetricData;
import com.webank.wedatasphere.qualitis.entity.ReportBatchInfo;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
public class ReportUtil {
    private static final String ABNORMAL_VALUE = "abnormal_value: ";
    private static final String RULE_METRIC_VALUE = "rule_metric_value: ";

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

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
                        && taskRuleAlarmConfig.getUploadAbnormalValue() != null && taskRuleAlarmConfig.getUploadAbnormalValue()) {
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
}
