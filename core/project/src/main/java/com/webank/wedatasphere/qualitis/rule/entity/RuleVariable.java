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
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_variable")
public class RuleVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Rule rule;
    @ManyToOne
    private TemplateMidTableInputMeta templateMidTableInputMeta;
    @ManyToOne
    private TemplateStatisticsInputMeta templateStatisticsInputMeta;

    @Column(name = "input_action_step")
    private Integer inputActionStep;
    @Column(name = "value", columnDefinition = "MEDIUMTEXT")
    private String value;
    @Column(name = "origin_value", columnDefinition = "MEDIUMTEXT")
    private String originValue;
    @Column(length = 50, name = "cluster_name")
    private String clusterName;
    @Column(length = 200, name = "table_name")
    private String tableName;
    @Column(length = 200, name = "db_name")
    private String dbName;

    public RuleVariable() {
    }

    public RuleVariable(Rule rule, Integer inputActionStep, TemplateMidTableInputMeta midTableInputMeta,
                        TemplateStatisticsInputMeta statisticsInputMeta , Map<String, String> autoAdaptValue) {
        this.rule = rule;
        this.inputActionStep = inputActionStep;
        this.templateMidTableInputMeta = midTableInputMeta;
        this.templateStatisticsInputMeta = statisticsInputMeta;

        this.value = autoAdaptValue.get("value");
        this.dbName = autoAdaptValue.get("dbName");
        this.tableName = autoAdaptValue.get("tableName");
        this.clusterName = autoAdaptValue.get("clusterName");
        this.originValue = autoAdaptValue.get("originValue");
    }

    public RuleVariable(Rule rule, Integer inputActionStep, TemplateMidTableInputMeta midTableInputMeta,
                        TemplateStatisticsInputMeta statisticsInputMeta , String value, String clusterName, String dbName, String tableName) {
        this.rule = rule;
        this.inputActionStep = inputActionStep;
        this.templateMidTableInputMeta = midTableInputMeta;
        this.templateStatisticsInputMeta = statisticsInputMeta;

        this.value = value;
        this.dbName = dbName;
        this.tableName = tableName;
        this.clusterName = clusterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public TemplateMidTableInputMeta getTemplateMidTableInputMeta() {
        return templateMidTableInputMeta;
    }

    public void setTemplateMidTableInputMeta(TemplateMidTableInputMeta templateMidTableInputMeta) {
        this.templateMidTableInputMeta = templateMidTableInputMeta;
    }

    public TemplateStatisticsInputMeta getTemplateStatisticsInputMeta() {
        return templateStatisticsInputMeta;
    }

    public void setTemplateStatisticsInputMeta(TemplateStatisticsInputMeta templateStatisticsInputMeta) {
        this.templateStatisticsInputMeta = templateStatisticsInputMeta;
    }

    public Integer getInputActionStep() {
        return inputActionStep;
    }

    public void setInputActionStep(Integer inputActionStep) {
        this.inputActionStep = inputActionStep;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOriginValue() {
        return originValue;
    }

    public void setOriginValue(String originValue) {
        this.originValue = originValue;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        RuleVariable that = (RuleVariable) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RuleVariable{" +
                "id=" + id +
                ", rule=" + rule +
                ", inputActionStep=" + inputActionStep +
                ", templateStatisticsInputMeta=" + templateStatisticsInputMeta +
                ", templateMidTableInputMeta=" + templateMidTableInputMeta +
                ", value='" + value + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
