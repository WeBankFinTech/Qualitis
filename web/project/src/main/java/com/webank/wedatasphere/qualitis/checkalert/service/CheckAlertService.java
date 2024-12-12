package com.webank.wedatasphere.qualitis.checkalert.service;

import com.webank.wedatasphere.qualitis.checkalert.request.CheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryCheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryWorkFlowRequest;
import com.webank.wedatasphere.qualitis.checkalert.response.CheckAlertResponse;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2023/3/1 11:30
 */
public interface CheckAlertService {

    /**
     * Add
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    public GeneralResponse<CheckAlertResponse> add(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete
     * @param checkAlertId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    public GeneralResponse<CheckAlertResponse> delete(Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    public GeneralResponse<CheckAlertResponse> modify(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get
     * @param checkAlertId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    public GeneralResponse<CheckAlertResponse> get(Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Check datasource existence in the form.
     * @param alertTable
     * @param alertCol
     * @param majorAlertCol
     * @param contentCols
     * @return
     * @throws Exception
     */
    public GeneralResponse<Object> checkDatasource(String alertTable, String alertCol, String majorAlertCol, String contentCols) throws Exception;

    /**
     * get Check Alert Query
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<GetAllResponse<CheckAlertResponse>> getCheckAlertQuery(QueryCheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get Deduplication Field
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    Map<String,Object> getDeduplicationField(QueryWorkFlowRequest request) throws UnExpectedRequestException;

}
