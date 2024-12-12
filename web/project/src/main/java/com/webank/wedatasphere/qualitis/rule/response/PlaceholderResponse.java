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
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDefaultInputMeta;
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
    @JsonProperty("input_cn_name")
    private String inputCnName;
    @JsonProperty("input_en_name")
    private String inputEnName;
    @JsonProperty("placeholder_description")
    private String placeholderDescription;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("cn_description")
    private String cnDescription;
    @JsonProperty("en_description")
    private String enDescription;
    @JsonProperty("field_multiple_choice")
    private Boolean fieldMultipleChoice;
    @JsonProperty("whether_standard_value")
    private Boolean whetherStandardValue;
    @JsonProperty("whether_new_value")
    private Boolean whetherNewValue;
    @JsonProperty("name")
    private String name;


    public PlaceholderResponse() {
    }

    public PlaceholderResponse(TemplateMidTableInputMeta templateMidTableInputMeta) {
        this.placeholder = "${" + templateMidTableInputMeta.getPlaceholder() + "}";
        this.placeholderId = templateMidTableInputMeta.getId();
        this.inputType = templateMidTableInputMeta.getInputType();
        this.placeholderDescription = templateMidTableInputMeta.getPlaceholderDescription();
        this.cnName = templateMidTableInputMeta.getCnName();
        this.enName = templateMidTableInputMeta.getEnName();
        this.cnDescription = templateMidTableInputMeta.getCnDescription();
        this.enDescription = templateMidTableInputMeta.getEnDescription();
        this.fieldMultipleChoice = templateMidTableInputMeta.getFieldMultipleChoice();
        this.whetherStandardValue = templateMidTableInputMeta.getWhetherStandardValue();
        this.whetherNewValue = templateMidTableInputMeta.getWhetherNewValue();
        this.name = templateMidTableInputMeta.getName();
        this.inputCnName = TemplateInputTypeEnum.getTemplateData(templateMidTableInputMeta.getInputType()).get("cnMessage").toString();
        this.inputEnName = TemplateInputTypeEnum.getTemplateData(templateMidTableInputMeta.getInputType()).get("enMessage").toString();
    }

    public PlaceholderResponse(TemplateDefaultInputMeta templateDefaultInputMeta) {
        this.placeholder = templateDefaultInputMeta.getPlaceholder();
        this.placeholderId = templateDefaultInputMeta.getId();
        this.inputType = templateDefaultInputMeta.getType();
        this.placeholderDescription = templateDefaultInputMeta.getPlaceholderDesc();
        this.cnName = templateDefaultInputMeta.getCnName();
        this.enName = templateDefaultInputMeta.getEnName();
        this.cnDescription = templateDefaultInputMeta.getCnDesc();
        this.enDescription = templateDefaultInputMeta.getEnDesc();
        this.fieldMultipleChoice = templateDefaultInputMeta.getSupportFields();
        this.whetherStandardValue = templateDefaultInputMeta.getSupportStandard();
        this.whetherNewValue = templateDefaultInputMeta.getSupportNewValue();
        this.inputCnName = TemplateInputTypeEnum.getTemplateData(templateDefaultInputMeta.getType()).get("cnMessage").toString();
        this.inputEnName = TemplateInputTypeEnum.getTemplateData(templateDefaultInputMeta.getType()).get("enMessage").toString();
    }

    public PlaceholderResponse(TemplateMidTableInputMeta templateMidTableInputMeta, List<TemplateRegexpExpr> templateRegexpExprs) {
        this.placeholder = "${" + templateMidTableInputMeta.getPlaceholder() + "}";
        this.placeholderId = templateMidTableInputMeta.getId();
        this.inputType = templateMidTableInputMeta.getInputType();
        this.placeholderDescription = templateMidTableInputMeta.getPlaceholderDescription();
        this.cnName = templateMidTableInputMeta.getCnName();
        this.enName = templateMidTableInputMeta.getEnName();
        this.cnDescription = templateMidTableInputMeta.getCnDescription();
        this.enDescription = templateMidTableInputMeta.getEnDescription();
        this.fieldMultipleChoice = templateMidTableInputMeta.getFieldMultipleChoice();
        this.whetherStandardValue = templateMidTableInputMeta.getWhetherStandardValue();
        this.whetherNewValue = templateMidTableInputMeta.getWhetherNewValue();
        this.name = templateMidTableInputMeta.getName();
        this.inputCnName = TemplateInputTypeEnum.getTemplateData(templateMidTableInputMeta.getInputType()).get("cnMessage").toString();
        this.inputEnName = TemplateInputTypeEnum.getTemplateData(templateMidTableInputMeta.getInputType()).get("enMessage").toString();

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

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnDescription() {
        return cnDescription;
    }

    public void setCnDescription(String cnDescription) {
        this.cnDescription = cnDescription;
    }

    public String getEnDescription() {
        return enDescription;
    }

    public void setEnDescription(String enDescription) {
        this.enDescription = enDescription;
    }

    public Boolean getFieldMultipleChoice() {
        return fieldMultipleChoice;
    }

    public void setFieldMultipleChoice(Boolean fieldMultipleChoice) {
        this.fieldMultipleChoice = fieldMultipleChoice;
    }

    public Boolean getWhetherStandardValue() {
        return whetherStandardValue;
    }

    public void setWhetherStandardValue(Boolean whetherStandardValue) {
        this.whetherStandardValue = whetherStandardValue;
    }

    public Boolean getWhetherNewValue() {
        return whetherNewValue;
    }

    public void setWhetherNewValue(Boolean whetherNewValue) {
        this.whetherNewValue = whetherNewValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputCnName() {
        return inputCnName;
    }

    public void setInputCnName(String inputCnName) {
        this.inputCnName = inputCnName;
    }

    public String getInputEnName() {
        return inputEnName;
    }

    public void setInputEnName(String inputEnName) {
        this.inputEnName = inputEnName;
    }
}
