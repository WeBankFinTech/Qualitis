package com.webank.wedatasphere.qualitis.cmdb.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 14:51
 */
public class SubSystemConditionRequest {
  @JsonProperty(value = "subsystem_name")
  private String subsystemName;

  public SubSystemConditionRequest() {
    // Default Constructor
  }

  public String getSubsystemName() {
    return subsystemName;
  }

  public void setSubsystemName(String subsystemName) {
    this.subsystemName = subsystemName;
  }
}
