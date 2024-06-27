package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.entity.RuleMetricTypeConfig;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.service.RuleMetricService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/rule_metric")
public class RuleMetricController {

  @Autowired
  private RuleMetricService ruleMetricService;

  @Autowired
  private ProjectBatchService projectBatchService;

  private static final Logger LOGGER = LoggerFactory.getLogger(RuleMetricController.class);

  @POST
  @Path("add")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
    try {
      return ruleMetricService.addRuleMetric(request);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (PermissionDeniedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }  catch (Exception e) {
      LOGGER.error("Failed to add rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("modify")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricResponse> modifyRuleMetric(ModifyRuleMetricRequest request)
      throws UnExpectedRequestException, PermissionDeniedRequestException {
    try {
      return ruleMetricService.modifyRuleMetric(request);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (PermissionDeniedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to modify rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("delete/{rule_metric_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricResponse> deleteRuleMetric(@PathParam("rule_metric_id") long id)
      throws UnExpectedRequestException, PermissionDeniedRequestException {
    try {
      return ruleMetricService.deleteRuleMetric(id);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (PermissionDeniedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (DataIntegrityViolationException e) {
      LOGGER.error(e.getMessage(), e);
      throw new UnExpectedRequestException("{&RULE_METRIC_HAS_BEEN_USED_WITH_RULE_OR_TASK}");
    } catch (Exception e) {
      LOGGER.error("Failed to delete rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("delete/batch")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricResponse> deleteBatchRuleMetric(DeleteBatchRuleMetricRequest deleteBatchRuleMetricRequest)
      throws UnExpectedRequestException, PermissionDeniedRequestException {
    try {
      DeleteBatchRuleMetricRequest.checkRequest(deleteBatchRuleMetricRequest);
      for (Long ruleMetricId : deleteBatchRuleMetricRequest.getRuleMetricIds()) {
        ruleMetricService.deleteRuleMetric(ruleMetricId);
      }
      return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_RULE_METRIC_SUCCESSFULLY}", null);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (PermissionDeniedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (DataIntegrityViolationException e) {
      LOGGER.error(e.getMessage(), e);
      throw new UnExpectedRequestException("{&RULE_METRIC_HAS_BEEN_USED_WITH_RULE_OR_TASK}");
    } catch (Exception e) {
      LOGGER.error("Failed to delete rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("detail/{rule_metric_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricResponse> getRuleMetric(@PathParam("rule_metric_id") long id)
      throws UnExpectedRequestException, PermissionDeniedRequestException {
    try {
      return ruleMetricService.getRuleMetricDetail(id);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (PermissionDeniedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to get rule metric detail, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException {
    try {
      return ruleMetricService.getAllRuleMetric(request);
    } catch (Exception e) {
      LOGGER.error("Failed to get rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_METRIC}", null);
    }
  }

  @GET
  @Path("envs/{rule_metric_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<GetAllResponse<EnvResponse>> getAllEnvs(@PathParam("rule_metric_id") long id) throws UnExpectedRequestException {
    try {
      return ruleMetricService.getAllEnvs(id);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to get rule envs, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&GET_RULE_ENVIRONMENT_FAILED}", null);
    }
  }

  @POST
  @Path("condition")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricConditionResponse> getRuleMetricCondition() {
    try {
      return new GeneralResponse<>(ResponseStatusConstants.OK, "{&INIT_RULE_METRIC_CONDITION_SUCCESS}", ruleMetricService.conditions());
    } catch (Exception e) {
      LOGGER.error("Failed to get rule metric condition, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_INIT_RULE_METRIC_CONDITION}", null);
    }
  }

  @POST
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException {
    try {
      return ruleMetricService.queryRuleMetric(request, true);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }  catch (Exception e) {
      LOGGER.error("Failed to get query rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_QUERY_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("rules/{ruleMetricId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<DataInfo<HiveRuleDetail>> rules(@PathParam("ruleMetricId") long id) throws UnExpectedRequestException {
    int page = 0;
    int size = Integer.MAX_VALUE;
    try {
      DataInfo<HiveRuleDetail> dataInfo =  ruleMetricService.getRuleByRuleMetric(id, page, size);
      LOGGER.info("Succeed to query rules.");
      return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", dataInfo);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to query rules, internal error", e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
    }
  }

  @POST
  @Path("rule_metric_value")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<DataInfo<RuleMetricValueResponse>> ruleMetricValues(RuleMetricValuesRequest ruleMetricValuesRequest) {
    int page = ruleMetricValuesRequest.getPage();
    int size = ruleMetricValuesRequest.getSize();
    LOGGER.info("get rule metric values request detail: {}", ruleMetricValuesRequest.toString());
    try {
      DataInfo<RuleMetricValueResponse> dataInfo = ruleMetricService.getResultsByRuleMetric(ruleMetricValuesRequest.getRuleMetricId(), ruleMetricValuesRequest.getStartTime()
          , ruleMetricValuesRequest.getEndTime(), ruleMetricValuesRequest.getEnvName(), page, size);

      LOGGER.info("Succeed to query rule metric values.");
      return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", dataInfo);
    } catch (Exception e) {
      LOGGER.error("Failed to query rule metric values, internal error", e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
    }
  }

  @POST
  @Path("rule_metric_value_list")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<List<RuleMetricListValueResponse>> ruleMetricListValues(RuleMetricListValuesRequest ruleMetricListValuesRequest) throws UnExpectedRequestException {
    try {
      List<RuleMetricListValueResponse> responses = ruleMetricService.getResultsByRuleMetricList(ruleMetricListValuesRequest);

      LOGGER.info("Succeed to query rule metric values.");
      return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", responses);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to query rule metric values, internal error", e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
    }
  }

  @POST
  @Path("download")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse download(DownloadRuleMetricRequest request, @Context HttpServletResponse response) throws UnExpectedRequestException {
    try {
      DownloadRuleMetricRequest.checkRequest(request);
      return ruleMetricService.download(request, response);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DOWNLOAD_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("upload")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public GeneralResponse upload(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition)
      throws UnExpectedRequestException {
    try {
      return ruleMetricService.upload(fileInputStream, fileDisposition);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_UPLOAD_RULE_METRIC}", null);
    }
  }

  @GET
  @Path("types")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<List<RuleMetricTypeConfig>> types() {
    try {
      return ruleMetricService.types();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_METRIC_TYPES}", null);
    }
  }
}
