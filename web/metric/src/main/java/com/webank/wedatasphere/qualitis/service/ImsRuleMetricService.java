package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;

import java.util.List;


/**
 * @author v_wenxuanzhang
 */
public interface ImsRuleMetricService {

    /**
     * get metric data for outer
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
//    GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricDataFromOuter(ImsRuleMetricQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get ims rule metric.
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
//    GeneralResponse<GetAllMetricResponse<ImsRuleMetricQueryResponse>> getMetricData(ImsRuleMetricQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get Alarm Data
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
//    GeneralResponse<GetDataResponse<ImsAlarmDataQueryResponse>> getAlarmData(ImsAlarmDataQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get metric collect data
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
//    GetAllResponse<ImsmetricCollectViewResponse> getMetricCollectList(MetricCollectQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get metric collect data for outer
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
//    List<ImsmetricCollectViewOuterResponse> getMetricCollectListForOuter(MetricCollectOuterQueryRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get metric templates
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
//    GetAllResponse<MetricTemplateQueryResponse> getTemplateList(TemplatePageRequest request) throws UnExpectedRequestException;

    /**
     * get all proxy users
     * @return
     */
//    List<String> findAllDataUsers();

    /**
     *  get datasource
     * @param cluster
     * @param db
     * @return
     */
//    DataSourcesConditionResponse getAllDataSources(String cluster, String db) throws UnExpectedRequestException;

    /**
     *
     * @param request
     */
//    void createMetricTemplate(AddCalcuTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     *
     * @param request
     */
//    void modifyMetricTemplate(ModifyCalcuTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get detail of metric template
     * @param templateId
     * @return
     */
//    MetricTemplateDetailResponse getMetricTemplateDetail(Long templateId) throws UnExpectedRequestException;

}
