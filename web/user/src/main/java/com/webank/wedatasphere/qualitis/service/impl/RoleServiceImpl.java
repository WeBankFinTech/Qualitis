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

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.filter.BodyReaderHttpServletRequestWrapper;
import com.webank.wedatasphere.qualitis.request.role.RoleAddRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleModifyRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleRequest;
import com.webank.wedatasphere.qualitis.rule.constant.RoleDefaultTypeEnum;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RoleResponse;
import com.webank.wedatasphere.qualitis.response.UserAndRoleResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DepartmentDao departmentDao;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    public RoleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RoleResponse> addRole(RoleAddRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of role by role name
        String roleName = request.getRoleName();
        Role roleInDb = roleDao.findByRoleName(roleName);
        if (roleInDb != null) {
            throw new UnExpectedRequestException("role name {&ALREADY_EXIST}, request: " + request);
        }

        // Save new role
        Role newRole = new Role();
        newRole.setName(roleName);
        if (request.getDepartmentName() != null) {
            Department departmentInDb = departmentDao.findByName(request.getDepartmentName());
            if (departmentInDb == null) {
                throw new UnExpectedRequestException("department {&DOES_NOT_EXIST}, name: " + request.getDepartmentName());
            }
            LOGGER.info("Succeed to find department. Name: " + departmentInDb.getName());
            newRole.setDepartment(departmentInDb);
        }

        Role savedRole = roleDao.saveRole(newRole);
        RoleResponse roleResponse = new RoleResponse(savedRole);

        LOGGER.info("Succeed to add role, role: {}, current_user: {}", roleResponse, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&CREATE_ROLE_SUCCESSFULLY}", roleResponse);
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRole(RoleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of role by roleId
        Long roleId = request.getRoleId();
        Role roleInDb = roleDao.findById(roleId);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("role id {&DOES_NOT_EXIST}, request: " + request);
        }
        // Check department template.
        checkTemplate(roleInDb);
        List<UserRole> userRolesInDb = userRoleDao.findByRole(roleInDb);
        if (null != userRolesInDb && !userRolesInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_ROLE_HAS_FOREIGN_KEY}");
        }
        List<RolePermission> rolePermissionsInDb = rolePermissionDao.findByRole(roleInDb);
        if (null != rolePermissionsInDb && !rolePermissionsInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_ROLE_PERMISSION_HAS_FOREIGN_KEY}");
        }

        // Delete role
        roleDao.deleteRole(roleInDb);

        LOGGER.info("Succeed to delete role, role_id: {}, role_name: {}, current_user: {}", roleInDb.getId(), roleInDb.getName(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_ROLE_SUCCESSFULLY}", null);
    }

    private void checkTemplate(Role roleInDb) throws UnExpectedRequestException {
        if (! roleDao.checkTemplate(roleInDb)) {
            throw new UnExpectedRequestException("{&ROLE_HAS_DEPARTMENT_TEMPLATES}");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyRole(RoleModifyRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of role by roleId
        Long id = request.getRoleId();
        String roleName = request.getRoleName();
        Role roleInDb = roleDao.findById(id);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("role id {&DOES_NOT_EXIST}, request: " + request);
        }
        LOGGER.info("Succeed to find role, role_id: {}, role_name: {}, current_user: {}", roleInDb.getId(), roleInDb.getName(), HttpUtils.getUserName(httpServletRequest));

        // Modify role name and save
        roleInDb.setName(roleName);
        if (request.getDepartmentName() != null) {
            Department departmentInDb = departmentDao.findByName(request.getDepartmentName());
            if (departmentInDb == null) {
                throw new UnExpectedRequestException("department {&DOES_NOT_EXIST}, name: " + request.getDepartmentName());
            }
            LOGGER.info("Succeed to find department. Name: " + departmentInDb.getName());
            roleInDb.setDepartment(departmentInDb);
        }
        Role savedRole = roleDao.saveRole(roleInDb);
        LOGGER.info("Succeed to modify role, role_id: {}, role_name: {}, current_user: {}", savedRole.getId(), savedRole.getName(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_ROLE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<RoleResponse>> getAllRole(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<Role> roles = roleDao.findAllRole(page, size);
        long total = roleDao.countAll();
        List<RoleResponse> roleResponses = new ArrayList<>();
        for (Role role : roles) {
            RoleResponse tmp = new RoleResponse(role);
            roleResponses.add(tmp);
        }
        GetAllResponse<RoleResponse> responses = new GetAllResponse<>();
        responses.setData(roleResponses);
        responses.setTotal(total);

        LOGGER.info("Succeed to find all roles, page: {}, size: {}, role: {}, current_user: {}", page, size, responses, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_ROLES_SUCCESSFULLY}", responses);
    }

    @Override
    public GeneralResponse<?> getRoleByUser() {
        Long userId;
        String username = null;
        // Get current userId
        userId = HttpUtils.getUserId(httpServletRequest);
        username = HttpUtils.getUserName(httpServletRequest);
        User userInDb = userDao.findById(userId);

        // Find user role by user
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        // Get all roles of user
        List<String> roleNames = userRoles.stream().map(userRole -> userRole.getRole().getName()).collect(Collectors.toList());
        UserAndRoleResponse response = new UserAndRoleResponse();
        response.setRoles(roleNames);
        response.setUsername(username);
        HttpSession session = httpServletRequest.getSession();
        response.setLoginRandom((Integer) session.getAttribute("loginRandom"));
        LOGGER.info("Succeed to get role of user, {}  role: {}, current_user: {}", username, roleNames.toString(), username);
        return new GeneralResponse<>("200", "{&GET_ROLE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<?> getProxyUserByUser() {
        Long userId = HttpUtils.getUserId(httpServletRequest);
        User userInDb = userDao.findById(userId);

        List<String> proxyUserNames = new ArrayList<>();
        if (userInDb.getUserProxyUsers() != null) {
            for (UserProxyUser userProxyUser : userInDb.getUserProxyUsers()) {
                proxyUserNames.add(userProxyUser.getProxyUser().getProxyUserName());
            }
        }
        LOGGER.info("Succeed to get proxy user of user. user: {}, proxy user: {}",userInDb.getUserName(), proxyUserNames);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_PROXY_USER_OF_USER}", proxyUserNames);
    }

  @Override
  public Integer getRoleType(List<UserRole> userRoles) {
      Integer roleType = RoleDefaultTypeEnum.PROJECTOR.getCode();
      for (UserRole userRole : userRoles) {
          if (userRole.getRole().getName().equals(RoleDefaultTypeEnum.ADMIN.getMessage())) {
              roleType = RoleDefaultTypeEnum.ADMIN.getCode();
              break;
          } else if (! userRole.getRole().getName().equals(RoleDefaultTypeEnum.PROJECTOR.getMessage())) {
              roleType = RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode();
          } else {
              continue;
          }
      }
      return roleType;
  }

  private void checkRequest(RoleAddRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        String roleName = request.getRoleName();
        if (StringUtils.isBlank(roleName)) {
            throw new UnExpectedRequestException("role name {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
    }

    private void checkRequest(RoleRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        Long id = request.getRoleId();
        if (null == id) {
            throw new UnExpectedRequestException("role id {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
    }

    private void checkRequest(RoleModifyRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        Long id = request.getRoleId();
        String roleName = request.getRoleName();
        if (null == id) {
            throw new UnExpectedRequestException("role id {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }

        if (StringUtils.isBlank(roleName)) {
            throw new UnExpectedRequestException("role name {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
    }
}
