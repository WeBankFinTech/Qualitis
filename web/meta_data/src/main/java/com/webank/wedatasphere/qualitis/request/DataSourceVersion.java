package com.webank.wedatasphere.qualitis.request;

import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-02 15:13
 * @description
 */
public class DataSourceVersion {

    private Long datasourceId;
    private Map<String, Object> connectParams;

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }
}
