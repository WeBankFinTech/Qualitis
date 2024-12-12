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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author allenzhou
 */
public class CopyRuleWithDatasourceRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_id_list")
    private List<Long> ruleIdList;

    private List<DataSourceRequest> datasource;

    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("work_flow_version")
    private String workFlowVersion;
    @JsonProperty("work_flow_name")
    private String workFlowName;

    public CopyRuleWithDatasourceRequest() {
        // Default Constructor
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public List<Long> getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List<Long> ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public List<DataSourceRequest> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceRequest> datasource) {
        this.datasource = datasource;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(String workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public static void checkRequest(CopyRuleWithDatasourceRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");

        if (request.getProjectId() == null && CollectionUtils.isEmpty(request.getRuleIdList())) {
            throw new UnExpectedRequestException("Source rule info" + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
