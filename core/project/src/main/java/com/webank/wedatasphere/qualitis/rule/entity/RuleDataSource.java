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

package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_datasource")
public class RuleDataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cluster_name", length = 100)
    private String clusterName;
    @Column(name = "db_name", length = 100)
    private String dbName;
    @Column(name = "table_name", length = 100)
    private String tableName;
    @Column(name = "col_name", columnDefinition = "MEDIUMTEXT")
    private String colName;
    @Column(name = "black_col_name")
    private Boolean blackColName;
    @Column(name = "project_id", length = 20)
    private Long projectId;

    @ManyToOne
    private Rule rule;

    @Column(length = 3200)
    private String filter;

    @Column(name = "datasource_index")
    private Integer datasourceIndex;

    @Column(name = "proxy_user")
    private String proxyUser;

    @Column(name = "linkis_datasoure_id")
    private Long linkisDataSourceId;
    @Column(name = "linkis_datasoure_version_id")
    private Long linkisDataSourceVersionId;

    @Column(name = "linkis_datasource_name")
    private String linkisDataSourceName;
    /**
     * datasource type, such as hive, mysql, kafka
     */
    @Column(name = "datasource_type")
    private Integer datasourceType;

    public RuleDataSource() {
        // Default Constructor
    }

    public RuleDataSource(RuleDataSource ruleDataSource) {
        this.id = ruleDataSource.getId();
        this.clusterName = ruleDataSource.getClusterName();
        this.tableName = ruleDataSource.getTableName();
        this.colName = ruleDataSource.getColName();
        this.blackColName = ruleDataSource.getBlackColName();
        this.dbName = ruleDataSource.getDbName();
        this.projectId = ruleDataSource.getProjectId();
        this.filter = ruleDataSource.getFilter();
        this.rule = ruleDataSource.getRule();
        this.datasourceIndex = ruleDataSource.getDatasourceIndex();
        this.linkisDataSourceId = ruleDataSource.getLinkisDataSourceId();
        this.linkisDataSourceVersionId = ruleDataSource.getLinkisDataSourceVersionId();
        this.linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
        this.datasourceType = ruleDataSource.getDatasourceType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Boolean getBlackColName() {
        return blackColName;
    }

    public void setBlackColName(Boolean blackColName) {
        this.blackColName = blackColName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getDatasourceIndex() {
        return datasourceIndex;
    }

    public void setDatasourceIndex(Integer datasourceIndex) {
        this.datasourceIndex = datasourceIndex;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public Long getLinkisDataSourceVersionId() {
        return linkisDataSourceVersionId;
    }

    public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
        this.linkisDataSourceVersionId = linkisDataSourceVersionId;
    }

    public String getLinkisDataSourceName() {
        return linkisDataSourceName;
    }

    public void setLinkisDataSourceName(String linkisDataSourceName) {
        this.linkisDataSourceName = linkisDataSourceName;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        RuleDataSource that = (RuleDataSource) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RuleDataSource{" +
            "id=" + id +
            ", clusterName='" + clusterName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", colName='" + colName + '\'' +
            ", projectId=" + projectId +
            ", filter='" + filter + '\'' +
            ", datasourceIndex=" + datasourceIndex +
            ", proxyUser=" + proxyUser +
            '}';
    }
}
