package com.webank.wedatasphere.qualitis.request;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/23 16:10
 */
public class DeleteServiceInfoRequest {
    private Long id;

    public DeleteServiceInfoRequest() {
        // Do nothing.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
