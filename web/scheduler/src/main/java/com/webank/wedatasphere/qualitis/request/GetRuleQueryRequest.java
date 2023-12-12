package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class GetRuleQueryRequest extends PageRequest {

    @JsonProperty("app_id")
    private String appId;
    private Long timestamp;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("login_user")
    private String loginUser;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

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

    public static void checkRequest(GetRuleQueryRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getAppId(), "appId");
        CommonChecker.checkObject(request.getTimestamp(),"timestamp");
        CommonChecker.checkObject(request.getNonce(),"nonce");
        CommonChecker.checkString(request.getSignature(), "signature");
        CommonChecker.checkString(request.getSignature(), "project_name");
        CommonChecker.checkString(request.getLoginUser(), "login_user");

    }





}
