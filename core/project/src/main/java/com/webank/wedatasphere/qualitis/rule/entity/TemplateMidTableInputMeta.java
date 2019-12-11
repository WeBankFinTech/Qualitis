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

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * @author howeye
 */
@Table
@Entity(name = "qualitis_template_mid_table_input_meta")
public class TemplateMidTableInputMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50, updatable = false)
    private String name;

    @ManyToOne
    private Template template;

    @Column(length = 30)
    private String placeholder;

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


    @ManyToOne
    private TemplateMidTableInputMeta parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<TemplateMidTableInputMeta> children;


    @Column(name = "concat_template", length = 3000)
    private String concatTemplate;

    public TemplateMidTableInputMeta() {
        // Default Constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Set<TemplateMidTableInputMeta> getChildren() {
        return children;
    }

    public void setChildren(Set<TemplateMidTableInputMeta> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        TemplateMidTableInputMeta that = (TemplateMidTableInputMeta) o;
        return id == that.id;
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
}
