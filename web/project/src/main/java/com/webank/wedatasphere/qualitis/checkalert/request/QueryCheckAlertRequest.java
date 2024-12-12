package com.webank.wedatasphere.qualitis.checkalert.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_gaojiedeng@webank.com
 */
public class QueryCheckAlertRequest extends PageRequest {

    @JsonProperty("project_id")
    private Long projectId;
    private String topic;

    @JsonProperty("alert_table")
    private String alertTable;
    @JsonProperty("work_flow_space")
    private String workFlowSpace;
    @JsonProperty("work_flow_project")
    private String workFlowProject;
    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("node_name")
    private String nodeName;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAlertTable() {
        return alertTable;
    }

    public void setAlertTable(String alertTable) {
        this.alertTable = alertTable;
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

    public void convertParameter() {
        if (StringUtils.isEmpty(this.topic)) {
            this.topic = null;
        } else {
            this.topic = SpecCharEnum.PERCENT.getValue() + this.topic + SpecCharEnum.PERCENT.getValue();
        }

        this.alertTable = StringUtils.trimToNull(this.alertTable);
        this.workFlowSpace = StringUtils.trimToNull(this.workFlowSpace);

        this.workFlowProject = StringUtils.trimToNull(this.workFlowProject);
        this.workFlowName = StringUtils.trimToNull(this.workFlowName);
        this.nodeName = StringUtils.trimToNull(this.nodeName);

        this.createUser = StringUtils.trimToNull(this.createUser);
        this.modifyUser = StringUtils.trimToNull(this.modifyUser);
        this.startCreateTime = StringUtils.trimToNull(this.startCreateTime);
        this.endCreateTime = StringUtils.trimToNull(this.endCreateTime);
        this.startModifyTime = StringUtils.trimToNull(this.startModifyTime);
        this.endModifyTime = StringUtils.trimToNull(this.endModifyTime);
    }
}
