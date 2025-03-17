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

package com.webank.wedatasphere.qualitis.checkalert.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author allenzhou
 */
public class CheckAlertResponse implements Serializable {
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
    @JsonProperty("work_flow_project")
    private String workFlowProject;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("default_alert_level")
    private Integer defaultAlertLevel;
    @JsonProperty("default_alert_ways")
    private List<Integer> defaultAlertWays;
    @JsonProperty("advanced_alert_level")
    private Integer advancedAlertLevel;
    @JsonProperty("advanced_alert_ways")
    private List<Integer> advancedAlertWays;

    public CheckAlertResponse() {
        // Default Constructor
    }

    public CheckAlertResponse(CheckAlert checkAlert) {
        BeanUtils.copyProperties(checkAlert, this);
        this.projectId = checkAlert.getProject().getId();
        this.ruleGroupId = checkAlert.getRuleGroup().getId();
        this.workFlowProject = checkAlert.getProject().getName();
        if (StringUtils.isNotEmpty(checkAlert.getAdvancedAlertWays())) {
            Iterable<String> stringIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(checkAlert.getAdvancedAlertWays());
            this.advancedAlertWays = StreamSupport.stream(stringIterable.spliterator(), false).map(Integer::valueOf).sorted().collect(Collectors.toList());
        } else {
            this.advancedAlertWays = Collections.emptyList();
        }
        if (StringUtils.isNotEmpty(checkAlert.getDefaultAlertWays())) {
            Iterable<String> stringIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(checkAlert.getDefaultAlertWays());
            this.defaultAlertWays = StreamSupport.stream(stringIterable.spliterator(), false).map(Integer::valueOf).sorted().collect(Collectors.toList());
        } else {
            this.defaultAlertWays = Collections.emptyList();
        }
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

    public String getWorkFlowProject() {
        return workFlowProject;
    }

    public void setWorkFlowProject(String workFlowProject) {
        this.workFlowProject = workFlowProject;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
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

    public Integer getAdvancedAlertLevel() {
        return advancedAlertLevel;
    }

    public void setAdvancedAlertLevel(Integer advancedAlertLevel) {
        this.advancedAlertLevel = advancedAlertLevel;
    }

    public List<Integer> getAdvancedAlertWays() {
        return advancedAlertWays;
    }

    public void setAdvancedAlertWays(List<Integer> advancedAlertWays) {
        this.advancedAlertWays = advancedAlertWays;
    }

    @Override
    public String toString() {
        return "CheckAlertResponse{" +
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
                ", advancedAlertCol='" + advancedAlertCol + '\'' +
                ", contentCols='" + contentCols + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", workFlowName='" + workFlowName + '\'' +
                ", workFlowVersion='" + workFlowVersion + '\'' +
                ", workFlowSpace='" + workFlowSpace + '\'' +
                ", workFlowProject='" + workFlowProject + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createUser='" + createUser + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", defaultAlertLevel=" + defaultAlertLevel +
                ", defaultAlertWays=" + defaultAlertWays +
                ", advancedAlertLevel=" + advancedAlertLevel +
                ", advancedAlertWays=" + advancedAlertWays +
                '}';
    }
}
