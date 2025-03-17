package com.webank.wedatasphere.qualitis.scheduled.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.scheduled.request.PublishUserRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledFormRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledProjectRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.WorkFlowRequest;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author v_gaojiedeng@webank.com
 */
@Path("api/v1/projector/scheduled/config")
public class ScheduledConfigController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledConfigController.class);

    @Autowired
    private ScheduledConfigService scheduledConfigService;

    @POST
    @Path("get/All/publish/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getAllPublishUser(PublishUserRequest request) throws UnExpectedRequestException {
        try {

            return scheduledConfigService.getAllPublishUser(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get publish user from wtss. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_PROJECT_FROM_WTSS}", null);
        }
    }

    @POST
    @Path("get/All/project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getAllProject(ScheduledProjectRequest scheduledProjectRequest) throws UnExpectedRequestException {
        try {
            return scheduledConfigService.getScheduledProject(scheduledProjectRequest);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get project from wtss. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_PROJECT_FROM_WTSS}", null);
        }
    }


    @POST
    @Path("getWorkFlow")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getWorkFlow(WorkFlowRequest request) throws UnExpectedRequestException {
        try {
            return scheduledConfigService.getScheduledWorkFlow(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get Work Flow from wtss. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_WORK_FLOW_FROM_WTSS}", null);
        }
    }

    @POST
    @Path("getTask")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getWtssTask(ScheduledFormRequest request) throws UnExpectedRequestException {
        try {
            return scheduledConfigService.getScheduledTask(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get task from wtss. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_TASK_FROM_WTSS}", null);
        }
    }


}
