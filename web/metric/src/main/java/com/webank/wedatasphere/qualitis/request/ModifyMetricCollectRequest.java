package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-14 11:15
 * @description
 */
public class ModifyMetricCollectRequest {

    @JsonProperty(value = "id", required = true)
    private Long id;
    @JsonProperty(value = "template_id", required = true)
    private Long  templateId;
    @JsonProperty("execution_parameters_name")
    private String executionParametersName;
    @JsonProperty(value = "column", required = true)
    private String column;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
