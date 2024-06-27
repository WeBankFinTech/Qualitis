package com.webank.wedatasphere.qualitis.response;

import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-07 15:05
 * @description
 */
public class DataSourceEnvResponse {

    private Long id;
    private String envName;
    private String dcnNum;
    private String logicArea;

    public DataSourceEnvResponse(LinkisDataSourceEnv linkisDataSourceEnv) {
        this.id = linkisDataSourceEnv.getEnvId();
        this.envName = linkisDataSourceEnv.getEnvName();
        this.dcnNum = linkisDataSourceEnv.getDcnNum();
        this.logicArea = linkisDataSourceEnv.getLogicArea();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDcnNum() {
        return dcnNum;
    }

    public void setDcnNum(String dcnNum) {
        this.dcnNum = dcnNum;
    }

    public String getLogicArea() {
        return logicArea;
    }

    public void setLogicArea(String logicArea) {
        this.logicArea = logicArea;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }
}
