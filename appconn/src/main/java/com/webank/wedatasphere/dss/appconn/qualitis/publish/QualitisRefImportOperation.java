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
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.ImportWitContextRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlDownloadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefImportOperation extends QualitisDevelopmentOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl, RefJobContentResponseRef>
    implements RefImportOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl> {

    private final static Logger LOGGER = LoggerFactory.getLogger(QualitisRefImportOperation.class);


    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    private static final String IMPORT_RULE_URL = "qualitis/outer/api/v1/projector/rule/import";

    /**
     * Default user for BML download and upload.
     */
    private static final String DEFAULT_USER = "hadoop";

    @Override
    public RefJobContentResponseRef importRef(ImportWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        Map<String, Object> resourceMap = requestRef.getResourceMap();
        LOGGER.info("Import request body" + new Gson().toJson(requestRef));
        /**
         * BML client download opeartion.
         */
        BmlClient bmlClient = BmlClientFactory.createBmlClient(DEFAULT_USER);
        BmlDownloadResponse bmlDownloadResponse = bmlClient.downloadResource(DEFAULT_USER, resourceMap.get("resourceId").toString(), resourceMap.get("version").toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String dataJsonString = "";
        try {
            dataJsonString = IOUtils.toString(bmlDownloadResponse.inputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("Error with bml download and mapper to json.", e);
        }
        Map<String, Object> data = null;
        try {
            data = objectMapper.readValue(dataJsonString, Map.class);
            LOGGER.info("BML downloaded data: ", data.toString());
        } catch (JsonProcessingException e) {
            LOGGER.error("BML parse error.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> ruleGroupInfo;

        data.put("newProjectId", requestRef.getRefProjectId());
        data.put("userName", requestRef.getUserName());

        HttpEntity<Object> entity = new HttpEntity<>(new Gson().toJson(data), headers);
        try {
            String url = HttpUtils.buildUrI(getBaseUrl(), IMPORT_RULE_URL, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
            LOGGER.info("Start to import rule. url: {}, method: {}, body: {}", url, HttpMethod.PUT, entity);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class).getBody();
            if (response == null) {
                LOGGER.error("Failed to import rule. Response is null.");
                throw new ExternalOperationFailedException(90157, "import qualitis appconn node exception.");
            }
            String code = response.get("code").toString();
            if (! "200".equals(code)) {
                LOGGER.error("Failed to import rule. Response is not OK. Error message : {}",(String)response.get("message"));
                throw new ExternalOperationFailedException(90157, "import qualitis appconn node exception : "+(String)response.get("message"));
            }
            LOGGER.info("Finished to import rule. response: {}", response);
            ruleGroupInfo = (Map<String, Object>) response.get("data");
            if (ruleGroupInfo.containsKey("rule_group_id") && ruleGroupInfo.get("rule_group_id") != null) {
                Object ruleGroupId = ruleGroupInfo.get("rule_group_id");
                ruleGroupInfo.remove("rule_group_id");
                ruleGroupInfo.put("ruleGroupId", ruleGroupId);
            }

            return RefJobContentResponseRef.newBuilder().setRefJobContent(ruleGroupInfo).success();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Import rule failed.", e);
            throw new ExternalOperationFailedException(90157, "Import qualitis appJoint node exception.", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90157, "Import qualitis appJoint node exception.", e);
        }
    }
}
