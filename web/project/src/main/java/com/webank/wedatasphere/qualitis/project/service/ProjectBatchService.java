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

package com.webank.wedatasphere.qualitis.project.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import javax.management.relation.RoleNotFoundException;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author howeye
 */
public interface ProjectBatchService {

    /**
     * Add project by project excel
     * @param fileInputStream
     * @param fileDisposition
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     * @throws ParseException
     * @throws SemanticException
     */
    GeneralResponse<?> uploadProjects(InputStream fileInputStream, FormDataContentDisposition fileDisposition)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, SemanticException, ParseException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Download project excel
     * @param request
     * @param response
     * @return
     * @throws UnExpectedRequestException
     * @throws WriteExcelException
     * @throws IOException
     */
    GeneralResponse<?> downloadProjects(DownloadProjectRequest request, HttpServletResponse response)
        throws UnExpectedRequestException, WriteExcelException, IOException, PermissionDeniedRequestException;

    /**
     * Update project by project excel for outer.
     * @param fileInputStream
     * @param fileName
     * @param userName
     * @return
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> outerUploadProjects(InputStream fileInputStream, String fileName, String userName)
        throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * For aomp deploy.
     * @param fileInputStream
     * @param fileName
     * @param userName
     * @return
     */
    GeneralResponse<?> uploadProjectRulesAndRuleMetrics(InputStream fileInputStream, FormDataContentDisposition fileName, String userName);


}
