package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
@Entity
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
  @Column(name = "column_names")
  private String columnNames;
  @Column(name = "start_date")
  private String startDate;
  @Column(name = "end_date")
  private String endDate;
  /**
   * （0：未采集；1：已采集）
   */
  @Column(name = "collect_status")
  private Integer collectStatus;

  public ImsMetricHistoryScheduler() {
    //do nothing
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getCollectStatus() {
    return collectStatus;
  }

  public void setCollectStatus(Integer collectStatus) {
    this.collectStatus = collectStatus;
  }

  public String getColumnNames() {
    return columnNames;
  }

  public void setColumnNames(String columnNames) {
    this.columnNames = columnNames;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
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
