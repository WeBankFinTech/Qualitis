package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/6 18:28
 */
public class RuleMetricQueryRequest {
    @JsonProperty("sub_system_name")
    private String subSystemName;
    @JsonProperty("rule_metric_name")
    private String ruleMetricName;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("en_code")
    private String enCode;

    @JsonProperty("available")
    private Boolean available;

    @JsonProperty("multi_envs")
    private Boolean multiEnvs;

    @JsonProperty("dev_department_id")
    private String devDepartmentId;
    @JsonProperty("ops_department_id")
    private String opsDepartmentId;
    @JsonProperty("action_range")
    private Set<String> actionRange;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;


    private int page;
    private int size;

    public RuleMetricQueryRequest() {
        this.page = 0;
        this.size = 15;
    }

    public RuleMetricQueryRequest(int page, int size) {
        this.page = page;
        this.size = size;
        this.enCode = "";
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEnCode() {
        return enCode;
    }

    public void setEnCode(String enCode) {
        this.enCode = enCode;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getMultiEnvs() {
        return multiEnvs;
    }

    public void setMultiEnvs(Boolean multiEnvs) {
        this.multiEnvs = multiEnvs;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(String devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public String getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(String opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public Set<String> getActionRange() {
        return actionRange;
    }

    public void setActionRange(Set<String> actionRange) {
        this.actionRange = actionRange;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }
}
