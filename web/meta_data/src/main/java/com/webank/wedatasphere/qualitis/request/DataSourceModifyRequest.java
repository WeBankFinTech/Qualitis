package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 11:35
 */
public class DataSourceModifyRequest {
    private List<String> labels;
    private String createSystem;

    private String dataSourceDesc;
    private String dataSourceName;
    private Long dataSourceTypeId;
    private String subSystem;
    /**
     * 共享登录认证信息
     */
    private ConnectParams connectParams;
    /**
     * 录入方式（1-手动录入，2-自动录入）
     */
    private Integer inputType;
    /**
     * 认证方式（1-共享，2-非共享）
     */
    private Integer verifyType;
    private List<String> dcnSequence;
    /**
     * 环境配置
     */
    private List<DataSourceEnv> dataSourceEnvs;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoRequest> visibilityDepartmentList;

    public List<String> getDcnSequence() {
        return dcnSequence;
    }

    public void setDcnSequence(List<String> dcnSequence) {
        this.dcnSequence = dcnSequence;
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

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoRequest> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public ConnectParams getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(ConnectParams connectParams) {
        this.connectParams = connectParams;
    }

    public Integer getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(Integer verifyType) {
        this.verifyType = verifyType;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public List<DataSourceEnv> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<DataSourceEnv> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }
}
