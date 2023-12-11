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

    @JsonProperty("app_id")
    private String appId;
    private Long timestamp;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("rule_id_list")
    private List<Long> ruleIdList;
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

    public List<Long> getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List<Long> ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public static void checkRequest(BatchDeleteRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getAppId(), "appId");
        CommonChecker.checkObject(request.getTimestamp(),"timestamp");
        CommonChecker.checkObject(request.getNonce(),"nonce");
        CommonChecker.checkString(request.getSignature(), "signature");
        CommonChecker.checkString(request.getLoginUser(), "login_user");

        if (request.getRuleIdList()== null && CollectionUtils.isEmpty(request.getRuleIdList())) {
            throw new UnExpectedRequestException("rule id list" + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

    }

}





