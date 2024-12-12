package com.webank.wedatasphere.qualitis.project.util;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.project.request.ProjectTypeRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/project/util")
public class ProjectUtilController {

    @Autowired
    private ProjectService projectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUtilController.class);

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(ProjectTypeRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_ALL_PROJECT_SUCCESSFULLY}", projectService.getAllProjectByUserReal(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ALL_PROJECT}", null);
        }
    }
}
