/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.checkalert.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou
 */
public class CheckAlertRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    private Long id;

    private String topic;

    @JsonProperty("default_receiver")
    private String defaultReceiver;
    @JsonProperty("advanced_receiver")
    private String advancedReceiver;

    @JsonProperty("alert_table")
    private String alertTable;

    private String filter;

    @JsonProperty("alert_col")
    private String alertCol;
    @JsonProperty("advanced_alert_col")
    private String advancedAlertCol;

    @JsonProperty("content_cols")
    private String contentCols;

    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("work_flow_version")
    private String workFlowVersion;

    @JsonProperty("work_flow_space")
    private String workFlowSpace;

    @JsonProperty("default_alert_level")
    private Integer defaultAlertLevel;
    @JsonProperty("default_alert_ways")
    private List<Integer> defaultAlertWays;
    @JsonProperty("advanced_alert_level")
    private Integer advancedAlertLevel;
    @JsonProperty("advanced_alert_ways")
    private List<Integer> advancedAlertWays;

    public CheckAlertRequest() {
        // Default Constructor
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAlertTable() {
        return alertTable;
    }

    public void setAlertTable(String alertTable) {
        this.alertTable = alertTable;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getAlertCol() {
        return alertCol;
    }

    public void setAlertCol(String alertCol) {
        this.alertCol = alertCol;
    }

    public String getDefaultReceiver() {
        return defaultReceiver;
    }

    public void setDefaultReceiver(String defaultReceiver) {
        this.defaultReceiver = defaultReceiver;
    }

    public String getAdvancedReceiver() {
        return advancedReceiver;
    }

    public void setAdvancedReceiver(String advancedReceiver) {
        this.advancedReceiver = advancedReceiver;
    }

    public String getAdvancedAlertCol() {
        return advancedAlertCol;
    }

    public void setAdvancedAlertCol(String advancedAlertCol) {
        this.advancedAlertCol = advancedAlertCol;
    }

    public Integer getDefaultAlertLevel() {
        return defaultAlertLevel;
    }

    public void setDefaultAlertLevel(Integer defaultAlertLevel) {
        this.defaultAlertLevel = defaultAlertLevel;
    }

    public List<Integer> getDefaultAlertWays() {
        return defaultAlertWays;
    }

    public void setDefaultAlertWays(List<Integer> defaultAlertWays) {
        this.defaultAlertWays = defaultAlertWays;
    }

    public List<Integer> getAdvancedAlertWays() {
        return advancedAlertWays;
    }

    public void setAdvancedAlertWays(List<Integer> advancedAlertWays) {
        this.advancedAlertWays = advancedAlertWays;
    }

    public Integer getAdvancedAlertLevel() {
        return advancedAlertLevel;
    }

    public void setAdvancedAlertLevel(Integer advancedAlertLevel) {
        this.advancedAlertLevel = advancedAlertLevel;
    }

    public String getContentCols() {
        return contentCols;
    }

    public void setContentCols(String contentCols) {
        this.contentCols = contentCols;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(String workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

    public static void checkRequest(CheckAlertRequest request, boolean modify) throws UnExpectedRequestException {
        if (modify) {
            CommonChecker.checkObject(request.getId(), "Check alert ID");
            CommonChecker.checkObject(request.getRuleGroupId(),"Rule group ID");
        }

        CommonChecker.checkString(request.getTopic(),"Alert topic");
        CommonChecker.checkString(request.getAlertCol(),"Alert column");
        CommonChecker.checkString(request.getAlertTable(),"Alert table");
        CommonChecker.checkString(request.getDefaultReceiver(),"Alert default receiver");
        CommonChecker.checkObject(request.getDefaultAlertLevel(),"Alert default alert level");
        CommonChecker.checkListMinSize(request.getDefaultAlertWays(), 1,"Alert default alert ways");
        CommonChecker.checkString(request.getTopic(),"Alert topic");

        CommonChecker.checkString(request.getNodeName(),"Node name");
        CommonChecker.checkString(request.getWorkFlowName(),"Workflow name");
        CommonChecker.checkString(request.getWorkFlowVersion(),"Workflow version");
        CommonChecker.checkString(request.getWorkFlowSpace(),"Workflow space");

        CommonChecker.checkObject(request.getProjectId(),"Project ID");

        if (StringUtils.isNotEmpty(request.getContentCols())) {
            String[] strings = request.getContentCols().split(SpecCharEnum.DIVIDER.getValue());
            if (strings.length > QualitisConstants.DEFAULT_CONTENT_COLUMN_LENGTH) {
                throw new UnExpectedRequestException("Content cols is oversize.");
            }
        }

        checkAlertReceivers(request.getDefaultReceiver());
        checkAlertReceivers(request.getAdvancedReceiver());
    }

    private static void checkAlertReceivers (String alertReceiver) throws UnExpectedRequestException {
        if (StringUtils.isBlank(alertReceiver)) {
            return;
        }
        Iterable<String> defaultReceiverIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(alertReceiver);
        List<Integer> erpGroupIds = new ArrayList<>();
        List<String> defaultReceivers = new ArrayList<>();
        defaultReceiverIterable.forEach(item -> {
            if (item.matches(QualitisConstants.NUMBER_REGEX)) {
                try {
                    erpGroupIds.add(Integer.valueOf(item));
                } catch (NumberFormatException e) {
//                    doing nothing
                }
            } else {
                defaultReceivers.add(item);
            }
        });
        if (defaultReceivers.size() > 13 || erpGroupIds.size() > 1) {
            throw new UnExpectedRequestException("告警接收人最多接收13个告警人和1个企业微信群号");
        }
    }

    @Override
    public String toString() {
        return "CheckAlertRequest{" +
            "projectId=" + projectId +
            ", projectName='" + projectName + '\'' +
            ", ruleGroupId=" + ruleGroupId +
            ", id=" + id +
            ", topic='" + topic + '\'' +
            ", defaultReceiver='" + defaultReceiver + '\'' +
            ", advancedReceiver='" + advancedReceiver + '\'' +
            ", alertTable='" + alertTable + '\'' +
            ", filter='" + filter + '\'' +
            ", alertCol='" + alertCol + '\'' +
            ", majorAlertCol='" + advancedAlertCol + '\'' +
            ", contentCols='" + contentCols + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", workFlowName='" + workFlowName + '\'' +
            ", workFlowVersion='" + workFlowVersion + '\'' +
            '}';
    }
}
