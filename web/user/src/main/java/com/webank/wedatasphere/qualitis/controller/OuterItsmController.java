package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.user.ItsmRequest;
import com.webank.wedatasphere.qualitis.response.RetResponse;
import com.webank.wedatasphere.qualitis.service.ItsmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-12 16:36
 * @description
 */
@Path("outer/itsm/api/v1/")
public class OuterItsmController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterItsmController.class);

    @Autowired
    private ItsmService itsmService;

    /**
     * @param request
     * @return
     */
    @POST
    @Path("/user/action")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RetResponse user(ItsmRequest request) {
        LOGGER.info("Request body: {}", request.getData());

        try {
            itsmService.handleUser(request);
        } catch (UnExpectedRequestException e) {
            return new RetResponse(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage(), null);
        } catch (RoleNotFoundException e) {
            return new RetResponse(HttpServletResponse.SC_FORBIDDEN, "The user no permission.", null);
        } catch (Exception e) {
            return new RetResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return new RetResponse(null);
    }

    @POST
    @Path("/alert_whitelist/action")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RetResponse alertWhitelist(ItsmRequest request) {
        LOGGER.info("Request body: {}", request.getData());

        try {
            itsmService.handleAlertWhitelist(request);
        } catch (IllegalArgumentException e) {
            return new RetResponse(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            return new RetResponse(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage(), null);
        } catch (Exception e) {
            return new RetResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return new RetResponse(null);
    }

}
