package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-25 15:43
 * @description
 */
public class RuleDataSourceEnvResponse {
    private String envName;
    private Long envId;
    private String dbAndTable;
    private Long ruleDataSourceId;

    public RuleDataSourceEnvResponse(){}

    public RuleDataSourceEnvResponse(RuleDataSourceEnv ruleDataSourceEnv) {
        BeanUtils.copyProperties(ruleDataSourceEnv, this);
        this.ruleDataSourceId = ruleDataSourceEnv.getRuleDataSource().getId();
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getDbAndTable() {
        return dbAndTable;
    }

    public void setDbAndTable(String dbAndTable) {
        this.dbAndTable = dbAndTable;
    }

    public Long getRuleDataSourceId() {
        return ruleDataSourceId;
    }

    public void setRuleDataSourceId(Long ruleDataSourceId) {
        this.ruleDataSourceId = ruleDataSourceId;
    }
}
