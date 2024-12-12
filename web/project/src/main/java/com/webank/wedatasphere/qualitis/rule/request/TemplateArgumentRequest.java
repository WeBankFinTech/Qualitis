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

/**
 * @author howeye
 */
public class TemplateArgumentRequest {
    @JsonProperty("argument_id")
    private Long argumentId;
    @JsonProperty("argument_type")
    private Integer argumentType;
    @JsonProperty("argument_step")
    private Integer argumentStep;
    @JsonProperty("argument_value")
    private String argumentValue;
    @JsonProperty("argument_placeholder")
    private String argumentPlaceholder;
    @JsonProperty("argument_cluster")
    private String argumentCluster;
    @JsonProperty("argument_db")
    private String argumentDb;
    @JsonProperty("argument_table")
    private String argumentTable;
    @JsonProperty("field_multiple_choice")
    private Boolean fieldMultipleChoice;


    public TemplateArgumentRequest() {
        // Default Constructor
    }

    public TemplateArgumentRequest(Long argumentId, Integer argumentType, Integer argumentStep, String argumentValue) {
        this.argumentId = argumentId;
        this.argumentType = argumentType;
        this.argumentStep = argumentStep;
        this.argumentValue = argumentValue;
    }

    public Integer getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(Integer argumentType) {
        this.argumentType = argumentType;
    }

    public Integer getArgumentStep() {
        return argumentStep;
    }

    public void setArgumentStep(Integer argumentStep) {
        this.argumentStep = argumentStep;
    }

    public Long getArgumentId() {
        return argumentId;
    }

    public void setArgumentId(Long argumentId) {
        this.argumentId = argumentId;
    }

    public String getArgumentValue() {
        return argumentValue;
    }

    public void setArgumentValue(String argumentValue) {
        this.argumentValue = argumentValue;
    }

    public String getArgumentPlaceholder() {
        return argumentPlaceholder;
    }

    public void setArgumentPlaceholder(String argumentPlaceholder) {
        this.argumentPlaceholder = argumentPlaceholder;
    }

    public String getArgumentCluster() {
        return argumentCluster;
    }

    public void setArgumentCluster(String argumentCluster) {
        this.argumentCluster = argumentCluster;
    }

    public String getArgumentDb() {
        return argumentDb;
    }

    public void setArgumentDb(String argumentDb) {
        this.argumentDb = argumentDb;
    }

    public String getArgumentTable() {
        return argumentTable;
    }

    public void setArgumentTable(String argumentTable) {
        this.argumentTable = argumentTable;
    }

    public Boolean getFieldMultipleChoice() {
        return fieldMultipleChoice;
    }

    public void setFieldMultipleChoice(Boolean fieldMultipleChoice) {
        this.fieldMultipleChoice = fieldMultipleChoice;
    }

    public static void checkRequest(TemplateArgumentRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "template_argument_request");
        CommonChecker.checkObject(request.getArgumentStep(), "argument_step");
        CommonChecker.checkObject(request.getArgumentId(), "argument_id");
        CommonChecker.checkString(request.getArgumentValue(), "argument_value");
    }

    @Override
    public String toString() {
        return "TemplateArgumentRequest{" +
                "argumentStep=" + argumentStep +
                ", argumentId=" + argumentId +
                ", argumentValue='" + argumentValue + '\'' +
                ", argumentCluster='" + argumentCluster + '\'' +
                ", argumentDb='" + argumentDb + '\'' +
                ", argumentTable='" + argumentTable + '\'' +
                '}';
    }
}
