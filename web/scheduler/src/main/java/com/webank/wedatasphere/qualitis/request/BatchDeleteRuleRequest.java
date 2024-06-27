package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class BatchDeleteRuleRequest {

    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("login_user")
    private String loginUser;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public static void checkRequest(BatchDeleteRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getRuleName(), "rule_name");
        CommonChecker.checkString(request.getProjectName(), "project_name");
        CommonChecker.checkString(request.getCreateUser(), "create_user");
        CommonChecker.checkString(request.getLoginUser(), "login_user");
    }

}





