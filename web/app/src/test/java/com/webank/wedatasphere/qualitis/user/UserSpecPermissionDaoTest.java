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

package com.webank.wedatasphere.qualitis.user;


import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author v_wblwyan
 * @date 2018-11-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserSpecPermissionDaoTest {

  @Autowired
  UserSpecPermissionDao dao;
  @Autowired
  PermissionDao permissionDao;
  @Autowired
  UserDao userDao;

  @Test
  @Transactional
  public void test() {
    Permission savePermission = savePermission();
    User saveUser = saveUser();
    //保存是否成功
    UserSpecPermission entity = new UserSpecPermission();
    entity.setId(UuidGenerator.generate());
    entity.setPermission(savePermission);
    entity.setUser(saveUser);
    UserSpecPermission saveEntity = dao.saveUserSpecPermission(entity);
    assertNotNull(saveEntity.getId());

    //总数量大于0
    long size = dao.countAll();
    assertTrue(size > 0);

    //分页查询有结果
    List<UserSpecPermission> datas = dao.findAllUserSpecPermission(0, 5);
    assertTrue(datas.size() > 0);

    //保存到数据库的对象是否和保存的值一致
    UserSpecPermission findByIdEntity = dao.findByUuid(saveEntity.getId());
    assertNotNull(findByIdEntity);
    assertEquals(findByIdEntity.getPermission().getUrl(), saveEntity.getPermission().getUrl());
    assertEquals(findByIdEntity.getUser().getUserName(), saveEntity.getUser().getUserName());

    UserSpecPermission findByRoleAndPermissionEntity = dao.findByUserAndPermission(saveUser, savePermission);
    assertNotNull(findByRoleAndPermissionEntity);
    assertEquals(findByRoleAndPermissionEntity.getPermission().getUrl(), saveEntity.getPermission().getUrl());
    assertEquals(findByRoleAndPermissionEntity.getUser().getUserName(), saveEntity.getUser().getUserName());

    //根据username查询的数据库对象是否和保存的值一致
    List<UserSpecPermission> findByRoleEntity = dao.findByUser(saveUser);
    assertTrue(findByRoleEntity.size() > 0);
    assertEquals(findByRoleEntity.get(0).getPermission().getUrl(), saveEntity.getPermission().getUrl());

    //根据username查询的数据库对象是否和保存的值一致
    List<UserSpecPermission> findByPermissionEntity = dao.findByPermission(savePermission);
    assertTrue(findByPermissionEntity.size() > 0);
    assertEquals(findByPermissionEntity.get(0).getUser().getUserName(), saveEntity.getUser().getUserName());

    //删除后,是否还能找到对象
    dao.deleteUserSpecPermission(saveEntity);
    UserSpecPermission deleteEntity = dao.findByUuid(saveEntity.getId());
    assertNull(deleteEntity);
  }


  private User saveUser() {
    //保存是否成功
    User user = new User();
    user.setChineseName("xxx");
    user.setUserName("xxx");
    user.setPassword("xxx");
    User saveUser = userDao.saveUser(user);
    assertTrue(saveUser.getId() != 0);
    return saveUser;
  }

  private Permission savePermission() {
    //保存是否成功
    Permission entity = new Permission();
    entity.setUrl("/junit/**");
    entity.setMethod("GET");
    Permission saveEntity = permissionDao.savePermission(entity);
    assertTrue(saveEntity.getId() != 0);
    return saveEntity;
  }


}
