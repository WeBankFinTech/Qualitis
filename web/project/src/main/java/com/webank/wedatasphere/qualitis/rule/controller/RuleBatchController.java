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

package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleBatchService;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author howeye
 */
@Path("api/v1/projector/rule/batch")
public class RuleBatchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBatchController.class);

    @Autowired
    private RuleBatchService ruleBatchService;

    @POST
    @Path("download")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> downloadRules(DownloadRuleRequest downloadRuleRequest, @Context HttpServletResponse response)
        throws UnExpectedRequestException, WriteExcelException {
        try {
            return ruleBatchService.downloadRules(downloadRuleRequest, response);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (WriteExcelException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }catch (Exception e) {
            LOGGER.error("Failed to download rules, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DOWNLOAD_RULES}", null);
        }
    }

    @POST
    @Path("upload/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GeneralResponse<?> uploadRules(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition,
                                          @PathParam("projectId") Long projectId)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        try {
            return ruleBatchService.uploadRules(fileInputStream, fileDisposition, projectId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }  catch (SemanticException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_GET_DB_AND_TABLE_FROM_SQL}");
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_PARSE_SQL}");
        } catch (Exception e) {
            LOGGER.error("Failed to upload rules, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_RULES}", null);
        }
    }

}
