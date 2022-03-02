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

package com.webank.wedatasphere.qualitis.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectUserRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author v_wblwyan
 * @date 2018-11-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectUserRepositoryTest {

  @Autowired
  ProjectUserRepository repository;

  @Autowired
  ProjectRepository projectRepository;

  @Test
  @Transactional
  public void test() {
    //保存是否成功
    Project project = new Project();
    project.setName("junitProject");
    project.setCreateUser("setCreateUser");
    project.setCreateUserFullName("setCreateUserFullName");
    project.setDescription("setDescription");
    project.setUserDepartment("setUserDepartment");
    Project saveProject = projectRepository.save(project);
    assertTrue(saveProject.getId() != 0);

    ProjectUser saveEntity = new ProjectUser(ProjectUserPermissionEnum.CREATOR.getCode(), project,
                                          "v_wblwyan_test", "颜龙武");
    ProjectUser entity = repository.save(saveEntity);

    //根据username查询的数据库对象是否和保存的值一致
    List<ProjectUser> findByUserNameList = repository.findByUserName(entity.getUserName());
    assertTrue(findByUserNameList.size() > 0);
    assertEquals(findByUserNameList.get(0).getUserName(), entity.getUserName());

    //删除后,是否还能找到对象
    repository.deleteByProject(project);
    List<ProjectUser> deleteEntity = repository.findByUserName(entity.getUserName());
    assertTrue(deleteEntity.isEmpty());

  }


}
