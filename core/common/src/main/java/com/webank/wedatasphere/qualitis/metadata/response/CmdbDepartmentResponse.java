package com.webank.wedatasphere.qualitis.metadata.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-20 16:13
 * @description
 */
public class CmdbDepartmentResponse {

    @JsonProperty("department_code")
    private String code;
    @JsonProperty("department_name")
    private String name;
    private String disable;

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
