package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.DownloadRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricQueryRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricValuesRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricConditionResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricValueResponse;
import com.webank.wedatasphere.qualitis.service.RuleMetricService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import org.springframework.dao.DataIntegrityViolationException;

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
  public GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException {
    try {
      return ruleMetricService.addRuleMetric(request);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to add rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE_METRIC}", null);
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
      return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_METRIC}", null);
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
      return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE_METRIC}", null);
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
      return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(PageRequest request) throws UnExpectedRequestException {
    try {
      return ruleMetricService.getAllRuleMetric(request);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to get rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("condition")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<RuleMetricConditionResponse> getRuleMetricCondition() {
    try {
      return new GeneralResponse<>("200", "{&INIT_RULE_METRIC_CONDITION_SUCCESS}", ruleMetricService.conditions());
    } catch (Exception e) {
      LOGGER.error("Failed to get rule metric condition, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>("500", "{&FAILED_TO_INIT_RULE_METRIC_CONDITION}", null);
    }
  }

  @POST
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException {
    try {
      return ruleMetricService.queryRuleMetric(request);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }  catch (Exception e) {
      LOGGER.error("Failed to get query rule metric, caused by system error: {}", e.getMessage());
      return new GeneralResponse<>("500", "{&FAILED_TO_QUERY_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("rules/{ruleMetricId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> rules(@PathParam("ruleMetricId") long id) throws UnExpectedRequestException {
    int page = 0;
    int size = Integer.MAX_VALUE;
    try {
      DataInfo<HiveRuleDetail> dataInfo =  ruleMetricService.getRulesByRuleMetric(id, page, size);
      LOGGER.info("Succeed to query rules.");
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", dataInfo);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to query rules, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), null);
    }
  }

  @POST
  @Path("rule_metric_value")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> ruleMetricValues(RuleMetricValuesRequest ruleMetricValuesRequest) throws UnExpectedRequestException {
    int page = ruleMetricValuesRequest.getPage();
    int size = ruleMetricValuesRequest.getSize();
    try {
      DataInfo<RuleMetricValueResponse> dataInfo = ruleMetricService.getResultsByRuleMetric(ruleMetricValuesRequest.getRuleMetricId(), page, size);

      LOGGER.info("Succeed to query rule metric values.");
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", dataInfo);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Failed to query rule metric values, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), null);
    }
  }

  @POST
  @Path("download")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> download(DownloadRuleMetricRequest request, @Context HttpServletResponse response) throws UnExpectedRequestException {
    try {
      DownloadRuleMetricRequest.checkRequest(request);
      return ruleMetricService.download(request, response);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>("500", "{&FAILED_TO_DOWNLOAD_RULE_METRIC}", null);
    }
  }

  @POST
  @Path("upload")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public GeneralResponse<?> upload(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition)
      throws UnExpectedRequestException {
    try {
      return ruleMetricService.upload(fileInputStream, fileDisposition);
    } catch (UnExpectedRequestException e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_RULE_METRIC}", null);
    }
  }

  @GET
  @Path("types")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> types() {
    try {
      return ruleMetricService.types();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_METRIC_TYPES}", null);
    }
  }
}
