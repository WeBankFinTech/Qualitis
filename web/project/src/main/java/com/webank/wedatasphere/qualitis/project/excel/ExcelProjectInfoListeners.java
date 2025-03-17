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

package com.webank.wedatasphere.qualitis.project.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class ExcelProjectInfoListeners {

    private List<ExcelProject> excelProjectContent = new ArrayList<>();
    private List<ExcelRuleByProject> excelRuleContent = new ArrayList<>();
    private List<ExcelGroupByProject> excelGroupByProjects = new ArrayList<>();

    private List<ExcelRuleUdf> excelRuleUdfContent = new ArrayList<>();
    private List<ExcelRuleMetric> excelMetricContent = new ArrayList<>();
    private List<ExcelTemplate> excelTemplateContent = new ArrayList<>();
    private List<ExcelDatasourceEnv> excelDatasourceEnvContent = new ArrayList<>();
    private List<ExcelStandardValue> excelStandardValueContent = new ArrayList<>();
    private List<ExcelPublishScheduled> excelPublishScheduledContent = new ArrayList<>();
    private List<ExcelRelationScheduled> excelRelationScheduledContent = new ArrayList<>();
    private List<ExcelWorkflowBusiness> excelWorkflowBusinessContent = new ArrayList<>();

    private List<ExcelExecutionParametersByProject> excelExecutionParametersContent = new ArrayList<>();

    public void setExcelProjectContent(List<ExcelProject> excelProjectContent) {
        this.excelProjectContent = excelProjectContent;
    }

    public void setExcelRuleContent(List<ExcelRuleByProject> excelRuleContent) {
        this.excelRuleContent = excelRuleContent;
    }

    public void setExcelGroupByProjects(List<ExcelGroupByProject> excelGroupByProjects) {
        this.excelGroupByProjects = excelGroupByProjects;
    }

    public void setExcelRuleUdfContent(List<ExcelRuleUdf> excelRuleUdfContent) {
        this.excelRuleUdfContent = excelRuleUdfContent;
    }

    public void setExcelMetricContent(List<ExcelRuleMetric> excelMetricContent) {
        this.excelMetricContent = excelMetricContent;
    }

    public void setExcelTemplateContent(List<ExcelTemplate> excelTemplateContent) {
        this.excelTemplateContent = excelTemplateContent;
    }

    public void setExcelDatasourceEnvContent(List<ExcelDatasourceEnv> excelDatasourceEnvContent) {
        this.excelDatasourceEnvContent = excelDatasourceEnvContent;
    }

    public void setExcelStandardValueContent(List<ExcelStandardValue> excelStandardValueContent) {
        this.excelStandardValueContent = excelStandardValueContent;
    }

    public void setExcelPublishScheduledContent(List<ExcelPublishScheduled> excelPublishScheduledContent) {
        this.excelPublishScheduledContent = excelPublishScheduledContent;
    }

    public void setExcelRelationScheduledContent(List<ExcelRelationScheduled> excelRelationScheduledContent) {
        this.excelRelationScheduledContent = excelRelationScheduledContent;
    }

    public void setExcelWorkflowBusinessContent(List<ExcelWorkflowBusiness> excelWorkflowBusinessContent) {
        this.excelWorkflowBusinessContent = excelWorkflowBusinessContent;
    }

    public void setExcelExecutionParametersContent(List<ExcelExecutionParametersByProject> excelExecutionParametersContent) {
        this.excelExecutionParametersContent = excelExecutionParametersContent;
    }

    public List<ExcelProject> getExcelProjectContent() {
        return excelProjectContent;
    }

    public List<ExcelRuleMetric> getExcelMetricContent() {
        return excelMetricContent;
    }

    public List<ExcelExecutionParametersByProject> getExcelExecutionParametersContent() {
        return excelExecutionParametersContent;
    }

    public List<ExcelGroupByProject> getExcelGroupByProjects() {
        return excelGroupByProjects;
    }

    public List<ExcelRuleByProject> getExcelRuleContent() {
        return excelRuleContent;
    }

    public List<ExcelTemplate> getExcelTemplateContent() {
        return excelTemplateContent;
    }

    public List<ExcelStandardValue> getExcelStandardVauleContent() {
        return excelStandardValueContent;
    }

    public List<ExcelRuleUdf> getExcelRuleUdfContent() {
        return excelRuleUdfContent;
    }

    public List<ExcelDatasourceEnv> getExcelDatasourceEnvContent() {
        return excelDatasourceEnvContent;
    }

    public List<ExcelPublishScheduled> getExcelPublishScheduledContent() {
        return excelPublishScheduledContent;
    }

    public List<ExcelRelationScheduled> getExcelRelationScheduledContent() {
        return excelRelationScheduledContent;
    }

    public List<ExcelStandardValue> getExcelStandardValueContent() {
        return excelStandardValueContent;
    }

    public List<ExcelWorkflowBusiness> getExcelWorkflowBusinessContent() {
        return excelWorkflowBusinessContent;
    }

    @Override
    public String toString() {
        return "ExcelProjectListener{" +
                "excelProjectContent=" + excelProjectContent +
                ", excelRuleContent=" + excelRuleContent +
                ", excelGroupByProjects=" + excelGroupByProjects +
                ", excelRuleUdfContent=" + excelRuleUdfContent +
                ", excelMetricContent=" + excelMetricContent +
                ", excelTemplateContent=" + excelTemplateContent +
                ", excelDatasourceEnvContent=" + excelDatasourceEnvContent +
                ", excelStandardValueContent=" + excelStandardValueContent +
                ", excelPublishScheduledContent=" + excelPublishScheduledContent +
                ", excelRelationScheduledContent=" + excelRelationScheduledContent +
                ", excelWorkflowBusinessContent=" + excelWorkflowBusinessContent +
                ", excelExecutionParametersContent=" + excelExecutionParametersContent +
                '}';
    }
}
