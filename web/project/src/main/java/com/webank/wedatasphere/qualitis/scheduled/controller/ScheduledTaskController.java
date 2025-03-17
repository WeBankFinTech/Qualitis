package com.webank.wedatasphere.qualitis.scheduled.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.scheduled.constant.ScheduledTaskTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.request.*;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledPublishTaskResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledTaskResponse;
import com.webank.wedatasphere.qualitis.scheduled.response.TableListResponse;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskService;
import com.webank.wedatasphere.qualitis.util.RequestParametersUtils;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author v_gaojiedeng@webank.com
 */
@Path("api/v1/projector/scheduledTask")
public class ScheduledTaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskController.class);

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @POST
    @Path("system/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getSystemEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SCHEDULED_SYSTEM_ENUMN_SUCCESSFULLY}", scheduledTaskService.getAllApproveEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get Scheduled System enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SCHEDULED_SYSTEM_ENUMN}", e.getMessage());
        }
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse addScheduledTask(AddScheduledRelationProjectRequest request) throws UnExpectedRequestException {
        try {
            return scheduledTaskService.addScheduledTask(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add ScheduledTask. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAIL_TO_ASSOCIATED_SCHEDULING_TO_WTSS}", e.getMessage());
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteScheduledTask(DeleteScheduledRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return scheduledTaskService.deleteScheduledTask(request);
        } catch (UnExpectedRequestException | PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete ScheduledTask.  caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAIL_TO_ASSOCIATED_SCHEDULING_TO_WTSS}", e.getMessage());
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyScheduledTask(ModifyScheduledRelationProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return scheduledTaskService.modifyScheduledTask(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify ScheduledTask , caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAIL_TO_ASSOCIATED_SCHEDULING_TO_WTSS}", e.getMessage());
        }
    }

    @POST
    @Path("rule/group/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> ruleGroupModify(ModifyRuleGroupRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return scheduledTaskService.frontAndBackRuleGroup(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify ScheduledTask , caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAIL_TO_ASSOCIATED_SCHEDULING_TO_WTSS}", e.getMessage());
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ScheduledTaskResponse>> getAllScheduledTask(ScheduledTaskRequest request) throws UnExpectedRequestException {
        try {
            return scheduledTaskService.getAllScheduledTask(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get ScheduledTask, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SCHEDULED_TASK}", null);
        }
    }

    @POST
    @Path("data/set/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getDataQuery(DataLatitudeRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DATA_SET_QUERY_SUCCESSFULLY}", scheduledTaskService.getDbAndTableQuery(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get data set query, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DATA_SET_QUERY}", null);
        }
    }

    @POST
    @Path("drop/down/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<TableListResponse> dropDownList(DataLatitudeRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DATA_SET_QUERY_SUCCESSFULLY}", scheduledTaskService.findTableListDistinct(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get data set query, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DATA_SET_QUERY}", null);
        }
    }



    @POST
    @Path("/table/rule/group")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getTableRuleGroupList(TableRuleGroupRequest request) throws UnExpectedRequestException {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success",scheduledTaskService.findRuleGroupForTable(request));
    }

    @GET
    @Path("/{scheduled_task_front_back_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getScheduledTaskDetail(@PathParam("scheduled_task_front_back_id") Long scheduledTaskFrontBackId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return scheduledTaskService.getScheduledTaskDetail(scheduledTaskFrontBackId);
        } catch (UnExpectedRequestException | PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get ScheduledTask detail. scheduled_task_front_back_id: {}, caused by system error: {}", scheduledTaskFrontBackId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SCHEDULED_TASK_DETAIL}", e.getMessage());
        }
    }

    @POST
    @Path("get/rule/group")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getRuleGroup(ScheduledFrontBackRuleRequest request) throws UnExpectedRequestException {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.findFrontAndBackData(request));
    }


    @GET
    @Path("rule/group/{project_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getRuleGroupNotInScheduledWorkflowTaskRelation(@PathParam("project_id") Long projectId) {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.findRuleGroupScheduledWorkflowTaskRelation(projectId));
    }

    @POST
    @Path("/publish/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ScheduledPublishTaskResponse> queryPublishTask(PublishScheduledTaskRequest request) throws UnExpectedRequestException {
        try {
            RequestParametersUtils.emptyStringToNull(request);
            PublishScheduledTaskRequest.checkRequest(request);
            GetAllResponse<ScheduledPublishTaskResponse> getAllResponse = scheduledTaskService.getPublishScheduledTask(request);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", getAllResponse);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get publish ScheduledTask . caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SCHEDULEDTASK}", null);
        }
    }

    @GET
    @Path("option/publish/dbs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse queryDbs(@QueryParam("project_id")Long projectId) {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", scheduledTaskService.findDbsAndTable(projectId));
    }

    @POST
    @Path("option/cluster/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getClusterList(ScheduledTaskRequest request) throws UnExpectedRequestException {
        RequestParametersUtils.emptyStringToNull(request);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.getOptionClusterList(request.getTaskType(), request.getScheduleSystem(), request.getProjectId()));
    }


    @POST
    @Path("option/project/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getWtssProjectNameList(ScheduledTaskRequest request) throws UnExpectedRequestException {
        RequestParametersUtils.emptyStringToNull(request);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.getOptionProjectList(request.getTaskType(), request.getScheduleSystem(), request.getCluster(), request.getProjectId()));
    }

    @POST
    @Path("option/workflow/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getWorkflowNameList(ScheduledTaskRequest request) throws UnExpectedRequestException {
        RequestParametersUtils.emptyStringToNull(request);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.getOptionWorkflowList(request.getTaskType(), request.getScheduleSystem(), request.getCluster(), request.getWtssProjectName(), request.getProjectId()));
    }

    @POST
    @Path("option/task/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getTaskNameList(ScheduledTaskRequest request) throws UnExpectedRequestException {
        RequestParametersUtils.emptyStringToNull(request);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", scheduledTaskService.getOptionTaskList(request.getTaskType(), request.getScheduleSystem(), request.getCluster(), request.getWtssProjectName(), request.getWorkFlow(), request.getProjectId()));
    }

    @POST
    @Path("release")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse release(ScheduledReleaseRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getProjectId(), "project_id");
        CommonChecker.checkObject(request.getTaskType(), "task_type");
        CommonChecker.checkString(request.getApproveNumber(), "approve_number");
        CommonChecker.checkString(request.getScheduleSystem(), "schedule_system");
        CommonChecker.checkString(request.getCluster(), "cluster");
        CommonChecker.checkString(request.getWtssProjectName(), "scheduled_project_name");
        CommonChecker.checkStringLength(request.getApproveNumber(), 30, "approve_number");
        if (ScheduledTaskTypeEnum.fromCode(request.getTaskType()) == null) {
            throw new UnExpectedRequestException("Error parameter: task_type!");
        }

        if (ScheduledTaskTypeEnum.PUBLISH.getCode().equals(request.getTaskType())) {
            CommonChecker.checkObject(request.getWorkflowBusinessId(), "workflow_business_id");
        }

        try {
            scheduledTaskService.release(request);
        } catch (UnExpectedRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new UnExpectedRequestException("Error!Failed to release task.");
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", null);
    }

}
