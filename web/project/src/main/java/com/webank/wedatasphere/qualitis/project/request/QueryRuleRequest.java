package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-05-27 14:54
 * @description
 */
public class QueryRuleRequest extends PageRequest {

    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_cn_name")
    private String ruleCnName;
    @JsonProperty("rule_template_id")
    private Integer ruleTemplateId;
    @JsonProperty("db_name")
    private String db;
    @JsonProperty("table_name")
    private String table;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("start_create_time")
    private String startCreateTime;

    @JsonProperty("end_create_time")
    private String endCreateTime;

    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("start_modify_time")
    private String startModifyTime;

    @JsonProperty("end_modify_time")
    private String endModifyTime;

    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    @JsonProperty("work_flow_space")
    private String workFlowSpace;
    @JsonProperty("work_flow_project")
    private String workFlowProject;
    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("node_name")
    private String nodeName;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getStartModifyTime() {
        return startModifyTime;
    }

    public void setStartModifyTime(String startModifyTime) {
        this.startModifyTime = startModifyTime;
    }

    public String getEndModifyTime() {
        return endModifyTime;
    }

    public void setEndModifyTime(String endModifyTime) {
        this.endModifyTime = endModifyTime;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getRuleTemplateId() {
        return ruleTemplateId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

    public String getWorkFlowProject() {
        return workFlowProject;
    }

    public void setWorkFlowProject(String workFlowProject) {
        this.workFlowProject = workFlowProject;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setRuleTemplateId(Integer ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public void convertParameter() {
        if (StringUtils.isEmpty(this.ruleName)) {
            this.ruleName = null;
        } else {
            this.ruleName = "%" + this.ruleName + "%";
        }
        if (StringUtils.isEmpty(this.ruleCnName)) {
            this.ruleCnName = null;
        } else {
            this.ruleCnName = "%" + this.ruleCnName + "%";
        }
        this.ruleCnName = StringUtils.trimToNull(this.ruleCnName);
        this.db = StringUtils.trimToNull(this.db);
        this.table = StringUtils.trimToNull(this.table);

        this.createUser = StringUtils.trimToNull(this.createUser);
        this.modifyUser = StringUtils.trimToNull(this.modifyUser);
        this.startCreateTime = StringUtils.trimToNull(this.startCreateTime);
        this.endCreateTime = StringUtils.trimToNull(this.endCreateTime);
        this.startModifyTime = StringUtils.trimToNull(this.startModifyTime);
        this.endModifyTime = StringUtils.trimToNull(this.endModifyTime);

        if (StringUtils.isEmpty(this.ruleGroupName)) {
            this.ruleGroupName = null;
        } else {
            this.ruleGroupName = "%" + this.ruleGroupName + "%";
        }

        this.workFlowSpace = StringUtils.trimToNull(this.workFlowSpace);
        this.workFlowProject = StringUtils.trimToNull(this.workFlowProject);
        this.workFlowName = StringUtils.trimToNull(this.workFlowName);
        this.nodeName = StringUtils.trimToNull(this.nodeName);
    }
}
