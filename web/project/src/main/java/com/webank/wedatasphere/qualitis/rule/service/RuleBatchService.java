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

package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelCustomRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateFileRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
public interface RuleBatchService {

    /**
     * Download rule as excel file
     * @param downloadRuleRequest
     * @param response
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws WriteExcelException
     */
    GeneralResponse<?> downloadRules(DownloadRuleRequest downloadRuleRequest, HttpServletResponse response)
        throws UnExpectedRequestException, IOException, WriteExcelException, PermissionDeniedRequestException;

    /**
     * Upload rule from excel file
     * @param fileInputStream
     * @param formDataContentDisposition
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws MetaDataAcquireFailedException
     * @throws SemanticException
     * @throws ParseException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    GeneralResponse<?> uploadRules(InputStream fileInputStream, FormDataContentDisposition formDataContentDisposition, Long projectId)
        throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException;

    /**
     * Get excel template from rules
     * @param rules
     * @param localeStr
     * @return
     */
    List<ExcelTemplateRuleByProject> getTemplateRule(Iterable<Rule> rules, String localeStr);

    /**
     * Get multi-table rule template from rules
     * @param rules
     * @param localeStr
     * @return
     */
    List<ExcelMultiTemplateRuleByProject> getMultiTemplateRule(Iterable<Rule> rules, String localeStr);

    /**
     * Fil rule
     * @param rules
     * @param localeStr
     * @return
     */
    List<ExcelTemplateFileRuleByProject> getFileRule(Iterable<Rule> rules, String localeStr);

    /**
     * Get cutsom rule template from rule
     * @param rules
     * @param localeStr
     * @return
     */
    List<ExcelCustomRuleByProject> getCustomRule(Iterable<Rule> rules, String localeStr);

    /**
     * Upload rule from excel file for outer
     * @param fileInputStream
     * @param fileName
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws MetaDataAcquireFailedException
     * @throws SemanticException
     * @throws ParseException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<?> outerUploadRules(InputStream fileInputStream, String fileName, String userName)
        throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException;

    /**
     * Four types rule.
     * @param rulePartitionedByRuleName
     * @param customRulePartitionedByRuleName
     * @param multiRulePartitionedByRuleName
     * @param fileRulePartitionedByRuleName
     * @param project
     * @param userName
     * @param aomp
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws SemanticException
     * @throws ParseException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    void getAndSaveRule(Map<String, List<ExcelTemplateRuleByProject>> rulePartitionedByRuleName,
        Map<String, List<ExcelCustomRuleByProject>> customRulePartitionedByRuleName,
        Map<String, List<ExcelMultiTemplateRuleByProject>> multiRulePartitionedByRuleName,
        Map<String, List<ExcelTemplateFileRuleByProject>> fileRulePartitionedByRuleName,
        Project project, String userName, boolean aomp)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, SemanticException, ParseException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException;
}
