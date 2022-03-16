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
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import javax.persistence.*;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_alarm_config")
public class AlarmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Rule rule;
    @ManyToOne
    private TemplateOutputMeta templateOutputMeta;

    @Column(name = "file_output_name")
    private Integer fileOutputName;

    @Column(name = "file_output_unit")
    private Integer fileOutputUnit;

    @Column(name = "check_template")
    private Integer checkTemplate;

    private Double threshold;
    @Column(name = "compare_type")
    private Integer compareType;

    @ManyToOne
    private RuleMetric ruleMetric;

    @Column(name = "upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;

    @Column(name = "upload_abnormal_value")
    private Boolean uploadAbnormalValue;

    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

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

    public Integer getFileOutputName() {
        return fileOutputName;
    }

    public void setFileOutputName(Integer fileOutputName) {
        this.fileOutputName = fileOutputName;
    }

    public Integer getFileOutputUnit() {
        return fileOutputUnit;
    }

    public void setFileOutputUnit(Integer fileOutputUnit) {
        this.fileOutputUnit = fileOutputUnit;
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

    public RuleMetric getRuleMetric() {
        return ruleMetric;
    }

    public void setRuleMetric(RuleMetric ruleMetric) {
        this.ruleMetric = ruleMetric;
    }

    public Boolean getUploadRuleMetricValue() {
        return uploadRuleMetricValue;
    }

    public void setUploadRuleMetricValue(Boolean uploadRuleMetricValue) {
        this.uploadRuleMetricValue = uploadRuleMetricValue;
    }

    public Boolean getUploadAbnormalValue() {
        return uploadAbnormalValue;
    }

    public void setUploadAbnormalValue(Boolean uploadAbnormalValue) {
        this.uploadAbnormalValue = uploadAbnormalValue;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
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
            ", rule=" + rule +
            ", templateOutputMeta=" + templateOutputMeta +
            ", fileOutputName=" + fileOutputName +
            ", fileOutputUnit=" + fileOutputUnit +
            ", checkTemplate=" + checkTemplate +
            ", threshold=" + threshold +
            ", compareType=" + compareType +
            '}';
    }
}
