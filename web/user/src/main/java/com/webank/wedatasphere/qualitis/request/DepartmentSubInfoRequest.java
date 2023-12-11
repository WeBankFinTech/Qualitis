package com.webank.wedatasphere.qualitis.request;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-20 16:42
 * @description
 */
public class DepartmentSubInfoRequest {

    private Long id;
    private String name;

    public DepartmentSubInfoRequest() {
        // Default Constructor
    }

    public DepartmentSubInfoRequest(Long id, String name) {
        this.id = id;
        this.name = name;
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
