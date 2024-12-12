package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class GetRuleQueryRequest extends PageRequest {

    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("login_user")
    private String loginUser;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public static void checkRequest(GetRuleQueryRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getProjectName(), "project_name");
        CommonChecker.checkString(request.getCreateUser(), "create_user");
        CommonChecker.checkString(request.getLoginUser(), "login_user");
    }

}
