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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import javax.persistence.Column;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class AlarmConfigResponse {
    @JsonProperty("alarm_config_id")
    private Long id;
    @JsonProperty("output_meta_id")
    private Long outputMetaId;
    @JsonProperty("output_meta_name")
    private String outputMetaName;
    @JsonProperty("check_template")
    private Integer checkTemplate;
    @JsonProperty("compare_type")
    private Integer compareType;
    private Double threshold;

    @JsonProperty("file_output_name")
    private Integer fileOutputName;

    @JsonProperty("file_output_unit")
    private Integer fileOutputUnit;

    @JsonProperty("rule_metric_id")
    private Long ruleMetricId;

    @JsonProperty("rule_metric_en_code")
    private String ruleMetricEnCode;

    @JsonProperty("upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;

    @JsonProperty("upload_abnormal_value")
    private Boolean uploadAbnormalValue;

    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    public AlarmConfigResponse() {
    }

    public AlarmConfigResponse(AlarmConfig alarmConfig) {
        this.id = alarmConfig.getId();
        this.outputMetaId = alarmConfig.getTemplateOutputMeta().getId();
        this.outputMetaName = alarmConfig.getTemplateOutputMeta().getOutputName();
        this.checkTemplate = alarmConfig.getCheckTemplate();
        this.compareType = alarmConfig.getCompareType();
        this.threshold = alarmConfig.getThreshold();
        this.ruleMetricId = alarmConfig.getRuleMetric() == null ? -1 : alarmConfig.getRuleMetric().getId();
        this.ruleMetricEnCode = alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getEnCode();
        this.uploadRuleMetricValue = alarmConfig.getUploadRuleMetricValue();
        this.uploadAbnormalValue = alarmConfig.getUploadAbnormalValue();
    }

    public AlarmConfigResponse(AlarmConfig alarmConfig, Integer type) {
        this.id = alarmConfig.getId();
        if (type.equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            this.fileOutputName = alarmConfig.getFileOutputName();
            this.fileOutputUnit = alarmConfig.getFileOutputUnit();
        } else if (type.equals(RuleTypeEnum.CUSTOM_RULE.getCode())){
            this.outputMetaId = alarmConfig.getTemplateOutputMeta().getId();
            this.outputMetaName = alarmConfig.getTemplateOutputMeta().getOutputName();
        }
        this.checkTemplate = alarmConfig.getCheckTemplate();
        this.compareType = alarmConfig.getCompareType();
        this.threshold = alarmConfig.getThreshold();
        this.ruleMetricId = alarmConfig.getRuleMetric() == null ? -1 : alarmConfig.getRuleMetric().getId();
        this.ruleMetricEnCode = alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getEnCode();
        this.uploadAbnormalValue = alarmConfig.getUploadAbnormalValue();
        this.uploadRuleMetricValue = alarmConfig.getUploadRuleMetricValue();
        this.deleteFailCheckResult = alarmConfig.getDeleteFailCheckResult();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOutputMetaId() {
        return outputMetaId;
    }

    public void setOutputMetaId(Long outputMetaId) {
        this.outputMetaId = outputMetaId;
    }

    public String getOutputMetaName() {
        return outputMetaName;
    }

    public void setOutputMetaName(String outputMetaName) {
        this.outputMetaName = outputMetaName;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
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

    public Long getRuleMetricId() {
        return ruleMetricId;
    }

    public void setRuleMetricId(Long ruleMetricId) {
        this.ruleMetricId = ruleMetricId;
    }

    public String getRuleMetricEnCode() {
        return ruleMetricEnCode;
    }

    public void setRuleMetricEnCode(String ruleMetricEnCode) {
        this.ruleMetricEnCode = ruleMetricEnCode;
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
}
