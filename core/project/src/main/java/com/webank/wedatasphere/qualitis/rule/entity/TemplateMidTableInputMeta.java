/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author howeye
 */
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Entity(name = "qualitis_template_mid_table_input_meta")
public class TemplateMidTableInputMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, updatable = false)
    private String name;

    @Column(length = 30)
    private String placeholder;

    @ManyToOne
    @JsonIgnore
    private Template template;

    /**
     * Input type, such as field, fixed_value, database, table, field_concat...
     */
    @Column(name = "input_type")
    private Integer inputType;

    /**
     * If input type equal to field, field type should be provided
     */
    @Column(name = "field_type")
    private Integer fieldType;

    /**
     * If input type euqal to regexp, return value from request or database
     */
    @Column(name = "replace_by_request")
    private Boolean replaceByRequest;

    /**
     * regexp type, such as: data, number, identity
     */
    @Column(name = "regexp_type")
    private Integer regexpType;

    @Column(name = "placeholder_description", length = 300, updatable = false)
    private String placeholderDescription;

    @Column(name = "concat_template", length = 3000)
    private String concatTemplate;

    @Column(name = "cn_name", length = 16)
    private String cnName;

    @Column(name = "en_name", length = 64)
    private String enName;

    @Column(name = "cn_description", length = 256)
    private String cnDescription;

    @Column(name = "en_description", length = 256)
    private String enDescription;

    @Column(name = "field_multiple_choice")
    private Boolean fieldMultipleChoice;

    @Column(name = "whether_standard_value")
    private Boolean whetherStandardValue;

    @Column(name = "whether_new_value")
    private Boolean whetherNewValue;

    public TemplateMidTableInputMeta() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getReplaceByRequest() {
        return replaceByRequest;
    }

    public void setReplaceByRequest(Boolean replaceByRequest) {
        this.replaceByRequest = replaceByRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        TemplateMidTableInputMeta that = (TemplateMidTableInputMeta) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getRegexpType() {
        return regexpType;
    }

    public void setRegexpType(Integer regexpType) {
        this.regexpType = regexpType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceholderDescription() {
        return placeholderDescription;
    }

    public void setPlaceholderDescription(String placeholderDescription) {
        this.placeholderDescription = placeholderDescription;
    }

    public String getConcatTemplate() {
        return concatTemplate;
    }

    public void setConcatTemplate(String concatTemplate) {
        this.concatTemplate = concatTemplate;
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

    public String getCnDescription() {
        return cnDescription;
    }

    public void setCnDescription(String cnDescription) {
        this.cnDescription = cnDescription;
    }

    public String getEnDescription() {
        return enDescription;
    }

    public void setEnDescription(String enDescription) {
        this.enDescription = enDescription;
    }

    public Boolean getFieldMultipleChoice() {
        return fieldMultipleChoice;
    }

    public void setFieldMultipleChoice(Boolean fieldMultipleChoice) {
        this.fieldMultipleChoice = fieldMultipleChoice;
    }

    public Boolean getWhetherStandardValue() {
        return whetherStandardValue;
    }

    public void setWhetherStandardValue(Boolean whetherStandardValue) {
        this.whetherStandardValue = whetherStandardValue;
    }

    public Boolean getWhetherNewValue() {
        return whetherNewValue;
    }

    public void setWhetherNewValue(Boolean whetherNewValue) {
        this.whetherNewValue = whetherNewValue;
    }


}
