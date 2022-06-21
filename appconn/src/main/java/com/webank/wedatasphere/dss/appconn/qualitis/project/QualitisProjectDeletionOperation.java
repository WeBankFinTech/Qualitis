package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.apache.commons.lang.RandomStringUtils;
import java.security.NoSuchAlgorithmException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou
 */
public class QualitisProjectDeletionOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {
    private static Logger LOGGER = LoggerFactory.getLogger(QualitisProjectDeletionOperation.class);
    private static final String DELETE_PROJECT_PATH = "qualitis/outer/api/v1/project/workflow/delete";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";
    @Override
    protected String getAppConnName() {
        return QualitisAppConn.QUALITIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), DELETE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Delete Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Delete qualitis project by build url exception", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Qualitis uri syntax exception.", e);
            throw new ExternalOperationFailedException(90176, "Delete qualitis project by build url exception", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();

        Map<String, Object> requestPayLoad = new HashMap<>(4);
        requestPayLoad.put("project_id", projectRef.getRefProjectId());
        requestPayLoad.put("username", projectRef.getUserName());

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

        Map<String, Object> resMap;
        try {
            RestTemplate restTemplate = new RestTemplate();
            LOGGER.info("Start to update qualitis project. url: {}, method: {}, body: {}", url, HttpMethod.POST, entity);
            resMap = restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, Map.class).getBody();
        } catch (Exception e) {
            LOGGER.error("Delete Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Delete qualitis project exception", e);
        }

        if (! checkResponse(resMap)) {
            String message = (String) resMap.get("message");
            String errorMessage = String.format("Error! Can not delete project, exception: %s", message);
            LOGGER.error(errorMessage);
            throw new ExternalOperationFailedException(90176, errorMessage, null);
        }

        LOGGER.info("Deleted qualitis project ID: {}", projectRef.getRefProjectId());
        return new ResponseRefImpl(gson.toJson(resMap), HttpStatus.OK.value(), "", resMap);
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
    }

}
