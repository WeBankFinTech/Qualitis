package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author allenzhou
 */
//@Entity
//@Table(name = "qualitis_calcu_unit")
public class CalcuUnit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;
  @Column(name = "sql_action")
  private String sqlAction;

  @Column(name = "udf_id")
  private Long udfId;

  @Column(name = "create_user", length = 50)
  private String createUser;
  @Column(name = "create_time", length = 25)
  private String createTime;
  @Column(name = "modify_user", length = 50)
  private String modifyUser;
  @Column(name = "modify_time", length = 25)
  private String modifyTime;

  public CalcuUnit() {
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

  public String getSqlAction() {
    return sqlAction;
  }

  public void setSqlAction(String sqlAction) {
    this.sqlAction = sqlAction;
  }

  public Long getUdfId() {
    return udfId;
  }

  public void setUdfId(Long udfId) {
    this.udfId = udfId;
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
    return "CalcuUnit{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", sqlAction='" + sqlAction + '\'' +
            ", udfId=" + udfId +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            '}';
  }
}
