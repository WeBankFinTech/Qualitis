package com.webank.wedatasphere.qualitis.util;

import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.client.AlarmClient;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.AlertTypeEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ImsLevelEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.AlarmInfoDao;
import com.webank.wedatasphere.qualitis.dao.ApplicationCommentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultStatusDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskResultStatus;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
public class AlarmUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

    private static final String ADMIN = "ADMIN";

    private AlarmUtil() {

    }

    public static List<TaskRuleAlarmConfig> notSafeAlarmConfig(List<Task> tasks) {
        List<TaskRuleAlarmConfig> taskRuleAlarmConfigs = new ArrayList<>();
        for (Task task : tasks) {
            for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                if (taskRuleSimple.getAlertLevel() == null) {
                    continue;
                }
                for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
                    if (taskRuleAlarmConfig.getStatus().equals(AlarmConfigStatusEnum.NOT_PASS.getCode())) {
                        taskRuleAlarmConfigs.add(taskRuleAlarmConfig);
                    }
                }
            }
        }
        return taskRuleAlarmConfigs;
    }

    public static List<TaskRuleSimple> notSafeTaskRuleSimple(List<Task> tasks) {
        List<TaskRuleSimple> taskRuleSimples = new ArrayList<>();
        for (Task task : tasks) {
            for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();

                for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleAlarmConfigList) {
                    if (taskRuleAlarmConfig.getStatus().equals(AlarmConfigStatusEnum.NOT_PASS.getCode())) {
                        taskRuleSimples.add(taskRuleSimple);
                        break;
                    }
                }
            }
        }
        return taskRuleSimples;
    }

    public static void sendAlarmMessage(Application application, List<TaskRuleSimple> checkFailedRules, ImsConfig imsConfig, AlarmClient client
            , AlarmInfoDao alarmInfoDao, UserDao userDao, TaskResultStatusDao taskResultStatusDao, Integer alert, String alertReceiver, Boolean flag) {
        boolean bdap = imsConfig.getTitlePrefix().contains("BDAP");
        // 获取告警内容
        StringBuilder alertInfo = new StringBuilder();
        List<Map<String, Object>> requestList = new ArrayList<>(checkFailedRules.size());
        // 遍历每一个规则
        for (TaskRuleSimple taskRuleSimpleTemp : checkFailedRules) {
            Map<String, Object> request = new HashMap<>(6);
            // 获取告警标题
            String cnName = taskRuleSimpleTemp.getCnName();
            String enName = taskRuleSimpleTemp.getRuleName();
            String realRuleName = StringUtils.isNotEmpty(cnName) ? cnName : enName;

            String enProjectName = taskRuleSimpleTemp.getProjectName();
            String cnProjectName = taskRuleSimpleTemp.getProjectCnName();
            String realProjectName = StringUtils.isNotEmpty(cnProjectName) ? cnProjectName : enProjectName;

            String alertTitle = "";
            alertInfo.append(getDepartAlerters(taskRuleSimpleTemp, userDao, AlertTypeEnum.TASK_FAIL_CHECKOUT.getCode(), StringUtils.isNotBlank(alertReceiver) ? alertReceiver : taskRuleSimpleTemp.getAlertReceiver())).append("\n");
            if (flag) {
                alertTitle = imsConfig.getNewTitleSucceedPrefix() + "【" + realRuleName + "】";
                alertInfo.append("Qualitis项目: ").append(realProjectName).append("， 技术规则: ").append(realRuleName).append(" 任务校验通过.\n");
            } else {
                alertTitle = imsConfig.getNewTitlePrefix() + "【" + realRuleName + "】";
                alertInfo.append("Qualitis项目: ").append(realProjectName).append("， 技术规则: ").append(realRuleName).append(" 任务校验不通过.\n");
            }
            alertInfo.append("库表信息：").append(retrieveDatasource(taskRuleSimpleTemp)).append("\n");

            List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimpleTemp.getTaskRuleAlarmConfigList();
            Map<Long, TaskRuleAlarmConfig> taskRuleAlarmConfigMap = taskRuleAlarmConfigList.stream().collect(Collectors.toMap(TaskRuleAlarmConfig::getId, t -> t, (oValue, nValue) -> nValue));
            List<TaskResultStatus> taskResultStatusList = taskResultStatusDao.findByStatus(application.getId(), taskRuleSimpleTemp.getRuleId(), AlarmConfigStatusEnum.NOT_PASS.getCode());
            for (TaskResultStatus taskResultStatus : taskResultStatusList) {
                TaskRuleAlarmConfig alarmConfig = taskRuleAlarmConfigMap.get(taskResultStatus.getTaskRuleAlarmConfigId());
                if (null == alarmConfig) {
                    continue;
                }
                TaskResult taskResult = taskResultStatus.getTaskResult();
                String value = StringUtils.isBlank(taskResult.getValue()) ? "empty value" : taskResult.getValue();
                String compareValue = StringUtils.isBlank(taskResult.getCompareValue()) ? "empty value" : taskResult.getCompareValue();
                if (alarmConfig.getRuleMetric() == null || alarmConfig.getRuleMetric().getId().equals(taskResult.getRuleMetricId())) {
                    alarmStringAppend(alertInfo, alarmConfig, value, compareValue, realRuleName, realProjectName, taskResult.getEnvName());
                }
            }

            alertInfo.append("\n也可进入 Qualitis 系统查看详情。");
            List<RuleMetric> ruleMetrics = taskRuleSimpleTemp.getTaskRuleAlarmConfigList().stream().map(TaskRuleAlarmConfig::getRuleMetric)
                    .filter(ruleMetric -> ruleMetric != null).collect(Collectors.toList());
            // 获取告警规则关联子系统
            int subSystemId = QualitisConstants.SUB_SYSTEM_ID;
            if (CollectionUtils.isEmpty(ruleMetrics)) {
                LOGGER.info("Qualitis find project's subsystem ID or datasource's subsystem ID because there is no rule metric. Rule name: " + realRuleName);
                if (null != application.getSubSystemId()) {
                    subSystemId = application.getSubSystemId().intValue();
                }
                if (taskRuleSimpleTemp.getTask() != null && CollectionUtils.isNotEmpty(taskRuleSimpleTemp.getTask().getTaskDataSources())) {
                    List<Long> subSystemIds = taskRuleSimpleTemp.getTask().getTaskDataSources().stream()
                            .map(taskDataSource -> taskDataSource.getSubSystemId()).filter(ele -> ele != null).collect(
                                    Collectors.toList());
                    if (CollectionUtils.isNotEmpty(subSystemIds)) {
                        Long currentSubSystemId = subSystemIds.iterator().next();
                        if (currentSubSystemId != null) {
                            subSystemId = currentSubSystemId.intValue();
                        }
                    }
                }
            } else {
                // 获取子系统
                if (ruleMetrics.iterator().next().getSubSystemId() != null) {
                    subSystemId = ruleMetrics.iterator().next().getSubSystemId();
                }
            }
            // 获取告警级别
            int alertLevel = alert != null ? alert.intValue() : taskRuleSimpleTemp.getAlertLevel().intValue();
            // 获取告警人
            List<String> receivers = getReceivers(taskRuleSimpleTemp, AlertTypeEnum.TASK_FAIL_CHECKOUT.getCode(), StringUtils.isNotBlank(alertReceiver) ? alertReceiver : taskRuleSimpleTemp.getAlertReceiver());
            // Alert object: database1[table1,table2];database2[table3,table4]
            String alertObj = contructAlertObj(taskRuleSimpleTemp.getTask().getTaskDataSources());
            // 封装告警
            packageAlarm(alertInfo, request, alertTitle, subSystemId, alertLevel, receivers, alertObj, imsConfig);
            requestList.add(request);

            // 保存alarm_info表
            for (String username : receivers) {
                AlarmInfo alarmInfo = new AlarmInfo(getAlertLevel(alertLevel), alertInfo.toString(), application.getId(), application.getSubmitTime(),
                        application.getFinishTime(), application.getFinishTime(), username, AlertTypeEnum.TASK_FAIL_CHECKOUT.getCode(), realProjectName);
                alarmInfoDao.save(alarmInfo);
            }

            if (bdap) {
                client.sendAlarm(StringUtils.join(receivers, ","), imsConfig.getTitlePrefix() + "集群 Qualitis 任务告警\n"
                        , alertInfo.toString(), String.valueOf(alertLevel));
            }

            alertInfo.delete(0, alertInfo.length());
        }
        if (CollectionUtils.isNotEmpty(requestList) && !bdap) {
            client.sendNewAlarm(requestList);
        }
    }

    private static String retrieveDatasource(TaskRuleSimple taskRuleSimpleTemp) {
        if (CollectionUtils.isNotEmpty(taskRuleSimpleTemp.getTask().getTaskDataSources())) {
            String dbAndTable = taskRuleSimpleTemp.getTask().getTaskDataSources().stream().filter(taskDataSource -> taskDataSource.getRuleId().equals(taskRuleSimpleTemp.getRuleId())).map(taskDataSource ->
                    (StringUtils.isNotEmpty(taskDataSource.getDatabaseName()) ? taskDataSource.getDatabaseName() : "") + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() +
                            (StringUtils.isNotEmpty(taskDataSource.getTableName()) ? taskDataSource.getTableName() : "") + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() +
                            (StringUtils.isNotEmpty(taskDataSource.getColName()) ? taskDataSource.getColName() : "[]"))
                    .collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));
            return dbAndTable;
        }
        return "";
    }

    private static void packageAlarm(StringBuilder alertInfo, Map<String, Object> request, String alertTitle, int subSystemId, int alertLevel,
                                     List<String> receivers, String alertObj, ImsConfig imsConfig) {
        request.put("alert_reciver", StringUtils.join(receivers, ","));
        request.put("alert_info", alertInfo.toString());
        request.put("sub_system_id", subSystemId);
        request.put("alert_title", alertTitle);
        request.put("alert_level", alertLevel);
        request.put("alert_obj", alertObj);
        // RTX, EMAIL
        request.put("alert_way", imsConfig.getAlertWay());
        request.put("ci_type_id", 55);
    }

    /**
     * 告警内容添加科室通知人
     *
     * @param taskRuleSimple taskRuleSimple
     * @param userDao        userDao
     * @param alarmCode
     * @return 请通知XX
     */
    public static String getDepartAlerters(TaskRuleSimple taskRuleSimple, UserDao userDao, Integer alarmCode, String alertReceiver) {
        User creator = userDao.findByUsername(taskRuleSimple.getProjectCreator());
        StringBuilder alerters = new StringBuilder("请通知：");
        String creatorName = creator.getUsername();
        alerters.append("告警接收人").append(alertReceiver);
        Set<String> departAlerters = new HashSet<>();

        if (alarmCode.equals(AlertTypeEnum.TASK_FAILED.getCode())) {
            Role role = SpringContextHolder.getBean(RoleDao.class).findByRoleName(ADMIN);
            Set<String> admins = SpringContextHolder.getBean(UserRoleDao.class).findByRole(role).stream().map(UserRole::getUser).map(User::getUsername).collect(Collectors.toSet());
            departAlerters.addAll(admins);
            departAlerters.remove(creatorName);

            if (departAlerters.isEmpty()) {
                return alerters.toString();
            }
            // 增加失败人
            alerters.append("，大数据平台室").append(StringUtils.join(departAlerters, ","));
            return alerters.toString();
        } else if (alarmCode.equals(AlertTypeEnum.TASK_FAIL_CHECKOUT.getCode())) {
            return alerters.toString();
        }

        return alerters.toString();
    }

    /**
     * 告警内容添加初始化失败接收人信息
     *
     * @param rules
     * @return 请通知XX
     */
    public static String getDepartAlerters(List<Rule> rules) {
        StringBuilder alerters = new StringBuilder("请通知：");
        alerters.append("告警接收人");
        for (Rule rule : rules) {
            if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                if (executionParameters != null) {
                    //兼容旧规则数据
                    if (StringUtils.isNotBlank(executionParameters.getAlertReceiver())) {
                        if (!alerters.toString().contains(executionParameters.getAlertReceiver())) {
                            alerters.append(
                                    StringUtils.isNotEmpty(executionParameters.getAlertReceiver()) ? executionParameters.getAlertReceiver() : "未设置")
                                    .append("或创建用户")
                                    .append(rule.getCreateUser())
                                    .append(";");
                        }
                        continue;
                    }
                    //0.23.0版本
                    if (CollectionUtils.isNotEmpty(executionParameters.getAlarmArgumentsExecutionParameters())) {
                        for (AlarmArgumentsExecutionParameters parameters : executionParameters.getAlarmArgumentsExecutionParameters()) {
                            if (!alerters.toString().contains(parameters.getAlarmReceiver())) {
                                alerters.append(StringUtils.isNotEmpty(parameters.getAlarmReceiver()) ? parameters.getAlarmReceiver() : "未设置")
                                        .append("或创建用户")
                                        .append(rule.getCreateUser())
                                        .append(";");
                            }
                        }
                    }

                }
            } else {
                alerters.append(StringUtils.isNotEmpty(rule.getAlertReceiver()) ? rule.getAlertReceiver() : "未设置")
                        .append("或创建用户")
                        .append(rule.getCreateUser())
                        .append(";");
            }
        }
        alerters.append("\n");
        return alerters.toString();
    }

    /**
     * Qualitis 项目xxx 技术规则xxx 任务运行完成，不符合数据质量要求。原因：任务运行结果: [5], 超出设定阈值: [4], 比较模版: [月波动], 比较方式: [大于]
     *
     * @param alertInfo
     * @param alarmConfig
     */
    private static void alarmStringAppend(StringBuilder alertInfo, TaskRuleAlarmConfig alarmConfig, String value, String compareValue, String ruleName, String projectName, String envName) {
        Integer checkTemplate = alarmConfig.getCheckTemplate();
        String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(checkTemplate);
        alertInfo.append("Qualitis项目: ").append(projectName).
                append(" 技术规则: ").append(ruleName)
                .append(" 任务运行完成, 不符合数据质量要求。原因: ")
                .append(alarmConfig.getOutputName() + " - [").append(StringUtils.isEmpty(value) ? "" : value)
                .append(alarmConfig.getOutputUnit() == null ? "" : alarmConfig.getOutputUnit()).append("]")
                .append(", 不符合设定阈值: [").append(alarmConfig.getThreshold()).append(alarmConfig.getOutputUnit() == null ? "" : alarmConfig.getOutputUnit())
                .append("]")
                .append(", 比较模版: [").append(checkTemplateName).append("]");
        if (checkTemplate.equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            Integer compareType = alarmConfig.getCompareType();
            String compareTypeName = CompareTypeEnum.getCompareTypeName(compareType);
            alertInfo.append(", 比较方式: [").append(compareTypeName).append("]");
        } else if (checkTemplate.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())
                || checkTemplate.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())) {
            alertInfo.append(", 计算过程：(本期 - 上期) / 上期：")
                    .append(compareValue);
        } else if (checkTemplate.equals(CheckTemplateEnum.DAY_FLUCTUATION.getCode())
                || checkTemplate.equals(CheckTemplateEnum.MONTH_FLUCTUATION.getCode())
                || checkTemplate.equals(CheckTemplateEnum.WEEK_FLUCTUATION.getCode())) {
            alertInfo.append(", 过去的平均值：").append(compareValue);
        }
        if (StringUtils.isNotBlank(envName)) {
            alertInfo.append("。环境名称: [").append(envName).append("]");
        }
        alertInfo.append("\n");
    }

    public static List<String> getReceivers(TaskRuleSimple taskRuleSimple, Integer alarmCode, String alertReceiver) {
        List<String> users = new ArrayList<>();
        // 增加规则关注人
        if (StringUtils.isNotBlank(alertReceiver)) {
            Collections.addAll(users, alertReceiver.split(","));
        }
        if (!users.contains(taskRuleSimple.getProjectCreator())) {
            // 增加创建者
            users.add(taskRuleSimple.getProjectCreator());
        }
        if (alarmCode.equals(AlertTypeEnum.TASK_FAILED.getCode()) || alarmCode.equals(AlertTypeEnum.TASK_FAIL_CHECKOUT.getCode())) {
            // Automatically grant the highest authority to the system administrator.
            // 增加超出阈值告警人
            Role role = SpringContextHolder.getBean(RoleDao.class).findByRoleName(ADMIN);
            Set<String> admins = SpringContextHolder.getBean(UserRoleDao.class).findByRole(role).stream().map(UserRole::getUser).map(User::getUsername).collect(Collectors.toSet());
            for (String user : admins) {
                if (!users.contains(user)) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    private static String getAlertLevel(Integer alert) {
        String alertLevel;
        if (alert.toString().equals(ImsLevelEnum.CRITICAL.getCode())) {
            alertLevel = ImsLevelEnum.CRITICAL.getCode();
        } else if (alert.toString().equals(ImsLevelEnum.MAJOR.getCode())) {
            alertLevel = ImsLevelEnum.MAJOR.getCode();
        } else if (alert.toString().equals(ImsLevelEnum.MINOR.getCode())) {
            alertLevel = ImsLevelEnum.MINOR.getCode();
        } else if (alert.toString().equals(ImsLevelEnum.WARNING.getCode())) {
            alertLevel = ImsLevelEnum.WARNING.getCode();
        } else {
            alertLevel = ImsLevelEnum.INFO.getCode();
        }
        return alertLevel;
    }

    public static Map<Long, List<TaskRuleSimple>> rulePartitionByProject(List<TaskRuleSimple> taskRuleSimples) {
        Map<Long, List<TaskRuleSimple>> result = new HashMap<>(2);
        for (TaskRuleSimple taskRuleSimple : taskRuleSimples) {
            if (!result.containsKey(taskRuleSimple.getProjectId())) {
                List<TaskRuleSimple> tmp = new ArrayList<>();
                tmp.add(taskRuleSimple);
                result.put(taskRuleSimple.getProjectId(), tmp);
            } else {
                result.get(taskRuleSimple.getProjectId()).add(taskRuleSimple);
            }
        }

        return result;
    }

    public static Map<Long, List<TaskRuleAlarmConfig>> alarmConfigPartitionByProject(List<TaskRuleAlarmConfig> alarmConfigs) {
        Map<Long, List<TaskRuleAlarmConfig>> result = new HashMap<>(2);
        for (TaskRuleAlarmConfig alarmConfig : alarmConfigs) {
            Long projectId = alarmConfig.getTaskRuleSimple().getProjectId();

            if (!result.containsKey(projectId)) {
                List<TaskRuleAlarmConfig> tmp = new ArrayList<>();
                tmp.add(alarmConfig);
                result.put(projectId, tmp);
            } else {
                result.get(projectId).add(alarmConfig);
            }
        }
        return result;
    }

    public static void sendFailedMessage(Application application, List<TaskRuleSimple> failedRules, ImsConfig imsConfig, AlarmClient client
            , AlarmInfoDao alarmInfoDao, UserDao userDao, Integer alertRank, String alertReceiver) {
        boolean bdap = imsConfig.getTitlePrefix().contains("BDAP");
        // 获取告警内容
        StringBuilder alertInfo = new StringBuilder();
        List<Map<String, Object>> requestList = new ArrayList<>(failedRules.size());

        for (TaskRuleSimple taskRuleSimpleTemp : failedRules) {
            Map<String, Object> request = new HashMap<>(6);
            // 获取告警标题
            String cnName = taskRuleSimpleTemp.getCnName();
            String enName = taskRuleSimpleTemp.getRuleName();
            String realRuleName = StringUtils.isNotEmpty(cnName) ? cnName : enName;

            String enProjectName = taskRuleSimpleTemp.getProjectName();
            String cnProjectName = taskRuleSimpleTemp.getProjectCnName();
            String realPorjectName = StringUtils.isNotEmpty(cnProjectName) ? cnProjectName : enProjectName;
            String alertTitle = imsConfig.getNewTitlePrefix() + "【" + realRuleName + "】";
            alertInfo.append(getDepartAlerters(taskRuleSimpleTemp, userDao, AlertTypeEnum.TASK_FAILED.getCode(), StringUtils.isNotBlank(alertReceiver) ? alertReceiver : taskRuleSimpleTemp.getAlertReceiver())).append("\n");
            alertInfo.append("Qualitis项目: ").append(realPorjectName).append("， 技术规则: ").append(realRuleName).append(" 任务执行失败.\n");
            alertInfo.append("任务编号: ").append(application.getId()).append(". ");
            if (null != application.getApplicationComment()) {
                ApplicationComment applicationComment = SpringContextHolder.getBean(ApplicationCommentDao.class).getByCode(application.getApplicationComment());
                alertInfo.append("任务备注： ").append(applicationComment != null ? applicationComment.getZhMessage() : null).append(". ");
            }
            alertInfo.append("\n");
            alertInfo.append("\n也可进入 Qualitis 系统查看详情。");
            List<RuleMetric> ruleMetrics = taskRuleSimpleTemp.getTaskRuleAlarmConfigList().stream().map(TaskRuleAlarmConfig::getRuleMetric)
                    .filter(ruleMetric -> ruleMetric != null).collect(Collectors.toList());
            int subSystemId = QualitisConstants.SUB_SYSTEM_ID;
            if (CollectionUtils.isEmpty(ruleMetrics)) {
                LOGGER.info("Qualitis find project's subsystem ID or datasource's subsystem ID because there is no rule metric. Rule name: " + realRuleName);
                if (null != application.getSubSystemId()) {
                    subSystemId = application.getSubSystemId().intValue();
                }
                if (taskRuleSimpleTemp.getTask() != null && CollectionUtils.isNotEmpty(taskRuleSimpleTemp.getTask().getTaskDataSources())) {
                    List<Long> subSystemIds = taskRuleSimpleTemp.getTask().getTaskDataSources().stream()
                            .map(taskDataSource -> taskDataSource.getSubSystemId()).filter(ele -> ele != null).collect(
                                    Collectors.toList());
                    if (CollectionUtils.isNotEmpty(subSystemIds)) {
                        Long currentSubSystemId = subSystemIds.iterator().next();
                        if (currentSubSystemId != null) {
                            subSystemId = currentSubSystemId.intValue();
                        }
                    }
                }
            } else {
                // 获取子系统
                if (ruleMetrics.iterator().next().getSubSystemId() != null) {
                    subSystemId = ruleMetrics.iterator().next().getSubSystemId();
                }
            }
            // 获取告警人
            List<String> receivers = getReceivers(taskRuleSimpleTemp, AlertTypeEnum.TASK_FAILED.getCode(), StringUtils.isNotBlank(alertReceiver) ? alertReceiver : taskRuleSimpleTemp.getAlertReceiver());

            // 获取告警级别
            int alertLevel = alertRank != null ? alertRank.intValue() : taskRuleSimpleTemp.getAlertLevel();
            // Alert object: database1[table1,table2];database2[table3,table4]
            String alertObj = contructAlertObj(taskRuleSimpleTemp.getTask().getTaskDataSources());
            // 封装告警
            request.put("alert_reciver", StringUtils.join(receivers, ","));
            request.put("alert_info", alertInfo.toString());
            request.put("sub_system_id", subSystemId);
            request.put("alert_title", alertTitle);
            request.put("alert_level", alertLevel);
            request.put("alert_obj", alertObj);
            // RTX, EMAIL
            request.put("alert_way", imsConfig.getAlertWay());
            request.put("ci_type_id", 55);
            requestList.add(request);

            // 保存alarm_info表
            for (String username : receivers) {
                AlarmInfo alarmInfo = new AlarmInfo(getAlertLevel(alertLevel), alertInfo.toString(), application.getId(), application.getSubmitTime(),
                        application.getFinishTime(), application.getFinishTime(), username, AlertTypeEnum.TASK_FAILED.getCode(), realPorjectName);
                alarmInfoDao.save(alarmInfo);
            }

            if (bdap) {
                client.sendAlarm(StringUtils.join(receivers, ","), imsConfig.getTitlePrefix() + "集群 Qualitis 任务告警\n"
                        , alertInfo.toString(), String.valueOf(alertLevel));
            }
            alertInfo.delete(0, alertInfo.length());
        }
        // 发送告警
        if (CollectionUtils.isNotEmpty(requestList) && !bdap) {
            client.sendNewAlarm(requestList);
        }
    }

    private static String contructAlertObj(Set<TaskDataSource> taskDataSources) {
        Map<String, List<String>> dbAndTables = new HashMap<>(taskDataSources.size());
        for (TaskDataSource taskDataSource : taskDataSources) {
            String databaseName = taskDataSource.getDatabaseName();
            String tableName = taskDataSource.getTableName();

            if (dbAndTables.keySet().contains(databaseName)) {
                dbAndTables.get(databaseName).add(tableName);
            } else {
                List<String> tables = new ArrayList<>();
                tables.add(tableName);

                dbAndTables.put(databaseName, tables);
            }
        }

        List<String> dbs = new ArrayList<>(dbAndTables.keySet().size());
        StringBuilder tempDb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : dbAndTables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            dbs.add(tempDb.append(key).append("[").append(StringUtils.join(value, ",")).append("]").toString());
            tempDb.delete(0, tempDb.length());
        }

        return StringUtils.join(dbs, ";");
    }

    public static List<TaskRuleSimple> getFailedTaskRule(List<Task> tasks) {
        List<TaskRuleSimple> taskRuleSimples = new ArrayList<>();
        for (Task task : tasks) {
            taskRuleSimples.addAll(task.getTaskRuleSimples());
        }

        return taskRuleSimples;
    }

    public static void sendInitFailedMessage(Application application, ApplicationComment applicationComment, List<Rule> rules, ImsConfig imsConfig, AlarmClient client, AlarmInfoDao alarmInfoDao) {
        // 获取告警标题
        String alertTitle = imsConfig.getTitlePrefix() + "集群 Qualitis 任务告警\n";

        // 获取告警内容
        StringBuilder alertContent = new StringBuilder();
        int maxLevel = Integer.parseInt(ImsLevelEnum.INFO.getCode());
        // 获取告警接受者
        Set<String> receivers = new HashSet<>();

        StringBuilder alerters = new StringBuilder("请通知：");
        alerters.append("告警接收人");
        for (Rule rule : rules) {
            if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                if (executionParameters != null) {
                    //兼容旧规则数据
                    if (StringUtils.isNotBlank(executionParameters.getAlertReceiver())) {
                        if (!alerters.toString().contains(executionParameters.getAlertReceiver())) {
                            alerters.append(
                                    StringUtils.isNotEmpty(executionParameters.getAlertReceiver()) ? executionParameters.getAlertReceiver() : "未设置")
                                    .append("或创建用户")
                                    .append(rule.getCreateUser())
                                    .append(";");
                        }

                        maxLevel = getMaxLevelAndReceivers(maxLevel, receivers, executionParameters);
                        continue;
                    }

                    //0.23.0版本
                    if (CollectionUtils.isNotEmpty(executionParameters.getAlarmArgumentsExecutionParameters())) {
                        for (AlarmArgumentsExecutionParameters parameters : executionParameters.getAlarmArgumentsExecutionParameters()) {
                            if (!alerters.toString().contains(parameters.getAlarmReceiver())) {
                                alerters.append(StringUtils.isNotEmpty(parameters.getAlarmReceiver()) ? parameters.getAlarmReceiver() : "未设置")
                                        .append("或创建用户")
                                        .append(rule.getCreateUser())
                                        .append(";");
                            }
                            // Fix: add receivers when init failed.
                            if (StringUtils.isNotEmpty(parameters.getAlarmReceiver())) {
                                receivers.addAll(Arrays.asList(parameters.getAlarmReceiver().split(SpecCharEnum.COMMA.getValue())));
                            }
                            if (parameters.getAlarmLevel() != null && parameters.getAlarmLevel() < maxLevel) {
                                maxLevel = parameters.getAlarmLevel();
                            }
                        }

                    }

                }
            } else {
                alerters.append(StringUtils.isNotEmpty(rule.getAlertReceiver()) ? rule.getAlertReceiver() : "未设置")
                        .append("或创建用户")
                        .append(rule.getCreateUser())
                        .append(";");
                // Fix: add receivers when init failed.
                if (StringUtils.isNotEmpty(rule.getAlertReceiver())) {
                    receivers.addAll(Arrays.asList(rule.getAlertReceiver().split(SpecCharEnum.COMMA.getValue())));
                }
            }
        }
        alerters.append("\n");
        alertContent.append(alerters);

        alertContent.append("Qualitis项目： ").append(application.getProjectName()).append("，Qualitis任务: ").append(application.getId())
                .append(" 含有技术规则: ").append("\n");

        for (Rule rule : rules) {
            alertContent.append(StringUtils.isNotEmpty(rule.getCnName()) ? rule.getCnName() : rule.getName()).append("\n");

        }

        if (ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode().equals(application.getStatus())) {
            alertContent.append("初始化失败，失败备注：");
        }
        if (applicationComment != null) {
            alertContent.append(applicationComment.getZhMessage()).append("\n").append("也可进入 Qualitis 系统查看详情。").append("\n");
        } else {
            alertContent.append(application.getExceptionMessage()).append("\n").append("也可进入 Qualitis 系统查看详情。").append("\n");
        }

        Role role = SpringContextHolder.getBean(RoleDao.class).findByRoleName(ADMIN);
        Set<String> admins = SpringContextHolder.getBean(UserRoleDao.class).findByRole(role).stream().map(UserRole::getUser).map(User::getUsername).collect(Collectors.toSet());

        receivers.addAll(admins);

        // 保存alarm_info表
        for (String username : receivers) {
            AlarmInfo alarmInfo = new AlarmInfo(maxLevel + "", alertContent.toString(), application.getId(), application.getSubmitTime(),
                    application.getFinishTime(), application.getFinishTime(), username, AlertTypeEnum.TASK_INIT_FAIL.getCode(), application.getProjectName());
            alarmInfoDao.save(alarmInfo);
        }

        // 发送告警
        if (CollectionUtils.isNotEmpty(receivers)) {
            client.sendAlarm(StringUtils.join(receivers, ","), alertTitle, alertContent.toString(), maxLevel + "");
        }
    }

    public static void sendInitFailedMessage(Application application, CheckAlert checkAlert, ImsConfig imsConfig, AlarmClient client, AlarmInfoDao alarmInfoDao) {
        // 获取告警标题
        String alertTitle = imsConfig.getTitlePrefix() + "集群 Qualitis 任务告警\n";

        // 获取告警内容
        StringBuilder alertContent = new StringBuilder();

        // 获取告警接受者
        Set<String> receivers = new HashSet<>();

        StringBuilder alerters = new StringBuilder("请通知：");
        alerters.append("告警接收人").append(checkAlert.getCreateUser()).append("；\n");

        alertContent.append(alerters);
        alertContent.append("Qualitis项目： ").append(application.getProjectName()).append("，Qualitis任务: ").append(application.getId()).append(" 含有告警规则: ").append("\n").append(checkAlert.toString()).append("\n");

        alertContent.append("初始化失败，失败详情：").append(application.getExceptionMessage()).append("\n").append("也可进入 Qualitis 系统查看详情。").append("\n");

        Role role = SpringContextHolder.getBean(RoleDao.class).findByRoleName(ADMIN);
        Set<String> admins = SpringContextHolder.getBean(UserRoleDao.class).findByRole(role).stream().map(UserRole::getUser).map(User::getUsername).collect(Collectors.toSet());

        receivers.addAll(admins);

        // 保存alarm_info表
        for (String username : receivers) {
            AlarmInfo alarmInfo = new AlarmInfo(ImsLevelEnum.WARNING.getCode(), alertContent.toString(), application.getId(), application.getSubmitTime(), application.getFinishTime(), application.getFinishTime(), username, AlertTypeEnum.TASK_INIT_FAIL.getCode(), application.getProjectName());
            alarmInfoDao.save(alarmInfo);
        }

        // 发送告警
        if (CollectionUtils.isNotEmpty(receivers)) {
            client.sendAlarm(StringUtils.join(receivers, ","), alertTitle, alertContent.toString(), ImsLevelEnum.WARNING.getCode());
        }
    }

    private static int getMaxLevelAndReceivers(int maxLevel, Set<String> receivers, ExecutionParameters executionParameters) {
        //告警级别比对、告警人拼接
        if (executionParameters.getAlertLevel() != null && StringUtils.isNotEmpty(executionParameters.getAlertReceiver())) {
            String[] receiverSet = executionParameters.getAlertReceiver().split(SpecCharEnum.COMMA.getValue());
            for (String currentReceiver : receiverSet) {
                receivers.add(currentReceiver);
            }
            if (executionParameters.getAlertLevel() < maxLevel) {
                maxLevel = executionParameters.getAlertLevel();
            }
        }
        return maxLevel;
    }

    public static void sendAbnormalDataRecordAlarm(ImsConfig imsConfig, AlarmClient alarmClient, List<Map<String, Object>> data) {
        alarmClient.sendAbnormalDataRecordAlarm(imsConfig, data);
    }
}
