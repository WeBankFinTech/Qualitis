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
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class RuleTemplateResponse {
    @JsonProperty("template_id")
    private Long ruleTemplateId;
    @JsonProperty("template_name")
    private String ruleTemplateName;
    @JsonProperty("cluster_num")
    private Integer clusterNum;
    @JsonProperty("db_num")
    private Integer dbNum;
    @JsonProperty("table_num")
    private Integer tableNum;
    @JsonProperty("field_num")
    private Integer fieldNum;
    @JsonProperty("datasource_type")
    private List<Integer> datasourceType;
    @JsonProperty("action_type")
    private Integer actionType;
    @JsonProperty("save_mid_table")
    private Boolean saveMidTable;
    @JsonProperty("mid_table_action")
    private String midTableAction;
    @JsonProperty("template_type")
    private Integer templateType;
    @JsonProperty("template_level")
    private Integer templateLevel;
    @JsonProperty("creator")
    private String creator;

    @JsonProperty("template_output_meta")
    private List<TemplateOutputMetaResponse> templateOutputMetaResponses;
    @JsonProperty("template_mid_table_input_meta")
    private List<TemplateMidTableInputMetaResponse> templateMidTableInputMetaResponses;
    @JsonProperty("template_statistics_input_meta")
    private List<TemplateStatisticsInputMetaResponse> templateStatisticsInputMetaResponses;

    public RuleTemplateResponse() {
    }

    public RuleTemplateResponse(Template template) {
        this.ruleTemplateId = template.getId();
        this.ruleTemplateName = template.getName();
        this.templateType = template.getTemplateType();
        this.templateLevel = template.getLevel();
        this.saveMidTable = template.getSaveMidTable();
        if (template.getCreateUser() != null) {
            this.creator = template.getCreateUser().getUserName();
        }
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public Integer getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(Integer clusterNum) {
        this.clusterNum = clusterNum;
    }

    public Integer getDbNum() {
        return dbNum;
    }

    public void setDbNum(Integer dbNum) {
        this.dbNum = dbNum;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public void setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
    }

    public Integer getFieldNum() {
        return fieldNum;
    }

    public void setFieldNum(Integer fieldNum) {
        this.fieldNum = fieldNum;
    }

    public List<Integer> getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(List<Integer> datasourceType) {
        this.datasourceType = datasourceType;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public String getMidTableAction() {
        return midTableAction;
    }

    public void setMidTableAction(String midTableAction) {
        this.midTableAction = midTableAction;
    }


    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public Integer getTemplateLevel() {
        return templateLevel;
    }

    public void setTemplateLevel(Integer templateLevel) {
        this.templateLevel = templateLevel;
    }

    public List<TemplateOutputMetaResponse> getTemplateOutputMetaResponses() {
        return templateOutputMetaResponses;
    }

    public void setTemplateOutputMetaResponses(
        List<TemplateOutputMetaResponse> templateOutputMetaResponses) {
        this.templateOutputMetaResponses = templateOutputMetaResponses;
    }

    public List<TemplateMidTableInputMetaResponse> getTemplateMidTableInputMetaResponses() {
        return templateMidTableInputMetaResponses;
    }

    public void setTemplateMidTableInputMetaResponses(
        List<TemplateMidTableInputMetaResponse> templateMidTableInputMetaResponses) {
        this.templateMidTableInputMetaResponses = templateMidTableInputMetaResponses;
    }

    public List<TemplateStatisticsInputMetaResponse> getTemplateStatisticsInputMetaResponses() {
        return templateStatisticsInputMetaResponses;
    }

    public void setTemplateStatisticsInputMetaResponses(
        List<TemplateStatisticsInputMetaResponse> templateStatisticsInputMetaResponses) {
        this.templateStatisticsInputMetaResponses = templateStatisticsInputMetaResponses;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "RuleTemplateResponse{" +
                "ruleTemplateId=" + ruleTemplateId +
                ", ruleTemplateName='" + ruleTemplateName + '\'' +
                '}';
    }
}
