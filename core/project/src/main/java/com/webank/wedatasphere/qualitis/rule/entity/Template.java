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
import com.webank.wedatasphere.qualitis.rule.util.LazyGetUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

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
    @Column(length = 180)
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

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<TemplateStatisticsInputMeta> statisticAction;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<TemplateMidTableInputMeta> templateMidTableInputMetas;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<TemplateOutputMeta> templateOutputMetas;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<TemplateUdf> templateUdf;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<TemplateCountFunction> templateCountFunction;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<TemplateDataSourceType> templateDataSourceType;

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

    /**
     * 开发部门
     */
    @Column(name = "dev_department_name")
    private String devDepartmentName;

    /**
     * 运维部门
     */
    @Column(name = "ops_department_name")
    private String opsDepartmentName;

    @Column(name = "dev_department_id")
    private Long devDepartmentId;

    @Column(name = "ops_department_id")
    private Long opsDepartmentId;

    /**
     * 英文名称
     */
    @Column(name="en_name",length = 64)
    private String enName;
    /**
     * 模板描述
     */
    @Column(name="description",length = 256)
    private String description;
    /**
     * 校验级别
     */
    @Column(name="verification_level")
    private Integer verificationLevel;
    /**
     * 校验类型
     */
    @Column(name="verification_type")
    private Integer verificationType;
    /**
     * 是否保存异常数据
     */
    @Column(name="exception_database")
    private Boolean exceptionDatabase;
    /**
     * 是否需要过滤字段
     */
    @Column(name="filter_fields")
    private Boolean filterFields;
    /**
     * 是否使用UDF
     */
    @Column(name="whether_using_functions")
    private Boolean whetherUsingFunctions;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private String modifyTime;

    /**
     * 校验中文名
     */
    @Column(name = "verification_cn_name")
    private String verificationCnName;

    /**
     * 校验英文名
     */
    @Column(name = "verification_en_name")
    private String verificationEnName;

    /**
     * 命名方式
     */
    @Column(name="naming_method")
    private Integer namingMethod;

    /**
     * 是否固化校验方式
     */
    @Column(name="whether_solidification")
    private Boolean whetherSolidification;

    /**
     * 校验方式
     */
    @Column(name="check_template")
    private Integer checkTemplate;

    /**
     * 模板大类、类别
     */
    @Column(name="major_type")
    private String majorType;

    /**
     * 模板编号
     */
    @Column(name="template_number")
    private String templateNumber;

    /**
     * 自定义中文码
     */
    @Column(name="custom_zh_code")
    private String customZhCode;

    @Column(name = "calcu_unit_id")
    private Long calcuUnitId;

    public Template() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
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
        if (this.id == null) {
            return this.templateMidTableInputMetas;
        } else {
            return LazyGetUtil.getTemplateMidTableInputMetas(this);
        }
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
        if (this.id == null) {
            return this.statisticAction;
        } else {
            return LazyGetUtil.getStatisticAction(this);
        }
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

    public Set<TemplateUdf> getTemplateUdf() {
        return templateUdf;
    }

    public void setTemplateUdf(Set<TemplateUdf> templateUdf) {
        this.templateUdf = templateUdf;
    }

    public Set<TemplateCountFunction> getTemplateCountFunction() {
        return templateCountFunction;
    }

    public void setTemplateCountFunction(Set<TemplateCountFunction> templateCountFunction) {
        this.templateCountFunction = templateCountFunction;
    }

    public Set<TemplateDataSourceType> getTemplateDataSourceType() {
        return templateDataSourceType;
    }

    public void setTemplateDataSourceType(Set<TemplateDataSourceType> templateDataSourceType) {
        this.templateDataSourceType = templateDataSourceType;
    }

    public String getImportExportName() {
        return importExportName;
    }

    public void setImportExportName(String importExportName) {
        this.importExportName = importExportName;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(Integer verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    public Integer getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(Integer verificationType) {
        this.verificationType = verificationType;
    }

    public Boolean getExceptionDatabase() {
        return exceptionDatabase;
    }

    public void setExceptionDatabase(Boolean exceptionDatabase) {
        this.exceptionDatabase = exceptionDatabase;
    }

    public Boolean getFilterFields() {
        return filterFields;
    }

    public void setFilterFields(Boolean filterFields) {
        this.filterFields = filterFields;
    }

    public Boolean getWhetherUsingFunctions() {
        return whetherUsingFunctions;
    }

    public void setWhetherUsingFunctions(Boolean whetherUsingFunctions) {
        this.whetherUsingFunctions = whetherUsingFunctions;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getVerificationCnName() {
        return verificationCnName;
    }

    public void setVerificationCnName(String verificationCnName) {
        this.verificationCnName = verificationCnName;
    }

    public String getVerificationEnName() {
        return verificationEnName;
    }

    public void setVerificationEnName(String verificationEnName) {
        this.verificationEnName = verificationEnName;
    }

    public Integer getNamingMethod() {
        return namingMethod;
    }

    public void setNamingMethod(Integer namingMethod) {
        this.namingMethod = namingMethod;
    }

    public Boolean getWhetherSolidification() {
        return whetherSolidification;
    }

    public void setWhetherSolidification(Boolean whetherSolidification) {
        this.whetherSolidification = whetherSolidification;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public String getMajorType() {
        return majorType;
    }

    public void setMajorType(String majorType) {
        this.majorType = majorType;
    }

    public String getTemplateNumber() {
        return templateNumber;
    }

    public void setTemplateNumber(String templateNumber) {
        this.templateNumber = templateNumber;
    }

    public String getCustomZhCode() {
        return customZhCode;
    }

    public void setCustomZhCode(String customZhCode) {
        this.customZhCode = customZhCode;
    }

    public Long getCalcuUnitId() {
        return calcuUnitId;
    }

    public void setCalcuUnitId(Long calcuUnitId) {
        this.calcuUnitId = calcuUnitId;
    }

    @Override
    public String toString() {
        return "Template{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", midTableAction='" + midTableAction + '\'' +
            ", level=" + level +
            ", createUser=" + createUser +
            ", modifyUser=" + modifyUser +
            ", devDepartmentName='" + devDepartmentName + '\'' +
            ", opsDepartmentName='" + opsDepartmentName + '\'' +
            ", devDepartmentId=" + devDepartmentId +
            ", opsDepartmentId=" + opsDepartmentId +
            ", enName='" + enName + '\'' +
            '}';
    }
}
