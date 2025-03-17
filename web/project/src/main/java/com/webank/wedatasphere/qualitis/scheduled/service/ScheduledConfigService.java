package com.webank.wedatasphere.qualitis.scheduled.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.scheduled.request.PublishUserRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledFormRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.WorkFlowRequest;

import java.io.IOException;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledConfigService {

    /**
     * get get All Publish User
     *
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @param request
     */
    GeneralResponse<Map<String, Object>> getAllPublishUser(PublishUserRequest request) throws UnExpectedRequestException, IOException;

    /**
     * get Scheduled  Project
     *
     * @param scheduledProjectRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    GeneralResponse<Map<String, Object>> getScheduledProject(ScheduledProjectRequest scheduledProjectRequest) throws UnExpectedRequestException, IOException;

    /**
     * get Scheduled Work Flow
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    GeneralResponse<Map<String, Object>> getScheduledWorkFlow(WorkFlowRequest request) throws UnExpectedRequestException, IOException;


    /**
     * get Scheduled Task
     *
     * @param scheduledFormRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    GeneralResponse<Map<String, Object>> getScheduledTask(ScheduledFormRequest scheduledFormRequest) throws UnExpectedRequestException, IOException;


}
