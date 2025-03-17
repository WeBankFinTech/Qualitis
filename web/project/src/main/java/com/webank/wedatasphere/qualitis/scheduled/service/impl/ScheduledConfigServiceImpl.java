package com.webank.wedatasphere.qualitis.scheduled.service.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.scheduled.request.PublishUserRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledFormRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.WorkFlowRequest;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledConfigService;
import com.webank.wedatasphere.qualitis.scheduled.util.ScheduledGetSessionUtil;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class ScheduledConfigServiceImpl implements ScheduledConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledConfigServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    private HttpServletRequest httpServletRequest;

    public ScheduledConfigServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<Map<String, Object>> getAllPublishUser(PublishUserRequest request) throws UnExpectedRequestException, IOException {
        PublishUserRequest.checkRequest(request);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getCluster());
        String serverAddress = getServerAddress(clusterInfo);
        String realUser = HttpUtils.getUserName(httpServletRequest);
        String sessionId = ScheduledGetSessionUtil.getSessionIdByRealUser(realUser, request.getCluster());
        logger.info(">>>>>>>>>> Success Get SessionId From Scheduled System:  <<<<<<<<<<" +sessionId);
        String response;
        try {
//            String url = String.format("%s/manager?ajax=ajaxFetchMaintainedDeptUsers&userName=%s&session.id=%s", existCluster.get("path").toString(), realUser, sessionId);
            String url = UriBuilder.fromUri(serverAddress).path("/manager")
                    .queryParam("ajax", "ajaxFetchMaintainedDeptUsers")
                    .queryParam("session.id", sessionId)
                    .queryParam("userName", realUser)
                    .toString();
            logger.info("发起请求 url: " + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            //将请求头部和参数合成一个请求
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To Get publish user  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Failed to get publish user from wtss, exception: " + e.getMessage(), 500);
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(response, resultMap.getClass());

        return new GeneralResponse<>(ResponseStatusConstants.OK, "Success to get publish user from wtss", resultMap);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getScheduledProject(ScheduledProjectRequest request) throws UnExpectedRequestException, IOException {
        ScheduledProjectRequest.checkRequest(request);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getCluster());
        String serverAddress = getServerAddress(clusterInfo);
        //username放运维用户，normalUserName放实名用户
        //登录用户作为实名用户，发布用户作为运维用户查询 wtss 项目下拉列表接口；如果发布用户不选择，发布用户后台默认处理与登录用户相同
        String realUser = HttpUtils.getUserName(httpServletRequest);
        String sessionId = handReleaseUserForSession(request.getReleaseUser(), request.getCluster());
        logger.info(">>>>>>>>>> Success Get SessionId From Scheduled System:  <<<<<<<<<<" + sessionId);
        String response;
        try {
//            String url = String.format("%s/manager?ajax=ajaxFetchUserPermProjects&userName=%s&session.id=%s", serverAddress, realUser, sessionId);
            String url = UriBuilder.fromUri(serverAddress).path("/manager")
                    .queryParam("ajax", "ajaxFetchUserPermProjects")
                    .queryParam("session.id", sessionId)
                    .queryParam("userName", realUser)
                    .toString();
            logger.info("发起请求 url: " + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            //将请求头部和参数合成一个请求
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To Get Wtss Project  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Failed to get project from wtss, exception: " + e.getMessage(), 500);
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(response, resultMap.getClass());

        return new GeneralResponse<>(ResponseStatusConstants.OK, "Success to get Project from wtss", resultMap);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getScheduledWorkFlow(WorkFlowRequest request) throws UnExpectedRequestException, IOException {
        WorkFlowRequest.checkRequest(request);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getCluster());
        String serverAddress = getServerAddress(clusterInfo);
        String sessionId = handReleaseUserForSession(request.getReleaseUser(), request.getCluster());
        logger.info(">>>>>>>>>> Success Get SessionId From Scheduled System:  <<<<<<<<<<"+sessionId);
        String response;
        try {
//            String url = String.format("%s/manager?session.id=%s&ajax=fetchprojectflows&project=%s", existCluster.get("path").toString(), sessionId, request.getProjectName());
            String url = UriBuilder.fromUri(serverAddress).path("/manager")
                    .queryParam("ajax", "fetchprojectflows")
                    .queryParam("session.id", sessionId)
                    .queryParam("project", request.getProjectName())
                    .toString();
            logger.info("发起请求 url: " + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            //将请求头部和参数合成一个请求
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To Get Wtss Work Flow  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Failed to get Work Flow from wtss, exception: " + e.getMessage(), 500);
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(response, resultMap.getClass());

        return new GeneralResponse<>(ResponseStatusConstants.OK, "Success to get Work Flow from wtss", resultMap);
    }

    private String handReleaseUserForSession(String releaseUser, String cluster) throws UnExpectedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        String operationUser = StringUtils.isNotBlank(releaseUser) ? releaseUser : loginUser;
        return ScheduledGetSessionUtil.getSessionId(operationUser, loginUser, cluster);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getScheduledTask(ScheduledFormRequest request) throws UnExpectedRequestException, IOException {
        ScheduledFormRequest.checkRequest(request);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getCluster());
        String serverAddress = getServerAddress(clusterInfo);
        String sessionId = handReleaseUserForSession(request.getReleaseUser(), request.getCluster());
        logger.info(">>>>>>>>>> Success Get SessionId From Scheduled System:  <<<<<<<<<<"+sessionId);
        String response;
        try {
            String url = UriBuilder.fromUri(serverAddress).path("/manager")
                    .queryParam("ajax", "fetchflowjobs")
                    .queryParam("session.id", sessionId)
                    .queryParam("project", request.getProject())
                    .queryParam("flow", request.getFlow())
                    .toString();
            logger.info("发起请求 url: " + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            //将请求头部和参数合成一个请求
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To Get Wtss Task  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Failed to get Task from wtss, exception: " + e.getMessage(), 500);
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(response, resultMap.getClass());

        if (MapUtils.isNotEmpty(resultMap) && resultMap.containsKey("nodes")) {
            List<Object> nodes = (List<Object>) resultMap.get("nodes");
            nodes = nodes.stream().filter(node -> {
                Map<String, Object> nodeMap = (Map<String, Object>) node;
                if ("flow".equals(nodeMap.get("type"))) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            resultMap.put("nodes", nodes);
        }

        return new GeneralResponse<>(ResponseStatusConstants.OK, "Success to get Task from wtss", resultMap);
    }

    private String getServerAddress(ClusterInfo clusterInfo) throws UnExpectedRequestException {
        if (Objects.isNull(clusterInfo)) {
            throw new UnExpectedRequestException("Cluster doesn't exists");
        }
        Map<String, Object> wtssJsonMap = clusterInfo.getWtssJsonMap();
        String serverAddress = String.valueOf(wtssJsonMap.getOrDefault("path", ""));
        if (StringUtils.isEmpty(serverAddress)) {
            throw new UnExpectedRequestException("The address of server doesn't exists");
        }
        return serverAddress;
    }

}
