package com.webank.wedatasphere.qualitis.scheduled.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.constants.ThirdPartyConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleGroupTypeEnum;
//import com.webank.wedatasphere.qualitis.scheduled.constant.ScheduledSystemTypeEnum;
//import com.webank.wedatasphere.qualitis.scheduled.constant.ScheduledTaskTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledFrontBackRuleDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledTaskDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowTaskRelationDao;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledOperateHistory;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.*;
import com.webank.wedatasphere.qualitis.scheduled.response.*;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledProjectService;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskPushService;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskService;
import com.webank.wedatasphere.qualitis.scheduled.util.ScheduledGetSessionUtil;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    @Autowired
    private ScheduledTaskDao scheduledTasksDao;
    @Autowired
    private ScheduledFrontBackRuleDao scheduledFrontBackRuleDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private ScheduledWorkflowTaskRelationDao scheduledWorkflowTaskRelationDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ScheduledProjectService scheduledProjectService;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private ScheduledOperateHistoryDao scheduledOperateHistoryDao;
    @Resource(name = "wtssScheduledPushService")
    private ScheduledTaskPushService scheduledTaskPushService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);

    private final Integer OPERATE_TYPE_RELEASE = 1;
    private final Integer OPERATE_TYPE_DELETE = 2;
    private final Integer PROGRESS_STATUS_ENDED = 2;
    private final Integer PROGRESS_STATUS_FAILED = 3;
    private final Integer TASK_RELEASE_STATUS_NO = 0;
    private final Integer TASK_RELEASE_STATUS_SUCCESS = 1;
    private final Integer TASK_RELEASE_STATUS_FAILED = 2;

    private HttpServletRequest httpServletRequest;

    public ScheduledTaskServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse addScheduledTask(AddScheduledRelationProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        request.checkRequest();
//        List<AddScheduledRelationTaskRequest> taskList = request.getTaskList();
//        validateAddScheduledTask(request.getCluster(), request.getScheduleSystem(), request.getProjectName(), taskList);
//
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//        // Check existence of project
//        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
//        // Check permissions of project
//        List<Integer> permissions = new ArrayList<>();
//        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
//        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
//
//        for (AddScheduledRelationTaskRequest addScheduledRelationTaskRequest : taskList) {
//            ScheduledTask scheduledTask = new ScheduledTask();
//            scheduledTask.setDispatchingSystemType(request.getScheduleSystem());
//            scheduledTask.setClusterName(request.getCluster());
//            scheduledTask.setProjectName(request.getProjectName());
//            scheduledTask.setTaskType(ScheduledTaskTypeEnum.RELATION.getCode());
//            scheduledTask.setProject(projectInDb);
//            if (StringUtils.isNotBlank(request.getReleaseUser())) {
//                scheduledTask.setReleaseUser(request.getReleaseUser());
//            }
//            scheduledTask.setWorkFlowName(addScheduledRelationTaskRequest.getWorkFlow());
//            scheduledTask.setTaskName(addScheduledRelationTaskRequest.getTaskName());
//            scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_NO);
//            scheduledTask.setCreateTime(DateUtils.now());
//            scheduledTask.setCreateUser(loginUser);
//            scheduledTask = scheduledTasksDao.saveScheduledTask(scheduledTask);
//            handleBatchScheduled(scheduledTask, addScheduledRelationTaskRequest.getFrontRuleGroup(), addScheduledRelationTaskRequest.getBackRuleGroup());
//        }

        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<Object> modifyScheduledTask(ModifyScheduledRelationProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        request.checkRequest();
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//
//        ScheduledTask scheduledTask = scheduledTasksDao.findById(request.getScheduledTaskId());
//        if (scheduledTask == null) {
//            throw new UnExpectedRequestException("scheduled_task_id [" + request.getScheduledTaskId() + "] {&DOES_NOT_EXIST}");
//        }
//        // Check existence of project
//        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
//        // Check permissions of project
//        List<Integer> permissions = new ArrayList<>();
//        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
//        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
//        List<ScheduledFrontBackRule> list = scheduledFrontBackRuleDao.findByScheduledTask(scheduledTask);
//        if (CollectionUtils.isEmpty(list)) {
//            throw new UnExpectedRequestException("ScheduledFrontBackRule [" + scheduledTask.getId() + "] {&DOES_NOT_EXIST}");
//        }
//
//        scheduledFrontBackRuleDao.deleteAllScheduledFrontBackRule(list);
//
//        handleBatchScheduled(scheduledTask, request.getFrontRuleGroup(), request.getBackRuleGroup());
//
//        scheduledTask.setDispatchingSystemType(request.getScheduleSystem());
//        scheduledTask.setClusterName(request.getCluster());
//        scheduledTask.setProjectName(request.getProjectName());
//        scheduledTask.setWorkFlowName(request.getWorkFlow());
//        scheduledTask.setTaskName(request.getTaskName());
//        scheduledTask.setTaskType(ScheduledTaskTypeEnum.RELATION.getCode());
//        scheduledTask.setProject(projectInDb);
//        scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_NO);
//        scheduledTask.setReleaseUser(request.getReleaseUser());
//        scheduledTask.setModifyUser(loginUser);
//        scheduledTask.setModifyTime(DateUtils.now());
//        scheduledTasksDao.saveScheduledTask(scheduledTask);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", null);
    }

//    public void validateAddScheduledTask(String clusterName, String dispatchingSystemType, String wtssProjectName, List<AddScheduledRelationTaskRequest> taskList) throws UnExpectedRequestException {
//        for (AddScheduledRelationTaskRequest addScheduledRelationTaskRequest : taskList) {
//            ScheduledTask scheduledTask = scheduledTasksDao.findOnlyObject(clusterName, dispatchingSystemType, wtssProjectName, addScheduledRelationTaskRequest.getWorkFlow(), addScheduledRelationTaskRequest.getTaskName(), ScheduledTaskTypeEnum.RELATION.getCode());
//            if (scheduledTask != null) {
//                throw new UnExpectedRequestException("{&SCHEDULED_TASK_ALREADY_EXIST}");
//            }
//        }
//        Long taskNameNum = taskList.stream().map(AddScheduledRelationTaskRequest::getTaskName).distinct().count();
//        if (taskNameNum.intValue() < taskList.size()) {
//            throw new UnExpectedRequestException("Duplicated task name!");
//        }
//    }

    private String getSessionIdByReleaseUser(String releaseUser, String cluster) throws UnExpectedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        String operationUser = StringUtils.isNotBlank(releaseUser) ? releaseUser : loginUser;
        return ScheduledGetSessionUtil.getSessionId(operationUser, loginUser, cluster);
    }

    private Map<String, Object> synchroToWtss(String cluster, String projectName, String workFlow, String taskName
            , List<Long> frontRuleGroup, List<Long> backRuleGroup, String releaseUser) throws UnExpectedRequestException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(cluster);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Failed to find cluster_info named [" + cluster + "]");
        }
        if (StringUtils.isBlank(clusterInfo.getHiveUrn())) {
            throw new UnExpectedRequestException("Failed to find Hive-Urn config [" + cluster + "]");
        }
        String serverAddress = getServerAddress(clusterInfo);
        if (StringUtils.isBlank(serverAddress)) {
            throw new UnExpectedRequestException("Lack of server address of WTSS");
        }
        String sessionId = getSessionIdByReleaseUser(releaseUser, cluster);
        LOGGER.info(">>>>>>>>>> Success Get SessionId From Scheduled System:  <<<<<<<<<<" + sessionId);
        return scheduledTaskPushService.synchronousRelationTask(projectName, workFlow, taskName, frontRuleGroup, backRuleGroup, clusterInfo.getHiveUrn(), sessionId, serverAddress);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<Object> frontAndBackRuleGroup(ModifyRuleGroupRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
//        ModifyRuleGroupRequest.checkRequest(request);
//        ScheduledTask scheduledTask = scheduledTasksDao.findOnlyObject(request.getCluster(), request.getScheduleSystem(), request.getProjectName(), request.getWorkFlow(), request.getTaskName(), ScheduledTaskTypeEnum.RELATION.getCode());
//        if (scheduledTask == null) {
//            throw new UnExpectedRequestException("scheduledTask {&DOES_NOT_EXIST}");
//        }
//        ScheduledFrontBackRule scheduledFrontBackRule = scheduledFrontBackRuleDao.findById(request.getScheduledTaskFrontBackId());
//        if (scheduledFrontBackRule == null) {
//            throw new UnExpectedRequestException("scheduled_task_front_back_id [" + request.getScheduledTaskFrontBackId() + "] {&DOES_NOT_EXIST}");
//        }
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//        // Check existence of project
//        Project projectInDb = projectService.checkProjectExistence(scheduledFrontBackRule.getScheduledTask().getProject().getId(), loginUser);
//        // Check permissions of project
//        List<Integer> permissions = new ArrayList<>();
//        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
//        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
//
//        if (scheduledTask.getId().toString().equals(scheduledFrontBackRule.getScheduledTask().getId().toString())) {
//            scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_NO);
//            scheduledTask.setReleaseUser(request.getReleaseUser());
//            scheduledTask.setModifyUser(loginUser);
//            scheduledTask.setModifyTime(DateUtils.now());
//            scheduledTasksDao.saveScheduledTask(scheduledTask);
//            scheduledFrontBackRuleDao.saveScheduledFrontBackRule(scheduledFrontBackRule);
//            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ASSOCIATED_SCHEDULING_TO_WTSS_SUCCESSFULLY}", null);
//        } else {
//            //将表格的关联任务的规则组(前置与后置)删除，并将规则组追加到查询出来的关联任务里，保证规则组是关联任务唯一的
//            List<ScheduledFrontBackRule> byScheduledTask = scheduledFrontBackRuleDao.findByScheduledTask(scheduledFrontBackRule.getScheduledTask());
//            if (CollectionUtils.isEmpty(byScheduledTask)) {
//                throw new UnExpectedRequestException("ScheduledTask [" + scheduledFrontBackRule.getScheduledTask().getId() + "] {&DOES_NOT_EXIST}");
//            }
//            List<ScheduledFrontBackRule> accordObject = byScheduledTask.stream().filter(x -> x.getRuleGroup().getId().toString().equals(scheduledFrontBackRule.getRuleGroup().getId().toString())).collect(Collectors.toList());
//
//            List<ScheduledFrontBackRule> goodVos = byScheduledTask.stream().filter(p ->
//                    !accordObject.stream()
//                            .map(b -> b.getId()).collect(Collectors.toList())
//                            .contains(p.getId())).collect(Collectors.toList());
//
//            List<ScheduledFrontBackRule> frontRule = goodVos.stream().filter(x -> ThirdPartyConstants.WTSS_FRONT_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
//            List<ScheduledFrontBackRule> backRule = goodVos.stream().filter(x -> ThirdPartyConstants.WTSS_BACK_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
//            List<Long> frontCollect = frontRule.stream().map(x -> x.getRuleGroup().getId()).collect(Collectors.toList());
//            List<Long> backCollect = backRule.stream().map(x -> x.getRuleGroup().getId()).collect(Collectors.toList());
//
//            //查询关联任务下无规则组，就删除关联任务
//            if (CollectionUtils.isEmpty(goodVos)) {
//                scheduledTasksDao.deleteScheduledTask(scheduledFrontBackRule.getScheduledTask());
//            } else {
//                scheduledFrontBackRuleDao.deleteAllScheduledFrontBackRule(accordObject);
//            }
//
//            Map<String, Object> stringObjectMap = synchroToWtss(scheduledFrontBackRule.getScheduledTask().getClusterName(), scheduledFrontBackRule.getScheduledTask().getProjectName(), scheduledFrontBackRule.getScheduledTask().getWorkFlowName(), scheduledFrontBackRule.getScheduledTask().getTaskName(),
//                    frontCollect, backCollect, scheduledFrontBackRule.getScheduledTask().getReleaseUser());
//
//            if (ThirdPartyConstants.WTSS_STATUS_CODE.equals(stringObjectMap.get(ThirdPartyConstants.WTSS_RESPONSE_CODE).toString())) {
//                List<ScheduledFrontBackRule> queryScheduledTask = scheduledFrontBackRuleDao.findByScheduledTask(scheduledTask);
//                if (CollectionUtils.isEmpty(queryScheduledTask)) {
//                    throw new UnExpectedRequestException("ScheduledTask [" + scheduledTask.getId() + "] {&DOES_NOT_EXIST}");
//                }
//
//                List<ScheduledFrontBackRule> frontGroup = queryScheduledTask.stream().filter(x -> ThirdPartyConstants.WTSS_FRONT_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
//                List<Long> frontGroupCollect = frontGroup.stream().map(x -> x.getRuleGroup().getId()).collect(Collectors.toList());
//                List<ScheduledFrontBackRule> backGroup = queryScheduledTask.stream().filter(x -> ThirdPartyConstants.WTSS_BACK_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
//                List<Long> backGroupCollect = backGroup.stream().map(x -> x.getRuleGroup().getId()).collect(Collectors.toList());
//
//                if (ThirdPartyConstants.WTSS_FRONT_TYPE.equals(scheduledFrontBackRule.getTriggerType().toString())) {
//                    frontGroupCollect.add(scheduledFrontBackRule.getRuleGroup().getId());
//                } else if (ThirdPartyConstants.WTSS_BACK_TYPE.equals(scheduledFrontBackRule.getTriggerType().toString())) {
//                    backGroupCollect.add(scheduledFrontBackRule.getRuleGroup().getId());
//                }
//
//                scheduledFrontBackRuleDao.deleteAllScheduledFrontBackRule(queryScheduledTask);
//
//                handleBatchScheduled(scheduledTask, frontGroupCollect, backGroupCollect);
//                scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_NO);
//                scheduledTask.setReleaseUser(request.getReleaseUser());
//                scheduledTask.setModifyUser(loginUser);
//                scheduledTask.setModifyTime(DateUtils.now());
//                return new GeneralResponse<>(ResponseStatusConstants.OK, "success", null);
//
//            } else {
//                throw new UnExpectedRequestException("Failed to modify ScheduledTask, Error log:" + stringObjectMap.get("error").toString());
//
//            }
//
//        }
        return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "", null);
    }

    private void handleBatchScheduled(ScheduledTask scheduledTask, List<Long> frontRuleGroup, List<Long> backRuleGroup) throws UnExpectedRequestException {
        List<Long> longList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(frontRuleGroup)) {
            longList.addAll(frontRuleGroup);
        }
        if (CollectionUtils.isNotEmpty(backRuleGroup)) {
            longList.addAll(backRuleGroup);
        }

        if (scheduledWorkflowTaskRelationDao.isExists(longList) || scheduledFrontBackRuleDao.isExists(longList)) {
            throw new UnExpectedRequestException("规则组已关联定时任务或调度任务，操作失败");
        }

        if (CollectionUtils.isNotEmpty(frontRuleGroup)) {
            List<ScheduledFrontBackRule> scheduledFrontRuleList = handleScheduledRule(scheduledTask, frontRuleGroup, RuleGroupTypeEnum.FRONT_RULE_GROUP);
            //前置规则组
            scheduledFrontBackRuleDao.saveAll(scheduledFrontRuleList);
        }

        if (CollectionUtils.isNotEmpty(backRuleGroup)) {
            List<ScheduledFrontBackRule> scheduledBackRuleList = handleScheduledRule(scheduledTask, backRuleGroup, RuleGroupTypeEnum.BACK_RULE_GROUP);
            //后置规则组
            scheduledFrontBackRuleDao.saveAll(scheduledBackRuleList);
        }

    }

    private List<ScheduledFrontBackRule> handleScheduledRule(ScheduledTask scheduledTask, List<Long> frontRuleGroup, RuleGroupTypeEnum ruleGroupType) {
        return frontRuleGroup.stream().map(ruleGroupId ->
        {
            ScheduledFrontBackRule scheduledFrontBackRule = new ScheduledFrontBackRule();
            RuleGroup ruleGroup = ruleGroupDao.findById(ruleGroupId);
            scheduledFrontBackRule.setRuleGroup(ruleGroup);
            scheduledFrontBackRule.setTriggerType(ruleGroupType.getCode());
            scheduledFrontBackRule.setScheduledTask(scheduledTask);
            return scheduledFrontBackRule;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<Object> deleteScheduledTask(DeleteScheduledRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {

        DeleteScheduledRequest.checkRequest(request);
        ScheduledFrontBackRule scheduledFrontBackRule = scheduledFrontBackRuleDao.findById(request.getScheduledTaskFrontBackId());
        if (scheduledFrontBackRule == null) {
            throw new UnExpectedRequestException("scheduled_task_front_back_id [" + request.getScheduledTaskFrontBackId() + "] {&DOES_NOT_EXIST}");
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        ScheduledTask scheduledTask = scheduledTasksDao.findById(scheduledFrontBackRule.getScheduledTask().getId());
        if (scheduledTask == null) {
            throw new UnExpectedRequestException("scheduled_task_id [" + scheduledFrontBackRule.getScheduledTask().getId() + "] {&DOES_NOT_EXIST}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(scheduledTask.getProject().getId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        //查询所有
        List<ScheduledFrontBackRule> byScheduledTask = scheduledFrontBackRuleDao.findByScheduledTask(scheduledTask);
        if (CollectionUtils.isEmpty(byScheduledTask)) {
            throw new UnExpectedRequestException("ScheduledFrontBackRule [" + scheduledTask.getId() + "] {&DOES_NOT_EXIST}");
        }
        List<ScheduledFrontBackRule> frontRule = byScheduledTask.stream().filter(x -> ThirdPartyConstants.WTSS_FRONT_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
        List<Long> frontCollect = frontRule.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<ScheduledFrontBackRule> backRule = byScheduledTask.stream().filter(x -> ThirdPartyConstants.WTSS_BACK_TYPE.equals(x.getTriggerType().toString())).collect(Collectors.toList());
        List<Long> backCollect = backRule.stream().map(x -> x.getId()).collect(Collectors.toList());

        List<Long> frontResult = Lists.newArrayList();
        List<Long> backResult = Lists.newArrayList();

        if (ThirdPartyConstants.WTSS_FRONT_TYPE.equals(scheduledFrontBackRule.getTriggerType().toString())) {
            //前置规则组
            handleScheduledFrontBackRule(scheduledFrontBackRule, frontCollect, frontResult);
            //后置规则组
            getRuleGroupId(backCollect, backResult);
        } else if (ThirdPartyConstants.WTSS_BACK_TYPE.equals(scheduledFrontBackRule.getTriggerType().toString())) {
            //前置规则组
            handleScheduledFrontBackRule(scheduledFrontBackRule, backCollect, backResult);
            //后置规则组
            getRuleGroupId(frontCollect, frontResult);
        }

        ScheduledOperateHistory scheduledOperateHistory = new ScheduledOperateHistory();
        try {
            Map<String, Object> stringObjectMap = synchroToWtss(scheduledTask.getClusterName(), scheduledTask.getProjectName(), scheduledTask.getWorkFlowName(), scheduledTask.getTaskName(),
                    frontResult, backResult, request.getReleaseUser());
            if (ThirdPartyConstants.WTSS_STATUS_CODE.equals(stringObjectMap.get(ThirdPartyConstants.WTSS_RESPONSE_CODE).toString())) {
                List<ScheduledFrontBackRule> collect = byScheduledTask.stream().filter(x -> !x.getId().toString().equals(scheduledFrontBackRule.getId().toString())).collect(Collectors.toList());
                //查询关联任务下无规则组，就删除关联任务
                scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_ENDED);
                return getScheduledTaskResponseGeneralResponse(scheduledFrontBackRule, scheduledTask, collect);
            } else {
                scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
                throw new UnExpectedRequestException("Failed to delete ScheduledTask, Error log:" + stringObjectMap.get("error").toString());
            }
        } catch (UnExpectedRequestException e) {
            scheduledOperateHistory.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            scheduledOperateHistory.setDispatchingSystemType(scheduledTask.getDispatchingSystemType());
            scheduledOperateHistory.setClusterName(scheduledTask.getClusterName());
            scheduledOperateHistory.setTaskType(scheduledTask.getTaskType());
            scheduledOperateHistory.setWtssProjectName(scheduledTask.getProjectName());
            scheduledOperateHistory.setWorkFlowName(scheduledTask.getWorkFlowName());
            scheduledOperateHistory.setTaskName(scheduledTask.getTaskName());
            scheduledOperateHistory.setOperateType(OPERATE_TYPE_DELETE);
            scheduledOperateHistory.setCreateUser(HttpUtils.getUserName(httpServletRequest));
            scheduledOperateHistory.setCreateTime(DateUtils.now());
            scheduledOperateHistoryDao.add(scheduledOperateHistory);
        }

    }

    private void getRuleGroupId(List<Long> backCollect, List<Long> backResult) {
        if (CollectionUtils.isNotEmpty(backCollect)) {
            List<ScheduledFrontBackRule> allById = scheduledFrontBackRuleDao.findAllById(backCollect);
            for (ScheduledFrontBackRule frontBackRule : allById) {
                backResult.add(frontBackRule.getRuleGroup().getId());
            }
        }
    }

    private void handleScheduledFrontBackRule(ScheduledFrontBackRule scheduledFrontBackRule, List<Long> frontCollect, List<Long> frontResult) {
        List<Long> collectFront = frontCollect.stream().filter(x -> !x.equals(scheduledFrontBackRule.getId())).collect(Collectors.toList());
        getRuleGroupId(collectFront, frontResult);
    }

    @Override
    public GeneralResponse<GetAllResponse<ScheduledTaskResponse>> getAllScheduledTask(ScheduledTaskRequest request) throws UnExpectedRequestException {
        ScheduledTaskRequest.checkRequest(request);
        //调度系统类型、项目、工作流、规则组、库、表、集群
        Set<Long> list = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(request.getRuleGroupId())) {
            for (Long s : request.getRuleGroupId()) {
                list.add(s);
            }
        }
        List<ScheduledFrontBackRule> mapLists = scheduledTasksDao.filterPage(request.getScheduleSystem(), request.getWtssProjectName(), request.getWorkFlow(),
                request.getTaskName(), list.size() == 0 ? null : list, request.getDbName(), request.getTableName(), request.getPage(), request.getSize(), request.getTaskType(), request.getProjectId(), request.getCluster());

        Long total = scheduledTasksDao.countAllScheduledTask(request.getScheduleSystem(), request.getWtssProjectName(), request.getWorkFlow(),
                request.getTaskName(), list.size() == 0 ? null : list, request.getDbName(), request.getTableName(), request.getTaskType(), request.getProjectId(), request.getCluster());

        GetAllResponse<ScheduledTaskResponse> response = new GetAllResponse<>();
        List<ScheduledTaskResponse> responseList = new ArrayList<>();

        List<ScheduledFrontBackRule> collect = mapLists.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(u -> u.getScheduledTask().getId()))), ArrayList::new));

        for (ScheduledFrontBackRule temp : collect) {
            List<ScheduledFrontBackRule> front = mapLists.stream().filter(x -> x.getScheduledTask().getId().equals(temp.getScheduledTask().getId())).collect(Collectors.toList());
            ScheduledTaskResponse scheduledTaskResponse = new ScheduledTaskResponse(temp.getScheduledTask(), front);
            responseList.add(scheduledTaskResponse);
        }

        response.setTotal(total);
        response.setData(responseList);

        LOGGER.info("Succeed to find ScheduledTask. response: {}", response);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SCHEDULED_TASK_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<ScheduledTaskResponse> getScheduledTaskDetail(Long scheduledTaskFrontBackId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        ScheduledFrontBackRule scheduledFrontBackRule = scheduledFrontBackRuleDao.findById(scheduledTaskFrontBackId);
        if (scheduledFrontBackRule == null) {
            throw new UnExpectedRequestException("scheduled_task_front_back_id [" + scheduledTaskFrontBackId + "] {&DOES_NOT_EXIST}");
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(scheduledFrontBackRule.getScheduledTask().getProject().getId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        ScheduledTaskResponse response = new ScheduledTaskResponse(scheduledFrontBackRule);
        LOGGER.info("Succeed to get scheduledTask detail. scheduled_task_id: {}", scheduledFrontBackRule.getScheduledTask().getId());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SCHEDULED_TASK_DETAIL_SUCCESSFULLY}", response);

    }

    private GeneralResponse<Object> getScheduledTaskResponseGeneralResponse(ScheduledFrontBackRule scheduledFrontBackRule, ScheduledTask scheduledTask, List<ScheduledFrontBackRule> byScheduledTask) {
        if (CollectionUtils.isEmpty(byScheduledTask)) {
            scheduledTasksDao.deleteScheduledTask(scheduledTask);
        } else {
            List<ScheduledFrontBackRule> scheduledFrontBackRuleList = Lists.newArrayList();
            scheduledFrontBackRuleList.add(scheduledFrontBackRule);
            scheduledFrontBackRuleDao.deleteAllScheduledFrontBackRule(scheduledFrontBackRuleList);
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ASSOCIATED_SCHEDULING_TO_WTSS_SUCCESSFULLY}", null);
    }

    @Override
    public List<Map<String, Object>> getAllApproveEnum() {
//        return ScheduledSystemTypeEnum.getScheduledEnumList();
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Map<String, Object>> getDbAndTableQuery(DataLatitudeRequest request) throws UnExpectedRequestException {
        DataLatitudeRequest.checkRequest(request);
        List<Map<String, Object>> dbAndColumnList = scheduledTasksDao.getDbAndTableQuery(request.getProjectId(), request.getTaskType());
        Map<Object, List<Map<String, Object>>> dbAndColumnsMap = dbAndColumnList.stream().filter(item -> item.get("db_name") != null).collect(Collectors.groupingBy(x -> x.get("db_name")));

        return dbAndColumnsMap.entrySet().stream().map(entry -> {
            Map<String, Object> database = Maps.newHashMapWithExpectedSize(2);
            database.put("db", entry.getKey());
            database.put("table_list", entry.getValue());
            return database;
        }).collect(Collectors.toList());

    }

    @Override
    public GetAllResponse<ScheduledPublishTaskResponse> getPublishScheduledTask(PublishScheduledTaskRequest request) {
        GetAllResponse<ScheduledPublishTaskResponse> allResponse = new GetAllResponse<>();

//        List<Long> ruleGroupIds = null;
//        if (StringUtils.isNotEmpty(request.getDbName()) || StringUtils.isNotEmpty(request.getTableName())) {
//            ruleGroupIds = ruleDataSourceDao.findRuleGroupIds(request.getProjectId(), request.getDbName(), request.getTableName());
//        } else if (Objects.nonNull(request.getRuleGroupId())) {
//            ruleGroupIds = Lists.newArrayList(request.getRuleGroupId());
//        }
//
//        Page<ScheduledTask> scheduledTasks = scheduledTasksDao.filterWithPage(request.getProjectId(), request.getScheduleSystem(), request.getProjectName(), request.getWorkFlow(),
//                request.getTaskName(), ruleGroupIds, request.getClusterName(), ScheduledTaskTypeEnum.PUBLISH.getCode(), request.getPage(), request.getSize());

//        List<ScheduledWorkflowTaskRelation> scheduledWorkflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByScheduledTaskList(scheduledTasks.getContent());
//        Map<Long, ScheduledWorkflowTaskRelation> taskIdMap = scheduledWorkflowTaskRelationList.stream().collect(Collectors.toMap(scheduledWorkflowTaskRelation -> scheduledWorkflowTaskRelation.getScheduledTask().getId(), Function.identity(), (oValue, nValue) -> nValue));
//
//        List<ScheduledPublishTaskResponse> responseList = scheduledTasks.stream()
//                .map(scheduledTask -> new ScheduledPublishTaskResponse(scheduledTask, taskIdMap.get(scheduledTask.getId()))).collect(Collectors.toList());
//        allResponse.setTotal(scheduledTasks.getTotalElements());
//        allResponse.setData(responseList);
        return allResponse;
    }

    @Override
    public List<String> getOptionClusterList(Integer taskType, String scheduleSystem, Long projectId) throws UnExpectedRequestException {
        Project projectInDb = null;
        if (Objects.nonNull(projectId)) {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            // Check existence of project
            projectInDb = projectService.checkProjectExistence(projectId, loginUser);
        }
        return scheduledTasksDao.findOptionClusterList(taskType, scheduleSystem, projectInDb);
    }

    @Override
    public List<String> getOptionProjectList(Integer taskType, String scheduleSystem, String clusterName, Long projectId) throws UnExpectedRequestException {
        Project projectInDb = null;
        if (Objects.nonNull(projectId)) {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            // Check existence of project
            projectInDb = projectService.checkProjectExistence(projectId, loginUser);
        }
        return scheduledTasksDao.findOptionProjectList(taskType, scheduleSystem, clusterName, projectInDb);
    }

    @Override
    public List<String> getOptionWorkflowList(Integer taskType, String scheduleSystem, String clusterName, String projectName, Long projectId) throws UnExpectedRequestException {
        Project projectInDb = null;
        if (Objects.nonNull(projectId)) {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            // Check existence of project
            projectInDb = projectService.checkProjectExistence(projectId, loginUser);
        }
        return scheduledTasksDao.findOptionWorkflowList(taskType, scheduleSystem, clusterName, projectName, projectInDb);
    }

    @Override
    public List<String> getOptionTaskList(Integer taskType, String scheduleSystem, String clusterName, String projectName, String workflowName, Long projectId) throws UnExpectedRequestException {
        Project projectInDb = null;
        if (Objects.nonNull(projectId)) {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            // Check existence of project
            projectInDb = projectService.checkProjectExistence(projectId, loginUser);
        }
        return scheduledTasksDao.findOptionTaskList(taskType, scheduleSystem, clusterName, projectName, workflowName, projectInDb);
    }

    @Override
    public List<SpecialRuleGroupResponse> findRuleGroupScheduledWorkflowTaskRelation(Long projectId) {
        List<Map<String, Object>> list = scheduledWorkflowTaskRelationDao.findRuleGroupMap(projectId);
        List<RuleGroup> ruleGroupList = ruleGroupDao.findByProjectIdAndExistRule(projectId);
        List<RuleGroup> goodVos = ruleGroupList.stream().filter(p ->
                !list.stream()
                        .map(b -> b.get("rule_group_id").toString()).collect(Collectors.toList())
                        .contains(p.getId().toString())).collect(Collectors.toList());
        List<SpecialRuleGroupResponse> objects = Lists.newArrayList();
        for (RuleGroup goodVo : goodVos) {
            SpecialRuleGroupResponse specialRuleGroupResponse = new SpecialRuleGroupResponse();
            specialRuleGroupResponse.setRuleGroupId(goodVo.getId());
            specialRuleGroupResponse.setRuleGroupName(goodVo.getRuleGroupName());
            specialRuleGroupResponse.setUsed(true);
            objects.add(specialRuleGroupResponse);
        }
        List<SpecialRuleGroupResponse> collect = list.stream().map(object -> {
            SpecialRuleGroupResponse objectNew = new SpecialRuleGroupResponse();
            objectNew.setRuleGroupId(Long.parseLong(object.get("rule_group_id").toString()));
            objectNew.setRuleGroupName(object.get("rule_group_name").toString());
            objectNew.setUsed(false);
            return objectNew;
        }).collect(Collectors.toList());
        objects.addAll(collect);
        return objects;
    }

    @Override
    public List<Map<String, Object>> findRuleGroupForTable(TableRuleGroupRequest request) throws UnExpectedRequestException {
        TableRuleGroupRequest.checkRequest(request);
        return scheduledTasksDao.findRuleGroupForTable(request.getProjectId(), request.getTaskType());
    }

    @Override
    public RelationRuleGroupResponse findFrontAndBackData(ScheduledFrontBackRuleRequest request) throws UnExpectedRequestException {
        ScheduledFrontBackRuleRequest.checkRequest(request);
        List<Map<String, Object>> list = scheduledTasksDao.getRelationRuleGroup(request.getCluster(), request.getScheduleSystem(), request.getProjectName(), request.getWorkFlow(), request.getTaskName());
        List<RelationScheduledTaskIdResponse> response = CustomObjectMapper.transMapsToObjects(list, RelationScheduledTaskIdResponse.class);

        long distinctNum = response.stream().map(RelationScheduledTaskIdResponse::getScheduledTaskId).distinct().count();
        if (distinctNum > 1) {
            throw new UnExpectedRequestException("匹配的数据集大小不等1");
        }
        RelationRuleGroupResponse relationRuleGroupResponse = new RelationRuleGroupResponse();
        List<ScheduledTaskFrontAndBackResponse> scheduledTaskFrontAndBacklist = Lists.newArrayList();
        for (RelationScheduledTaskIdResponse relationScheduledTaskIdResponse : response) {
            ScheduledTaskFrontAndBackResponse scheduledTaskFrontAndBackResponse = new ScheduledTaskFrontAndBackResponse();
            scheduledTaskFrontAndBackResponse.setRuleGroupId(relationScheduledTaskIdResponse.getRuleGroupId());
            scheduledTaskFrontAndBackResponse.setRuleGroupName(relationScheduledTaskIdResponse.getRuleGroupName());
            scheduledTaskFrontAndBackResponse.setTriggerType(relationScheduledTaskIdResponse.getTriggerType());
            relationRuleGroupResponse.setScheduledTaskId(relationScheduledTaskIdResponse.getScheduledTaskId());
            relationRuleGroupResponse.setReleaseUser(relationScheduledTaskIdResponse.getReleaseUser());
            scheduledTaskFrontAndBacklist.add(scheduledTaskFrontAndBackResponse);

        }
        relationRuleGroupResponse.setRuleGroupType(scheduledTaskFrontAndBacklist);
        return relationRuleGroupResponse;
    }

    @Override
    public List<Map<String, Object>> findDbsAndTable(Long projectId) {
        List<Map<String, String>> dbAndTableList = scheduledTasksDao.findDbAndTables(projectId);
        Map<Object, List<Map<String, String>>> dbAndTableMap = dbAndTableList.stream().collect(Collectors.groupingBy(stringStringMap -> stringStringMap.get("db_name")));
        return dbAndTableMap.entrySet().stream().map(entry -> {
            Map<String, Object> database = Maps.newHashMapWithExpectedSize(2);
            database.put("db", entry.getKey());
            database.put("table_list", entry.getValue());
            return database;
        }).collect(Collectors.toList());
    }

    @Override
    public TableListResponse findTableListDistinct(DataLatitudeRequest request) throws UnExpectedRequestException {
        DataLatitudeRequest.checkRequest(request);
        List<Map<String, String>> tableListDistinct = scheduledTasksDao.findTableListDistinct(request.getProjectId(), request.getTaskType());
        TableListResponse tableListResponse = new TableListResponse();
        if (CollectionUtils.isNotEmpty(tableListDistinct)) {
            tableListResponse.setCluster_name(tableListDistinct.stream().map(m -> (String) m.get("cluster_name")).collect(Collectors.toSet()));
            tableListResponse.setWtss_project_name(tableListDistinct.stream().map(m -> (String) m.get("wtss_project_name")).collect(Collectors.toSet()));
            tableListResponse.setWtss_task_name(tableListDistinct.stream().map(m -> (String) m.get("wtss_task_name")).collect(Collectors.toSet()));
            tableListResponse.setWtss_work_flow_name(tableListDistinct.stream().map(m -> (String) m.get("wtss_work_flow_name")).collect(Collectors.toSet()));
        }
        return tableListResponse;
    }

    @Override
    public void deleteRelationScheduleTasks(String projectName) throws UnExpectedRequestException {
        //查询所有匹配项目名称的关联任务
//        List<ScheduledTask> matchScheduledTask = scheduledTasksDao.findByProjectNameAndTaskType(projectName, ScheduledTaskTypeEnum.RELATION.getCode());
//        if (CollectionUtils.isNotEmpty(matchScheduledTask)) {
//            //将前后置规则id置空
//            List<Long> longList = Lists.newArrayList();
//            for (ScheduledTask scheduledTask : matchScheduledTask) {
//                Map<String, Object> result = synchroToWtss(scheduledTask.getClusterName(), scheduledTask.getProjectName(), scheduledTask.getWorkFlowName(), scheduledTask.getTaskName(),
//                        longList, longList, scheduledTask.getReleaseUser());
//
//                if (!ThirdPartyConstants.WTSS_STATUS_CODE.equals(result.get(ThirdPartyConstants.WTSS_RESPONSE_CODE).toString())) {
//                    throw new UnExpectedRequestException("Failed to synchronization Scheduled System, Error log:" + result.get("error").toString());
//                }
//            }
//            scheduledTasksDao.deleteScheduledTaskBatch(matchScheduledTask);
//        }
    }

    @Override
    public void release(ScheduledReleaseRequest request) throws Exception {
//        List<ScheduledTask> releaseTaskList = scheduledTasksDao.findByCondition(request.getProjectId(), request.getTaskType(), request.getScheduleSystem()
//                , request.getCluster(), request.getWtssProjectName()
//                , StringUtils.trimToEmpty(request.getWorkFlow()), StringUtils.trimToEmpty(request.getTaskName()), null);
//        if (CollectionUtils.isEmpty(releaseTaskList)) {
//            throw new UnExpectedRequestException("Not found tasks!");
//        }
//        releaseTaskList.forEach(scheduledTask -> scheduledTask.setApproveNumber(request.getApproveNumber()));
//
//        ScheduledOperateHistory scheduledOperateHistory = new ScheduledOperateHistory();
//        try {
//            if (ScheduledTaskTypeEnum.RELATION.getCode().equals(request.getTaskType())) {
//                pushAndUpdateRelationTask(request.getCluster(), releaseTaskList);
//                scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_ENDED);
//            } else if (ScheduledTaskTypeEnum.PUBLISH.getCode().equals(request.getTaskType())) {
//                pushAndUpdatePublishTask(request.getWtssProjectName(), request.getWorkFlow(), releaseTaskList);
//                scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_ENDED);
//            }
//        } catch (UnExpectedRequestException e) {
//            scheduledOperateHistory.setErrorMessage(e.getMessage());
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            throw e;
//        } catch (Exception e) {
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            throw e;
//        } finally {
//            scheduledOperateHistory.setApproveNumber(request.getApproveNumber());
//            scheduledOperateHistory.setDispatchingSystemType(request.getScheduleSystem());
//            scheduledOperateHistory.setClusterName(request.getCluster());
//            scheduledOperateHistory.setTaskType(request.getTaskType());
//            scheduledOperateHistory.setWtssProjectName(request.getWtssProjectName());
//            scheduledOperateHistory.setWorkFlowName(request.getWorkFlow());
//            scheduledOperateHistory.setTaskName(request.getTaskName());
//            scheduledOperateHistory.setOperateType(OPERATE_TYPE_RELEASE);
//            scheduledOperateHistory.setCreateUser(HttpUtils.getUserName(httpServletRequest));
//            scheduledOperateHistory.setCreateTime(DateUtils.now());
//            scheduledOperateHistoryDao.add(scheduledOperateHistory);
//        }
    }

    @Override
    public void checkRuleGroupIfDependedBySchedule(RuleGroup ruleGroup) throws UnExpectedRequestException {
        if (Objects.nonNull(ruleGroup)) {
            if (scheduledFrontBackRuleDao.isExists(Arrays.asList(ruleGroup.getId()))) {
                throw new UnExpectedRequestException("This rule is dependent on relation scheduled tasks");
            }
            if (scheduledWorkflowTaskRelationDao.isExists(Arrays.asList(ruleGroup.getId()))) {
                throw new UnExpectedRequestException("This rule is dependent on publish scheduled tasks");
            }
        }
    }

    @Override
    public void deleteAllUnreleasedSchedules(Project project) throws UnExpectedRequestException {
        List<ScheduledTask> scheduledTaskList = scheduledTasksDao.findByCondition(project.getId(), null, null, null, null, null, null, null);
        boolean existsReleasedScheduleTask = scheduledTaskList.stream().anyMatch(scheduledTask -> TASK_RELEASE_STATUS_SUCCESS.equals(scheduledTask.getReleaseStatus()));
        if (existsReleasedScheduleTask) {
            return;
        }
        LOGGER.info("Preparing to delete all scheduled tasks if they are all in an unreleased state.");

        try {
            deleteBatchPublishSchedules(project.getId());
        } catch (ScheduledPushFailedException | IOException e) {
            LOGGER.error("Failed to delete batch of publish schedule", e);
            throw new UnExpectedRequestException("Failed to delete publish schedule: " + e.getMessage());
        }

        try {
            deleteBatchRelationSchedules(scheduledTaskList);
        } catch (PermissionDeniedRequestException | UnExpectedRequestException | IOException e) {
            LOGGER.error("Failed to delete batch of relation schedule", e);
            throw new UnExpectedRequestException("Failed to delete relation schedule: " + e.getMessage());
        }
    }

    private void deleteBatchRelationSchedules(List<ScheduledTask> scheduledTaskList) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
//        for (ScheduledTask scheduledTask: scheduledTaskList) {
//            if (!ScheduledTaskTypeEnum.RELATION.getCode().equals(scheduledTask.getTaskType())) {
//                continue;
//            }
//            //将前后置规则id置空
//            List<Long> longList = Lists.newArrayList();
//            Map<String, Object> result = synchroToWtss(scheduledTask.getClusterName(), scheduledTask.getProjectName(), scheduledTask.getWorkFlowName(), scheduledTask.getTaskName(),
//                    longList, longList, scheduledTask.getReleaseUser());
//            if (!ThirdPartyConstants.WTSS_STATUS_CODE.equals(result.get(ThirdPartyConstants.WTSS_RESPONSE_CODE).toString())) {
//                LOGGER.warn("Failed to synchronization Scheduled System when delete relation schedules, Error log: {}", result.get("error").toString());
//            }
//            scheduledTasksDao.deleteScheduledTask(scheduledTask);
//        }
    }

    private void deleteBatchPublishSchedules(Long projectId) throws UnExpectedRequestException, ScheduledPushFailedException, IOException {
        ScheduledTaskRequest scheduledTaskRequest = new ScheduledTaskRequest();
        scheduledTaskRequest.setProjectId(projectId);
        List<Map<String, Object>> scheduleProjectList = scheduledProjectService.getProjectOptionList(scheduledTaskRequest);
        List<Long> scheduledProjectIds = scheduleProjectList.stream().map(map -> (Long) map.get("scheduled_project_id")).distinct().collect(Collectors.toList());
        for (Long scheduledProjectId : scheduledProjectIds) {
            scheduledProjectService.delete(scheduledProjectId);
        }
    }

    private void pushAndUpdatePublishTask(String wtssProjectName, String workflowName, List<ScheduledTask> releaseTaskList) throws UnExpectedRequestException {
        UnExpectedRequestException exception = null;
        boolean success = false;
        try {
            success = scheduledProjectService.releaseSchedulesToWTSS(wtssProjectName, workflowName, releaseTaskList);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("", e);
            exception = e;
        } catch (ScheduledPushFailedException e) {
            LOGGER.error("", e);
            exception = new UnExpectedRequestException("Failed to push publish schedule to WTSS");
        } catch (IOException e) {
            LOGGER.error("", e);
            exception = new UnExpectedRequestException("File I/O Error!");
        }
        if (success) {
            releaseTaskList.forEach(scheduledTask -> scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_SUCCESS));
        } else {
            releaseTaskList.forEach(scheduledTask -> scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_FAILED));
        }
        scheduledTasksDao.saveAll(releaseTaskList);

        if (null != exception) {
            throw exception;
        }
    }

    private void pushAndUpdateRelationTask(String cluster, List<ScheduledTask> releaseTaskList) throws Exception {
        List<ScheduledFrontBackRule> allScheduledFrontBackRuleList = scheduledFrontBackRuleDao.findByScheduledTaskList(releaseTaskList);
        Map<Long, List<ScheduledFrontBackRule>> taskIdAndFrontBackRuleMap = allScheduledFrontBackRuleList.stream().collect(Collectors.groupingBy(scheduledFrontBackRule -> scheduledFrontBackRule.getScheduledTask().getId()));
        Exception exception = null;

        List<String> clusterNames = releaseTaskList.stream().map(ScheduledTask::getClusterName).distinct().collect(Collectors.toList());
        List<ClusterInfo> clusterInfoList = clusterInfoDao.findByClusterNames(clusterNames);
        Map<String, ClusterInfo> clusterNameMap = CollectionUtils.isEmpty(clusterInfoList) ? new HashMap<>() : clusterInfoList.stream().collect(Collectors.toMap(ClusterInfo::getClusterName, Function.identity(), (oldVal, newVal) -> oldVal));

        Map<String, String> releaseUserAndSessionIdMap = new HashMap<>();
        for (ScheduledTask scheduledTask : releaseTaskList) {
            if (!taskIdAndFrontBackRuleMap.containsKey(scheduledTask.getId())) {
                continue;
            }
            ClusterInfo clusterInfo = clusterNameMap.get(scheduledTask.getClusterName());
            String address = getServerAddress(clusterInfo);
            String sessionId;
            if (releaseUserAndSessionIdMap.containsKey(scheduledTask.getReleaseUser())) {
                sessionId = releaseUserAndSessionIdMap.get(scheduledTask.getReleaseUser());
            } else {
                sessionId = getSessionIdByReleaseUser(scheduledTask.getReleaseUser(), cluster);
                releaseUserAndSessionIdMap.put(scheduledTask.getReleaseUser(), sessionId);
            }

            List<ScheduledFrontBackRule> scheduledFrontBackRuleList = taskIdAndFrontBackRuleMap.get(scheduledTask.getId());
            List<Long> frontRuleGroupIdList = scheduledFrontBackRuleList.stream()
                    .filter(scheduledFrontBackRule -> scheduledFrontBackRule.getTriggerType() == 1)
                    .map(ScheduledFrontBackRule::getRuleGroup)
                    .map(RuleGroup::getId)
                    .collect(Collectors.toList());
            List<Long> backRuleGroupIdList = scheduledFrontBackRuleList.stream()
                    .filter(scheduledFrontBackRule -> scheduledFrontBackRule.getTriggerType() == 2)
                    .map(ScheduledFrontBackRule::getRuleGroup)
                    .map(RuleGroup::getId)
                    .collect(Collectors.toList());
            try {
                scheduledTaskPushService.synchronousRelationTask(scheduledTask.getProjectName(), scheduledTask.getWorkFlowName(), scheduledTask.getTaskName()
                        , frontRuleGroupIdList, backRuleGroupIdList, clusterInfo.getHiveUrn(), sessionId, address);
                scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_SUCCESS);
            } catch (UnExpectedRequestException e) {
                scheduledTask.setReleaseStatus(TASK_RELEASE_STATUS_FAILED);
                exception = e;
            }
        }
        scheduledTasksDao.saveAll(releaseTaskList);

        boolean ifAllPushedSuccess = releaseTaskList.stream()
                .filter(scheduledTask -> null != scheduledTask.getReleaseStatus() && scheduledTask.getReleaseStatus() == 1)
                .count() == releaseTaskList.size();
        if (!ifAllPushedSuccess && null != exception) {
            throw exception;
        }
    }

    private String getServerAddress(ClusterInfo clusterInfo) throws UnExpectedRequestException {
        if (Objects.isNull(clusterInfo)) {
            throw new UnExpectedRequestException("Cluster doesn't exists");
        }
        Map<String, Object> wtssJsonMap = clusterInfo.getWtssJsonMap();
        String serverAddress = MapUtils.getString(wtssJsonMap, "path");
        if (StringUtils.isEmpty(serverAddress)) {
            throw new UnExpectedRequestException("The address of server doesn't exists");
        }
        return serverAddress;
    }

}
