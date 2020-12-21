package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.BeanUtils;

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
    private Integer datasourceType;
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

    public ModifyRuleTemplateRequest() {
    }

    public ModifyRuleTemplateRequest(Long templateId, String templateName, Integer clusterNum, Integer dbNum, Integer tableNum,
        Integer fieldNum, Integer datasourceType, Integer actionType, Boolean saveMidTable, String midTableAction) {
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

    public static AddRuleTemplateRequest checkRequest(ModifyRuleTemplateRequest request, String modifyDefaultRuleTemplateRequest)
        throws UnExpectedRequestException, InvocationTargetException, IllegalAccessException {
        CommonChecker.checkObject(request.getTemplateId(), "templateId");
        AddRuleTemplateRequest addRuleTemplateRequest = new AddRuleTemplateRequest();
        BeanUtils.copyProperties(request, addRuleTemplateRequest);
        AddRuleTemplateRequest.checkRequest(addRuleTemplateRequest, true);
        return addRuleTemplateRequest;
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

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
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
}
