package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-08 14:50
 * @description
 */
public class LinkisDataSourceResponse {

    private String dataSourceDesc;
    private String linkisDataSourceName;
    private Long dataSourceTypeId;
    private String devDepartmentName;
    private String opsDepartmentName;
    private String envs;
    private String labels;
    private String dcnSequence;
    private String createSystem;
    private Map<String, Object> connectParams;
    private List<DepartmentSubInfoResponse> dataVisibilityList;

    public LinkisDataSourceResponse(){
//        Doing something
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public List<DepartmentSubInfoResponse> getDataVisibilityList() {
        return dataVisibilityList;
    }

    public void setDataVisibilityList(List<DepartmentSubInfoResponse> dataVisibilityList) {
        this.dataVisibilityList = dataVisibilityList;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public String getEnvs() {
        return envs;
    }

    public void setEnvs(String envs) {
        this.envs = envs;
    }

    public String getDcnSequence() {
        return dcnSequence;
    }

    public void setDcnSequence(String dcnSequence) {
        this.dcnSequence = dcnSequence;
    }

    public String getLinkisDataSourceName() {
        return linkisDataSourceName;
    }

    public void setLinkisDataSourceName(String linkisDataSourceName) {
        this.linkisDataSourceName = linkisDataSourceName;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
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

}
