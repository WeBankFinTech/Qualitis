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
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import org.springframework.beans.BeanUtils;

/**
 * @author allenzhou
 */
public class CheckAlertResponse {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    private Long id;

    private String topic;

    @JsonProperty("info_receiver")
    private String infoReceiver;
    @JsonProperty("major_receiver")
    private String majorReceiver;

    @JsonProperty("alert_table")
    private String alertTable;

    private String filter;

    @JsonProperty("alert_col")
    private String alertCol;
    @JsonProperty("major_alert_col")
    private String majorAlertCol;

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

    public CheckAlertResponse() {
        // Default Constructor
    }

    public CheckAlertResponse(CheckAlert checkAlert) {
        BeanUtils.copyProperties(checkAlert, this);
        this.projectId = checkAlert.getProject().getId();
        this.ruleGroupId = checkAlert.getRuleGroup().getId();
        this.workFlowProject = checkAlert.getProject().getName();
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

    public String getInfoReceiver() {
        return infoReceiver;
    }

    public void setInfoReceiver(String infoReceiver) {
        this.infoReceiver = infoReceiver;
    }

    public String getMajorReceiver() {
        return majorReceiver;
    }

    public void setMajorReceiver(String majorReceiver) {
        this.majorReceiver = majorReceiver;
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

    public String getMajorAlertCol() {
        return majorAlertCol;
    }

    public void setMajorAlertCol(String majorAlertCol) {
        this.majorAlertCol = majorAlertCol;
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

    @Override
    public String toString() {
        return "CheckAlertResponse{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", ruleGroupId=" + ruleGroupId +
                ", id=" + id +
                ", topic='" + topic + '\'' +
                ", infoReceiver='" + infoReceiver + '\'' +
                ", majorReceiver='" + majorReceiver + '\'' +
                ", alertTable='" + alertTable + '\'' +
                ", filter='" + filter + '\'' +
                ", alertCol='" + alertCol + '\'' +
                ", majorAlertCol='" + majorAlertCol + '\'' +
                ", contentCols='" + contentCols + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", workFlowName='" + workFlowName + '\'' +
                ", workFlowVersion='" + workFlowVersion + '\'' +
                '}';
    }
}
