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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.Template;

/**
 * @author howeye
 */
public class RuleTemplateResponse {

    @JsonProperty("template_id")
    private Long ruleTemplateId;
    @JsonProperty("template_name")
    private String ruleTemplateName;

    public RuleTemplateResponse() {
    }

    public RuleTemplateResponse(Template template) {
        this.ruleTemplateId = template.getId();
        this.ruleTemplateName = template.getName();
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    @Override
    public String toString() {
        return "RuleTemplateResponse{" +
                "ruleTemplateId=" + ruleTemplateId +
                ", ruleTemplateName='" + ruleTemplateName + '\'' +
                '}';
    }
}
