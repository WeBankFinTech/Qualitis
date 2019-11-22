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
import com.webank.wedatasphere.qualitis.rule.excel.ExcelCustomRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelMultiTemplateRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * @author howeye
 */
public class ExcelProjectListener extends AnalysisEventListener {

    private List<ExcelProject> excelProjectContent = new ArrayList<>();
    private Map<String, Map<String, List<ExcelTemplateRule>>> excelRuleContent = new HashMap<>(4);
    private Map<String, Map<String, List<ExcelCustomRule>>> excelCustomRuleContent = new HashMap<>(4);
    private Map<String, Map<String, List<ExcelMultiTemplateRule>>> excelMultiRuleContent = new HashMap<>(4);

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
            ExcelTemplateRuleByProject excelTemplateRuleByProject = (ExcelTemplateRuleByProject) object;
            String projectName = excelTemplateRuleByProject.getProjectName();
            String ruleName = excelTemplateRuleByProject.getRuleName();
            if (!excelRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelTemplateRule>> tmpMap = new HashMap<>(8);
                List<ExcelTemplateRule> tmpList = new ArrayList<>();
                ExcelTemplateRule tmp = new ExcelTemplateRule();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelTemplateRule>> tmpMap = excelRuleContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelTemplateRule> tmpList = new ArrayList<>();
                    ExcelTemplateRule tmp = new ExcelTemplateRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelTemplateRule> tmpList = tmpMap.get(ruleName);
                    ExcelTemplateRule tmp = new ExcelTemplateRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.PROJECT_NAME)) {
            excelProjectContent.add((ExcelProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
            ExcelCustomRuleByProject excelCustomRuleByProject = (ExcelCustomRuleByProject) object;
            String projectName = excelCustomRuleByProject.getProjectName();
            String ruleName = excelCustomRuleByProject.getRuleName();
            if (!excelCustomRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelCustomRule>> tmpMap = new HashMap<>(8);
                List<ExcelCustomRule> tmpList = new ArrayList<>();
                ExcelCustomRule tmp = new ExcelCustomRule();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelCustomRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelCustomRule>> tmpMap = excelCustomRuleContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelCustomRule> tmpList = new ArrayList<>();
                    ExcelCustomRule tmp = new ExcelCustomRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelCustomRule> tmpList = tmpMap.get(ruleName);
                    ExcelCustomRule tmp = new ExcelCustomRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
            ExcelMultiTemplateRuleByProject excelMultiTemplateRuleByProject = (ExcelMultiTemplateRuleByProject) object;
            String projectName = excelMultiTemplateRuleByProject.getProjectName();
            String ruleName = excelMultiTemplateRuleByProject.getRuleName();
            if (!excelMultiRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelMultiTemplateRule>> tmpMap = new HashMap<>(8);
                List<ExcelMultiTemplateRule> tmpList = new ArrayList<>();
                ExcelMultiTemplateRule tmp = new ExcelMultiTemplateRule();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelMultiRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelMultiTemplateRule>> tmpMap = excelMultiRuleContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelMultiTemplateRule> tmpList = new ArrayList<>();
                    ExcelMultiTemplateRule tmp = new ExcelMultiTemplateRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelMultiTemplateRule> tmpList = tmpMap.get(ruleName);
                    ExcelMultiTemplateRule tmp = new ExcelMultiTemplateRule();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // no need to implement
    }

    public List<ExcelProject> getExcelProjectContent() {
        return excelProjectContent;
    }

    public Map<String, Map<String, List<ExcelTemplateRule>>> getExcelRuleContent() {
        return excelRuleContent;
    }

    public Map<String, Map<String, List<ExcelCustomRule>>> getExcelCustomRuleContent() {
        return excelCustomRuleContent;
    }

    public Map<String, Map<String, List<ExcelMultiTemplateRule>>> getExcelMultiRuleContent() {
        return excelMultiRuleContent;
    }
}
