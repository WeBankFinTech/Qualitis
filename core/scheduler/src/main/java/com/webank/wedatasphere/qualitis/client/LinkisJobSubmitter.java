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

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.bean.JobSubmitResult;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.exception.LogPartialException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author howeye
 */
@Component
public class LinkisJobSubmitter extends AbstractJobSubmitter {

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisJobSubmitter.class);

    private static final String END_FLAG = "查询完成, 成功的步骤";
    private static final Pattern LINE_PATTERN = Pattern.compile("\n");

    @Override
    public JobSubmitResult submitJob(String code, String user, String remoteAddress, String clusterName, Long taskId) throws JobSubmitException, ClusterInfoNotConfigException {
        String url = getPath(remoteAddress).path(linkisConfig.getSubmitJob()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));

        Map<String, Object> map = new HashMap<>(4);
        // TODO：recover qualitis
        map.put("requestApplicationName", linkisConfig.getAppName());
        map.put("executeApplicationName", "spark");
        map.put("executionCode", code);
        map.put("runType", "scala");
        Gson gson = new Gson();
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), headers);
        LOGGER.info("Start to submit job to linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        LOGGER.info("Succeed to submit job to linkis. response: {}", response);

        if (!checkResponse(response)) {
            String message = (String) response.get("message");
            throw new JobSubmitException("Error! Can not submit job, exception: " + message);
        }

        Integer jobId = (Integer) ((Map<String, Object>)response.get("data")).get("taskID");
        String status = "";
        return new JobSubmitResult(taskId, status, clusterName, remoteAddress, jobId);
    }

    @Override
    public String getTaskStatus(Integer taskId, String user, String remoteAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        Map response = getTaskDetail(taskId, user, remoteAddress, clusterName);

        return (String) ((Map)((Map)response.get("data")).get("task")).get("status");
    }

    @Override
    public LogResult getJobPartialLog(Integer taskId, Integer begin, String user, String remoteAddress, String clusterName) throws LogPartialException, ClusterInfoNotConfigException {
        Integer begin1 = 0;

        String jobStatus = null;
        String logPath = null;
        String execId = null;
        try {
            Map response = getTaskDetail(taskId, user, remoteAddress, clusterName);
            jobStatus = (String) ((Map)((Map)response.get("data")).get("task")).get("status");
            logPath = (String) ((Map)((Map)response.get("data")).get("task")).get("logPath");
            execId = (String) ((Map)((Map)response.get("data")).get("task")).get("strongerExecId");
        } catch (TaskNotExistException e) {
            throw new LogPartialException(e);
        }

        String log = "";
        if (isTaskRunning(jobStatus)) {
            String url = getPath(remoteAddress).path(linkisConfig.getRunningLog()).toString();
            url = url.replace("{id}", execId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Token-User", user);
            headers.add("Token-Code", getToken(clusterName));
            HttpEntity entity = new HttpEntity<>(headers);

            LOGGER.info("Start to get job log from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
            Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            LOGGER.info("Succeed to get job log from linkis. repsonse: {}", response);

            if (!checkResponse(response)) {
                throw new LogPartialException("Failed to get partial logs, task_id: " + taskId);
            }

            log = (String) ((List)((Map)response.get("data")).get("log")).get(3);
        } else {
            String url = getPath(remoteAddress).path(linkisConfig.getFinishLog()).toString() + "?path=" + logPath;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Token-User", user);
            headers.add("Token-Code", getToken(clusterName));
            HttpEntity entity = new HttpEntity<>(headers);

            LOGGER.info("Start to get job log from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
            Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            LOGGER.info("Succeed to get job log from linkis. repsonse: {}", response);

            if (!checkResponse(response)) {
                throw new LogPartialException("Failed to get partial logs, task_id: " + taskId);
            }

            log = (String) ((List)((Map)response.get("data")).get("log")).get(3);
        }


        //将账号敏感信息脱敏替换成*****
        log = maskAccountInfo(log);
        Integer end = getEnd(log) + begin1;
        return new LogResult(log, begin1, end, getLast(log));
    }

    private String getToken(String clusterName) throws ClusterInfoNotConfigException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new ClusterInfoNotConfigException("Failed to find cluster_info named [" + clusterName + "]");
        }
        return clusterInfo.getLinkisToken();
    }

    private Integer getEnd(String log) {
        int count = 0;
        Matcher m = LINE_PATTERN.matcher(log);
        while (m.find()) {
            count++;
        }
        return count;
    }

    private Boolean getLast(String log) {
        return log.contains(END_FLAG);
    }

    private String maskAccountInfo(String log) {
        if (StringUtils.isBlank(log) || StringUtils.isBlank(linkisConfig.getLogMaskKey())) {
            LOGGER.info("ujes task log is null or mask key:{} is null.finish mask.", linkisConfig.getLogMaskKey());
            return log;
        }
        String[] maskKeys = linkisConfig.getLogMaskKey().split(",");
        for (String key : maskKeys) {
            //将配置文件yml中key对应的value,替换成*****
            String envValue = env.getProperty(key.trim());
            if (StringUtils.isBlank(envValue)) {
                continue;
            }
            log = log.replace(envValue, "*****");
        }
        LOGGER.info("Succeed to mask ujes log. mask key:{}", linkisConfig.getLogMaskKey());
        return log;
    }

    private Map getTaskDetail(Integer taskId, String user, String ujesAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        String url = getPath(ujesAddress).path(linkisConfig.getStatus()).toString();
        url = url.replace("{id}", String.valueOf(taskId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));
        HttpEntity entity = new HttpEntity<>(headers);

        LOGGER.info("Start to get job status from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Succeed to get job status from linkis. response: {}", response);

        if (!checkResponse(response)) {
            throw new TaskNotExistException("Can not get status of task, task_id : " + taskId);
        }

        Object taskObj = ((Map)response.get("data")).get("task");
        if (taskObj == null) {
            throw new TaskNotExistException("Job id: " + taskId + " does not exist");
        }

        return response;
    }

    private boolean checkResponse(Map response) {
        Integer responseStatus = (Integer) response.get("status");
        return responseStatus == 0;
    }

    private boolean isTaskRunning(String status) {
        status = status.toUpperCase();
        return TaskStatusEnum.SUBMITTED.getState().equals(status) || TaskStatusEnum.INITED.getState().equals(status)
                || TaskStatusEnum.RUNNING.getState().equals(status) || TaskStatusEnum.SCHEDULED.getState().equals(status);
    }

    private UriBuilder getPath(String ujesAddress) {
        return UriBuilder.fromUri(ujesAddress).path(linkisConfig.getPrefix());
    }
}
