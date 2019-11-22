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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.service.StaffService;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-16
 */
@Service
public class StaffServiceImpl implements StaffService {

  @Autowired
  private UserDao userDao;

  /**
   * Return format: english name, chinese name, department
   * @return
   */
  @Override
  public List<String> getAllStaffs() {
    List<User> allUsers = userDao.findAllUser();
    return concatStaffResponse(allUsers);
  }

  private List<String> concatStaffResponse(List<User> allUsers) {
    if (allUsers == null || allUsers.isEmpty()){
      return null;
    }
    List<String> concatStaffList = new ArrayList<>();
    for (User staff : allUsers){
      concatStaffList.add(
          staff.getUsername() + "," + staff.getChineseName() + "," + staff.getDepartment());
    }
    return concatStaffList;
  }

}
