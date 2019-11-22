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
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private Double result;
    @JsonProperty("save_table")
    private String saveTable;

    public CheckTableResponse() {
    }

    public CheckTableResponse(TaskDataSource taskDataSource, Map<Long, TaskRuleSimple> taskRuleSimpleMap, Map<Long, TaskResult> taskResultMap) {
        Long ruleId = taskDataSource.getRuleId();
        this.ruleName = taskRuleSimpleMap.get(ruleId).getRuleName();
        this.columnNames = Arrays.asList(taskDataSource.getColName().split(","));
        TaskResult taskResult = taskResultMap.get(ruleId);
        this.result = taskResult == null? null : taskResult.getValue();
        this.saveTable = taskRuleSimpleMap.get(ruleId).getMidTableName();
        this.checkAlarmVariables = new ArrayList<>();
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimpleMap.get(ruleId).getTaskRuleAlarmConfigList()) {
            CheckAlarmVariable checkAlarmVariable = new CheckAlarmVariable(taskRuleAlarmConfig);
            this.checkAlarmVariables.add(checkAlarmVariable);
        }
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

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public String getSaveTable() {
        return saveTable;
    }

    public void setSaveTable(String saveTable) {
        this.saveTable = saveTable;
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
