package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author allenzhou
 */
public class TemplateOutputMetaRequest {
    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("field_name")
    private String fieldName;
    @JsonProperty("field_type")
    private Integer fieldType;

    public TemplateOutputMetaRequest() {
    }

    public TemplateOutputMetaRequest(String outputName, String fieldName, Integer fieldType) {
        this.outputName = outputName;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
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

    public static void checkRequest(TemplateOutputMetaRequest templateOutputMetaRequest) throws UnExpectedRequestException {
        CommonChecker.checkObject(templateOutputMetaRequest, "templateOutputMetaRequest");
        CommonChecker.checkString(templateOutputMetaRequest.getOutputName(), "templateOutputName");
        CommonChecker.checkString(templateOutputMetaRequest.getFieldName(), "templateOutputFieldName");
    }
}
