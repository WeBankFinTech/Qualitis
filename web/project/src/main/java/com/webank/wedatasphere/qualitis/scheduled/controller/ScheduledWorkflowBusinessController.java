package com.webank.wedatasphere.qualitis.scheduled.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
//import com.webank.wedatasphere.qualitis.response.GeneralResponse;
//import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
//import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledWorkflowBusinessRequest;
//import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledWorkflowBusinessResponse;
//import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledWorkflowBusinessService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
import com.webank.wedatasphere.qualitis.scheduled.response.ScheduledWorkflowBusinessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.List;
//import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 10:18
 * @description
 */
@Path("api/v1/projector/scheduled/workflow_business")
public class ScheduledWorkflowBusinessController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledWorkflowBusinessController.class);

//    @Autowired
//    private ScheduledWorkflowBusinessService scheduledWorkflowBusinessService;
//
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<String> add(ScheduledWorkflowBusinessRequest request) throws UnExpectedRequestException {
//        if (request == null) {
//            throw new UnExpectedRequestException("request parameters must be not null!");
//        }
//        scheduledWorkflowBusinessService.add(request);
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "ok", null);
//    }
//
//    @PUT
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<String> modify(ScheduledWorkflowBusinessRequest request) throws UnExpectedRequestException {
//        if (request == null) {
//            throw new UnExpectedRequestException("request parameters must be not null!");
//        }
//        CommonChecker.checkObject(request.getId(), "id");
//        scheduledWorkflowBusinessService.modify(request);
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "ok", null);
//    }
//
//    @DELETE
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<String> delete(@PathParam("id") Long id) throws UnExpectedRequestException {
//        scheduledWorkflowBusinessService.delete(id);
//        return new GeneralResponse<>(ResponseStatusConstants.OK, "ok", null);
//    }
//
    @GET
    @Path("list/{project_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<ScheduledWorkflowBusinessResponse>> list(@PathParam("project_id") Long projectId) throws UnExpectedRequestException {

        return new GeneralResponse<>(ResponseStatusConstants.OK, "ok", Collections.emptyList());
    }

}
