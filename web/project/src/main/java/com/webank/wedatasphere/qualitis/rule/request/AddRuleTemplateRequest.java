package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import java.util.List;

/**
 * @author allenzhou
 */
public class AddRuleTemplateRequest {
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

    public AddRuleTemplateRequest() {
        actionType = TemplateActionTypeEnum.SQL.getCode();
    }

    public AddRuleTemplateRequest(String templateName, List<Integer> datasourceType, Integer actionType, Boolean saveMidTable,
        String midTableAction) {
        this.templateName = templateName;
        this.datasourceType = datasourceType;
        this.actionType = actionType;
        this.saveMidTable = saveMidTable;
        this.midTableAction = midTableAction;
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

    @Override
    public String toString() {
        return "AddRuleTemplateRequest{" +
            "templateName='" + templateName + '\'' +
            ", clusterNum=" + clusterNum +
            ", dbNum=" + dbNum +
            ", tableNum=" + tableNum +
            ", fieldNum=" + fieldNum +
            ", datasourceType=" + datasourceType +
            ", actionType=" + actionType +
            ", saveMidTable=" + saveMidTable +
            ", midTableAction='" + midTableAction + '\'' +
            ", templateType=" + templateType +
            '}';
    }

    public static void checkRequest(AddRuleTemplateRequest request, boolean modify) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "AddDefaultRuleTemplateRequest");
        CommonChecker.checkString(request.getTemplateName(), "templateName");
        CommonChecker.checkObject(request.getClusterNum(), "clusterNum");
        CommonChecker.checkObject(request.getDbNum(), "dbNum");
        CommonChecker.checkObject(request.getTableNum(), "tableNum");
        CommonChecker.checkObject(request.getFieldNum(), "fieldNum");
        CommonChecker.checkObject(request.getTemplateType(), "templateType");
        CommonChecker.checkListMinSize(request.getTemplateOutputMetaRequests(), 1, "TemplateOutputMetaRequests");
        CommonChecker.checkListMinSize(request.getTemplateMidTableInputMetaRequests(), 1, "TemplateMidTableInputMetaRequests");
        CommonChecker.checkListMinSize(request.getTemplateStatisticsInputMetaRequests(), 1, "TemplateStatisticsInputMetaRequests");

        for (TemplateOutputMetaRequest templateOutputMetaRequest : request.getTemplateOutputMetaRequests()) {
            TemplateOutputMetaRequest.checkRequest(templateOutputMetaRequest);
        }

        for (TemplateMidTableInputMetaRequest templateMidTableInputMetaRequest : request.getTemplateMidTableInputMetaRequests()) {
            TemplateMidTableInputMetaRequest.checkRequest(templateMidTableInputMetaRequest);
        }

        for (TemplateStatisticsInputMetaRequest templateStatisticsInputMetaRequest : request.getTemplateStatisticsInputMetaRequests()) {
            TemplateStatisticsInputMetaRequest.checkRequest(templateStatisticsInputMetaRequest);
        }

    }
}
