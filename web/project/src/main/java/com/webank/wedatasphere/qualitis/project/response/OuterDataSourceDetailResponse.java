package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-20 10:43
 * @description
 */
public class OuterDataSourceDetailResponse {

    @JsonProperty("data_source_id")
    private Long linkisDataSourceId;
    @JsonProperty("data_source_type_name")
    private String dataSourceTypeName;
    @JsonProperty("operate_user")
    private String username;
    @JsonProperty("sub_system")
    private String subSystem;
    @JsonProperty("input_type")
    private Integer inputType;
    @JsonProperty("verify_type")
    private Integer verifyType;
    @JsonProperty("connect_params")
    private LinkisConnectParamsRequest connectParams;
    @JsonProperty("data_source_name")
    private String dataSourceName;
    @JsonProperty("data_source_desc")
    private String dataSourceDesc;
    private String labels;
    @JsonProperty("version_id")
    private Long versionId;
    @JsonProperty("published_version_id")
    private Long publishedVersionId;
    @JsonProperty("create_system")
    private String createSystem = "Qualitis";
    @Column(name = "dcn_range_type")
    private String dcnRangeType;

    @JsonProperty("datasource_envs")
    private List<OuterDataSourceEnvResponse> dataSourceEnvs;

    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoResponse> visibilityDepartmentList;

    public OuterDataSourceDetailResponse(){
//        Doing something
    }

    public OuterDataSourceDetailResponse(LinkisDataSource linkisDataSource) {
        BeanUtils.copyProperties(linkisDataSource, this);
    }

    public String getDcnRangeType() {
        return dcnRangeType;
    }

    public void setDcnRangeType(String dcnRangeType) {
        this.dcnRangeType = dcnRangeType;
    }

    public List<DepartmentSubInfoResponse> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoResponse> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public Long getPublishedVersionId() {
        return publishedVersionId;
    }

    public void setPublishedVersionId(Long publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LinkisConnectParamsRequest getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(LinkisConnectParamsRequest connectParams) {
        this.connectParams = connectParams;
    }

    public String getDataSourceTypeName() {
        return dataSourceTypeName;
    }

    public void setDataSourceTypeName(String dataSourceTypeName) {
        this.dataSourceTypeName = dataSourceTypeName;
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

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public List<OuterDataSourceEnvResponse> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<OuterDataSourceEnvResponse> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }
}
