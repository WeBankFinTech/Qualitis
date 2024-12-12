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
import com.webank.wedatasphere.qualitis.entity.User;
import javax.persistence.*;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_template")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 180, updatable = false)
    private String name;

    @Column(name = "cluster_num")
    private Integer clusterNum;
    @Column(name = "db_num")
    private Integer dbNum;
    @Column(name = "table_num")
    private Integer tableNum;
    @Column(name = "field_num")
    private Integer fieldNum;

    @Column(length = 5000, name = "mid_table_action")
    private String midTableAction;
    @Column(name = "save_mid_table")
    private Boolean saveMidTable;
    @Column(name = "show_sql", length = 5000)
    private String showSql;

    /**
     * template type, such as custom, single, multi-table
     */
    @Column(name = "template_type")
    private Integer templateType;

    /**
     * SQL(Hive), Java，Python，Scala
     */
    @Column(name = "action_type")
    private Integer actionType;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<TemplateStatisticsInputMeta> statisticAction;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<TemplateMidTableInputMeta> templateMidTableInputMetas;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<TemplateOutputMeta> templateOutputMetas;

    /**
     * Built-in templates, department shared templates, personal templates
     */
    @Column(name = "template_level")
    private Integer level;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User createUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User modifyUser;

    @OneToMany(mappedBy = "template", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<TemplateDepartment> templateDepartments;

    @Column(name = "import_export_name")
    private String importExportName;

    @OneToOne
    @JsonIgnore
    private Template parentTemplate;

    @OneToOne(mappedBy = "parentTemplate", fetch = FetchType.EAGER)
    @JsonIgnore
    private Template childTemplate;

    public Template() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMidTableAction() {
        return midTableAction;
    }

    public void setMidTableAction(String midTableAction) {
        this.midTableAction = midTableAction;
    }

    public Set<TemplateMidTableInputMeta> getTemplateMidTableInputMetas() {
        return templateMidTableInputMetas;
    }

    public void setTemplateMidTableInputMetas(Set<TemplateMidTableInputMeta> templateMidTableInputMetas) {
        this.templateMidTableInputMetas = templateMidTableInputMetas;
    }

    public Set<TemplateOutputMeta> getTemplateOutputMetas() {
        return templateOutputMetas;
    }

    public void setTemplateOutputMetas(Set<TemplateOutputMeta> templateOutputMetas) {
        this.templateOutputMetas = templateOutputMetas;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Set<TemplateStatisticsInputMeta> getStatisticAction() {
        return statisticAction;
    }

    public void setStatisticAction(Set<TemplateStatisticsInputMeta> statisticAction) {
        this.statisticAction = statisticAction;
    }

    public Integer getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(Integer clusterNum) {
        this.clusterNum = clusterNum;
    }

    public Integer getDbNum() {
        return dbNum;
    }

    public void setDbNum(Integer dbNum) {
        this.dbNum = dbNum;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public void setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
    }

    public Integer getFieldNum() {
        return fieldNum;
    }

    public void setFieldNum(Integer fieldNum) {
        this.fieldNum = fieldNum;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(User modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Set<TemplateDepartment> getTemplateDepartments() {
        return templateDepartments;
    }

    public void setTemplateDepartments(Set<TemplateDepartment> templateDepartments) {
        this.templateDepartments = templateDepartments;
    }

    public String getImportExportName() {
        return importExportName;
    }

    public void setImportExportName(String importExportName) {
        this.importExportName = importExportName;
    }

    public Template getParentTemplate() {
        return parentTemplate;
    }

    public void setParentTemplate(Template parentTemplate) {
        this.parentTemplate = parentTemplate;
    }

    public Template getChildTemplate() {
        return childTemplate;
    }

    public void setChildTemplate(Template childTemplate) {
        this.childTemplate = childTemplate;
    }
}
