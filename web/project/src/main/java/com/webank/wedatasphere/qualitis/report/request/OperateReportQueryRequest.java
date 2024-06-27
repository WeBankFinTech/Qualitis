package com.webank.wedatasphere.qualitis.report.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_gaojiedeng@webank.com
 */
public class OperateReportQueryRequest extends PageRequest {

    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_type")
    private Integer projectType;
    private String receiver;

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

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public void convertParameter() throws UnExpectedRequestException {
        if (StringUtils.isEmpty(this.projectName)) {
            this.projectName = null;
        } else {
            this.projectName = SpecCharEnum.PERCENT.getValue() + this.projectName + SpecCharEnum.PERCENT.getValue();
        }

        this.receiver = StringUtils.trimToNull(this.receiver);
        CommonChecker.checkObject(this.projectType, "project Type");
    }
}
