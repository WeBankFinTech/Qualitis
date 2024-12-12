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

import com.webank.wedatasphere.qualitis.rule.entity.Rule;

/**
 * @author howeye
 */
public class RuleTaskDetail {
    private Rule rule;
    private String midTableName;

    public RuleTaskDetail(Rule rule, String midTableName) {
        this.rule = rule;
        this.midTableName = midTableName;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getMidTableName() {
        return midTableName;
    }

    public void setMidTableName(String midTableName) {
        this.midTableName = midTableName;
    }

    @Override
    public String toString() {
        return "RuleTaskDetail{" +
                "rule=" + "ruleName: " + rule.getName() + " ruleId: " + rule.getId() +
                ", midTableName='" + midTableName + '\'' +
                '}';
    }
}
