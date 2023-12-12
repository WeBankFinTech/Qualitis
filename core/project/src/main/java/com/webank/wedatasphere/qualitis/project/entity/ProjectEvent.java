package com.webank.wedatasphere.qualitis.project.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/20 16:59
 */
@Entity
@Table(name = "qualitis_project_event")
public class ProjectEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;
    @Column(name = "time", length = 25)
    private String time;
    @Column(name = "operate_type")
    private Integer operateType;
    @Column(name = "operate_user")
    private String operateUser;

    public ProjectEvent() {
    }

    public ProjectEvent(Project project, String operateUser, String content, String time, Integer operateType) {
        this.operateUser = operateUser;
        this.operateType = operateType;
        this.projectId = project.getId();
        this.content = content;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectEvent that = (ProjectEvent) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(content, that.content) &&
            Objects.equals(time, that.time) &&
            Objects.equals(operateType, that.operateType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, content, time, operateType);
    }
}
