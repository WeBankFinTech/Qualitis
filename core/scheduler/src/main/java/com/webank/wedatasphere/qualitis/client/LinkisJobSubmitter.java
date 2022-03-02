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
import com.webank.wedatasphere.qualitis.bean.JobKillResult;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.LinkisResponseKeyEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
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
    private static final int PASS_LEN = 100;
    private static final String END_FLAG = "查询完成, 成功的步骤";

    private static final Pattern LINE_PATTERN = Pattern.compile("\n");
    private static final Pattern NUBBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern JDBC_USER_PASSWORD_PATTERN = Pattern.compile("option\\(\"(user|password)\",\"");

    @Override
    public JobSubmitResult submitJob(String code, String engineName, String user, String remoteAddress, String clusterName, Long taskId,
        String csId, String nodeName, String startupParam) throws JobSubmitException, ClusterInfoNotConfigException {
        String url = getPath(remoteAddress).path(linkisConfig.getSubmitJob()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>(4);
        Map<String, Map> configuration = new HashMap<>(2);

        map.put("requestApplicationName", linkisConfig.getAppName());
        map.put("executeApplicationName", engineName);
        map.put("executionCode", code);
        map.put("runType", "scala");

        Map<String, String> runtime = new HashMap<>(2);
        runtime.put("contextID", csId);
        runtime.put("nodeName", nodeName);
        configuration.put("runtime", runtime);

        Map<String, Object> startup;
        if (StringUtils.isNotBlank(startupParam)) {
            String[] startupParams = startupParam.split(SpecCharEnum.DIVIDER.getValue());
            startup = new HashMap<>(startupParams.length);

            for (String param : startupParams) {
                if (StringUtils.isBlank(param)) {
                    continue;
                }
                String[] params = param.split("=");
                String key = params[0];
                String value = params[1];
                Matcher matcher = NUBBER_PATTERN.matcher(value);

                if(matcher.matches()){
                    startup.put(key, Integer.parseInt(value));
                }else{
                    startup.put(key, value);
                }
            }
            configuration.put("startup", startup);
        }

        Map<String, Map> params = new HashMap<>(1);
        params.put("configuration", configuration);
        map.put("params", params);
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), headers);
        LOGGER.info("Start to submit job to linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = null;
        try {
            response = restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        LOGGER.info("Succeed to submit job to linkis. response: {}", response);

        if (!checkResponse(response)) {
            String message = (String) response.get("message");
            throw new JobSubmitException("Error! Can not submit job, exception: " + message);
        }

        Long jobId = ((Integer) ((Map<String, Object>)response.get("data")).get("taskID")).longValue();
        String execId = (String) ((Map<String, Object>)response.get("data")).get("execID");
        String status = "";
        return new JobSubmitResult(taskId, status, clusterName, remoteAddress, jobId, execId);
    }

    @Override
    public JobSubmitResult submitJobNew(String code, String engineName, String user, String remoteAddress, String clusterName, Long taskId,
        String csId, String nodeName, String startupParam, boolean engineReUse) throws JobSubmitException, ClusterInfoNotConfigException {
        String url = getPath(remoteAddress).path(linkisConfig.getSubmitJobNew()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>(4);

        Map<String, String> executionContent = new HashMap<>(2);
        executionContent.put("code", code);
        executionContent.put("runType", "scala");
        map.put("executionContent", executionContent);

        map.put("executeApplicationName", engineName);
        map.put("executeUser", user);

        Map<String, Map> params = new HashMap<>(2);
        Map<String, Map> configuration = new HashMap<>(2);
        Map<String, String> runtime = new HashMap<>(2);
        runtime.put("contextID", csId);
        runtime.put("nodeName", nodeName);
        configuration.put("runtime", runtime);
        Map<String, Object> startup;
        Map<String, String> labels = new HashMap<>(4);
        if (! engineReUse) {
            labels.put("executeOnce", "");
        }
        if (StringUtils.isNotBlank(startupParam)) {
            String[] startupParams = startupParam.split(SpecCharEnum.DIVIDER.getValue());
            startup = new HashMap<>(startupParams.length);

            for (String param : startupParams) {
                if (StringUtils.isBlank(param)) {
                    continue;
                }
                String[] paramStrs = param.split("=");
                if (paramStrs.length < 2) {
                    continue;
                }
                String key = paramStrs[0];
                String value = paramStrs[1];
                Matcher matcher = NUBBER_PATTERN.matcher(value);

                if (matcher.matches()) {
                    startup.put(key, Integer.parseInt(value));
                } else {
                    startup.put(key, value);
                }

            }
            configuration.put("startup", startup);
        }
        params.put("configuration", configuration);
        map.put("params", params);

        labels.put("engineType", linkisConfig.getEngineName() + "-" + linkisConfig.getEngineVersion());
        labels.put("userCreator", user + "-" + linkisConfig.getAppName());
        labels.put("codeLanguageType", "scala");
        map.put("labels", labels);

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), headers);
        LOGGER.info("Start to submit job to linkis 1.0. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = null;
        try {
            response = restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new JobSubmitException("{&FAILED_TO_SUBMIT_TO_LINKIS}. Exception: " + e.getMessage());
        }
        LOGGER.info("Succeed to submit job to linkis 1.0. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            throw new JobSubmitException("{&FAILED_TO_SUBMIT_TO_LINKIS}. Exception: " + message);
        }

        Long jobId = ((Integer) ((Map<String, Object>)response.get("data")).get("taskID")).longValue();
        String execId = (String) ((Map<String, Object>)response.get("data")).get("execID");
        String status = "";
        return new JobSubmitResult(taskId, status, clusterName, remoteAddress, jobId, execId);
    }

    @Override
    public JobKillResult killJob(String user, String clusterName, Task task) throws ClusterInfoNotConfigException, JobKillException {
        String url = getPath(task.getSubmitAddress()).path(linkisConfig.getKillJob()).path(task.getTaskExecId()).path("kill")
            .queryParam("taskID", task.getTaskRemoteId()).toString();
        LOGGER.info("Finish to construct kill job url. Url: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to kill job to linkis. url: {}, method: {}", url, javax.ws.rs.HttpMethod.GET);
        Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Succeed to kill job to linkis. response: {}", response);

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            throw new JobKillException("{&FAILED_TO_KILL_TO_LINKIS}. Exception: " + message);
        }

        String message = (String) response.get("message");
        return new JobKillResult(message);
    }

    @Override
    public String getTaskStatus(Long taskId, String user, String remoteAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        Map response = getTaskDetail(taskId, user, remoteAddress, clusterName);

        return (String) ((Map)((Map)response.get(LinkisResponseKeyEnum.DATA.getKey())).get(LinkisResponseKeyEnum.TASK.getKey())).get(LinkisResponseKeyEnum.STATUS.getKey());
    }

    @Override
    public Map<String, Object> getTaskStatusAndProgressAndErrCode(Long taskId, String user, String remoteAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        Map response = getTaskDetail(taskId, user, remoteAddress, clusterName);

        String status = (String) ((Map)((Map)response.get(LinkisResponseKeyEnum.DATA.getKey())).get(LinkisResponseKeyEnum.TASK.getKey())).get(LinkisResponseKeyEnum.STATUS.getKey());
        Double progress = 0.0;
        Integer errCode = (Integer) ((Map)((Map)response.get(LinkisResponseKeyEnum.DATA.getKey())).get(LinkisResponseKeyEnum.TASK.getKey())).get(LinkisResponseKeyEnum.ERROR_CODE.getKey());
        if (((Map)((Map)response.get(LinkisResponseKeyEnum.DATA.getKey())).get(LinkisResponseKeyEnum.TASK.getKey())).get(LinkisResponseKeyEnum.PROGRESS.getKey()) != null) {
            progress = Double.parseDouble(((Map)((Map)response.get(LinkisResponseKeyEnum.DATA.getKey())).get(LinkisResponseKeyEnum.TASK.getKey())).get(LinkisResponseKeyEnum.PROGRESS.getKey()).toString());
        }
        Map<String, Object> taskInfos = new HashMap<>(2);
        taskInfos.put("status", status);
        taskInfos.put("progress", progress);
        taskInfos.put("errCode", errCode);
        return taskInfos;
    }

    @Override
    public LogResult getJobPartialLog(Long taskId, Integer begin, String user, String remoteAddress, String clusterName) throws LogPartialException, ClusterInfoNotConfigException {
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

            if (! checkResponse(response)) {
                throw new LogPartialException("Failed to get partial logs, task ID: " + taskId);
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

            if (! checkResponse(response)) {
                throw new LogPartialException("Failed to get partial logs, task ID: " + taskId);
            }

            log = (String) ((List)((Map)response.get("data")).get("log")).get(3);
        }

        // 将账号敏感信息脱敏替换成 ******
        log = maskAccountInfo(log);
        Integer end = getEnd(log) + begin1;
        return new LogResult(log, begin1, end, getLast(log));
    }

    private String getToken(String clusterName) throws ClusterInfoNotConfigException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new ClusterInfoNotConfigException("Failed to find cluster_info name [" + clusterName + "]");
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
            LOGGER.info("ujes task log is null or mask key:{} is null. Finish mask.", linkisConfig.getLogMaskKey());
            return log;
        }
        String[] maskKeys = linkisConfig.getLogMaskKey().split(",");
        for (String key : maskKeys) {
            // 将配置文件yml中key对应的value，替换成******
            String envValue = env.getProperty(key.trim());
            if (StringUtils.isBlank(envValue)) {
                continue;
            }
            log = log.replace(envValue, "******");
        }
        String realPassword = "";
        int indexPassword = log.indexOf("prop.setProperty(\"password\"");

        if (indexPassword != -1) {
            int indexSemicolon = log.indexOf(";", indexPassword + "\"password\"".length());
            if (indexSemicolon != -1) {
                realPassword = log.subSequence(indexPassword + "prop.setProperty(\"password\"".length() + 3, indexSemicolon - 2).toString();
            }
        }
        if (realPassword.length() > 0 && realPassword.length() < PASS_LEN) {
            log = log.replace(realPassword, "******");
        }
        // 0.16.0 mask jdbc user name and password.
        Matcher matcher = JDBC_USER_PASSWORD_PATTERN.matcher(log);
        while(matcher.find()) {
            int index = log.indexOf(matcher.group(0));
            int firstIndex = index + matcher.group(0).length();
            int lastIndex = log.indexOf("\"", firstIndex);
            log = log.replace(log.substring(firstIndex, lastIndex), "******");
        }
        LOGGER.info("Succeed to mask ujes log. mask key:{}", linkisConfig.getLogMaskKey());
        return log;
    }

    private Map getTaskDetail(Long taskId, String user, String ujesAddress, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        String url = getPath(ujesAddress).path(linkisConfig.getStatus()).toString();
        url = url.replace("{id}", String.valueOf(taskId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", getToken(clusterName));
        HttpEntity entity = new HttpEntity<>(headers);

        LOGGER.info("Start to get job detail from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Succeed to get job detail from linkis. response: {}", response);

        if (! checkResponse(response)) {
            throw new TaskNotExistException("Can not get detail of task, task ID: " + taskId);
        }

        Object taskObj = ((Map)response.get("data")).get("task");
        if (taskObj == null) {
            throw new TaskNotExistException("Job ID: " + taskId + " {&DOES_NOT_EXIST}");
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
