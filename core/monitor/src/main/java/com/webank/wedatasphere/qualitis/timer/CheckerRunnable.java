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
import com.webank.wedatasphere.qualitis.constant.ApplicationCommentEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;

import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.ha.AbstractServiceCoordinator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author howeye
 */
public class CheckerRunnable implements Runnable {
    private TaskDao taskDao;
    private int updateJobSize;
    private IChecker iChecker;
    private ApplicationDao applicationDao;
    private static final ThreadPoolExecutor POOL;
    private AbstractServiceCoordinator abstractServiceCoordinator;

    private static final Logger LOGGER = LoggerFactory.getLogger("monitor");

    static {
        POOL = new ThreadPoolExecutor(50,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new UpdaterThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());
    }

    public CheckerRunnable(ApplicationDao applicationDao, TaskDao taskDao, IChecker iChecker, AbstractServiceCoordinator abstractServiceCoordinator, int updateSize) {
        this.applicationDao = applicationDao;
        this.taskDao = taskDao;
        this.iChecker = iChecker;
        this.updateJobSize = updateSize;
        this.abstractServiceCoordinator = abstractServiceCoordinator;

       abstractServiceCoordinator.init();
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Start to monitor application.");
            abstractServiceCoordinator.coordinate();

            // Get task that is not finished
            List<JobChecker> jobs = null;
            try {
                jobs = getJobs();
                LOGGER.info("Succeed to find applications that are not end. Application: {}", jobs);
            } catch (Exception e) {
                LOGGER.error("Failed to find applications that are not end.", e);
                return;
            }
            int total = jobs.size();
            int updateThreadSize = total / updateJobSize + 1;
            CountDownLatch latch = new CountDownLatch(updateThreadSize);

            for (int indexThread = 0; total > 0 && indexThread < total;) {
                if (indexThread + updateJobSize < total) {
                    POOL.execute(new UpdaterRunnable(iChecker, jobs.subList(indexThread, indexThread + updateJobSize), latch));
                } else {
                    POOL.execute(new UpdaterRunnable(iChecker, jobs.subList(indexThread, total), latch));
                }
                indexThread += updateJobSize;
                updateThreadSize --;
            }
            if (total > 0 && updateThreadSize == 0) {
                latch.await();
            }
            LOGGER.info("Finish to monitor application.");
        } catch (Exception e) {
            LOGGER.error("Failed to monitor application, caused by: {}", e.getMessage(), e);
        } finally {
            abstractServiceCoordinator.release();
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
                JobChecker tmp = new JobChecker(app.getId(), TaskStatusEnum.getTaskStateByCode(task.getStatus()), task.getProgress(), StringUtils.isNotBlank(task.getTaskProxyUser()) ? task.getTaskProxyUser() : app.getExecuteUser(), task.getSubmitAddress(), task.getClusterName(), task);
                jobCheckers.add(tmp);
            }

            if (notEndTasks.isEmpty()) {
                LOGGER.info("Find abnormal application, which tasks is all end, but application is not end.");
                List<Task> allTasks = taskDao.findByApplication(app);

                app.resetTask();
                applicationDao.saveApplication(app);
                LOGGER.info("Finish to reset application status num.");

                LOGGER.info("Start to recover application status.");
                try {
                    for (Task task : allTasks) {
                        if (task.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                            iChecker.checkIfLastJob(app, false, false, false);
                        } else if (task.getAbortOnFailure() != null && !task.getAbortOnFailure() && task.getStatus()
                            .equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())) {
                            iChecker.checkIfLastJob(app, true, false, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())) {
                            iChecker.checkIfLastJob(app, true, true, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.TASK_NOT_EXIST.getCode())) {
                            iChecker.checkIfLastJob(app, false, false, true);
                        } else if (task.getStatus().equals(TaskStatusEnum.CANCELLED.getCode())) {
                            app.setApplicationComment(ApplicationCommentEnum.TIMEOUT_KILL.getCode());
                            iChecker.checkIfLastJob(app, false, false, false);
                        }
                    }
                    LOGGER.info("Succeed to recover application status.");
                } catch (Exception e) {
                    LOGGER.error("Failed to recover applications that are not end.");
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        return jobCheckers;
    }
}
