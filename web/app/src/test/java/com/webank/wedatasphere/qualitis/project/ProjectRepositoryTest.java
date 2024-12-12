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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
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
public class ProjectRepositoryTest {

  @Autowired
  ProjectRepository repository;

  @Test
  @Transactional
  public void test() {
    //保存是否成功
    Project entity = new Project();
    entity.setName("junitProject");
    entity.setCreateUser("setCreateUser");
    entity.setCreateUserFullName("setCreateUserFullName");
    entity.setDescription("setDescription");
    entity.setUserDepartment("setUserDepartment");
    Project saveEntity = repository.save(entity);
    assertTrue(saveEntity.getId() != 0);


    //保存到数据库的对象是否和保存的值一致
    List<Project> findByCreateUserList = repository.findByCreateUser(saveEntity.getCreateUser());
    assertTrue(findByCreateUserList.size() > 0);
    assertEquals(findByCreateUserList.get(0).getName(), saveEntity.getName());

    //根据username查询的数据库对象是否和保存的值一致
    Project findByNameUser = repository.findByName(saveEntity.getName());
    assertNotNull(findByNameUser);
    assertEquals(findByNameUser.getName(), saveEntity.getName());
  }

}
