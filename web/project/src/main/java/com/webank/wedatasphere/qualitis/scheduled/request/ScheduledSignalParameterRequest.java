package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.scheduled.constant.SignalTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.request.checker.AbstractJsonParameterChecker;
import com.webank.wedatasphere.qualitis.scheduled.request.checker.SignalParameterCheckerFactory;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 11:11
 * @description
 */
public class ScheduledSignalParameterRequest {

    private Long id;
    private Integer type;
    private String name;
    @JsonProperty("rule_group_list")
    private List<RuleGroupRequest> ruleGroupList;
    @JsonProperty("content_json")
    private String contentJson;

    public ScheduledSignalParameterRequest() {
        // Default Constructor
    }

    public static void checkRequest(ScheduledSignalParameterRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getName(), "name");
        CommonChecker.checkString(request.getContentJson(), "content_json");
        SignalTypeEnum signalTypeEnum = SignalTypeEnum.fromCode(request.getType());
        if (null == signalTypeEnum) {
            throw new UnExpectedRequestException("Invalid parameter: type");
        }
        AbstractJsonParameterChecker signalChecker = SignalParameterCheckerFactory.INSTANCE.fromSignalType(signalTypeEnum);
        signalChecker.check(request.getContentJson());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RuleGroupRequest> getRuleGroupList() {
        return ruleGroupList;
    }

    public void setRuleGroupList(List<RuleGroupRequest> ruleGroupList) {
        this.ruleGroupList = ruleGroupList;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }
}
