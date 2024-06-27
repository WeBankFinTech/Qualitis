package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-19 18:12
 * @description
 */
public class OuterDataSourceDcnRequest {

    @JsonProperty("username")
    private String username;
    @JsonProperty("datasource_name_list")
    private List<String> datasourceNameList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getDatasourceNameList() {
        return datasourceNameList;
    }

    public void setDatasourceNameList(List<String> datasourceNameList) {
        this.datasourceNameList = datasourceNameList;
    }
}
