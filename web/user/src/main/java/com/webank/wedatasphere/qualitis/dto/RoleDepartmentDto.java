package com.webank.wedatasphere.qualitis.dto;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-24 10:11
 * @description
 */
public class RoleDepartmentDto {

    private RoleSystemTypeEnum roleSystemType;

    private List<Department> departmentList;

    public RoleSystemTypeEnum getRoleSystemType() {
        return roleSystemType;
    }

    public void setRoleSystemType(RoleSystemTypeEnum roleSystemType) {
        this.roleSystemType = roleSystemType;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }
}
