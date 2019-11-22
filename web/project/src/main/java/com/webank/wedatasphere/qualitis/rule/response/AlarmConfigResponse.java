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
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;

/**
 * @author howeye
 */
public class AlarmConfigResponse {

    @JsonProperty("output_meta_id")
    private Long outputMetaId;
    @JsonProperty("output_meta_name")
    private String outputMetaName;
    @JsonProperty("check_template")
    private Integer checkTemplate;
    @JsonProperty("compare_type")
    private Integer compareType;
    private Double threshold;

    public AlarmConfigResponse() {
    }

    public AlarmConfigResponse(AlarmConfig alarmConfig) {
        this.outputMetaId = alarmConfig.getTemplateOutputMeta().getId();
        this.outputMetaName = alarmConfig.getTemplateOutputMeta().getOutputName();
        this.checkTemplate = alarmConfig.getCheckTemplate();
        this.compareType = alarmConfig.getCompareType();
        this.threshold = alarmConfig.getThreshold();
    }

    public Long getOutputMetaId() {
        return outputMetaId;
    }

    public void setOutputMetaId(Long outputMetaId) {
        this.outputMetaId = outputMetaId;
    }

    public String getOutputMetaName() {
        return outputMetaName;
    }

    public void setOutputMetaName(String outputMetaName) {
        this.outputMetaName = outputMetaName;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
}
