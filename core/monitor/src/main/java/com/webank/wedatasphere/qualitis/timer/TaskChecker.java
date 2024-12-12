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

package com.webank.wedatasphere.qualitis.timer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.bean.JobChecker;
import com.webank.wedatasphere.qualitis.client.AlarmClient;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationCommentEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ImsLevelEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.AbnormalDataRecordInfoDao;
import com.webank.wedatasphere.qualitis.dao.AlarmInfoDao;
import com.webank.wedatasphere.qualitis.dao.ApplicationCommentDao;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.LinksErrorCodeDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultStatusDao;
import com.webank.wedatasphere.qualitis.dao.TaskRuleAlarmConfigDao;
import com.webank.wedatasphere.qualitis.dao.TaskRuleSimpleDao;
import com.webank.wedatasphere.qualitis.dao.UploadRecordDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.AbnormalDataRecordInfo;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import com.webank.wedatasphere.qualitis.entity.LinksErrorCode;
import com.webank.wedatasphere.qualitis.entity.ReportBatchInfo;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskResultStatus;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.entity.UploadRecord;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.rule.constant.NoiseStrategyEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.response.CheckConditionsResponse;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.AlarmUtil;
import com.webank.wedatasphere.qualitis.util.PassUtil;
import com.webank.wedatasphere.qualitis.util.ReportUtil;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
public class TaskChecker implements IChecker {
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskResultDao taskResultDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private TaskDataSourceDao taskDataSourceDao;
    @Autowired
    private TaskRuleAlarmConfigDao taskRuleAlarmConfigDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ImsConfig imsConfig;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private UploadRecordDao uploadRecordDao;
    @Autowired
    private ExecutionManager executionManager;
    @Autowired
    private TaskRuleSimpleDao taskRuleSimpleDao;
    @Autowired
    private AbnormalDataRecordInfoDao abnormalDataRecordInfoDao;
    @Autowired
    private AlarmInfoDao alarmInfoDao;
    @Autowired
    private AlarmClient alarmClient;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskResultStatusDao taskResultStatusDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private LinksErrorCodeDao linksErrorCodeDao;
    @Autowired
    private ApplicationCommentDao applicationCommentDao;

//    @Value("${intellect.check.project_name}")
//    private String intellectCheckProjectName;

    @Value("${alarm.ims.receiver.collect:leoli,dqdong}")
    private String collectReceiver;

