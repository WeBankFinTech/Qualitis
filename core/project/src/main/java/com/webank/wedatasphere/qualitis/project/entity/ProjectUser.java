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

import javax.persistence.*;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_project_user")
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Project project;
    private Integer permission;

    @Column(name = "user_name", length = 20)
    private String userName;

    @Column(name = "user_full_name", length = 30)
    private String userFullName;

    public ProjectUser() {
    }

    public ProjectUser(Integer permission, Project project, String userName, String userFullName) {
        this.permission = permission;
        this.project = project;
        this.userName = userName;
        this.userFullName = userFullName;
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

  @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ProjectUser that = (ProjectUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectUser{" +
                "id=" + id +
                ", project=" + project.getId() +
                ", permission=" + permission +
                ", userName='" + userName + '\'' +
                ", userFullName='" + userFullName + '\'' +
                '}';
    }
}
