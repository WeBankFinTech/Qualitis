package com.webank.wedatasphere.qualitis.scheduled.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.scheduled.exception.ScheduledPushFailedException;
import com.webank.wedatasphere.qualitis.scheduled.request.AddScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ModifyScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledTaskRequest;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledProjectDetailResponse;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.RoleNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-18 11:10
 * @description
 */
@Path("api/v1/projector/scheduled/project")
public class ScheduledProjectController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledProjectController.class);

    @Autowired
    private ScheduledProjectService scheduledProjectService;

    @POST
    @Path("checkIfExists")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse isCreatedScheduleProject(AddScheduledTaskRequest request) throws UnExpectedRequestException, ScheduledPushFailedException {
        CommonChecker.checkString(request.getScheduledProjectName(), "scheduled_project_name");
        CommonChecker.checkString(request.getClusterName(), "cluster_name");
        return new GeneralResponse(ResponseStatusConstants.OK, "success", scheduledProjectService.isCreatedScheduleProject(request));
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse add(AddScheduledTaskRequest request) throws UnExpectedRequestException {
        request.checkRequest();
        try {
            scheduledProjectService.add(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (ScheduledPushFailedException e) {
            LOGGER.error("Failed to push task to scheduled system, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_PUSH_WTSS}", null);
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to authorize permissions to proxy user: permission denied");
        } catch (RoleNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to authorize permissions to proxy user: role not found");
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modify(ModifyScheduledTaskRequest modifyScheduledTaskRequest) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException, RoleNotFoundException {
        try {
            scheduledProjectService.modify(modifyScheduledTaskRequest);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (ScheduledPushFailedException e) {
            LOGGER.error("Failed to push task to scheduled system, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_PUSH_WTSS}", null);
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to authorize permissions to proxy user: permission denied");
        } catch (RoleNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to authorize permissions to proxy user: role not found");
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @DELETE
    @Path("{scheduledProjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse delete(@PathParam("scheduledProjectId") Long scheduledProjectId) throws UnExpectedRequestException,IOException {
        try {
            scheduledProjectService.delete(scheduledProjectId);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
        } catch (ScheduledPushFailedException e) {
            LOGGER.error("Failed to push task to scheduled system, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_PUSH_WTSS}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("{scheduledProjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ScheduledProjectDetailResponse> getProjectDetail(@PathParam("scheduledProjectId") Long scheduledProjectId) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledProjectService.getProjectDetail(scheduledProjectId));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("rule/group/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getRuleGroupList(@PathParam("projectId") Long projectId) {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledProjectService.findRuleGroupNotInFrontBackRule(projectId));
    }

    @POST
    @Path("option/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getProjectList(ScheduledTaskRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getScheduleSystem(), "schedule_system");
        CommonChecker.checkString(request.getCluster(), "cluster");
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledProjectService.getProjectOptionList(request));
    }

}
