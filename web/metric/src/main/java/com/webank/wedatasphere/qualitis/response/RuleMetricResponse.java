package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:30
 */
public class RuleMetricResponse {

  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("cn_name")
  private String cnName;
  @JsonProperty("metric_desc")
  private String metricDesc;

  @JsonProperty("sub_system_id")
  private String subSystemId;
  @JsonProperty("sub_system_name")
  private String subSystemName;
  @JsonProperty("full_cn_name")
  private String fullCnName;

  @JsonProperty("product_id")
  private String productId;
  @JsonProperty("product_name")
  private String productName;

  @JsonProperty("create_user")
  private String createUser;
  @JsonProperty("create_time")
  private String createTime;
  @JsonProperty("modify_user")
  private String modifyUser;
  @JsonProperty("modify_time")
  private String modifyTime;

  @JsonProperty("department_code")
  private String departmentCode;
  @JsonProperty("department_name")
  private String departmentName;
  @JsonProperty("dev_department_name")
  private String devDepartmentName;
  @JsonProperty("ops_department_name")
  private String opsDepartmentName;
  @JsonProperty("dev_department_id")
  private Long devDepartmentId;
  @JsonProperty("ops_department_id")
  private Long opsDepartmentId;

  @JsonProperty("metric_level")
  private Integer level;

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

  @JsonProperty("multi_env")
  private Boolean multiEnv;

  @JsonProperty("used")
  private Boolean used;
  @JsonProperty("is_editable")
  private boolean isEditable;
  @JsonProperty("visibility_department_list")
  private List<DepartmentSubInfoResponse> visibilityDepartmentList;

  public RuleMetricResponse() {
  }

  public RuleMetricResponse(RuleMetric ruleMetric) {
    BeanUtils.copyProperties(ruleMetric, this);
    this.devDepartmentId = ruleMetric.getDevDepartmentId();
    this.opsDepartmentId = ruleMetric.getOpsDepartmentId();
  }

  public RuleMetricResponse(RuleMetric ruleMetric, boolean used) {
    BeanUtils.copyProperties(ruleMetric, this);
    this.used = used;
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

  public boolean isEditable() {
    return isEditable;
  }

  public void setEditable(boolean editable) {
    isEditable = editable;
  }

  public Boolean getUsed() {
    return used;
  }

  public void setUsed(Boolean used) {
    this.used = used;
  }

  public List<DepartmentSubInfoResponse> getVisibilityDepartmentList() {
    return visibilityDepartmentList;
  }

  public void setVisibilityDepartmentList(List<DepartmentSubInfoResponse> visibilityDepartmentList) {
    this.visibilityDepartmentList = visibilityDepartmentList;
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
}
