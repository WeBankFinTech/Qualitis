package com.webank.wedatasphere.qualitis.client.constant;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/3 10:41
 */
public enum OperateEnum {
  /**
   * 消息类型
   */
  SUB_SYSTEM(1, "子系统信息请求"),
  PRODUCT(2, "产品信息请求"),
  DEPARTMENT(3, "部门信息请求"),
  DEV_DEPARTMENT(4, "开发部门信息请求"),
  OPS_DEPARTMENT(5, "运维部门信息请求"),
  SUB_SYSTEM_FIND_DCN(6, "子系统查询DCN")
  ;

  private int code;
  private String message;

  OperateEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
