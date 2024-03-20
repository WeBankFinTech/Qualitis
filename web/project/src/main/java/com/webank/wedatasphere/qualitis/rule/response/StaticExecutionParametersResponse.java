package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class StaticExecutionParametersResponse {

    @JsonProperty("parameter_id")
    private Long parameterId;

    @JsonProperty("parameter_type")
    private Integer parameterType;

    @JsonProperty("parameter_name")
    private String parameterName;

    @JsonProperty("parameter_value")
    private String parameterValue;

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public Integer getParameterType() {
        return parameterType;
    }

    public void setParameterType(Integer parameterType) {
        this.parameterType = parameterType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
