package com.webank.wedatasphere.qualitis.response;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class UserPermissionResponse {

    private String userId;
    private String userName;
    private List<UserRolesResponse> roles;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<UserRolesResponse> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRolesResponse> roles) {
        this.roles = roles;
    }
}
