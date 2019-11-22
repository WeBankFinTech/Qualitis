/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.query.controller;

import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;

import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
@Path("api/v1/projector/query")
public class RuleQueryController {

  private static final Logger LOG = LoggerFactory.getLogger(RuleQueryController.class);
  @Autowired
  RuleQueryService ruleQueryService;

  @GET
  @Path("conditions")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> conditions(@Context HttpServletRequest request) {
    // Get login user
    String user = HttpUtils.getUserName(request);
    try {
      Map<String, Object> results = ruleQueryService.conditions(user);
      LOG.info("[My DataSource] Succeed to the query initial conditions, the result:{}", results);
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
    } catch (Exception e) {
      LOG.error("[My DataSource] Failed to the query initial conditions, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), null);
    }
  }

  @GET
  @Path("init")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> init(@Context HttpServletRequest request) {
    // Get login user
    String user = HttpUtils.getUserName(request);
    try {
      List<RuleQueryProject> results = ruleQueryService.init(user);
      LOG.info("[My DataSource] Succeed to query initial results. The number of results:{}", results == null ? 0 : results.size());
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
    } catch (Exception e) {
      LOG.error("[My DataSource] Failed to query initial results, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), null);
    }
  }

  @POST
  @Path("query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> query(RuleQueryRequest param, @Context HttpServletRequest request) {
    if (param == null) {
      param = new RuleQueryRequest();
    }
    // Get login user
    param.setUser(HttpUtils.getUserName(request));
    try {
      List<RuleQueryProject> results = ruleQueryService.query(param);
      LOG.info("[My DataSource] Query successfully. The number of results:{}", results == null ? 0 : results.size());
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
    } catch (Exception e) {
      LOG.error("[My DataSource] Query failed, internal error.", e);
      return new GeneralResponse<>("500", e.getMessage(), null);
    }
  }
}
