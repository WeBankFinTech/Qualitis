package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-13 17:58
 * @description
 */
public class ScheduledProjectResponse {

    private String projectName;

    private String name;

    private String dispatchingSystemType;

    private String clusterName;
    
    private String releaseUser;

    private String createTime;
    
    private String createUser;
    
    private String modifyTime;
    
    private String modifyUser;

    public ScheduledProjectResponse() {
//        Doing something
    }

    public ScheduledProjectResponse(ScheduledProject scheduledProject) {
        BeanUtils.copyProperties(scheduledProject, this);
        this.projectName = scheduledProject.getProject().getName();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
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
}
