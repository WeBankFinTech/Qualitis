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

import com.webank.wedatasphere.qualitis.request.DeleteClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyClusterInfoRequest;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import com.webank.wedatasphere.qualitis.request.AddClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyClusterInfoRequest;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/admin/cluster_info")
public class ClusterInfoController {

    @Autowired
    private ClusterInfoService clusterInfoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterInfoController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ClusterInfo> addClusterInfo(AddClusterInfoRequest request) throws UnExpectedRequestException {
        try {
            return clusterInfoService.addClusterInfo(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add cluster_info. request: {}, caused by: {}", request, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_CLUSTER_INFO}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteClusterInfo(DeleteClusterInfoRequest request) throws UnExpectedRequestException {
        try {
            return clusterInfoService.deleteClusterInfo(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete cluster_info. cluster_info_id: {}, caused by: {}", request.getClusterInfoId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_CLUSTER_INFO}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyClusterInfo(ModifyClusterInfoRequest request) throws UnExpectedRequestException {
        try {
            return clusterInfoService.modifyClusterInfo(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify cluster_info, cluster_info_id: {}, caused by: {}", request.getClusterInfoId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_CLUSTER_INFO}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ClusterInfo>> findAllClusterInfo(PageRequest request) throws UnExpectedRequestException {
        try {
            return clusterInfoService.findAllClusterInfo(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find cluster_infos. page: {}, size: {}, caused by: {}", request.getPage(), request.getSize(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_CLUSTER_INFOS}", null);
        }
    }

}
