package com.webank.wedatasphere.qualitis.request.user;

import com.google.common.base.Preconditions;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-13 11:23
 * @description
 */
public class ItsmAlertWhitelistAddRequest {

    private String item;
    private Integer type;
    @JsonProperty("authorized_user")
    private String authorizedUser;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAuthorizedUser() {
        return authorizedUser;
    }

    public void setAuthorizedUser(String authorizedUser) {
        this.authorizedUser = authorizedUser;
    }

    public static void checkRequest(ItsmAlertWhitelistAddRequest alertWhitelistAddRequest) throws UnExpectedRequestException {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(alertWhitelistAddRequest.getItem()), "item must be not empty");
            Preconditions.checkArgument(StringUtils.isNotBlank(alertWhitelistAddRequest.getAuthorizedUser()), "authorized_user must be not empty");
            Preconditions.checkNotNull(alertWhitelistAddRequest.getType(), "type must be not empty");
        } catch (Exception e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }
}
