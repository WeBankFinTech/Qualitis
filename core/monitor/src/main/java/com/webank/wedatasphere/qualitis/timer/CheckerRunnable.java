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
import com.webank.wedatasphere.qualitis.dao.ApplicationCommentDao;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class CheckerRunnable implements Runnable {
    private String ip;
    private TaskDao taskDao;
    private int updateJobSize;
    private IChecker iChecker;
    private ApplicationDao applicationDao;
    private static final ThreadPoolExecutor POOL;

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

    public CheckerRunnable(ApplicationDao applicationDao, TaskDao taskDao, IChecker iChecker, int updateSize, String ip) {
        this.applicationDao = applicationDao;
        this.ip = ip;
        this.taskDao = taskDao;
        this.iChecker = iChecker;
        this.updateJobSize = updateSize;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Start to monitor application.");

            // Get task that is not finished
            List<JobChecker> jobs;
            try {
                jobs = getJobs();
                LOGGER.info("Succeed to find applications that are not end. Application: {}", jobs);
            } catch (Exception e) {
                LOGGER.error("Failed to find applications that are not end.");
                LOGGER.error(e.getMessage(), e);
                return;
            }
            int total = jobs.size();
            int updateThreadSize = total / updateJobSize + 1;
            CountDownLatch latch = new CountDownLatch(updateThreadSize);

            for (int indexThread = 0; total > 0 && indexThread < total; indexThread += updateJobSize) {
                if (indexThread + updateJobSize < total) {
                    POOL.execute(new UpdaterRunnable(iChecker, jobs.subList(indexThread, indexThread + updateJobSize), latch));
                } else {
                    POOL.execute(new UpdaterRunnable(iChecker, jobs.subList(indexThread, total), latch));
                }
                updateThreadSize --;
            }
            if (total > 0 && updateThreadSize == 0) {
                latch.await();
            }
            LOGGER.info("Finish to monitor application.");
        } catch (Exception e) {
            LOGGER.error("Failed to monitor application, caused by: {}", e.getMessage(), e);
        }
    }

    private static final List<Integer> NOT_END_APPLICATION_STATUS_LIST = Arrays.asList(ApplicationStatusEnum.SUBMITTED.getCode(),
                                                                                        ApplicationStatusEnum.RUNNING.getCode(),
                                                                                        ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode());
    private static final List<Integer> NOT_END_TASK_STATUS_LIST = Arrays.asList(TaskStatusEnum.SUBMITTED.getCode(), TaskStatusEnum.INITED.getCode(), TaskStatusEnum.RUNNING.getCode(), TaskStatusEnum.SCHEDULED.getCode());

    private List<JobChecker> getJobs() {
        List<Application> notEndApplications = applicationDao.findByStatusIn(NOT_END_APPLICATION_STATUS_LIST);
        notEndApplications = notEndApplications.stream().filter(application -> ip.equals(application.getIp())).collect(Collectors.toList());
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
                if (CollectionUtils.isEmpty(allTasks)) {
                    LOGGER.info("Find abnormal application, which has no tasks, finish it with failed status.");
                    app.setStatus(ApplicationStatusEnum.FAILED.getCode());
                    applicationDao.saveApplication(app);
                    continue;
                }
                app.resetTask();
                applicationDao.saveApplication(app);
                LOGGER.info("Finish to reset application status num.");

                LOGGER.info("Start to recover application status.");
                try {
                    for (Task task : allTasks) {
                        if (task.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                            iChecker.checkIfLastJob(app, false, false, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.CANCELLED.getCode())) {
                            ApplicationComment applicationComment = SpringContextHolder.getBean(ApplicationCommentDao.class).getByCode(ApplicationCommentEnum.TIMEOUT_KILL.getCode());
                            app.setApplicationComment(applicationComment != null ? applicationComment.getCode() : null);
                            iChecker.checkIfLastJob(app, false, false, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())) {
                            iChecker.checkIfLastJob(app, true, false, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())) {
                            iChecker.checkIfLastJob(app, true, true, false);
                        } else if (task.getStatus().equals(TaskStatusEnum.TASK_NOT_EXIST.getCode())) {
                            iChecker.checkIfLastJob(app, false, false, true);
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
