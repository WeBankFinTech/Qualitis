package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.SubDepartmentAddRequest;
import com.webank.wedatasphere.qualitis.request.SubDepartmentModifyRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.SubDepartmentResponse;
import com.webank.wedatasphere.qualitis.service.SubDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-14 9:40
 * @description
 */
@Path("api/v1/admin/sub_department")
public class SubDepartmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubDepartmentController.class);

    @Autowired
    private SubDepartmentService subDepartmentService;

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<SubDepartmentResponse> addDepartment(SubDepartmentAddRequest request) throws UnExpectedRequestException{
        try {
            return subDepartmentService.addSubDepartment(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add department, department: {}, caused by: {}", request.getDepartmentName(), e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyDepartment(SubDepartmentModifyRequest request) throws UnExpectedRequestException {
        try {
            return subDepartmentService.modifySubDepartment(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify department, department: {}, caused by: {}", request.getDepartmentName(), e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("delete/{sub_department_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteDepartment(@PathParam("sub_department_id") Long subDepartmentId) throws UnExpectedRequestException{
        try {
            return subDepartmentService.deleteDepartment(subDepartmentId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify department, department id: {}, caused by: {}", subDepartmentId, e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<SubDepartmentResponse>> getAllSubDepartment(QueryDepartmentRequest queryDepartmentRequest) throws UnExpectedRequestException{
        try {
            return subDepartmentService.findAllSubDepartment(queryDepartmentRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get subDepartment, caused by: {}", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DEPARTMENT}", null);
        }
    }

}
