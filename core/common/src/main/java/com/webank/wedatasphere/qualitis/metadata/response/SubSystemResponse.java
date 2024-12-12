package com.webank.wedatasphere.qualitis.metadata.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 10:54
 */
public class SubSystemResponse {
  private String subSystemId;
  private String subSystemName;
  private String subSystemFullCnName;

  @JsonProperty("department_name")
  private String departmentName;

  @JsonProperty("dev_department_name")
  private String devDepartmentName;
  @JsonProperty("ops_department_name")
  private String opsDepartmentName;

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

  public String getSubSystemFullCnName() {
    return subSystemFullCnName;
  }

  public void setSubSystemFullCnName(String subSystemFullCnName) {
    this.subSystemFullCnName = subSystemFullCnName;
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
}
