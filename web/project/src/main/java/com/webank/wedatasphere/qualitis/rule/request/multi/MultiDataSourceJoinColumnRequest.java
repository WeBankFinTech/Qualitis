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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author howeye
 */
public class MultiDataSourceJoinColumnRequest {

    @JsonProperty("column_name")
    private String columnName;
    @JsonProperty("column_type")
    private String columnType;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDataSourceJoinColumnRequest.class);
    public MultiDataSourceJoinColumnRequest() {
    }

    public MultiDataSourceJoinColumnRequest(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public MultiDataSourceJoinColumnRequest(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public static void checkRequest(MultiDataSourceJoinColumnRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getColumnName(), "Column name");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MultiDataSourceJoinColumnRequest that = (MultiDataSourceJoinColumnRequest) o;
        return Objects.equals(columnName, that.columnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName);
    }

    @Override
    public String toString() {
        return "MultiDataSourceJoinColumnRequest{" +
            "columnName='" + columnName + '\'' +
            ", columnType='" + columnType + '\'' +
            '}';
    }
}
