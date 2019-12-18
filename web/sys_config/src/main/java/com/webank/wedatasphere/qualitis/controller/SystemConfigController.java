package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.ModifySystemConfigRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.SystemConfigService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author howeye
 */
@Path("api/v1/admin/system_config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigController.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifySystemConfig(ModifySystemConfigRequest request) throws UnExpectedRequestException {
        try {
            return systemConfigService.modifySystemConfig(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("{&FAILED_TO_MODIFY_SYSTEM_CONFIG} key_name: {}, value: {}, caused by: {}", request.getKeyName(), request.getValue(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_SYSTEM_CONFIG}", null);
        }
    }

    @GET
    @Path("{key_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> findByKeyName(@PathParam("key_name")String keyName) throws UnExpectedRequestException {
        try {
            return systemConfigService.findByKeyName(keyName);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("{&FAILED_TO_FIND_SYSTEM_CONFIG}. key: {}, caused by: {}", keyName, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_SYSTEM_CONFIG}", null);
        }
    }


}