package com.webank.wedatasphere.qualitis.rule.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@Table(name = "qualitis_standard_value_department_version")
public class StandardValueDepartmentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deptment_id")
    private Long deptmentId;

    @Column(name = "standard_value_version_id")
    private Long standardvalueversionId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptmentId() {
        return deptmentId;
    }

    public void setDeptmentId(Long deptmentId) {
        this.deptmentId = deptmentId;
    }

    public Long getStandardvalueversionId() {
        return standardvalueversionId;
    }

    public void setStandardvalueversionId(Long standardvalueversionId) {
        this.standardvalueversionId = standardvalueversionId;
    }
}
