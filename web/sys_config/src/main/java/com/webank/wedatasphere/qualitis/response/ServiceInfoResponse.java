package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/23 16:10
 */
public class ServiceInfoResponse {
    private Long id;
    private String ip;
    private Long updatingApplicationNum;
    private Integer status;

    @JsonProperty("tenant_user_name")
    private String tenantUserName;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("modify_time")
    private String modifyTime;

    @JsonProperty("collect_status")
    private Integer collectStatus;

    public ServiceInfoResponse() {
    }

    public ServiceInfoResponse(ServiceInfo serviceInfo) {
        this.id = serviceInfo.getId();
        this.ip = serviceInfo.getIp();
        this.status = serviceInfo.getStatus();
        this.updatingApplicationNum = serviceInfo.getUpdatingApplicationNum();

        if (serviceInfo.getTenantUser() != null) {
            this.tenantUserName = serviceInfo.getTenantUser().getTenantName();
        }
        this.createUser = serviceInfo.getCreateUser();
        this.createTime = serviceInfo.getCreateTime();
        this.modifyUser = serviceInfo.getModifyUser();
        this.modifyTime = serviceInfo.getModifyTime();
        this.collectStatus = serviceInfo.getCollectStatus();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUpdatingApplicationNum() {
        return updatingApplicationNum;
    }

    public void setUpdatingApplicationNum(Long updatingApplicationNum) {
        this.updatingApplicationNum = updatingApplicationNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTenantUserName() {
        return tenantUserName;
    }

    public void setTenantUserName(String tenantUserName) {
        this.tenantUserName = tenantUserName;
    }

    public Integer getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(Integer collectStatus) {
        this.collectStatus = collectStatus;
    }
}
