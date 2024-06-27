package com.webank.wedatasphere.qualitis.report.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.report.constant.ExecutionFrequencyEnum;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
public class SubscribeOperateReportResponse {

    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_ids")
    private List<Long> projectIds;
    private String receiver;
    @JsonProperty("execution_frequency")
    private String executionFrequency;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    private Long id;

    public SubscribeOperateReportResponse() {
        // Default Constructor
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(String executionFrequency) {
        this.executionFrequency = executionFrequency;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscribeOperateReportResponse(SubscribeOperateReport subscribeOperateReport) {
        BeanUtils.copyProperties(subscribeOperateReport, this);
        this.executionFrequency = ExecutionFrequencyEnum.getExecutionFrequencyName(subscribeOperateReport.getExecutionFrequency());
        this.projectName = subscribeOperateReport.getSubscribeOperateReportProjectsSet().stream().map(item -> item.getProject().getName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));

        String ids = subscribeOperateReport.getSubscribeOperateReportProjectsSet().stream().map(item -> item.getProject().getId().toString()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        if (StringUtils.isNotBlank(ids)) {
            this.projectIds = Arrays.stream(ids.split(",")).map(v -> Long.parseLong(v)).collect(Collectors.toList());
        } else {
            this.projectIds = Lists.newArrayList();
        }

    }
}
