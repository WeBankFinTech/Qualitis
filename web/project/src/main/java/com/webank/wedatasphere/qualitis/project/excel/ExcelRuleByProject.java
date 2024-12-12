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

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * @author howeye
 */
public class ExcelRuleByProject extends BaseRowModel {

    @ExcelProperty(value = "Project Name", index = 0)
    private String projectName;

    @ExcelProperty(value = "Rule Group Name", index = 1)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Json Object", index = 2)
    private String ruleJsonObject;

    @ExcelProperty(value = "Rule Template Json Object", index = 3)
    private String ruleTemplateJsonObject;

    @ExcelProperty(value = "Rule Template Visibility Object", index = 4)
    private String ruleTemplateVisibilityObject;

    @ExcelProperty(value = "Rule Template Preview", index = 5)
    private String ruleTemplatePreview;

    public ExcelRuleByProject() {
        // Default Constructor
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public String getRuleJsonObject() {
        return ruleJsonObject;
    }

    public void setRuleJsonObject(String ruleJsonObject) {
        this.ruleJsonObject = ruleJsonObject;
    }

    public String getRuleTemplateJsonObject() {
        return ruleTemplateJsonObject;
    }

    public void setRuleTemplateJsonObject(String ruleTemplateJsonObject) {
        this.ruleTemplateJsonObject = ruleTemplateJsonObject;
    }

    public String getRuleTemplateVisibilityObject() {
        return ruleTemplateVisibilityObject;
    }

    public void setRuleTemplateVisibilityObject(String ruleTemplateVisibilityObject) {
        this.ruleTemplateVisibilityObject = ruleTemplateVisibilityObject;
    }

    public String getRuleTemplatePreview() {
        return ruleTemplatePreview;
    }

    public void setRuleTemplatePreview(String ruleTemplatePreview) {
        this.ruleTemplatePreview = ruleTemplatePreview;
    }
}
