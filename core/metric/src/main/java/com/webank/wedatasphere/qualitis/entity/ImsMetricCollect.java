package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_imsmetric_collect")
public class ImsMetricCollect {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "collect_age", columnDefinition = "TINYINT(5)")
  private Integer collectAge;
  @Column(name = "execution_parameters_name")
  private String executionParametersName;
  @Column(name = "exec_ip")
  private String execIp;
  @Column(name = "column_name")
  private String columnName;
  @Column(name = "column_type")
  private String columnType;
  @Column(name = "table_name")
  private String tableName;
  @Column(name = "db_name")
  private String dbName;
  @Column(name = "cluster_name")
  private String clusterName;
  @Column(name = "filter")
  private String filter;
  @Column(name = "proxy_user")
  private String proxyUser;
  /**
   * datasource type, such as hive, mysql, tdsql, kafka, fps
   */
  @Column(name = "datasource_type")
  private Integer datasourceType;
  @Column(name = "create_user")
  private String createUser;
  @Column(name = "modify_user")
  private String modifyUser;
  @Column(name = "create_time")
  private String createTime;
  @Column(name = "modify_time")
  private String modifyTime;
  @Column(name = "self_calcu_unit")
  private String selfCalcuUnit;

  public ImsMetricCollect() {
    // do nothing
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getModifyUser() {
    return modifyUser;
  }

  public void setModifyUser(String modifyUser) {
    this.modifyUser = modifyUser;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public Integer getCollectAge() {
    return collectAge;
  }

  public void setCollectAge(Integer collectAge) {
    this.collectAge = collectAge;
  }

  public String getExecutionParametersName() {
    return executionParametersName;
  }

  public void setExecutionParametersName(String executionParametersName) {
    this.executionParametersName = executionParametersName;
  }

  public String getExecIp() {
    return execIp;
  }

  public void setExecIp(String execIp) {
    this.execIp = execIp;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
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

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public Integer getDatasourceType() {
    return datasourceType;
  }

  public void setDatasourceType(Integer datasourceType) {
    this.datasourceType = datasourceType;
  }

  public String getSelfCalcuUnit() {
    return selfCalcuUnit;
  }

  public void setSelfCalcuUnit(String selfCalcuUnit) {
    this.selfCalcuUnit = selfCalcuUnit;
  }

  @Override
  public String toString() {
    return "ImsMetricCollect{" +
            "id=" + id +
            ", templateId=" + templateId +
            ", collectAge=" + collectAge +
            ", executionParametersName='" + executionParametersName + '\'' +
            ", execIp='" + execIp + '\'' +
            ", columnName='" + columnName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", filter='" + filter + '\'' +
            ", proxyUser='" + proxyUser + '\'' +
            ", datasourceType=" + datasourceType +
            '}';
  }
}
