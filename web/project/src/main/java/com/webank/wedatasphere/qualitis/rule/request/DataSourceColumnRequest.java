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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-27
 */
public class DataSourceColumnRequest {

    @JsonProperty("column_name")
    private String columnName;
    @JsonProperty("data_type")
    private String dataType;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public DataSourceColumnRequest() {
        // 默认构造函数
    }

    public DataSourceColumnRequest(String columnName, String dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public static void checkRequest(List<DataSourceColumnRequest> requests, boolean fps) throws UnExpectedRequestException {
        for (DataSourceColumnRequest request : requests){
            CommonChecker.checkString(request.getColumnName(), "column_name");
            if (fps) {
                continue;
            } else {
                CommonChecker.checkString(request.getDataType(), "data_type");
            }
        }
    }
}
