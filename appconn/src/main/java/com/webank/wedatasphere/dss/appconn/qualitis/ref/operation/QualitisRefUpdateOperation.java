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
import com.webank.wedatasphere.dss.appconn.qualitis.publish.QualitisDevelopmentOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.UpdateWitContextRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefUpdateOperation extends QualitisDevelopmentOperation<UpdateWitContextRequestRefImpl, ResponseRef>
    implements RefUpdateOperation<ThirdlyRequestRef.UpdateWitContextRequestRefImpl> {

    private static final String MODIFY_RULE_URL = "/qualitis/outer/api/v1/projector/rule/modify";
    private final static Logger LOGGER = LoggerFactory.getLogger(QualitisRefUpdateOperation.class);

    private static String appId = "linkis_id";
    private static String appToken = "***REMOVED***";

    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef updateRef(UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        // Get rule group info from request.
        LOGGER.info("Start to get the job content when modify.");
        Map<String, Object> jobContent = requestRef.getRefJobContent();
        LOGGER.info("The job content when modify ref is:" + jobContent);
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), MODIFY_RULE_URL, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Modify rule failed. Rule group info: {}, exception: {}", jobContent, e);
            throw new ExternalOperationFailedException(90156, "Construct modify outer url failed when delete.");
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90156, "Construct modify outer url failed when delete.");
        }
        if (jobContent == null || ! jobContent.containsKey("ruleGroupId") || jobContent.get("ruleGroupId") == null) {
            LOGGER.info("Rule group ID is null when modify.");
            return ResponseRef.newExternalBuilder().success();
        }
        Integer ruleGroupId = null;
        if(jobContent.get("ruleGroupId") instanceof Double){
            Double tempId = (Double)jobContent.get("ruleGroupId");
            ruleGroupId = tempId.intValue();
        }else if(jobContent.get("ruleGroupId") instanceof Integer){
            ruleGroupId = (Integer)jobContent.get("ruleGroupId");
        }
        LOGGER.info("Rules in {} will be modified.", ruleGroupId.toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String,Object> request = new HashMap<>();
            request.put("rule_group_id", Long.valueOf(ruleGroupId.toString()));
            request.put("node_name", requestRef.getName());

            HttpEntity<Object> entity = new HttpEntity<>(new Gson().toJson(request), headers);

            LOGGER.info("Start to modify rule. url: {}, method: {}, body: {}", String.format("%s/%s", url, MODIFY_RULE_URL), javax.ws.rs.HttpMethod.POST, entity);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
            if (response == null) {
                LOGGER.error("Failed to modify rule. Response is null.");
                throw new ExternalOperationFailedException(90156, "Modify outer url return null.");
            }
            LOGGER.info("Finished to modify rule. Response: {}", response);
            String status = response.get("code").toString();
            if (status != null && HttpStatus.OK.value() == Integer.parseInt(status)) {
                LOGGER.info("Modify rule successfully.");
            } else {
                LOGGER.error("Modify rule failed.");
                throw new ExternalOperationFailedException(90156, "Modify outer url return status is not ok.");
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to modify rule because of restTemplate exception. Exception is: {}", e);
            throw new ExternalOperationFailedException(90156, "Modify outer url request failed with rest template.");
        }
        return ResponseRef.newExternalBuilder().success();
    }
}
