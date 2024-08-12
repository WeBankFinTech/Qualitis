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

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class ExcelProjectListener extends AnalysisEventListener {

    private List<ExcelProject> excelProjectContent = new ArrayList<>();
    private List<ExcelRuleByProject> excelRuleContent = new ArrayList<>();
    private List<ExcelGroupByProject> excelGroupByProjects = new ArrayList<>();

    private List<ExcelRuleUdf> excelRuleUdfContent = new ArrayList<>();
    private List<ExcelRuleMetric> excelMetricContent = new ArrayList<>();
    private List<ExcelTemplate> excelTemplateContent = new ArrayList<>();
    private List<ExcelDatasourceEnv> excelDatasourceEnvContent = new ArrayList<>();
    private List<ExcelStandardValue> excelStandardValueContent = new ArrayList<>();

    private List<ExcelExecutionParametersByProject> excelExecutionParametersContent = new ArrayList<>();

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.PROJECT_NAME)) {
            excelProjectContent.add((ExcelProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
            excelMetricContent.add((ExcelRuleMetric) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.EXECUTION_PARAMETERS_NAME)) {
            excelExecutionParametersContent.add((ExcelExecutionParametersByProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_NAME)) {
            excelRuleContent.add((ExcelRuleByProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TABLE_GROUP)) {
            excelGroupByProjects.add((ExcelGroupByProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_UDF)) {
            excelRuleUdfContent.add((ExcelRuleUdf) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.DATASOURCE_ENV)) {
            excelDatasourceEnvContent.add((ExcelDatasourceEnv) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.STANDARD_VAULE)) {
            excelStandardValueContent.add((ExcelStandardValue) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_TEMPLATE_NAME)) {
            excelTemplateContent.add((ExcelTemplate) object);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // no need to implement
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

}
