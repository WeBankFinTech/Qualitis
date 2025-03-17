package com.webank.wedatasphere.qualitis.scheduled.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.ScheduledOperateHistoryDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.constant.ScheduledTaskTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.constant.SignalTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.dao.*;
import com.webank.wedatasphere.qualitis.scheduled.entity.*;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.*;
import com.webank.wedatasphere.qualitis.scheduled.response.RuleGroupResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledProjectDetailResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledSignalParameterResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledWorkflowDetailResponse;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledProjectService;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskPushService;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskService;
import com.webank.wedatasphere.qualitis.scheduled.util.ScheduledGetSessionUtil;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 10:30
 * @description
 */
@Service
public class ScheduledProjectServiceImpl implements ScheduledProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledProjectServiceImpl.class);

//    @Autowired
//    private ProjectDao projectDao;
//    @Autowired
//    private ScheduledProjectDao scheduledProjectDao;
//    @Autowired
//    private ScheduledWorkflowDao scheduledWorkflowDao;
//    @Autowired
//    private ScheduledSignalDao scheduledSignalDao;
//    @Autowired
//    private ScheduledTaskDao scheduledTaskDao;
//    @Autowired
//    private ScheduledWorkflowBusinessDao scheduledWorkflowBusinessDao;
//    @Autowired
//    private ScheduledWorkflowTaskRelationDao scheduledWorkflowTaskRelationDao;
//    @Autowired
//    private ScheduledFrontBackRuleDao scheduledFrontBackRuleDao;
//    @Autowired
//    private RuleGroupDao ruleGroupDao;
//    @Resource(name = "wtssScheduledPushService")
//    private ScheduledTaskPushService scheduledTaskPushService;
//    @Autowired
//    private ProjectUserService projectUserService;
//    @Autowired
//    private ProjectUserDao projectUserDao;
//    @Resource
//    private ClusterInfoDao clusterInfoDao;
//    @Autowired
//    private ScheduledTaskService scheduledTaskService;
//    @Autowired
//    private ProxyUserRepository proxyUserRepository;
//    @Autowired
//    private ScheduledOperateHistoryDao scheduledOperateHistoryDao;
//
//    private HttpServletRequest httpServletRequest;
//
//    private final Integer OPERATE_TYPE_EDIT = 3;
//    private final Integer OPERATE_TYPE_DELETE = 2;
//    private final Integer PROGRESS_STATUS_ENDED = 2;
//    private final Integer PROGRESS_STATUS_FAILED = 3;
//
//    public ScheduledProjectServiceImpl(@Context HttpServletRequest httpServletRequest) {
//        this.httpServletRequest = httpServletRequest;
//    }
//
//    @Override
//    public Boolean isCreatedScheduleProject(AddScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException {
//        WtssScheduledProjectRequest wtssScheduledProjectRequest = new WtssScheduledProjectRequest();
//        wtssScheduledProjectRequest.setReleaseUser(request.getReleaseUser());
//        wtssScheduledProjectRequest.setProjectName(request.getScheduledProjectName());
//        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
//        String serverAddress = getServerAddress(clusterInfo);
//        String sessionId = getSessionIdByReleaseUser(request.getReleaseUser(), request.getClusterName());
//        return checkIfCreatedScheduleProject(wtssScheduledProjectRequest, sessionId, serverAddress);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void add(AddScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException, PermissionDeniedRequestException, RoleNotFoundException {
//        LOGGER.info("add scheduled project, req: {}", request);
//        if (scheduledProjectDao.findByName(request.getScheduledProjectName()) != null) {
//            throw new UnExpectedRequestException("Schedule project name already exists");
//        }
//        Project project = projectDao.findById(request.getProjectId());
//        if (Objects.isNull(project)) {
//            throw new UnExpectedRequestException("Project doesn't exists");
//        }
//
//        List<ScheduledWorkflowRequest> workflowRequestList = request.getWorkflowList();
//        validateWorkflowParameter(workflowRequestList, request.getScheduledProjectName());
//
////        save ScheduledProject
//        ScheduledProject scheduledProject = saveScheduledProject(project, request);
//
////        save ScheduledWorkflow
//        List<ScheduledWorkflow> scheduledWorkflowList = saveScheduledWorkflow(scheduledProject, workflowRequestList);
//
////        save ScheduledTask
//        saveScheduledTaskAndRelation(scheduledProject, scheduledWorkflowList, workflowRequestList);
//
////        save ScheduleSignal
//        saveScheduledSignal(scheduledProject, scheduledWorkflowList, workflowRequestList);
//
//        authorizePermissionForProxyUser(scheduledProject, workflowRequestList);
//
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void modify(ModifyScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException, PermissionDeniedRequestException, RoleNotFoundException {
//        LOGGER.info("modify scheduled project, req: {}", request);
//        request.checkRequest();
//        Optional<ScheduledProject> projectOptional = scheduledProjectDao.findById(request.getScheduledProjectId());
//        if (!projectOptional.isPresent()) {
//            throw new UnExpectedRequestException("Schedule Project doesn't exists");
//        }
//        ScheduledProject scheduledProject = projectOptional.get();
//        List<ScheduledWorkflowRequest> workflowRequestList = request.getWorkflowList();
//        validateWorkflowParameter(workflowRequestList, scheduledProject.getName());
//
//        ScheduledOperateHistory scheduledOperateHistory = new ScheduledOperateHistory();
//        try {
////        Modifying ScheduledProject
//            modifyScheduledProject(scheduledProject, request);
////        Deleting ScheduledWorkflows to be consistent with front-page
//            List<ScheduledWorkflow> deletedWorkflowList = deleteScheduledWorkflow(scheduledProject, workflowRequestList);
////        Modifying ScheduledWorkflows to be consistent with front-page
//            editScheduledWorkflow(scheduledProject, workflowRequestList);
////        Creating ScheduledWorkflows to be consistent with front-page
//            addScheduledWorkflow(scheduledProject, workflowRequestList);
//
//            authorizePermissionForProxyUser(scheduledProject, workflowRequestList);
//
//            deleteWorkflowOnWTSS(scheduledProject, deletedWorkflowList);
//        } catch (UnExpectedRequestException | ScheduledPushFailedException e) {
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            scheduledOperateHistory.setErrorMessage(e.getMessage());
//            throw e;
//        } catch (PermissionDeniedRequestException e) {
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            scheduledOperateHistory.setErrorMessage("Failed to authorize permissions to proxy user: permission denied");
//            throw e;
//        } catch (RoleNotFoundException e) {
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            scheduledOperateHistory.setErrorMessage("Failed to authorize permissions to proxy user: role not found");
//            throw e;
//        } finally {
//            scheduledOperateHistory.setDispatchingSystemType(scheduledProject.getDispatchingSystemType());
//            scheduledOperateHistory.setClusterName(scheduledProject.getClusterName());
//            scheduledOperateHistory.setWtssProjectName(scheduledProject.getName());
//            scheduledOperateHistory.setTaskType(ScheduledTaskTypeEnum.PUBLISH.getCode());
//            scheduledOperateHistory.setWtssProjectName(scheduledProject.getName());
//            scheduledOperateHistory.setOperateType(OPERATE_TYPE_EDIT);
//            scheduledOperateHistory.setCreateUser(HttpUtils.getUserName(httpServletRequest));
//            scheduledOperateHistory.setCreateTime(DateUtils.now());
//            scheduledOperateHistoryDao.add(scheduledOperateHistory);
//        }
//    }
//
//    @Transactional(rollbackFor = {RuntimeException.class, ScheduledPushFailedException.class, UnExpectedRequestException.class})
//    @Override
//    public void delete(Long scheduledProjectId) throws ScheduledPushFailedException, UnExpectedRequestException {
//        LOGGER.info("delete scheduled project, scheduledProjectId:{}", scheduledProjectId);
//        Optional<ScheduledProject> projectOptional = scheduledProjectDao.findById(scheduledProjectId);
//        if (!projectOptional.isPresent()) {
//            throw new UnExpectedRequestException("Schedule Project doesn't exists");
//        }
//        ScheduledProject scheduledProject = projectOptional.get();
//        //覆盖：关联任务下所有匹配项目名称都删除
//        scheduledTaskService.deleteRelationScheduleTasks(scheduledProject.getName());
//
//        List<ScheduledWorkflow> workflowList = scheduledWorkflowDao.findByScheduledProject(scheduledProject);
//        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(scheduledProject.getClusterName());
//
//        ScheduledOperateHistory scheduledOperateHistory = new ScheduledOperateHistory();
//        try {
////            Delete task
//            deleteScheduledTaskAndRelation(workflowList);
////            Delete signal
//            scheduledSignalDao.deleteByScheduledWorkflowList(workflowList);
////            Delete ScheduledWorkflow
//            scheduledWorkflowDao.deleteAll(workflowList);
////        Delete ScheduledProject
//            scheduledProjectDao.delete(scheduledProject);
//
//            if (clusterInfo != null) {
//                String serverAddress = getServerAddress(clusterInfo);
//                String sessionId = getSessionIdByReleaseUser(scheduledProject.getReleaseUser(), scheduledProject.getClusterName());
//                WtssScheduledProjectRequest wtssScheduledProjectRequest = new WtssScheduledProjectRequest(scheduledProject);
//                if (checkIfCreatedScheduleProject(wtssScheduledProjectRequest, sessionId, serverAddress)) {
////              Deleting workflow to WTSS
//                    List<WtssScheduledJobRequest> wtssScheduledJobRequests = createScheduleJobRequestByWorkflow(scheduledProject, workflowList);
//                    for (WtssScheduledJobRequest wtssScheduledJobRequest : wtssScheduledJobRequests) {
//                        if (ScheduledTaskPushService.SCHEDULE_TYPE_INTERVAL.equals(wtssScheduledJobRequest.getScheduledType())) {
//                            scheduledTaskPushService.deleteIntervalJob(wtssScheduledJobRequest, sessionId, serverAddress);
//                        } else if (ScheduledTaskPushService.SCHEDULE_TYPE_SIGNAL.equals(wtssScheduledJobRequest.getScheduledType())) {
//                            scheduledTaskPushService.deleteSignalJob(wtssScheduledJobRequest, sessionId, serverAddress);
//                        }
//                    }
//                    scheduledTaskPushService.deleteProject(wtssScheduledProjectRequest, sessionId, serverAddress);
//                }
//            }
//
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_ENDED);
//        } catch (UnExpectedRequestException | ScheduledPushFailedException e) {
//            scheduledOperateHistory.setProgressStatus(PROGRESS_STATUS_FAILED);
//            scheduledOperateHistory.setErrorMessage(e.getMessage());
//            throw e;
//        } finally {
//            scheduledOperateHistory.setDispatchingSystemType(scheduledProject.getDispatchingSystemType());
//            scheduledOperateHistory.setClusterName(scheduledProject.getClusterName());
//            scheduledOperateHistory.setWtssProjectName(scheduledProject.getName());
//            scheduledOperateHistory.setTaskType(ScheduledTaskTypeEnum.PUBLISH.getCode());
//            scheduledOperateHistory.setWtssProjectName(scheduledProject.getName());
//            scheduledOperateHistory.setOperateType(OPERATE_TYPE_DELETE);
//            scheduledOperateHistory.setCreateUser(HttpUtils.getUserName(httpServletRequest));
//            scheduledOperateHistory.setCreateTime(DateUtils.now());
//            scheduledOperateHistoryDao.add(scheduledOperateHistory);
//        }
//    }
//
//    @Override
//    public List<Map<String, Object>> findRuleGroupNotInFrontBackRule(Long projectId) {
//        return scheduledFrontBackRuleDao.findRuleGroupNotInFrontBackRule(projectId);
//    }
//
//    @Override
//    public ScheduledProjectDetailResponse getProjectDetail(Long scheduledProjectId) throws UnExpectedRequestException {
//        Optional<ScheduledProject> scheduledProjectOptional = scheduledProjectDao.findById(scheduledProjectId);
//        if (!scheduledProjectOptional.isPresent()) {
//            throw new UnExpectedRequestException("Schedule Project doesn't exists");
//        }
//        List<ScheduledWorkflow> scheduledWorkflowList = scheduledWorkflowDao.findByScheduledProject(scheduledProjectOptional.get());
//        List<ScheduledWorkflowDetailResponse> workflowDetailResponseList = scheduledWorkflowList.stream().map(ScheduledWorkflowDetailResponse::new).collect(Collectors.toList());
//
//        List<RuleGroupResponse> allRuleGroupResponseList = new LinkedList<>();
//
////        set ruleGroupList
//        List<ScheduledWorkflowTaskRelation> workflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByWorkflowList(scheduledWorkflowList);
//        Map<Long, List<ScheduledWorkflowTaskRelation>> workflowTaskRelationMap = workflowTaskRelationList.stream().collect(Collectors.groupingBy(w -> w.getScheduledWorkflow().getId()));
//        for (ScheduledWorkflowDetailResponse detailResponse : workflowDetailResponseList) {
//            List<ScheduledWorkflowTaskRelation> subWorkflowTaskRelationList = workflowTaskRelationMap.get(detailResponse.getWorkflowId());
//            if (CollectionUtils.isEmpty(subWorkflowTaskRelationList)) {
//                continue;
//            }
//            List<RuleGroupResponse> ruleGroupList = subWorkflowTaskRelationList.stream().map(RuleGroupResponse::new).collect(Collectors.toList());
//            detailResponse.setRuleGroupList(ruleGroupList);
//
//            allRuleGroupResponseList.addAll(ruleGroupList);
//        }
//
////       set signal parameter
//        Map<Long, RuleGroupResponse> ruleGroupResponseMap = allRuleGroupResponseList.stream().collect(Collectors.toMap(RuleGroupResponse::getRuleGroupId, t -> t, (oValue, nValue) -> nValue));
//        List<ScheduledSignal> scheduledSignalList = scheduledSignalDao.findByWorkflowList(scheduledWorkflowList);
//        Map<Long, List<ScheduledSignal>> signalMap = scheduledSignalList.stream().collect(Collectors.groupingBy(w -> w.getScheduledWorkflow().getId()));
//        for (ScheduledWorkflowDetailResponse detailResponse : workflowDetailResponseList) {
//            List<ScheduledSignal> subScheduledSignalList = signalMap.get(detailResponse.getWorkflowId());
//            if (CollectionUtils.isEmpty(subScheduledSignalList)) {
//                continue;
//            }
//            List<ScheduledSignalParameterResponse> scheduledSignalParameterResponseList = subScheduledSignalList.stream().map(ScheduledSignalParameterResponse::new).collect(Collectors.toList());
//            for (ScheduledSignalParameterResponse signalParameterResponse : scheduledSignalParameterResponseList) {
//                List<Long> ruleGroupIds = signalParameterResponse.getRuleGroupIds();
//                if (CollectionUtils.isNotEmpty(ruleGroupIds)) {
//                    List<RuleGroupResponse> ruleGroupResponseList = ruleGroupIds.stream().map(ruleGroupResponseMap::get).filter(Objects::nonNull).collect(Collectors.toList());
//                    signalParameterResponse.setRuleGroupList(ruleGroupResponseList);
//                } else {
//                    signalParameterResponse.setRuleGroupList(Collections.emptyList());
//                }
//            }
//
//            detailResponse.setSignalParameterList(scheduledSignalParameterResponseList);
//        }
//
//        ScheduledProjectDetailResponse detailResponse = new ScheduledProjectDetailResponse(scheduledProjectOptional.get());
//        detailResponse.setWorkflowList(workflowDetailResponseList);
//        return detailResponse;
//    }
//
//    @Override
//    public List<Map<String, Object>> getProjectOptionList(ScheduledTaskRequest request) {
//        List<ScheduledProject> scheduledProjectList = scheduledProjectDao.findProjectList(request);
//        return scheduledProjectList.stream().map(scheduledProject -> {
//            Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
//            map.put("scheduled_project_id", scheduledProject.getId());
//            map.put("scheduled_project_name", scheduledProject.getName());
//            return map;
//        }).collect(Collectors.toList());
//    }
//
//    private void validateWorkflowParameter(List<ScheduledWorkflowRequest> workflowRequestList, String scheduleProjectName) throws UnExpectedRequestException {
//        long workflowCount = workflowRequestList.stream().map(ScheduledWorkflowRequest::getWorkflowName).distinct().count();
//        if (workflowCount < workflowRequestList.size()) {
//            throw new UnExpectedRequestException("Duplicate workflow name");
//        }
//        List<Long> allRuleGroupIdList = new ArrayList<>();
////        规则组全局唯一
//        for (ScheduledWorkflowRequest scheduledWorkflowRequest : workflowRequestList) {
//            List<Long> ruleGroupIdList = scheduledWorkflowRequest.getRuleGroupList().stream().map(RuleGroupRequest::getRuleGroupId).collect(Collectors.toList());
////           工作流之间不能存在相同的规则组
//            if (CollectionUtils.containsAny(allRuleGroupIdList, ruleGroupIdList)) {
//                throw new UnExpectedRequestException("Exist the same rule-group among workflows");
//            }
////            相同的规则组不能被关联调度关联
//            if (scheduledFrontBackRuleDao.isExists(ruleGroupIdList)) {
//                throw new UnExpectedRequestException("Existed rule-group related to front-back task ");
//            }
////           项目内规则组唯一
//            List<ScheduledWorkflowTaskRelation> workflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByRuleGroupIds(ruleGroupIdList);
//            Optional<ScheduledWorkflowTaskRelation> relationOptional = workflowTaskRelationList.stream()
////                    当前项目之外是否还存在相同的规则组
//                    .filter(relation -> !relation.getScheduledProject().getName().equals(scheduleProjectName)).findAny();
//            if (relationOptional.isPresent()) {
//                throw new UnExpectedRequestException("Exist same rule group in other schedule project: " + relationOptional.get().getScheduledProject().getName());
//            }
//            allRuleGroupIdList.addAll(ruleGroupIdList);
//        }
//    }
//
//    private List<ScheduledWorkflow> getDeletedWorkflowOnPage(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflow> workflowList = scheduledWorkflowDao.findByScheduledProject(scheduledProject);
//        List<Long> workflowRequestIds = workflowRequestList.stream().map(ScheduledWorkflowRequest::getWorkflowId).collect(Collectors.toList());
//        return workflowList.stream().filter(scheduledWorkflow -> !workflowRequestIds.contains(scheduledWorkflow.getId())).collect(Collectors.toList());
//    }
//
//    private List<ScheduledWorkflowRequest> getModifiedWorkflowOnPage(List<ScheduledWorkflowRequest> workflowRequestList) {
//        return workflowRequestList.stream().filter(scheduledWorkflowRequest -> Objects.nonNull(scheduledWorkflowRequest.getWorkflowId())).collect(Collectors.toList());
//    }
//
//    private List<ScheduledWorkflowRequest> getCreatedWorkflowOnPage(List<ScheduledWorkflowRequest> workflowRequestList) {
//        return workflowRequestList.stream().filter(scheduledWorkflowRequest -> Objects.isNull(scheduledWorkflowRequest.getWorkflowId())).collect(Collectors.toList());
//    }
//
//    private void deleteScheduledTaskAndRelation(List<ScheduledWorkflow> scheduledWorkflows) {
//        List<ScheduledWorkflowTaskRelation> workflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByWorkflowList(scheduledWorkflows);
//        List<ScheduledTask> scheduledTaskList = workflowTaskRelationList.stream().map(ScheduledWorkflowTaskRelation::getScheduledTask).collect(Collectors.toList());
//        scheduledWorkflowTaskRelationDao.deleteByScheduledWorkflowList(scheduledWorkflows);
//        scheduledTaskDao.deleteScheduledTaskBatch(scheduledTaskList);
//    }
//
//
//    private List<ScheduledTask> saveScheduledTaskAndRelation(ScheduledProject scheduledProject, List<ScheduledWorkflow> workflowList, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledTask> scheduledTaskList = Lists.newArrayList();
//        Map<String, List<RuleGroupRequest>> workflowAndRuleGroupMap = workflowRequestList.stream().collect(Collectors.toMap(ScheduledWorkflowRequest::getWorkflowName, ScheduledWorkflowRequest::getRuleGroupList, (oValue, nValue) -> nValue));
//        for (ScheduledWorkflow scheduledWorkflow : workflowList) {
//            List<RuleGroupRequest> ruleGroupRequestList = workflowAndRuleGroupMap.get(scheduledWorkflow.getName());
//            if (CollectionUtils.isEmpty(ruleGroupRequestList)) {
//                continue;
//            }
//            for (RuleGroupRequest ruleGroupRequest : ruleGroupRequestList) {
//                ScheduledTask scheduledTask = saveScheduledTask(scheduledProject, scheduledWorkflow, ruleGroupRequest);
//                saveScheduledWorkflowTaskRelation(scheduledProject, scheduledWorkflow, scheduledTask, ruleGroupRequest);
//                scheduledTaskList.add(scheduledTask);
//            }
//        }
//        return scheduledTaskList;
//    }
//
//    private void saveScheduledSignal(ScheduledProject scheduledProject, List<ScheduledWorkflow> workflowList, List<ScheduledWorkflowRequest> workflowRequestList) {
//        Map<String, ScheduledWorkflowRequest> workflowAndSignalMap = workflowRequestList.stream().collect(Collectors.toMap(ScheduledWorkflowRequest::getWorkflowName, t -> t, (oValue, nValue) -> nValue));
//        for (ScheduledWorkflow scheduledWorkflow : workflowList) {
//            if (!workflowAndSignalMap.containsKey(scheduledWorkflow.getName())) {
//                continue;
//            }
//            ScheduledWorkflowRequest scheduledWorkflowRequest = workflowAndSignalMap.get(scheduledWorkflow.getName());
//            List<ScheduledSignalParameterRequest> signalParameterRequests = scheduledWorkflowRequest.getSignalParameterList();
//            if (CollectionUtils.isEmpty(signalParameterRequests)) {
//                continue;
//            }
//            List<ScheduledSignal> scheduledSignals = Lists.newArrayListWithCapacity(signalParameterRequests.size());
//            for (ScheduledSignalParameterRequest parameterRequest : signalParameterRequests) {
//                ScheduledSignal scheduledSignal = new ScheduledSignal();
//                scheduledSignal.setScheduledProject(scheduledProject);
//                scheduledSignal.setScheduledWorkflow(scheduledWorkflow);
//                scheduledSignal.setName(parameterRequest.getName());
//                scheduledSignal.setType(parameterRequest.getType());
//                scheduledSignal.setContentJson(parameterRequest.getContentJson());
//                List<RuleGroupRequest> ruleGroupList = parameterRequest.getRuleGroupList();
//                if (CollectionUtils.isEmpty(ruleGroupList)) {
//                    ruleGroupList = scheduledWorkflowRequest.getRuleGroupList();
//                }
//                String ruleGroupIds = ruleGroupList.stream().map(RuleGroupRequest::getRuleGroupId).map(String::valueOf).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
//                scheduledSignal.setRuleGroupIds(ruleGroupIds);
//
//                scheduledSignals.add(scheduledSignal);
//            }
//            scheduledSignalDao.saveAll(scheduledSignals);
//        }
//    }
//
//    private void modifyScheduledTaskAndRelation(ScheduledProject scheduledProject, List<ScheduledWorkflow> modifyWorkflowList, List<ScheduledWorkflowRequest> modifyWorkflowRequestList) {
//        Map<String, List<RuleGroupRequest>> workflowAndRuleGroupMap = modifyWorkflowRequestList.stream().collect(Collectors.toMap(ScheduledWorkflowRequest::getWorkflowName, ScheduledWorkflowRequest::getRuleGroupList, (oValue, nValue) -> nValue));
//        for (ScheduledWorkflow scheduledWorkflow : modifyWorkflowList) {
//            List<RuleGroupRequest> ruleGroupRequestList = workflowAndRuleGroupMap.get(scheduledWorkflow.getName());
//            if (CollectionUtils.isEmpty(ruleGroupRequestList)) {
//                continue;
//            }
//            List<ScheduledWorkflowTaskRelation> storedTaskRelationList = scheduledWorkflowTaskRelationDao.findByWorkflow(scheduledWorkflow);
//            List<Long> storedRuleGroupIdList = storedTaskRelationList.stream().map(workflowTaskRelation -> workflowTaskRelation.getRuleGroup().getId()).collect(Collectors.toList());
//
////            Delete ScheduledTask to be consistent with front-page
//            List<Long> ruleGroupIdList = ruleGroupRequestList.stream().map(RuleGroupRequest::getRuleGroupId).collect(Collectors.toList());
//            List<ScheduledWorkflowTaskRelation> deleteTaskRelationList = storedTaskRelationList.stream().filter(workflowTaskRelation -> !ruleGroupIdList.contains(workflowTaskRelation.getRuleGroup().getId())).collect(Collectors.toList());
//            scheduledWorkflowTaskRelationDao.deleteBatch(deleteTaskRelationList);
//            scheduledTaskDao.deleteScheduledTaskBatch(deleteTaskRelationList.stream().map(ScheduledWorkflowTaskRelation::getScheduledTask).collect(Collectors.toList()));
//
////            Create ScheduledTask to be consistent with front-page
//            List<RuleGroupRequest> newRuleGroupList = ruleGroupRequestList.stream().filter(ruleGroupRequest -> !storedRuleGroupIdList.contains(ruleGroupRequest.getRuleGroupId())).collect(Collectors.toList());
//            for (RuleGroupRequest ruleGroupRequest : newRuleGroupList) {
//                ScheduledTask scheduledTask = saveScheduledTask(scheduledProject, scheduledWorkflow, ruleGroupRequest);
//                saveScheduledWorkflowTaskRelation(scheduledProject, scheduledWorkflow, scheduledTask, ruleGroupRequest);
//            }
//        }
//    }
//
//    private void modifyScheduledSignal(ScheduledProject scheduledProject, List<ScheduledWorkflow> modifyWorkflowList, List<ScheduledWorkflowRequest> modifyWorkflowRequestList) {
//        List<ScheduledSignal> scheduledSignalList = new ArrayList<>();
//        Map<String, ScheduledWorkflowRequest> workflowAndSignalMap = modifyWorkflowRequestList.stream().collect(Collectors.toMap(ScheduledWorkflowRequest::getWorkflowName, t -> t, (oValue, nValue) -> nValue));
//        for (ScheduledWorkflow scheduledWorkflow : modifyWorkflowList) {
//            if (!workflowAndSignalMap.containsKey(scheduledWorkflow.getName())) {
//                continue;
//            }
//            ScheduledWorkflowRequest scheduledWorkflowRequest = workflowAndSignalMap.get(scheduledWorkflow.getName());
//            List<ScheduledSignalParameterRequest> signalParameterRequests = scheduledWorkflowRequest.getSignalParameterList();
//            if (CollectionUtils.isEmpty(signalParameterRequests)) {
//                continue;
//            }
//            List<RuleGroupRequest> defaultRruleGroupList = scheduledWorkflowRequest.getRuleGroupList();
//            List<ScheduledSignal> storedScheduledSignalList = scheduledSignalDao.findByWorkflow(scheduledWorkflow);
//
////            Delete ScheduledSignal to be consistent with front-page
//            List<Long> requestSignalIdList = signalParameterRequests.stream().map(ScheduledSignalParameterRequest::getId).collect(Collectors.toList());
//            List<ScheduledSignal> deleteSignalList = storedScheduledSignalList.stream().filter(storedSignal -> !requestSignalIdList.contains(storedSignal.getId())).collect(Collectors.toList());
//            scheduledSignalDao.deleteAll(deleteSignalList);
//
////            Modify ScheduledSignal to be consistent with front-page
//            List<ScheduledSignal> modifySignalList = signalParameterRequests.stream().filter(parameterRequest -> Objects.nonNull(parameterRequest.getId()))
//                    .map(signalParameterRequest -> convertSignalEntity(scheduledProject, scheduledWorkflow, signalParameterRequest, defaultRruleGroupList))
//                    .collect(Collectors.toList());
//
////            Create ScheduledSignal to be consistent with front-page
//            List<ScheduledSignal> createSignalList = signalParameterRequests.stream().filter(scheduledSignal -> Objects.isNull(scheduledSignal.getId()))
//                    .map(signalParameterRequest -> convertSignalEntity(scheduledProject, scheduledWorkflow, signalParameterRequest, defaultRruleGroupList))
//                    .collect(Collectors.toList());
//
//            scheduledSignalList.addAll(modifySignalList);
//            scheduledSignalList.addAll(createSignalList);
//        }
//        scheduledSignalDao.saveAll(scheduledSignalList);
//    }
//
//    private ScheduledSignal convertSignalEntity(ScheduledProject scheduledProject, ScheduledWorkflow scheduledWorkflow
//            , ScheduledSignalParameterRequest signalParameterRequest, List<RuleGroupRequest> defaultRruleGroupList) {
//        ScheduledSignal scheduledSignal = new ScheduledSignal();
//        scheduledSignal.setScheduledProject(scheduledProject);
//        scheduledSignal.setScheduledWorkflow(scheduledWorkflow);
//        scheduledSignal.setId(signalParameterRequest.getId());
//        scheduledSignal.setName(signalParameterRequest.getName());
//        scheduledSignal.setType(signalParameterRequest.getType());
//        scheduledSignal.setContentJson(signalParameterRequest.getContentJson());
//        List<RuleGroupRequest> ruleGroupList = signalParameterRequest.getRuleGroupList();
//        if (CollectionUtils.isEmpty(ruleGroupList)) {
//            ruleGroupList = defaultRruleGroupList;
//        }
//        String ruleGroupIds = ruleGroupList.stream().map(RuleGroupRequest::getRuleGroupId).map(String::valueOf).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
//        scheduledSignal.setRuleGroupIds(ruleGroupIds);
//        return scheduledSignal;
//    }
//
//    private ScheduledProject saveScheduledProject(Project project, AddScheduledTaskRequest request) {
//        ScheduledProject scheduledProject = new ScheduledProject();
//        scheduledProject.setProject(project);
//        scheduledProject.setName(request.getScheduledProjectName());
//        scheduledProject.setClusterName(request.getClusterName());
//        scheduledProject.setDispatchingSystemType(request.getDispatchingSystemType().toUpperCase());
//
//        scheduledProject.setReleaseUser(request.getReleaseUser());
//        scheduledProject.setCreateUser(HttpUtils.getUserName(httpServletRequest));
//        scheduledProject.setCreateTime(DateUtils.now());
//        return scheduledProjectDao.save(scheduledProject);
//    }
//
//    private void modifyScheduledProject(ScheduledProject scheduledProject, ModifyScheduledTaskRequest request) {
//        boolean ifNeedToUpdate = !scheduledProject.getReleaseUser().equals(request.getReleaseUser());
//        if (ifNeedToUpdate) {
////            modify ScheduledProject
//            scheduledProject.setReleaseUser(request.getReleaseUser());
//            scheduledProject.setModifyUser(HttpUtils.getUserName(httpServletRequest));
//            scheduledProject.setModifyTime(DateUtils.now());
//            scheduledProjectDao.save(scheduledProject);
//        }
//    }
//
//    private List<ScheduledWorkflow> saveScheduledWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflow> scheduledWorkflowList = Lists.newArrayListWithCapacity(workflowRequestList.size());
//        String currentTime = DateUtils.now();
//        String currentUser = HttpUtils.getUserName(httpServletRequest);
//        for (ScheduledWorkflowRequest workflowRequest : workflowRequestList) {
//            ScheduledWorkflow scheduledWorkflow = getScheduledWorkflow(workflowRequest.getWorkflowId());
//            scheduledWorkflow.setName(workflowRequest.getWorkflowName());
//            scheduledWorkflow.setScheduledProject(scheduledProject);
//            scheduledWorkflow.setProxyUser(workflowRequest.getProxyUser());
//            scheduledWorkflow.setScheduledType(workflowRequest.getScheduledType());
//            scheduledWorkflow.setExecuteInterval(workflowRequest.getExecuteInterval());
//            scheduledWorkflow.setExecuteDateInInterval(workflowRequest.getExecuteDateInInterval());
//            scheduledWorkflow.setExecuteTimeInDate(workflowRequest.getExecuteTimeInDate());
//            scheduledWorkflow.setScheduledSignalJson(workflowRequest.getScheduledSignalJson());
//            scheduledWorkflow.setCreateUser(currentUser);
//            scheduledWorkflow.setCreateTime(currentTime);
//            scheduledWorkflowList.add(scheduledWorkflow);
//        }
//        return scheduledWorkflowDao.saveAll(scheduledWorkflowList);
//    }
//
//    private List<ScheduledWorkflow> modifyScheduledWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflow> workflowList = Lists.newArrayListWithCapacity(workflowRequestList.size());
//        String currentTime = DateUtils.now();
//        String currentUser = HttpUtils.getUserName(httpServletRequest);
//        for (ScheduledWorkflowRequest workflowRequest : workflowRequestList) {
//            ScheduledWorkflow scheduledWorkflow = getScheduledWorkflow(workflowRequest.getWorkflowId());
////            modify work_flow_name with qualitis_scheduled_task table
//            if (!scheduledWorkflow.getName().equals(workflowRequest.getWorkflowName())) {
//                List<ScheduledWorkflowTaskRelation> workflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByWorkflow(scheduledWorkflow);
//                List<ScheduledTask> scheduledTaskList = workflowTaskRelationList.stream().map(workflowTaskRelation -> {
//                    ScheduledTask scheduledTask = workflowTaskRelation.getScheduledTask();
//                    scheduledTask.setWorkFlowName(workflowRequest.getWorkflowName());
//                    return scheduledTask;
//                }).collect(Collectors.toList());
//                scheduledTaskDao.saveAll(scheduledTaskList);
//            }
//
//            scheduledWorkflow.setName(workflowRequest.getWorkflowName());
//            scheduledWorkflow.setScheduledProject(scheduledProject);
//            scheduledWorkflow.setProxyUser(workflowRequest.getProxyUser());
//            scheduledWorkflow.setScheduledType(workflowRequest.getScheduledType());
//            if (ScheduledTaskPushService.SCHEDULE_TYPE_INTERVAL.equals(workflowRequest.getScheduledType())) {
//                scheduledWorkflow.setExecuteInterval(workflowRequest.getExecuteInterval());
//                scheduledWorkflow.setExecuteDateInInterval(workflowRequest.getExecuteDateInInterval());
//                scheduledWorkflow.setExecuteTimeInDate(workflowRequest.getExecuteTimeInDate());
//                scheduledWorkflow.setScheduledSignalJson(null);
//            } else if (ScheduledTaskPushService.SCHEDULE_TYPE_SIGNAL.equals(workflowRequest.getScheduledType())) {
//                scheduledWorkflow.setScheduledSignalJson(workflowRequest.getScheduledSignalJson());
//                scheduledWorkflow.setExecuteInterval(null);
//                scheduledWorkflow.setExecuteDateInInterval(null);
//                scheduledWorkflow.setExecuteTimeInDate(null);
//            }
//
//            scheduledWorkflow.setModifyUser(currentUser);
//            scheduledWorkflow.setModifyTime(currentTime);
//            workflowList.add(scheduledWorkflow);
//        }
//        return scheduledWorkflowDao.saveAll(workflowList);
//    }
//
//    private ScheduledWorkflow getScheduledWorkflow(Long id) {
//        if (Objects.nonNull(id)) {
//            Optional<ScheduledWorkflow> workflowOptional = scheduledWorkflowDao.findById(id);
//            if (workflowOptional.isPresent()) {
//                return workflowOptional.get();
//            }
//        }
//        return new ScheduledWorkflow();
//    }
//
//    private void saveScheduledWorkflowTaskRelation(ScheduledProject scheduledProject, ScheduledWorkflow scheduledWorkflow, ScheduledTask scheduledTask, RuleGroupRequest ruleGroupRequest) {
//        ScheduledWorkflowTaskRelation workflowTaskRelation = new ScheduledWorkflowTaskRelation();
//        workflowTaskRelation.setScheduledProject(scheduledProject);
//        workflowTaskRelation.setScheduledWorkflow(scheduledWorkflow);
//        workflowTaskRelation.setScheduledTask(scheduledTask);
//        RuleGroup ruleGroup = new RuleGroup();
//        ruleGroup.setId(ruleGroupRequest.getRuleGroupId());
//        workflowTaskRelation.setRuleGroup(ruleGroup);
//        scheduledTask.setScheduledWorkflowTaskRelation(workflowTaskRelation);
//        scheduledWorkflowTaskRelationDao.save(workflowTaskRelation);
//    }
//
//    private ScheduledTask saveScheduledTask(ScheduledProject scheduledProject, ScheduledWorkflow scheduledWorkflow, RuleGroupRequest ruleGroupRequest) {
//        ScheduledTask scheduledTask = new ScheduledTask();
//        scheduledTask.setTaskName(ruleGroupRequest.getRuleGroupName());
//        scheduledTask.setProjectName(scheduledProject.getName());
//        scheduledTask.setClusterName(scheduledProject.getClusterName());
//        scheduledTask.setWorkFlowName(scheduledWorkflow.getName());
//        scheduledTask.setProject(scheduledProject.getProject());
//        scheduledTask.setDispatchingSystemType(scheduledProject.getDispatchingSystemType());
//        scheduledTask.setReleaseUser(scheduledProject.getReleaseUser());
//        scheduledTask.setTaskType(ScheduledTaskTypeEnum.PUBLISH.getCode());
//        scheduledTask.setCreateUser(HttpUtils.getUserName(httpServletRequest));
//        scheduledTask.setCreateTime(DateUtils.now());
//        scheduledTask.setReleaseStatus(0);
//        return scheduledTaskDao.saveScheduledTask(scheduledTask);
//    }
//
//    private String getSessionIdByReleaseUser(String releaseUser, String cluster) throws UnExpectedRequestException {
//        String loginUser = HttpUtils.getUserName(httpServletRequest);
//        String operationUser = StringUtils.isNotBlank(releaseUser) ? releaseUser : loginUser;
//        return ScheduledGetSessionUtil.getSessionId(operationUser, loginUser, cluster);
//    }
//
//    private List<WtssScheduledJobRequest> createWtssScheduledJobRequestList(ScheduledProject scheduledProject, Map<Long, ScheduledWorkflow> workflowIdMap
//            , List<ScheduledTask> scheduledTaskList, List<ScheduledSignal> scheduledSignalList)
//            throws UnExpectedRequestException {
//        int capacity = scheduledTaskList.size() + workflowIdMap.size() + scheduledSignalList.size();
//        List<WtssScheduledJobRequest> wtssScheduledJobRequestList = Lists.newArrayListWithCapacity(capacity);
////        Creating ScheduledJobRequest with signal
//        List<WtssScheduledJobRequest> scheduledSignalRequests = createWtssScheduledJobRequestBySignal(scheduledProject, workflowIdMap, scheduledSignalList);
//
////        Creating ScheduledJobRequest with rule-group
//        Map<Long, List<String>> ruleGroupAndSignalRelationMap = getRuleGroupOfReceiverSignalMap(workflowIdMap, scheduledSignalList);
//        List<WtssScheduledJobRequest> scheduledTaskRequests = createWtssScheduledJobRequestByTask(scheduledProject, workflowIdMap, scheduledTaskList, ruleGroupAndSignalRelationMap);
//
////        Creating ScheduledJobRequest with workflow
//        List<WtssScheduledJobRequest> scheduledWorkflowRequests = createWtssScheduledJobRequestByWorkflow(scheduledProject, workflowIdMap, scheduledTaskList);
//
//        wtssScheduledJobRequestList.addAll(scheduledSignalRequests);
//        wtssScheduledJobRequestList.addAll(scheduledTaskRequests);
//        wtssScheduledJobRequestList.addAll(scheduledWorkflowRequests);
//        return wtssScheduledJobRequestList;
//    }
//
//    private List<WtssScheduledJobRequest> createScheduleJobRequestByWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflow> workflowList) throws UnExpectedRequestException {
//        List<WtssScheduledJobRequest> wtssScheduledJobRequests = Lists.newArrayListWithCapacity(workflowList.size());
//        for (ScheduledWorkflow scheduledWorkflow : workflowList) {
//            if (Objects.isNull(scheduledWorkflow.getScheduleId())) {
//                continue;
//            }
//            WtssScheduledJobRequest wtssScheduledJobRequest = WtssScheduledJobRequest.fromScheduledWorkflow(scheduledProject, scheduledWorkflow);
//            wtssScheduledJobRequests.add(wtssScheduledJobRequest);
//        }
//        setJobRsaTo(wtssScheduledJobRequests);
//        return wtssScheduledJobRequests;
//    }
//
//    private void setJobRsaTo(List<WtssScheduledJobRequest> wtssScheduledJobRequestList) throws UnExpectedRequestException {
//        for (WtssScheduledJobRequest wtssScheduledJobRequest : wtssScheduledJobRequestList) {
//            ProxyUser proxyUser = proxyUserRepository.findByProxyUserName(wtssScheduledJobRequest.getProxyUser());
//            Map<String, Object> userConfigMap = proxyUser.getUserConfigMap();
//            wtssScheduledJobRequest.setJobRsa(userConfigMap.getOrDefault(QualitisConstants.BDP_CLIENT_TOKEN, StringUtils.EMPTY).toString());
//            if (StringUtils.isBlank(wtssScheduledJobRequest.getJobRsa())) {
//                LOGGER.warn("user_config_json is empty: {}", proxyUser.getUserConfigJson());
//                throw new UnExpectedRequestException("bdp_client_token of proxy user doesn't exist! proxy user: " + proxyUser.getProxyUserName());
//            }
//        }
//    }
//
//    private List<WtssScheduledJobRequest> createWtssScheduledJobRequestBySignal(ScheduledProject scheduledProject, Map<Long, ScheduledWorkflow> workflowIdMap, List<ScheduledSignal> scheduledSignalList) throws UnExpectedRequestException {
//        List<WtssScheduledJobRequest> wtssScheduledJobRequests = Lists.newArrayListWithCapacity(scheduledSignalList.size());
//        for (ScheduledSignal scheduledSignal : scheduledSignalList) {
//            ScheduledWorkflow scheduledWorkflow = workflowIdMap.get(scheduledSignal.getScheduledWorkflow().getId());
//            WtssScheduledJobRequest wtssScheduledJobRequest = WtssScheduledJobRequest.fromScheduledSignal(scheduledProject, scheduledWorkflow, scheduledSignal);
//
//            if (SignalTypeEnum.EVENT_SEND.getCode().equals(scheduledSignal.getType())
//                    || SignalTypeEnum.RMB_SEND.getCode().equals(scheduledSignal.getType())) {
//                List<Long> ruleGroupRelationList = getRuleGroupOfSignalList(scheduledWorkflow, scheduledSignal);
//                List<RuleGroup> ruleGroupList = ruleGroupDao.findByIds(ruleGroupRelationList);
//                String dependencies = ruleGroupList.stream().map(ruleGroup -> WtssScheduledTaskPushServiceImpl.clipRuleGroupName(ruleGroup.getRuleGroupName())).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
//                wtssScheduledJobRequest.setDependencies(dependencies);
//            }
//
//            wtssScheduledJobRequests.add(wtssScheduledJobRequest);
//        }
//        return wtssScheduledJobRequests;
//    }
//
//    private List<WtssScheduledJobRequest> createWtssScheduledJobRequestByTask(ScheduledProject scheduledProject, Map<Long, ScheduledWorkflow> workflowIdMap
//            , List<ScheduledTask> scheduledTaskList, Map<Long, List<String>> ruleGroupAndSignalRelationMap) throws UnExpectedRequestException {
//        List<WtssScheduledJobRequest> wtssScheduledJobRequests = Lists.newArrayListWithCapacity(scheduledTaskList.size());
//        for (ScheduledTask scheduledTask : scheduledTaskList) {
//            Long workflowId = scheduledTask.getScheduledWorkflowTaskRelation().getScheduledWorkflow().getId();
//            if (!workflowIdMap.containsKey(workflowId)) {
//                LOGGER.warn("Finding not ScheduledWorkflow, workflowId: {}", workflowId);
//                continue;
//            }
//            ScheduledWorkflow scheduledWorkflow = workflowIdMap.get(workflowId);
//            WtssScheduledJobRequest wtssScheduledJobRequest = WtssScheduledJobRequest.fromScheduledTask(scheduledProject, scheduledWorkflow, scheduledTask);
//            Long ruleGroupId = scheduledTask.getScheduledWorkflowTaskRelation().getRuleGroup().getId();
//            if (ruleGroupAndSignalRelationMap.containsKey(ruleGroupId)) {
//                List<String> signalNameList = ruleGroupAndSignalRelationMap.get(ruleGroupId);
//                wtssScheduledJobRequest.setDependencies(StringUtils.join(signalNameList, SpecCharEnum.COMMA.getValue()));
//            }
//            wtssScheduledJobRequests.add(wtssScheduledJobRequest);
//        }
//        setJobRsaTo(wtssScheduledJobRequests);
//
//        return wtssScheduledJobRequests;
//    }
//
//    private List<WtssScheduledJobRequest> createWtssScheduledJobRequestByWorkflow(ScheduledProject scheduledProject, Map<Long, ScheduledWorkflow> workflowIdMap, List<ScheduledTask> scheduledTaskList) throws UnExpectedRequestException {
//        List<WtssScheduledJobRequest> wtssScheduledJobRequests = Lists.newArrayListWithCapacity(workflowIdMap.size());
//        for (Map.Entry<Long, ScheduledWorkflow> workflowEntry : workflowIdMap.entrySet()) {
//            ScheduledWorkflow scheduledWorkflow = workflowEntry.getValue();
//            WtssScheduledJobRequest wtssScheduledJobRequest = WtssScheduledJobRequest.fromScheduledWorkflow(scheduledProject, scheduledWorkflow);
//            List<String> dependencies = scheduledTaskList.stream()
//                    .filter(scheduledTask -> workflowEntry.getKey().equals(scheduledTask.getScheduledWorkflowTaskRelation().getScheduledWorkflow().getId()))
//                    .map(scheduledTask -> WtssScheduledTaskPushServiceImpl.clipRuleGroupName(scheduledTask.getTaskName())).collect(Collectors.toList());
//            wtssScheduledJobRequest.setDependencies(StringUtils.join(dependencies, SpecCharEnum.COMMA.getValue()));
//            wtssScheduledJobRequests.add(wtssScheduledJobRequest);
//        }
//        setJobRsaTo(wtssScheduledJobRequests);
//        return wtssScheduledJobRequests;
//    }
//
//    private Map<Long, List<String>> getRuleGroupOfReceiverSignalMap(Map<Long, ScheduledWorkflow> workflowIdMap, List<ScheduledSignal> scheduledSignalList) {
//        Map<Long, List<String>> ruleGroupAndSignalRelationMap = new HashMap<>();
//        for (ScheduledSignal scheduledSignal : scheduledSignalList) {
//            if (SignalTypeEnum.EVENT_RECEIVE.getCode().equals(scheduledSignal.getType())) {
//                ScheduledWorkflow scheduledWorkflow = workflowIdMap.get(scheduledSignal.getScheduledWorkflow().getId());
//                List<Long> ruleGroupRelationList = getRuleGroupOfSignalList(scheduledWorkflow, scheduledSignal);
//                ruleGroupRelationList.forEach(ruleGroupId -> {
//                    if (ruleGroupAndSignalRelationMap.containsKey(ruleGroupId)) {
//                        List<String> signalList = ruleGroupAndSignalRelationMap.get(ruleGroupId);
//                        signalList.add(scheduledSignal.getName());
//                    } else {
//                        ruleGroupAndSignalRelationMap.put(ruleGroupId, Arrays.asList(scheduledSignal.getName()));
//                    }
//                });
//            }
//        }
//        return ruleGroupAndSignalRelationMap;
//    }
//
//    private List<Long> getRuleGroupOfSignalList(ScheduledWorkflow scheduledWorkflow, ScheduledSignal scheduledSignal) {
//        if (StringUtils.isNotBlank(scheduledSignal.getRuleGroupIds())) {
//            return Arrays.stream(StringUtils.split(scheduledSignal.getRuleGroupIds(), SpecCharEnum.COMMA.getValue())).map(Long::valueOf).collect(Collectors.toList());
//        } else {
//            List<ScheduledWorkflowTaskRelation> workflowTaskRelationList = scheduledWorkflowTaskRelationDao.findByWorkflow(scheduledWorkflow);
//            return workflowTaskRelationList.stream().map(ScheduledWorkflowTaskRelation::getRuleGroup).map(RuleGroup::getId).collect(Collectors.toList());
//        }
//    }
//
//    private List<ScheduledWorkflow> deleteScheduledWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflow> deletedWorkflowList = getDeletedWorkflowOnPage(scheduledProject, workflowRequestList);
//        if (CollectionUtils.isEmpty(deletedWorkflowList)) {
//            return Collections.emptyList();
//        }
//        deleteScheduledTaskAndRelation(deletedWorkflowList);
//        scheduledSignalDao.deleteByScheduledWorkflowList(deletedWorkflowList);
////            Deleting ScheduledWorkflows
//        scheduledWorkflowDao.deleteAll(deletedWorkflowList);
//
//        return deletedWorkflowList;
//    }
//
//    private void deleteWorkflowOnWTSS(ScheduledProject scheduledProject, List<ScheduledWorkflow> deletedWorkflowList) throws UnExpectedRequestException, ScheduledPushFailedException {
//        if (CollectionUtils.isEmpty(deletedWorkflowList)) {
//            return;
//        }
////        Deleting workflow to WTSS
//        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(scheduledProject.getClusterName());
//        String serverAddress = getServerAddress(clusterInfo);
//        String sessionId = getSessionIdByReleaseUser(scheduledProject.getReleaseUser(), scheduledProject.getClusterName());
//        List<WtssScheduledJobRequest> wtssScheduledJobRequests = createScheduleJobRequestByWorkflow(scheduledProject, deletedWorkflowList);
//        for (WtssScheduledJobRequest wtssScheduledJobRequest : wtssScheduledJobRequests) {
//            if (ScheduledTaskPushService.SCHEDULE_TYPE_INTERVAL.equals(wtssScheduledJobRequest.getScheduledType())) {
//                scheduledTaskPushService.deleteIntervalJob(wtssScheduledJobRequest, sessionId, serverAddress);
//            } else if (ScheduledTaskPushService.SCHEDULE_TYPE_SIGNAL.equals(wtssScheduledJobRequest.getScheduledType())) {
//                scheduledTaskPushService.deleteSignalJob(wtssScheduledJobRequest, sessionId, serverAddress);
//            }
//        }
//    }
//
//    private void editScheduledWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflowRequest> modifyWorkflowList = getModifiedWorkflowOnPage(workflowRequestList);
//        if (CollectionUtils.isEmpty(modifyWorkflowList)) {
//            return;
//        }
//        List<ScheduledWorkflow> modifiedWorkflowList = modifyScheduledWorkflow(scheduledProject, modifyWorkflowList);
//        modifyScheduledTaskAndRelation(scheduledProject, modifiedWorkflowList, modifyWorkflowList);
//        modifyScheduledSignal(scheduledProject, modifiedWorkflowList, modifyWorkflowList);
//    }
//
//    private void addScheduledWorkflow(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> workflowRequestList) {
//        List<ScheduledWorkflowRequest> createWorkflowList = getCreatedWorkflowOnPage(workflowRequestList);
//        if (CollectionUtils.isEmpty(createWorkflowList)) {
//            return;
//        }
//        List<ScheduledWorkflow> createdWorkflowList = saveScheduledWorkflow(scheduledProject, createWorkflowList);
//        saveScheduledTaskAndRelation(scheduledProject, createdWorkflowList, createWorkflowList);
//        saveScheduledSignal(scheduledProject, createdWorkflowList, createWorkflowList);
//    }
//
//    /**
//     * Adding OPERATOR and BUSSMAN permission to proxy user
//     *
//     * @param scheduledProject
//     * @param scheduledWorkflowList
//     * @throws UnExpectedRequestException
//     * @throws PermissionDeniedRequestException
//     * @throws RoleNotFoundException
//     */
//    private void authorizePermissionForProxyUser(ScheduledProject scheduledProject, List<ScheduledWorkflowRequest> scheduledWorkflowList) throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
//        for (ScheduledWorkflowRequest scheduledWorkflow : scheduledWorkflowList) {
//            List<ProjectUser> projectUserList = projectUserDao.findByProject(scheduledProject.getProject());
//            List<Integer> permissionList = projectUserService.getPermissionList(projectUserList, scheduledWorkflow.getProxyUser());
//            if (!permissionList.contains(ProjectUserPermissionEnum.OPERATOR.getCode())
//                    || !permissionList.contains(ProjectUserPermissionEnum.BUSSMAN.getCode())) {
//                permissionList.add(ProjectUserPermissionEnum.OPERATOR.getCode());
//                permissionList.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
//                AuthorizeProjectUserRequest authorizeProjectUserRequest = new AuthorizeProjectUserRequest();
//                authorizeProjectUserRequest.setProjectId(scheduledProject.getProject().getId());
//                authorizeProjectUserRequest.setProjectUser(scheduledWorkflow.getProxyUser());
//                authorizeProjectUserRequest.setProjectPermissions(permissionList);
//                Long userId = HttpUtils.getUserId(httpServletRequest);
//                projectUserService.authorizePermission(authorizeProjectUserRequest, userId, true);
//            }
//        }
//    }
//
//    private boolean checkIfCreatedScheduleProject(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String serverAddress) throws UnExpectedRequestException, ScheduledPushFailedException {
//        Map<String, Object> resultMap = scheduledTaskPushService.getOperationHistories(wtssScheduledProjectRequest, sessionId, serverAddress);
//        if (resultMap.containsKey("logData")) {
//            List<Object> logData = (List<Object>) resultMap.get("logData");
//            if (CollectionUtils.isNotEmpty(logData)) {
//                List<String> logColumn = (List<String>) logData.get(0);
//                return !StringUtils.equals("DELETED", logColumn.get(2));
//            }
//        }
//        return false;
//    }
//
//    private String getServerAddress(ClusterInfo clusterInfo) throws UnExpectedRequestException {
//        if (Objects.isNull(clusterInfo)) {
//            throw new UnExpectedRequestException("Cluster doesn't exists");
//        }
//        Map<String, Object> wtssJsonMap = clusterInfo.getWtssJsonMap();
//        String serverAddress = MapUtils.getString(wtssJsonMap, "path");
//        if (StringUtils.isEmpty(serverAddress)) {
//            throw new UnExpectedRequestException("The address of server doesn't exists");
//        }
//        return serverAddress;
//    }

//    @Override
//    public boolean releaseSchedulesToWTSS(String wtssProjectName, String releaseWorkflowName, List<ScheduledTask> releaseTaskList, Long workflowBusinessId, String itsmNo) throws UnExpectedRequestException, ScheduledPushFailedException, IOException {
//        ScheduledProject scheduledProject = scheduledProjectDao.findByName(wtssProjectName);
//        if (scheduledProject == null) {
//            throw new UnExpectedRequestException("Not found project!");
//        }
////        将要发布的工作流
//        List<ScheduledWorkflow> releaseWorkflowList;
////        将要发布的工作流 + 已经发布过的工作流
//        List<ScheduledWorkflow> releaseAndPushedWorkflowList;
//        List<String> releaseAndPushedWorkflowNameList;
//        if (StringUtils.isNotEmpty(releaseWorkflowName)) {
//            ScheduledWorkflow scheduledWorkflow = scheduledWorkflowDao.findByScheduledProjectAndWorkflowName(scheduledProject, releaseWorkflowName);
//            if (null == scheduledWorkflow) {
//                throw new UnExpectedRequestException("Not found workflow!");
//            }
//            releaseWorkflowList = Arrays.asList(scheduledWorkflow);
//
//            List<ScheduledTask> pushedScheduledTaskList = scheduledTaskDao.findByCondition(scheduledProject.getProject().getId()
//                    , ScheduledTaskTypeEnum.PUBLISH.getCode(), scheduledProject.getDispatchingSystemType()
//                    , scheduledProject.getClusterName(), scheduledProject.getName(), StringUtils.EMPTY, StringUtils.EMPTY, 1);
//            releaseAndPushedWorkflowNameList = pushedScheduledTaskList.stream().map(ScheduledTask::getWorkFlowName).distinct().collect(Collectors.toList());
//            if (!releaseAndPushedWorkflowNameList.contains(releaseWorkflowName)) {
//                releaseAndPushedWorkflowNameList.add(releaseWorkflowName);
//            }
//            releaseAndPushedWorkflowList = scheduledWorkflowDao.findByScheduledProjectAndWorkflowNameList(scheduledProject, releaseAndPushedWorkflowNameList);
//        } else {
//            releaseAndPushedWorkflowList = scheduledWorkflowDao.findByScheduledProject(scheduledProject);
//            releaseWorkflowList = releaseAndPushedWorkflowList;
//            releaseAndPushedWorkflowNameList = releaseAndPushedWorkflowList.stream().map(ScheduledWorkflow::getName).collect(Collectors.toList());
//        }
//
//        if (CollectionUtils.isEmpty(releaseWorkflowList)) {
//            throw new UnExpectedRequestException("Not found workflows!");
//        }
//
//        List<ScheduledTask> releaseAndPushedTaskList = scheduledTaskDao.findByWorkflowList(scheduledProject.getProject().getId(), ScheduledTaskTypeEnum.PUBLISH.getCode()
//                , scheduledProject.getDispatchingSystemType(), scheduledProject.getClusterName(), scheduledProject.getName(), releaseAndPushedWorkflowNameList);
//        replaceApproveNumberWithReleaseTask(releaseAndPushedTaskList, releaseTaskList);
//
//        List<ScheduledSignal> releaseAndPushedSignalList = scheduledSignalDao.findByWorkflowList(releaseAndPushedWorkflowList);
//        List<String> releaseWorkflowNameList = releaseWorkflowList.stream().map(ScheduledWorkflow::getName).collect(Collectors.toList());
//
//        executePushToWTSS(scheduledProject, releaseAndPushedWorkflowList, releaseAndPushedTaskList, releaseAndPushedSignalList, releaseWorkflowNameList, workflowBusinessId, itsmNo);
//        return true;
//    }

//    private void replaceApproveNumberWithReleaseTask(List<ScheduledTask> releaseAndPushedTaskList, List<ScheduledTask> releaseTaskList) {
//        Map<Long, ScheduledTask> idScheduledTaskMap = releaseTaskList.stream().collect(Collectors.toMap(ScheduledTask::getId, Function.identity(), (oldVal, newVal) -> oldVal));
//        for (ScheduledTask scheduledTask : releaseAndPushedTaskList) {
//            if (idScheduledTaskMap.containsKey(scheduledTask.getId())) {
//                scheduledTask.setApproveNumber(idScheduledTaskMap.get(scheduledTask.getId()).getApproveNumber());
//            }
//        }
//    }
//
//    private void executePushToWTSS(ScheduledProject scheduledProject, List<ScheduledWorkflow> scheduledWorkflowList
//            , List<ScheduledTask> scheduledTaskList, List<ScheduledSignal> scheduledSignalList
//            , List<String> allowedWorkflowList, Long workflowBusinessId, String itsmNo)
//            throws UnExpectedRequestException, ScheduledPushFailedException, IOException {
//        Map<Long, ScheduledWorkflow> workflowIdMap = scheduledWorkflowList.stream().collect(Collectors.toMap(ScheduledWorkflow::getId, v -> v, (oValue, nValue) -> nValue));
//        List<WtssScheduledJobRequest> wtssScheduledJobRequestList = createWtssScheduledJobRequestList(scheduledProject, workflowIdMap, scheduledTaskList, scheduledSignalList);
//
////        Pushing the schedule project to WTSS
//        String sessionId = getSessionIdByReleaseUser(scheduledProject.getReleaseUser(), scheduledProject.getClusterName());
//        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(scheduledProject.getClusterName());
//        String serverAddress = getServerAddress(clusterInfo);
//        WtssScheduledProjectRequest wtssScheduledProjectRequest = new WtssScheduledProjectRequest(scheduledProject);
//        if (!checkIfCreatedScheduleProject(wtssScheduledProjectRequest, sessionId, serverAddress)) {
////            create schedule project to WTSS
//            scheduledTaskPushService.createProject(wtssScheduledProjectRequest, sessionId, serverAddress);
//        }
//        String scheduleProjectId = scheduledTaskPushService.uploadProject(wtssScheduledJobRequestList, clusterInfo.getJobServerJsonMap(), sessionId, serverAddress);
//        if (StringUtils.isBlank(scheduleProjectId)) {
//            throw new UnExpectedRequestException("Failed to upload project to WTSS: cannot get project id from WTSS.");
//        }
//        Optional<ScheduledWorkflowBusiness> scheduledWorkflowBusiness = scheduledWorkflowBusinessDao.get(workflowBusinessId);
//        if (!scheduledWorkflowBusiness.isPresent()) {
//            throw new UnExpectedRequestException("ScheduledWorkflowBusiness doesn't exists.");
//        }
//
////        commit flow business before upload material package to WTSS
//        boolean submitedWorkflowBusiness = scheduledTaskPushService.submitWorkflowBusiness(scheduledWorkflowBusiness.get(), scheduledWorkflowList, sessionId, serverAddress, scheduleProjectId, itsmNo);
//
////        Pushing the schedule workflow to WTSS, and update ScheduledWorkflow with scheduleId
//        try {
//            for (WtssScheduledJobRequest wtssScheduledJobRequest : wtssScheduledJobRequestList) {
//                if (!allowedWorkflowList.contains(wtssScheduledJobRequest.getWorkflowName())) {
//                    continue;
//                }
//                if (ScheduledTaskPushService.NODE_LEVEL_FLOW.equals(wtssScheduledJobRequest.getNodeLevel()) && Objects.nonNull(wtssScheduledJobRequest.getWorkflowId())) {
//                    Long scheduleId;
//                    if (ScheduledTaskPushService.SCHEDULE_TYPE_INTERVAL.equals(wtssScheduledJobRequest.getScheduledType())) {
//                        scheduleId = scheduledTaskPushService.createIntervalWorkflow(wtssScheduledJobRequest, sessionId, serverAddress, itsmNo);
//                    } else {
//                        scheduleId = scheduledTaskPushService.createSignalWorkflow(wtssScheduledJobRequest, sessionId, serverAddress);
//                    }
//                    ScheduledWorkflow scheduledWorkflow = workflowIdMap.get(wtssScheduledJobRequest.getWorkflowId());
//                    scheduledWorkflow.setScheduleId(scheduleId);
//                    scheduledWorkflow.setWorkflowBusinessName(scheduledWorkflowBusiness.get().getName());
//                }
//            }
//        } catch (Exception e) {
//            if (!submitedWorkflowBusiness) {
//                throw new ScheduledPushFailedException("{&FAILTED_TO_CHECK_IN_BUSINESS_TO_WTSS}");
//            } else {
//                throw e;
//            }
//        }
//
//        scheduledWorkflowDao.saveAll(scheduledWorkflowList);
//    }

}
