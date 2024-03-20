package com.webank.wedatasphere.qualitis.client.request;

import com.webank.wedatasphere.qualitis.client.constant.OperateEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 14:47
 */
public class OperateRequest {
  private String userAuthKey;
  private String type;
  private int startIndex;
  private int pageSize;
  private String action;
  private Boolean isPaging;
  private List<String> resultColumn;
  private Map<String, String> filter;

  public OperateRequest(int code) {
    if (code == OperateEnum.SUB_SYSTEM.getCode()) {
      type = "wb_subsystem";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(3);
      resultColumn.add("subsystem_id");
      resultColumn.add("subsystem_name");
      resultColumn.add("full_cn_name");
      resultColumn.add("devdept");
      resultColumn.add("busiResDept");
      resultColumn.add("pro_oper_group");
    } else if (code == OperateEnum.PRODUCT.getCode()) {
      type = "wb_product_cd";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(2);
      resultColumn.add("product_cd");
      resultColumn.add("cn_name");
    } else if (code == OperateEnum.DEPARTMENT.getCode()) {
      type = "wb_sec_level_dep";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(2);
      resultColumn.add("dep_name");
      resultColumn.add("dep_code");
    } else if (code == OperateEnum.DEV_DEPARTMENT.getCode()) {
      type = "wb_dev_oper_group";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(1);
      resultColumn.add("group_name");
    } else if (code == OperateEnum.OPS_DEPARTMENT.getCode()) {
      type = "wb_test_oper_group";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(1);
      resultColumn.add("group_name");
    } else if (code == OperateEnum.SUB_SYSTEM_FIND_DCN.getCode()) {
      filter = new HashMap<>();
      type = "DB_subsystem_tdsql";
      startIndex = 0;
      pageSize = Integer.MAX_VALUE;
      action = "select";
      isPaging = false;
      resultColumn = new ArrayList<>(1);
      resultColumn.add("db_name");
      resultColumn.add("idc");
      resultColumn.add("phy_set_name");
      resultColumn.add("set_type");
      resultColumn.add("vip");
      resultColumn.add("logic_area");
      resultColumn.add("gwport");
      resultColumn.add("dbinstance_name");
      resultColumn.add("clu_name");
      resultColumn.add("set_name");
      resultColumn.add("dcn_num");
      resultColumn.add("logic_dcn");
    }

  }

  public String getUserAuthKey() {
    return userAuthKey;
  }

  public void setUserAuthKey(String userAuthKey) {
    this.userAuthKey = userAuthKey;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Boolean getPaging() {
    return isPaging;
  }

  public void setPaging(Boolean paging) {
    isPaging = paging;
  }

  public List<String> getResultColumn() {
    return resultColumn;
  }

  public void setResultColumn(List<String> resultColumn) {
    this.resultColumn = resultColumn;
  }

  public Map<String, String> getFilter() {
    return filter;
  }

  public void setFilter(Map<String, String> filter) {
    this.filter = filter;
  }
}
