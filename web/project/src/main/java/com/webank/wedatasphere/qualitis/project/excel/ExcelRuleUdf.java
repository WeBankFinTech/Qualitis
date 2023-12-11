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
 * @author allenzhou
 */
public class ExcelRuleUdf extends BaseRowModel {

    @ExcelProperty(value = "Rule Udf Json Object", index = 0)
    private String udfJsonObject;

    @ExcelProperty(value = "Data Visibility Json Object", index = 1)
    private String dataVisibilityJsonObject;

    public ExcelRuleUdf() {
        // Default
    }

    public String getUdfJsonObject() {
        return udfJsonObject;
    }

    public void setUdfJsonObject(String udfJsonObject) {
        this.udfJsonObject = udfJsonObject;
    }

    public String getDataVisibilityJsonObject() {
        return dataVisibilityJsonObject;
    }

    public void setDataVisibilityJsonObject(String dataVisibilityJsonObject) {
        this.dataVisibilityJsonObject = dataVisibilityJsonObject;
    }
}
