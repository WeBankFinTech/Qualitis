package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/20 17:20
 */
public class ProjectEventResponse {
    private String content;
    private String time;
    @JsonProperty("field")
    private String field;
    @JsonProperty("before_modify")
    private String beforeModify;
    @JsonProperty("after_modify")
    private String afterModify;
    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("execute_user")
    private String executeUser;

    public ProjectEventResponse(ProjectEvent projectEvent) {
        this.content = projectEvent.getContent();
        this.time = projectEvent.getTime();
        this.field = projectEvent.getField();
        this.beforeModify = projectEvent.getBeforeModify();
        this.afterModify = projectEvent.getAfterModify();
        this.modifyUser = projectEvent.getModifyUser();
        this.executeUser = projectEvent.getExecuteUser();
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getBeforeModify() {
        return beforeModify;
    }

    public void setBeforeModify(String beforeModify) {
        this.beforeModify = beforeModify;
    }

    public String getAfterModify() {
        return afterModify;
    }

    public void setAfterModify(String afterModify) {
        this.afterModify = afterModify;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }
}
