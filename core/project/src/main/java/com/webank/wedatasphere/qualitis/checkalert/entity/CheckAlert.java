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

package com.webank.wedatasphere.qualitis.checkalert.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;

import javax.persistence.*;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_alert_config")
public class CheckAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(name = "default_receiver")
    private String defaultReceiver;
    @Column(name = "advanced_receiver")
    private String advancedReceiver;

    @Column(name = "alert_table")
    private String alertTable;

    private String filter;

    @Column(name = "alert_col")
    private String alertCol;
    @Column(name = "advanced_alert_col")
    private String advancedAlertCol;

    @Column(name = "content_cols")
    private String contentCols;

    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "work_flow_name")
    private String workFlowName;

    @Column(name = "work_flow_version")
    private String workFlowVersion;

    @Column(name = "work_flow_space")
    private String workFlowSpace;

    @Column(name = "default_alert_level")
    private Integer defaultAlertLevel;
    @Column(name = "default_alert_ways")
    private String defaultAlertWays;
    @Column(name = "advanced_alert_level")
    private Integer advancedAlertLevel;
    @Column(name = "advanced_alert_ways")
    private String advancedAlertWays;
    
    @ManyToOne
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JsonIgnore
    private RuleGroup ruleGroup;

    public CheckAlert() {
        // Do nothing.
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public RuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
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

    public String getDefaultAlertWays() {
        return defaultAlertWays;
    }

    public void setDefaultAlertWays(String defaultAlertWays) {
        this.defaultAlertWays = defaultAlertWays;
    }

    public Integer getAdvancedAlertLevel() {
        return advancedAlertLevel;
    }

    public void setAdvancedAlertLevel(Integer advancedAlertLevel) {
        this.advancedAlertLevel = advancedAlertLevel;
    }

    public String getAdvancedAlertWays() {
        return advancedAlertWays;
    }

    public void setAdvancedAlertWays(String advancedAlertWays) {
        this.advancedAlertWays = advancedAlertWays;
    }

    @Override
    public String toString() {
        return "CheckAlert{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", defaultReceiver='" + defaultReceiver + '\'' +
                ", advancedReceiver='" + advancedReceiver + '\'' +
                ", alertTable='" + alertTable + '\'' +
                ", filter='" + filter + '\'' +
                ", alertCol='" + alertCol + '\'' +
                ", advancedAlertCol='" + advancedAlertCol + '\'' +
                ", contentCols='" + contentCols + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", workFlowName='" + workFlowName + '\'' +
                ", workFlowVersion='" + workFlowVersion + '\'' +
                ", workFlowSpace='" + workFlowSpace + '\'' +
                ", defaultAlertLevel=" + defaultAlertLevel +
                ", defaultAlertWays='" + defaultAlertWays + '\'' +
                ", advancedAlertLevel=" + advancedAlertLevel +
                ", advancedAlertWays='" + advancedAlertWays + '\'' +
                ", project=" + project +
                ", ruleGroup=" + ruleGroup +
                '}';
    }
}
