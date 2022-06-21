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

package com.webank.wedatasphere.dss.appconn.qualitis.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.RefJobContentRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.net.URISyntaxException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.HttpMethod;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefExportOperation extends QualitisDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ExportResponseRef>
    implements RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    private final static Logger LOGGER = LoggerFactory.getLogger(QualitisRefExportOperation.class);
    private static final String EXPORT_RULE_URL = "qualitis/outer/api/v1/projector/rule/export";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    /**
     * Default user for BML download and upload.
     */
    private static final String DEFAULT_USER = "hadoop";

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }

    @Override
    public ExportResponseRef exportRef(RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        Map<String, Object> jobContent = requestRef.getRefJobContent();
        LOGGER.info("Export request body" + new Gson().toJson(requestRef));

        String userName = requestRef.getUserName();
        String id = "";
        if (jobContent.get("ruleGroupId") != null) {
            id = jobContent.get("ruleGroupId").toString();
        } else {
            id = jobContent.get("rule_group_id").toString();
        }
        float f = Float.valueOf(id);

        Long groupId = (long) f;

        if (null == userName || null == groupId) {
            throw new ExternalOperationFailedException(90156, "Rule group ID or username is null when export.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();

        Map<String, Object> requestPayLoad = new HashMap<>();

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

        RestTemplate restTemplate = new RestTemplate();
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(),  EXPORT_RULE_URL + "/" + groupId.toString(), appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Qualitis build export url error.", e);
            throw new ExternalOperationFailedException(90156, "Qualitis build export url error.", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90156, "Qualitis build export url error.", e);
        }
        LOGGER.info("Start to export to qualitis. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.getForEntity(url, Map.class).getBody();
        String finishLog = String.format("Finish to export to qualitis. response: %s", response);
        LOGGER.info(finishLog);

        if (response == null) {
            String errorMsg = "Error! Can not export, response is null";
            LOGGER.error(errorMsg);
            return null;
        }

        if (! checkResponse(response)) {
            String message = (String) response.get("message");
            String errorMsg = String.format("Error! Can not export, exception: %s", message);
            LOGGER.error(errorMsg);
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = (Map) response.get("data");
        String dataString;
        try {
            dataString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error when parse export responses to json.", e);
            throw new ExternalOperationFailedException(90156, "Error when parse export responses to json.", e);
        }
        /**
         * BML client upload opeartion.
         */
        BmlClient bmlClient = BmlClientFactory.createBmlClient(DEFAULT_USER);
        BmlUploadResponse bmlUploadResponse = bmlClient.uploadResource(DEFAULT_USER,
            "Qualitis_exported_" + UUID.randomUUID().toString(), new ByteArrayInputStream(dataString.getBytes(StandardCharsets.UTF_8)));
        Map<String, Object> resourceMap = new HashMap();
        resourceMap.put("resourceId", bmlUploadResponse.resourceId());
        resourceMap.put("version", bmlUploadResponse.version());
        return ExportResponseRef.newBuilder().setResourceMap(resourceMap).success();
    }
}
