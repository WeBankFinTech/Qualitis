package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.config.FpsConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.service.FpsService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
@Service
public class FpsServiceImpl implements FpsService {

    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FpsConfig fpsConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(FpsServiceImpl.class);
    private static final List<String> END_TASK_STATUS_LIST = Arrays.asList(TaskStatusEnum.SUCCEED.getState(), TaskStatusEnum.CANCELLED.getState(), TaskStatusEnum.TIMEOUT.getState(), TaskStatusEnum.FAILED.getState());
    private static final String FPS_FILE_PATH = "hdfs:///apps-data/{USER}/";
    private static final String KEY_STATUS = "status";

    @Override
    public void downloadFpsFile(String fileId, String fileHashValue, String fileName, String clusterName, User user)
            throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Failed to find cluster_info named [" + clusterName + "]");
        }
        Gson gson = new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", QualitisConstants.FPS_DEFAULT_USER);
        headers.add("Token-Code", clusterInfo.getLinkisToken());
        String url = UriBuilder.fromUri(clusterInfo.getLinkisAddress()).path(fpsConfig.getPrefix()).path(fpsConfig.getSubmitJobNew()).toString();

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(4);
        Map<String, String> executionContent = Maps.newHashMapWithExpectedSize(1);
        executionContent.put("code", generateCode(fileId, fileHashValue, fileName, user));
        map.put("executionContent", executionContent);
        Map<String, String> labels = Maps.newHashMapWithExpectedSize(3);
        labels.put("engineType", "jobserver-1");
//        固定
        labels.put("userCreator", "hadoop-IDE");
        labels.put("codeType", "esjson");
        map.put("labels", labels);
        map.put("executeApplicationName", "jobserver");
//        必须与Token-User保持一致
        map.put("executeUser", QualitisConstants.FPS_DEFAULT_USER);

        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), headers);
        LOGGER.info("Start to submit fps job to linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        LOGGER.info("Succeed to submit fps job to linkis. response: {}", response);
        checkIfUploadSuccess(response, clusterName, fileId);
    }

    private String generateCode(String fileId, String fileHashValue, String fileName, User user) throws UnExpectedRequestException {
        Map<String, Object> codeMap = Maps.newHashMapWithExpectedSize(9);
        codeMap.put("handler.type", "fpsSend");
        codeMap.put("exec.user", user.getUsername());
        codeMap.put("fps.name", "fps");
        codeMap.put("fps.fileid", fileId);
        codeMap.put("fps.hashcode", fileHashValue);
        codeMap.put("fpsSend.target", "jar");
        codeMap.put("fpsSend.file.rename.to", fileName);
        // 最终保存在hdfs的文件目录为/apps-data/<exec.user>/<fpsSend.jar.subdir>，<fpsSend.jar.subdir> = fpsyyyyMMdd/
        String subDir = "fps" + DateUtils.now("yyyyMMdd") + SpecCharEnum.SLASH.getValue();
        codeMap.put("fpsSend.jar.subdir", subDir);
        try {
            return new ObjectMapper().writeValueAsString(codeMap);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to format code", e);
            throw new UnExpectedRequestException("Failed to format code");
        }
    }

    private void checkIfUploadSuccess(Map<String, Object> response, String clusterName, String fileId)
            throws ClusterInfoNotConfigException, TaskNotExistException, UnExpectedRequestException {
        if (response != null && 0 == (Integer) response.get(KEY_STATUS)) {
            int fpsTaskId = ((Integer) ((Map<String, Object>) response.get("data")).get("taskID")).intValue();
            String fpsTaskStatus;
            int maxRequestCount = fpsConfig.getMaxRetries() + 1;
            int maxWaitDuration = fpsConfig.getTotalWaitDuration();
            int retryInterval = maxWaitDuration / maxRequestCount;
            int requestCount = 1;
            do {
                fpsTaskStatus = getTaskStatus(fpsTaskId, QualitisConstants.FPS_DEFAULT_USER, clusterName).toUpperCase();
                if (END_TASK_STATUS_LIST.contains(fpsTaskStatus)) {
                    break;
                }
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    LOGGER.error("Fps file operation is not finished! But interrupted!", e);
                }
                LOGGER.info("Retry to check status of task, count: {}", requestCount);
                requestCount++;
            } while (requestCount < maxRequestCount);
            if (!fpsTaskStatus.equals(TaskStatusEnum.SUCCEED.getState())) {
                throw new UnExpectedRequestException("Retry has ended, failed to upload fps file! Current task status is: " + fpsTaskStatus);
            }
        } else {
            throw new UnExpectedRequestException("Failed to get fps file. Maybe fps file is already expired! File ID[" + fileId + "]");
        }
    }

    @Override
    public String getExcelSheetName(String fileName, String clusterName, User user) throws UnExpectedRequestException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Failed to find cluster_info named [" + clusterName + "]");
        }
        String subDir = "fps" + DateUtils.now("yyyyMMdd") + SpecCharEnum.SLASH.getValue();
        String hdfsPath = FPS_FILE_PATH.replace("{USER}", user.getUsername()) + subDir;
        String url = UriBuilder.fromUri(clusterInfo.getLinkisAddress())
                .path(fpsConfig.getPrefix())
                .path(fpsConfig.getFileSystem())
                .queryParam("path", hdfsPath.concat(fileName)).toString();
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Fps url decode with UTF-8 exception.", e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user.getUsername());
        headers.add("Token-Code", clusterInfo.getLinkisToken());

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get fps excel sheet name. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        LOGGER.info("Succeed to get fps excel sheet name. response: {}", response);
        if (response != null && 0 == (Integer) response.get(KEY_STATUS)) {
            return ((List<String>) ((Map<String, Object>) ((Map<String, Object>) response.get("data")).get("formate")).get("sheetName")).get(0);
        } else {
            throw new UnExpectedRequestException("Faild to get fps excel file.");
        }
    }

    @Override
    public String getTaskStatus(Integer taskId, String user, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        Map<String, Object> response = getTaskDetail(taskId, user, clusterName);

        return (String) response.get(KEY_STATUS);
    }

    private Map<String, Object> getTaskDetail(Integer taskId, String user, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new ClusterInfoNotConfigException("Failed to find cluster_info named [" + clusterName + "]");
        }
        String url = UriBuilder.fromUri(clusterInfo.getLinkisAddress()).path(fpsConfig.getPrefix()).path(fpsConfig.getStatus()).toString();
        url = url.replace("{id}", String.valueOf(taskId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", clusterInfo.getLinkisToken());
        HttpEntity entity = new HttpEntity<>(headers);

        LOGGER.info("Start to get job status from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        try {
            LOGGER.info("Succeed to get job status from linkis. response: {}", new ObjectMapper().writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to format response", e);
        }

        if (response != null && 0 == (Integer) response.get(KEY_STATUS)) {
            return (Map) ((Map) response.get("data")).get("task");
        } else {
            throw new TaskNotExistException("Can not get status of task, task_id : " + taskId);
        }
    }
}
