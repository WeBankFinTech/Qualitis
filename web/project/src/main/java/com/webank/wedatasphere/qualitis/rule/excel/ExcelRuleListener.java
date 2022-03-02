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

package com.webank.wedatasphere.qualitis.rule.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;

import com.webank.wedatasphere.qualitis.project.excel.ExcelCustomRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateFileRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateRuleByProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class ExcelRuleListener extends AnalysisEventListener<Object> {

    private Map<String, List<ExcelTemplateRuleByProject>> templateExcelContent = new HashMap<>(32);
    private Map<String, List<ExcelCustomRuleByProject>> customExcelContent = new HashMap<>(32);
    private Map<String, List<ExcelMultiTemplateRuleByProject>> multiTemplateExcelContent = new HashMap<>(32);
    private Map<String, List<ExcelTemplateFileRuleByProject>> templateFileExcelContent = new HashMap<>(32);

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
            ExcelTemplateRuleByProject excelTemplateRuleByProject = (ExcelTemplateRuleByProject) object;
            String key = excelTemplateRuleByProject.getRuleName();
            if (templateExcelContent.containsKey(key)) {
                templateExcelContent.get(key).add(excelTemplateRuleByProject);
            } else {
                List<ExcelTemplateRuleByProject> tmp = new ArrayList<>();
                tmp.add(excelTemplateRuleByProject);
                templateExcelContent.put(key, tmp);
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
            ExcelCustomRuleByProject excelCustomRule = (ExcelCustomRuleByProject) object;
            String key = excelCustomRule.getRuleName();
            if (customExcelContent.containsKey(key)) {
                customExcelContent.get(key).add(excelCustomRule);
            } else {
                List<ExcelCustomRuleByProject> tmp = new ArrayList<>();
                tmp.add(excelCustomRule);
                customExcelContent.put(key, tmp);
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
            ExcelMultiTemplateRuleByProject excelMultiTemplateRule = (ExcelMultiTemplateRuleByProject) object;
            String key = excelMultiTemplateRule.getRuleName();
            if (multiTemplateExcelContent.containsKey(key)) {
                multiTemplateExcelContent.get(key).add(excelMultiTemplateRule);
            } else {
                List<ExcelMultiTemplateRuleByProject> tmp = new ArrayList<>();
                tmp.add(excelMultiTemplateRule);
                multiTemplateExcelContent.put(key, tmp);
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_FILE_RULE_NAME)) {
            ExcelTemplateFileRuleByProject excelTemplateFileRule = (ExcelTemplateFileRuleByProject) object;
            String key = excelTemplateFileRule.getRuleName();
            if (templateFileExcelContent.containsKey(key)) {
                templateFileExcelContent.get(key).add(excelTemplateFileRule);
            } else {
                List<ExcelTemplateFileRuleByProject> tmp = new ArrayList<>();
                tmp.add(excelTemplateFileRule);
                templateFileExcelContent.put(key, tmp);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // no need to implement
    }

    public Map<String, List<ExcelTemplateRuleByProject>> getTemplateExcelContent() {
        return templateExcelContent;
    }

    public Map<String, List<ExcelCustomRuleByProject>> getCustomExcelContent() {
        return customExcelContent;
    }

    public Map<String, List<ExcelMultiTemplateRuleByProject>> getMultiTemplateExcelContent() {
        return multiTemplateExcelContent;
    }

    public Map<String, List<ExcelTemplateFileRuleByProject>> getTemplateFileExcelContent() {
        return templateFileExcelContent;
    }
}
