package com.webank.wedatasphere.qualitis.rule.request;

/**
 * @author v_minminghe@webank.com
 * @date 2024-08-16 11:34
 * @description
 */
public class DeleteRuleMetricRequest {

    private String name;
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
