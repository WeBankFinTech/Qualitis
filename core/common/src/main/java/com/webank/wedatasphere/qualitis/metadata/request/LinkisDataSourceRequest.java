package com.webank.wedatasphere.qualitis.metadata.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-12 11:00
 * @description
 */
public class LinkisDataSourceRequest {

    @JsonIgnore
    private String subSystem;
    @JsonIgnore
    private Long linkisDataSourceId;
    @JsonIgnore
    private Integer inputType;
    @JsonIgnore
    private Integer verifyType;
    private Long dataSourceTypeId;
    private String dataSourceName;
    private String dataSourceDesc;
    private String labels;
    private String createSystem = "Qualitis";
    private Map<String, Object> connectParams = new HashMap<>();
    @JsonIgnore
    private LinkisConnectParamsRequest sharedConnectParams;
    @JsonIgnore
    private String dcnRangeType;
    @JsonIgnore
    private List<LinkisDataSourceEnvRequest> dataSourceEnvs;

    public List<LinkisDataSourceEnvRequest> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<LinkisDataSourceEnvRequest> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }

    public String getDcnRangeType() {
        return dcnRangeType;
    }

    public void setDcnRangeType(String dcnRangeType) {
        this.dcnRangeType = dcnRangeType;
    }

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public LinkisConnectParamsRequest getSharedConnectParams() {
        return sharedConnectParams;
    }

    public void setSharedConnectParams(LinkisConnectParamsRequest sharedConnectParams) {
        this.sharedConnectParams = sharedConnectParams;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(Integer verifyType) {
        this.verifyType = verifyType;
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

}
