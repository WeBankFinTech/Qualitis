package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.ExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng
 */
public interface ExecutionParametersService {

    /**
     * add ExecutionParameters for bdp-client
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<ExecutionParametersResponse> addExecutionParametersForOuter(AddExecutionParametersRequest request,
        String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * add ExecutionParameters
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ExecutionParametersResponse> addExecutionParameters(AddExecutionParametersRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * modify ExecutionParameters
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ExecutionParametersResponse> modifyExecutionParameters(ModifyExecutionParametersRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * delete ExecutionParameters
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse deleteExecutionParameters(DeleteExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get All ExecutionParameters
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<GetAllResponse<ExecutionParametersResponse>> getAllExecutionParameters(ExecutionParametersRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get ExecutionParameters Detail
     * @param executionParametersId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ExecutionParametersResponse> getExecutionParametersDetail(Long executionParametersId) throws UnExpectedRequestException;

    /**
     * modify ExecutionParameters for bdp-client
     * @param modifyExecutionParametersRequest
     * @param createUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ExecutionParametersResponse> modifyExecutionParametersForOuter(ModifyExecutionParametersRequest modifyExecutionParametersRequest, String createUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Static Arguments Type Enum
     * @return
     */
    List<Map<String, Object>> getAllStaticArgumentsTypeEnum();

    /**
     * Alarm Event Type Enum
     * @return
     */
    List<Map<String, Object>> getAllAlarmEventTypeEnum();


    /**
     * Data Select Method Enum
     * @return
     */
    List<Map<String, Object>> getAllDataSelectMethodEnum();


    /**
     * Noise Strategy Enum
     * @return
     */
    List<Map<String, Object>> getAllNoiseStrategyEnumEnum();

    /**
     * Dynamic Engine Enum
     * @return
     */
    List<Map<String, Object>> getAllDynamicEngineEnum();

    /**
     * Dynamic Engine init Enum
     * @param code
     * @return
     * @throws UnExpectedRequestException
     */
    List<Map<String, Object>> getAllEngineJudgeEnum(Integer code) throws UnExpectedRequestException;

    /**
     * Execution Variable Enum
     * @return
     */
    List<String> getAllExecutionVariableEnum();

    /**
     * 执行参数模板 执行参数execution_param数据迁移到qualitis_execution_variable
     * handle ExecutionParameters Data Migration
     * @return
     */
    GeneralResponse handleExecutionParametersDataMigration();
}
