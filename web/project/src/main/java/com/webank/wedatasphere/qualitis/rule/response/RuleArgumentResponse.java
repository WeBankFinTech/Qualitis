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
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;

/**
 * @author howeye
 */
public class RuleArgumentResponse {

    @JsonProperty("argument_id")
    private Long argumentId;
    @JsonProperty("argument_name")
    private String argumentName;
    @JsonProperty("argument_type")
    private Integer argumentType;
    @JsonProperty("argument_step")
    private Integer argumentStep;
    @JsonProperty("regexp_type")
    private Integer regexpType;

    public RuleArgumentResponse() {
    }

    public RuleArgumentResponse(TemplateMidTableInputMeta templateMidTableInputMeta) {
        this.argumentId = templateMidTableInputMeta.getId();
        this.argumentName = templateMidTableInputMeta.getName();
        this.argumentType = templateMidTableInputMeta.getInputType();
        this.argumentStep = InputActionStepEnum.TEMPLATE_INPUT_META.getCode();
        this.regexpType = templateMidTableInputMeta.getRegexpType();
    }

    public RuleArgumentResponse(TemplateStatisticsInputMeta templateStatisticsInputMeta) {
        this.argumentId = templateStatisticsInputMeta.getId();
        this.argumentName = templateStatisticsInputMeta.getName();
        this.argumentType = templateStatisticsInputMeta.getValueType();
        this.argumentStep = InputActionStepEnum.STATISTICS_ARG.getCode();
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public Integer getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(Integer argumentType) {
        this.argumentType = argumentType;
    }

    public Integer getArgumentStep() {
        return argumentStep;
    }

    public void setArgumentStep(Integer argumentStep) {
        this.argumentStep = argumentStep;
    }

    public Long getArgumentId() {
        return argumentId;
    }

    public void setArgumentId(Long argumentId) {
        this.argumentId = argumentId;
    }

    public Integer getRegexpType() {
        return regexpType;
    }

    public void setRegexpType(Integer regexpType) {
        this.regexpType = regexpType;
    }
}
