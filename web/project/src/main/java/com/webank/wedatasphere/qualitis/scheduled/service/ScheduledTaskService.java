package com.webank.wedatasphere.qualitis.scheduled.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.request.AddScheduledRelationProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.DataLatitudeRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.DeleteScheduledRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ModifyRuleGroupRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ModifyScheduledRelationProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.PublishScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledFrontBackRuleRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledReleaseRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.TableRuleGroupRequest;
import com.webank.wedatasphere.qualitis.scheduled.response.RelationRuleGroupResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledPublishTaskResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledTaskResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.SpecialRuleGroupResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.TableListResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledTaskService {

    /**
     * add ScheduledTask
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse addScheduledTask(AddScheduledRelationProjectRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * modify ScheduledTask
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse modifyScheduledTask(ModifyScheduledRelationProjectRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * modify frontAndBackRuleGroup
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse frontAndBackRuleGroup(ModifyRuleGroupRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;


    /**
     * delete ScheduledTask
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<Object> deleteScheduledTask(DeleteScheduledRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * get All ScheduledTask
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ScheduledTaskResponse>> getAllScheduledTask(ScheduledTaskRequest request) throws UnExpectedRequestException;

    /**
     * 根据scheduledTaskId获取ScheduledTask详情
     *
     * @param scheduledTaskFrontBackId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<ScheduledTaskResponse> getScheduledTaskDetail(Long scheduledTaskFrontBackId) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * get scheduled enum
     *
     * @return
     */
    List<Map<String, Object>> getAllApproveEnum();

    /**
     * getDbAndTableQuery
     *
     * @param request
     * @throws UnExpectedRequestException
     * @return
     */
    List<Map<String, Object>> getDbAndTableQuery(DataLatitudeRequest request) throws UnExpectedRequestException;

    /**
     * get ScheduledTask of publish type
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GetAllResponse<ScheduledPublishTaskResponse> getPublishScheduledTask(PublishScheduledTaskRequest request) throws UnExpectedRequestException;


    /**
     * find cluster list by scheduled system
     * @param taskType
     * @param scheduleSystem
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     */
    List<String> getOptionClusterList(Integer taskType, String scheduleSystem, Long projectId) throws UnExpectedRequestException;

    /**
     * find project list by cluster
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     */
    List<String> getOptionProjectList(Integer taskType, String scheduleSystem, String clusterName, Long projectId) throws UnExpectedRequestException;

    /**
     * find workflow list by project
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param projectName
     * @param projectId
     * @throws UnExpectedRequestException
     * @return
     */
    List<String> getOptionWorkflowList(Integer taskType, String scheduleSystem, String clusterName, String projectName, Long projectId) throws UnExpectedRequestException;

    /**
     * find task list by workflow
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param projectName
     * @param workflowName
     * @param projectId
     * @throws UnExpectedRequestException
     * @return
     */
    List<String> getOptionTaskList(Integer taskType, String scheduleSystem, String clusterName, String projectName, String workflowName, Long projectId) throws UnExpectedRequestException;

    /**
     * find Rule Group Scheduled Work flow Task Relation
     * @param projectId
     * @return
     */
    List<SpecialRuleGroupResponse> findRuleGroupScheduledWorkflowTaskRelation(Long projectId);

    /**
     * find RuleGroup For Table
     * @param request
     * @throws UnExpectedRequestException
     * @return
     */
    List<Map<String, Object>> findRuleGroupForTable(TableRuleGroupRequest request) throws UnExpectedRequestException;

    /**
     *find Front And BackData
     * @param request
     * @throws UnExpectedRequestException
     * @return
     */
    RelationRuleGroupResponse findFrontAndBackData(ScheduledFrontBackRuleRequest request) throws UnExpectedRequestException;

    /**
     * findDbsAndTable
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findDbsAndTable(Long projectId);

    /**
     * findTableListDistinct
     * @param request
     * @throws UnExpectedRequestException
     * @return
     */
    TableListResponse findTableListDistinct(DataLatitudeRequest request) throws UnExpectedRequestException;

    /**
     * check If Existence Project Name
     * @param projectName
     * @throws UnExpectedRequestException
     * @throws IOException
     * @return
     */
    void deleteRelationScheduleTasks(String projectName) throws UnExpectedRequestException;

    /**
     * release schedule task to WTSS
     * @param request
     * @throws Exception
     */
    void release(ScheduledReleaseRequest request) throws Exception;

    /**
     * checkRuleGroupIfDependedBySchedule
     * @param ruleGroup
     * @throws UnExpectedRequestException
     */
    void checkRuleGroupIfDependedBySchedule(RuleGroup ruleGroup) throws UnExpectedRequestException;

    /**
     * 如果关联调度和发布调度都未发布，则将其删除
     * @param project
     * @throws UnExpectedRequestException
     */
    void deleteAllUnreleasedSchedules(Project project) throws UnExpectedRequestException;

}
