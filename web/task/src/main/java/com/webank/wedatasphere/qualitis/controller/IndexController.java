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

package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.IndexRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.service.IndexService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationChartResponse;
import com.webank.wedatasphere.qualitis.response.IndexApplicationTodayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Index page related apis
 *
 * @author v_wblwyan
 * @date 2018-11-14
 */
@Path("/api/v1/projector/application/index")
public class IndexController {

  private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
  @Autowired
  private IndexService indexService;
  
  /**
   * Paging get applications information that was submitted today.
   * Including details of applications
   * @return
   */
  @POST
  @Path("application/today")
  @Produces(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> getTodaySubmitApplications(
      @Context HttpServletRequest httpServletRequest, PageRequest pageRequest)
      throws UnExpectedRequestException {
    PageRequest.checkRequest(pageRequest);
    String user = HttpUtils.getUserName(httpServletRequest);
    try {
      IndexApplicationTodayResponse response = indexService.getTodaySubmitApplications(user, pageRequest);
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", response);
    } catch (Exception e) {
      LOG.error("[Home overview]Failed to query API: application/today, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), -1);
    }
  }

  /**
   * Return applications status in given time.
   * Including num of successful task, failed task and not pass task
   *
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  @POST
  @Path("application/chart")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<?> getApplicationChart(IndexRequest request,
      @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
    IndexRequest.checkRequest(request);
    String user = HttpUtils.getUserName(httpServletRequest);
    request.setUser(user);
    try {
      List<IndexApplicationChartResponse> response = indexService.getApplicationChart(request);
      return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", response);
    } catch (Exception e) {
      LOG.error("[Home overview]Failed to query API: application/chart, internal error", e);
      return new GeneralResponse<>("500", e.getMessage(), -1);
    }
  }

}
