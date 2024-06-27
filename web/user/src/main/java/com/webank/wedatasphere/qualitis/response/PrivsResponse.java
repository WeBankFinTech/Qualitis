package com.webank.wedatasphere.qualitis.response;

import com.webank.wedatasphere.qualitis.entity.Permission;

import java.util.Objects;

/**
 * @author v_gaojiedeng@webank.com
 */
public class PrivsResponse {

    private String privCode;
    private String privName;
    private String privNameCn;

    public PrivsResponse(Permission permission) {
        this.privCode = permission.getMethod() + " " + permission.getUrl();
        this.privName = permission.getEnName();
        this.privNameCn = permission.getCnName();
    }

    public String getPrivCode() {
        return privCode;
    }

    public void setPrivCode(String privCode) {
        this.privCode = privCode;
    }

    public String getPrivName() {
        return privName;
    }

    public void setPrivName(String privName) {
        this.privName = privName;
    }

    public String getPrivNameCn() {
        return privNameCn;
    }

    public void setPrivNameCn(String privNameCn) {
        this.privNameCn = privNameCn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivsResponse that = (PrivsResponse) o;
        return privCode.equals(that.privCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privCode);
    }
}
