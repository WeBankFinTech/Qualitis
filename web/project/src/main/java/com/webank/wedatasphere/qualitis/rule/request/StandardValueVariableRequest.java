package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-20 10:16
 * @description
 */
public class StandardValueVariableRequest {

    @JsonProperty("standard_value_variables_id")
    private Long standardValueVariablesId;
    @JsonProperty("standard_value_version_variables_name")
    private String standardValueVersionVariablesName;


    public Long getStandardValueVariablesId() {
        return standardValueVariablesId;
    }

    public void setStandardValueVariablesId(Long standardValueVariablesId) {
        this.standardValueVariablesId = standardValueVariablesId;
    }

    public String getStandardValueVersionVariablesName() {
        return standardValueVersionVariablesName;
    }

    public void setStandardValueVersionVariablesName(String standardValueVersionVariablesName) {
        this.standardValueVersionVariablesName = standardValueVersionVariablesName;
    }

}
