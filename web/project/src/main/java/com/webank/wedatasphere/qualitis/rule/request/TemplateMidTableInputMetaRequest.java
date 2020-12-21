package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author allenzhou
 */
public class TemplateMidTableInputMetaRequest {
    @JsonProperty("input_meta_name")
    private String name;
    @JsonProperty("input_meta_placeholder")
    private String placeholder;

    @JsonProperty("input_type")
    private Integer inputType;

    @JsonProperty("field_type")
    private Integer fieldType;

    @JsonProperty("replace_by_request")
    private Boolean replaceByRequest;

    @JsonProperty("regexp_type")
    private Integer regexpType;

    @JsonProperty("placeholder_description")
    private String placeholderDescription;

    public TemplateMidTableInputMetaRequest() {
    }

    public TemplateMidTableInputMetaRequest(String name, String placeholder, Integer inputType, Integer fieldType, Boolean replaceByRequest,
        Integer regexpType) {
        this.name = name;
        this.placeholder = placeholder;
        this.inputType = inputType;
        this.fieldType = fieldType;
        this.replaceByRequest = replaceByRequest;
        this.regexpType = regexpType;
    }

    public static void checkRequest(TemplateMidTableInputMetaRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getName(), "templateMidTableInputMetaRequestName");
        CommonChecker.checkString(request.getPlaceholder(), "templateMidTableInputMetaRequestPlaceholder");
        CommonChecker.checkObject(request.getInputType(), "templateMidTableInputMetaRequestInputType");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getReplaceByRequest() {
        return replaceByRequest;
    }

    public void setReplaceByRequest(Boolean replaceByRequest) {
        this.replaceByRequest = replaceByRequest;
    }

    public Integer getRegexpType() {
        return regexpType;
    }

    public void setRegexpType(Integer regexpType) {
        this.regexpType = regexpType;
    }

    public String getPlaceholderDescription() {
        return placeholderDescription;
    }

    public void setPlaceholderDescription(String placeholderDescription) {
        this.placeholderDescription = placeholderDescription;
    }
}
