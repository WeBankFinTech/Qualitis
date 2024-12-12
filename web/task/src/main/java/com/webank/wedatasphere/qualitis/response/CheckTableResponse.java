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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

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

    public CheckTableResponse(TaskDataSource taskDataSource, Map<Long, TaskRuleSimple> taskRuleSimpleMap, Map<Long, List<TaskResult>> taskResultMap) {
        Long ruleId = taskDataSource.getRuleId();
        this.ruleName = taskRuleSimpleMap.get(ruleId).getRuleName();
        String col = taskDataSource.getColName();
        if (StringUtils.isNotBlank(col)) {
            this.columnNames = Arrays.asList(col.split(","));
        }
        List<TaskResult> taskResults = taskResultMap.get(ruleId);
        if (CollectionUtils.isNotEmpty(taskResults)) {
            this.result = taskResults.stream().map(TaskResult::getValue).map(value -> value == null ? "0 " : value + " ").collect(Collectors.joining());
        }
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
