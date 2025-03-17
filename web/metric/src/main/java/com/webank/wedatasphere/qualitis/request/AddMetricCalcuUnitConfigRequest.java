package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-17 15:45
 * @description
 */
public class AddMetricCalcuUnitConfigRequest {

    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("template_name")
    private String templateName;
    private List<String> columns;
    private Map<String, String> columnSelfCalcuUnitMap;


    public AddMetricCalcuUnitConfigRequest() {
//        doing nothing
    }

    // for analysis result of enum
    public AddMetricCalcuUnitConfigRequest(Long enumNumTemplateId, List<String> enumString) {
        this.templateId = enumNumTemplateId;
        this.columns = enumString;
    }

    // for analysis result of date, int, bigint, decimal
    public AddMetricCalcuUnitConfigRequest(Long templateId, Map<String, String> current) {
        this.templateId = templateId;
        this.columnSelfCalcuUnitMap = current;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Map<String, String> getColumnSelfCalcuUnitMap() {
        return columnSelfCalcuUnitMap;
    }

    public void setColumnSelfCalcuUnitMap(Map<String, String> columnSelfCalcuUnitMap) {
        this.columnSelfCalcuUnitMap = columnSelfCalcuUnitMap;
    }
}
