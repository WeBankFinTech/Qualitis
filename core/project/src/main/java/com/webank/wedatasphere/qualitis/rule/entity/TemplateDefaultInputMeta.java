package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Entity(name = "qualitis_template_default_input_meta")
public class TemplateDefaultInputMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type")
    private Integer type;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "placeholder_desc")
    private String placeholderDesc;

    @Column(name = "cn_name")
    private String cnName;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "cn_desc")
    private String cnDesc;

    @Column(name = "en_desc")
    private String enDesc;

    @Column(name = "support_fields")
    private Boolean supportFields;

    @Column(name = "support_standard")
    private Boolean supportStandard;

    @Column(name = "support_new_value")
    private Boolean supportNewValue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholderDesc() {
        return placeholderDesc;
    }

    public void setPlaceholderDesc(String placeholderDesc) {
        this.placeholderDesc = placeholderDesc;
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

    public String getCnDesc() {
        return cnDesc;
    }

    public void setCnDesc(String cnDesc) {
        this.cnDesc = cnDesc;
    }

    public String getEnDesc() {
        return enDesc;
    }

    public void setEnDesc(String enDesc) {
        this.enDesc = enDesc;
    }

    public Boolean getSupportFields() {
        return supportFields;
    }

    public void setSupportFields(Boolean supportFields) {
        this.supportFields = supportFields;
    }

    public Boolean getSupportStandard() {
        return supportStandard;
    }

    public void setSupportStandard(Boolean supportStandard) {
        this.supportStandard = supportStandard;
    }

    public Boolean getSupportNewValue() {
        return supportNewValue;
    }

    public void setSupportNewValue(Boolean supportNewValue) {
        this.supportNewValue = supportNewValue;
    }
}
