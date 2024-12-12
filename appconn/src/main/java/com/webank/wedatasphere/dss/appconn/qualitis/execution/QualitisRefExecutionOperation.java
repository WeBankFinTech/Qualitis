/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.execution;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.constant.QualitisTaskStatusEnum;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Procedure;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * @author allenzhou@webank.com
 * @date 2021/7/12 15:23
 */
public class QualitisRefExecutionOperation extends LongTermRefExecutionOperation implements Killable, Procedure {
    private static final String SUBMIT_TASK_PATH = "/qualitis/outer/api/v1/execution";
    private static final String GET_TASK_STATUS_PATH = "/qualitis/outer/api/v1/application/{applicationId}/status/";
    private static final String GET_TASK_RESULT_PATH = "/qualitis/outer/api/v1/application/{applicationId}/result/";
    private static final String KILL_TASK_PATH = "/qualitis/outer/api/v1/execution/application/kill/{applicationId}/{executionUser}";

    private static Logger LOGGER = LoggerFactory.getLogger(QualitisRefExecutionOperation.class);

    private String appId = "linkis_id";
    private String appToken = "***REMOVED***";

    private DevelopmentService developmentService;

    @Override
    public boolean kill(RefExecutionAction action) {
        String applicationId = ((QualitisRefExecutionAction) action).getApplicationId();
        String executtionUser = ((QualitisRefExecutionAction) action).getExecutionUser();
        if (applicationId == null) {
            LOGGER.error("Cannot get application id from QualitisNodeExecutionAction. Kill qualitis job failed.");
            return false;
        }
        try {
            // Send request and get response
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity entity = new HttpEntity(headers);

            String path = KILL_TASK_PATH.replace("{applicationId}", applicationId).replace("{executionUser}", executtionUser);
            URI url = HttpUtils.buildUrI(getBaseUrl(), path, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
            String startLog = String.format("Start to kill job. url: %s, method: %s, body: %s", url, HttpMethod.GET, entity);
            LOGGER.info(startLog);
            Map<String, Object> response = restTemplate.getForEntity(url.toString(), Map.class).getBody();

            if (response == null) {
                String errorMsg = String.format("Error! Can not get kill result, job_id: %s, response is null", applicationId);
                LOGGER.error(errorMsg);
                return false;
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMsg = String.format("Error! Can not get kill result, exception: {}", message);
                LOGGER.error(errorMsg);
                return false;
            }

            String finishLog = String.format("Succeed to get kill result. response: %s", response);
            LOGGER.info(finishLog);
            return true;
        } catch (Exception e) {
            String errorMsg = String.format("Error! Can not kill job result, job_id: %s", applicationId);
            LOGGER.error(errorMsg, e);
            return false;
        }

    }

    @Override
    protected RefExecutionAction submit(ExecutionRequestRef requestRef) {
        try {
            Map jobContent = requestRef.getJobContent();
            LOGGER.info("Qualitis rule group content: " + new Gson().toJson(requestRef.getJobContent()));
            String executionUser = String.valueOf(((AsyncExecutionRequestRef) requestRef).getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString());
            String nodeName = "";
            nodeName = String.valueOf(((AsyncExecutionRequestRef) requestRef).getExecutionRequestRefContext().getRuntimeMap().get("nodeName"));
            if ("null".equals(nodeName) || StringUtils.isEmpty(nodeName)) {
                nodeName = requestRef.getName();
            }
            LOGGER.info("The node name: " + nodeName);
            String id = "";
            if (jobContent.get("ruleGroupId") != null) {
                id = jobContent.get("ruleGroupId").toString();
            } else {
                id = jobContent.get("rule_group_id").toString();
            }
            float f = Float.valueOf(id);

            Long groupId = (long)f;

            if (nodeName == null) {
                String errorMsg = "Error! Can not submit job, node name is null";
                LOGGER.error(errorMsg);
                return null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Gson gson = new Gson();

            Map<String, Object> requestPayLoad = new HashMap<>();
            requestPayLoad.put("execution_user", executionUser);
            requestPayLoad.put("create_user", executionUser);
            requestPayLoad.put("node_name", nodeName);
            requestPayLoad.put("group_id", groupId);

            HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

            RestTemplate restTemplate = new RestTemplate();
            String url = HttpUtils.buildUrI(getBaseUrl(), SUBMIT_TASK_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
            LOGGER.info("Start to submit job to linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            String finishLog = String.format("Succeed to submit job to qualitis. response: %s", response);
            LOGGER.info(finishLog);

            if (response == null) {
                String errorMsg = "Error! Can not submit job, response is null";
                LOGGER.error(errorMsg);
                return null;
            }

            if (! checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
                LOGGER.error(errorMsg);
                return null;
            }

            String applicationId = (String) ((Map<String, Object>) response.get("data")).get("application_id");
            LOGGER.info("Qualitis application ID: {}", applicationId);
            return new QualitisRefExecutionAction(applicationId, executionUser);
        } catch (Exception e) {
            String errorMsg = "Error! Can not submit job";
            LOGGER.error(errorMsg, e);
            return null;
        }
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }

    @Override
    public RefExecutionState state(RefExecutionAction action) {
        if (null == action) {
            return RefExecutionState.Failed;
        }

        QualitisRefExecutionAction qualitisRefExecutionAction = (QualitisRefExecutionAction) action;
        String applicationId = qualitisRefExecutionAction.getApplicationId();
        String executionUser = qualitisRefExecutionAction.getExecutionUser();
        LOGGER.info("Qualitis application ID: {}", applicationId);
        LOGGER.info("Qualitis execution user: {}", executionUser);
        if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(executionUser)) {
            return RefExecutionState.Failed;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), GET_TASK_STATUS_PATH.replace("{applicationId}", applicationId), appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Qualitis no signature algor.", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
        }
        LOGGER.info("Start to check job. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.getForEntity(url, Map.class).getBody();
        String finishLog = String.format("Succeed to submit job to qualitis. response: %s", response);
        LOGGER.info(finishLog);

        if (response == null) {
            String errorMsg = "Error! Can not check job, response is null";
            LOGGER.error(errorMsg);
            return null;
        }

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
            LOGGER.error(errorMsg);
            return null;
        }

        LOGGER.info("Succeed to get job status. response: {}", response);
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("task");
        Map<RefExecutionState, Integer> statusCountMap = new HashMap<RefExecutionState, Integer>(8);
        initCountMap(statusCountMap);
        Integer taskSize = tasks.size();
        for (Map<String, Object> task : tasks) {
            Integer taskStatus = (Integer) task.get("task_status");
            Boolean abortOnFailure = (Boolean) task.get("abort_on_failure");
            addStatus(taskStatus, abortOnFailure, statusCountMap);
        }

        Integer runningCount = statusCountMap.get(RefExecutionState.Running);
        Integer successCount = statusCountMap.get(RefExecutionState.Success);
        Integer failedCount = statusCountMap.get(RefExecutionState.Failed);

        if (runningCount != 0) {
            return RefExecutionState.Running;
        } else if (successCount.equals(taskSize)) {
            return RefExecutionState.Success;
        } else if (failedCount != 0) {
            return RefExecutionState.Failed;
        } else {
            return RefExecutionState.Accepted;
        }
    }

    private void addStatus(Integer status, Boolean abortOnFailure, Map<RefExecutionState, Integer> statusCountMap) {
        if (status.equals(QualitisTaskStatusEnum.SUBMITTED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.INITED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.RUNNING.getCode())) {
            statusCountMap.put(RefExecutionState.Running, statusCountMap.get(RefExecutionState.Running) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SUCCEED.getCode())) {
            statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.PASS_CHECKOUT.getCode())) {
            statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.FAIL_CHECKOUT.getCode())) {
            if (abortOnFailure != null && abortOnFailure) {
                statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
            } else {
                statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
            }
        } else if (status.equals(QualitisTaskStatusEnum.FAILED.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TASK_NOT_EXIST.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.CANCELLED.getCode())) {
            statusCountMap.put(RefExecutionState.Killed, statusCountMap.get(RefExecutionState.Killed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TIMEOUT.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SCHEDULED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        }

    }

    private void initCountMap(Map<RefExecutionState, Integer> statusCountMap) {
        statusCountMap.put(RefExecutionState.Accepted, 0);
        statusCountMap.put(RefExecutionState.Running, 0);
        statusCountMap.put(RefExecutionState.Success, 0);
        statusCountMap.put(RefExecutionState.Failed, 0);
    }

    @Override
    public CompletedExecutionResponseRef result(RefExecutionAction action) {
        if (null == action) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), "Application ID is null.");
        }

