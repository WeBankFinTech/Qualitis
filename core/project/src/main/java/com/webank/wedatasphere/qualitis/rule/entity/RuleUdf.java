package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author allenzhou@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_udf")
public class RuleUdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "udf_name")
    private String udfName;

    @ManyToOne
    @JsonIgnore
    private Rule rule;

    @OneToOne
    @JsonIgnore
    private RuleDataSource ruleDatasource;

    public RuleUdf() {
    }

    public RuleUdf(String udfName, Rule ruleInDb) {
        this.udfName = udfName;
        this.rule = ruleInDb;
    }

    public RuleDataSource getRuleDatasource() {
        return ruleDatasource;
    }

    public void setRuleDatasource(RuleDataSource ruleDatasource) {
        this.ruleDatasource = ruleDatasource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUdfName() {
        return udfName;
    }

    public void setUdfName(String udfName) {
        this.udfName = udfName;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
