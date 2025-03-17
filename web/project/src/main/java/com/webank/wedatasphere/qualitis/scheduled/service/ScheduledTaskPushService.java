package com.webank.wedatasphere.qualitis.scheduled.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.WtssScheduledJobRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.WtssScheduledProjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 18:16
 * @description
 */
public interface ScheduledTaskPushService {

    Integer NODE_LEVEL_FLOW = 1;
    Integer NODE_LEVEL_TASK = 2;
    Integer NODE_LEVEL_SIGNAL = 3;

    String SCHEDULE_TYPE_INTERVAL = "interval";
    String SCHEDULE_TYPE_SIGNAL = "signal";

    /**
     * getOperationHistories
     *
     * @param wtssScheduledProjectRequest
     * @param sessionId
     * @param address
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    void deleteProject(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws UnExpectedRequestException, ScheduledPushFailedException;

    /**
     * get operation history from WTSS
     *
     * @param wtssScheduledProjectRequest
     * @param sessionId
     * @param address
     * @return
     * @throws UnExpectedRequestException
     * @throws ScheduledPushFailedException
     */
    Map<String, Object> getOperationHistories(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws UnExpectedRequestException, ScheduledPushFailedException;

    /**
     * create Schedule Project
     *
     * @param wtssScheduledProjectRequest
     * @param sessionId
     * @param address
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    void createProject(WtssScheduledProjectRequest wtssScheduledProjectRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * upload zip to Schedule Project
     *
     * @param wtssScheduledJobRequests
     * @param sessionId
     * @param jsonServerMap
     * @param address
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    String uploadProject(List<WtssScheduledJobRequest> wtssScheduledJobRequests, Map<String, Object> jsonServerMap, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException, IOException;

    /**
     * Add workflow after upload project to scheduled system
     *
     * @param wtssScheduledJobRequest
     * @param sessionId
     * @param address
     * @return
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    Long createIntervalWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address, Object itsmNo) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * create Signal Work flow
     *
     * @param wtssScheduledJobRequest
     * @param sessionId
     * @param address
     * @return
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    Long createSignalWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * Modify workflow on scheduled system
     *
     * @param wtssScheduledJobRequest
     * @param sessionId
     * @param address
     * @return
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    Long modifyScheduleWorkflow(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * Delete workflow on scheduled system
     *
     * @param wtssScheduledJobRequest
     * @param sessionId
     * @param address
     * @return
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    void deleteIntervalJob(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * delete Signal Job
     *
     * @param wtssScheduledJobRequest
     * @param sessionId
     * @param address
     * @throws ScheduledPushFailedException
     * @throws UnExpectedRequestException
     */
    void deleteSignalJob(WtssScheduledJobRequest wtssScheduledJobRequest, String sessionId, String address) throws ScheduledPushFailedException, UnExpectedRequestException;

    /**
     * push relation task to WTSS
     *
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param frontRuleGroup
     * @param backRuleGroup
     * @param hiveUrn
     * @param sessionId
     * @param address
     * @return
     * @throws UnExpectedRequestException
     * @throws ScheduledPushFailedException
     */
    Map<String, Object> synchronousRelationTask(String projectName, String workFlow, String taskName
            , List<Long> frontRuleGroup, List<Long> backRuleGroup, String hiveUrn, String sessionId, String address) throws UnExpectedRequestException;

    /**
     *  submit buz
     * @param scheduledWorkflowBusiness
     * @param scheduledWorkflowList
     * @param sessionId
     * @param address
     * @param scheduleProjectId
     * @throws UnExpectedRequestException
     * @throws ScheduledPushFailedException
     */
    boolean submitWorkflowBusiness(ScheduledWorkflowBusiness scheduledWorkflowBusiness, List<ScheduledWorkflow> scheduledWorkflowList
            , String sessionId, String address, String scheduleProjectId, String itsmNo) throws UnExpectedRequestException;

}
