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

import com.webank.wedatasphere.qualitis.bean.JobChecker;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationCommentEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.TaskRuleSimpleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.PassUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

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
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private ExecutionManager executionManager;
    @Autowired
    private TaskRuleSimpleDao taskRuleSimpleDao;
    @Autowired
    private UserDao userDao;

    private static final String PRINT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskChecker.class);
    private static final DateTimeFormatter PRINT_TIME_FORMAT = DateTimeFormat.forPattern(PRINT_TIME_PATTERN);

    private static final Map<Integer, Integer> ERR_CODE_TYPE = new HashMap<Integer, Integer>(){{
        put(60075,2);
        put(10001,2);
        put(20001,3);
        put(20002,3);
        put(20003,3);
        put(20083,3);
        put(70059,3);
        put(11011,3);
        put(11012,3);
        put(11013,3);
        put(11014,3);
        put(11015,3);
        put(11016,3);
        put(11017,3);
        put(60035,3);
        put(21304,3);
        put(30001,1);
        put(60010,1);
        put(40001,4);
        put(40002,10);
        put(40004,4);
        put(40003,4);
        put(40005,4);
        put(50001,4);
        put(50002,4);
        put(50003,4);
        put(50004,4);
        put(50005,4);
        put(50007,4);
        put(50012,4);
        put(50013,4);
        put(50014,4);
        put(50017,4);
        put(50019,4);
        put(60003,4);
        put(11017,3);
        put(60035,3);
        put(60075,2);
        put(95002,1);
        put(95003,1);
        put(95004,1);
        put(95006,1);
        put(60079,1);
        put(30002,5);
        put(50007,4);
    }};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkTaskStatus(JobChecker jobChecker) {
        try {
            Map<String, Object> taskInfos = monitorManager.getTaskStatus(jobChecker.getTaskId(), jobChecker.getUsername(),
                jobChecker.getUjesAddress(), jobChecker.getClusterName());
            String jobStatus = ((String) taskInfos.get("status")).toUpperCase();
            Integer errCode = (Integer) taskInfos.get("errCode");
            LOGGER.info("Task status: {}", jobStatus);

            if (! jobStatus.equals(jobChecker.getOldStatus())) {
                LOGGER.info("Start to update task status. old status: {}, new status: {}, task_id: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
                writeDb(jobChecker, jobStatus, errCode);
                LOGGER.info("Succeed to update task status. old status: {}, new status: {}, task_id: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
            }

            // Compute task time in same progress.
            if (linkisConfig.getKillStuckTasks() && TaskStatusEnum.RUNNING.getState().equals(jobStatus)) {
                Task taskInDb = taskDao.findByRemoteTaskIdAndClusterName(jobChecker.getTaskId(), jobChecker.getClusterName());
                Double progress = (Double) taskInfos.get("progress");
                LOGGER.info("Old time progress[{}].", jobChecker.getOldProgress());
                LOGGER.info("Current time progress[{}].", progress);
                long runningTime = System.currentTimeMillis() - taskInDb.getRunningTime();
                LOGGER.info("Current task running time [{}] minutes.", runningTime / (60 * 1000));
                if (progress.equals(jobChecker.getOldProgress())) {
                    long diff = System.currentTimeMillis() - taskInDb.getNewProgressTime();
                    long diffMinutes = diff;
                    LOGGER.info("Time in same progress[{}]: {} minutes. Config max time: {} minutes.", progress, diffMinutes / (60 * 1000)
                        , linkisConfig.getKillStuckTasksTime().longValue() / (60 * 1000));
                    if (diffMinutes > linkisConfig.getKillStuckTasksTime().longValue()) {
                        killTimeoutTask(applicationDao.findById(jobChecker.getApplicationId()),taskInDb, jobChecker);
                    }
                } else {
                    LOGGER.info("Progress is updating , so is task new progress.");
                    taskInDb.setNewProgressTime(System.currentTimeMillis());
                    taskInDb.setProgress(progress);
                    if (runningTime > linkisConfig.getKillStuckTasksTime().longValue()) {
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
        } else if (! isNotExist) {
            applicationInDb.addFailJobNum();
            LOGGER.info("Application add failed task, application: {}", applicationInDb);
        } else if (isNotExist) {
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
            taskInDb.setTaskComment(errCode == null ? ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode() : ERR_CODE_TYPE.get(errCode));
            modifyJobStatus(taskInDb, newStatus);
            taskDao.save(taskInDb);
            applicationInDb.setApplicationComment(errCode == null ? ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode() : ERR_CODE_TYPE.get(errCode));
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
                modifyJobStatus(taskInDb, TaskStatusEnum.PASS_CHECKOUT.getState());
                isPass = true;
                finish = true;
            } else {
                if (taskInDb.getAbortOnFailure() != null && taskInDb.getAbortOnFailure()) {
                    modifyJobStatus(taskInDb, TaskStatusEnum.FAILED.getState());
                    taskInDb.setTaskComment(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode());
                    applicationInDb.setApplicationComment(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode());
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
            applicationInDb.setApplicationComment(ApplicationCommentEnum.TIMEOUT_KILL.getCode());
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
            task.setTaskComment(ApplicationCommentEnum.SAME_ISSUES.getCode());
        } else if (newStatus.equals(TaskStatusEnum.FAIL_CHECKOUT.getState())) {
            task.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
            task.setProgress(Double.parseDouble("1"));
            task.setTaskComment(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode());
        } else if (newStatus.equals(TaskStatusEnum.CANCELLED.getState())) {
            task.setStatus(TaskStatusEnum.CANCELLED.getCode());
            task.setTaskComment(ApplicationCommentEnum.TIMEOUT_KILL.getCode());
        } else if (newStatus.equals(TaskStatusEnum.TIMEOUT.getState())) {
            task.setStatus(TaskStatusEnum.TIMEOUT.getCode());
            task.setTaskComment(ApplicationCommentEnum.TIMEOUT_KILL.getCode());
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
            if (! checkTaskRuleSimplePass(applicationId, taskRuleSimple)) {
                passFlag = false;
            }

            if (taskRuleSimple.getChildRuleSimple() != null) {
                if (! checkTaskRuleSimplePass(applicationId, taskRuleSimple.getChildRuleSimple())) {
                    passFlag = false;
                }
            }
        }

        return passFlag;
    }

    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    private Boolean checkTaskRuleSimplePass(String applicationId, TaskRuleSimple taskRuleSimple) {
        Boolean passFlag = true;
        List<TaskResult> taskResults = taskResultDao.findByApplicationAndRule(applicationId, taskRuleSimple.getRuleId());
        for (TaskResult taskResult : taskResults) {
            List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = taskRuleSimple.getTaskRuleAlarmConfigList();
            Long ruleMetricId = taskResult.getRuleMetricId();
            if (ruleMetricId != null && ruleMetricId.longValue() != -1) {
                taskRuleAlarmConfigList = taskRuleAlarmConfigList.stream().filter(taskRuleAlarmConfig ->
                   taskRuleAlarmConfig.getRuleMetric().getId().equals(ruleMetricId)
                ).collect(Collectors.toList());
            }
            for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleAlarmConfigList) {
                if (PassUtil.notSafe(applicationId, taskRuleSimple.getRuleId(), taskRuleAlarmConfig, taskResult, taskResultDao)) {
                    taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
                } else {
                    passFlag = false;
                    taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());

                    if (taskRuleSimple.getRuleType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
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
        }

        return passFlag;
    }

    private void ifLastTaskAndSaveApplication(Application applicationInDb) {
        if (isLastJob(applicationInDb)) {
            LOGGER.info("Succeed to execute all task of application. Application: {}", applicationInDb);
            applicationInDb.setFinishTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            if (applicationInDb.getFinishTaskNum().equals(applicationInDb.getTotalTaskNum())) {
                applicationInDb.setStatus(ApplicationStatusEnum.FINISHED.getCode());
                applicationInDb.setApplicationComment(ApplicationCommentEnum.SAME_ISSUES.getCode());
            } else if (!applicationInDb.getFailTaskNum().equals(0) || !applicationInDb.getAbnormalTaskNum().equals(0)){
                applicationInDb.setStatus(ApplicationStatusEnum.FAILED.getCode());
            } else {
                applicationInDb.setStatus(ApplicationStatusEnum.NOT_PASS.getCode());
                applicationInDb.setApplicationComment(ApplicationCommentEnum.DIFF_DATA_ISSUES.getCode());
            }
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
}
