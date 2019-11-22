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
public class TemplateOutputMetaSimpleResponse {

    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("output_id")
    private Long outputId;

    public TemplateOutputMetaSimpleResponse() {
    }

    public TemplateOutputMetaSimpleResponse(TemplateOutputMeta templateOutputMeta) {
        this.outputName = templateOutputMeta.getOutputName();
        this.outputId = templateOutputMeta.getId();
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public Long getOutputId() {
        return outputId;
    }

    public void setOutputId(Long outputId) {
        this.outputId = outputId;
    }
}
