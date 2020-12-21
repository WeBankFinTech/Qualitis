package com.webank.wedatasphere.qualitis.rule.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;

/**
 * @author allenzhou
 */
public class DeleteRuleTemplateRequest {

    private List<Long> ruleTemplateIdList;

    public DeleteRuleTemplateRequest() {
    }

    public List<Long> getRuleTemplateIdList() {
        return ruleTemplateIdList;
    }

    public void setRuleTemplateIdList(List<Long> ruleTemplateIdList) {
        this.ruleTemplateIdList = ruleTemplateIdList;
    }

    public static void checkRequest(DeleteRuleTemplateRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "DeleteRuleTemplateRequest");
        CommonChecker.checkListMinSize(request.getRuleTemplateIdList(), 1, "DeleteRuleTemplateIdList");
    }
}
