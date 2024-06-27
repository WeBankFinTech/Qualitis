package com.webank.wedatasphere.qualitis.rule.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@Table(name = "qualitis_standard_value_action_version")
public class StandardValueActionVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private StandardValueVersion standardValueVersion;

    @Column(name = "action_range")
    private String actionRange;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StandardValueVersion getStandardValueVersion() {
        return standardValueVersion;
    }

    public void setStandardValueVersion(StandardValueVersion standardValueVersion) {
        this.standardValueVersion = standardValueVersion;
    }

    public String getActionRange() {
        return actionRange;
    }

    public void setActionRange(String actionRange) {
        this.actionRange = actionRange;
    }
}
