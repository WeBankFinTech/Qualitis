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

package com.webank.wedatasphere.qualitis.bean;

/**
 * @author howeye
 */
public class RuleSummary {

    private String ruleName;
    private Long ruleId;
    private String midTableName;
    private Long projectId;
    private String projectName;

    public RuleSummary() {
    }

    public RuleSummary(RuleTaskDetail ruleTaskDetail) {
        this.ruleId = ruleTaskDetail.getRule().getId();
        this.ruleName = ruleTaskDetail.getRule().getName();
        this.midTableName = ruleTaskDetail.getMidTableName();
        this.projectId = ruleTaskDetail.getRule().getProject().getId();
        this.projectName = ruleTaskDetail.getRule().getProject().getName();
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getMidTableName() {
        return midTableName;
    }

    public void setMidTableName(String midTableName) {
        this.midTableName = midTableName;
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

    @Override
    public String toString() {
        return "RuleSummary{" +
                "ruleName='" + ruleName + '\'' +
                ", ruleId=" + ruleId +
                ", midTableName='" + midTableName + '\'' +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
