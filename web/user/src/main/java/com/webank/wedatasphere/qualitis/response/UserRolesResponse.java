package com.webank.wedatasphere.qualitis.response;

import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.UserRole;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
public class UserRolesResponse {

    private String roleCode;
    private String roleName;
    private String roleNameCn;
    private List<PrivsResponse> privs;

    public UserRolesResponse() {
        // Default Constructor
    }

    public UserRolesResponse(UserRole userRole) {
        this.roleCode = userRole.getRole().getId() + SpecCharEnum.BOTTOM_BAR.getValue() + userRole.getRole().getName();
        this.roleName = userRole.getRole().getName();
        this.roleNameCn = userRole.getRole().getZnName();

        Set<PrivsResponse> set = Sets.newHashSet();
        for (Permission permission : userRole.getRole().getPermissions()) {
            set.add(new PrivsResponse(permission));
        }
        this.privs = set.stream().collect(Collectors.toList());
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleNameCn() {
        return roleNameCn;
    }

    public void setRoleNameCn(String roleNameCn) {
        this.roleNameCn = roleNameCn;
    }

    public List<PrivsResponse> getPrivs() {
        return privs;
    }

    public void setPrivs(List<PrivsResponse> privs) {
        this.privs = privs;
    }
}
