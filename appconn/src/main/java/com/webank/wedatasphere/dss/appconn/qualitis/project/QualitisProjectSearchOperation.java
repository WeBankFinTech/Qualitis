package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef.RefProjectContentRequestRefImpl;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2022/5/12 19:15
 */
public class QualitisProjectSearchOperation extends AbstractStructureOperation<RefProjectContentRequestRefImpl, ProjectResponseRef>
    implements ProjectSearchOperation<RefProjectContentRequestRefImpl> {
    private static Logger LOGGER = LoggerFactory.getLogger(QualitisProjectSearchOperation.class);

    private static final String GET_PROJECT_PATH = "qualitis/outer/api/v1/project/workflow/get";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";
    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), GET_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Search qualitis project exception", e);
            throw new ExternalOperationFailedException(90176, "Search qualitis project by build url exception", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90176, "Search qualitis project by build url exception", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayLoad = new HashMap<>(2);
        requestPayLoad.put("username", projectRef.getUserName());
        requestPayLoad.put("name", projectRef.getProjectName());
        Gson gson = new Gson();

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

        Map<String, Object> resMap;
        try {
            RestTemplate restTemplate = new RestTemplate();
            LOGGER.info("Start to search qualitis project. url: {}, method: {}, body: {}", url, HttpMethod.POST, entity);
            resMap = restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, Map.class).getBody();

        } catch (Exception e) {
            LOGGER.error("Search qualitis project exception", e);
            throw new ExternalOperationFailedException(90176, "Search qualitis project exception", e);
        }

        if (! checkResponse(resMap)) {
            String message = (String) resMap.get("message");
            String errorMessage = String.format("Error! Can not search project, exception: %s", message);
            LOGGER.error(errorMessage);
            throw new ExternalOperationFailedException(90176, errorMessage, null);
        }
        Integer projectId = (Integer) ((Map<String, Object>) ((Map<String, Object>) resMap.get("data")).get("project_detail")).get("project_id");
        LOGGER.info("Get qualitis project ID: {}", projectId);
        return ProjectResponseRef.newExternalBuilder().setRefProjectId(projectId.longValue()).success();
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }
}
