package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-16 15:12
 * @description
 */
public class AddMetricCollectRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    private String database;
    @JsonProperty("datasource_type")
    private Integer datasourceType;
    private String table;
    @JsonProperty("proxy_user")
    private String proxyUser;
    private String partition;
    @JsonProperty("collect_configs")
    private List<AddMetricCollectConfigRequest> collectConfigRequests;
    @JsonProperty("enum_check_string")
    private String enumCheckString;

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public List<AddMetricCollectConfigRequest> getCollectConfigRequests() {
        return collectConfigRequests;
    }

    public void setCollectConfigRequests(List<AddMetricCollectConfigRequest> collectConfigRequests) {
        this.collectConfigRequests = collectConfigRequests;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkString(this.clusterName, "cluster_name");
        CommonChecker.checkString(this.database, "database");
        CommonChecker.checkString(this.table, "table");
        CommonChecker.checkObject(this.datasourceType, "datasource_type");
        CommonChecker.checkString(this.proxyUser, "proxy_user");
    }

    public String getEnumCheckString() {
        return enumCheckString;
    }

    public void setEnumCheckString(String enumCheckString) {
        this.enumCheckString = enumCheckString;
    }

    @Override
    public String toString() {
        return "AddMetricCollectRequest{" +
                "clusterName='" + clusterName + '\'' +
                ", database='" + database + '\'' +
                ", datasourceType=" + datasourceType +
                ", table='" + table + '\'' +
                ", proxyUser='" + proxyUser + '\'' +
                ", partition='" + partition + '\'' +
                ", collectConfigRequests=" + collectConfigRequests +
                ", enumCheckString='" + enumCheckString + '\'' +
                '}';
    }
}
