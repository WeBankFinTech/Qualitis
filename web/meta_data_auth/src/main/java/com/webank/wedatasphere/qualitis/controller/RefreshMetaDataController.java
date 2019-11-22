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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.RefreshTableByClusterAndDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.RefreshMetaDataService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.RefreshTableByClusterAndDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.RefreshMetaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/admin/meta_data/refresh")
public class RefreshMetaDataController {

    @Autowired
    private RefreshMetaDataService refreshMetaDataService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshMetaDataController.class);

    /**
     * Refresh cluster
     * @return
     */
    @POST
    @Path("cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> refreshCluster() throws UnExpectedRequestException {
        try {
            return refreshMetaDataService.refreshCluster();
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to refresh cluster, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to refresh cluster, caused by: " + e.getMessage(), null);
        }
    }

    /**
     * Refresh Database for cluster
     * @return
     */
    @POST
    @Path("db")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> refreshDbByCluster(RefreshDbByClusterRequest request) throws UnExpectedRequestException {
        try {
            return refreshMetaDataService.refreshDbByCluster(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to refresh db by cluster, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to refresh db by cluster, caused by: " + e.getMessage(), null);
        }
    }

    /**
     * Refresh Table nad filed for cluster and database
     * @return
     */
    @POST
    @Path("table_column")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> refreshTableByClusterAndDb(RefreshTableByClusterAndDbRequest request) throws UnExpectedRequestException {
        try {
            return refreshMetaDataService.refreshTableByClusterAndDb(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to refresh table by cluster and db, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to refresh table by cluster and db, caused by: " + e.getMessage(), null);
        }
    }

}
