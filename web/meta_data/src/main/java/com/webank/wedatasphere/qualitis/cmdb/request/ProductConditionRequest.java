package com.webank.wedatasphere.qualitis.cmdb.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 14:51
 */
public class ProductConditionRequest {
  @JsonProperty(value = "cn_name")
  private String productCnName;

  public ProductConditionRequest() {
    // Default Constructor
  }

  public String getProductCnName() {
    return productCnName;
  }

  public void setProductCnName(String productCnName) {
    this.productCnName = productCnName;
  }
}
