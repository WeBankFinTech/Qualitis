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

package com.webank.wedatasphere.qualitis.client;

import com.webank.wedatasphere.qualitis.bean.JobKillResult;
import com.webank.wedatasphere.qualitis.bean.JobSubmitResult;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.exception.LogPartialException;
import java.util.Map;

/**
 * The function of submitter is to submit job, get task status and get task log
 * @author howeye
 */
public abstract class AbstractJobSubmitter {

    /**
     * Submit job.
     * @param code
     * @param engineName
     * @param user
     * @param remoteAddress
     * @param clusterName
     * @param taskId
     * @param id
     * @param csId
     * @param nodeName
     * @return
     * @throws JobSubmitException
     * @throws ClusterInfoNotConfigException
     */
    public abstract JobSubmitResult submitJob(String code, String engineName, String user, String remoteAddress, String clusterName, Long taskId,
        String id, String csId, String nodeName) throws JobSubmitException, ClusterInfoNotConfigException;

    /**
     * Submit job to linkis 1.0.
     * @param code
     * @param engineName
     * @param user
     * @param remoteAddress
     * @param clusterName
     * @param taskId
     * @param csId
     * @param nodeName
     * @param startupParam
     * @param engineReUse
     * @return
     * @throws JobSubmitException
     * @throws ClusterInfoNotConfigException
     */
    public abstract JobSubmitResult submitJobNew(String code, String engineName, String user, String remoteAddress, String clusterName, Long taskId,
        String csId, String nodeName, String startupParam, boolean engineReUse) throws JobSubmitException, ClusterInfoNotConfigException;

    /**
     * Kill job
     * @param user
     * @param clusterName
     * @param task
     * @return
     * @throws ClusterInfoNotConfigException
     * @throws JobKillException
     */
    public abstract JobKillResult killJob(String user, String clusterName, Task task) throws ClusterInfoNotConfigException, JobKillException;

    /**
     * Get task status
     * @param taskId
     * @param user
     * @param remoteAddress
     * @param clusterName
     * @return
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    public abstract String getTaskStatus(Long taskId, String user, String remoteAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException;


    /**
     * Get task status and progress.
     * @param taskId
     * @param user
     * @param remoteAddress
     * @param clusterName
     * @return
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    public abstract Map<String, Object> getTaskStatusAndProgressAndErrCode(Long taskId, String user, String remoteAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException;

    /**
     * Get task log from remote
     * @param taskId
     * @param begin
     * @param user
     * @param remoteAddress
     * @param clusterName
     * @return
     * @throws LogPartialException
     * @throws ClusterInfoNotConfigException
     */
    public abstract LogResult getJobPartialLog(Long taskId, Integer begin, String user, String remoteAddress, String clusterName) throws LogPartialException, ClusterInfoNotConfigException;

}
