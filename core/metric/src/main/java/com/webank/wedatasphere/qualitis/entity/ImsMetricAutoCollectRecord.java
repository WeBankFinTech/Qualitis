package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_auto_collect_record")
public class ImsMetricAutoCollectRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cluster_name")
  private String clusterName;
  @Column(name = "table_name")
  private String tableName;
  @Column(name = "db_name")
  private String dbName;
  @Column(name = "proxy_user")
  private String proxyUser;
  @Column(name = "status")
  private Integer status;
  @Column(name = "create_date", updatable = false)
  private String createDate;
  @Column(name = "modify_date")
  private String modifyDate;

  public ImsMetricAutoCollectRecord() {
    // do nothing
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public String getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(String modifyDate) {
    this.modifyDate = modifyDate;
  }

  @Override
  public String toString() {
    return "ImsMetricAutoCollectRecord{" +
            "id=" + id +
            ", clusterName='" + clusterName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", status=" + status +
            ", createDate='" + createDate + '\'' +
            ", modifyDate='" + modifyDate + '\'' +
            '}';
  }
}
