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
@Table(name = "meta_data_db", uniqueConstraints = @UniqueConstraint(columnNames = {"db_name", "cluster_name"}))
public class MetaDataDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "db_name", length = 155)
    private String dbName;

    @ManyToOne
    @JoinColumn(name = "cluster_name")
    private MetaDataCluster metaDataCluster;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "metaDataDb")
    private List<MetaDataTable> metaDataTableList;

    public MetaDataDb() {
    }

    public MetaDataDb(MetaDataCluster metaDataCluster, String dbName) {
        this.dbName = dbName;
        this.metaDataCluster = metaDataCluster;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetaDataDb(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public MetaDataCluster getMetaDataCluster() {
        return metaDataCluster;
    }

    public void setMetaDataCluster(MetaDataCluster metaDataCluster) {
        this.metaDataCluster = metaDataCluster;
    }
}
