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
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateFileRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * @author howeye
 */
public class ExcelProjectListener extends AnalysisEventListener {

    private List<ExcelProject> excelProjectContent = new ArrayList<>();
    private List<ExcelRuleMetric> excelMetricContent = new ArrayList<>();
    private Map<String, Map<String, List<ExcelTemplateRuleByProject>>> excelRuleContent = new HashMap<>(4);
    private Map<String, Map<String, List<ExcelCustomRuleByProject>>> excelCustomRuleContent = new HashMap<>(4);
    private Map<String, Map<String, List<ExcelMultiTemplateRuleByProject>>> excelMultiRuleContent = new HashMap<>(4);
    private Map<String, Map<String, List<ExcelTemplateFileRuleByProject>>> templateFileExcelContent = new HashMap<>(4);

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
            ExcelTemplateRuleByProject excelTemplateRuleByProject = (ExcelTemplateRuleByProject) object;
            String projectName = excelTemplateRuleByProject.getProjectName();
            String ruleName = excelTemplateRuleByProject.getRuleName();
            if (! excelRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelTemplateRuleByProject>> tmpMap = new HashMap<>(8);
                List<ExcelTemplateRuleByProject> tmpList = new ArrayList<>();
                ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelTemplateRuleByProject>> tmpMap = excelRuleContent.get(projectName);
                if (! tmpMap.containsKey(ruleName)) {
                    List<ExcelTemplateRuleByProject> tmpList = new ArrayList<>();
                    ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelTemplateRuleByProject> tmpList = tmpMap.get(ruleName);
                    ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.PROJECT_NAME)) {
            excelProjectContent.add((ExcelProject) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
            excelMetricContent.add((ExcelRuleMetric) object);
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
            ExcelCustomRuleByProject excelCustomRuleByProject = (ExcelCustomRuleByProject) object;
            String projectName = excelCustomRuleByProject.getProjectName();
            String ruleName = excelCustomRuleByProject.getRuleName();
            if (! excelCustomRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelCustomRuleByProject>> tmpMap = new HashMap<>(8);
                List<ExcelCustomRuleByProject> tmpList = new ArrayList<>();
                ExcelCustomRuleByProject tmp = new ExcelCustomRuleByProject();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelCustomRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelCustomRuleByProject>> tmpMap = excelCustomRuleContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelCustomRuleByProject> tmpList = new ArrayList<>();
                    ExcelCustomRuleByProject tmp = new ExcelCustomRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelCustomRuleByProject> tmpList = tmpMap.get(ruleName);
                    ExcelCustomRuleByProject tmp = new ExcelCustomRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
            ExcelMultiTemplateRuleByProject excelMultiTemplateRuleByProject = (ExcelMultiTemplateRuleByProject) object;
            String projectName = excelMultiTemplateRuleByProject.getProjectName();
            String ruleName = excelMultiTemplateRuleByProject.getRuleName();
            if (! excelMultiRuleContent.containsKey(projectName)) {
                Map<String, List<ExcelMultiTemplateRuleByProject>> tmpMap = new HashMap<>(8);
                List<ExcelMultiTemplateRuleByProject> tmpList = new ArrayList<>();
                ExcelMultiTemplateRuleByProject tmp = new ExcelMultiTemplateRuleByProject();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                excelMultiRuleContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelMultiTemplateRuleByProject>> tmpMap = excelMultiRuleContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelMultiTemplateRuleByProject> tmpList = new ArrayList<>();
                    ExcelMultiTemplateRuleByProject tmp = new ExcelMultiTemplateRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelMultiTemplateRuleByProject> tmpList = tmpMap.get(ruleName);
                    ExcelMultiTemplateRuleByProject tmp = new ExcelMultiTemplateRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                }
            }
        } else if (context.getCurrentSheet().getSheetName().equals(ExcelSheetName.TEMPLATE_FILE_RULE_NAME)) {
            ExcelTemplateFileRuleByProject excelTemplateFileRuleByProject = (ExcelTemplateFileRuleByProject) object;
            String projectName = excelTemplateFileRuleByProject.getProjectName();
            String ruleName = excelTemplateFileRuleByProject.getRuleName();
            if (! templateFileExcelContent.containsKey(projectName)) {
                Map<String, List<ExcelTemplateFileRuleByProject>> tmpMap = new HashMap<>(8);
                List<ExcelTemplateFileRuleByProject> tmpList = new ArrayList<>();
                ExcelTemplateFileRuleByProject tmp = new ExcelTemplateFileRuleByProject();
                BeanUtils.copyProperties(object, tmp);
                tmpList.add(tmp);
                tmpMap.put(ruleName, tmpList);
                templateFileExcelContent.put(projectName, tmpMap);
            } else {
                Map<String, List<ExcelTemplateFileRuleByProject>> tmpMap = templateFileExcelContent.get(projectName);
                if (!tmpMap.containsKey(ruleName)) {
                    List<ExcelTemplateFileRuleByProject> tmpList = new ArrayList<>();
                    ExcelTemplateFileRuleByProject tmp = new ExcelTemplateFileRuleByProject();
                    BeanUtils.copyProperties(object, tmp);
                    tmpList.add(tmp);
                    tmpMap.put(ruleName, tmpList);
                } else {
                    List<ExcelTemplateFileRuleByProject> tmpList = tmpMap.get(ruleName);
                    ExcelTemplateFileRuleByProject tmp = new ExcelTemplateFileRuleByProject();
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

    public List<ExcelRuleMetric> getExcelMetricContent() {
        return excelMetricContent;
    }

    public Map<String, Map<String, List<ExcelTemplateRuleByProject>>> getExcelRuleContent() {
        return excelRuleContent;
    }

    public Map<String, Map<String, List<ExcelCustomRuleByProject>>> getExcelCustomRuleContent() {
        return excelCustomRuleContent;
    }

    public Map<String, Map<String, List<ExcelMultiTemplateRuleByProject>>> getExcelMultiRuleContent() {
        return excelMultiRuleContent;
    }

    public Map<String, Map<String, List<ExcelTemplateFileRuleByProject>>> getTemplateFileExcelContent() {
        return templateFileExcelContent;
    }
}
