package com.webank.wedatasphere.qualitis.project.util;

import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser() {
        try {
            return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", projectService.getAllProjectByUserReal());
        } catch (Exception e) {
            LOGGER.error("Failed to get project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_PROJECT}", null);
        }
    }
}
