package com.webank.wedatasphere.qualitis.dto;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-13 10:26
 * @description
 */
public class ItsmUserDto {

    private String username;
    private String departmentName;
    private String subDepartmentName;
    private String positionEn;
    private String positionZn;
    private String proxyUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getSubDepartmentName() {
        return subDepartmentName;
    }

    public void setSubDepartmentName(String subDepartmentName) {
        this.subDepartmentName = subDepartmentName;
    }

    public String getPositionEn() {
        return positionEn;
    }

    public void setPositionEn(String positionEn) {
        this.positionEn = positionEn;
    }

    public String getPositionZn() {
        return positionZn;
    }

    public void setPositionZn(String positionZn) {
        this.positionZn = positionZn;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }
}
