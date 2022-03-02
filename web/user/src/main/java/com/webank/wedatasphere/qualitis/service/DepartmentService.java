package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.DepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

/**
 * @author allenzhou
 */
public interface DepartmentService {
    /**
     * Add department.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<DepartmentResponse> addDepartment(DepartmentAddRequest request) throws UnExpectedRequestException;

    /**
     * Modify department name
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyDepartment(DepartmentModifyRequest request) throws UnExpectedRequestException;

    /**
     * Delete department.
     * @param departmentId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteDepartment(Long departmentId) throws UnExpectedRequestException;

    /**
     * Paging get all departments
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<DepartmentResponse>> findAllDepartment(PageRequest request) throws UnExpectedRequestException;

}
