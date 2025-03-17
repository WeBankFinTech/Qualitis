package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class AddScheduledRelationProjectRequest {

    @JsonProperty("project_id")
    private Long projectId;
    /**
     * 调度项目
     */
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("release_user")
    private String releaseUser;
    @JsonProperty("task_list")
    private List<AddScheduledRelationTaskRequest> taskList;

    public List<AddScheduledRelationTaskRequest> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<AddScheduledRelationTaskRequest> taskList) {
        this.taskList = taskList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(this.getProjectId(),"project_id");
        CommonChecker.checkString(this.getScheduleSystem(), "schedule_system");
        CommonChecker.checkString(this.getCluster(), "cluster");
        CommonChecker.checkString(this.getProjectName(), "project_name");
        CommonChecker.checkCollections(this.getTaskList(), "task_list");
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (AddScheduledRelationTaskRequest taskRequest: taskList) {
                taskRequest.checkRequest();
            }
        }
    }

}
