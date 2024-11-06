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

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleByProject;
import com.webank.wedatasphere.qualitis.project.request.DiffVariableRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author howeye
 */
public interface RuleBatchService {

    /**
     * Download rule as excel file.
     * @param downloadRuleRequest
     * @param response
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws WriteExcelException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse downloadRules(DownloadRuleRequest downloadRuleRequest, HttpServletResponse response)
        throws UnExpectedRequestException, IOException, WriteExcelException, PermissionDeniedRequestException;

    /**
     * Upload rule from excel file.
     * @param fileInputStream
     * @param formDataContentDisposition
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse uploadRules(InputStream fileInputStream, FormDataContentDisposition formDataContentDisposition, Long projectId)
        throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Handle rule
     * @param rule
     * @param ruleInDb
     * @param ruleJsonObject
     * @param ruleTemplateJsonObject
     * @param ruleTemplateVisibilityObject
     * @param projectInDb
     * @param realRuleGroup
     * @param newRuleNames
     * @param diffVariableRequestList
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    void handleRule(Rule rule, Rule ruleInDb, String ruleJsonObject, String ruleTemplateJsonObject, String ruleTemplateVisibilityObject,
        Project projectInDb, RuleGroup realRuleGroup, List<String> newRuleNames
        , List<DiffVariableRequest> diffVariableRequestList) throws IOException, UnExpectedRequestException;

    /**
     * Get excel template from rules
     * @param rules
     * @param diffVariableRequestList
     * @return
     * @throws IOException
     */
    List<ExcelRuleByProject> getRule(Iterable<Rule> rules,
        List<DiffVariableRequest> diffVariableRequestList) throws IOException;

    /**
     * Get and save
     * @param excelRuleContent
     * @param projectInDb
     * @param newRuleNames
     * @param userName
     * @param diffVariableRequestList
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    void getAndSaveRule(List<ExcelRuleByProject> excelRuleContent, Project projectInDb, List<String> newRuleNames,
        String userName, List<DiffVariableRequest> diffVariableRequestList) throws IOException, UnExpectedRequestException;

}
