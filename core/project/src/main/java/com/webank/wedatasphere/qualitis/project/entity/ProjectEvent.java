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

    @ManyToOne
    private Project project;

    @Column(name = "content", length = 500)
    private String content;
    @Column(name = "field", length = 50)
    private String field;
    @Column(name = "before_modify", length = 200)
    private String beforeModify;
    @Column(name = "after_modify", length = 200)
    private String afterModify;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "execute_user", length = 50)
    private String executeUser;

    @Column(name = "time", length = 25)
    private String time;

    @Column(name = "event_type")
    private Integer eventType;

    public ProjectEvent() {
    }

    public ProjectEvent(Project project, String content, String time) {
        this.project = project;
        this.content = content;
        this.time = time;
    }

    public ProjectEvent(Project project, String executeUser, String content, String time, Integer eventType) {
        this.executeUser = executeUser;
        this.eventType = eventType;
        this.project = project;
        this.content = content;
        this.time = time;
    }

    public ProjectEvent(Project projectInDb, String userName, String field, String beforeModify, String afterModify, String time, Integer eventType) {
        this.beforeModify = beforeModify;
        this.afterModify = afterModify;
        this.eventType = eventType;
        this.project = projectInDb;
        this.modifyUser = userName;
        this.field = field;
        this.time = time;

        this.content = userName + " modified " + field;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
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
            Objects.equals(project, that.project) &&
            Objects.equals(content, that.content) &&
            Objects.equals(time, that.time) &&
            Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, project, content, time, eventType);
    }
}
