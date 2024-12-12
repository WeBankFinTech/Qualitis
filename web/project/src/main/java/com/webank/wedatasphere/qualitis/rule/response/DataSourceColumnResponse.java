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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;

/**
 * @author howeye
 */
public class DataSourceColumnResponse {

    @JsonProperty("column_name")
    private String columnName;
    @JsonProperty("data_type")
    private String dataType;


    public DataSourceColumnResponse() {
    }

    public DataSourceColumnResponse(String colType) {
        int index = colType.indexOf(SpecCharEnum.COLON.getValue());
        if(index > 0){
            this.columnName = colType.substring(0, index);
            this.dataType = colType.substring(index + 1);
        }
    }

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
}
