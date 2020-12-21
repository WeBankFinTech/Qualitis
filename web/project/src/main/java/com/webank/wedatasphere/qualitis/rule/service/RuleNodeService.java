package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import java.io.IOException;

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
    GeneralResponse<RuleResponse> modifyRuleWithContextService(ModifyRuleRequest request) throws UnExpectedRequestException;

    /**
     * 根据规则组，使用json序列化，导出rule及其相关对象
     * @param ruleGroupId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleNodeResponse> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException;

    /**
     * 使用json反序列化，导入rule及其相关对象
     * @param ruleObject
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> importRule(RuleNodeRequest ruleObject) throws UnExpectedRequestException, IOException;
}
