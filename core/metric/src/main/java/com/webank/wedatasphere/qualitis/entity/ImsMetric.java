package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_imsmetric")
public class ImsMetric {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;
  @Column(name = "identify_value")
  private String identifyValue;

  @Column(name = "metric_collect_id")
  private Long metricCollectId;

  @Column(name = "create_user", length = 50)
  private String createUser;
  @Column(name = "create_time", length = 25)
  private String createTime;
  @Column(name = "modify_user", length = 50)
  private String modifyUser;
  @Column(name = "modify_time", length = 25)
  private String modifyTime;

  public ImsMetric() {
    // do nothing
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdentifyValue() {
    return identifyValue;
  }

  public void setIdentifyValue(String identifyValue) {
    this.identifyValue = identifyValue;
  }

  public Long getMetricCollectId() {
    return metricCollectId;
  }

  public void setMetricCollectId(Long metricCollectId) {
    this.metricCollectId = metricCollectId;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getModifyUser() {
    return modifyUser;
  }

  public void setModifyUser(String modifyUser) {
    this.modifyUser = modifyUser;
  }

  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }

  @Override
  public String toString() {
    return "ImsMetric{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", identifyValue='" + identifyValue + '\'' +
            ", metricCollectId=" + metricCollectId +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            '}';
  }
}
