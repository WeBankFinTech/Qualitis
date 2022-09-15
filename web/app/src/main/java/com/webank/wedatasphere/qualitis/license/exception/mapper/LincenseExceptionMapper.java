package com.webank.wedatasphere.qualitis.license.exception.mapper;

import com.webank.wedatasphere.qualitis.license.exception.LicenseAuthenticationException;
import org.springframework.http.HttpStatus;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LincenseExceptionMapper  implements ExceptionMapper<LicenseAuthenticationException> {
    @Override
    public Response toResponse(LicenseAuthenticationException exception) {
        return Response.ok(exception.getResponse()).status(HttpStatus.BAD_REQUEST.value()).type(MediaType.APPLICATION_JSON).build();
    }
}
