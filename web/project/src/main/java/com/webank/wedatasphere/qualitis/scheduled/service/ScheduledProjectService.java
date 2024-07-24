package com.webank.wedatasphere.qualitis.scheduled.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.AddScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ModifyScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledProjectDetailResponse;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 10:29
 * @description
 */
public interface ScheduledProjectService {

    /**
     * isCreatedScheduleProject
     * @param request
     * @return
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    Boolean isCreatedScheduleProject(AddScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException;
    /**
     * add
     * @param request
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     * @throws RoleNotFoundException
     */
    void add(AddScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * modify
     * @param request
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     * @throws RoleNotFoundException
     */
    void modify(ModifyScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException, IOException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Delete scheduled project、scheduled workflow、scheduled task
     * @param scheduledProjectId
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    void delete(Long scheduledProjectId) throws ScheduledPushFailedException, UnExpectedRequestException, IOException;

    /**
     * Find rule group by projectId
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findRuleGroupNotInFrontBackRule(Long projectId);


    /**
     * Find detail of scheduled project by id
     * @param scheduledProjectId
     * @return
     * @throws UnExpectedRequestException
     */
    ScheduledProjectDetailResponse getProjectDetail(Long scheduledProjectId) throws UnExpectedRequestException;

    /**
     * Find list of id and name
     * @param request
     * @return
     */
    List<Map<String, Object>> getProjectOptionList(ScheduledTaskRequest request);

    /**
     * push schedules to WTSS
     * @param wtssProjectName
     * @param releaseWorkflowName
     * @param releaseTaskList
     * @throws UnExpectedRequestException
     * @throws ScheduledPushFailedException
     * @throws IOException
     * @return
     */
    boolean releaseSchedulesToWTSS(String wtssProjectName, String releaseWorkflowName, List<ScheduledTask> releaseTaskList) throws UnExpectedRequestException, ScheduledPushFailedException, IOException;

}
