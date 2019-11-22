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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author howeye
 */
@Entity
@Table(name = "meta_data_table", uniqueConstraints = @UniqueConstraint(columnNames = {"table_name", "db_id"}))
public class MetaDataTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", length = 230)
    private String tableName;

    @ManyToOne
    @JoinColumn(name = "db_id")
    private MetaDataDb metaDataDb;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "metaDataTable")
    private List<MetaDataColumn> metaDataColumnList;

    public MetaDataTable() {
    }

    public MetaDataTable(MetaDataDb metaDataDb, String tableName) {
        this.metaDataDb = metaDataDb;
        this.tableName = tableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetaDataTable(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public MetaDataDb getMetaDataDb() {
        return metaDataDb;
    }

    public void setMetaDataDb(MetaDataDb metaDataDb) {
        this.metaDataDb = metaDataDb;
    }
}
