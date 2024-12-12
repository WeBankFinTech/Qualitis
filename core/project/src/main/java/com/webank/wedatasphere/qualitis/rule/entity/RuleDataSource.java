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

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.codehaus.jackson.annotate.JsonIgnore;

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
    @Column(name = "file_uuid", length = 50)
    private String fileUuid;

    @Column(name = "project_id", length = 20)
    private Long projectId;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Rule rule;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private RuleGroup ruleGroup;

    @Column(length = 3200)
    private String filter;

    @Column(name = "datasource_index")
    private Integer datasourceIndex;

    @Column(name = "file_id", length = 256)
    private String fileId;

    @Column(name = "file_hash_values")
    private String fileHashValue;

    @Column(name = "file_table_desc")
    private String fileTableDesc;

    @Column(name = "file_delimiter")
    private String fileDelimiter;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_header")
    private Boolean fileHeader;

    @Column(name= "file_sheet_name")
    private String fileSheetName;

    @Column(name = "proxy_user")
    private String proxyUser;

    @Column(name = "linkis_datasource_id")
    private Long linkisDataSourceId;

    @Column(name = "linkis_datasource_version_id")
    private Long linkisDataSourceVersionId;

    @Column(name = "linkis_datasource_name")
    private String linkisDataSourceName;

    @OneToMany(mappedBy = "ruleDataSource", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<RuleDataSourceEnv> ruleDataSourceEnvs;
    /**
     * datasource type, such as hive, mysql, tdsql, kafka, fps
     */
    @Column(name = "datasource_type")
    private Integer datasourceType;

    @Column(name = "sub_system_id")
    private Long subSystemId;
    @Column(name = "sub_system_name")
    private String subSystemName;
    @Column(name = "department_code")
    private String departmentCode;
    @Column(name = "department_name")
    private String departmentName;
    @Column(name = "dev_department_name")
    private String devDepartmentName;
    @Column(name = "tag_code")
    private String tagCode;
    @Column(name = "tag_name")
    private String tagName;

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
        this.fileUuid = ruleDataSource.getFileUuid();
        this.filter = ruleDataSource.getFilter();
        this.rule = ruleDataSource.getRule();
        this.datasourceIndex = ruleDataSource.getDatasourceIndex();
        this.linkisDataSourceId = ruleDataSource.getLinkisDataSourceId();
        this.linkisDataSourceVersionId = ruleDataSource.getLinkisDataSourceVersionId();
        this.linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
        this.datasourceType = ruleDataSource.getDatasourceType();
    }

    public String getFileSheetName() {
        return fileSheetName;
    }

    public void setFileSheetName(String fileSheetName) {
        this.fileSheetName = fileSheetName;
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

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public Integer getDatasourceIndex() {
        return datasourceIndex;
    }

    public void setDatasourceIndex(Integer datasourceIndex) {
        this.datasourceIndex = datasourceIndex;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileTableDesc() {
        return fileTableDesc;
    }

    public void setFileTableDesc(String fileTableDesc) {
        this.fileTableDesc = fileTableDesc;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getFileHashValue() {
        return fileHashValue;
    }

    public void setFileHashValue(String fileHashValue) {
        this.fileHashValue = fileHashValue;
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

    public List<RuleDataSourceEnv> getRuleDataSourceEnvs() {
        return ruleDataSourceEnvs;
    }

    public void setRuleDataSourceEnvs(List<RuleDataSourceEnv> ruleDataSourceEnvs) {
        this.ruleDataSourceEnvs = ruleDataSourceEnvs;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public RuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public Long getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(Long subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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
            ", fileId='" + fileId + '\'' +
            ", fileTableDesc='" + fileTableDesc + '\'' +
            ", fileDelimiter='" + fileDelimiter + '\'' +
            ", fileType='" + fileType + '\'' +
            ", fileHeader=" + fileHeader +
            ", proxyUser=" + proxyUser +
            '}';
    }
}
