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

/**
 * @author allenzhou
 */
public class ApplicationResultValueResponse {
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("value")
    private String value;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_metric_name")
    private String ruleMetricName;
    @JsonProperty("env_name")
    private String envName;

    public ApplicationResultValueResponse() {
    }

    public ApplicationResultValueResponse(TaskResult taskResult) {
        this.createTime = taskResult.getCreateTime();
        this.envName = taskResult.getEnvName();
        this.value = taskResult.getValue();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    @Override
    public String toString() {
        return "ApplicationResultValueResponse{" +
            "createTime='" + createTime + '\'' +
            ", value='" + value + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", ruleMetricName='" + ruleMetricName + '\'' +
            ", envName='" + envName + '\'' +
            '}';
    }
}
