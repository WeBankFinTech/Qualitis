package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_imsmetric_scheduler")
public class ImsMetricScheduler {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "exec_freq")
  private String execFreq;
  @Column(name = "table_name")
  private String tableName;
  @Column(name = "db_name")
  private String dbName;
  @Column(name = "partition_value")
  private String partition;

  public ImsMetricScheduler() {
    //do nothing
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getExecFreq() {
    return execFreq;
  }

  public void setExecFreq(String execFreq) {
    this.execFreq = execFreq;
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

  public String getPartition() {
    return partition;
  }

  public void setPartition(String partition) {
    this.partition = partition;
  }

  @Override
  public String toString() {
    return "ImsMetricScheduler{" +
            "id=" + id +
            ", execFreq='" + execFreq + '\'' +
            ", tableName='" + tableName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", partition='" + partition + '\'' +
            '}';
  }
}
