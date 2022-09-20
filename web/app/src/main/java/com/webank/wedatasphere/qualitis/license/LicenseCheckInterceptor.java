package com.webank.wedatasphere.qualitis.license;

import com.webank.wedatasphere.qualitis.license.exception.LicenseAuthenticationException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;



/**
 * LicenseCheckInterceptor
 */
public class LicenseCheckInterceptor implements ContainerRequestFilter {
    private static Logger logger = LogManager.getLogger(LicenseCheckInterceptor.class);

    private static final String loginUrl = "api/v1/login/local";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        UriInfo uriInfo = requestContext.getUriInfo();
        if (uriInfo.getPath().equals(loginUrl)) {
            LicenseVerify licenseVerify = new LicenseVerify();

            System.out.println("校验证书是否生效");

            //校验证书是否有效
            boolean verifyResult = licenseVerify.verify();

            if (!verifyResult) {
                GeneralResponse<Object> response = new GeneralResponse<>();
                response.setCode(HttpStatus.BAD_REQUEST.toString());
                response.setMessage("您的证书无效，请核查服务器是否取得授权或重新申请证书！");
                throw new LicenseAuthenticationException(response);
            }
        }

    }
}
