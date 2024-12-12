package com.webank.wedatasphere.qualitis.entity;

import com.webank.wedatasphere.qualitis.constant.RuleMetricBussCodeEnum;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author allenzhou
 */
@Entity
@Table(name = "qualitis_rule_metric")
public class RuleMetric {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;
  @Column(name = "cn_name")
  private String cnName;
  @Column(name = "metric_desc")
  private String metricDesc;

  @Column(name = "sub_system_id")
  private String subSystemId;
  @Column(name = "sub_system_name")
  private String subSystemName;
  @Column(name = "full_cn_name")
  private String fullCnName;

  @Column(name = "product_id")
  private String productId;
  @Column(name = "product_name")
  private String productName;

  @Column(name = "create_user", length = 50)
  private String createUser;
  @Column(name = "create_time", length = 25)
  private String createTime;
  @Column(name = "modify_user", length = 50)
  private String modifyUser;
  @Column(name = "modify_time", length = 25)
  private String modifyTime;

  @Column(name = "department_code")
  private String departmentCode;
  @Column(name = "department_name")
  private String departmentName;
  @Column(name = "dev_department_name")
  private String devDepartmentName;
  @Column(name = "ops_department_name")
  private String opsDepartmentName;
  @Column(name = "dev_department_id")
  private Long devDepartmentId;
  @Column(name = "ops_department_id")
  private Long opsDepartmentId;

  @Column(name = "metric_level")
  private Integer level;

  @Column(name = "type")
  private Integer type;
  @Column(name = "en_code")
  private String enCode;

  @Column(name = "available")
  private Boolean available;
  @Column(name = "frequency")
  private Integer frequency;

  @Column(name = "buss_code")
  private Integer bussCode;
  @Column(name = "buss_custom")
  private String bussCustom;

  @Column(name = "multi_env")
  private Boolean multiEnv;

  public RuleMetric(String name, String cnName, String desc, String subSystemId, String subSystemName, String fullCnName, String productId
      , String productName, String departmentCode, String departmentName, String devDepartmentName, String opsDepartmentName, Integer type
      , String enCode, Integer frequency, Boolean available, Integer bussCode, String bussCustom, Boolean multiEnv) {
    this.name = name;
    this.cnName = cnName;
    this.metricDesc = desc;
    this.bussCode = bussCode;

    if (RuleMetricBussCodeEnum.SUBSYSTEM.getCode().equals(bussCode)) {
      this.subSystemId = subSystemId;
      this.subSystemName = subSystemName;
      this.fullCnName = fullCnName;
    } else if (RuleMetricBussCodeEnum.PRODUCT.getCode().equals(bussCode)) {
      this.productId = productId;
      this.productName = productName;
    } else if (RuleMetricBussCodeEnum.CUSTOM.getCode().equals(bussCode)) {
      this.bussCustom = bussCustom;
    }
    this.departmentCode = departmentCode;
    this.departmentName = departmentName;
    this.devDepartmentName = devDepartmentName;
    this.opsDepartmentName = opsDepartmentName;

    this.type = type;
    this.enCode = enCode;
    this.frequency = frequency;
    this.available = available;
    this.multiEnv = multiEnv;
  }

  public RuleMetric() {

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

  public String getCnName() {
    return cnName;
  }

  public void setCnName(String cnName) {
    this.cnName = cnName;
  }

  public String getMetricDesc() {
    return metricDesc;
  }

  public void setMetricDesc(String metricDesc) {
    this.metricDesc = metricDesc;
  }

  public String getSubSystemId() {
    return subSystemId;
  }

  public void setSubSystemId(String subSystemId) {
    this.subSystemId = subSystemId;
  }

  public String getSubSystemName() {
    return subSystemName;
  }

  public void setSubSystemName(String subSystemName) {
    this.subSystemName = subSystemName;
  }

  public String getFullCnName() {
    return fullCnName;
  }

  public void setFullCnName(String fullCnName) {
    this.fullCnName = fullCnName;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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

  public String getOpsDepartmentName() {
    return opsDepartmentName;
  }

  public void setOpsDepartmentName(String opsDepartmentName) {
    this.opsDepartmentName = opsDepartmentName;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
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

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getEnCode() {
    return enCode;
  }

  public void setEnCode(String enCode) {
    this.enCode = enCode;
  }

  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public Integer getBussCode() {
    return bussCode;
  }

  public void setBussCode(Integer bussCode) {
    this.bussCode = bussCode;
  }

  public String getBussCustom() {
    return bussCustom;
  }

  public void setBussCustom(String bussCustom) {
    this.bussCustom = bussCustom;
  }

  public Boolean getMultiEnv() {
    return multiEnv;
  }

  public void setMultiEnv(Boolean multiEnv) {
    this.multiEnv = multiEnv;
  }

  @Override
  public String toString() {
    return "RuleMetric{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", cnName='" + cnName + '\'' +
        ", metricDesc='" + metricDesc + '\'' +
        ", departmentCode='" + departmentCode + '\'' +
        ", departmentName='" + departmentName + '\'' +
        ", devDepartmentName='" + devDepartmentName + '\'' +
        ", opsDepartmentName='" + opsDepartmentName + '\'' +
        ", level=" + level +
        ", createUser='" + createUser + '\'' +
        ", createTime='" + createTime + '\'' +
        ", type='" + type + '\'' +
        ", enCode='" + enCode + '\'' +
        ", available=" + available +
        ", frequency=" + frequency +
        ", bussCode=" + bussCode +
        '}';
  }
}
