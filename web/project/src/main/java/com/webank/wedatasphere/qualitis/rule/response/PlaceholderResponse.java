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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class PlaceholderResponse {

    private String placeholder;
    @JsonProperty("placeholder_id")
    private Long placeholderId;
    @JsonProperty("enum_value")
    private List<EnumValueResponse> enumValue;
    @JsonProperty("input_type")
    private Integer inputType;
    @JsonProperty("placeholder_description")
    private String placeholderDescription;

    public PlaceholderResponse() {
    }

    public PlaceholderResponse(TemplateMidTableInputMeta templateMidTableInputMeta) {
        this.placeholder = "${" + templateMidTableInputMeta.getPlaceholder() + "}";
        this.placeholderId = templateMidTableInputMeta.getId();
        this.inputType = templateMidTableInputMeta.getInputType();
        this.placeholderDescription = templateMidTableInputMeta.getPlaceholderDescription();
    }

    public PlaceholderResponse(TemplateMidTableInputMeta templateMidTableInputMeta, List<TemplateRegexpExpr> templateRegexpExprs) {
        this.placeholder = "${" + templateMidTableInputMeta.getPlaceholder() + "}";
        this.placeholderId = templateMidTableInputMeta.getId();
        this.inputType = templateMidTableInputMeta.getInputType();
        this.placeholderDescription = templateMidTableInputMeta.getPlaceholderDescription();

        enumValue = new ArrayList<>();
        for (TemplateRegexpExpr templateRegexpExpr : templateRegexpExprs) {
            enumValue.add(new EnumValueResponse(templateRegexpExpr));
        }
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Long getPlaceholderId() {
        return placeholderId;
    }

    public void setPlaceholderId(Long placeholderId) {
        this.placeholderId = placeholderId;
    }

    public List<EnumValueResponse> getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(List<EnumValueResponse> enumValue) {
        this.enumValue = enumValue;
    }

    public String getPlaceholderDescription() {
        return placeholderDescription;
    }

    public void setPlaceholderDescription(String placeholderDescription) {
        this.placeholderDescription = placeholderDescription;
    }
}
