package com.webank.wedatasphere.qualitis.response;

import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-21 10:54
 * @description
 */
public class DepartmentSubInfoResponse {

    private Long id;
    private String name;

    public DepartmentSubInfoResponse() {
    }

    public DepartmentSubInfoResponse(DataVisibility dataVisibility) {
        this.id = dataVisibility.getDepartmentSubId();
        this.name = dataVisibility.getDepartmentSubName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
