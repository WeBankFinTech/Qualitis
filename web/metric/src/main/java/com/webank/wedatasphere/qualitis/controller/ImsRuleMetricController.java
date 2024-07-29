package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * @author v_wenxuanzhang
 */
@Path("api/v1/projector/imsmetric/")
public class ImsRuleMetricController {

    @Autowired
    private ImsRuleMetricService imsRuleMetricService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImsRuleMetricController.class);

//    @POST
//    @Path("getMetricData")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricData(ImsRuleMetricQueryRequest request) {
//        try {
//            return imsRuleMetricService.getMetricData(request);
//        } catch (UnExpectedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        } catch (PermissionDeniedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        } catch (Exception e) {
//            LOGGER.error("Failed to get ims rule metric detail, caused by system error: {}", e.getMessage());
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_METRIC}", null);
//        }
//    }

//    @POST
//    @Path("getAlarmData")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetDataResponse<ImsAlarmDataQueryResponse>> getAlarmData(ImsAlarmDataQueryRequest request) {
//        try {
//            return imsRuleMetricService.getAlarmData(request);
//        } catch (UnExpectedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        } catch (PermissionDeniedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        } catch (Exception e) {
//            LOGGER.error("Failed to get alarm data, caused by system error: {}", e.getMessage());
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ALARM_DATA}", null);
//        }
//    }

//    @POST
//    @Path("/collect_list")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetDataResponse> getMetricViewList(MetricCollectQueryRequest queryRequest) throws UnExpectedRequestException {
//        try {
//            GetAllResponse<ImsmetricCollectViewResponse> getAllMetricResponse = imsRuleMetricService.getMetricCollectList(queryRequest);
//            return new GeneralResponse(ResponseStatusConstants.OK, "success", getAllMetricResponse);
//        } catch (PermissionDeniedRequestException e) {
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        }
//    }

//    @POST
//    @Path("/template/list")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetDataResponse> getTemplateList(TemplatePageRequest request) throws UnExpectedRequestException {
//        GetAllResponse<MetricTemplateQueryResponse> getAllResponse = imsRuleMetricService.getTemplateList(request);
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", getAllResponse);
//    }
//
//    @POST
//    @Path("/template/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetDataResponse> createMetricTemplate(AddCalcuTemplateRequest request) throws UnExpectedRequestException {
//        try {
//            ParameterChecker.checkEmpty(request);
//        } catch (IllegalAccessException e) {
//            throw new UnExpectedRequestException("Failed to validate request parameters.");
//        }
//        try {
//            imsRuleMetricService.createMetricTemplate(request);
//        } catch (PermissionDeniedRequestException e) {
//            throw new UnExpectedRequestException("${HAS_NO_PERMISSION_TO_EDIT}");
//        }
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }
//
//    @POST
//    @Path("/template/modify")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetDataResponse> modifyMetricTemplate(ModifyCalcuTemplateRequest request) throws UnExpectedRequestException {
//        try {
//            ParameterChecker.checkEmpty(request);
//        } catch (IllegalAccessException e) {
//            throw new UnExpectedRequestException("Failed to validate request parameters.");
//        }
//        try {
//            imsRuleMetricService.modifyMetricTemplate(request);
//        } catch (PermissionDeniedRequestException e) {
//            throw new UnExpectedRequestException("${HAS_NO_PERMISSION_TO_EDIT}");
//        }
//
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }

//    @GET
//    @Path("/template/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<MetricTemplateDetailResponse> getTemplateDetail(@PathParam("id") Long templateId) throws UnExpectedRequestException {
//
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricService.getMetricTemplateDetail(templateId));
//    }

//    @GET
//    @Path("/option/user_list")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<List<String>> getAllDataUser() {
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricService.findAllDataUsers());
//    }
//
//    @GET
//    @Path("/data_source/conditions")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<DataSourcesConditionResponse> getAllDataSources(@QueryParam("cluster") String cluster, @QueryParam("db") String db) throws UnExpectedRequestException {
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricService.getAllDataSources(StringUtils.isNotBlank(cluster) ? cluster : null, StringUtils.isNotBlank(db) ? db : null));
//    }

}
