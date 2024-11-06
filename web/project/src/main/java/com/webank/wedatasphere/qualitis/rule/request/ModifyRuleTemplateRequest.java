package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author allenzhou
 */
public class ModifyRuleTemplateRequest {
    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("template_name")
    private String templateName;

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

    @JsonProperty("template_output_meta")
    private List<TemplateOutputMetaRequest> templateOutputMetaRequests;
    @JsonProperty("template_mid_table_input_meta")
    private List<TemplateMidTableInputMetaRequest> templateMidTableInputMetaRequests;
    @JsonProperty("template_statistics_input_meta")
    private List<TemplateStatisticsInputMetaRequest> templateStatisticsInputMetaRequests;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoRequest> visibilityDepartmentNameList;

    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("verification_level")
    private Integer verificationLevel;
    @JsonProperty("verification_type")
    private Integer verificationType;

//    @JsonProperty("exception_database")
//    private Boolean exceptionDatabase;

    @JsonProperty("filter_fields")
    private Boolean filterFields;
    @JsonProperty("whether_using_functions")
    private Boolean whetherUsingFunctions;
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

    public ModifyRuleTemplateRequest() {
    }

    public ModifyRuleTemplateRequest(Long templateId, String templateName, Integer clusterNum, Integer dbNum, Integer tableNum,
        Integer fieldNum, List<Integer> datasourceType, Integer actionType, Boolean saveMidTable, String midTableAction) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.clusterNum = clusterNum;
        this.dbNum = dbNum;
        this.tableNum = tableNum;
        this.fieldNum = fieldNum;
        this.datasourceType = datasourceType;
        this.actionType = actionType;
        this.saveMidTable = saveMidTable;
        this.midTableAction = midTableAction;
    }

    public static AddRuleTemplateRequest checkRequest(ModifyRuleTemplateRequest request)
        throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getTemplateId(), "templateId");
        AddRuleTemplateRequest addRuleTemplateRequest = new AddRuleTemplateRequest();
        BeanUtils.copyProperties(request, addRuleTemplateRequest);
//        AddRuleTemplateRequest.checkRequest(addRuleTemplateRequest, true);
        return addRuleTemplateRequest;
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

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentNameList() {
        return visibilityDepartmentNameList;
    }

    public void setVisibilityDepartmentNameList(List<DepartmentSubInfoRequest> visibilityDepartmentNameList) {
        this.visibilityDepartmentNameList = visibilityDepartmentNameList;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public List<TemplateOutputMetaRequest> getTemplateOutputMetaRequests() {
        return templateOutputMetaRequests;
    }

    public void setTemplateOutputMetaRequests(
        List<TemplateOutputMetaRequest> templateOutputMetaRequests) {
        this.templateOutputMetaRequests = templateOutputMetaRequests;
    }

    public List<TemplateMidTableInputMetaRequest> getTemplateMidTableInputMetaRequests() {
        return templateMidTableInputMetaRequests;
    }

    public void setTemplateMidTableInputMetaRequests(
        List<TemplateMidTableInputMetaRequest> templateMidTableInputMetaRequests) {
        this.templateMidTableInputMetaRequests = templateMidTableInputMetaRequests;
    }

    public List<TemplateStatisticsInputMetaRequest> getTemplateStatisticsInputMetaRequests() {
        return templateStatisticsInputMetaRequests;
    }

    public void setTemplateStatisticsInputMetaRequests(
        List<TemplateStatisticsInputMetaRequest> templateStatisticsInputMetaRequests) {
        this.templateStatisticsInputMetaRequests = templateStatisticsInputMetaRequests;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
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

    public List<String> getUdfFunctionName() {
        return udfFunctionName;
    }

    public void setUdfFunctionName(List<String> udfFunctionName) {
        this.udfFunctionName = udfFunctionName;
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

    public String getSamplingContent() {
        return samplingContent;
    }

    public void setSamplingContent(String samplingContent) {
        this.samplingContent = samplingContent;
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

    @Override
    public String toString() {
        return "ModifyRuleTemplateRequest{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", clusterNum=" + clusterNum +
                ", dbNum=" + dbNum +
                ", tableNum=" + tableNum +
                ", fieldNum=" + fieldNum +
                ", datasourceType=" + datasourceType +
                ", actionType=" + actionType +
                ", saveMidTable=" + saveMidTable +
                ", midTableAction='" + midTableAction + '\'' +
                ", templateType=" + templateType +
                ", templateOutputMetaRequests=" + templateOutputMetaRequests +
                ", templateMidTableInputMetaRequests=" + templateMidTableInputMetaRequests +
                ", templateStatisticsInputMetaRequests=" + templateStatisticsInputMetaRequests +
                ", devDepartmentName='" + devDepartmentName + '\'' +
                ", opsDepartmentName='" + opsDepartmentName + '\'' +
                ", devDepartmentId=" + devDepartmentId +
                ", opsDepartmentId=" + opsDepartmentId +
                ", visibilityDepartmentNameList=" + visibilityDepartmentNameList +
                ", enName='" + enName + '\'' +
                ", description='" + description + '\'' +
                ", verificationLevel=" + verificationLevel +
                ", verificationType=" + verificationType +
                ", filterFields=" + filterFields +
                ", whetherUsingFunctions=" + whetherUsingFunctions +
                ", udfFunctionName=" + udfFunctionName +
                ", countFunctionName='" + countFunctionName + '\'' +
                ", countFunctioAlias='" + countFunctioAlias + '\'' +
                ", verificationCnName='" + verificationCnName + '\'' +
                ", verificationEnName='" + verificationEnName + '\'' +
                ", samplingContent='" + samplingContent + '\'' +
                ", namingMethod=" + namingMethod +
                ", whetherSolidification=" + whetherSolidification +
                ", checkTemplate=" + checkTemplate +
                ", majorType='" + majorType + '\'' +
                ", templateNumber='" + templateNumber + '\'' +
                ", customZhCode='" + customZhCode + '\'' +
                '}';
    }
}