    private static final int BATCH_ABNORMAL_DATA_RECORD = 500;
    private static final String PRINT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskChecker.class);
    private static final DateTimeFormatter PRINT_TIME_FORMAT = DateTimeFormat.forPattern(PRINT_TIME_PATTERN);
    private static final java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern(PRINT_TIME_PATTERN);

    private static final Map<String, Integer> ERR_CODE_TYPE = new HashMap<String, Integer>();

    private static final List<ApplicationComment> APPLICATION_COMMENT_LIST = Lists.newArrayList();
    private static final String IMS_LOG = "{\"TYPE\": \"QUALITIS\",\"RES_CODE\": %d,\"COST_TIME\": %d,\"RES_MSG\": \"%s\"}";

    @PostConstruct
    public void init() {
        List<LinksErrorCode> allLinksErrorCode = linksErrorCodeDao.findAllLinksErrorCode();
        if (CollectionUtils.isNotEmpty(allLinksErrorCode)) {
            for (LinksErrorCode linksErrorCode : allLinksErrorCode) {
                ERR_CODE_TYPE.put(linksErrorCode.getLinkisErrorCode(), linksErrorCode.getApplicationComment());
            }
        }

        List<ApplicationComment> allApplicationComment = applicationCommentDao.findAllApplicationComment();
        if (CollectionUtils.isNotEmpty(allApplicationComment)) {
            APPLICATION_COMMENT_LIST.addAll(allApplicationComment);
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkTaskStatus(JobChecker jobChecker) {
        try {
            Map<String, Object> taskInfos = monitorManager.getTaskStatus(jobChecker.getTaskId(), jobChecker.getUsername(),
                    jobChecker.getUjesAddress(), jobChecker.getClusterName());
            String jobStatus = ((String) taskInfos.get("status")).toUpperCase();
            Integer errCode = (Integer) taskInfos.get("errCode");
            LOGGER.info("Task status: {}", jobStatus);

            if (!jobStatus.equals(jobChecker.getOldStatus())) {
                LOGGER.info("Start to update task status. old status: {}, new status: {}, task ID: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
                writeDb(jobChecker, jobStatus, errCode);
                LOGGER.info("Succeed to update task status. old status: {}, new status: {}, task ID: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
            }

            // Compute task time in same progress.
            if (linkisConfig.getKillStuckTasks() && TaskStatusEnum.RUNNING.getState().equals(jobStatus)) {
                Task taskInDb = taskDao.findByRemoteTaskIdAndClusterName(jobChecker.getTaskId(), jobChecker.getClusterName());
                Double progress = (Double) taskInfos.get("progress");
                LOGGER.info("Old time progress[{}].", jobChecker.getOldProgress());
                LOGGER.info("Current time progress[{}].", progress);
                long runningTime = System.currentTimeMillis() - taskInDb.getRunningTime();
                LOGGER.info("Current task running time [{}] minutes.", runningTime / (60 * 1000));
                //1.等于0是相等  2.>0前者大于后者 3.反之 <0 前者小于后者
                if (BigDecimal.valueOf(progress).compareTo(BigDecimal.valueOf(jobChecker.getOldProgress())) == 0) {
                    long diff = System.currentTimeMillis() - taskInDb.getNewProgressTime();
                    long diffMinutes = diff;
                    LOGGER.info("Time in same progress[{}]: {} minutes. Config max time: {} minutes.", progress, diffMinutes / (60 * 1000)
                            , linkisConfig.getKillStuckTasksTime().longValue() / (60 * 1000));
                    if (diffMinutes > linkisConfig.getKillStuckTasksTime().longValue()) {
                        killTimeoutTask(applicationDao.findById(jobChecker.getApplicationId()), taskInDb, jobChecker);
                    }
                } else {
                    LOGGER.info("Progress is updating, so is task new progress.");
                    taskInDb.setNewProgressTime(System.currentTimeMillis());
                    taskInDb.setProgress(progress);
                    if (runningTime > linkisConfig.getKillTotalTasksTime().longValue()) {
                        LOGGER.info("Running cost time more than doubled config max time : {} minutes.", linkisConfig.getKillTotalTasksTime().longValue() / (60 * 1000));
                        killTimeoutTask(applicationDao.findById(jobChecker.getApplicationId()), taskInDb, jobChecker);
                    }
                }

                taskDao.save(taskInDb);
            }
        } catch (TaskNotExistException e) {
            LOGGER.error("Spark Task [{}] does not exist, application id : [{}]", jobChecker.getTaskId(), jobChecker.getApplicationId(), e);
            jobChecker.getTask().setStatus(TaskStatusEnum.TASK_NOT_EXIST.getCode());
            taskDao.save(jobChecker.getTask());
            jobChecker.getTask().getApplication().addAbnormalTaskNum();
            applicationDao.saveApplication(jobChecker.getTask().getApplication());
        } catch (Exception e) {
            LOGGER.error("Check task id:[{}] failed, application id:[{}]", jobChecker.getTaskId(), jobChecker.getApplicationId(), e);
        }
    }

    private void killTimeoutTask(Application applicationInDb, Task taskInDb, JobChecker jobChecker) {
        LOGGER.warn("Start to kill timeout task. Task remote ID:[{}]", taskInDb.getTaskRemoteId());
        // Kill timeout task.
        try {
            executionManager.killApplication(applicationInDb, jobChecker.getUsername());
        } catch (JobKillException e) {
            LOGGER.error("Kill timeout task failed. Qualitis try to kill again. Exception: {}", e.getMessage(), e);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Kill timeout task failed. Qualitis try to kill again. Exception: {}", e.getMessage(), e);
        } catch (ResourceAccessException e) {
            LOGGER.error("Kill timeout task failed. Qualitis try to kill again.Exception: {}", e.getMessage(), e);
        } catch (ClusterInfoNotConfigException e) {
            LOGGER.error("Kill timeout task failed. Qualitis try to kill again.Exception: {}", e.getMessage(), e);
        }
        LOGGER.warn("Finish to kill timeout task. Task remote ID:[{}]", taskInDb.getTaskRemoteId());
    }

    @Override
    public void checkIfLastJob(Application applicationInDb, boolean finish, boolean isPass, boolean isNotExist) {
        if (finish) {
            if (isPass) {
                applicationInDb.addSuccessJobNum();
                LOGGER.info("Application add successful task, application: {}", applicationInDb);
            } else {
                applicationInDb.addNotPassTaskNum();
                LOGGER.info("Application add not pass task, application: {}", applicationInDb);
            }
        } else if (!isNotExist) {
            applicationInDb.addFailJobNum();
            LOGGER.info("Application add failed task, application: {}", applicationInDb);
        } else {
            applicationInDb.addAbnormalTaskNum();
            LOGGER.info("Application add abnormal task, application: {}", applicationInDb);
        }
        ifLastTaskAndSaveApplication(applicationInDb);
    }

    private void writeDb(JobChecker jobChecker, String newStatus, Integer errCode) {
        Task taskInDb = taskDao.findByRemoteTaskIdAndClusterName(jobChecker.getTaskId(), jobChecker.getClusterName());
        Application applicationInDb = applicationDao.findById(jobChecker.getApplicationId());

        if (newStatus.equals(TaskStatusEnum.FAILED.getState())) {
            /*
             * 1.Modify end time of job
             * 2.Modify task finish time and failed num if last job
             * */
            taskInDb.setEndTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            taskInDb.setTaskComment(errCode == null ? applicationCommentCode : ERR_CODE_TYPE.get(String.valueOf(errCode)));
            modifyJobStatus(taskInDb, newStatus);
            taskDao.save(taskInDb);
            applicationInDb.setApplicationComment(errCode == null ? applicationCommentCode : ERR_CODE_TYPE.get(String.valueOf(errCode)));
            checkIfLastJob(applicationInDb, false, false, false);
        } else if (newStatus.equals(TaskStatusEnum.SUCCEED.getState())) {
            /*
             * 1.Modify end time of job
             * 2.Modify task finish time and succeed num if last job
             * */
            taskInDb.setEndTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            boolean isPass;
            boolean finish;
            if (passCheckOut(jobChecker.getApplicationId(), taskInDb)) {
                LOGGER.info("Check passed! Task:[{}]", taskInDb.getId());
                modifyJobStatus(taskInDb, TaskStatusEnum.PASS_CHECKOUT.getState());
                isPass = true;
                finish = true;
            } else {
                LOGGER.info("Check not passed! Task:[{}]", taskInDb.getId());
                if (Boolean.FALSE.equals(checkWhetherBlocked(taskInDb)) && Boolean.TRUE.equals(taskInDb.getAbortOnFailure())) {
                    modifyJobStatus(taskInDb, TaskStatusEnum.FAILED.getState());
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    applicationInDb.setApplicationComment(applicationCommentCode);
                    finish = false;
                } else {
                    modifyJobStatus(taskInDb, TaskStatusEnum.FAIL_CHECKOUT.getState());
                    finish = true;
                }
                isPass = false;
            }
            taskDao.save(taskInDb);
            checkIfLastJob(applicationInDb, finish, isPass, false);
        } else if (newStatus.equals(TaskStatusEnum.CANCELLED.getState())) {
            modifyJobStatus(taskInDb, newStatus);
            taskDao.save(taskInDb);
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.TIMEOUT_KILL.getCode().toString())).collect(Collectors.toList());
            Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            applicationInDb.setApplicationComment(applicationCommentCode);
            checkIfLastJob(applicationInDb, false, false, false);
        } else {
            modifyJobStatus(taskInDb, newStatus);
            taskDao.save(taskInDb);
        }
    }

    private void modifyJobStatus(Task task, String newStatus) {
        if (newStatus.equals(TaskStatusEnum.SUBMITTED.getState())) {
            task.setStatus(TaskStatusEnum.SUBMITTED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.INITED.getState())) {
            task.setStatus(TaskStatusEnum.INITED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.RUNNING.getState())) {
            task.setStatus(TaskStatusEnum.RUNNING.getCode());
            if (null == task.getRunningTime()) {
                task.setRunningTime(System.currentTimeMillis());
            }
            task.getApplication().setStatus(ApplicationStatusEnum.RUNNING.getCode());
            if (task.getNewProgressTime() == null) {
                task.setNewProgressTime(System.currentTimeMillis());
            }
            if (task.getApplication().getTotalTaskNum() != null) {
                applicationDao.saveApplication(task.getApplication());
            }
            LOGGER.info("Succeed to set application status to [{}], application: {}", ApplicationStatusEnum.RUNNING.getState(), task.getApplication());
        } else if (newStatus.equals(TaskStatusEnum.SUCCEED.getState())) {
            task.setStatus(TaskStatusEnum.SUCCEED.getCode());
            task.setProgress(Double.parseDouble("1"));
        } else if (newStatus.equals(TaskStatusEnum.FAILED.getState())) {
            task.setStatus(TaskStatusEnum.FAILED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.PASS_CHECKOUT.getState())) {
            task.setStatus(TaskStatusEnum.PASS_CHECKOUT.getCode());
            task.setProgress(Double.parseDouble("1"));
        } else if (newStatus.equals(TaskStatusEnum.FAIL_CHECKOUT.getState())) {
            task.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
            task.setProgress(Double.parseDouble("1"));
        } else if (newStatus.equals(TaskStatusEnum.CANCELLED.getState())) {
            task.setStatus(TaskStatusEnum.CANCELLED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.TIMEOUT.getState())) {
            task.setStatus(TaskStatusEnum.TIMEOUT.getCode());
        } else if (newStatus.equals(TaskStatusEnum.SCHEDULED.getState())) {
            task.setStatus(TaskStatusEnum.SCHEDULED.getCode());
        } else {
            LOGGER.error("Error! Task Status: [{}] is not understanding", newStatus);
        }
        LOGGER.info("Succeed to set task status to [{}], task: {}", newStatus, task);
    }

    private Boolean passCheckOut(String applicationId, Task task) {
        Boolean passFlag = true;
        for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
            if (!checkTaskRuleSimplePass(applicationId, taskRuleSimple)) {
                passFlag = false;
            }
        }

        return passFlag;
    }

    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    private Boolean checkTaskRuleSimplePass(String applicationId, TaskRuleSimple taskRuleSimple) {
        Boolean passFlag = Boolean.TRUE;
        Application application = applicationDao.findById(applicationId);
        if (StringUtils.isNotEmpty(application.getClusterName()) && application.getClusterName().contains(SpecCharEnum.COMMA.getValue()) && (QualitisConstants.MULTI_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateEnName()) || QualitisConstants.SINGLE_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateName()))) {
            return Boolean.TRUE;
        }
        List<TaskResult> taskResults = taskResultDao.findByApplicationAndRule(applicationId, taskRuleSimple.getRuleId());
        if (CollectionUtils.isEmpty(taskResults)) {
            LOGGER.warn("Has no task result. Application:[{}], Rule[{}]", applicationId, taskRuleSimple.getRuleId());
            return false;
        }
        List<TaskResultStatus> taskResultStatusList = Lists.newArrayList();
        for (TaskResult taskResult : taskResults) {
            List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();
            Long ruleMetricId = taskResult.getRuleMetricId();
            if (ruleMetricId != null && ruleMetricId.longValue() != -1) {
                taskRuleAlarmConfigList = taskRuleAlarmConfigList.stream().filter(taskRuleAlarmConfig ->
                        taskRuleAlarmConfig.getRuleMetric().getId().equals(ruleMetricId)
                ).collect(Collectors.toList());
            }
            // 遍历校验预期
            for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleAlarmConfigList) {
                TaskResultStatus taskResultStatus = new TaskResultStatus();
                taskResultStatus.setApplicationId(applicationId);
                taskResultStatus.setRuleId(taskRuleSimple.getRuleId());
                taskResultStatus.setTaskRuleAlarmConfigId(taskRuleAlarmConfig.getId());
                taskResultStatus.setTaskResult(taskResult);
                taskResultStatusList.add(taskResultStatus);

                Boolean passReal = PassUtil.notSafe(applicationId, taskRuleSimple.getRuleId(), taskRuleAlarmConfig, taskResult, taskResultDao);

                if (passReal) {
                    if (! AlarmConfigStatusEnum.NOT_PASS.getCode().equals(taskRuleAlarmConfig.getStatus())) {
                        taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                    }
                    taskResultStatus.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                    LOGGER.info("Current task rule alarm config passed. TaskRuleAlarmConfig:[{}]", taskRuleAlarmConfig.toString());
                } else {
                    passFlag = false;
                    taskResultStatus.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                    taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                    LOGGER.info("Current task rule alarm config not passed. TaskRuleAlarmConfig:[{}]", taskRuleAlarmConfig.toString());

                    if (taskRuleSimple.getRuleType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())
                        || taskRuleSimple.getRuleType().equals(RuleTemplateTypeEnum.FILE_COUSTOM.getCode())) {
                        if (taskRuleAlarmConfig.getDeleteFailCheckResult() != null && true == taskRuleAlarmConfig.getDeleteFailCheckResult().booleanValue()) {
                            taskResult.setSaveResult(false);
                            taskResultDao.saveTaskResult(taskResult);
                        }
                    } else {
                        if (taskRuleSimple.getDeleteFailCheckResult() != null && true == taskRuleSimple.getDeleteFailCheckResult().booleanValue()) {
                            taskResult.setSaveResult(false);
                            taskResultDao.saveTaskResult(taskResult);
                        }
                    }
                }
            }
            taskRuleAlarmConfigDao.saveAll(taskRuleAlarmConfigList);
        }
        taskResultStatusDao.saveBatch(taskResultStatusList);
        return passFlag;
    }

    private void ifLastTaskAndSaveApplication(Application applicationInDb) {
        if (isLastJob(applicationInDb)) {
            LOGGER.info("Succeed to execute all task of application. Application: {}", applicationInDb);
            applicationInDb.setFinishTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            if (StringUtils.isNotEmpty(applicationInDb.getClusterName()) && applicationInDb.getClusterName().contains(SpecCharEnum.COMMA.getValue())) {
                List<TaskResult> taskResults = taskResultDao.findByApplicationId(applicationInDb.getId());
                Map<Long, List<TaskResult>> ruleTaskResults = new HashMap<>(taskResults.size());
                for (TaskResult taskResult : taskResults) {
                    Long ruleId = taskResult.getRuleId();
                    if (ruleTaskResults.keySet().contains(ruleId)) {
                        ruleTaskResults.get(ruleId).add(taskResult);
                    } else {
                        List<TaskResult> tmpTaskResults = new ArrayList<>();
                        tmpTaskResults.add(taskResult);
                        ruleTaskResults.put(ruleId, tmpTaskResults);
                    }
                }

                for (Long ruleId : ruleTaskResults.keySet()) {
                    List<TaskRuleSimple> taskRuleSimples = taskRuleSimpleDao.findByApplicationAndRule(applicationInDb.getId(), ruleId);
                    boolean allMatch = taskRuleSimples.stream().allMatch(taskRuleSimple -> QualitisConstants.MULTI_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateEnName()) || QualitisConstants.SINGLE_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateName()));
                    if (! allMatch) {
                        continue;
                    }
                    boolean allSucc = taskRuleSimples.stream().allMatch(taskRuleSimple -> TaskStatusEnum.PASS_CHECKOUT.getCode().equals(taskRuleSimple.getTask().getStatus()));
                    if (! allSucc) {
                        continue;
                    }
                    boolean pass = true;
//                    boolean allZero = true;

                    List<TaskResult> taskResultList = ruleTaskResults.get(ruleId);
                    TaskResult first = taskResultList.get(0);
                    String value = first.getValue();

//                    if (0 != Integer.valueOf(value)) {
//                        allZero = false;
//                    }
                    List<TaskResultStatus> taskResultStatusList = Lists.newArrayList();
                    for (TaskResult taskResult : taskResultList) {
                        TaskResultStatus taskResultStatus = new TaskResultStatus();
                        taskResultStatus.setRuleId(ruleId);
                        taskResultStatus.setTaskResult(taskResult);
                        taskResultStatus.setApplicationId(applicationInDb.getId());
                        taskResultStatus.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                        Long taskRuleAlarmConfigId = taskRuleSimples.stream().filter(taskRuleSimple -> taskRuleSimple.getTask().getId().equals(taskResult.getTaskId())).iterator().next().getTaskRuleAlarmConfigList().stream().iterator().next().getId();
                        taskResultStatus.setTaskRuleAlarmConfigId(taskRuleAlarmConfigId);
                        taskResultStatusList.add(taskResultStatus);
                        if (! value.equals(taskResult.getValue())) {
                            pass = false;
                            break;
                        }
//                        if (allZero && 0 != Integer.valueOf(taskResult.getValue())) {
//                            allZero = false;
//                        }
                    }
//                    if (allZero) {
//                        pass = false;
//                    }

                    if (! pass) {
                        applicationInDb.reduceSuccessJobNum();
                        applicationInDb.reduceSuccessJobNum();
                        taskDao.saveAll(taskRuleSimples.stream().map(taskRuleSimple -> {
                            Task task = taskRuleSimple.getTask();
                            if (Boolean.TRUE.equals(task.getAbortOnFailure())) {
                                applicationInDb.addFailJobNum();
                                task.setStatus(TaskStatusEnum.FAILED.getCode());
                            } else {
                                applicationInDb.addNotPassTaskNum();
                                task.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
                            }
                            return task;
                        }).collect(Collectors.toList()));
                        taskRuleAlarmConfigDao.saveAll(taskRuleSimples.stream()
                            .map(taskRuleSimple -> taskRuleSimple.getTaskRuleAlarmConfigList())
                            .flatMap(taskRuleAlarmConfigs -> taskRuleAlarmConfigs.stream())
                            .map(taskRuleAlarmConfig -> {
                            taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                            return taskRuleAlarmConfig;
                        }).collect(Collectors.toList()));
                        taskResultStatusDao.saveBatch(taskResultStatusList.stream().map(taskResultStatus -> {
                            taskResultStatus.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                            return taskResultStatus;
                        }).collect(Collectors.toList()));
                    } else {
                        taskRuleAlarmConfigDao.saveAll(taskRuleSimples.stream().map(taskRuleSimple -> taskRuleSimple.getTaskRuleAlarmConfigList()).flatMap(taskRuleAlarmConfigs -> taskRuleAlarmConfigs.stream()).map(taskRuleAlarmConfig -> {
                            taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                            return taskRuleAlarmConfig;
                        }).collect(Collectors.toList()));
                        taskResultStatusDao.saveBatch(taskResultStatusList.stream().map(taskResultStatus -> {
                            taskResultStatus.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                            return taskResultStatus;
                        }).collect(Collectors.toList()));
                    }
                }
            }
            if (applicationInDb.getFinishTaskNum().equals(applicationInDb.getTotalTaskNum())) {
                applicationInDb.setStatus(ApplicationStatusEnum.FINISHED.getCode());
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream()
                    .filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.SAME_ISSUES.getCode().toString()))
                    .collect(Collectors.toList());
                Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;
                applicationInDb.setApplicationComment(applicationCommentCode);
                printImsLog(applicationInDb,ApplicationStatusEnum.FINISHED);
            } else if (!applicationInDb.getFailTaskNum().equals(0) || !applicationInDb.getAbnormalTaskNum().equals(0)) {
                applicationInDb.setStatus(ApplicationStatusEnum.FAILED.getCode());
                printImsLog(applicationInDb,ApplicationStatusEnum.FAILED);
            } else {
                applicationInDb.setStatus(ApplicationStatusEnum.NOT_PASS.getCode());
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;
                applicationInDb.setApplicationComment(applicationCommentCode);
                printImsLog(applicationInDb,ApplicationStatusEnum.NOT_PASS);
            }
            checkIfSendAlarm(applicationInDb);
            checkIfReport(applicationInDb, imsConfig);
        }
        if (applicationInDb.getTotalTaskNum() != null) {
            applicationDao.saveApplication(applicationInDb);
        }
        LOGGER.info("Succeed to save application. Application: {}", applicationInDb);
    }

    private boolean isLastJob(Application application) {
        LOGGER.info("Calculate application num: application.getFinishTaskNum ADD application.getFailTaskNum ADD application.getNotPassTaskNum ADD application.getAbnormalTaskNum = {} ADD {} ADD {} ADD {} vs application.getTotalTaskNum == {}",
                application.getFinishTaskNum(), application.getFailTaskNum(), application.getNotPassTaskNum(), application.getAbnormalTaskNum(), application.getTotalTaskNum());
        if (application.getTotalTaskNum() == null) {
            return false;
        }
        return application.getFinishTaskNum() + application.getFailTaskNum() + application.getNotPassTaskNum() + application.getAbnormalTaskNum() == application.getTotalTaskNum();
    }

    private void checkIfSendAlarm(Application application) {
        if (StringUtils.isNotBlank(application.getCollectIds())) {
            LOGGER.info("Start to alarm collect task.");
            List<Task> tasks = taskDao.findByApplication(application);
            List<Task> failedTask = tasks.stream().filter(job -> TaskStatusEnum.FAILED.getCode().equals(job.getStatus()) || TaskStatusEnum.CANCELLED.getCode().equals(job.getStatus())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(failedTask)) {
                LOGGER.info("No failed collect task.");
                return;
            }
//            Set<String> dbTableFilters = failedTask.stream().map(task -> task.getTaskDataSources())
//                    .flatMap(taskDataSources -> taskDataSources.stream()).map(taskDataSource -> taskDataSource.getDatabaseName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + taskDataSource.getTableName() + SpecCharEnum.COLON.getValue() + taskDataSource.getFilter()).collect(Collectors.toSet());
//            Set<Long> failedTaskRemoteIds = failedTask.stream().map(task -> task.getTaskRemoteId()).collect(Collectors.toSet());
//            String alertInfo = linkisConfig.getCollectTemplate();
//            alertInfo = alertInfo.replace("dbTableFilters", StringUtils.join(dbTableFilters, SpecCharEnum.COMMA.getValue())).replace("applicationID", application.getId()).replace("failedTaskRemoteIds", Arrays.toString(failedTaskRemoteIds.toArray()));
//            alarmClient.sendAlarm(imsConfig.getFailReceiver() + SpecCharEnum.COMMA.getValue() + collectReceiver, imsConfig.getTitlePrefix() + "集群 Qualitis 采集任务告警", alertInfo, String.valueOf(ImsLevelEnum.MINOR.getCode()), QualitisConstants.SUB_SYSTEM_ID);
            LOGGER.info("Finish to alarm collect task.");
            return;
        }

        LOGGER.info("Start to collect alarm info.");
        List<Task> tasks = taskDao.findByApplication(application);

//      Previously, tasks that didn't pass and were aborted had been modified as failed tasks, but when sending alarm to receivers,
//      their status still appeared as 'not pass' and 'aborted'
        List<Task> notPassAndAbortTask = new ArrayList<>();
        for (Task task : tasks) {
            if (TaskStatusEnum.FAILED.getCode().equals(task.getStatus())) {
                boolean hasNo = ifTaskHasNotCheckRuleAlarmConfig(task.getTaskRuleSimples());
                if (hasNo) {
                    notPassAndAbortTask.add(task);
                }
            }
        }

        List<Task> notPassTask = tasks.stream().filter(job -> job.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notPassAndAbortTask)) {
            notPassTask.addAll(notPassAndAbortTask);
        }

        LOGGER.info("Succeed to collect failed pass tasks. Task ID: {}", notPassTask.stream().map(Task::getId).collect(Collectors.toList()));
        List<TaskRuleSimple> notPassTaskRuleSimples = AlarmUtil.notSafeTaskRuleSimple(notPassTask);
        List<TaskRuleSimple> checkAlarmAcrossClusters = notPassTaskRuleSimples.stream().filter(taskRuleSimple -> QualitisConstants.MULTI_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateEnName()) || QualitisConstants.SINGLE_SOURCE_ACROSS_TEMPLATE_NAME.equals(taskRuleSimple.getTemplateName())).collect(Collectors.toList());
        List<Long> alarmedRuleIds = null;
        if (CollectionUtils.isNotEmpty(checkAlarmAcrossClusters)) {
            alarmedRuleIds = new ArrayList<>(checkAlarmAcrossClusters.size());
        }
        List<Task> failedTask = tasks.stream().filter(job -> job.getStatus().equals(TaskStatusEnum.FAILED.getCode()) || job.getStatus().equals(TaskStatusEnum.CANCELLED.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notPassAndAbortTask)) {
            failedTask = failedTask.stream().filter(task -> ! notPassAndAbortTask.contains(task)).collect(Collectors.toList());
        }

        LOGGER.info("Succeed to collect failed tasks. Task ID: {}", failedTask.stream().map(Task::getId).collect(Collectors.toList()));
        List<TaskRuleSimple> failedTaskRuleSimples = AlarmUtil.getFailedTaskRule(failedTask);
        for (Iterator<TaskRuleSimple> taskRuleSimpleIterator = failedTaskRuleSimples.iterator(); taskRuleSimpleIterator.hasNext(); ) {
            TaskRuleSimple taskRuleSimple = taskRuleSimpleIterator.next();
            List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();
            int count = (int) taskRuleAlarmConfigList.stream().filter(o -> ! AlarmConfigStatusEnum.PASS.getCode().equals(o.getStatus())).count();
            if (0 == count) {
                taskRuleSimpleIterator.remove();
            }
        }

        // 是否告警都跟配置的告警事件来判断  AlarmEventEnum a. only pass b. task failed, not pass + abort, not pass + not abort c. pass, not pass + abort, not pass + not abort
        // CHECK_SUCCESS 校验成功、CHECK_FAILURE 校验失败、EXECUTION_COMPLETED 执行完成
        List<String> alreadyAlertApp = new ArrayList<>();
        for (Task task : tasks) {
            Set<TaskRuleSimple> taskRuleSimpleCollect = task.getTaskRuleSimples();
            for (TaskRuleSimple taskRuleSimple : taskRuleSimpleCollect) {
                if (CollectionUtils.isNotEmpty(alarmedRuleIds) && alarmedRuleIds.contains(taskRuleSimple.getRuleId())) {
                    continue;
                }
                boolean isAcrossCluster = false;
                Rule rule = ruleDao.findById(taskRuleSimple.getRuleId());
                if (checkAlarmAcrossClusters.contains(taskRuleSimple)) {
                    alarmedRuleIds.add(taskRuleSimple.getRuleId());
                    isAcrossCluster = true;
                }
                if (rule != null && StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                    ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                    if (executionParameters != null) {
                        // strategy: 1只告警不阻断, 2不告警不阻断     checkEligible(匹配告警参数对象，1 run_date 符合 2 模板ID符合 3 校验条件符合)
                        // 当strategy不为null且级别等于1或2，配置去噪参数，都是不阻断的(前提匹配上去噪参数)，取校验条件的并集，决定是否属于去噪范围
                        Integer strategy = checkEligible(rule, executionParameters, taskRuleSimple);

                        boolean startAlarm = (Boolean.FALSE.equals(executionParameters.getWhetherNoise())) || (strategy != null && strategy.equals(NoiseStrategyEnum.ALARM_ONLY_NO_BLOCKING.getCode())) || (strategy == null);
                        if (startAlarm) {
                            // 无告警事件
                            if (executionParameters.getAlertLevel() != null && StringUtils.isNotBlank(executionParameters.getAlertReceiver())) {
                                if (!alreadyAlertApp.contains(application.getId())) {
                                    List<TaskRuleSimple> taskRuleSimples = notPassTaskRuleSimples.stream().filter(taskRuleSimpleTemp -> taskRuleSimpleTemp.getAlertLevel() != null).collect(Collectors.toList());
                                    handleCheckFailure(alreadyAlertApp, application, task, taskRuleSimples, null, null, null);
                                }
                                if (!alreadyAlertApp.contains(application.getId())) {
                                    List<TaskRuleSimple> taskRuleSimples = failedTaskRuleSimples.stream().filter(taskRuleSimpleTemp -> taskRuleSimpleTemp.getAlertLevel() != null).collect(Collectors.toList());
                                    handleTaskFailure(alreadyAlertApp, application, taskRuleSimples, null, null, null);
                                }
                                continue;
                            }
                            // 有告警事件
                            if (CollectionUtils.isNotEmpty(executionParameters.getAlarmArgumentsExecutionParameters())) {
                                for (AlarmArgumentsExecutionParameters parameters : executionParameters.getAlarmArgumentsExecutionParameters()) {
                                    if (QualitisConstants.CHECK_SUCCESS.toString().equals(parameters.getAlarmEvent().toString())) {
                                        // a. only pass
                                        handleCheckSuccess(application, task, taskRuleSimple, parameters.getAlarmLevel(), parameters.getAlarmReceiver());
                                    } else if (QualitisConstants.CHECK_FAILURE.toString().equals(parameters.getAlarmEvent().toString())) {
                                        // b. task failed
                                        handleTaskFailure(alreadyAlertApp, application, failedTaskRuleSimples, isAcrossCluster ? null : taskRuleSimple, parameters.getAlarmLevel(), parameters.getAlarmReceiver());
                                        // b. not pass + not abort, not pass + abort
                                        handleCheckFailure(alreadyAlertApp, application, task, notPassTaskRuleSimples, isAcrossCluster ? null : taskRuleSimple, parameters.getAlarmLevel(), parameters.getAlarmReceiver());
                                    } else if (QualitisConstants.EXECUTION_COMPLETED.toString().equals(parameters.getAlarmEvent().toString())) {
                                        // c. pass
                                        handleCheckSuccess(application, task, taskRuleSimple, parameters.getAlarmLevel(), parameters.getAlarmReceiver());
                                        // c. not pass + abort
                                        // c. not pass + not abort
                                        handleCheckFailure(alreadyAlertApp, application, task, notPassTaskRuleSimples, isAcrossCluster ? null : taskRuleSimple, parameters.getAlarmLevel(), parameters.getAlarmReceiver());
                                    }
                                }
                            }

                        }
                    }
                } else {
                    if ((null != rule && Boolean.TRUE.equals(rule.getAlert())) || (null != taskRuleSimple.getAlertLevel() && StringUtils.isNotEmpty(taskRuleSimple.getAlertReceiver()))) {
                        if (!alreadyAlertApp.contains(application.getId())) {
                            List<TaskRuleSimple> taskRuleSimples = notPassTaskRuleSimples.stream().filter(taskRuleSimpleTemp -> taskRuleSimpleTemp.getAlertLevel() != null).collect(Collectors.toList());
                            handleCheckFailure(alreadyAlertApp, application, task, taskRuleSimples, null, null, null);
                        }
                        if (!alreadyAlertApp.contains(application.getId())) {
                            List<TaskRuleSimple> taskRuleSimples = failedTaskRuleSimples.stream().filter(taskRuleSimpleTemp -> taskRuleSimpleTemp.getAlertLevel() != null).collect(Collectors.toList());
                            handleTaskFailure(alreadyAlertApp, application, taskRuleSimples, null, null, null);
                        }
                    }
                }
            }
        }
        handleAbnormalDataRecord(tasks);
    }

    /**
     * 异常数据告警收集，含指标且已配置告警的规则
     *
     * @param tasks
     */
    private void handleAbnormalDataRecord(List<Task> tasks) {
        List<AbnormalDataRecordInfo> abnormalDataRecordInfoList = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            Set<TaskRuleSimple> taskRuleSimpleSet = task.getTaskRuleSimples();
            for (TaskRuleSimple taskRuleSimple : taskRuleSimpleSet) {
                if (taskRuleSimple.getAlertLevel() == null) {
                    continue;
                }
                List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();
                List<RuleMetric> ruleMetricList = taskRuleAlarmConfigList.stream().map(TaskRuleAlarmConfig::getRuleMetric)
                        .filter(ruleMetric -> ruleMetric != null).distinct().collect(Collectors.toList());
                if (CollectionUtils.isEmpty(ruleMetricList)) {
                    continue;
                }
                try {
                    constructAbnormalDataRecordInfo(task, taskRuleSimple, ruleMetricList, abnormalDataRecordInfoList);
                } catch (Exception e) {
                    LOGGER.error("Construct abnormal data failed due to " + e.getMessage());
                }
            }
        }
        LOGGER.info("Abnormal data record info: {}", Arrays.toString(abnormalDataRecordInfoList.toArray()));
    }

    /**
     * 处理校验成功 only pass
     *
     * @param application
     * @param task
     * @param taskRuleSimple
     * @param alert
     * @param alertReceiver
     */
    private void handleCheckSuccess(Application application, Task task, TaskRuleSimple taskRuleSimple, Integer alert, String alertReceiver) {
        if (application.getStatus().equals(ApplicationStatusEnum.FINISHED.getCode()) && task.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())) {
            List<TaskRuleSimple> safes = new ArrayList<>();
            safes.add(taskRuleSimple);
            LOGGER.info("Succeed to collect check success simple rule. Simple rules: {}", safes);
            AlarmUtil.sendAlarmMessage(application, safes, imsConfig, alarmClient, alarmInfoDao, userDao, taskResultStatusDao, alert, alertReceiver, true, false);
        }
    }


    /**
     * not pass + not abort(校验不通过+不阻断)
     * not pass + abort(校验失败+阻断)
     *  @param alreadyAlertApp
     * @param application
     * @param task
     * @param notSafes
     * @param currentTaskRuleSimple
     */
    private void handleCheckFailure(List<String> alreadyAlertApp, Application application, Task task, List<TaskRuleSimple> notSafes, TaskRuleSimple currentTaskRuleSimple, Integer alertRank, String alertReceiver) {
        boolean isAbort = false;
        if (Boolean.TRUE.equals(task.getAbortOnFailure())) {
            isAbort = true;
        }
        if ((isAbort && TaskStatusEnum.FAILED.getCode().equals(task.getStatus())) || !application.getNotPassTaskNum().equals(0)) {
            if (null != currentTaskRuleSimple) {
                if (notSafes.contains(currentTaskRuleSimple)) {
                    List<TaskRuleSimple> taskRuleSimples = new ArrayList<>();
                    taskRuleSimples.add(currentTaskRuleSimple);

                    AlarmUtil.sendAlarmMessage(application, taskRuleSimples, imsConfig, alarmClient, alarmInfoDao, userDao, taskResultStatusDao, alertRank, alertReceiver, false, isAbort);
                }
            } else {
                alreadyAlertApp.add(application.getId());
                AlarmUtil.sendAlarmMessage(application, notSafes, imsConfig, alarmClient, alarmInfoDao, userDao, taskResultStatusDao, alertRank, alertReceiver, false, isAbort);
            }

        }
    }

    private boolean ifTaskHasNotCheckRuleAlarmConfig(Collection<TaskRuleSimple> taskRuleSimples) {
        int notCheckNum = taskRuleSimples.stream()
                .map(taskRuleSimple -> taskRuleSimple.getTaskRuleAlarmConfigList())
                .flatMap(taskRuleAlarmConfigList -> taskRuleAlarmConfigList.stream())
                .filter(taskRuleAlarmConfig -> AlarmConfigStatusEnum.NOT_CHECK.getCode().equals(taskRuleAlarmConfig.getStatus()))
                .collect(Collectors.toList()).size();
        LOGGER.info("Task has not check num is : " + notCheckNum);
        if (notCheckNum == 0) {
            return true;
        }
        return false;
    }

    /**
     * task failed(任务失败)
     *
     * @param alreadyAlertApp
     * @param application
     * @param failedTaskRuleSimples
     * @param currentTaskRuleSimple
     * @param alertRank
     * @param alertReceiver
     */
    private void handleTaskFailure(List<String> alreadyAlertApp, Application application, List<TaskRuleSimple> failedTaskRuleSimples, TaskRuleSimple currentTaskRuleSimple, Integer alertRank, String alertReceiver) {
        if (!application.getFailTaskNum().equals(0)) {
            if (null != currentTaskRuleSimple) {
                if (failedTaskRuleSimples.contains(currentTaskRuleSimple)) {
                    List<TaskRuleSimple> taskRuleSimples = new ArrayList<>();
                    taskRuleSimples.add(currentTaskRuleSimple);
                    AlarmUtil.sendFailedMessage(application, taskRuleSimples, imsConfig, alarmClient, alarmInfoDao, userDao, alertRank, alertReceiver);
                }
            } else {
                alreadyAlertApp.add(application.getId());
                AlarmUtil.sendFailedMessage(application, failedTaskRuleSimples, imsConfig, alarmClient, alarmInfoDao, userDao, alertRank, alertReceiver);
            }
        }
    }

    private void constructAbnormalDataRecordInfo(Task task, TaskRuleSimple taskRuleSimple, List<RuleMetric> ruleMetricList, List<AbnormalDataRecordInfo> abnormalDataRecordInfoList) {
        RuleMetric currentRuleMetric = ruleMetricList.iterator().next();
        String departmentName = currentRuleMetric.getDevDepartmentName();
        String subSystemId = currentRuleMetric.getSubSystemId();
        if (null == subSystemId) {
            subSystemId = QualitisConstants.SUB_SYSTEM_ID;
        }

        int execNum = 1;
        boolean collectTask = task.getStatus().equals(TaskStatusEnum.FAILED.getCode()) || task.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode()) || task.getStatus().equals(TaskStatusEnum.CANCELLED.getCode());
        int alarmNum = collectTask && taskRuleSimple.getAlertLevel() < Integer.parseInt(ImsLevelEnum.INFO.getCode()) ? 1 : 0;

        for (TaskDataSource taskDataSource : task.getTaskDataSources()) {
            String dbName = taskDataSource.getDatabaseName();
            String tableName = taskDataSource.getTableName();
            long currentTime = System.currentTimeMillis();

            String nowDate = QualitisConstants.PRINT_DATE_FORMAT.format(new Date(currentTime));
            String datasourceType = TemplateDataSourceTypeEnum.getMessage(taskDataSource.getDatasourceType());
            AbnormalDataRecordInfo abnormalDataRecordInfoExists = abnormalDataRecordInfoDao.findByPrimary(taskRuleSimple.getRuleId(), dbName, tableName, nowDate);
            String standardRuleName = "DQM-" + taskRuleSimple.getProjectName() + "-" + taskRuleSimple.getRuleName() + "-" + taskRuleSimple.getTemplateName();
            String standardRuleDetail = taskRuleSimple.getProjectName() + "-" + taskRuleSimple.getRuleName() + "-" + taskRuleSimple.getTemplateName();
            if (abnormalDataRecordInfoExists == null) {
                AbnormalDataRecordInfo abnormalDataRecordInfo = new AbnormalDataRecordInfo(taskRuleSimple.getRuleId(), standardRuleName, datasourceType
                        , dbName, tableName, departmentName, Integer.valueOf(subSystemId), execNum, alarmNum);
                abnormalDataRecordInfo.setRuleDetail(StringUtils.isEmpty(taskRuleSimple.getRuleDetail()) ? standardRuleDetail : taskRuleSimple.getRuleDetail());
                abnormalDataRecordInfo.setRecordDate(nowDate);
                abnormalDataRecordInfo.setRecordTime(QualitisConstants.PRINT_TIME_FORMAT.format(currentTime));
                abnormalDataRecordInfoList.add(abnormalDataRecordInfoDao.save(abnormalDataRecordInfo));
            } else {
                abnormalDataRecordInfoExists.setRuleName(standardRuleName);
                abnormalDataRecordInfoExists.setRuleDetail(StringUtils.isEmpty(taskRuleSimple.getRuleDetail()) ? standardRuleDetail : taskRuleSimple.getRuleDetail());
                abnormalDataRecordInfoExists.setRecordTime(QualitisConstants.PRINT_TIME_FORMAT.format(currentTime));
                abnormalDataRecordInfoExists.setExecuteNum(abnormalDataRecordInfoExists.getExecuteNum() + execNum);
                abnormalDataRecordInfoExists.setEventNum(abnormalDataRecordInfoExists.getEventNum() + alarmNum);
                abnormalDataRecordInfoExists.setDepartmentName(departmentName);
                abnormalDataRecordInfoExists.setDatasource(datasourceType);
                abnormalDataRecordInfoExists.setSubSystemId(Integer.valueOf(subSystemId));
                abnormalDataRecordInfoList.add(abnormalDataRecordInfoDao.save(abnormalDataRecordInfoExists));
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void abnormalDataRecordAlarm() {
        // Check if upload with another qualitis.
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            LOGGER.info("Start to find upload success record today. Ip:" + inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.error("Failed to get host info and record log.");
        }

        Date nowDate = new Date();
        UploadRecord uploadRecord = uploadRecordDao.findByUnique(new Date(nowDate.getTime()), true);
        if (uploadRecord != null) {
            LOGGER.info("Upload record today successfully.");
            return;
        } else {
            LOGGER.info("Upload record today is still incomplete.");
        }

        LOGGER.info("Start to find yesterday's abnormal data record with exists rules and alarm.");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        List<AbnormalDataRecordInfo> abnormalDataRecordInfoList = abnormalDataRecordInfoDao.findWithExistRulesByRecordDate(QualitisConstants.PRINT_DATE_FORMAT.format(new Date(calendar.getTime().getTime())));
        if (CollectionUtils.isEmpty(abnormalDataRecordInfoList)) {
            LOGGER.info("No abnormal data record to alarm.");
            return;
        }
        UploadRecord currentUploadRecord = new UploadRecord(abnormalDataRecordInfoList.size(), true, new Date(nowDate.getTime())
                , QualitisConstants.PRINT_TIME_FORMAT.format(nowDate), "");
        int stage = abnormalDataRecordInfoList.size() / BATCH_ABNORMAL_DATA_RECORD + 1;
        try {
            for (int index = 0; index < stage; index++) {
                // Report in batch.
                List<AbnormalDataRecordInfo> currentAbnormalDataRecordInfoList;
                if (index * BATCH_ABNORMAL_DATA_RECORD + BATCH_ABNORMAL_DATA_RECORD < abnormalDataRecordInfoList.size()) {
                    currentAbnormalDataRecordInfoList = abnormalDataRecordInfoList.subList(index * BATCH_ABNORMAL_DATA_RECORD, index * BATCH_ABNORMAL_DATA_RECORD + BATCH_ABNORMAL_DATA_RECORD);

                } else {
                    currentAbnormalDataRecordInfoList = abnormalDataRecordInfoList.subList(index * BATCH_ABNORMAL_DATA_RECORD, abnormalDataRecordInfoList.size());
                }

                List<Map<String, Object>> data = new ArrayList<>(currentAbnormalDataRecordInfoList.size());
                for (AbnormalDataRecordInfo abnormalDataRecordInfo : currentAbnormalDataRecordInfoList) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("ruleId", abnormalDataRecordInfo.getRuleId());
                    map.put("ruleName", abnormalDataRecordInfo.getRuleName());
                    map.put("ruleDetail", abnormalDataRecordInfo.getRuleDetail());
                    map.put("dataSource", abnormalDataRecordInfo.getDatasource().toUpperCase());
                    map.put("dbName", abnormalDataRecordInfo.getDbName());
                    map.put("tableName", abnormalDataRecordInfo.getTableName());
                    map.put("dept", abnormalDataRecordInfo.getDepartmentName());
                    map.put("subsystemId", abnormalDataRecordInfo.getSubSystemId());
                    map.put("executeNum", abnormalDataRecordInfo.getExecuteNum());
                    map.put("eventNum", abnormalDataRecordInfo.getEventNum());
                    map.put("reportSource", "Qualitis");
                    data.add(map);
                }
                // Alarm client.
                AlarmUtil.sendAbnormalDataRecordAlarm(imsConfig, alarmClient, data);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send abnormal data record ims. Exception: {}", e.getMessage(), e);
            currentUploadRecord.setStatus(false);
            currentUploadRecord.setErrMsg(e.getMessage());
        }

        uploadRecordDao.save(currentUploadRecord);
    }

    private void checkIfReport(Application application, ImsConfig imsConfig) {
        List<Task> tasks = taskDao.findByApplication(application);
        List<ReportBatchInfo> reportBatchInfos;
        try {
            LOGGER.info("Start to collect task result and to construct report metric data.");
            reportBatchInfos = ReportUtil.collectTaskResult(tasks, taskResultDao, taskDataSourceDao, ruleMetricDao, imsConfig);
            LOGGER.info("Success to collect task result and to construct report metric data.");

            // Construct report content and report.
            for (ReportBatchInfo reportBatchInfo : reportBatchInfos) {
                ReportUtil.reportTaskResult(reportBatchInfo, alarmClient);
            }
        } catch (Exception e) {
            LOGGER.error("Report to IMS failed in batch call!");
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * 查询规则配置是否匹配去噪管理参数
     * 1只告警不阻断、2不告警不阻断
     * 阻断是将未通过校验变成失败
     * <p>
     * 去噪参数匹配---->>
     * 1.run_date；    页面输入时间格式：20221219(例子)
     * 2.模板id符合；
     * 3.校验条件符合；
     *
     * @param rule                规则
     * @param executionParameters 执行参数模板对象
     * @return
     */
    private Integer checkEligible(Rule rule, ExecutionParameters executionParameters, TaskRuleSimple taskRuleSimple) {
        // 获取run_date
        List<TaskResult> taskResultList = taskResultDao.findByApplicationAndRule(taskRuleSimple.getApplicationId(), rule.getId());
        // 时间戳格式 1661616000000
        Set<Long> collect = taskResultList.stream().map(item -> item.getRunDate() != null ? item.getRunDate() : null).collect(Collectors.toSet());

        LOGGER.info("Execution Variable run_date : {}", CollectionUtils.isNotEmpty(collect) ? collect.iterator().next() : null);

        if (executionParameters.getWhetherNoise() != null && Boolean.TRUE.equals(executionParameters.getWhetherNoise()) && CollectionUtils.isNotEmpty(executionParameters.getNoiseEliminationManagement())) {

            for (NoiseEliminationManagement noiseEliminationManagement : executionParameters.getNoiseEliminationManagement()) {

                if (Boolean.FALSE.equals(noiseEliminationManagement.getAvailable())) {
                    continue;
                }

                Set<Long> dateCollectResponses = Sets.newHashSet();
                if (StringUtils.isNotBlank(noiseEliminationManagement.getBusinessDate())) {
                    String[] businessDate = noiseEliminationManagement.getBusinessDate().split(",");
                    for (String date : businessDate) {
                        dateCollectResponses.add(Long.parseLong(date));
                    }
                }
                // 业务时间范围 1661616000000
                boolean flag = dateCollectResponses.stream().anyMatch(new HashSet<>(collect)::contains);

                LOGGER.info("Noise removal parameters template ID : {}", noiseEliminationManagement.getTemplateId() != null ? noiseEliminationManagement.getTemplateId() : null);
                // 模板ID
                boolean templateIdOrNo = noiseEliminationManagement.getTemplateId().toString().equals(rule.getTemplate().getId().toString());

                List<CheckConditionsResponse> collectList = CustomObjectMapper.transJsonToObjects(noiseEliminationManagement.getNoiseNormRatio(), CheckConditionsResponse.class);
                // 因CompareType可能为空，所以这里默认给一个CheckTemplateEnum 不存在的值0
                if (CollectionUtils.isNotEmpty(collectList)) {
                    for (CheckConditionsResponse item : collectList) {
                        item.setOutputMetaName(null);
                        item.setCompareType(item.getCompareType() != null ? item.getCompareType() : 0);
                    }

                }
                List<CheckConditionsResponse> noiseLists = rule.getAlarmConfigs().stream().map(temp -> {
                    CheckConditionsResponse gather = new CheckConditionsResponse();
                    gather.setOutputMetaId(temp.getTemplateOutputMeta().getId());
                    gather.setCheckTemplate(temp.getCheckTemplate());
                    gather.setCompareType(temp.getCompareType() != null ? temp.getCompareType() : 0);
                    gather.setThreshold(temp.getThreshold());
                    return gather;
                }).collect(Collectors.toList());

                // 校验条件
                boolean condition = false;
                for (CheckConditionsResponse check : collectList) {

                    CheckConditionsResponse response = noiseLists.stream().filter(temp ->
                            check.getOutputMetaId().toString().equals(temp.getOutputMetaId().toString()) &&
                                    check.getCheckTemplate().toString().equals(temp.getCheckTemplate().toString()) &&
                                    check.getCompareType().toString().equals(temp.getCompareType().toString()) &&
                                    check.getThreshold().toString().equals(temp.getThreshold().toString())
                    ).findAny().orElse(null);
                    if (response != null) {
                        condition = true;
                    }

                }

                LOGGER.info("Judge whether the noise removal parameters are met,verification conditions : {}, business time: {}, template id: {}, Enable or not: {}",
                        condition, flag, templateIdOrNo, noiseEliminationManagement.getAvailable());
                if (Boolean.TRUE.equals(condition) && Boolean.TRUE.equals(flag) && Boolean.TRUE.equals(templateIdOrNo) && Boolean.TRUE.equals(noiseEliminationManagement.getAvailable())) {
                    //满足去噪参数 将结果表的噪声属性设置为true，命中去噪范围表示是一个噪声值，不参与正常的趋势变化校验
                    if (CollectionUtils.isNotEmpty(taskResultList)) {
                        //遍历 set 去噪属性为 true
                        taskResultList.stream().forEach(e -> e.setDenoisingValue(true));
                        for (TaskResult taskResult : taskResultList) {
                            taskResultDao.saveTaskResult(taskResult);
                        }
                    }
                    LOGGER.info("Noise reduction strategy : {}", noiseEliminationManagement.getEliminateStrategy() != null ? NoiseStrategyEnum.getNoiseStrategyMessage(noiseEliminationManagement.getEliminateStrategy()) : null);
                    return noiseEliminationManagement.getEliminateStrategy();
                }

            }

        }
        return null;
    }

    private Boolean checkWhetherBlocked(Task task) {
        Set<TaskRuleSimple> taskRuleSimpleCollect = task.getTaskRuleSimples();
        for (TaskRuleSimple taskRuleSimple : taskRuleSimpleCollect) {
            Rule rule = ruleDao.findById(taskRuleSimple.getRuleId());
            if (null == rule) {
                return false;
            }
            if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                ExecutionParameters executionParameters = executionParametersDao
                        .findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                if (executionParameters != null) {
                    Integer strategy = checkEligible(rule, executionParameters, taskRuleSimple);
                    if (strategy != null) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    private void printImsLog(Application applicationInDb, ApplicationStatusEnum applicationStatusEnum){
//        try {
//            if(!intellectCheckProjectName.equals(applicationInDb.getProjectName())){
//                return;
//            }
//            java.time.LocalDateTime submitTime = java.time.LocalDateTime.parse(applicationInDb.getSubmitTime(), FORMATTER);
//            java.time.LocalDateTime finishTime = java.time.LocalDateTime.parse(applicationInDb.getFinishTime(), FORMATTER);
//            long costTime = ChronoUnit.SECONDS.between(submitTime, finishTime);
//            LOGGER.info(String.format(IMS_LOG, applicationStatusEnum.getCode(), costTime, applicationStatusEnum.getMessage()));
//        } catch (Exception e) {
//            LOGGER.error("ims_omnis_prophet collect log printing failure");
//            LOGGER.error(e.getMessage(), e);
//        }
    }

}
