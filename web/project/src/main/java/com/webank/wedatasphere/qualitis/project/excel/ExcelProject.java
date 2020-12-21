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
public class ExcelProject extends BaseRowModel {

    @ExcelProperty(value = "Project", index = 0)
    private String projectName;

    @ExcelProperty(value = "Project Description", index = 1)
    private String projectDescription;

    @ExcelProperty(value = "Project Labels", index = 2)
    private String projectLabels;

    public ExcelProject() {
        // Default Constructor
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectLabels() {
        return projectLabels;
    }

    public void setProjectLabels(String projectLabels) {
        this.projectLabels = projectLabels;
    }

    @Override
    public String toString() {
        return "ExcelProject{" +
            "projectName='" + projectName + '\'' +
            ", projectDescription='" + projectDescription + '\'' +
            ", projectLabels='" + projectLabels + '\'' +
            '}';
    }
}
