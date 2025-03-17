package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.MetricExtInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-21 14:39
 * @description
 */
@Path("api/v1/projector/metric_ext")
public class MetricExtInfoController {

    @Autowired
    private MetricExtInfoService metricExtInfoService;

    @GET
    @Path("/monitoring_capabilities")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getAllMonitoringCapabilities() {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "ok", metricExtInfoService.allMonitoringCapabilities());
    }

}
