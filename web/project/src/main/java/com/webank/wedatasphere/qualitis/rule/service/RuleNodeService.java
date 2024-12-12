package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequests;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponses;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import java.io.IOException;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.codehaus.jackson.map.ObjectMapper;

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
    GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException;

    /**
     * Modify rule detail
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleByCsId(ModifyRuleRequest request) throws UnExpectedRequestException;

    /**
     * 根据规则组，使用json序列化，导出rule及其相关对象
     * @param ruleGroupId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleNodeResponses> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException;

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
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> importRuleGroup(RuleNodeRequests ruleNodeRequests)
        throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Copy rule by project ID or rule group ID.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> copyRuleByRuleGroupId(CopyRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException;
}
