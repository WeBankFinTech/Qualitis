package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.entity.RuleMetricTypeConfig;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.DownloadRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricListValuesRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricQueryRequest;
import com.webank.wedatasphere.qualitis.response.EnvResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricConditionResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricListValueResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricValueResponse;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/22 16:19
 */
public interface RuleMetricService {

  /**
   * Add for bdp-client.
   * @param request
   * @param loginUser
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse> addRuleMetricForOuter(AddRuleMetricRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Modify for bdp-client.
   * @param modifyRuleMetricRequest
   * @param createUser
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse>  modifyRuleMetricForOuter(ModifyRuleMetricRequest modifyRuleMetricRequest, String createUser)
      throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Add rule metric.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Delete rule metric.
   * @param id
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse> deleteRuleMetric(long id) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Modify rule metric.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse> modifyRuleMetric(ModifyRuleMetricRequest request)
      throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Get rule metric.
   * @param id
   * @return
   * @throws UnExpectedRequestException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<RuleMetricResponse> getRuleMetricDetail(long id) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Get all rule metrics.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException;


  /**
   * Initial conditions.
   * @return
   * @throws UnExpectedRequestException
   */
  RuleMetricConditionResponse conditions();

  /**
   * Query rule metric.
   * @param request
   * @param needVisibilityDepartmentList
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request, boolean needVisibilityDepartmentList) throws UnExpectedRequestException;

  /**
   * Find rule by rule metric ID with page.
   * @param ruleMetricId
   * @param page
   * @param size
   * @return
   * @throws UnExpectedRequestException
   */
  DataInfo<HiveRuleDetail> getRuleByRuleMetric(long ruleMetricId, int page, int size) throws UnExpectedRequestException;

  /**
   * Find rule metric value by rule metric ID with page.
   * @param id
   * @param startTime
   * @param endTime
   * @param envName
   * @param page
   * @param size
   * @return
   * @throws UnExpectedRequestException
   */
  DataInfo<RuleMetricValueResponse> getResultsByRuleMetric(long id, String startTime, String endTime, String envName, int page, int size) throws UnExpectedRequestException;

  /**
   * Download rule metric.
   * @param request
   * @param response
   * @return
   * @throws UnExpectedRequestException
   * @throws IOException
   * @throws WriteExcelException
   * @throws PermissionDeniedRequestException
   */
  GeneralResponse<Object> download(DownloadRuleMetricRequest request, HttpServletResponse response)
      throws UnExpectedRequestException, IOException, WriteExcelException, PermissionDeniedRequestException;

  /**
   * Upload rule metric.
   * @param fileInputStream
   * @param fileDisposition
   * @return
   * @throws UnExpectedRequestException
   * @throws IOException
   */
  GeneralResponse<Object> upload(InputStream fileInputStream, FormDataContentDisposition fileDisposition) throws UnExpectedRequestException, IOException;


  /**
   * Find rule metric types.
   * @return
   */
  GeneralResponse<List<RuleMetricTypeConfig>> types();

  /**
   * Find rule metric value by rule metric ID list with start and end time.
   * @param ruleMetricListValuesRequest
   * @return
   * @throws UnExpectedRequestException
   */
  List<RuleMetricListValueResponse> getResultsByRuleMetricList(RuleMetricListValuesRequest ruleMetricListValuesRequest)
      throws UnExpectedRequestException;

  /**
   * Get envs
   *
   * @param id
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<GetAllResponse<EnvResponse>> getAllEnvs(long id) throws UnExpectedRequestException;
}
