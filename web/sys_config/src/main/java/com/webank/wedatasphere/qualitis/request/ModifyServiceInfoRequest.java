package com.webank.wedatasphere.qualitis.request;


/**
 * @author allenzhou@webank.com
 * @date 2022/2/24 15:40
 */
public class ModifyServiceInfoRequest {
    private Long id;
    private Integer status;

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
}
