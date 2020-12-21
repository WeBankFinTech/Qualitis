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
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.util.PassUtil;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.bean.JobChecker;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.util.PassUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private ApplicationDao applicationDao;
    @Autowired
    private TaskResultDao taskResultDao;

    private static final String PRINT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter PRINT_TIME_FORMAT = DateTimeFormat.forPattern(PRINT_TIME_PATTERN);
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskChecker.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkTaskStatus(JobChecker jobChecker) throws ClusterInfoNotConfigException {
        try {
            String jobStatus;
            jobStatus = monitorManager.getTaskStatus(jobChecker.getTaskId(), jobChecker.getUsername(), jobChecker.getUjesAddress(), jobChecker.getClusterName()).toUpperCase();

            if (!jobStatus.equals(jobChecker.getOldStatus())) {
                LOGGER.info("Start to update task status. old status: {}, new status: {}, task_id: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
                writeDb(jobChecker, jobStatus);
                LOGGER.info("Succeed to update task status. old status: {}, new status: {}, task_id: {}", jobChecker.getOldStatus(), jobStatus, jobChecker.getTaskId());
            }
        } catch (TaskNotExistException e) {
            LOGGER.error("Spark Task [{}] does not exist, application id : [{}]", jobChecker.getTaskId(), jobChecker.getApplicationId(), e);
            jobChecker.getTask().setStatus(TaskStatusEnum.TASK_NOT_EXIST.getCode());
            taskDao.save(jobChecker.getTask());
            jobChecker.getTask().getApplication().addAbnormalTaskNum();
            applicationDao.saveApplication(jobChecker.getTask().getApplication());
        }
    }

    @Override
    public void checkIfLastJob(String applicationId, boolean finish, boolean isPass, boolean isNotExist) {
        Application applicationInDb = applicationDao.findById(applicationId);
        if (finish) {
            if (isPass) {
                applicationInDb.addSuccessJobNum();
                LOGGER.info("Application add successful task, application: {}", applicationInDb);
            } else {
                applicationInDb.addNotPassTaskNum();
                LOGGER.info("Application add not pass task, application: {}", applicationInDb);
            }
        } else if (!isNotExist){
            applicationInDb.addFailJobNum();
            LOGGER.info("Application add failed task, application: {}", applicationInDb);
        }

        ifLastTaskAndSaveApplication(applicationInDb);
    }

    private void writeDb(JobChecker jobChecker, String newStatus) {
        Task taskInDb = taskDao.findByRemoteTaskId(jobChecker.getTaskId());
        if (newStatus.equals(TaskStatusEnum.FAILED.getState())) {
            /*
             * 1.Modify end time of job
             * 2.Modify task finish time and failed num if last job
             * */
            taskInDb.setEndTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            modifyJobStatus(taskInDb, newStatus);
            taskDao.save(taskInDb);
            checkIfLastJob(jobChecker.getApplicationId(), false, false, false);
        } else if (newStatus.equals(TaskStatusEnum.SUCCEED.getState())) {
            /*
             * 1.Modify end time of job
             * 2.Modify task finish time and succeed num if last job
             * */
            taskInDb.setEndTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            boolean isPass;
            if (passCheckOut(jobChecker.getApplicationId(), taskInDb)) {
                modifyJobStatus(taskInDb, TaskStatusEnum.PASS_CHECKOUT.getState());
                isPass = true;
            } else {
                modifyJobStatus(taskInDb, TaskStatusEnum.FAIL_CHECKOUT.getState());
                isPass = false;
            }
            taskDao.save(taskInDb);
            checkIfLastJob(jobChecker.getApplicationId(), true, isPass, false);
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
            task.getApplication().setStatus(ApplicationStatusEnum.RUNNING.getCode());
            applicationDao.saveApplication(task.getApplication());
            LOGGER.info("Succeed to set application status to [{}], application: {}", ApplicationStatusEnum.RUNNING.getState(), task.getApplication());
        } else if (newStatus.equals(TaskStatusEnum.SUCCEED.getState())) {
            task.setStatus(TaskStatusEnum.SUCCEED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.FAILED.getState())) {
            task.setStatus(TaskStatusEnum.FAILED.getCode());
        } else if (newStatus.equals(TaskStatusEnum.PASS_CHECKOUT.getState())) {
            task.setStatus(TaskStatusEnum.PASS_CHECKOUT.getCode());
        } else if (newStatus.equals(TaskStatusEnum.FAIL_CHECKOUT.getState())) {
            if (task.isAbortOnFailure() != null && task.isAbortOnFailure()) {
                task.setStatus(TaskStatusEnum.FAILED.getCode());
            } else {
                task.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
            }
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

            if (taskRuleSimple.getChildRuleSimple() != null) {
                if (!checkTaskRuleSimplePass(applicationId, taskRuleSimple.getChildRuleSimple())) {
                    passFlag = false;
                }
            }
        }

        return passFlag;
    }

    private Boolean checkTaskRuleSimplePass(String applicationId, TaskRuleSimple taskRuleSimple) {
        Boolean passFlag = true;
        TaskResult taskResult = taskResultDao.findByApplicationIdAndRuleId(applicationId, taskRuleSimple.getRuleId());
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
            if (PassUtil.notSafe(applicationId, taskRuleSimple.getRuleId(), taskRuleAlarmConfig, taskResult, taskResultDao)) {
                taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
            } else {
                passFlag = false;
                taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
            }
        }
        return passFlag;
    }

    private void ifLastTaskAndSaveApplication(Application applicationInDb) {
        if (isLastJob(applicationInDb)) {
            LOGGER.info("Succeed to execute all task of application. application: {}", applicationInDb);
            applicationInDb.setFinishTime(new DateTime(new Date()).toString(PRINT_TIME_FORMAT));
            if (applicationInDb.getFinishTaskNum().equals(applicationInDb.getTotalTaskNum())) {
                applicationInDb.setStatus(ApplicationStatusEnum.FINISHED.getCode());
            } else if (!applicationInDb.getFailTaskNum().equals(0) || !applicationInDb.getAbnormalTaskNum().equals(0)){
                applicationInDb.setStatus(ApplicationStatusEnum.FAILED.getCode());
            } else {
                applicationInDb.setStatus(ApplicationStatusEnum.NOT_PASS.getCode());
            }
        }
        applicationDao.saveApplication(applicationInDb);
        LOGGER.info("Succeed to save application. application: {}", applicationInDb);
    }

    private boolean isLastJob(Application application) {
        if (application.getTotalTaskNum() == null) {
            return false;
        }
        return application.getFinishTaskNum() + application.getFailTaskNum() + application.getNotPassTaskNum() + application.getAbnormalTaskNum() == application.getTotalTaskNum();
    }
}
