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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.service.JobService;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.LogPartialException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.LogPartialException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.JobService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * @author howeye
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public JobServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<?> getTaskLog(Long taskId, String clusterName) throws UnExpectedRequestException {
        Task task = taskDao.findById(taskId);
        if (task == null) {
            throw new UnExpectedRequestException("{&JOB_ID_DOES_NOT_EXIST}");
        }

        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster info {&DOES_NOT_EXIST}");
        }

        LogResult logResult;
        String proxyUser = task.getTaskProxyUser();
        try {
            logResult = monitorManager.getTaskPartialLog(task.getTaskRemoteId(), 0, StringUtils.isNotBlank(proxyUser) ? proxyUser : task.getApplication().getExecuteUser(), clusterInfo.getLinkisAddress(), clusterName);
        } catch (LogPartialException | ClusterInfoNotConfigException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }

        LOGGER.info("Succeed to get task log, task_id: {}, cluster_id: {}", taskId, clusterName);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_LOG}", logResult.getLog());
    }
}
