package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.ExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
import com.webank.wedatasphere.qualitis.rule.service.ExecutionParametersService;
import com.webank.wedatasphere.qualitis.util.RequestParametersUtils;
import javax.ws.rs.QueryParam;
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
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng
 */
@Path("api/v1/projector/execution_parameters")
public class ExecutionParametersController {

    @Autowired
    private ExecutionParametersService executionParametersService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionParametersController.class);


    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ExecutionParametersResponse> addExecutionParameters(AddExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return executionParametersService.addExecutionParameters(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add ExecutionParameters. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_EXECUTIONPARAMETERS}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> deleteExecutionParameters(DeleteExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return executionParametersService.deleteExecutionParameters(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete ExecutionParameters. execution_parameters_id: {}, caused by system error: {}", request.getExecutionParametersId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_EXECUTIONPARAMETERS}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ExecutionParametersResponse> modifyRuleDetail(ModifyExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return executionParametersService.modifyExecutionParameters(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify ExecutionParameters detail. execution_parameters_id: {}, caused by system error: {}", request.getExecutionParametersId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_EXECUTIONPARAMETERS}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ExecutionParametersResponse>> getAllExecutionParameters(ExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return executionParametersService.getAllExecutionParameters(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get ExecutionParameters, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ALL_EXECUTIONPARAMETERS}", null);
        }
    }

    @GET
    @Path("/{execution_parameters_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ExecutionParametersResponse> getRuleDetail(@PathParam("execution_parameters_id") Long executionParametersId) throws UnExpectedRequestException {
        try {
            return executionParametersService.getExecutionParametersDetail(executionParametersId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get ExecutionParameters detail. execution_parameters_id: {}, caused by system error: {}", executionParametersId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_EXECUTIONPARAMETERS_DETAIL}", null);
        }
    }

    @POST
    @Path("execution/type/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getExecutionTypeEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_EXECUTION_TYPE_ENUMN_SUCCESSFULLY}", executionParametersService.getAllStaticArgumentsTypeEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get execution type enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_EXECUTION_TYPE_ENUMN}", null);
        }
    }

    @POST
    @Path("alarm/type/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getAlarmTypeEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_ALARM_TYPE_ENUMN_SUCCESSFULLY}", executionParametersService.getAllAlarmEventTypeEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get alarm type enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ALARM_TYPE_ENUMN}", null);
        }
    }


    @POST
    @Path("select/method/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getSelectMethodEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SELECT_METHOD_ENUMN_SUCCESSFULLY}", executionParametersService.getAllDataSelectMethodEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get select method enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SELECT_METHOD_ENUMN}", null);
        }
    }

    @POST
    @Path("noise/strategy/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getNoiseStrategyEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_NOISE_STRATEGY_ENUMN_SUCCESSFULLY}", executionParametersService.getAllNoiseStrategyEnumEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get noise strategy enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_NOISE_STRATEGY_ENUMN}", null);
        }
    }


    @POST
    @Path("dynamic/engine/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getDynamicEngineEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DYNAMIC_ENGINE_ENUMN_SUCCESSFULLY}", executionParametersService.getAllDynamicEngineEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get dynamic engine enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DYNAMIC_ENGINE_ENUMN}", null);
        }
    }


    @GET
    @Path("/engine/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getEngineJudgeEnumn(@PathParam("code") Integer code) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_ENGINE_ENUMN_SUCCESSFULLY}", executionParametersService.getAllEngineJudgeEnum(code));
        } catch (Exception e) {
            LOGGER.error("Failed to get engine enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ENGINE_ENUMN}", null);
        }
    }

    @POST
    @Path("execution/variable/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getExecutionVariableEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_EXECUTION_VARIABLE_ENUMN_SUCCESSFULLY}", executionParametersService.getAllExecutionVariableEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get execution variable enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_EXECUTION_VARIABLE_ENUMN}", null);
        }
    }

    @GET
    @Path("/synchronization/variable")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> synchronizationVariable(){
        try {
            return executionParametersService.handleExecutionParametersDataMigration();
        }  catch (Exception e) {
            LOGGER.error("Failed to synchronization variable. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_SYNCHRONIZATION_VARIABLE}", null);
        }
    }

    @GET
    @Path("/current/securitesworkdays")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getSecuritiesWorkDays(@QueryParam("start_time") String startTime, @QueryParam("end_time") String endTime) {
        try {
            return executionParametersService.getSecuritiesWorkDays(startTime, endTime);
        }  catch (Exception e) {
            LOGGER.error("Failed to get current holidays. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "failed", null);
        }
    }

    @GET
    @Path("/current/bankworkdays")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getBankWorkDays(@QueryParam("start_time") String startTime, @QueryParam("end_time") String endTime) {
        try {
            return executionParametersService.getBankWorkDays(startTime, endTime);
        }  catch (Exception e) {
            LOGGER.error("Failed to get current holidays. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "failed", null);
        }
    }

    @GET
    @Path("/current/holidays")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getCurrentHolidays(@QueryParam("start_time") String startTime, @QueryParam("end_time") String endTime) {
        try {
            return executionParametersService.getCurrentHolidays(startTime, endTime);
        }  catch (Exception e) {
            LOGGER.error("Failed to get current holidays. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "failed", null);
        }
    }

    @GET
    @Path("/current/weeks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getCurrentWeeks(@QueryParam("start_time") String startTime, @QueryParam("end_time") String endTime) {
        try {
            return executionParametersService.getCurrentWeeks(startTime, endTime);
        }  catch (Exception e) {
            LOGGER.error("Failed to get current weeks. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "failed", null);
        }
    }


}
