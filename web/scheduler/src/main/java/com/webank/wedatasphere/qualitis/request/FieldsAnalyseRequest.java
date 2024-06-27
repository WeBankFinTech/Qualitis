package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FieldsAnalyseRequest {

    @JsonProperty("ruleIdList")
    private List<Long> ruleIdList;

    @JsonProperty("dataDateList")
    private List<Integer> dataDateList;

    @JsonProperty("analyseType")
    private Integer analyseType;

    @JsonProperty("partitionAttrs")
    private String partitionAttrs;

    public List< Long > getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List< Long > ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public List< Integer > getDataDateList() {
        return dataDateList;
    }

    public void setDataDateList(List< Integer > dataDateList) {
        this.dataDateList = dataDateList;
    }

    public Integer getAnalyseType() {
        return analyseType;
    }

    public void setAnalyseType(Integer analyseType) {
        this.analyseType = analyseType;
    }

    public String getPartitionAttrs() {
        return partitionAttrs;
    }

    public void setPartitionAttrs(String partitionAttrs) {
        this.partitionAttrs = partitionAttrs;
    }
}
