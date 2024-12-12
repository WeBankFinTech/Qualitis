package com.webank.wedatasphere.qualitis.request;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/24 15:40
 */
public class ModifyServiceInfoRequest {
    private Long id;
    private Integer status;

    @JsonProperty(value = "collect_status")
    private Integer collectStatus;

    public ModifyServiceInfoRequest() {
        // Do nothing.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(Integer collectStatus) {
        this.collectStatus = collectStatus;
    }
}
