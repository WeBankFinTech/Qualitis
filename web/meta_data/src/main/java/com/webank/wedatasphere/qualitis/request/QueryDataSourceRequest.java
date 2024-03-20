package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-16 16:54
 * @description
 */
public class QueryDataSourceRequest {

    private String name;
    @JsonProperty("data_source_type_id")
    private Long dataSourceTypeId;
    @JsonProperty("sub_system_name")
    private String subSystem;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visible_department_ids")
    private List<Long> visibleDepartmentIds;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;
    private Integer page = 0;
    private Integer size = 10;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public List<Long> getVisibleDepartmentIds() {
        return visibleDepartmentIds;
    }

    public void setVisibleDepartmentId(List<Long> visibleDepartmentIds) {
        this.visibleDepartmentIds = visibleDepartmentIds;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "QueryDataSourceRequest{" +
                "name='" + name + '\'' +
                ", dataSourceTypeId=" + dataSourceTypeId +
                ", subSystem='" + subSystem + '\'' +
                ", devDepartmentId=" + devDepartmentId +
                ", opsDepartmentId=" + opsDepartmentId +
                ", visibleDepartmentIds=" + visibleDepartmentIds +
                ", createUser='" + createUser + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
