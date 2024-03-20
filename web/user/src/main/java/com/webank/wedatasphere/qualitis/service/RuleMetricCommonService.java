package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddRuleMetricRequest;
import com.webank.wedatasphere.qualitis.request.ModifyRuleMetricRequest;

import java.io.IOException;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface RuleMetricCommonService {

    /**
     * according Rule Metric Name Add
     *
     * @param ruleMetricName 指标名
     * @param loginUser      登录名
     * @param multiEnv       是否为多DCN指标
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    RuleMetric accordingRuleMetricNameAdd(String ruleMetricName, String loginUser, boolean multiEnv) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * add RuleMetric For Object
     *
     * @param request
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    RuleMetric addRuleMetricForObject(AddRuleMetricRequest request, String userName) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 检查根据指标名称判断新增或修改
     *
     * @param ruleMetricName 指标名称
     * @param loginUser      当前登录用户名
     * @param multiEnv       是否多DCN
     * @param createUser     创建用户
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    Map<String, Object> checkRuleMetricNameAndAddOrModify(String ruleMetricName, String loginUser, Boolean multiEnv, String createUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;


    /**
     * modify Rule Metric Real
     *
     * @param request
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    RuleMetric modifyRuleMetricReal(ModifyRuleMetricRequest request, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException;
}
