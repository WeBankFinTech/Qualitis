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
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class TaskRuleResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_data_sources")
    private List<RuleDataSourceResponse> ruleDataSources;

    public TaskRuleResponse(TaskRuleSimple taskRuleSimple, Set<TaskDataSource> taskDataSources) {
        this.ruleId = taskRuleSimple.getRuleId();
        this.ruleName = taskRuleSimple.getRuleName();
        this.ruleDataSources = new ArrayList<>();
        List<TaskDataSource> dataSources = taskDataSources.stream().filter(jobDataSource -> jobDataSource.getRuleId().equals(ruleId)).collect(Collectors.toList());
        for (TaskDataSource taskDataSource : dataSources) {
            if (taskDataSource.getDatasourceIndex() != null && taskDataSource.getDatasourceIndex() < 0) {
                continue;
            }
            this.ruleDataSources.add(new RuleDataSourceResponse(taskDataSource));
        }
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<RuleDataSourceResponse> getRuleDataSources() {
        return ruleDataSources;
    }

    public void setRuleDataSources(List<RuleDataSourceResponse> ruleDataSources) {
        this.ruleDataSources = ruleDataSources;
    }
}
