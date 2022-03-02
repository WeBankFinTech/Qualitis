package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.DownloadRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.RuleMetricQueryRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricConditionResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricResponse;
import com.webank.wedatasphere.qualitis.response.RuleMetricValueResponse;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.transaction.annotation.Transactional;

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
   */
  GeneralResponse<RuleMetricResponse> addRuleMetricForOuter(AddRuleMetricRequest request, String loginUser) throws UnExpectedRequestException;

  /**
   * Modify for bdp-client.
   * @param modifyRuleMetricRequest
   * @param createUser
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<RuleMetricResponse>  modifyRuleMetricForOuter(ModifyRuleMetricRequest modifyRuleMetricRequest, String createUser)
      throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Add rule metric.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<RuleMetricResponse> addRuleMetric(AddRuleMetricRequest request) throws UnExpectedRequestException;

  /**
   * Delete rule metric.
   * @param id
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<RuleMetricResponse> deleteRuleMetric(long id) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Modify rule metric.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<RuleMetricResponse> modifyRuleMetric(ModifyRuleMetricRequest request)
      throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Get rule metric.
   * @param id
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<RuleMetricResponse> getRuleMetricDetail(long id) throws UnExpectedRequestException, PermissionDeniedRequestException;

  /**
   * Get all rule metrics.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<GetAllResponse<RuleMetricResponse>> getAllRuleMetric(PageRequest request) throws UnExpectedRequestException;


  /**
   * Initial conditions.
   * @return
   * @throws UnExpectedRequestException
   */
  RuleMetricConditionResponse conditions();

  /**
   * Query rule metric.
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  GeneralResponse<GetAllResponse<RuleMetricResponse>> queryRuleMetric(RuleMetricQueryRequest request) throws UnExpectedRequestException;

  /**
   * Find rule by rule metric ID with page.
   * @param ruleMetricId
   * @param page
   * @param size
   * @return
   * @throws UnExpectedRequestException
   */
  DataInfo<HiveRuleDetail> getRulesByRuleMetric(long ruleMetricId, int page, int size) throws UnExpectedRequestException;

  /**
   * Find rule metric value by rule metric ID with page.
   * @param id
   * @param page
   * @param size
   * @return
   * @throws UnExpectedRequestException
   */
  DataInfo<RuleMetricValueResponse> getResultsByRuleMetric(long id, int page, int size) throws UnExpectedRequestException;

  /**
   * Download rule metric.
   * @param request
   * @param response
   * @return
   * @throws UnExpectedRequestException
   * @throws IOException
   * @throws WriteExcelException
   */
  GeneralResponse<?> download(DownloadRuleMetricRequest request, HttpServletResponse response)
      throws UnExpectedRequestException, IOException, WriteExcelException, PermissionDeniedRequestException;

  /**
   * Upload rule metric.
   * @param fileInputStream
   * @param fileDisposition
   * @return
   * @throws UnExpectedRequestException
   * @throws IOException
   */
  GeneralResponse<?> upload(InputStream fileInputStream, FormDataContentDisposition fileDisposition) throws UnExpectedRequestException, IOException;


  /**
   * Find rule metric types.
   * @return
   */
  GeneralResponse<?> types();
}
