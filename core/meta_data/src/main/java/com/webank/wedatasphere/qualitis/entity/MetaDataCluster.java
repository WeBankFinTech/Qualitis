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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author howeye
 */
@Entity
@Table(name = "meta_data_cluster")
public class MetaDataCluster {

    @Id
    @Column(name = "cluster_name", length = 100)
    private String clusterName;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "metaDataCluster")
    private List<MetaDataDb> metaDataDbList;

    public MetaDataCluster() {
    }

    public MetaDataCluster(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
