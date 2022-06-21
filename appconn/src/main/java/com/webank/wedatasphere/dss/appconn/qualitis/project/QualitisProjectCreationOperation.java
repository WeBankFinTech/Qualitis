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
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.AuthorizeUtil;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisProjectCreationOperation extends AbstractStructureOperation<DSSProjectContentRequestRefImpl, ProjectResponseRef>
    implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private static Logger LOGGER = LoggerFactory.getLogger(QualitisProjectCreationOperation.class);

    private static final String CREATE_PROJECT_PATH = "qualitis/outer/api/v1/project/workflow";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), CREATE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        }

        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayLoad = new HashMap<>(4);

        requestPayLoad.put("project_name", projectRef.getDSSProject().getName());
        requestPayLoad.put("username", projectRef.getDSSProject().getCreateBy());
        requestPayLoad.put("description", projectRef.getDSSProject().getDescription());

        List<Map<String, Object>> authorizeUsers = AuthorizeUtil.constructAuthorizeUsers(projectRef.getDSSProjectPrivilege().getAccessUsers(), projectRef.getDSSProjectPrivilege().getEditUsers(), projectRef.getDSSProjectPrivilege().getReleaseUsers());
        requestPayLoad.put("project_authorize_users", authorizeUsers);

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);
        Map<String, Object> resMap;
        try {
            RestTemplate restTemplate = new RestTemplate();
            LOGGER.info("Start to create qualitis project. url: {}, method: {}, body: {}", url, HttpMethod.PUT, entity);
            resMap = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, entity, Map.class).getBody();
        } catch (Exception e) {
            LOGGER.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project exception", e);
        }

        if (! checkResponse(resMap)) {
            String message = (String) resMap.get("message");
            String errorMessage = String.format("Error! Can not add project, exception: %s", message);
            LOGGER.error(errorMessage);
            throw new ExternalOperationFailedException(90176, errorMessage, null);
        }

        Integer projectId = (Integer) ((Map<String, Object>) ((Map<String, Object>) resMap.get("data")).get("project_detail")).get("project_id");
        LOGGER.info("Create qualitis project ID: {}", projectId);
        return ProjectResponseRef.newExternalBuilder().setRefProjectId(projectId.longValue()).success();
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }

    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }
}
