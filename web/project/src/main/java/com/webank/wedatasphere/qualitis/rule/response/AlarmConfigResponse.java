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
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonAlarmConfigRequest;

/**
 * @author howeye
 */
public class AlarmConfigResponse extends AbstractCommonAlarmConfigRequest {
    @JsonProperty("alarm_config_id")
    private Long id;
    @JsonProperty("output_meta_id")
    private Long outputMetaId;
    @JsonProperty("output_meta_name")
    private String outputMetaName;

    @JsonProperty("file_output_name")
    private Integer fileOutputName;

    @JsonProperty("file_output_unit")
    private Integer fileOutputUnit;

    @JsonProperty("rule_metric_id")
    private Long ruleMetricId;

    public AlarmConfigResponse() {
        // Default Constructor
    }

    public AlarmConfigResponse(AlarmConfig alarmConfig) {
        this.id = alarmConfig.getId();
        this.outputMetaId = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getId() : null;
        this.outputMetaName = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getOutputName() : null;
        super.setCheckTemplate(alarmConfig.getCheckTemplate());
        super.setCompareType(alarmConfig.getCompareType());
        super.setThreshold(alarmConfig.getThreshold());
        this.ruleMetricId = alarmConfig.getRuleMetric() == null ? -1 : alarmConfig.getRuleMetric().getId();
        super.setRuleMetricName(alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getName());
        super.setRuleMetricEnCode(alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getEnCode());
        super.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
        super.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
    }

    public AlarmConfigResponse(AlarmConfig alarmConfig, Integer ruleType) {
        this.id = alarmConfig.getId();
        this.outputMetaId = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getId() : null;
        this.outputMetaName = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getOutputName() : null;
        if (ruleType.equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            this.fileOutputName = alarmConfig.getFileOutputName();
            this.fileOutputUnit = alarmConfig.getFileOutputUnit();
        } else if (ruleType.equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            this.outputMetaId = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getId() : null;
            this.outputMetaName = alarmConfig.getTemplateOutputMeta() != null ? alarmConfig.getTemplateOutputMeta().getOutputName() : null;
        }
        super.setCheckTemplate(alarmConfig.getCheckTemplate());
        super.setCompareType(alarmConfig.getCompareType());
        super.setThreshold(alarmConfig.getThreshold());
        this.ruleMetricId = alarmConfig.getRuleMetric() == null ? -1 : alarmConfig.getRuleMetric().getId();
        super.setRuleMetricName(alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getName());
        super.setRuleMetricEnCode(alarmConfig.getRuleMetric() == null ? "" : alarmConfig.getRuleMetric().getEnCode());
        super.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
        super.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
        super.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
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

}
