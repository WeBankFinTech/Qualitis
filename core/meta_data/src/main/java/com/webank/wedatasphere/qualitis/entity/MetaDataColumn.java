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

package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author howeye
 */
@Entity
@Table(name = "meta_data_column", uniqueConstraints = @UniqueConstraint(columnNames = {"column_name", "table_id"}))
public class MetaDataColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "column_name", length = 230)
    private String columnName;

    @Column(name = "column_type", length = 300)
    private String columnType;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private MetaDataTable metaDataTable;

    public MetaDataColumn() {
    }

    public MetaDataColumn(MetaDataTable metaDataTable, String columnName, String columnType) {
        this.metaDataTable = metaDataTable;
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public MetaDataTable getMetaDataTable() {
        return metaDataTable;
    }

    public void setMetaDataTable(MetaDataTable metaDataTable) {
        this.metaDataTable = metaDataTable;
    }
}
