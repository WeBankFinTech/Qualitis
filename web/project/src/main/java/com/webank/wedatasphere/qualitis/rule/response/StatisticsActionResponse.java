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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;

/**
 * @author howeye
 */
public class StatisticsActionResponse {

    @JsonProperty("statistics_action_id")
    private Long statisticsActionId;
    @JsonProperty("func_name")
    private String funcName;
    private String value;
    @JsonProperty("result_type")
    private String resultType;
    @JsonProperty("value_type")
    private Integer valueType;

    public StatisticsActionResponse() {
    }

    public StatisticsActionResponse(TemplateStatisticsInputMeta templateStatisticsInputMeta) {
        this.statisticsActionId = templateStatisticsInputMeta.getId();
        this.funcName = templateStatisticsInputMeta.getFuncName();
        this.value = templateStatisticsInputMeta.getValue();
        this.resultType = templateStatisticsInputMeta.getResultType();
        this.valueType = templateStatisticsInputMeta.getValueType();
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getStatisticsActionId() {
        return statisticsActionId;
    }

    public void setStatisticsActionId(Long statisticsActionId) {
        this.statisticsActionId = statisticsActionId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }
}
