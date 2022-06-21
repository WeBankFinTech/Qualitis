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

package com.webank.wedatasphere.dss.appconn.qualitis.ref.operation;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.appconn.qualitis.publish.QualitisDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.UpdateWitContextRequestRefImpl;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefUpdateOperation extends QualitisDevelopmentOperation<UpdateWitContextRequestRefImpl, ResponseRef>
    implements RefUpdateOperation<ThirdlyRequestRef.UpdateWitContextRequestRefImpl> {

    private static final String UPDATE_RULE_PATH = "/qualitis/outer/api/v1/projector/rule/modify";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    private final static Logger LOGGER = LoggerFactory.getLogger(QualitisRefUpdateOperation.class);

    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    private ResponseRef updateQualitisCS(UpdateWitContextRequestRefImpl qualitisUpdateCSRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("Update CS request body" + new Gson().toJson(qualitisUpdateCSRequestRef));
        String csId = qualitisUpdateCSRequestRef.getContextId();
        String projectName = qualitisUpdateCSRequestRef.getProjectName();

        LOGGER.info("Start set context for qualitis node: {}", qualitisUpdateCSRequestRef.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestPayLoad = new HashMap<>();
        try {
            requestPayLoad.put("cs_id", csId);
            requestPayLoad.put("rule_name", qualitisUpdateCSRequestRef.getName());
            requestPayLoad.put("project_name", projectName);
            requestPayLoad.put("project_id", qualitisUpdateCSRequestRef.getRefProjectId());

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Object> entity = new HttpEntity<>(new Gson().toJson(requestPayLoad), headers);
            String url = HttpUtils.buildUrI(getBaseUrl(), UPDATE_RULE_PATH, appId, appToken,
                RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
            LOGGER.info("Set context service url is {}", url);
            Map<String, Object> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, Map.class).getBody();
            if (response == null) {
                LOGGER.error("Failed to delete rule. Response is null.");
                throw new ExternalOperationFailedException(90176, "Failed to delete rule. Response is null.");
            }
            String status = response.get("code").toString();
            if (! "200".equals(status)) {
                String errorMsg = response.get("message").toString();
                throw new ExternalOperationFailedException(90176, errorMsg);
            }
            return new ResponseRefImpl(new Gson().toJson(response), HttpStatus.OK.value(), "", response);
        } catch (Exception e){
            throw new ExternalOperationFailedException(90176,"Set context Qualitis AppJointNode Exception", e);
        }
    }

    @Override
    public ResponseRef updateRef(UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        return updateQualitisCS(requestRef);
    }
}