        QualitisRefExecutionAction qualitisRefExecutionAction = (QualitisRefExecutionAction) action;
        String applicationId = qualitisRefExecutionAction.getApplicationId();

        if (StringUtils.isEmpty(applicationId)) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), "Application ID is null.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), GET_TASK_RESULT_PATH.replace("{applicationId}", applicationId), appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Qualitis no signature algor.", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
        }
        LOGGER.info("Start to get job result. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.getForEntity(url, Map.class).getBody();
        String finishLog = String.format("Succeed to submit job to qualitis. response: %s", response);
        LOGGER.info(finishLog);

        if (response == null) {
            String errorMsg = "Error! Can not check job, response is null";
            LOGGER.error(errorMsg);
            return null;
        }

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
            LOGGER.error(errorMsg);
            return null;
        }

        LOGGER.info("Succeed to get job result. response: {}", response);
        Integer passNum = (Integer) ((Map<String, Object>) response.get("data")).get("pass_num");
        Integer failedNum = (Integer) ((Map<String, Object>) response.get("data")).get("failed_num");
        Integer notPassNum = (Integer) ((Map<String, Object>) response.get("data")).get("not_pass_num");
        String resultMessage = (String) ((Map<String, Object>) response.get("data")).get("result_message");

        String taskMsg = String.format("Task result: Pass/Failed/Not Pass ------- %s/%s/%s", passNum, failedNum, notPassNum);
        LOGGER.info(taskMsg);
        LOGGER.info(resultMessage);

        if (failedNum != 0) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), taskMsg + resultMessage);
        } else {
            return new QualitisCompletedExecutionResponseRef(200, taskMsg + resultMessage);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    @Override
    public float progress(RefExecutionAction action) {
        return 0.5f;
    }

    @Override
    public String log(RefExecutionAction action) {
        return "";
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
