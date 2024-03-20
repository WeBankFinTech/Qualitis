package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class CheckConditionsResponse {

    @JsonProperty("output_meta_id")
    private Long outputMetaId;
    @JsonProperty("output_meta_name")
    private String outputMetaName;

    @JsonProperty("check_template")
    private Integer checkTemplate;
    @JsonProperty("compare_type")
    private Integer compareType;
    private Double threshold;


    public Long getOutputMetaId() {
        return outputMetaId;
    }

    public void setOutputMetaId(Long outputMetaId) {
        this.outputMetaId = outputMetaId;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getOutputMetaName() {
        return outputMetaName;
    }

    public void setOutputMetaName(String outputMetaName) {
        this.outputMetaName = outputMetaName;
    }
}
