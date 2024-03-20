package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class BatchExecutionParametersRequest {

    @JsonProperty("rule_id_list")
    private List<Long> ruleIdList;

    @JsonProperty("execution_parameters_name")
    private String executionParametersName;

    public List<Long> getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List<Long> ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public static void checkRequest(BatchExecutionParametersRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkCollections(request.getRuleIdList(), "rule_id_list");
        CommonChecker.checkString(request.getExecutionParametersName(), "execution_parameters_name");

    }

}
