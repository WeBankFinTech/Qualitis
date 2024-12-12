package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2022/1/6 16:05
 */
public class ApplicationQueryRequest {
    @JsonProperty("job_id")
    private String jobId;

    public ApplicationQueryRequest() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
