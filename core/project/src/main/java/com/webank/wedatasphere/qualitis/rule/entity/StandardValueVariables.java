package com.webank.wedatasphere.qualitis.rule.entity;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-10 14:38
 * @description
 */
@Entity
@Table(name = "qualitis_standard_value_variables")
public class StandardValueVariables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "standard_value_version_id")
    private Long standardValueVersionId;

    @Column(name = "standard_value_version_en_name")
    private String standardValueVersionEnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getStandardValueVersionId() {
        return standardValueVersionId;
    }

    public void setStandardValueVersionId(Long standardValueVersionId) {
        this.standardValueVersionId = standardValueVersionId;
    }

    public String getStandardValueVersionEnName() {
        return standardValueVersionEnName;
    }

    public void setStandardValueVersionEnName(String standardValueVersionEnName) {
        this.standardValueVersionEnName = standardValueVersionEnName;
    }
}
