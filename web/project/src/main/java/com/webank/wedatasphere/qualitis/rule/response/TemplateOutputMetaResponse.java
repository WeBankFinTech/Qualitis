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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;

/**
 * @author howeye
 */
public class TemplateOutputMetaResponse {

    @JsonProperty("output_id")
    private Long outputId;
    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("field_name")
    private String fieldName;
    @JsonProperty("field_type")
    private Integer fieldType;

    public TemplateOutputMetaResponse() {
    }

    public TemplateOutputMetaResponse(TemplateOutputMeta templateOutputMeta) {
        this.outputId = templateOutputMeta.getId();
        this.outputName = templateOutputMeta.getOutputName();
        this.fieldName = templateOutputMeta.getFieldName();
        this.fieldType = templateOutputMeta.getFieldType();
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Long getOutputId() {
        return outputId;
    }

    public void setOutputId(Long outputId) {
        this.outputId = outputId;
    }
}
