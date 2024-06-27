package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;
import com.webank.wedatasphere.qualitis.request.AddMetricCollectRequest;
import com.webank.wedatasphere.qualitis.request.AddMetricSchedulerRequest;
import com.webank.wedatasphere.qualitis.request.MetricCollectQueryRequest;
import com.webank.wedatasphere.qualitis.request.ModifyMetricCollectRequest;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricCollectService;
import com.webank.wedatasphere.qualitis.service.ImsRuleMetricService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-13 14:36
 * @description
 */
@Path("api/v1/projector/imsmetric/collect")
public class ImsRuleMetricCollectController {

    @Autowired
    private ImsRuleMetricCollectService imsRuleMetricCollectService;
    @Autowired
    private ImsRuleMetricService imsRuleMetricService;

    @POST
    @Path("/partition/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getPartitionList(MetricCollectQueryRequest request) throws UnExpectedRequestException {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricCollectService.getPartitionList(request.getClusterName(), request.getDatabase(), request.getTable()));
    }

    @POST
    @Path("/scheduler/detail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getSchedulerDetail(MetricCollectQueryRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getPartition(), "partition");
        CommonChecker.checkString(request.getDatabase(), "database");
        CommonChecker.checkString(request.getTable(), "table");
        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricCollectService.getSchedulerDetail(request.getDatabase(), request.getTable(), request.getPartition()));
    }

    @POST
    @Path("/scheduler/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse createScheduler(List<AddMetricSchedulerRequest> requests) throws UnExpectedRequestException {
        try {
            imsRuleMetricCollectService.createOrModifySchedulers(requests);
        } catch (ParseException | IllegalAccessException e) {
            throw new UnExpectedRequestException("Failed to convert cron expression.");
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @POST
    @Path("/create_group")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse create(List<AddMetricCollectRequest> request) throws UnExpectedRequestException {
        for (AddMetricCollectRequest addMetricCollectRequest : request) {
            imsRuleMetricCollectService.checkCreateRequest(addMetricCollectRequest);
        }
        imsRuleMetricCollectService.createBatch(request);
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @POST
    @Path("/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modify(ModifyMetricCollectRequest request) throws UnExpectedRequestException {
        try {
            ParameterChecker.checkEmpty(request);
        } catch (IllegalAccessException e) {
            throw new UnExpectedRequestException("Failed to validate request parameters.");
        }
        imsRuleMetricCollectService.modify(request);
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<MetricCollectQueryResponse> list(MetricCollectQueryRequest request) throws UnExpectedRequestException {
        GetAllResponse<MetricCollectQueryResponse> getAllResponse = imsRuleMetricCollectService.list(request);
        return new GeneralResponse(ResponseStatusConstants.OK, "success", getAllResponse);
    }

    @GET
    @Path("/detail/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<MetricCollectQueryResponse> detail(@PathParam("id") Long id) throws UnExpectedRequestException {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricCollectService.getDetailInfo(id));
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<MetricCollectQueryResponse> delete(List<Long> ids) throws UnExpectedRequestException {
        imsRuleMetricCollectService.deleteMetricCollect(ids);
        return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
    }

    @GET
    @Path("/execution_parameters_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<ExecutionParametersResponse>> getAllExecutionParameters() throws UnExpectedRequestException {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricCollectService.getAllExecutionParameters());
    }

    @GET
    @Path("/project_info")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getImsMetricProject() {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", imsRuleMetricCollectService.getImsMetricProject());
    }

    @POST
    @Path("/template/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetDataResponse> getTemplateList(TemplatePageRequest request) throws UnExpectedRequestException {
        GetAllResponse<MetricTemplateQueryResponse> getAllResponse = imsRuleMetricService.getTemplateList(request);
        List<MetricTemplateQueryResponse> metricTemplateQueryResponses = getAllResponse.getData();
        if (CollectionUtils.isNotEmpty(metricTemplateQueryResponses)) {
            ListIterator<MetricTemplateQueryResponse> listIterator = metricTemplateQueryResponses.listIterator();
            while (listIterator.hasNext()) {
                MetricTemplateQueryResponse metricTemplateQueryResponse = listIterator.next();
                metricTemplateQueryResponse.setVisibilityDepartmentNameList(null);
            }
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "success", getAllResponse);
    }

}
