package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/20 17:20
 */
public class ProjectEventResponse {
    private String content;
    private String time;
    @JsonProperty("operate_user")
    private String operateUser;
    @JsonProperty("operate_type")
    private String operateType;

    public ProjectEventResponse(ProjectEvent projectEvent) {
        this.content = projectEvent.getContent();
        this.time = projectEvent.getTime();
        this.operateUser = projectEvent.getOperateUser();
        OperateTypeEnum operateTypeEnum = OperateTypeEnum.fromCode(projectEvent.getOperateType());
        this.operateType = operateTypeEnum == null ? null : operateTypeEnum.getName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
