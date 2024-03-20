package com.webank.wedatasphere.qualitis.metadata.response.datasource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-30 15:19
 * @description
 */
public class LinkisDataSourceInfoDetail {

    private Long id;
    /** Data source name */
    private String dataSourceName;

    /** Data source description */
    private String dataSourceDesc;

    /** ID of data source type */
    private Long dataSourceTypeId;

    /** Identify from creator */
    private String createIdentify;

    /** System name from creator */
    private String createSystem;
    /** Connection parameters */
    private Map<String, Object> connectParams = new HashMap<>();

    /** Create time */
    private Date createTime;

    /** Modify time */
    private Date modifyTime;

    /** Modify user */
    private String modifyUser;

    private String createUser;

    private String labels;

    private Long versionId;

    private Long publishedVersionId;

    private boolean expire;

    /** Data source type entity */
    private LinkisDataSourceTypeDetail dataSourceType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getCreateIdentify() {
        return createIdentify;
    }

    public void setCreateIdentify(String createIdentify) {
        this.createIdentify = createIdentify;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getPublishedVersionId() {
        return publishedVersionId;
    }

    public void setPublishedVersionId(Long publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }

    public LinkisDataSourceTypeDetail getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(LinkisDataSourceTypeDetail dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
