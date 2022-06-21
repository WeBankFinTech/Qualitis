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
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.RefJobContentRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.apache.commons.lang.RandomStringUtils;
import java.security.NoSuchAlgorithmException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefDeletionOperation extends QualitisDevelopmentOperation<RefJobContentRequestRefImpl, ResponseRef>
    implements RefDeletionOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    private static final String DELETE_RULE_URL = "/qualitis/outer/api/v1/projector/rule/delete";
    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisRefDeletionOperation.class);

    private static String appId = "linkis_id";
    private static String appToken = "a33693de51";

    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef deleteRef(RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        // Get rule group info from request.
        LOGGER.info("Start to get the job content when delete ref.");
        Map<String, Object> jobContent = requestRef.getRefJobContent();
        LOGGER.info("The job content when delete ref is:" + jobContent);
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), DELETE_RULE_URL, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Delete rule failed. Rule group info: {}, exception: {}", jobContent, e);
            throw new ExternalOperationFailedException(90156, "Construct delete outer url failed when delete.");
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90156, "Construct delete outer url failed when delete.");
        }
        if (jobContent == null || ! jobContent.containsKey("ruleGroupId") || jobContent.get("ruleGroupId") == null) {
            LOGGER.info("Rule group ID is null when delete.");
            return ResponseRef.newExternalBuilder().success();
        }
        Integer ruleGroupId = null;
        if(jobContent.get("ruleGroupId") instanceof Double){
            Double tempId = (Double)jobContent.get("ruleGroupId");
            ruleGroupId = tempId.intValue();
        }else if(jobContent.get("ruleGroupId") instanceof Integer){
            ruleGroupId = (Integer)jobContent.get("ruleGroupId");
        }
        LOGGER.info("Rules in {} will be deleted.", ruleGroupId.toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String,Object> request = new HashMap<>();
            request.put("rule_group_id", Long.valueOf(ruleGroupId.toString()));

            HttpEntity<Object> entity = new HttpEntity<>(new Gson().toJson(request), headers);

            LOGGER.info("Start to delete rule. url: {}, method: {}, body: {}", String.format("%s/%s", url, DELETE_RULE_URL), javax.ws.rs.HttpMethod.POST, entity);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
            if (response == null) {
                LOGGER.error("Failed to delete rule. Response is null.");
                throw new ExternalOperationFailedException(90156, "Delete outer url return null.");
            }
            LOGGER.info("Finished to delete rule. Response: {}", response);
            String status = response.get("code").toString();
            if (status != null && HttpStatus.OK.value() == Integer.parseInt(status)) {
                LOGGER.info("Delete rule successfully.");
            } else {
                LOGGER.error("Delete rule failed.");
                throw new ExternalOperationFailedException(90156, "Delete outer url return status is not ok.");
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to delete rule because of restTemplate exception. Exception is: {}", e);
            throw new ExternalOperationFailedException(90156, "Delete outer url request failed with rest template.");
        }
        return ResponseRef.newExternalBuilder().success();
    }
}
