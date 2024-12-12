package com.webank.wedatasphere.qualitis.request;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 10:40
 */
public class DataSourceConnectRequest {
    private Long id;
    private String createSystem;

    private Long createTime;
    private Long modifyTime;
    private String createUser;
    private String modifyUser;

    private String dataSourceDesc;
    private String dataSourceName;
    private Long dataSourceTypeId;

    private Boolean expire;
    private Long versionId;
    private Long publishedVersionId;
    private ConnectParams connectParams;
    private DataSourceType dataSourceType;
    /**
     * 录入方式（1-手动录入，2-自动录入）
     */
    private Integer inputType;
    /**
     * 认证方式（1-共享，2-非共享）
     */
    private Integer verifyType;
    /**
     * 环境配置
     */
    private List<DataSourceEnv> dataSourceEnvs;

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

    public List<DataSourceEnv> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<DataSourceEnv> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
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

    public Boolean getExpire() {
        return expire;
    }

    public void setExpire(Boolean expire) {
        this.expire = expire;
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

    public ConnectParams getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(ConnectParams connectParams) {
        this.connectParams = connectParams;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
