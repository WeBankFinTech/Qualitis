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
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateCheckLevelEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateCheckTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateFileTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateUdfDao;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUdf;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class RuleTemplateResponse {
    @JsonProperty("id")
    private Long id;
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
    @JsonProperty("creator")
    private String creator;
    @JsonProperty("create_id")
    private Long createId;
    @JsonProperty("template_type")
    private Integer templateType;
    @JsonProperty("template_level")
    private Integer templateLevel;

    @JsonProperty("template_output_meta")
    private List<TemplateOutputMetaResponse> templateOutputMetaResponses;
    @JsonProperty("template_mid_table_input_meta")
    private List<TemplateMidTableInputMetaResponse> templateMidTableInputMetaResponses;
    @JsonProperty("template_statistics_input_meta")
    private List<TemplateStatisticsInputMetaResponse> templateStatisticsInputMetaResponses;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoResponse> visibilityDepartmentNameList;
    @JsonProperty("is_editable")
    private boolean isEditable;

    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("filter_fields")
    private Boolean filterFields;
    @JsonProperty("whether_using_functions")
    private Boolean whetherUsingFunctions;
    @JsonProperty("description")
    private String description;
    @JsonProperty("verification_level")
    private Integer verificationLevel;
    @JsonProperty("verification_type")
    private Integer verificationType;
    @JsonProperty("verification_level_name")
    private String verificationLevelName;
    @JsonProperty("verification_type_name")
    private String verificationTypeName;
    @JsonProperty("exception_database")
    private Boolean exceptionDatabase;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("modify_name")
    private String modifyName;
    @JsonProperty("modify_id")
    private Long modifyId;
    @JsonProperty("udf_function_name")
    private List<String> udfFunctionName;

    @JsonProperty("count_function_name")
    private String countFunctionName;
    @JsonProperty("count_function_alias")
    private String countFunctioAlias;
    @JsonProperty("verification_cn_name")
    private String verificationCnName;
    @JsonProperty("verification_en_name")
    private String verificationEnName;
    @JsonProperty("sampling_content")
    private String samplingContent;
    @JsonProperty("sampling_content_name")
    private String samplingContentName;

    @JsonProperty("naming_method")
    private Integer namingMethod;
    @JsonProperty("whether_solidification")
    private Boolean whetherSolidification;
    @JsonProperty("check_template")
    private Integer checkTemplate;
    @JsonProperty("major_type")
    private String majorType;
    @JsonProperty("template_number")
    private String templateNumber;
    @JsonProperty("custom_zh_code")
    private String customZhCode;

    public RuleTemplateResponse() {
    }

    public RuleTemplateResponse(Template template) {
        this(template, false);
    }

    public RuleTemplateResponse(Template template, Boolean flag) {
        this.id = template.getId();
        this.ruleTemplateId = template.getId();
        this.ruleTemplateName = template.getName();
        this.templateType = template.getTemplateType();
        this.templateLevel = template.getLevel();
        this.saveMidTable = template.getSaveMidTable();
        this.creator = template.getCreateUser() != null ? template.getCreateUser().getUsername() : "";
        this.createId = template.getCreateUser() != null ? template.getCreateUser().getId() : null;
        this.modifyName = template.getModifyUser() != null ? template.getModifyUser().getUsername() : "";
        this.modifyId = template.getModifyUser() != null ? template.getModifyUser().getId() : null;

        this.actionType = template.getActionType();
        this.devDepartmentName = template.getDevDepartmentName();
        this.opsDepartmentName = template.getOpsDepartmentName();
        this.devDepartmentId = template.getDevDepartmentId();
        this.opsDepartmentId = template.getOpsDepartmentId();
        this.enName = template.getEnName();
        this.description = template.getDescription();
        this.verificationLevel = template.getVerificationLevel();
        this.verificationType = template.getVerificationType();
        this.verificationLevelName = template.getVerificationLevel() != null ? TemplateCheckLevelEnum.getMessage(template.getVerificationLevel()) : "";
        this.verificationTypeName = template.getVerificationType() != null ? TemplateCheckTypeEnum.getMessage(template.getVerificationType()) : "";
        this.exceptionDatabase = template.getExceptionDatabase();
        this.filterFields = template.getFilterFields();
        this.whetherUsingFunctions = template.getWhetherUsingFunctions();

        if (flag) {
            List<TemplateUdf> templateUdfFunctions = SpringContextHolder.getBean(TemplateUdfDao.class).findByRuleTemplate(template);
            this.udfFunctionName = templateUdfFunctions.stream().map(TemplateUdf::getName).collect(Collectors.toList());
            if (!template.getStatisticAction().isEmpty()) {
                this.countFunctionName = template.getStatisticAction().stream().map(TemplateStatisticsInputMeta::getFuncName).collect(Collectors.toList()).iterator().next();
                this.countFunctioAlias = template.getStatisticAction().stream().map(TemplateStatisticsInputMeta::getValue).collect(Collectors.toList()).iterator().next();
            }

        }

        this.verificationCnName = template.getVerificationCnName();
        this.verificationEnName = template.getVerificationEnName();

        this.createTime = template.getCreateTime();
        this.modifyTime = template.getModifyTime();
        if (RuleTemplateTypeEnum.FILE_COUSTOM.getCode().equals(template.getTemplateType()) && CollectionUtils.isNotEmpty(template.getTemplateOutputMetas())) {
            this.samplingContentName = template.getTemplateOutputMetas().stream().map(TemplateOutputMeta::getOutputName).collect(Collectors.toList()).iterator().next();
            this.samplingContent = TemplateFileTypeEnum.getCode(template.getTemplateOutputMetas().stream().map(TemplateOutputMeta::getOutputName).collect(Collectors.toList()).iterator().next()).toString();

        }

        this.namingMethod = template.getNamingMethod();
        this.whetherSolidification = template.getWhetherSolidification();
        this.checkTemplate = template.getCheckTemplate();

        this.majorType = template.getMajorType();
        this.templateNumber = template.getTemplateNumber();
        this.customZhCode = template.getCustomZhCode();
    }


    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public List<DepartmentSubInfoResponse> getVisibilityDepartmentNameList() {
        return visibilityDepartmentNameList;
    }

    public void setVisibilityDepartmentNameList(List<DepartmentSubInfoResponse> visibilityDepartmentNameList) {
        this.visibilityDepartmentNameList = visibilityDepartmentNameList;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(Integer verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    public Integer getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(Integer verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationLevelName() {
        return verificationLevelName;
    }

    public void setVerificationLevelName(String verificationLevelName) {
        this.verificationLevelName = verificationLevelName;
    }

    public String getVerificationTypeName() {
        return verificationTypeName;
    }

    public void setVerificationTypeName(String verificationTypeName) {
        this.verificationTypeName = verificationTypeName;
    }

    public Boolean getExceptionDatabase() {
        return exceptionDatabase;
    }

    public void setExceptionDatabase(Boolean exceptionDatabase) {
        this.exceptionDatabase = exceptionDatabase;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyName() {
        return modifyName;
    }

    public void setModifyName(String modifyName) {
        this.modifyName = modifyName;
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

    public Boolean getFilterFields() {
        return filterFields;
    }

    public void setFilterFields(Boolean filterFields) {
        this.filterFields = filterFields;
    }

    public Boolean getWhetherUsingFunctions() {
        return whetherUsingFunctions;
    }

    public void setWhetherUsingFunctions(Boolean whetherUsingFunctions) {
        this.whetherUsingFunctions = whetherUsingFunctions;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public List<String> getUdfFunctionName() {
        return udfFunctionName;
    }

    public void setUdfFunctionName(List<String> udfFunctionName) {
        this.udfFunctionName = udfFunctionName;
    }

    public String getCountFunctionName() {
        return countFunctionName;
    }

    public void setCountFunctionName(String countFunctionName) {
        this.countFunctionName = countFunctionName;
    }

    public String getCountFunctioAlias() {
        return countFunctioAlias;
    }

    public void setCountFunctioAlias(String countFunctioAlias) {
        this.countFunctioAlias = countFunctioAlias;
    }

    public String getVerificationCnName() {
        return verificationCnName;
    }

    public void setVerificationCnName(String verificationCnName) {
        this.verificationCnName = verificationCnName;
    }

    public String getVerificationEnName() {
        return verificationEnName;
    }

    public void setVerificationEnName(String verificationEnName) {
        this.verificationEnName = verificationEnName;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public String getSamplingContent() {
        return samplingContent;
    }

    public void setSamplingContent(String samplingContent) {
        this.samplingContent = samplingContent;
    }

    public String getSamplingContentName() {
        return samplingContentName;
    }

    public void setSamplingContentName(String samplingContentName) {
        this.samplingContentName = samplingContentName;
    }

    public Integer getNamingMethod() {
        return namingMethod;
    }

    public void setNamingMethod(Integer namingMethod) {
        this.namingMethod = namingMethod;
    }

    public Boolean getWhetherSolidification() {
        return whetherSolidification;
    }

    public void setWhetherSolidification(Boolean whetherSolidification) {
        this.whetherSolidification = whetherSolidification;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public String getMajorType() {
        return majorType;
    }

    public void setMajorType(String majorType) {
        this.majorType = majorType;
    }

    public String getTemplateNumber() {
        return templateNumber;
    }

    public void setTemplateNumber(String templateNumber) {
        this.templateNumber = templateNumber;
    }

    public String getCustomZhCode() {
        return customZhCode;
    }

    public void setCustomZhCode(String customZhCode) {
        this.customZhCode = customZhCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RuleTemplateResponse{" +
                "ruleTemplateId=" + ruleTemplateId +
                ", ruleTemplateName='" + ruleTemplateName + '\'' +
                '}';
    }
}
