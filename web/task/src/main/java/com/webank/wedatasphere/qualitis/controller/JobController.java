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

import com.webank.wedatasphere.qualitis.service.JobService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("/api/v1/projector/job")
public class JobController {

    @Autowired
    private JobService jobService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);

    @GET
    @Path("log/{clusterName}/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getJobLog(@PathParam("clusterName")String clusterName, @PathParam("taskId")Long taskId) throws UnExpectedRequestException {
        try {
            return jobService.getTaskLog(taskId, clusterName);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find task log. task_id: {}, cluster_name: {}, caused by: {}", taskId, clusterName, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_TASK_LOG}", null);
        }
    }

}
