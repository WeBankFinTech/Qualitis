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

package com.webank.wedatasphere.qualitis.project.controller;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.util.LdapUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author allenzhou
 */
@Path("outer/api/v1/aomp")
public class OuterProjectBatchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterProjectBatchController.class);

    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LdapUtil ldapUtil;

    @POST
    @Path("upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GeneralResponse uploadProjectFromAomp(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition
        , @FormDataParam("username") String userName, @FormDataParam("password") String password) throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, JSONException, MetaDataAcquireFailedException {
        int fileSize = fileInputStream.available();
        LOGGER.info("Upload zip file size: {} MB", fileSize / (1024 * 1024));
        int maxFileSize = 10 * 1024 * 1024;
        if(fileSize > maxFileSize) {
            throw new UnExpectedRequestException("File size exceeds limit: 10 MB");
        }

        // Ldap Proxy check with user name and password.
        if (! ldapUtil.loginByLdap(userName, password)) {
            String msg = "Failed to login by ldap, please check user name and password.";
            LOGGER.error(msg);
            return new GeneralResponse<>("401", "{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + msg, null);
        }

        return projectBatchService.uploadProjectFromAomp(fileInputStream, fileDisposition, userName);
    }
}
