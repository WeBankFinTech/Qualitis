package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.SubDepartmentResponse;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-14 9:48
 * @description
 */
public interface SubDepartmentService {

    /**
     * add Sub Department
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<SubDepartmentResponse> addSubDepartment(SubDepartmentAddRequest request) throws UnExpectedRequestException;

    /**
     * Modify department name
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse modifySubDepartment(SubDepartmentModifyRequest request) throws UnExpectedRequestException;

    /**
     * find All Sub Department
     * @param queryDepartmentRequest
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<SubDepartmentResponse>> findAllSubDepartment(QueryDepartmentRequest queryDepartmentRequest) throws UnExpectedRequestException;

    /**
     * delete Department
     * @param subDepartmentId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deleteDepartment(Long subDepartmentId) throws UnExpectedRequestException;
}
