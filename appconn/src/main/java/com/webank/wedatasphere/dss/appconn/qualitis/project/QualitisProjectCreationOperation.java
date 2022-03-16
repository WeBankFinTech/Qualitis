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

package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisProjectCreationOperation implements ProjectCreationOperation {

    private static Logger logger = LoggerFactory.getLogger(QualitisProjectCreationOperation.class);
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    StructureService structureService;

    private static final String CREATE_PROJECT_PATH = "/qualitis/outer/api/v1/project/workflow";

    private String appId = "linkis_id";
    private String appToken = "***REMOVED***";

    public QualitisProjectCreationOperation(StructureService service, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.structureService = service;
        this.ssoRequestOperation = ssoRequestOperation;
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), CREATE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        } catch (URISyntaxException e) {
            logger.error("Qualitis uri syntax exception.", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();

        Map<String, Object> requestPayLoad = new HashMap<>();

        requestPayLoad.put("description", projectRef.getDescription());
        requestPayLoad.put("project_name", projectRef.getName());
        requestPayLoad.put("username", projectRef.getCreateBy());

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

        Map<String, Object> resMap;
        try {
            RestTemplate restTemplate = new RestTemplate();
            logger.info("Start to create qualitis project. url: {}, method: {}, body: {}", url, HttpMethod.PUT, entity);
            resMap = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, entity, Map.class).getBody();

        } catch (Exception e) {
            logger.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project exception", e);
        }

        if (! checkResponse(resMap)) {
            String message = (String) resMap.get("message");
            String errorMessage = String.format("Error! Can not add project, exception: %s", message);
            logger.error(errorMessage);
            throw new ExternalOperationFailedException(90176, errorMessage, null);
        }

        Integer projectId = (Integer) ((Map<String, Object>) ((Map<String, Object>) resMap.get("data")).get("project_detail")).get("project_id");
        logger.info("Create qualitis project ID: {}", projectId);
        QualitisProjectResponseRef qualitisProjectResponseRef;
        try {
            qualitisProjectResponseRef = new QualitisProjectResponseRef(gson.toJson(resMap), HttpStatus.OK.value());
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Failed to parse response json", e);
        }
        qualitisProjectResponseRef.setAppInstance(structureService.getAppInstance());
        qualitisProjectResponseRef.setProjectRefId(projectId.longValue());
        return qualitisProjectResponseRef;
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService service) {
        this.structureService = service;
    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl();
    }
}
