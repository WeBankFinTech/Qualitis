package com.webank.wedatasphere.qualitis.report.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.report.request.OperateReportQueryRequest;
import com.webank.wedatasphere.qualitis.report.request.SubscribeOperateReportRequest;
import com.webank.wedatasphere.qualitis.report.response.SubscribeOperateReportResponse;
import com.webank.wedatasphere.qualitis.report.service.SubscribeOperateReportService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
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
@Path("api/v1/projector/operate/report")
public class SubscribeOperateReportController {

    @Autowired
    private SubscribeOperateReportService subscribeOperateReportService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeOperateReportController.class);

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<SubscribeOperateReportResponse> add(SubscribeOperateReportRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return subscribeOperateReportService.add(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add subscribe operate report, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_SUBSCRIBE_OPERATE_REPORT}", null);
        }
    }


    @POST
    @Path("delete/{subscribeOperateReportId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<SubscribeOperateReportResponse> delete(@PathParam("subscribeOperateReportId") Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return subscribeOperateReportService.delete(subscribeOperateReportId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to subscribe operate report, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_SUBSCRIBE_OPERATE_REPORT}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<SubscribeOperateReportResponse> modify(SubscribeOperateReportRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return subscribeOperateReportService.modify(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify subscribe operate report, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_SUBSCRIBE_OPERATE_REPORT}", null);
        }
    }

    @GET
    @Path("get/{subscribeOperateReportId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<SubscribeOperateReportResponse> getDetail(@PathParam("subscribeOperateReportId") Long subscribeOperateReportId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return subscribeOperateReportService.get(subscribeOperateReportId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get subscribe operate report, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SUBSCRIBE_OPERATE_REPORT_DETAIL}", null);
        }
    }


    /**
     * @param request
     * @return
     */
    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<SubscribeOperateReportResponse>> getSubscribeOperateReportQuery(OperateReportQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        request.convertParameter();
        try {
            return subscribeOperateReportService.getSubscribeOperateReportQuery(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get Subscribe Operate Report, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SUBSCRIBE_OPERATE_REPORT_QUERY}", null);
        }
    }

    @POST
    @Path("execution/frequency/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getExecutionFrequencyEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_EXECUTION_FREQUENCY_ENUMN_SUCCESSFULLY}", subscribeOperateReportService.getAllExecutionFrequencyEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get execution frequency enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_EXECUTION_FREQUENCY_ENUMN}", null);
        }
    }


}
