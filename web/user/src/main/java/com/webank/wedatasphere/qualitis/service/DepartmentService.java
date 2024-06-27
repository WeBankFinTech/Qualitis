package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.request.DepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.request.QueryDepartmentRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

import java.util.List;

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
    GeneralResponse modifyDepartment(DepartmentModifyRequest request) throws UnExpectedRequestException;

    /**
     * Delete department.
     * @param departmentId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deleteDepartment(Long departmentId) throws UnExpectedRequestException;

    /**
     * Paging get all departments
     * @param queryDepartmentRequest
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<DepartmentResponse>> findAllDepartment(QueryDepartmentRequest queryDepartmentRequest) throws UnExpectedRequestException;

    /**
     * find Sub Department By Dept Code
     * @param deptCode
     * @return
     * @throws UnExpectedRequestException
     */
    List<DepartmentSubResponse> getSubDepartmentByDeptCode(Integer deptCode) throws UnExpectedRequestException;

    /**
     * find code and name
     * @return
     */
    List<Department> findAllDepartmentCodeAndName();

    /**
     * find From Tenant User
     * @return
     */
    GeneralResponse<GetAllResponse<DepartmentResponse>> findFromTenantUser();
}
