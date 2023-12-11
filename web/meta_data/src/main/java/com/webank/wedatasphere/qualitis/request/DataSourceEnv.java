package com.webank.wedatasphere.qualitis.request;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-02 10:49
 * @description
 */
public class DataSourceEnv {

    /**
     * 环境ID
     */
    private Long id;
    /**
     * 环境名称
     */
    private String envName;
    /**
     * 环境描述
     */
    private String envDesc;
    /**
     * 数据库名称
     */
    private String database;
    /**
     * 非共享登录认证信息
     */
    private ConnectParams connectParams;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public ConnectParams getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(ConnectParams connectParams) {
        this.connectParams = connectParams;
    }
}
