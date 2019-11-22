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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class ExcelRuleListener extends AnalysisEventListener<Object> {

    private Map<String, List<ExcelTemplateRule>> templateExcelContent = new HashMap<>(32);
    private Map<String, List<ExcelCustomRule>> customExcelContent = new HashMap<>(32);
    private Map<String, List<ExcelMultiTemplateRule>> multiTemplateExcelContent = new HashMap<>(32);

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
            ExcelTemplateRule excelTemplateRule = (ExcelTemplateRule) object;
            String key = excelTemplateRule.getRuleName();
            if (templateExcelContent.containsKey(key)) {
                templateExcelContent.get(key).add(excelTemplateRule);
            } else {
                List<ExcelTemplateRule> tmp = new ArrayList<>();
                tmp.add(excelTemplateRule);
                templateExcelContent.put(key, tmp);
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
            ExcelCustomRule excelCustomRule = (ExcelCustomRule) object;
            String key = excelCustomRule.getRuleName();
            if (customExcelContent.containsKey(key)) {
                customExcelContent.get(key).add(excelCustomRule);
            } else {
                List<ExcelCustomRule> tmp = new ArrayList<>();
                tmp.add(excelCustomRule);
                customExcelContent.put(key, tmp);
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
            ExcelMultiTemplateRule excelMultiTemplateRule = (ExcelMultiTemplateRule) object;
            String key = excelMultiTemplateRule.getRuleName();
            if (multiTemplateExcelContent.containsKey(key)) {
                multiTemplateExcelContent.get(key).add(excelMultiTemplateRule);
            } else {
                List<ExcelMultiTemplateRule> tmp = new ArrayList<>();
                tmp.add(excelMultiTemplateRule);
                multiTemplateExcelContent.put(key, tmp);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // no need to implement
    }

    public Map<String, List<ExcelTemplateRule>> getTemplateExcelContent() {
        return templateExcelContent;
    }

    public Map<String, List<ExcelCustomRule>> getCustomExcelContent() {
        return customExcelContent;
    }

    public Map<String, List<ExcelMultiTemplateRule>> getMultiTemplateExcelContent() {
        return multiTemplateExcelContent;
    }
}
