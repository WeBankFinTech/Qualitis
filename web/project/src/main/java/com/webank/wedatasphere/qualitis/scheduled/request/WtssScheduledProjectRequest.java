package com.webank.wedatasphere.qualitis.scheduled.request;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-17 15:29
 * @description
 */
public class WtssScheduledProjectRequest {
    private String projectName;
    private String releaseUser;

    public WtssScheduledProjectRequest() {
        // Default Constructor
    }

    public WtssScheduledProjectRequest(ScheduledProject scheduledProject) {
        this.projectName = scheduledProject.getName();
        this.releaseUser = scheduledProject.getReleaseUser();
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "ScheduledProjectDto{" +
                "projectName='" + projectName + '\'' +
                ", releaseUser='" + releaseUser + '\'' +
                '}';
    }
}
