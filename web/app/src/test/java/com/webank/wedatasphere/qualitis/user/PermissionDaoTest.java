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
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
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
public class PermissionDaoTest {

  @Autowired
  PermissionDao dao;

  @Test
  @Transactional
  public void test() {
    //保存是否成功
    Permission entity = new Permission();
    entity.setUrl("/junit/**");
    entity.setMethod("GET");
    Permission saveEntity = dao.savePermission(entity);
    assertTrue(saveEntity.getId() != 0);

    //总数量大于0
    long size = dao.countAll();
    assertTrue(size > 0);

    //分页查询有结果
    List<Permission> datas = dao.findAllPermission(0, 5);
    assertTrue(datas.size() > 0);

    //保存到数据库的对象是否和保存的值一致
    Permission findByIdEntity = dao.findById(saveEntity.getId());
    assertNotNull(findByIdEntity);
    assertEquals(findByIdEntity.getUrl(), saveEntity.getUrl());

    //根据username查询的数据库对象是否和保存的值一致
    Permission findByFieldsEntity = dao.findByMethodAndUrl(saveEntity.getMethod(), saveEntity.getUrl());
    assertNotNull(findByFieldsEntity);
    assertEquals(findByFieldsEntity.getMethod(), saveEntity.getMethod());

    //删除后,是否还能找到对象
    dao.deletePermission(saveEntity);
    Permission deleteEntity = dao.findById(saveEntity.getId());
    assertNull(deleteEntity);
  }

}
