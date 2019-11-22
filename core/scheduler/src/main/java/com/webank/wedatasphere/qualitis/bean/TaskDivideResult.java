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

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class TaskDivideResult {

    private String applicationId;
    private List<RuleSummary> rules;
    private String clusterName;
    private String ujesAddress;

    public TaskDivideResult() {
    }

    public TaskDivideResult(DataQualityTask task, String clusterName, String ujesAddress) {
        this.applicationId = task.getApplicationId();
        this.clusterName = clusterName;
        this.ujesAddress = ujesAddress;
        rules = new ArrayList<>();
        for (RuleTaskDetail ruleTaskDetail : task.getRuleTaskDetails()) {
            rules.add(new RuleSummary(ruleTaskDetail));
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public List<RuleSummary> getRules() {
        return rules;
    }

    public void setRules(List<RuleSummary> rules) {
        this.rules = rules;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getUjesAddress() {
        return ujesAddress;
    }

    public void setUjesAddress(String ujesAddress) {
        this.ujesAddress = ujesAddress;
    }

    @Override
    public String toString() {
        return "TaskDivideResult{" +
                ", applicationId='" + applicationId + '\'' +
                ", rules=" + rules +
                ", clusterName='" + clusterName + '\'' +
                ", ujesAddress='" + ujesAddress + '\'' +
                '}';
    }
}
