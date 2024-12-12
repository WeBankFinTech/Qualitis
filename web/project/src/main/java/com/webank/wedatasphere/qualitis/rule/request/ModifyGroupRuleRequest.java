package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/6 15:40
 */
public class ModifyGroupRuleRequest extends AbstractCommonRequest {

    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;
    @JsonProperty("file_alarm_variable")
    private List<FileAlarmConfigRequest> fileAlarmVariable;
    @JsonProperty("col_names")
    private List<DataSourceColumnRequest> colNames;

    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;
    @JsonProperty("filter")
    private String filter;
    @JsonProperty("standard_value_version_id")
    private Long standardValueVersionId;

    public ModifyGroupRuleRequest() {
//        do something
    }

    public List<FileAlarmConfigRequest> getFileAlarmVariable() {
        return fileAlarmVariable;
    }

    public void setFileAlarmVariable(List<FileAlarmConfigRequest> fileAlarmVariable) {
        this.fileAlarmVariable = fileAlarmVariable;
    }

    public List<DataSourceColumnRequest> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnRequest> colNames) {
        this.colNames = colNames;
    }


    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public Long getStandardValueVersionId() {
        return standardValueVersionId;
    }

    public void setStandardValueVersionId(Long standardValueVersionId) {
        this.standardValueVersionId = standardValueVersionId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }
}
