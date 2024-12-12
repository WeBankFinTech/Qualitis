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
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author v_wblwyan
 * @date 2018-11-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RolePermissionDaoTest {

  @Autowired
  RolePermissionDao dao;
  @Autowired
  PermissionDao permissionDao;
  @Autowired
  RoleDao roleDao;

  @Test
  @Transactional
  public void test() {
    Permission savePermission = savePermission();
    Role saveRole = saveRole();
    //保存是否成功
    RolePermission entity = new RolePermission();
    entity.setId(UuidGenerator.generate());
    entity.setPermission(savePermission);
    entity.setRole(saveRole);
    RolePermission saveEntity = dao.saveRolePermission(entity);
    assertNotNull(saveEntity.getId());

    //总数量大于0
    long size = dao.countAll();
    assertTrue(size > 0);

    //分页查询有结果
    List<RolePermission> datas = dao.findAllRolePermission(0, 5);
    assertTrue(datas.size() > 0);

    //保存到数据库的对象是否和保存的值一致
    RolePermission findByIdEntity = dao.findByUuid(saveEntity.getId());
    assertNotNull(findByIdEntity);
    assertEquals(findByIdEntity.getPermission().getUrl(), saveEntity.getPermission().getUrl());
    assertEquals(findByIdEntity.getRole().getName(), saveEntity.getRole().getName());

    RolePermission findByRoleAndPermissionEntity = dao.findByRoleAndPermission(saveRole, savePermission);
    assertNotNull(findByRoleAndPermissionEntity);
    assertEquals(findByRoleAndPermissionEntity.getPermission().getUrl(), saveEntity.getPermission().getUrl());
    assertEquals(findByRoleAndPermissionEntity.getRole().getName(), saveEntity.getRole().getName());

    //根据username查询的数据库对象是否和保存的值一致
    List<RolePermission> findByRoleEntity = dao.findByRole(saveRole);
    assertTrue(findByRoleEntity.size() > 0);
    assertEquals(findByRoleEntity.get(0).getPermission().getUrl(), saveEntity.getPermission().getUrl());

    //根据username查询的数据库对象是否和保存的值一致
    List<RolePermission> findByPermissionEntity = dao.findByPermission(savePermission);
    assertTrue(findByPermissionEntity.size() > 0);
    assertEquals(findByPermissionEntity.get(0).getRole().getName(), saveEntity.getRole().getName());

    //删除后,是否还能找到对象
    dao.deleteRolePermission(saveEntity);
    RolePermission deleteEntity = dao.findByUuid(saveEntity.getId());
    assertNull(deleteEntity);
  }

  private Role saveRole() {
    //保存是否成功
    Role entity = new Role();
    entity.setName("junitRole");
    Role saveEntity = roleDao.saveRole(entity);
    assertTrue(saveEntity.getId() != 0);
    return saveEntity;
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
