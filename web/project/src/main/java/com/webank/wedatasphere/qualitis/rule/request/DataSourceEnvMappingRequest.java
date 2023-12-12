package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2023/1/3 19:25
 */
public class DataSourceEnvMappingRequest {
    @JsonProperty("datasource_envs")
    List<DataSourceEnvRequest> dataSourceEnvRequests;

    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("db_alias_name")
    private String dbAliasName;

    public DataSourceEnvMappingRequest() {
        this.dataSourceEnvRequests = new ArrayList<>();
    }

    public DataSourceEnvMappingRequest(String key, List<RuleDataSourceEnv> ruleDataSourceEnvs) {
        this.dataSourceEnvRequests = new ArrayList<>();
        String[] dbAndTable = key.split(SpecCharEnum.PERIOD.getValue());
        this.dbName = dbAndTable[0];
        this.dbAliasName = dbAndTable[1];

        for (RuleDataSourceEnv env : ruleDataSourceEnvs) {
            DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest(env);
            this.dataSourceEnvRequests.add(dataSourceEnvRequest);
        }
    }

    public List<DataSourceEnvRequest> getDataSourceEnvRequests() {
        return dataSourceEnvRequests;
    }

    public void setDataSourceEnvRequests(List<DataSourceEnvRequest> dataSourceEnvRequests) {
        this.dataSourceEnvRequests = dataSourceEnvRequests;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbAliasName() {
        return dbAliasName;
    }

    public void setDbAliasName(String dbAliasName) {
        this.dbAliasName = dbAliasName;
    }
}
