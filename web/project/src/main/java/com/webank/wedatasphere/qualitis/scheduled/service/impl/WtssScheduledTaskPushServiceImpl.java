package com.webank.wedatasphere.qualitis.scheduled.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.constants.WtssCommonConstants;
import com.webank.wedatasphere.qualitis.dao.ApplicationCommentDao;
import com.webank.wedatasphere.qualitis.dao.LinksErrorCodeDao;
import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import com.webank.wedatasphere.qualitis.entity.LinksErrorCode;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.WtssScheduledJobRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.WtssScheduledProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskPushService;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 18:17
 * @description
 */
@Service("wtssScheduledPushService")
public class WtssScheduledTaskPushServiceImpl implements ScheduledTaskPushService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WtssScheduledTaskPushServiceImpl.class);

    @Autowired
    private LinkisConfig linkisConfig;
    @Resource
    private RestTemplate restTemplate;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private LinksErrorCodeDao linksErrorCodeDao;
    @Autowired
    private ApplicationCommentDao applicationCommentDao;

    private static final String RULE_EXECUTE_COMMAND_FORMAT = "bdp-client -c %s dqm --rules-id %s %s";

    /**
     * This method used for preventing failures when pushing to WTSS due to excessively long character
     *
     * @param name
     * @return
     */
    public static String clipRuleGroupName(String name) {
        if (name.length() > 128) {
            String prefix = StringUtils.substring(name, 0, 95);
            String suffix = StringUtils.substring(name, name.length() - 32, name.length());
            return prefix + SpecCharEnum.BOTTOM_BAR.getValue() + suffix;
        }
        return name;
    }

    @Override
    public void deleteProject(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws ScheduledPushFailedException {
        String uri = address + "/manager?delete=true&session.id=" + sessionId + "&project=" + wtssScheduledProjectRequest.getProjectName();
        LOGGER.info("deleteProject, uri:{}", uri);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        LOGGER.info("deleteProject, resp:{}", responseEntity);
        if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
            throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
        }
    }

    @Override
    public Map<String, Object> getOperationHistories(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws ScheduledPushFailedException {
        String uri = address + "/manager?ajax=fetchProjectLogs&session.id=" + sessionId + "&project=" + wtssScheduledProjectRequest.getProjectName();
        LOGGER.info("getOperationHistories, uri:{}", uri);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        LOGGER.info("getOperationHistories, resp:{}", responseEntity);
        if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
            throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
        }
        String body = responseEntity.getBody();
        if (StringUtils.isEmpty(body)) {
            throw new ScheduledPushFailedException("Error!response body is null");
        }
        try {
            return new Gson().fromJson(body, Map.class);
        } catch (Exception e) {
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    @Override
    public void createProject(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException {
        String uri = address + "/manager?action=create&session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("action", "create");
        requestMap.add("name", wtssScheduledProjectRequest.getProjectName());
        requestMap.add("description", "Qualitis");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);
        LOGGER.info("createProject, uri:{}, req:{}", uri, wtssScheduledProjectRequest.toString());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
        LOGGER.info("createProject, resp:{}", responseEntity);
        if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
            throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
        }
        String body = responseEntity.getBody();
        if (StringUtils.isEmpty(body)) {
            throw new ScheduledPushFailedException("Error!response body is null");
        }
        try {
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if (!"success".equals(responseMap.get("status"))) {
                String message = responseMap.containsKey("message") ? responseMap.get("message").toString() : "Failed to create project";
                throw new ScheduledPushFailedException(message);
            }
        } catch (Exception e) {
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    @Override
    public String uploadProject(List<WtssScheduledJobRequest> wtssScheduledJobRequests, Map<String, Object> jsonServerMap, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException, IOException {
        if (CollectionUtils.isEmpty(wtssScheduledJobRequests)) {
            throw new UnExpectedRequestException("WTSS request parameters is empty.");
        }
        if (MapUtils.isEmpty(jsonServerMap)) {
            throw new UnExpectedRequestException("jobserver is empty.");
        }
        String scheduleProjectName = wtssScheduledJobRequests.get(0).getProjectName();
        Map<String, List<WtssScheduledJobRequest>> workflowAndTaskListMap = wtssScheduledJobRequests.stream().collect(Collectors.groupingBy(WtssScheduledJobRequest::getWorkflowName));
        File zipFile = generateZip(workflowAndTaskListMap, scheduleProjectName, jsonServerMap);
        if (zipFile == null) {
            throw new IOException("WtssScheduledTaskPushServiceImpl.uploadProject: Failed to create zip");
        }

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("ajax", "upload");
        requestMap.add("project", scheduleProjectName);
        requestMap.add("file", new FileSystemResource(zipFile));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);
        try {
            String uri = address + "/manager?ajax=upload&session.id=" + sessionId;
            LOGGER.info("uploadProject, uri:{}, scheduleProjectName:{}", uri, scheduleProjectName);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("uploadProject, resp:{}", responseEntity);

            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if (responseMap.containsKey("error")) {
                throw new ScheduledPushFailedException(responseMap.get("error").toString());
            }
            return responseMap.getOrDefault("projectId", "").toString();
        } catch (Exception e) {
            LOGGER.error("Failed to upload project to WTSS: {}", e.getMessage());
            throw new ScheduledPushFailedException(e.getMessage());
        } finally {
            if (zipFile.exists()) {
                Files.delete(zipFile.toPath());
            }
        }

    }

    @Override
    public Long createIntervalWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address, Object itsmNo) throws ScheduledPushFailedException {
        String uri = address + "/schedule?session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("projectName", wtssScheduledJobRequest.getProjectName());
        requestMap.add("ajax", "scheduleCronFlow");
        requestMap.add("cronExpression", wtssScheduledJobRequest.getCronExpression());
        requestMap.add("flow", wtssScheduledJobRequest.getScheduleName());
        requestMap.add("itsmNo", itsmNo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);

        try {
            LOGGER.info("createIntervalWorkflow, uri:{}, req:{}", uri, wtssScheduledJobRequest.toString());
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("createIntervalWorkflow, resp:{}", responseEntity);
            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if (!"success".equals(responseMap.get("status"))) {
                String message = responseMap.containsKey("message") ? responseMap.get("message").toString() : "Failed to add schedule";
                throw new ScheduledPushFailedException(message);
            }
            Object scheduleId = responseMap.get("scheduleId");
            Integer scheduleIdInt = Double.valueOf(scheduleId.toString()).intValue();
            return Long.valueOf(scheduleIdInt);
        } catch (Exception e) {
            LOGGER.error("Failed to add ScheduleWorkflow to WTSS", e);
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    @Override
    public Long createSignalWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException {
        String uri = address + "/eventschedule?session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("projectName", wtssScheduledJobRequest.getProjectName());
        Map<String, Object> signalTypeMap = wtssScheduledJobRequest.getSignalTypeMap();
        requestMap.add("ajax", "eventScheduleFlow");
        requestMap.add("scheduleId", null);
        requestMap.add("topic", signalTypeMap.get("msg.topic"));
        requestMap.add("msgName", signalTypeMap.get("msg.name"));
        requestMap.add("saveKey", signalTypeMap.get("msg.key"));
        requestMap.add("flow", wtssScheduledJobRequest.getScheduleName());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);

        try {
            LOGGER.info("createSignalWorkflow, uri:{}, req:{}", uri, wtssScheduledJobRequest.toString());
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("createSignalWorkflow, resp:{}", responseEntity);
            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if (!"success".equals(responseMap.get("status"))) {
                String message = responseMap.containsKey("error") ? responseMap.get("error").toString() : "Failed to add schedule";
                throw new ScheduledPushFailedException(message);
            }
            Object scheduleId = responseMap.get("eventScheduleId");
            Integer scheduleIdInt = Double.valueOf(scheduleId.toString()).intValue();
            return Long.valueOf(scheduleIdInt);
        } catch (Exception e) {
            LOGGER.error("Failed to add ScheduleWorkflow to WTSS", e);
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    @Override
    public Long modifyScheduleWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException {
        String uri = address + "/schedule?session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("scheduleId", wtssScheduledJobRequest.getScheduleId());
        requestMap.add("ajax", "scheduleEditFlow");
        requestMap.add("cronExpression", wtssScheduledJobRequest.getCronExpression());
        requestMap.add("flow", wtssScheduledJobRequest.getScheduleName());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);

        try {
            LOGGER.info("modifyScheduleWorkflow, uri:{}, req:{}", uri, wtssScheduledJobRequest);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("modifyScheduleWorkflow, resp:{}", responseEntity);
            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if (responseMap.containsKey("error")) {
                throw new ScheduledPushFailedException(responseMap.get("error").toString());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to modify schedule to WTSS", e);
            throw new ScheduledPushFailedException(e.getMessage());
        }
        return wtssScheduledJobRequest.getScheduleId();
    }

    @Override
    public void deleteSignalJob(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException {
        Long scheduleId = wtssScheduledJobRequest.getScheduleId();
        String uri = address + "/eventschedule?session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("action", "removeEventSchedule");
        requestMap.add("scheduleId", scheduleId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);

        try {
            LOGGER.info("deleteSignalJob, scheduleProject: {}, scheduleWorkflow: {}, scheduleId:{}", wtssScheduledJobRequest.getProjectName(), wtssScheduledJobRequest.getScheduleName(), scheduleId);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("deleteSignalJob, resp:{}", responseEntity);
            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if ("error".equals(responseMap.get("status"))) {
                String message = responseMap.containsKey("message") ? responseMap.get("message").toString() : "Failed to delete schedule";
                throw new ScheduledPushFailedException(message);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete schedule to WTSS", e);
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> synchronousRelationTask(String projectName, String workFlow, String taskName, List<Long> frontRuleGroup, List<Long> backRuleGroup
            , String hiveUrn, String sessionId, String address) throws UnExpectedRequestException {
        //ajax 固定值 prefixRules  前置校验规则组，规则 ID 组成，由逗号分隔  suffixRules   后置校验规则组，规则 ID 组成，由逗号分隔
        //前置校验规则组校验类型（阻断/旁路） 0/1   0 表示旁路，1 表示阻断
        Map<Long, String> prefixMap = Maps.newHashMap();
        //后置校验规则组校验类型（阻断/旁路） 0/1   0 表示旁路，1 表示阻断
        Map<Long, String> suffixMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(frontRuleGroup)) {
            prefixMap = relationRuleGroupConfig(frontRuleGroup, prefixMap);
        }
        if (CollectionUtils.isNotEmpty(backRuleGroup)) {
            suffixMap = relationRuleGroupConfig(backRuleGroup, suffixMap);
        }

        //jobCode WTSS/集群名/project/flow/job
        StringBuilder jobCode = new StringBuilder();
        jobCode.append("WTSS/");
        jobCode.append(hiveUrn);
        jobCode.append(SpecCharEnum.SLASH.getValue());
        jobCode.append(projectName);
        jobCode.append(SpecCharEnum.SLASH.getValue());
        jobCode.append(workFlow);
        jobCode.append(SpecCharEnum.SLASH.getValue());
        jobCode.append(taskName);
        try {
            String url = UriBuilder.fromUri(address).path("/executor")
                    .queryParam("ajax", "linkJobHook")
                    .queryParam("session.id", sessionId)
                    .queryParam("jobCode", jobCode)
                    .queryParam("prefixRules", URLEncoder.encode(CustomObjectMapper.transObjectToJson(prefixMap), "UTF-8"))
                    .queryParam("suffixRules", URLEncoder.encode(CustomObjectMapper.transObjectToJson(suffixMap), "UTF-8"))
                    .toString();
            LOGGER.info("To publish relation tasks to WTSS.  url: " + url);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            String response = restTemplate.exchange(new URI(url), HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
            LOGGER.info("Got response by publishing relation tasks from WTSS: " + response);
            Map<String, Object> resMap = new ObjectMapper().readValue(response, new TypeReference<Map<String, Object>>() {
            });
            if (!ResponseStatusConstants.OK.equals(resMap.getOrDefault("code", "").toString())) {
                resetWtssErrorResponse(resMap);
            }
            return resMap;
        } catch (IOException e) {
            throw new UnExpectedRequestException("Error!Failed to format response body.");
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>> Failed To Synchronization to Wtss  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Error!An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Error Example: Flow flow_rpd03.etl_main does not exist
     *
     * @param resMap
     * @return
     */
    private void resetWtssErrorResponse(Map<String, Object> resMap) {
        String errorInfo = resMap.getOrDefault("error", "Error!network occurred an unexpected error.").toString();
        LOGGER.error(errorInfo);
        List<LinksErrorCode> wtssErrorExpressionList = linksErrorCodeDao.findAllWtssErrorExpression();
        for (LinksErrorCode wtssErrorExpression : wtssErrorExpressionList) {
            if (errorInfo.trim().matches(wtssErrorExpression.getWtssErrorExpression())) {
                ApplicationComment applicationComment = applicationCommentDao.getByCode(wtssErrorExpression.getApplicationComment());
                resMap.put(WtssCommonConstants.WTSS_RESPONSE_CODE, ResponseStatusConstants.WORKFLOW_EXCEPTION_EXIST_CODE);
                resMap.put(WtssCommonConstants.WTSS_RES_ERROR, applicationComment.getZhMessage());
                break;
            }
        }
    }

    @Override
    public boolean submitWorkflowBusiness(ScheduledWorkflowBusiness scheduledWorkflowBusiness, List<ScheduledWorkflow> scheduledWorkflowList
            , String sessionId, String address, String scheduleProjectId, String itsmNo) {
        try {
            for (ScheduledWorkflow scheduledWorkflow : scheduledWorkflowList) {
                String url = UriBuilder.fromUri(address).path("/manager")
                        .queryParam("ajax", "mergeFlowBusiness")
                        .queryParam("session.id", sessionId)
                        .queryParam("projectId", scheduleProjectId)
                        .queryParam("flowId", scheduledWorkflow.getName())
                        .queryParam("busResLvl", scheduledWorkflowBusiness.getBusResLvl())
                        .queryParam("busDomain", URLEncoder.encode(scheduledWorkflowBusiness.getBusinessDomain(), "UTF-8"))
                        .queryParam("subsystem", scheduledWorkflowBusiness.getSubSystemId())
                        .queryParam("devDept", scheduledWorkflowBusiness.getDevDepartmentId())
                        .queryParam("opsDept", scheduledWorkflowBusiness.getOpsDepartmentId())
                        .queryParam("planStartTime", URLEncoder.encode(scheduledWorkflowBusiness.getPlanStartTime(), "UTF-8"))
                        .queryParam("planFinishTime", URLEncoder.encode(scheduledWorkflowBusiness.getPlanFinishTime(), "UTF-8"))
                        .queryParam("lastStartTime", URLEncoder.encode(scheduledWorkflowBusiness.getLastStartTime(), "UTF-8"))
                        .queryParam("lastFinishTime", URLEncoder.encode(scheduledWorkflowBusiness.getLastFinishTime(), "UTF-8"))
                        .queryParam("scanPartitionNum", 1)
                        .queryParam("scanDataSize", 1)
                        .queryParam("itsmNo", itsmNo)
                        .toString();
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                LOGGER.info("Ready to submit business info of workflow [{}] to WTSS. request url: {}", scheduledWorkflow.getName(), url);
                Map<String, Object> responseMap = restTemplate.exchange(new URI(url), HttpMethod.POST, new HttpEntity<>(headers), Map.class).getBody();
                if (responseMap.containsKey("errorMsg") && StringUtils.isNotBlank(responseMap.get("errorMsg").toString())) {
                    throw new UnExpectedRequestException(responseMap.get("errorMsg").toString());
                }
                LOGGER.info("Submitted business info of workflow [{}] to WTSS.", scheduledWorkflow.getName());
            }
        } catch (
                URISyntaxException e) {
            LOGGER.error("Failed to submit workflow business. error: {}", e.getMessage());
        } catch (
                UnsupportedEncodingException e) {
            LOGGER.error("Failed to submit workflow business. error: {}", e.getMessage());
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to submit workflow business. error: {}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     *
     * @param ruleGroupIds
     * @param map
     * @return  key: ruleGroupId ; value: "0,createUser,proxyUser"
     * @throws UnExpectedRequestException
     */
    private Map<Long, String> relationRuleGroupConfig(List<Long> ruleGroupIds, Map<Long, String> map) throws UnExpectedRequestException {
        List<RuleGroup> ruleGroupLists = ruleGroupDao.findByIds(ruleGroupIds);
        for (RuleGroup ruleGroup : ruleGroupLists) {
            List<Rule> ruleList = ruleDao.findByRuleGroup(ruleGroup);
            if (CollectionUtils.isEmpty(ruleList)) {
                throw new UnExpectedRequestException("No rules were found.");
            }
            List<Long> ruleIds = ruleList.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<RuleDataSource> ruleDataSourceList = ruleDataSourceDao.findByRuleIds(ruleIds);
            if (CollectionUtils.isEmpty(ruleDataSourceList)) {
                throw new UnExpectedRequestException("The dataSource of rule {&DOES_NOT_EXIST}.");
            }

            if (!checkIfCreatedBySameUser(ruleList)) {
                throw new UnExpectedRequestException("The rules were not created by the same user.");
            }

            boolean containEmptyProxyUser = ruleDataSourceList.stream().anyMatch(m -> null == m.getProxyUser() || "".equals(m.getProxyUser()));
            if (!containEmptyProxyUser && !checkIfDesignatedBySameProxyUser(ruleDataSourceList)) {
                throw new UnExpectedRequestException("The rule dataSources were not designated by the same proxy user.");
            }

            String createUser = ruleList.get(0).getCreateUser();
            String proxyUser = containEmptyProxyUser ? createUser : ruleDataSourceList.get(0).getProxyUser();

            String nonAbortValue = "0," + createUser + "," + proxyUser;
            String abortValue = "1," + createUser + "," + proxyUser;

            map.put(ruleGroup.getId(), nonAbortValue);
            for (Rule rule : ruleList) {
                if (StringUtils.isBlank(rule.getExecutionParametersName())) {
                    if (rule.getAbortOnFailure()) {
                        map.put(ruleGroup.getId(), abortValue);
                        break;
                    }
                } else {
                    ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                    if (executionParameters == null) {
                        throw new UnExpectedRequestException("executionParameters {&DOES_NOT_EXIST}");
                    }
                    if (executionParameters.getAbortOnFailure()) {
                        map.put(ruleGroup.getId(), abortValue);
                        break;
                    }

                }

            }

        }
        return map;
    }

    private boolean checkIfCreatedBySameUser(List<Rule> ruleList) {
        List<Rule> ruleCollect = ruleList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(u -> u.getCreateUser()))), ArrayList::new));
        return ruleCollect.size() == 1;
    }

    private boolean checkIfDesignatedBySameProxyUser(List<RuleDataSource> ruleDataSourceList) {
        List<RuleDataSource> ruleDataSourceCollect = ruleDataSourceList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(u -> u.getProxyUser()))), ArrayList::new));
        return ruleDataSourceCollect.size() == 1;
    }

    @Override
    public void deleteIntervalJob(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException {
        Long scheduleId = wtssScheduledJobRequest.getScheduleId();
        String uri = address + "/schedule?session.id=" + sessionId;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("action", "removeSched");
        requestMap.add("scheduleId", scheduleId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity httpEntity = new HttpEntity(requestMap, httpHeaders);

        try {
            LOGGER.info("deleteIntervalJob, scheduleProject: {}, scheduleWorkflow: {}, scheduleId:{}", wtssScheduledJobRequest.getProjectName(), wtssScheduledJobRequest.getScheduleName(), scheduleId);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);
            LOGGER.info("deleteIntervalJob, resp:{}", responseEntity);
            if (HttpStatus.OK.value() != responseEntity.getStatusCodeValue()) {
                throw new ScheduledPushFailedException("Error!network occurred an unexpected error");
            }
            String body = responseEntity.getBody();
            if (StringUtils.isEmpty(body)) {
                throw new ScheduledPushFailedException("Error!response body is empty");
            }
            Map<String, Object> responseMap = new Gson().fromJson(body, Map.class);
            if ("error".equals(responseMap.get("status"))) {
                String message = responseMap.containsKey("message") ? responseMap.get("message").toString() : "Failed to delete schedule";
                throw new ScheduledPushFailedException(message);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete schedule to WTSS", e);
            throw new ScheduledPushFailedException(e.getMessage());
        }
    }

    private File generateZip(Map<String, List<WtssScheduledJobRequest>> workflowAndTaskListMap, String projectName, Map<String, Object> jsonServerMap) throws IOException, UnExpectedRequestException {
        String homePath = linkisConfig.getUploadTmpPath() + File.separator + "job";
        projectName = projectName.replace(SpecCharEnum.PERIOD_NO_ESCAPE.getValue(), "");
        File directory = new File(homePath + File.separator + projectName);
        Map<String, Object> propertiesMap = (Map<String, Object>) jsonServerMap.get("properties");
        Map<String, Object> jobMap = (Map<String, Object>) jsonServerMap.get("job");
        try {
            if (!directory.exists()) {
                boolean isCreated = directory.mkdirs();
                if (!isCreated) {
                    throw new IOException("Failed to create project directory");
                }
            }
            for (String workflowName : workflowAndTaskListMap.keySet()) {
                List<WtssScheduledJobRequest> wtssScheduledJobRequestList = workflowAndTaskListMap.get(workflowName);
                String filePrefix = directory.getAbsolutePath() + File.separator;
                for (WtssScheduledJobRequest wtssScheduledJobRequest : wtssScheduledJobRequestList) {
                    if (ScheduledTaskPushService.NODE_LEVEL_TASK.equals(wtssScheduledJobRequest.getNodeLevel())) {
                        String propertiesName = clipRuleGroupName(wtssScheduledJobRequest.getScheduleName());
                        String filePath = filePrefix + propertiesName + ".job";
                        String propertiesFilePath = filePrefix + propertiesName + ".properties";
                        File propertiesFile = createFile(propertiesFilePath, getPropertiesContent(wtssScheduledJobRequest, propertiesMap));
                        createFile(filePath, generateScheduleJobContent(wtssScheduledJobRequest, propertiesFile.getName(), jobMap));
                    } else {
                        String filePath = filePrefix + wtssScheduledJobRequest.getScheduleName() + ".job";
                        createFile(filePath, generateScheduleJobContent(wtssScheduledJobRequest, StringUtils.EMPTY, jobMap));
                    }
                }
            }
            String filePath = homePath + File.separator + directory.getName() + ".zip";
            File zipFile = new File(filePath);
            if (zipFile.exists()) {
                Files.delete(zipFile.toPath());
            }
            ZipFile zip = new ZipFile(zipFile);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zip.createSplitZipFileFromFolder(directory, zipParameters, true, 65536);
            return zip.getFile();
        } catch (ZipException e) {
            LOGGER.error("Failed to create zip file", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("File is not exists", e);
        } catch (IOException e) {
            LOGGER.error("IO Exception", e);
        } finally {
            deleteDirectoryAndFiles(directory);
        }
        return null;
    }

    private String generateScheduleJobContent(WtssScheduledJobRequest wtssScheduledJobRequest, String propertiesFileName, Map<String, Object> jobMap) {
        Map<String, Object> jobTmpMap = Collections.emptyMap();
        if (ScheduledTaskPushService.NODE_LEVEL_FLOW.equals(wtssScheduledJobRequest.getNodeLevel())) {
            jobTmpMap = Maps.newHashMapWithExpectedSize(3);
            jobTmpMap.put("user.to.proxy", wtssScheduledJobRequest.getProxyUser());
            jobTmpMap.put("type", "command");
            jobTmpMap.put("dependencies", wtssScheduledJobRequest.getDependencies());
        } else if (ScheduledTaskPushService.NODE_LEVEL_TASK.equals(wtssScheduledJobRequest.getNodeLevel())) {
            jobTmpMap = Maps.newHashMapWithExpectedSize(jobMap.size() + 3);
            jobTmpMap.put("user.to.proxy", wtssScheduledJobRequest.getProxyUser());
            Long ruleGroupId = wtssScheduledJobRequest.getRuleGroupId();
            if (Objects.nonNull(ruleGroupId)) {
                jobTmpMap.put("command", generateCommandContent(ruleGroupId, propertiesFileName));
            }
            if (StringUtils.isNotBlank(wtssScheduledJobRequest.getDependencies())) {
                jobTmpMap.put("dependencies", wtssScheduledJobRequest.getDependencies());
            }
            jobTmpMap.putAll(jobMap);
            jobTmpMap.remove("dqm.server.ip");
        } else if (ScheduledTaskPushService.NODE_LEVEL_SIGNAL.equals(wtssScheduledJobRequest.getNodeLevel())) {
            Map<String, Object> signalParameterMap = wtssScheduledJobRequest.getSignalParameterMap();
            jobTmpMap = Maps.newHashMapWithExpectedSize(signalParameterMap.size() + 1);
            if (StringUtils.isNotBlank(wtssScheduledJobRequest.getDependencies())) {
                signalParameterMap.put("dependencies", wtssScheduledJobRequest.getDependencies());
            }
            jobTmpMap.putAll(signalParameterMap);
        }
        return mapToString(jobTmpMap);
    }

    public String generateCommandContent(Long ruleGroupId, String propertiesFileName) {
        RuleGroup ruleGroup = ruleGroupDao.findById(ruleGroupId);
        List<Rule> ruleList = ruleDao.findByRuleGroup(ruleGroup);
        if (CollectionUtils.isEmpty(ruleList)) {
            return StringUtils.EMPTY;
        }
        List<ExecutionParameters> executionParametersList = new ArrayList<>();
        for (Rule rule : ruleList) {
            if (StringUtils.isBlank(rule.getExecutionParametersName())) {
                continue;
            }
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), ruleGroup.getProjectId());
            if (executionParameters != null) {
                executionParametersList.add(executionParameters);
            }
        }
        List<Long> ruleIdList = ruleList.stream().map(Rule::getId).collect(Collectors.toList());
        String ruleIdStr = StringUtils.join(ruleIdList, SpecCharEnum.COMMA.getValue());
        String abortMark = StringUtils.EMPTY;
        boolean aborted = ruleList.stream().filter(Objects::nonNull).filter(rule -> Boolean.TRUE.equals(rule.getAbortOnFailure())).count() > 0
                || executionParametersList.stream().filter(executionParameters -> Boolean.TRUE.equals(executionParameters.getAbortOnFailure())).count() > 0;
        if (!aborted) {
            abortMark = "--async true";
        }
        return String.format(RULE_EXECUTE_COMMAND_FORMAT, propertiesFileName, ruleIdStr, abortMark);
    }

    private String getPropertiesContent(WtssScheduledJobRequest wtssScheduledJobRequest, Map<String, Object> propertiesMap) {
        propertiesMap.put("TssJob.user", wtssScheduledJobRequest.getProxyUser());
        propertiesMap.put("TssJob.password", wtssScheduledJobRequest.getJobRsa());
        return mapToString(propertiesMap);
    }

    private String mapToString(Map<String, Object> map) {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            content.append(entry.getKey());
            content.append(SpecCharEnum.EQUAL.getValue());
            content.append(entry.getValue());
            content.append(SpecCharEnum.LINE.getValue());
        }
        return content.toString();
    }

    private File createFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
        boolean newFile = file.createNewFile();
        if (!newFile) {
            LOGGER.error("Failed to create file");
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
            fileWriter.flush();
        }
        return file;
    }

    private void deleteDirectoryAndFiles(File directory) throws IOException {
        if (!directory.isDirectory()) {
            LOGGER.error("The target is not a directory");
            return;
        }
        File[] files = directory.listFiles();
        for (int i = 0, j = files.length; i < j; i++) {
            File file = files[i];
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        }
        if (directory.exists()) {
            Files.delete(directory.toPath());
        }
    }

}
