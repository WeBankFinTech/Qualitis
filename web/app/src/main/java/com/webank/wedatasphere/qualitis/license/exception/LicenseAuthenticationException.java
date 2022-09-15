package com.webank.wedatasphere.qualitis.license.exception;

import com.webank.wedatasphere.qualitis.response.GeneralResponse;

public class LicenseAuthenticationException extends RuntimeException {

    private GeneralResponse<Object> response;

    public LicenseAuthenticationException() {
    }

    public LicenseAuthenticationException(GeneralResponse response) {
        this.response = response;
    }

    public GeneralResponse getResponse() {
        return response;
    }

    public void setResponse(GeneralResponse response) {
        this.response = response;
    }
}
