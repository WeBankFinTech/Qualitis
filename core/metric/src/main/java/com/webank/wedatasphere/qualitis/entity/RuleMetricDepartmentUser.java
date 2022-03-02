package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author allenzhou
 */
@Entity
@Table(name = "qualitis_rule_metric_department_user")
public class RuleMetricDepartmentUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Department department;

  @ManyToOne
  private User user;

  @OneToOne
  private RuleMetric ruleMetric;

  public RuleMetricDepartmentUser() {
  }

  public RuleMetricDepartmentUser(Department department,
      User user, RuleMetric ruleMetric) {
    this.department = department;
    this.user = user;
    this.ruleMetric = ruleMetric;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public RuleMetric getRuleMetric() {
    return ruleMetric;
  }

  public void setRuleMetric(RuleMetric ruleMetric) {
    this.ruleMetric = ruleMetric;
  }
}
