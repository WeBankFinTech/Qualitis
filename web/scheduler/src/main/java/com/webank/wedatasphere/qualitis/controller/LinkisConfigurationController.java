package com.webank.wedatasphere.qualitis.controller;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 14:35
 */

import com.webank.wedatasphere.qualitis.client.LinkisConfiguration;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.SaveFullTreeRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.LinkisConfigurationResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/api/v1/projector/configuration")
public class LinkisConfigurationController {
    @Autowired
    private LinkisConfiguration linkisConfiguration;

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisConfigurationController.class);

    private HttpServletRequest httpServletRequest;

    public LinkisConfigurationController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getFullTree(@QueryParam("cluster_name") String clusterName) throws UnExpectedRequestException {
        String userName =  HttpUtils.getUserName(httpServletRequest);
        try {
            Map<String, Map> reponse =  linkisConfiguration.getFullTree(clusterName, userName);
            LinkisConfigurationResponse linkisConfigurationResponse = new LinkisConfigurationResponse();
            linkisConfigurationResponse.setQueueName(reponse.get("full_tree_queue_name"));
            linkisConfigurationResponse.setQueue(reponse.get("full_tree"));
            return new GeneralResponse<>("200", "{&SUCCESS_TO_GET_STARTUP_PATAM}", reponse);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to , caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_STARTUP_PATAM}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> saveFullTree(SaveFullTreeRequest request) throws UnExpectedRequestException {
        String userName =  HttpUtils.getUserName(httpServletRequest);
        try {
            Map reponse =  linkisConfiguration.saveFullTree(request.getClusterName(), request.getCreator(), request.getQueueName()
                , request.getFullTree(), userName);
            return new GeneralResponse<>("200", "{&SUCCESS_TO_MODIFY_STARTUP_PATAM}", reponse);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to , caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_STARTUP_PATAM}", null);
        }
    }
}
