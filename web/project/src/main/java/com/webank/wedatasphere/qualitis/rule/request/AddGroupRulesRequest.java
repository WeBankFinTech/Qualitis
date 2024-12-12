package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/1 21:10
 */
public class AddGroupRulesRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_list")
    private List<AddGroupRuleRequest> addGroupRuleRequestList;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public List<AddGroupRuleRequest> getAddGroupRuleRequestList() {
        return addGroupRuleRequestList;
    }

    public void setAddGroupRuleRequestList(List<AddGroupRuleRequest> addGroupRuleRequestList) {
        this.addGroupRuleRequestList = addGroupRuleRequestList;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(projectId, "Project ID");
        CommonChecker.checkCollections(addGroupRuleRequestList, "Add rule list");

        List<String> ruleMetricEncodes = addGroupRuleRequestList.stream().map(addGroupRuleRequest -> addGroupRuleRequest.getAlarmVariable()).flatMap(alarmConfigRequests -> alarmConfigRequests.stream()).filter(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode() != null).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).collect(
            Collectors.toList());
        List<String> fileRuleMetricEncodes = addGroupRuleRequestList.stream().map(addGroupRuleRequest -> addGroupRuleRequest.getFileAlarmVariable()).flatMap(alarmConfigRequests -> alarmConfigRequests.stream()).filter(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode() != null).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).collect(
            Collectors.toList());

        long count = ruleMetricEncodes.stream().distinct().count();
        if (ruleMetricEncodes.size() != count) {
            throw new UnExpectedRequestException("Rule metric repeat.");
        }
        count = fileRuleMetricEncodes.stream().distinct().count();
        if (fileRuleMetricEncodes.size() != count) {
            throw new UnExpectedRequestException("Rule metric repeat.");
        }

    }
}
