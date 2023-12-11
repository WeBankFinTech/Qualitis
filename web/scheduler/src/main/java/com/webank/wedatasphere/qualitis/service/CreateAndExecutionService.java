package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.CreateAndSubmitRequest;
import com.webank.wedatasphere.qualitis.response.CreateAndSubmitResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;

import java.io.IOException;

/**
 * @author allenzhou@webank.com
 * @date 2021/9/8 10:40
 */
public interface CreateAndExecutionService {

    /**
     * Add or modify rule.
     * @param addDirector
     * @param loginUser
     * @param abstrackAddRequest
     * @param ruleBash
     * @param workFlowName
     * @param workFlowVersion
     * @param workFlowSpace
     * @param nodeName
     * @param ruleGroupInDb
     * @param response
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    RuleResponse addOrModifyRule(AddDirector addDirector, String loginUser, AbstractCommonRequest abstrackAddRequest, String ruleBash,
                                 String workFlowName, String workFlowVersion, String workFlowSpace, String nodeName,
                                 RuleGroup ruleGroupInDb, CreateAndSubmitResponse response)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Check rule metric and save
     * @param abstrackAddRequest
     * @param createUser
     * @param templateFunction
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    String checkRuleMetricAndSave(AbstractCommonRequest abstrackAddRequest, String createUser, String templateFunction)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Check execution parameter and save
     * @param abstrackAddRequest
     * @param createUser
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void checkExecutionParameterAndSave(AbstractCommonRequest abstrackAddRequest,
        String createUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * create or modify, and submit rule
     * @param request
     * @return
     */
    GeneralResponse createOrModifyAndSubmitRule(CreateAndSubmitRequest request);

}
