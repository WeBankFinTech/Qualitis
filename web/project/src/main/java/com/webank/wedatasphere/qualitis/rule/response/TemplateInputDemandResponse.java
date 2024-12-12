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
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class TemplateInputDemandResponse {

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
    @JsonProperty("field_type")
    private Integer fieldType;
    @JsonProperty("rule_arguments")
    private List<RuleArgumentResponse> ruleArgumentResponse;
    @JsonProperty("sql_display_response")
    private SqlDisplayResponse sqlDisplayResponse;
    @JsonProperty("template_output")
    private List<TemplateOutputMetaSimpleResponse> templateOutput;

    public TemplateInputDemandResponse() {
    }

    public TemplateInputDemandResponse(Template template, Integer templateType) {
        if (templateType.equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            this.templateId = template.getId();
            this.clusterNum = template.getClusterNum();
            this.dbNum = template.getDbNum();
            this.tableNum = template.getTableNum();
            this.fieldNum = template.getFieldNum();
            this.sqlDisplayResponse = new SqlDisplayResponse(template.getShowSql());
            this.templateOutput = new ArrayList<>();
            for (TemplateOutputMeta templateOutputMeta : template.getTemplateOutputMetas()) {
                templateOutput.add(new TemplateOutputMetaSimpleResponse(templateOutputMeta));
            }

            if (template.getChildTemplate() != null) {
                for (TemplateOutputMeta templateOutputMeta : template.getChildTemplate().getTemplateOutputMetas()) {
                    templateOutput.add(new TemplateOutputMetaSimpleResponse(templateOutputMeta));
                }
            }
        }
    }

    public TemplateInputDemandResponse(Template template, RegexpExprMapperRepository regexpExprMapperRepository) {
        this.templateId = template.getId();
        this.clusterNum = template.getClusterNum();
        this.dbNum = template.getDbNum();
        this.tableNum = template.getTableNum();
        this.fieldNum = template.getFieldNum();
        sqlDisplayResponse = new SqlDisplayResponse(template, regexpExprMapperRepository);
        ruleArgumentResponse = new ArrayList<>();
        templateOutput = new ArrayList<>();
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                ruleArgumentResponse.add(new RuleArgumentResponse(templateMidTableInputMeta));
            }
            if (templateMidTableInputMeta.getFieldType() != null) {
                this.fieldType = templateMidTableInputMeta.getFieldType();
            }
        }

        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : template.getStatisticAction()) {
            if (TemplateStatisticsUtil.shouldResponse(templateStatisticsInputMeta)) {
                ruleArgumentResponse.add(new RuleArgumentResponse(templateStatisticsInputMeta));
            }
        }

        for (TemplateOutputMeta templateOutputMeta : template.getTemplateOutputMetas()) {
            templateOutput.add(new TemplateOutputMetaSimpleResponse(templateOutputMeta));
        }
        // Add child template
        if(template.getChildTemplate() != null){
            for (TemplateOutputMeta templateOutputMeta : template.getChildTemplate().getTemplateOutputMetas()) {
                templateOutput.add(new TemplateOutputMetaSimpleResponse(templateOutputMeta));
            }
        }
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

    public List<RuleArgumentResponse> getRuleArgumentResponse() {
        return ruleArgumentResponse;
    }

    public void setRuleArgumentResponse(List<RuleArgumentResponse> ruleArgumentResponse) {
        this.ruleArgumentResponse = ruleArgumentResponse;
    }

    public SqlDisplayResponse getSqlDisplayResponse() {
        return sqlDisplayResponse;
    }

    public void setSqlDisplayResponse(SqlDisplayResponse sqlDisplayResponse) {
        this.sqlDisplayResponse = sqlDisplayResponse;
    }

    public List<TemplateOutputMetaSimpleResponse> getTemplateOutput() {
        return templateOutput;
    }

    public void setTemplateOutput(List<TemplateOutputMetaSimpleResponse> templateOutput) {
        this.templateOutput = templateOutput;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }
}
