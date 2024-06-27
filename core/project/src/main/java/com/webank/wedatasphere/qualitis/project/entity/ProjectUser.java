/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.project.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_project_user")
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Project project;

    private Integer permission;

    @Column(name = "user_name", length = 20)
    private String userName;

    @Column(name = "user_full_name", length = 30)
    private String userFullName;

    @Column(name = "automatic_switch")
    private Boolean automaticSwitch;

    public ProjectUser() {
    }

    public ProjectUser(Integer permission, Project project, String userName, Boolean flag) {
        this.permission = permission;
        this.project = project;
        this.userName = userName;
        this.automaticSwitch = flag;
    }

    public ProjectUser(Integer permission, Project project, String userName, String userFullName, Boolean flag) {
        this.permission = permission;
        this.project = project;
        this.userName = userName;
        this.userFullName = StringUtils.isBlank(userFullName) ? userFullName : new String(userFullName.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        this.automaticSwitch = flag;
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

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Boolean getAutomaticSwitch() {
        return automaticSwitch;
    }

    public void setAutomaticSwitch(Boolean automaticSwitch) {
        this.automaticSwitch = automaticSwitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectUser that = (ProjectUser) o;
        return Objects.equals(id, that.id) && Objects.equals(userName, that.userName) && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectUser{" +
                ", project=" + project.getId() +
                ", permission=" + permission +
                ", userName='" + userName + '\'' +
                ", userFullName='" + userFullName + '\'' +
                '}';
    }
}
