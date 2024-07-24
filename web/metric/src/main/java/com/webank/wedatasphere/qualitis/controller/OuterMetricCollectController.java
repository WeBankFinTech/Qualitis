package com.webank.wedatasphere.qualitis.controller;

//import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
//import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
//import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.request.AddMetricCollectRequest;
//import com.webank.wedatasphere.qualitis.request.ImsRuleMetricQueryRequest;
//import com.webank.wedatasphere.qualitis.request.MetricCollectOuterQueryRequest;
//import com.webank.wedatasphere.qualitis.response.GeneralResponse;
//import com.webank.wedatasphere.qualitis.response.GetAllMetricResponse;
//import com.webank.wedatasphere.qualitis.response.ImsRuleMetricQueryResponse;
//import com.webank.wedatasphere.qualitis.response.ImsmetricCollectViewOuterResponse;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricCollectService;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricService;
import com.webank.wedatasphere.qualitis.service.OuterMetricCollectService;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-16 15:05
 * @description
 */
@Path("outer/api/v1/imsmetric")
public class OuterMetricCollectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterMetricCollectController.class);

    @Autowired
    private OuterMetricCollectService outerMetricCollectService;
    @Autowired
    private ImsRuleMetricService imsRuleMetricService;
    @Autowired
    private ImsRuleMetricCollectService imsRuleMetricCollectService;

//    @POST
//    @Path("/collect_config")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse addCollectConfig(List<AddMetricCollectRequest> addMetricCollectRequests) throws UnExpectedRequestException {
//        LOGGER.info("request body: {}", CustomObjectMapper.transObjectToJson(addMetricCollectRequests));
//        for (AddMetricCollectRequest addMetricCollectRequest : addMetricCollectRequests) {
//            addMetricCollectRequest.checkRequest();
//        }
//
//        imsRuleMetricCollectService.createBatch(addMetricCollectRequests);
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }

//    @POST
//    @Path("/collect_list")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<List<ImsmetricCollectViewOuterResponse>> getMetricViewList(MetricCollectOuterQueryRequest queryRequest) throws UnExpectedRequestException {
//        try {
//            if (StringUtils.isBlank(queryRequest.getClusterName()) && StringUtils.isBlank(queryRequest.getDatabase())
//                    && StringUtils.isBlank(queryRequest.getTable()) && StringUtils.isBlank(queryRequest.getColumn())
//                    && StringUtils.isBlank(queryRequest.getCalcuUnitName())) {
//                throw new UnExpectedRequestException("All parameters cannot be empty at the same time.");
//            }
//            List<ImsmetricCollectViewOuterResponse> metricCollectListForOuter = imsRuleMetricService.getMetricCollectListForOuter(queryRequest);
//            return new GeneralResponse(ResponseStatusConstants.OK, "success", metricCollectListForOuter);
//        } catch (PermissionDeniedRequestException e) {
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        }
//    }

//    @POST
//    @Path("getMetricData")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricData(ImsRuleMetricQueryRequest request) {
//        try {
//            return imsRuleMetricService.getMetricDataFromOuter(request);
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
//    @Path("/collect_enum")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse addEnumCollectConfig(AddMetricCollectRequest addMetricCollectRequest) throws Exception {
//        LOGGER.info("request body: {}", CustomObjectMapper.transObjectToJson(addMetricCollectRequest));
//        outerMetricCollectService.addMetricCollectEnumConfigs(addMetricCollectRequest);
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }
}
