package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.TaskNewValueRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.ModifyTaskNewValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.TaskNewValueResponse;
import com.webank.wedatasphere.qualitis.rule.service.TaskNewValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author v_gaojiedeng@webank.com
 */
@Path("api/v1/projector/taskNewValue")
public class TaskNewValueController {

    @Autowired
    private TaskNewValueService taskNewValueService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskNewValueController.class);

    @POST
    @Path("delete/{task_new_value_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteTaskNewValue(@PathParam("task_new_value_id") Long taskNewValueId) throws UnExpectedRequestException {
        try {
            taskNewValueService.deleteTaskNewValue(taskNewValueId);
            return new GeneralResponse<>("200", "{&DELETE_TASK_NEW_VALUE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete TaskNewValue. task_new_value_id: {}, caused by system error: {}", taskNewValueId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_TASK_NEW_VALUE}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<TaskNewValueResponse> modifyTaskNewValue(ModifyTaskNewValueRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&MODIFY_TASK_NEW_VALUE_SUCCESSFULLY}", taskNewValueService.modifyTaskNewValue(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify TaskNewValue . task_new_value_id: {}, caused by system error: {}", request.getTaskNewValueId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_TASK_NEW_VALUE}", null);
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<TaskNewValueResponse>> getAllTaskNewValue(TaskNewValueRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&GET_ALL_TASK_NEW_VALUE_SUCCESSFULLY}", taskNewValueService.getAllTaskNewValue(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get TaskNewValue, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_TASK_NEW_VALUE}", null);
        }
    }

    @GET
    @Path("/{task_new_value_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<TaskNewValueResponse> getTaskNewValueDetail(@PathParam("task_new_value_id") Long taskNewValueId) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&GET_TASK_NEW_VALUE_DETAIL_SUCCESSFULLY}", taskNewValueService.getTaskNewValueIdDetail(taskNewValueId));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get TaskNewValue detail. task_new_value_id: {}, caused by system error: {}", taskNewValueId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_TASK_NEW_VALUE_DETAIL}", null);
        }
    }
}
