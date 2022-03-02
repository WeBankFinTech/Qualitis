package com.webank.wedatasphere.qualitis.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author allenzhou
 */
@Entity
@Table(name = "qualitis_rule_metric_type_config")
public class RuleMetricTypeConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "cn_name")
  private String cnName;
  @Column(name = "en_name")
  private String enName;

  public RuleMetricTypeConfig() {
  }

  public RuleMetricTypeConfig(String cnName, String enName) {
    this.cnName = cnName;
    this.enName = enName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCnName() {
    return cnName;
  }

  public void setCnName(String cnName) {
    this.cnName = cnName;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleMetricTypeConfig that = (RuleMetricTypeConfig) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(cnName, that.cnName) &&
        Objects.equals(enName, that.enName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, cnName, enName);
  }
}
