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

import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.DeleteMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserOfMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataAuthService;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.DeleteMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserOfMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 1.Add authorization
 * 2.Delete authorization
 * 3.Query authorization
 * @author howeye
 */
@Path("api/v1/admin/meta_data_auth")
public class MetaDataAuthController {

    @Autowired
    private MetaDataAuthService metaDataAuthService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataAuthController.class);

    /**
     * Add authorization
     * @return
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addMetaDataAuth(AddMetaDataAuthRequest addMetaDataAuthRequest) throws UnExpectedRequestException {
        try {
            return metaDataAuthService.addMetaDataAuth(addMetaDataAuthRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add auth of meta data, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to add auth of meta data, caused by: " + e.getMessage(), null);
        }
    }

    /**
     * Delete authorization
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteMetaDataAuth(DeleteMetaDataAuthRequest deleteMetaDataAuthRequest) throws UnExpectedRequestException {
        try {
            return metaDataAuthService.deleteMetaDataAuth(deleteMetaDataAuthRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete auth of meta data, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to delete auth of meta data, caused by: " + e.getMessage(), null);
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> query(QueryUserOfMetaDataAuthRequest request) {
        try {
            if (request == null) {
                request = new QueryUserOfMetaDataAuthRequest();
            }
            // Query authorization list
            List<AuthMetaData> data = metaDataAuthService.query(request);
            // Count authorization
            long total = metaDataAuthService.count(request);
            return new GeneralResponse<>("200", String.format("get authMetaData successfully for request:%s",
                    request.toString()),
                                         new GetAllResponse<>(total, data));
        } catch (Exception e) {
            LOGGER.error("Failed to get authMetaData for request:{}, caused by: {}", request.toString(),
                         e.getMessage(), e);
            return new GeneralResponse<>("500", String.format(
                "Failed to get authMetaData for request:%s, caused by: %s", request.toString(),
                e.getMessage()), null);
        }
    }

}
