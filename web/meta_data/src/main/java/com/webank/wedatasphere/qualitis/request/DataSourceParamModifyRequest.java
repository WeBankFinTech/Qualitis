package com.webank.wedatasphere.qualitis.request;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 11:55
 */
public class DataSourceParamModifyRequest {
    private String comment;
    /**
     * 录入方式（1-手动录入，2-自动录入）
     */
    private Integer inputType;
    /**
     * 认证方式（1-共享，2-非共享）
     */
    private Integer verifyType;
    private List<String> envIdArray;
    private List<DataSourceEnv> dataSourceEnvs;
    private ConnectParams connectParams;

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

    public List<String> getEnvIdArray() {
        return envIdArray;
    }

    public void setEnvIdArray(List<String> envIdArray) {
        this.envIdArray = envIdArray;
    }

    public List<DataSourceEnv> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<DataSourceEnv> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }

    public DataSourceParamModifyRequest() {
        // Do nothing.
    }

    public DataSourceParamModifyRequest(String comment, ConnectParams connectParams) {
        this.comment = comment;
        this.connectParams = connectParams;
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

    @Override
    public String toString() {
        return "DataSourceParamModifyRequest{" +
                "comment='" + comment + '\'' +
                ", inputType=" + inputType +
                ", verifyType=" + verifyType +
                ", envIdArray=" + envIdArray +
                ", dataSourceEnvs=" + dataSourceEnvs +
                ", connectParams=" + connectParams +
                '}';
    }
}
