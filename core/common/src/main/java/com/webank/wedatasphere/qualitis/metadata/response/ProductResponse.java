package com.webank.wedatasphere.qualitis.metadata.response;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 10:54
 */
public class ProductResponse {
  private String productId;
  private String productName;

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
}
