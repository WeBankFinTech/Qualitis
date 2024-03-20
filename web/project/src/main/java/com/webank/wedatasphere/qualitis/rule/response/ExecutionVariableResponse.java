package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ExecutionVariableResponse {

    @JsonProperty("variable_id")
    private Long variableId;

    @JsonProperty("variable_type")
    private Integer variableType;

    @JsonProperty("variable_name")
    private String variableName;

    @JsonProperty("variable_value")
    private String variableValue;

    public Long getVariableId() {
        return variableId;
    }

    public void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    public Integer getVariableType() {
        return variableType;
    }

    public void setVariableType(Integer variableType) {
        this.variableType = variableType;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }
}
