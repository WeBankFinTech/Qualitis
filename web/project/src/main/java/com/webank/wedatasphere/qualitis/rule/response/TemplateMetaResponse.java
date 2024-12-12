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
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class TemplateMetaResponse {

    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("cluster_num")
    private Integer clusterNum;
    @JsonProperty("db_num")
    private Integer dbNum;
    @JsonProperty("table_num")
    private Integer tableNum;
    @JsonProperty("field_num")
    private Integer fieldNum;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("template_type")
    private Integer templateType;
    @JsonProperty("template_action_type")
    private Integer templateActionType;
    @JsonProperty("template_datasource_type")
    private List<Integer> dataSourceType;
    @JsonProperty("save_mid_table")
    private Boolean saveMidTable;
    @JsonProperty("show_sql")
    private String showSql;
    @JsonProperty("output_name")
    private List<String> templateOutputName;

    public TemplateMetaResponse() {
    }

    public TemplateMetaResponse(Template template, List<TemplateMidTableInputMeta> templateMidTableInputMetas, List<TemplateOutputMeta> templateOutputMetas, List<Integer> dataSourceTypes) {
        this.dataSourceType = dataSourceTypes;
        this.dbNum = template.getDbNum();
        this.templateId = template.getId();
        this.templateName = template.getName();
        this.tableNum = template.getTableNum();
        this.fieldNum = template.getFieldNum();
        this.clusterNum = template.getClusterNum();
        this.templateType = template.getTemplateType();
        this.templateActionType = template.getActionType();
        this.saveMidTable = template.getSaveMidTable();
        this.showSql = template.getMidTableAction();
        this.templateOutputName = new ArrayList<>();

        for (TemplateOutputMeta templateOutputMeta : templateOutputMetas) {
            templateOutputName.add(templateOutputMeta.getOutputName());
        }
    }

    public TemplateMetaResponse(Template template, List<TemplateOutputMeta> templateOutputMetas, List<Integer> dataSourceTypes) {
        this.dataSourceType = dataSourceTypes;

        this.dbNum = template.getDbNum();
        this.templateId = template.getId();
        this.templateName = template.getName();
        this.clusterNum = template.getClusterNum();
        this.tableNum = template.getTableNum();
        this.fieldNum = template.getFieldNum();
        this.templateType = template.getTemplateType();
        this.templateActionType = template.getActionType();
        this.saveMidTable = template.getSaveMidTable();
        this.showSql = template.getMidTableAction();
        this.templateOutputName = new ArrayList<>();

        for (TemplateOutputMeta templateOutputMeta : templateOutputMetas) {
            templateOutputName.add(templateOutputMeta.getOutputName());
        }
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }


    public Integer getTemplateActionType() {
        return templateActionType;
    }

    public void setTemplateActionType(Integer templateActionType) {
        this.templateActionType = templateActionType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public List<Integer> getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(List<Integer> dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public List<String> getTemplateOutputName() {
        return templateOutputName;
    }

    public void setTemplateOutputName(List<String> templateOutputName) {
        this.templateOutputName = templateOutputName;
    }
}
