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
import java.util.Map;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_rule_variable")
public class RuleVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Rule rule;
    @Column(name = "input_action_step")
    private Integer inputActionStep;
    @ManyToOne
    private TemplateStatisticsInputMeta templateStatisticsInputMeta;
    @ManyToOne
    private TemplateMidTableInputMeta templateMidTableInputMeta;
    @Column(length = 500)
    private String value;
    @Column(name = "origin_value", length = 100)
    private String originValue;
    @Column(length = 50, name = "cluster_name")
    private String clusterName;
    @Column(length = 50, name = "db_name")
    private String dbName;
    @Column(length = 50, name = "table_name")
    private String tableName;

    public RuleVariable() {
    }

    public RuleVariable(Rule rule, Integer inputActionStep, TemplateMidTableInputMeta midTableInputMeta,
                        TemplateStatisticsInputMeta statisticsInputMeta , Map<String, String> autoAdaptValue) {
        this.rule = rule;
        this.inputActionStep = inputActionStep;
        this.templateMidTableInputMeta = midTableInputMeta;
        this.templateStatisticsInputMeta = statisticsInputMeta;
        this.value = autoAdaptValue.get("value");
        this.clusterName = autoAdaptValue.get("clusterName");
        this.dbName = autoAdaptValue.get("dbName");
        this.tableName = autoAdaptValue.get("tableName");
        this.originValue = autoAdaptValue.get("originValue");
    }

    public RuleVariable(Rule rule, Integer inputActionStep, TemplateMidTableInputMeta midTableInputMeta,
                        TemplateStatisticsInputMeta statisticsInputMeta , String value, String clusterName, String dbName, String tableName) {
        this.rule = rule;
        this.inputActionStep = inputActionStep;
        this.templateMidTableInputMeta = midTableInputMeta;
        this.templateStatisticsInputMeta = statisticsInputMeta;
        this.value = value;
        this.clusterName = clusterName;
        this.dbName = dbName;
        this.tableName = tableName;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getInputActionStep() {
        return inputActionStep;
    }

    public void setInputActionStep(Integer inputActionStep) {
        this.inputActionStep = inputActionStep;
    }

    public TemplateStatisticsInputMeta getTemplateStatisticsInputMeta() {
        return templateStatisticsInputMeta;
    }

    public void setTemplateStatisticsInputMeta(TemplateStatisticsInputMeta templateStatisticsInputMeta) {
        this.templateStatisticsInputMeta = templateStatisticsInputMeta;
    }

    public String getOriginValue() {
        return originValue;
    }

    public void setOriginValue(String originValue) {
        this.originValue = originValue;
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
