package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.metadata.response.DataMapResultInfo;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-13 18:20
 * @description
 */
@Path("api/v1/projector/rule/datasource")
public class RuleDataSourceController {

    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @GET
    @Path("metadata/sync")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataMapResultInfo> syncMetadata(@Context HttpServletRequest httpServletRequest) {
        String userName = HttpUtils.getUserName(httpServletRequest);
        return ruleDataSourceService.syncMetadata(userName);
    }

}
