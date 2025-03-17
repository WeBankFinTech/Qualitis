package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricCollectService;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricService;
import com.webank.wedatasphere.qualitis.service.OuterMetricCollectService;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
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
    @Autowired
    private RuleTemplateDao ruleTemplateDao;

//    @POST
//    @Path("/collect_config")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse addCollectConfig(List<AddMetricCollectRequest> addMetricCollectRequests) throws UnExpectedRequestException {
//        LOGGER.info("request body: {}", CustomObjectMapper.transObjectToJson(addMetricCollectRequests));
//        for (AddMetricCollectRequest addMetricCollectRequest : addMetricCollectRequests) {
//            addMetricCollectRequest.checkRequest();
//            convertTemplateNameToId(addMetricCollectRequest.getCollectConfigRequests());
//        }
//
//        imsRuleMetricCollectService.createBatch(addMetricCollectRequests);
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }

//    private void convertTemplateNameToId(List<AddMetricCollectConfigRequest> collectConfigRequests) throws UnExpectedRequestException {
//        if (CollectionUtils.isEmpty(collectConfigRequests)) {
//            return;
//        }
//        for (AddMetricCollectConfigRequest addMetricCollectConfigRequest: collectConfigRequests) {
//            List<AddMetricCalcuUnitConfigRequest> metricCalcuUnitConfigRequestList = addMetricCollectConfigRequest.getMetricCalcuUnitConfigRequestList();
//            if (CollectionUtils.isEmpty(metricCalcuUnitConfigRequestList)) {
//                continue;
//            }
//            for (AddMetricCalcuUnitConfigRequest metricCalcuUnitConfigRequest: metricCalcuUnitConfigRequestList) {
//                if (metricCalcuUnitConfigRequest.getTemplateId() == null && StringUtils.isBlank(metricCalcuUnitConfigRequest.getTemplateName())) {
//                    throw new UnExpectedRequestException("Both the template_id and template_name cannot be null.");
//                }
//                if (StringUtils.isNotBlank(metricCalcuUnitConfigRequest.getTemplateName())) {
//                    Template template = ruleTemplateDao.findByName(metricCalcuUnitConfigRequest.getTemplateName());
//                    metricCalcuUnitConfigRequest.setTemplateId(template.getId());
//                }
//            }
//        }
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
//
//    @POST
//    @Path("getMetricIdentify")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<ImsMetricCollectQueryResponse> getMetricIdentify(ImsRuleMetricQueryRequest request) {
//        try {
//            LOGGER.info("getMetricIdentify access info, metricId={}, username={}", request.getMetricId(), request.getUsername());
//            request.checkRequest();
//            return new GeneralResponse(ResponseStatusConstants.OK, "success",
//                    imsRuleMetricService.getMetricIdentifyById(request.getMetricId()));
//        } catch (UnExpectedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(String.valueOf(e.getStatus()), e.getMessage(), null);
//        } catch (Exception e) {
//            LOGGER.error("Failed to get ims rule metric identify, caused by system error: {}", e.getMessage());
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_METRIC}", null);
//        }
//    }
//
//    @POST
//    @Path("/collect_config_with_analysis")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse addCollectConfigWithAnalysis() {
//        LOGGER.info("Start to create collect config for normal fields and enum fields with analysis.");
//        imsRuleMetricCollectService.addCollectConfigWithAnalysis();
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//    }
//
//    @POST
//    @Path("/collect_task_status")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<CollectTaskResponse> getCollectTaskStatus(ImsMetricTaskStatusQueryRequest queryRequest) throws UnExpectedRequestException {
//        CommonChecker.checkObject(queryRequest.getMetricId(), "metric_id");
//        CommonChecker.checkString(queryRequest.getDataDate(), "data_date");
//        Task application = imsRuleMetricCollectService.getCollectTaskStatus(queryRequest);
//        if (application == null) {
//            return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
//        }
//        CollectTaskResponse collectTaskResponse = new CollectTaskResponse();
//        collectTaskResponse.setStatus(application.getStatus());
//        return new GeneralResponse(ResponseStatusConstants.OK, "success", collectTaskResponse);
//    }

}
