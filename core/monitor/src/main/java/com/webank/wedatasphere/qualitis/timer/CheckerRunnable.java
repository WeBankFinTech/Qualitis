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
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.zk.ZookeeperCuratorManager;

import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.bean.JobChecker;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.zk.ZookeeperCuratorManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author howeye
 */
public class CheckerRunnable implements Runnable {

    private ApplicationDao applicationDao;
    private TaskDao taskDao;
    private IChecker iChecker;

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

    private InterProcessLock lock;
    private String lockPath;
    private boolean lockFlag = false;

    public CheckerRunnable(ApplicationDao applicationDao, TaskDao taskDao, ZookeeperCuratorManager zookeeperCuratorManager,
                           String monitorLockPath, IChecker iChecker) {
        this.applicationDao = applicationDao;
        this.taskDao = taskDao;
        this.lockFlag = false;
        this.iChecker = iChecker;

        LOGGER.info("Start to create zookeeper client");
        CuratorFramework client = zookeeperCuratorManager.createClient();
        lockPath = monitorLockPath;
        lock = new InterProcessMutex(client, lockPath);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Start to monitor application");
            acquireLock();

            // Get task that is not finished
            List<JobChecker> jobs = null;
            try {
                jobs = getJobs();
                LOGGER.info("Succeed to find applications that are not end. Application: {}", jobs);
            } catch (Exception t) {
                LOGGER.error("Failed to find applications that are not end", t);
                return;
            }

            for (JobChecker jobChecker : jobs) {
                try {
                    iChecker.checkTaskStatus(jobChecker);
                } catch (Exception t) {
                    LOGGER.error("Failed to check task status, application_id: {}, task_id: {}", jobChecker.getApplicationId(), jobChecker.getTaskId(), t);
                }
            }

            LOGGER.info("Finish to monitor application");
        } catch (Exception e) {
            LOGGER.error("Failed to monitor application, caused by: {}", e.getMessage(), e);
        } finally {
            if (lockFlag) {
                lockFlag = false;
                try {
                    lock.release();
                } catch (Exception e) {
                    LOGGER.error("Failed to release lock of zookeeper", e);
                }
            }
        }
    }

    private void acquireLock() {
        // Trying to acquire lock
        try {
            LOGGER.info("Trying to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
            lock.acquire();
            lockFlag = true;
            LOGGER.info("Succeed to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
        } catch (Exception e) {
            LOGGER.error("Failed to get lock of zk, lock_path: {}, caused by: {}", lockPath, e.getMessage(), e);
        }
    }

    private static final List<Integer> END_APPLICATION_STATUS_LIST = Arrays.asList(ApplicationStatusEnum.FINISHED.getCode(),
                                                                                        ApplicationStatusEnum.FAILED.getCode(),
                                                                                        ApplicationStatusEnum.NOT_PASS.getCode(),
                                                                                        ApplicationStatusEnum.ARGUMENT_NOT_CORRECT.getCode(),
                                                                                        ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode());
    private static final List<Integer> NOT_END_TASK_STATUS_LIST = Arrays.asList(TaskStatusEnum.SUBMITTED.getCode(), TaskStatusEnum.INITED.getCode(), TaskStatusEnum.RUNNING.getCode(), TaskStatusEnum.SCHEDULED.getCode());

    private List<JobChecker> getJobs() {
        List<Application> notEndApplications = applicationDao.findByStatusNotIn(END_APPLICATION_STATUS_LIST);

        List<JobChecker> jobCheckers = new ArrayList<>();
        for (Application app : notEndApplications) {
            // Find not end task
            List<Task> notEndTasks = taskDao.findByApplicationAndStatusInAndTaskRemoteIdNotNull(app, NOT_END_TASK_STATUS_LIST);
            for (Task task : notEndTasks) {
                JobChecker tmp = new JobChecker(app.getId(), TaskStatusEnum.getTaskStateByCode(task.getStatus()), app.getExecuteUser(), task.getSubmitAddress(), task.getClusterId(), task);
                jobCheckers.add(tmp);
            }

            if (notEndTasks.isEmpty()) {
                LOGGER.info("Find abnormal application, which tasks is all end, but application is not end");
                LOGGER.info("Start to recover application status");
                List<Task> allTasks = taskDao.findByApplication(app);
                app.resetTask();
                for (Task task : allTasks) {
                    if (task.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                        iChecker.checkIfLastJob(app.getId(), false, false, false);
                    } else if (task.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())) {
                        iChecker.checkIfLastJob(app.getId(), true, false, false);
                    } else if (task.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())) {
                        iChecker.checkIfLastJob(app.getId(), true, true, false);
                    } else if (task.getStatus().equals(TaskStatusEnum.TASK_NOT_EXIST.getCode())) {
                        iChecker.checkIfLastJob(app.getId(), false, false, true);
                    } else if (task.getStatus().equals(TaskStatusEnum.CANCELLED.getCode())) {
                        iChecker.checkIfLastJob(app.getId(), false, false, false);
                    }
                }
                LOGGER.info("Succeed to recover application status");
            }
        }

        return jobCheckers;
    }
}
