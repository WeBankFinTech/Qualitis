package com.webank.wedatasphere.qualitis.report.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public class SubscribeOperateReportRequest {

    private Long id;
    @JsonProperty("project_ids")
    private Set<Long> projectIds;
    private String receiver;
    @JsonProperty("execution_frequency")
    private Integer executionFrequency;

    private static final Integer TWENTY = 20;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(Integer executionFrequency) {
        this.executionFrequency = executionFrequency;
    }

    public static void checkRequest(SubscribeOperateReportRequest request, boolean modify) throws UnExpectedRequestException {
        if (modify) {
            CommonChecker.checkObject(request.getId(), "Subscribe Operate Report ID");
        }
        CommonChecker.checkCollections(request.getProjectIds(), "Project Ids");
        CommonChecker.checkString(request.getReceiver(), "Receiver");
        CommonChecker.checkObject(request.getExecutionFrequency(), "Execution Frequency");

        if (CollectionUtils.isNotEmpty(request.getProjectIds()) && request.getProjectIds().size() >= TWENTY) {
            throw new UnExpectedRequestException(request.getProjectIds().size() + " Exceeding the limit of 20 subscriptions per subscription");
        }

    }
}
