package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisTemplate;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.structure.optional.AbstractOptionalOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author leebai
 * @date 2022/5/19 17:00
 */
public class QualitisCheckTemplateSearchOperation extends AbstractOptionalOperation<StructureRequestRef, ResponseRef>{

    private static Logger LOGGER = LoggerFactory.getLogger(QualitisCheckTemplateSearchOperation.class);

    private CommonVars<String> QUALITIS_IP_PORT = CommonVars.apply("wds.dss.qualitis.ip.port", getBaseUrl());

    private CommonVars<String> SEARCH_USER_TEMPLATE_URL = CommonVars.apply("wds.dss.qualitis.template.url","qualitis/outer/api/v1/projector/rule/default/all");

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    @Override
    public String getOperationName() {
        return "getQualitisCheckTemplate";
    }

    @Override
    public ResponseRef apply(StructureRequestRef requestRef) {
        String url;
        try {
            url = HttpUtils.buildUrI(QUALITIS_IP_PORT.getValue(), SEARCH_USER_TEMPLATE_URL.getValue(), appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayLoad = new HashMap<>(2);
        requestPayLoad.put("user_name", requestRef.getUserName());
        requestPayLoad.put("page", 0);
        requestPayLoad.put("size", Integer.MAX_VALUE);
        Gson gson = new Gson();

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

        Map<String, Object> resMap;
        try {
            RestTemplate restTemplate = new RestTemplate();
            LOGGER.info("Start to search qualitis project. url: {}, method: {}, body: {}", url, HttpMethod.POST, entity);
            resMap = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();

        } catch (Exception e) {
            LOGGER.error("Search qualitis template exception", e);
            throw new ExternalOperationFailedException(90176, "Search qualitis template exception", e);
        }

        if (! checkResponse(resMap)) {
            String message = (String) resMap.get("message");
            String errorMessage = String.format("Error! Can not search project, exception: %s", message);
            LOGGER.error(errorMessage);
            throw new ExternalOperationFailedException(90176, errorMessage, null);
        }
        String templateJson = BDPJettyServerHelper.gson().toJson(((Map<String, Object>)resMap.get("data")).get("data"));
        QualitisTemplate[] templateArray = BDPJettyServerHelper.gson().fromJson(templateJson, QualitisTemplate[].class);
        List<QualitisTemplate> templateList = Arrays.asList(templateArray);
        Map<String, Object> responseMap = new HashMap<>(1);
        responseMap.put("template", templateList);
        LOGGER.info("Get qualitis project ID: {}", templateList);
        return new ResponseRefImpl("", 0 ,null, responseMap);
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }
}

