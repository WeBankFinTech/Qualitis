package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/6 15:40
 */
public class ModifyGroupRulesRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_list")
    private List<ModifyGroupRuleRequest> modifyGroupRuleRequestList;

    private int page;
    private int size;

    public ModifyGroupRulesRequest() {
        // Do nothing because of X and Y.
    }

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

    public List<ModifyGroupRuleRequest> getModifyGroupRuleRequestList() {
        return modifyGroupRuleRequestList;
    }

    public void setModifyGroupRuleRequestList(List<ModifyGroupRuleRequest> modifyGroupRuleRequestList) {
        this.modifyGroupRuleRequestList = modifyGroupRuleRequestList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(projectId, "Project ID");
        CommonChecker.checkObject(ruleGroupId, "Rule group ID");
        CommonChecker.checkCollections(modifyGroupRuleRequestList, "Modify rule list");

        List<List<AlarmConfigRequest>> alarmList = modifyGroupRuleRequestList.stream().map(addGroupRuleRequest -> addGroupRuleRequest.getAlarmVariable()).filter(alarmConfigRequestList -> CollectionUtils.isNotEmpty(alarmConfigRequestList)).collect(
                Collectors.toList());

        List<String> allRuleMetricEncodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(alarmList)) {

            for (List<AlarmConfigRequest> currentAlarmConfigRequest : alarmList) {
                Set<String> currentRuleMetricEncodes = currentAlarmConfigRequest.stream().filter(alarmConfigRequest -> StringUtils.isNotEmpty(alarmConfigRequest.getRuleMetricEnCode())).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).collect(
                        Collectors.toSet());
                allRuleMetricEncodes.addAll(currentRuleMetricEncodes);
            }

            List<AlarmConfigRequest> alarmConfigRequests = alarmList.stream().flatMap(alarmConfigRequestList -> alarmConfigRequestList.stream()).collect(
                    Collectors.toList());

            alarmConfigRequests = alarmConfigRequests.stream().filter(alarmConfigRequest -> StringUtils.isNotEmpty(alarmConfigRequest.getRuleMetricEnCode())).collect(
                    Collectors.toList());
            if (CollectionUtils.isNotEmpty(alarmConfigRequests)) {
                List<String> ruleMetricEncodes = alarmConfigRequests.stream().map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).distinct().collect(
                        Collectors.toList());
                if (ruleMetricEncodes.size() != allRuleMetricEncodes.size()) {
                    throw new UnExpectedRequestException("Single Rule metric repeat.");
                }
            }

            //指标随规则自动化创建校验
            List<String> ruleMetricName = alarmList.stream().flatMap(alarmConfigRequestList -> alarmConfigRequestList.stream()).filter(alarmConfigRequest -> StringUtils.isNotBlank(alarmConfigRequest.getRuleMetricName())).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricName()).collect(
                    Collectors.toList());

            long countAuto = ruleMetricName.stream().distinct().count();
            if (ruleMetricName.size() != countAuto) {
                throw new UnExpectedRequestException("Auto Single Rule metric repeat.");
            }

        }
        allRuleMetricEncodes.clear();
        List<List<FileAlarmConfigRequest>> fileAlarmList = modifyGroupRuleRequestList.stream().map(addGroupRuleRequest -> addGroupRuleRequest.getFileAlarmVariable()).filter(fileAlarmConfigRequestList -> CollectionUtils.isNotEmpty(fileAlarmConfigRequestList)).collect(
                Collectors.toList());
        if (CollectionUtils.isNotEmpty(fileAlarmList)) {

            for (List<FileAlarmConfigRequest> currentFileAlarmConfigRequest : fileAlarmList) {
                Set<String> currentRuleMetricEncodes = currentFileAlarmConfigRequest.stream().filter(alarmConfigRequest -> StringUtils.isNotEmpty(alarmConfigRequest.getRuleMetricEnCode())).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).collect(
                        Collectors.toSet());
                allRuleMetricEncodes.addAll(currentRuleMetricEncodes);
            }

            List<FileAlarmConfigRequest> fileAlarmConfigRequests = fileAlarmList.stream().flatMap(fileAlarmConfigRequestList -> fileAlarmConfigRequestList.stream()).collect(
                    Collectors.toList());

            fileAlarmConfigRequests = fileAlarmConfigRequests.stream().filter(alarmConfigRequest -> StringUtils.isNotEmpty(alarmConfigRequest.getRuleMetricEnCode())).collect(
                    Collectors.toList());
            if (CollectionUtils.isNotEmpty(fileAlarmConfigRequests)) {
                List<String> ruleMetricEncodes = fileAlarmConfigRequests.stream().map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricEnCode()).distinct().collect(
                        Collectors.toList());
                if (ruleMetricEncodes.size() != allRuleMetricEncodes.size()) {
                    throw new UnExpectedRequestException("File Rule metric repeat.");
                }
            }

            //指标随规则自动化创建校验
            List<String> fileRuleMetricName = fileAlarmList.stream().flatMap(alarmConfigRequestList -> alarmConfigRequestList.stream()).filter(alarmConfigRequest -> StringUtils.isNotBlank(alarmConfigRequest.getRuleMetricName())).map(alarmConfigRequest -> alarmConfigRequest.getRuleMetricName()).collect(
                    Collectors.toList());
            long countAuto = fileRuleMetricName.stream().distinct().count();
            if (fileRuleMetricName.size() != countAuto) {
                throw new UnExpectedRequestException("Auto File Rule metric repeat.");
            }

        }

    }
}
