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

package com.webank.wedatasphere.qualitis.predicate;

import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.queryqo.DataSourceQo;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 在DaoImpl中使用了Predicate动态查询的做单元测试
 * @author v_wblwyan
 * @date 2018-11-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PredicateTest {

  @Autowired
  ProjectUserDao dao;

  @Autowired
  TaskDataSourceDao taskDataSourceDao;

  @Autowired
  RuleDataSourceDao ruleDataSourceDao;

  @Test
  @Transactional
  public void test() {
    String user = "v_wblwyan_test1";

    /*
     * ProjectUserDao测试
     */
    DataSourceQo param = new DataSourceQo();
    param.setUser(user);
    param.setUserType(new Integer[] {
        ProjectUserPermissionEnum.CREATOR.getCode()});

    List<ProjectUser> projectUsers = dao.findByUsernameAndPermissionsIn(param);
    assertTrue(projectUsers.isEmpty());

    param.setUserType(new Integer[] {ProjectUserPermissionEnum.CREATOR.getCode()});
    projectUsers = dao.findByUsernameAndPermissionsIn(param);
    assertTrue(projectUsers.isEmpty());

    param.setUserType(null);
    projectUsers = dao.findByUsernameAndPermissionsIn(param);
    assertTrue(projectUsers.isEmpty());

    param.setUserType(new Integer[] {});
    projectUsers = dao.findByUsernameAndPermissionsIn(param);
    assertTrue(projectUsers.isEmpty());

    /*
     *TaskDataSourceDao 测试
     */
    List<TaskDataSource> taskDataSources = taskDataSourceDao.findByUserAndDataSource(user, null,
                                                                                     null, null, 0,
                                                                                     5);
    assertTrue(taskDataSources.isEmpty());

    taskDataSources = taskDataSourceDao.findByUserAndDataSource(user, "clusterName_test", null,
                                                                null, 0, 5);
    assertTrue(taskDataSources.isEmpty());

    taskDataSources = taskDataSourceDao.findByUserAndDataSource(user, "clusterName_test",
                                                                "databaseName_test", null, 0, 5);
    assertTrue(taskDataSources.isEmpty());

    taskDataSources = taskDataSourceDao.findByUserAndDataSource(user, "clusterName_test",
                                                                "databaseName_test",
                                                                "tableName_test", 0, 5);
    assertTrue(taskDataSources.isEmpty());

    long count = taskDataSourceDao.countByUserAndDataSource(user, "clusterName_test",
                                                            "databaseName_test", null);
    assertEquals(0, count);


    /*
     *RuleDataSourceDao 测试
     */
    List<RuleDataSource> ruleDataSources = ruleDataSourceDao.findByProjectUser(
        (long) Integer.MAX_VALUE, null, null, null);
    assertTrue(ruleDataSources.isEmpty());

    ruleDataSources = ruleDataSourceDao.findByProjectUser((long) Integer.MAX_VALUE,
                                                          "clusterName_test", null, null);
    assertTrue(ruleDataSources.isEmpty());

    ruleDataSources = ruleDataSourceDao.findByProjectUser((long) Integer.MAX_VALUE,
                                                          "clusterName_test", "databaseName_test",
                                                          null);
    assertTrue(ruleDataSources.isEmpty());

    ruleDataSources = ruleDataSourceDao.findByProjectUser((long) Integer.MAX_VALUE,
                                                          "clusterName_test", "databaseName_test",
                                                          "tableName_test");
    assertTrue(ruleDataSources.isEmpty());
  }

}
