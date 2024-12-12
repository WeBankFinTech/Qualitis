package com.webank.wedatasphere.qualitis.rule.entity;

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
 * @author v_gaojiedeng@webank.com
 */
@Entity
@Table(name = "qualitis_standard_value_version")
public class StandardValueVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 中文名称
     */
    @Column(name = "cn_name")
    private String cnName;

    /**
     * 英文名称
     */
    @Column(name = "en_name")
    private String enName;

    /**
     * 版本号
     */
    @Column(name = "version")
    private Long version;

    @OneToMany(mappedBy = "standardValueVersion", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<StandardValueLabelVersion> standardValueLabelVersion;

    @OneToMany(mappedBy = "standardValueVersion", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<StandardValueActionVersion> standardValueActionVersion;

    @ManyToOne
    private StandardValue standardValue;

    /**
     * 内容
     */
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    /**
     * 来源
     */
    @Column(name = "source", columnDefinition = "MEDIUMTEXT")
    private String source;

    /**
     * 审批系统
     */
    @Column(name = "approve_system")
    private String approveSystem;


    /**
     * 审批单号
     */
    @Column(name = "approve_number")
    private String approveNumber;

    /**
     * 等级(1内置模版2部门模版3个人模版)
     */
    @Column(name = "standard_value_type")
    private Integer standardValueType;

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
     * 创建人
     */
    @Column(name = "create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 修改人
     */
    @Column(name = "modify_user")
    private String modifyUser;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private String modifyTime;

    /**
     * 是否可用
     */
    @Column(name = "is_available")
    private Boolean isAvailable;

    /**
     * 标准值来源(自定义、DataShapis)
     */
    @Column(name = "source_value")
    private Integer sourceValue;

    /**
     * 数据标准类别英文名称
     */
    @Column(name = "std_sub_code")
    private String stdSubCode;

    /**
     * 数据标准类别中文名称
     */
    @Column(name = "std_sub_name")
    private String stdSubName;

    /**
     * 标准大类英文名称
     */
    @Column(name = "std_big_category_code")
    private String stdBigCategoryCode;

    /**
     * 标准大类中文名称
     */
    @Column(name = "std_big_category_name")
    private String stdBigCategoryName;

    /**
     * 标准小类英文名称
     */
    @Column(name = "small_category_code")
    private String smallCategoryCode;

    /**
     * 标准小类中文名称
     */
    @Column(name = "small_category_name")
    private String smallCategoryName;

    /**
     * 数据标准中文名称
     */
    @Column(name = "std_cn_name")
    private String stdCnName;

    /**
     * 数据标准代码--代码英文名
     */
    @Column(name = "code")
    private String code;

    /**
     * 数据标准代码中文名称--代码中文名
     */
    @Column(name = "code_name")
    private String codeName;

    /**
     * 数据标准小类urn
     */
    @Column(name = "std_small_category_urn")
    private String stdSmallCategoryUrn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<StandardValueLabelVersion> getStandardValueLabelVersion() {
        return standardValueLabelVersion;
    }

    public void setStandardValueLabelVersion(Set<StandardValueLabelVersion> standardValueLabelVersion) {
        this.standardValueLabelVersion = standardValueLabelVersion;
    }

    public Set<StandardValueActionVersion> getStandardValueActionVersion() {
        return standardValueActionVersion;
    }

    public void setStandardValueActionVersion(Set<StandardValueActionVersion> standardValueActionVersion) {
        this.standardValueActionVersion = standardValueActionVersion;
    }

    public StandardValue getStandardValue() {
        return standardValue;
    }

    public void setStandardValue(StandardValue standardValue) {
        this.standardValue = standardValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getApproveSystem() {
        return approveSystem;
    }

    public void setApproveSystem(String approveSystem) {
        this.approveSystem = approveSystem;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public Integer getStandardValueType() {
        return standardValueType;
    }

    public void setStandardValueType(Integer standardValueType) {
        this.standardValueType = standardValueType;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(Integer sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getStdSubCode() {
        return stdSubCode;
    }

    public void setStdSubCode(String stdSubCode) {
        this.stdSubCode = stdSubCode;
    }

    public String getStdSubName() {
        return stdSubName;
    }

    public void setStdSubName(String stdSubName) {
        this.stdSubName = stdSubName;
    }

    public String getStdBigCategoryCode() {
        return stdBigCategoryCode;
    }

    public void setStdBigCategoryCode(String stdBigCategoryCode) {
        this.stdBigCategoryCode = stdBigCategoryCode;
    }

    public String getStdBigCategoryName() {
        return stdBigCategoryName;
    }

    public void setStdBigCategoryName(String stdBigCategoryName) {
        this.stdBigCategoryName = stdBigCategoryName;
    }

    public String getSmallCategoryCode() {
        return smallCategoryCode;
    }

    public void setSmallCategoryCode(String smallCategoryCode) {
        this.smallCategoryCode = smallCategoryCode;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getStdCnName() {
        return stdCnName;
    }

    public void setStdCnName(String stdCnName) {
        this.stdCnName = stdCnName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getStdSmallCategoryUrn() {
        return stdSmallCategoryUrn;
    }

    public void setStdSmallCategoryUrn(String stdSmallCategoryUrn) {
        this.stdSmallCategoryUrn = stdSmallCategoryUrn;
    }
}
