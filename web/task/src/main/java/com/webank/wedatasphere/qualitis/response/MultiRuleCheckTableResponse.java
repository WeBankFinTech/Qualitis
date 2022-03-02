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
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

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

    public MultiRuleCheckTableResponse() {
    }

    public MultiRuleCheckTableResponse(TaskRuleSimple taskRuleSimple, Map<Long, List<TaskResult>> resultMap) {
        this.ruleName = taskRuleSimple.getRuleName();
        this.checkAlarmVariables = new ArrayList<>();
        this.result = new ArrayList<>();
        this.saveTable = new ArrayList<>();
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
            CheckAlarmVariable checkAlarmVariable = new CheckAlarmVariable(taskRuleAlarmConfig);
            this.checkAlarmVariables.add(checkAlarmVariable);
        }
        if (taskRuleSimple.getChildRuleSimple() != null) {
            for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getChildRuleSimple().getTaskRuleAlarmConfigList()) {
                CheckAlarmVariable checkAlarmVariable = new CheckAlarmVariable(taskRuleAlarmConfig);
                this.checkAlarmVariables.add(checkAlarmVariable);
            }
        }
        List<TaskResult> taskResults = resultMap.get(taskRuleSimple.getRuleId());
        if (CollectionUtils.isNotEmpty(taskResults)) {
            TaskResult taskResult = taskResults.iterator().next();
            this.result.add(new MultiRuleResultResponse(taskResult, 0));
            this.saveTable.add(new MultiRuleSaveTableResponse(taskRuleSimple.getMidTableName(), 0));
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
}
