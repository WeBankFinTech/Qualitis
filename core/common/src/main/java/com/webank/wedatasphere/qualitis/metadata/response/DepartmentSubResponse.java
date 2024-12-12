package com.webank.wedatasphere.qualitis.metadata.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-21 10:19
 * @description
 */
public class DepartmentSubResponse {

    @JsonProperty("department_sub_id")
    private String id;
    @JsonProperty("department_sub_name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
