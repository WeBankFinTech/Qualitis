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

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_template_regexp_expr")
public class TemplateRegexpExpr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "regexp_type")
    private Integer regexpType;

    @Column(name = "key_name")
    private String keyName;

    @Column(name = "regexp_value")
    private String regexpValue;

    public TemplateRegexpExpr() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRegexpType() {
        return regexpType;
    }

    public void setRegexpType(Integer regexpType) {
        this.regexpType = regexpType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getRegexpValue() {
        return regexpValue;
    }

    public void setRegexpValue(String regexpValue) {
        this.regexpValue = regexpValue;
    }
}
