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
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class CheckTableResponse {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("columns")
    private List<String> columnNames;
    @JsonProperty("alarm_variable")
    private List<CheckAlarmVariable> checkAlarmVariables;
    private String result;
    @JsonProperty("save_table")
    private String saveTable;
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_type")
    private Integer ruleType;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("table_group")
    private Boolean tableGroup;
    @JsonProperty("project_type")
    private Integer projectType;
    @JsonProperty("datasource_type")
    private Integer datasourceType;

    public CheckTableResponse(TaskDataSource taskDataSource, Map<Long, TaskRuleSimple> taskRuleSimpleMap, Map<Long, List<TaskResult>> taskResultMap) {
        Long currentRuleId = taskDataSource.getRuleId();
        this.projectId = taskDataSource.getProjectId();
        Project project = SpringContextHolder.getBean(ProjectDao.class).findById(taskDataSource.getProjectId());
        if (project != null) {
            this.projectType = project.getProjectType();
        }
        this.ruleId = currentRuleId;
        this.ruleName = taskRuleSimpleMap.get(currentRuleId).getRuleName();
        String col = taskDataSource.getColName();
        if (StringUtils.isNotBlank(col)) {
            this.columnNames = Arrays.asList(col.split(SpecCharEnum.VERTICAL_BAR.getValue()));
        }
        List<TaskResult> taskResults = taskResultMap.get(currentRuleId);
        if (CollectionUtils.isNotEmpty(taskResults)) {
            this.result = taskResults.stream().map(taskResult -> {
                String value = taskResult.getValue() == null ? "0" : taskResult.getValue() + "";
                return StringUtils.trimToEmpty(taskResult.getEnvName()) + "("+value+")";
            }).collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));
        }
        Rule rule = SpringContextHolder.getBean(RuleDao.class).findById(currentRuleId);
        if (rule != null) {
            this.ruleType = rule.getRuleType();
            this.ruleGroupId = rule.getRuleGroup().getId();
            if (CollectionUtils.isNotEmpty(rule.getRuleGroup().getRuleDataSources())) {
                this.tableGroup = true;
            } else {
                this.tableGroup = false;
            }
        }
        this.saveTable = taskRuleSimpleMap.get(currentRuleId).getMidTableName();

        this.checkAlarmVariables = new ArrayList<>();
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimpleMap.get(currentRuleId).getTaskRuleAlarmConfigList()) {

            CheckAlarmVariable checkAlarmVariable = new CheckAlarmVariable(taskRuleAlarmConfig);

            if (! RuleTypeEnum.CUSTOM_RULE.getCode().equals(ruleType)) {
                checkAlarmVariable.setResult(result);
                this.checkAlarmVariables.add(checkAlarmVariable);

                continue;
            }

            if (taskRuleAlarmConfig.getRuleMetric() != null && CollectionUtils.isNotEmpty(taskResults)) {
                List<TaskResult> tmpTaskResults = taskResults.stream().filter(taskResult -> taskResult.getRuleMetricId() != null).filter(taskResult -> taskResult.getRuleMetricId().equals(taskRuleAlarmConfig.getRuleMetric().getId())).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(tmpTaskResults)) {
                    String tmpValue = tmpTaskResults.stream().map(taskResult -> {
                        String value = taskResult.getValue() == null ? "0" : taskResult.getValue() + "";
                        return StringUtils.trimToEmpty(taskResult.getEnvName()) + "("+value+")";
                    }).collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));
                    checkAlarmVariable.setResult(tmpValue);
                }
            }
            this.checkAlarmVariables.add(checkAlarmVariable);
        }

        this.datasourceType = taskDataSource.getDatasourceType();
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<CheckAlarmVariable> getCheckAlarmVariables() {
        return checkAlarmVariables;
    }

    public void setCheckAlarmVariables(List<CheckAlarmVariable> checkAlarmVariables) {
        this.checkAlarmVariables = checkAlarmVariables;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSaveTable() {
        return saveTable;
    }

    public void setSaveTable(String saveTable) {
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

    public Boolean getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(Boolean tableGroup) {
        this.tableGroup = tableGroup;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    @Override
    public String toString() {
        return "CheckTableResponse{" +
                "ruleName='" + ruleName + '\'' +
                ", columnNames=" + columnNames +
                ", checkAlarmVariables=" + checkAlarmVariables +
                ", result=" + result +
                ", saveTable='" + saveTable + '\'' +
                '}';
    }
}
