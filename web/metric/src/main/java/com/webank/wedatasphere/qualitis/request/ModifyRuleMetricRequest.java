package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:33
 */
public class ModifyRuleMetricRequest {
  @JsonProperty("id")
  private Long id;
  private String name;
  @JsonProperty("cn_name")
  private String cnName;
  @JsonProperty("metric_desc")
  private String desc;

  @JsonProperty("sub_system_name")
  private String subSystemName;
  @JsonProperty("full_cn_name")
  private String fullCnName;

  @JsonProperty("product_name")
  private String productName;

  @JsonProperty("department_name")
  private String departmentName;


  @JsonProperty("dev_department_name")
  private String devDepartmentName;
  @JsonProperty("ops_department_name")
  private String opsDepartmentName;

  @JsonProperty("sub_system_alias")
  private String subSystemAlias;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("en_code")
  private String enCode;
  @JsonProperty("available")
  private Boolean available;
  @JsonProperty("frequency")
  private Integer frequency;
  @JsonProperty("buss_code")
  private Integer bussCode;
  @JsonProperty("buss_custom")
  private String bussCustom;

  public ModifyRuleMetricRequest() {
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

  public String getCnName() {
    return cnName;
  }

  public void setCnName(String cnName) {
    this.cnName = cnName;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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

  public String getSubSystemAlias() {
    return subSystemAlias;
  }

  public void setSubSystemAlias(String subSystemAlias) {
    this.subSystemAlias = subSystemAlias;
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

  @Override
  public String toString() {
    return "ModifyRuleMetricRequest{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", cnName='" + cnName + '\'' +
        ", desc='" + desc + '\'' +
        ", departmentName='" + departmentName + '\'' +
        ", devDepartmentName='" + devDepartmentName + '\'' +
        ", opsDepartmentName='" + opsDepartmentName + '\'' +
        ", subSystemAlias='" + subSystemAlias + '\'' +
        ", type='" + type + '\'' +
        ", enCode='" + enCode + '\'' +
        ", available=" + available +
        ", frequency=" + frequency +
        ", bussCode=" + bussCode +
        '}';
  }
}
