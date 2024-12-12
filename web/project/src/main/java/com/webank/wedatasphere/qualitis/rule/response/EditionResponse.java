package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class EditionResponse {

    @JsonProperty("standard_value_id")
    private Long standardValueId;
    @JsonProperty("version")
    private Long version;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;

//    public EditionResponse(StandardValue standardValue) {
//        this.standardValueId = standardValue.getId();
//        this.version = standardValue.getVersion();
//        this.createUser = standardValue.getCreateUser();
//        this.createTime = standardValue.getCreateTime();
//    }

    public Long getStandardValueId() {
        return standardValueId;
    }

    public void setStandardValueId(Long standardValueId) {
        this.standardValueId = standardValueId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
