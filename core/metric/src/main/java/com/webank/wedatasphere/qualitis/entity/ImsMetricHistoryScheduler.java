package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_imsmetric_history_scheduler")
public class ImsMetricHistoryScheduler {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "submit_time")
  private String submitTime;
  @Column(name = "table_name")
  private String tableName;
  @Column(name = "db_name")
  private String dbName;

  public ImsMetricHistoryScheduler() {
    //do nothing
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(String submitTime) {
    this.submitTime = submitTime;
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

  @Override
  public String toString() {
    return "ImsMetricScheduler{" +
            "id=" + id +
            ", submitTime='" + submitTime + '\'' +
            ", tableName='" + tableName + '\'' +
            ", dbName='" + dbName + '\'' +
            '}';
  }

}
