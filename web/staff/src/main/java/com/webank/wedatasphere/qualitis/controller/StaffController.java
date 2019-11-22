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

import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.StaffService;

import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * @author v_wblwyan
 * @date 2018-11-16
 */
@Path("api/v1/projector/staff")
public class StaffController {

    private static final Logger LOG = LoggerFactory.getLogger(StaffController.class);
    @Autowired
    private StaffService staffService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getHrStaffs() {
        try {
            List<String> response = staffService.getAllStaffs();
            LOG.info("[staff]Succeed to Get the full amount of personnel data, a total of {} people", response == null ? 0 : response.size());
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", response);
        } catch (Exception e) {
            LOG.error("[staff]Failed to get the full amount of personnel data, internal error.", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

}
