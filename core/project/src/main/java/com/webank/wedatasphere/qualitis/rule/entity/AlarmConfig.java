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

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_rule_alarm_config")
public class AlarmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Rule rule;
    @ManyToOne
    private TemplateOutputMeta templateOutputMeta;

    @Column(name = "check_template")
    private Integer checkTemplate;

    private Double threshold;
    @Column(name = "compare_type")
    private Integer compareType;

    public AlarmConfig() {
        // Default Constructor
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

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public TemplateOutputMeta getTemplateOutputMeta() {
        return templateOutputMeta;
    }

    public void setTemplateOutputMeta(TemplateOutputMeta templateOutputMeta) {
        this.templateOutputMeta = templateOutputMeta;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        AlarmConfig that = (AlarmConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AlarmConfig{" +
                "id=" + id +
                ", rule=" + rule.getId() +
                ", templateOutputMeta=" + templateOutputMeta.getOutputName() +
                ", checkTemplate=" + checkTemplate +
                ", threshold=" + threshold +
                ", compareType=" + compareType +
                '}';
    }
}
