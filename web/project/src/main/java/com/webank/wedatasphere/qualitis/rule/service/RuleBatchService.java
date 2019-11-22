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

import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelCustomRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelMultiTemplateRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelCustomRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelMultiTemplateRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
    GeneralResponse<?> downloadRules(DownloadRuleRequest downloadRuleRequest, HttpServletResponse response) throws UnExpectedRequestException, IOException, WriteExcelException;

    /**
     * Upload rule from excel file
     * @param fileInputStream
     * @param formDataContentDisposition
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws MetaDataAcquireFailedException
     * @throws ParseException
     * @throws SemanticException
     */
    GeneralResponse<?> uploadRules(InputStream fileInputStream, FormDataContentDisposition formDataContentDisposition, Long projectId) throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException;

    /**
     * Get excel template from rules
     * @param rules
     * @return
     */
    List<ExcelTemplateRule> getTemplateRule(Iterable<Rule> rules);

    /**
     * Get multi-table rule template from rules
     * @param rules
     * @return
     */
    List<ExcelMultiTemplateRule> getMultiTemplateRule(Iterable<Rule> rules);

    /**
     * Get cutsom rule template from rule
     * @param rules
     * @return
     */
    List<ExcelCustomRule> getCustomRule(Iterable<Rule> rules);

    /**
     * Get and save rules
     * @param rulePartitionedByRuleName
     * @param customRulePartitionedByRuleName
     * @param multiRulePartitionedByRuleName
     * @param project
     * @param username
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws ParseException
     * @throws SemanticException
     */
    void getAndSaveRule(Map<String, List<ExcelTemplateRule>> rulePartitionedByRuleName, Map<String, List<ExcelCustomRule>> customRulePartitionedByRuleName,
                        Map<String, List<ExcelMultiTemplateRule>> multiRulePartitionedByRuleName, Project project, String username) throws UnExpectedRequestException, MetaDataAcquireFailedException, SemanticException, ParseException;
}
