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
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author allenzhou
 */
public class CreateAndSubmitRequest {
    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("execution_user")
    private String executionUser;

    @JsonProperty("template_function")
    private String templateFunction;

    @JsonProperty("template_function_props")
    List<PropsRequest> propsRequests;


    @JsonProperty(value = "project_name")
    private String projectName;

    @JsonProperty(value = "rule_name")
    private String ruleName;

    @JsonProperty(value = "rule_cn_name")
    private String ruleCnName;

    @JsonProperty(value = "rule_detail")
    private String ruleDetail;

    @JsonProperty(value = "execution_param")
    private String executionParam;

    @JsonProperty("dynamic_partition_bool")
    private boolean dynamicPartition;

    @JsonProperty("dynamic_partition_prefix")
    private String dynamicPartitionPrefix;

    @JsonProperty("set_flag")
    private String setFlag;

    @JsonProperty("run_date")
    private String runDate;

    @JsonProperty("job_id")
    private String jobId;

    private Boolean async;

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getTemplateFunction() {
        return templateFunction;
    }

    public void setTemplateFunction(String templateFunction) {
        this.templateFunction = templateFunction;
    }

    public List<PropsRequest> getPropsRequests() {
        return propsRequests;
    }

    public void setPropsRequests(List<PropsRequest> propsRequests) {
        this.propsRequests = propsRequests;
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

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
    }

    public boolean getDynamicPartition() {
        return dynamicPartition;
    }

    public void setDynamicPartition(boolean dynamicPartition) {
        this.dynamicPartition = dynamicPartition;
    }

    public String getDynamicPartitionPrefix() {
        return dynamicPartitionPrefix;
    }

    public void setDynamicPartitionPrefix(String dynamicPartitionPrefix) {
        this.dynamicPartitionPrefix = dynamicPartitionPrefix;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public static void checkRequest(CreateAndSubmitRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getCreateUser(), "Create user");
        CommonChecker.checkString(request.getExecutionUser(), "Execution user");
        CommonChecker.checkString(request.getTemplateFunction(), "Template function");

        if (StringUtils.isBlank(request.getProjectName()) || StringUtils.isBlank(request.getRuleName())) {
            throw new UnExpectedRequestException("Expect function must with project name and rule name");
        }

    }

    @Override
    public String toString() {
        return "CreateAndSubmitRequest{" +
                "createUser='" + createUser + '\'' +
                ", executionUser='" + executionUser + '\'' +
                ", templateFunction='" + templateFunction + '\'' +
                ", propsRequests=" + propsRequests +
                ", projectName='" + projectName + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleCnName='" + ruleCnName + '\'' +
                ", ruleDetail='" + ruleDetail + '\'' +
                ", executionParam='" + executionParam + '\'' +
                ", dynamicPartition=" + dynamicPartition +
                ", dynamicPartitionPrefix='" + dynamicPartitionPrefix + '\'' +
                ", setFlag='" + setFlag + '\'' +
                ", runDate='" + runDate + '\'' +
                ", jobId='" + jobId + '\'' +
                ", async=" + async +
                '}';
    }
}
