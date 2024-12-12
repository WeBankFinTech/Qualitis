package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-15 14:09
 * @description
 */
public class QueryProxyUserRequest extends PageRequest {

    @JsonProperty("proxy_user_name")
    private String proxyUserName;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("sub_department_code")
    private String subDepartmentCode;

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getSubDepartmentCode() {
        return subDepartmentCode;
    }

    public void setSubDepartmentCode(String subDepartmentCode) {
        this.subDepartmentCode = subDepartmentCode;
    }
}
