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

    @ExcelProperty(value = "Project Object", index = 0)
    private String projectObject;

    @ExcelProperty(value = "Project User Object", index = 1)
    private String projectUserObject;

    @ExcelProperty(value = "Project Label Object", index = 2)
    private String projectLabelObject;

    public ExcelProject() {
        // Default Constructor
    }

    public String getProjectObject() {
        return projectObject;
    }

    public void setProjectObject(String projectObject) {
        this.projectObject = projectObject;
    }

    public String getProjectUserObject() {
        return projectUserObject;
    }

    public void setProjectUserObject(String projectUserObject) {
        this.projectUserObject = projectUserObject;
    }

    public String getProjectLabelObject() {
        return projectLabelObject;
    }

    public void setProjectLabelObject(String projectLabelObject) {
        this.projectLabelObject = projectLabelObject;
    }

    @Override
    public String toString() {
        return "ExcelProject{" +
            "projectObject='" + projectObject + '\'' +
            ", projectUserObject='" + projectUserObject + '\'' +
            ", projectLabelObject='" + projectLabelObject + '\'' +
            '}';
    }
}
