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


import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
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
public class UserDaoTest {

  @Autowired
  UserDao userDao;

  @Test
  @Transactional
  public void testUser() {
    //保存是否成功
    User user = new User();
    user.setChineseName("xxx");
    user.setUserName("xxx");
    user.setPassword("xxx");
    User saveUser = userDao.saveUser(user);
    assertTrue(saveUser.getId() != 0);

    //总数量大于0
    long userSize = userDao.countAll();
    assertTrue(userSize > 0);

    //分页查询有结果
    List<User> users = userDao.findAllUser(0, 5);
    assertTrue(users.size() > 0);

    //保存到数据库的对象是否和保存的值一致
    User findUser = userDao.findById(saveUser.getId());
    assertNotNull(findUser);
    assertEquals(findUser.getChineseName(), saveUser.getChineseName());
    assertEquals(findUser.getDepartment(), saveUser.getDepartment());
    assertEquals(findUser.getUserName(), saveUser.getUserName());

    //根据username查询的数据库对象是否和保存的值一致
    User findByUsernameUser = userDao.findByUsername(saveUser.getUserName());
    assertNotNull(findByUsernameUser);
    assertEquals(findByUsernameUser.getUserName(), saveUser.getUserName());

    //删除后,是否还能找到对象
    userDao.deleteUser(saveUser);
    User findDelUser = userDao.findById(saveUser.getId());
    assertNull(findDelUser);
  }

}
