package com.webank.wedatasphere.qualitis.request;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 11:55
 */
public class DataSourceParamModifyRequest {
    private String comment;
    private ConnectParams connectParams;

    public DataSourceParamModifyRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ConnectParams getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(ConnectParams connectParams) {
        this.connectParams = connectParams;
    }
}
