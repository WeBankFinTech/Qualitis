package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

/**
 * @author v_gaojiedeng@webank.com
 */
@Path("operation/aomp/detection")
public class ExploringActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExploringActivityController.class);

    @GET
    @Path("alive")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse alive() {
        HttpServletRequest request = getHttpServletRequest();
        String clientIp = QualitisConstants.getIp(request);
        LOGGER.info("Client Ip: " + clientIp);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "您的IP地址: " + clientIp, clientIp);
    }


    /**
     * 获取 HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

}
