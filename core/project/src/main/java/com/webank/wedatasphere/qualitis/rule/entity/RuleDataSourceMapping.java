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
import javax.persistence.*;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_datasource_mapping")
public class RuleDataSourceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "left_statement", length = 3000)
    private String leftStatement;
    private Integer operation;
    @Column(name = "right_statement", length = 3000)
    private String rightStatement;
    /**
     * separated by comma
     */
    @Column(name = "left_column_names", length = 2000)
    private String leftColumnNames;
    @Column(name = "right_column_names", length = 2000)
    private String rightColumnNames;
    @Column(name = "left_column_types", length = 2000)
    private String leftColumnTypes;
    @Column(name = "right_column_types", length = 2000)
    private String rightColumnTypes;

    @ManyToOne
    private Rule rule;

    public RuleDataSourceMapping() {
    }

    public String getLeftColumnTypes() {
        return leftColumnTypes;
    }

    public void setLeftColumnTypes(String leftColumnTypes) {
        this.leftColumnTypes = leftColumnTypes;
    }

    public String getRightColumnTypes() {
        return rightColumnTypes;
    }

    public void setRightColumnTypes(String rightColumnTypes) {
        this.rightColumnTypes = rightColumnTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeftStatement() {
        return leftStatement;
    }

    public void setLeftStatement(String leftStatement) {
        this.leftStatement = leftStatement;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getRightStatement() {
        return rightStatement;
    }

    public void setRightStatement(String rightStatement) {
        this.rightStatement = rightStatement;
    }

    public String getLeftColumnNames() {
        return leftColumnNames;
    }

    public void setLeftColumnNames(String leftColumnNames) {
        this.leftColumnNames = leftColumnNames;
    }

    public String getRightColumnNames() {
        return rightColumnNames;
    }

    public void setRightColumnNames(String rightColumnNames) {
        this.rightColumnNames = rightColumnNames;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        RuleDataSourceMapping that = (RuleDataSourceMapping) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
