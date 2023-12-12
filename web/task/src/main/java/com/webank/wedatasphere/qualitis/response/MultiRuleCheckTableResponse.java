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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class MultiRuleCheckTableResponse {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("alarm_variable")
    private List<CheckAlarmVariable> checkAlarmVariables;
    private List<MultiRuleResultResponse> result;
    @JsonProperty("save_table")
    private List<MultiRuleSaveTableResponse> saveTable;
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_type")
    private Integer ruleType;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("project_type")
    private Integer projectType;

    public MultiRuleCheckTableResponse() {
    }

    public MultiRuleCheckTableResponse(TaskRuleSimple taskRuleSimple, Map<Long, List<TaskResult>> resultMap) {
        this.ruleId = taskRuleSimple.getRuleId();
        this.projectId = taskRuleSimple.getProjectId();
        Project project = SpringContextHolder.getBean(ProjectDao.class).findById(taskRuleSimple.getProjectId());
        if (project != null) {
            this.projectType = project.getProjectType();
        }
        Rule rule = SpringContextHolder.getBean(RuleDao.class).findById(ruleId);
        if (rule != null) {
            this.ruleType = rule.getRuleType();
            this.ruleGroupId = rule.getRuleGroup().getId();
        }
        this.ruleName = taskRuleSimple.getRuleName();
        this.checkAlarmVariables = new ArrayList<>();
        this.result = new ArrayList<>();
        this.saveTable = new ArrayList<>();
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
            CheckAlarmVariable checkAlarmVariable = new CheckAlarmVariable(taskRuleAlarmConfig);
            this.checkAlarmVariables.add(checkAlarmVariable);
        }
        List<TaskResult> taskResults = resultMap.get(taskRuleSimple.getRuleId());
        if (CollectionUtils.isNotEmpty(taskResults)) {
            for (TaskResult taskResult : taskResults) {
                this.result.add(new MultiRuleResultResponse(taskResult, taskResult.getEnvName()));
                this.saveTable.add(new MultiRuleSaveTableResponse(taskRuleSimple.getMidTableName()));
            }
        }

    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<CheckAlarmVariable> getCheckAlarmVariables() {
        return checkAlarmVariables;
    }

    public void setCheckAlarmVariables(List<CheckAlarmVariable> checkAlarmVariables) {
        this.checkAlarmVariables = checkAlarmVariables;
    }

    public List<MultiRuleResultResponse> getResult() {
        return result;
    }

    public void setResult(List<MultiRuleResultResponse> result) {
        this.result = result;
    }

    public List<MultiRuleSaveTableResponse> getSaveTable() {
        return saveTable;
    }

    public void setSaveTable(List<MultiRuleSaveTableResponse> saveTable) {
        this.saveTable = saveTable;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
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

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }
}
