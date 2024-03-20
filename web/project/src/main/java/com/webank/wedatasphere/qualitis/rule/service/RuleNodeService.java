package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.BatchExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleWithDatasourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequests;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.CopyRuleWithDatasourceResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponses;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author allenzhou
 */
public interface RuleNodeService {
    /**
     * Delete rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException;

    /**
     * Modify rule node name
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse modifyRule(ModifyRuleRequest request) throws UnExpectedRequestException;

    /**
     * 根据规则组，使用json序列化，导出rule及其相关对象
     * @param ruleGroupId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleNodeResponses> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException;

    /**
     * ConstructFileRequest
     * @param rule
     * @param ruleGroup
     * @param workFlowName
     * @return
     */
    AddFileRuleRequest constructFileRequest(Rule rule, RuleGroup ruleGroup, String workFlowName);

    /**
     * ConstructMultiRequest
     * @param rule
     * @param ruleGroup
     * @param workFlowName
     * @return
     */
    AddMultiSourceRuleRequest constructMultiRequest(Rule rule, RuleGroup ruleGroup, String workFlowName);

    /**
     * ConstructCustomRequest
     * @param rule
     * @param ruleGroup
     * @param workFlowName
     * @return
     */
    AddCustomRuleRequest constructCustomRequest(Rule rule, RuleGroup ruleGroup, String workFlowName);

    /**
     * ConstructSingleRequest
     * @param rule
     * @param ruleGroup
     * @param workFlowName
     * @return
     */
    AddRuleRequest constructSingleRequest(Rule rule, RuleGroup ruleGroup, String workFlowName);

    /**
     * 使用json反序列化，导入rule及其相关对象
     * @param ruleNodeRequest
     * @param projectInDb
     * @param ruleGroup
     * @param objectMapper
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     */
    void importRule(RuleNodeRequest ruleNodeRequest, Project projectInDb, RuleGroup ruleGroup, ObjectMapper objectMapper) throws UnExpectedRequestException, IOException;

    /**
     * 批量导入规则组规则
     * @param ruleNodeRequests
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    GeneralResponse<RuleResponse> importRuleGroup(RuleNodeRequests ruleNodeRequests)
        throws UnExpectedRequestException, IOException, ExecutionException, InterruptedException;

    /**
     * Copy rule by project ID or rule group ID.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    GeneralResponse<RuleResponse> copyRuleByRuleGroupId(CopyRuleRequest request)
        throws UnExpectedRequestException, ExecutionException, InterruptedException;

    /**
     * HandleParameterIsNull
     * @param request
     * @param totalFinish
     * @param targetRuleGroup
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    int handleParameterIsNull(CopyRuleRequest request, int totalFinish, RuleGroup targetRuleGroup,
        Rule rule) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Copy rule with the datasource.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse copyRuleWithDatasource(CopyRuleWithDatasourceRequest request)
        throws UnExpectedRequestException;

    /**
     * batch update rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<List<Long>> updateRuleDataForBatch(BatchExecutionParametersRequest request)throws UnExpectedRequestException;

    /**
     * Handle execution param
     * @param ruleNodeRequests
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    void handleExecutionParamObject(RuleNodeRequests ruleNodeRequests) throws IOException, UnExpectedRequestException;
}
