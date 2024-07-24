package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.request.AddMetricCollectRequest;
import com.webank.wedatasphere.qualitis.request.AddMetricSchedulerRequest;
import com.webank.wedatasphere.qualitis.request.MetricCollectQueryRequest;
import com.webank.wedatasphere.qualitis.request.ModifyMetricCollectRequest;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.MetricCollectQueryResponse;
import com.webank.wedatasphere.qualitis.response.MetricSchedulerDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.ExecutionParametersResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-13 14:37
 * @description
 */
@Service
public interface ImsRuleMetricCollectService {
//
//    /**
//     * get partitions in table
//     *
//     * @param clusterName
//     * @param db
//     * @param table
//     * @return
//     */
//    List<String> getPartitionList(String clusterName, String db, String table);
//
//    /**
//     * get scheduler of paration
//     *
//     * @param db
//     * @param table
//     * @param partition
//     * @return
//     */
//    MetricSchedulerDetailResponse getSchedulerDetail(String db, String table, String partition);
//
//    /**
//     * batch to create metric schedulers
//     * create schedulers
//     *
//     * @param requests
//     */
//    void createOrModifySchedulers(List<AddMetricSchedulerRequest> requests) throws UnExpectedRequestException, ParseException, IllegalAccessException;
//
//    /**
//     * get all execution parameter of metric
//     */
//    /**
//     * @return
//     */
//    List<ExecutionParametersResponse> getAllExecutionParameters() throws UnExpectedRequestException;
//
//    /**
//     * @param metricCollectIds
//     */
//    void deleteMetricCollect(List<Long> metricCollectIds) throws UnExpectedRequestException;
//
//    /**
//     * @param metricCollectId
//     * @return
//     */
//    MetricCollectQueryResponse getDetailInfo(Long metricCollectId) throws UnExpectedRequestException;
//
//    /**
//     * @param request
//     * @throws UnExpectedRequestException
//     */
//    void modify(ModifyMetricCollectRequest request) throws UnExpectedRequestException;
//
//    /**
//     * @param request
//     * @return
//     * @throws UnExpectedRequestException
//     */
//    GetAllResponse<MetricCollectQueryResponse> list(MetricCollectQueryRequest request) throws UnExpectedRequestException;
//
//    /**
//     *
//     * @param request
//     * @throws UnExpectedRequestException
//     */
//    void createBatch(List<AddMetricCollectRequest> request) throws UnExpectedRequestException;
//
//    /**
//     *
//     * @param addMetricCollectRequests
//     * @throws UnExpectedRequestException
//     */
//    void addMetricCollectConfigs(List<AddMetricCollectRequest> addMetricCollectRequests) throws UnExpectedRequestException;
//
//    /**
//     * get project info of metric
//     */
//    ProjectResponse getImsMetricProject();
//
//    /**
//     * Check create request
//     * @param request
//     * @throws UnExpectedRequestException
//     */
//    void checkCreateRequest(AddMetricCollectRequest request) throws UnExpectedRequestException;
}
