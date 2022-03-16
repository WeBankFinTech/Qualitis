package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/6 18:28
 */
public class RuleMetricQueryRequest {
    @JsonProperty("sub_system_name")
    private String subSystemName;
    @JsonProperty("rule_metric_name")
    private String ruleMetricName;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("en_code")
    private String enCode;

    @JsonProperty("available")
    private Boolean available;

    private int page;
    private int size;

    public RuleMetricQueryRequest() {
        page = 0;
        size = 15;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEnCode() {
        return enCode;
    }

    public void setEnCode(String enCode) {
        this.enCode = enCode;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
