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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author allenzhou
 */
public class RuleBashRequest {
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("template_functions")
    private String templateFunctions;

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;

    /**
     * Each line must contains.
     */
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;


    @JsonProperty("node_name")
    private String nodeName;
    
    @JsonProperty("proxy_user")
    private String proxyUser;

    @JsonProperty("work_flow_name")
    private String workFlowName;

    @JsonProperty("work_flow_version")
    private String workFlowVersion;

    @JsonProperty("work_flow_space")
    private String workFlowSpace;

    public static void checkRequest(RuleBashRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getTemplateFunctions(), "Bash Content");
        CommonChecker.checkObject(request.getProjectId(), "Project ID");
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public RuleBashRequest() {
        // Default do nothing.
    }

    public String getTemplateFunctions() {
        return templateFunctions;
    }

    public void setTemplateFunctions(String templateFunctions) {
        this.templateFunctions = templateFunctions;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(String workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

}
