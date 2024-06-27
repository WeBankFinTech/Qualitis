package com.webank.wedatasphere.qualitis.report.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.report.request.OperateReportQueryRequest;
import com.webank.wedatasphere.qualitis.report.request.SubscribeOperateReportRequest;
import com.webank.wedatasphere.qualitis.report.response.SubscribeOperateReportResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscribeOperateReportService {
    /**
     * Add
     * @param request
     * @return
     * @throws Exception
     */
    GeneralResponse<SubscribeOperateReportResponse> add(SubscribeOperateReportRequest request) throws Exception;

    /**
     * Delete
     * @param subscribeOperateReportId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<SubscribeOperateReportResponse> delete(Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get All Execution Frequency Enum
     * @return
     */
    List<Map<String, Object>> getAllExecutionFrequencyEnum();

    /**
     * Modify
     * @param request
     * @return
     * @throws Exception
     */
    GeneralResponse<SubscribeOperateReportResponse> modify(SubscribeOperateReportRequest request) throws Exception;

    /**
     * Get
     * @param subscribeOperateReportId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<SubscribeOperateReportResponse> get(Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get Subscribe Operate Report Query
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<GetAllResponse<SubscribeOperateReportResponse>> getSubscribeOperateReportQuery(OperateReportQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

}
