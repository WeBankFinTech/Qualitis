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

package com.webank.wedatasphere.qualitis.metadata.response.column;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author howeye
 */
public class ColumnInfoDetail {
    @JsonProperty("column_name")
    private String fieldName;
    @JsonProperty("data_type")
    private String dataType;
    /**
     * Extended attributes, displayed as data source rule query.
     */
    @JsonProperty("column_length")
    private Integer columnLen;
    @JsonProperty("column_alias")
    private String columnAlias;
    @JsonProperty("column_comment")
    private String columnComment;
    @JsonProperty("is_primary")
    private Boolean isPrimary;
    @JsonProperty("is_partition")
    private Boolean isPartitionField;
    @JsonProperty("rule_count")
    private Integer ruleCount;

    public ColumnInfoDetail() {
        // Default Constructor
    }

    public ColumnInfoDetail(String fieldName, String dataType) {
        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getColumnLen() {
        return columnLen;
    }

    public void setColumnLen(Integer columnLen) {
        this.columnLen = columnLen;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Boolean getPartitionField() {
        return isPartitionField;
    }

    public void setPartitionField(Boolean partitionField) {
        isPartitionField = partitionField;
    }

    public Integer getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(Integer ruleCount) {
        this.ruleCount = ruleCount;
    }

    @Override
    public String toString() {
        return "ColumnInfoDetail{" +
            "fieldName='" + fieldName + '\'' +
            ", dataType='" + dataType + '\'' +
            '}';
    }
}
