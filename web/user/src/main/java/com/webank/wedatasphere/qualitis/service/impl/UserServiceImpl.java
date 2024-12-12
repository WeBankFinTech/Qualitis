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

import com.webank.wedatasphere.qualitis.constant.PositionRoleConstant;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.encoder.Sha256Encoder;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.password.RandomPasswordGenerator;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.user.UserAddRequest;
import com.webank.wedatasphere.qualitis.request.user.UserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.user.AddUserResponse;
import com.webank.wedatasphere.qualitis.response.user.UserResponse;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSpecPermissionDao userSpecPermissionDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DepartmentDao departmentDao;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<AddUserResponse> addUser(UserAddRequest request) throws UnExpectedRequestException, RoleNotFoundException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by username
        String username = request.getUsername();
        User userInDb = userDao.findByUsername(username);
        if (userInDb != null) {
            throw new UnExpectedRequestException("username: " + username + " {&ALREADY_EXIST}");
        }

        // Generate random password and save user
        User newUser = new User();
        String password = RandomPasswordGenerator.generate(16);
        String passwordEncoded = Sha256Encoder.encode(password);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoded);
        newUser.setChineseName(request.getChineseName());
        newUser.setDepartmentName(request.getDepartmentName());
        newUser.setUserConfigJson(request.getUserConfigJson());
        newUser.setSubDepartmentCode(request.getDepartmentSubId());
        newUser.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        newUser.setCreateTime(DateUtils.now());
        // Find department by department name
        Department departmentInDb = departmentDao.findById(request.getDepartmentId());
        if (null == departmentInDb) {
            throw new UnExpectedRequestException("Department ID of " + request.getDepartmentId() + " {&DOES_NOT_EXIST}");
        }
        newUser.setDepartment(departmentInDb);

        User savedUser = userDao.saveUser(newUser);
        AddUserResponse addUserResponse = new AddUserResponse(savedUser, password);

        //新用户----->增加普通用户角色
        addNormalRoleForUser(savedUser);

        //校验职位角色
        checkPositionRole(savedUser, request.getPositionEn(), request.getPositionZh(), false);

        LOGGER.info("Succeed to create user, response: {}, current_user: {}", addUserResponse, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&CREATE_USER_SUCCESSFULLY}", addUserResponse);
    }

    public void checkPositionRole(User savedUser, String englishName, String chineseName, Boolean ifModify) throws UnExpectedRequestException {
        Role role = roleDao.findByRoleNameAndType(englishName, RoleTypeEnum.POSITION_ROLE.getCode());
        if (role == null) {
            Role newRole = new Role();
            newRole.setRoleType(RoleTypeEnum.POSITION_ROLE.getCode());
            newRole.setZnName(chineseName);
            newRole.setName(englishName);
            newRole.setCreateUser(HttpUtils.getUserName(httpServletRequest));
            newRole.setCreateTime(UserServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
            Role savedRole = roleDao.saveRole(newRole);
            LOGGER.info("Succeed to add role, role: {}, current_user: {}", savedRole, HttpUtils.getUserName(httpServletRequest));
            addUserRole(savedRole, savedUser, ifModify);
        } else {
            addUserRole(role, savedUser, ifModify);
        }

    }

    public void addUserRole(Role role, User savedUser, Boolean ifModify) throws UnExpectedRequestException {
        if (ifModify) {
            List<Role> roles = savedUser.getRoles().stream().filter(item -> item.getRoleType().toString().equals(RoleTypeEnum.POSITION_ROLE.getCode().toString())).collect(Collectors.toList());
            //一个用户就一个职位角色 所以roles.get(0)
            if (CollectionUtils.isNotEmpty(roles)) {
                UserRole userRole = userRoleDao.findByUserAndRole(savedUser, roles.get(0));
                userRole.setRole(role);
                userRole.setUser(savedUser);
                userRole.setModifyUser(HttpUtils.getUserName(httpServletRequest));
                userRole.setModifyTime(UserRoleServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
                UserRole lastUserRole = userRoleDao.saveUserRole(userRole);
                LOGGER.info("Succeed to modify user role,user role: {}, current_user: {}", lastUserRole, HttpUtils.getUserName(httpServletRequest));
            } else {
                addUserRole(role, savedUser);
            }
        } else {
            addUserRole(role, savedUser);
        }

    }

    private void addUserRole(Role role, User savedUser) {
        UserRole newUserRole = new UserRole();
        newUserRole.setRole(role);
        newUserRole.setUser(savedUser);
        newUserRole.setId(UuidGenerator.generate());

        newUserRole.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        newUserRole.setCreateTime(UserRoleServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
        UserRole savedUserRole = userRoleDao.saveUserRole(newUserRole);
        LOGGER.info("Succeed to add user role,user role: {}, current_user: {}", savedUserRole, HttpUtils.getUserName(httpServletRequest));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteUser(UserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by id
        Long userId = request.getUserId();
        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }
        // Check personal template.
        checkTemplate(userInDb);
        List<UserRole> userRolesInDb = userRoleDao.findByUser(userInDb);
        if (null != userRolesInDb && !userRolesInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_ROLE_HAS_FOREIGN_KEY}");
        }
        List<UserSpecPermission> userSpecPermissionsInDb = userSpecPermissionDao.findByUser(userInDb);
        if (null != userSpecPermissionsInDb && !userSpecPermissionsInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_SPEC_PERMISSION_HAS_FOREIGN_KEY}");
        }

        // Delete user
        userDao.deleteUser(userInDb);
        LOGGER.info("Succeed to delete user, userId: {}, username: {}, current_user: {}", userInDb.getId(), userInDb.getUsername(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_USER_SUCCESSFULLY}", null);
    }

    private void checkTemplate(User userInDb) throws UnExpectedRequestException {
        if (!userDao.checkTemplate(userInDb)) {
            throw new UnExpectedRequestException("{&USER_HAS_TEMPLATES}");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<String> initPassword(UserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by id
        Long id = request.getUserId();
        User userInDb = userDao.findById(id);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }

        // Generate random password and save user
        String password = RandomPasswordGenerator.generate(16);
        String passwordEncoded = Sha256Encoder.encode(password);
        userInDb.setPassword(passwordEncoded);
        userDao.saveUser(userInDb);

        LOGGER.info("Succeed to init password, user_id: {}, current_user: {}", id, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&INIT_PASSWORD_SUCCESSFULLY}", password);
    }

    @Override
    public GeneralResponse<GetAllResponse<UserResponse>> findAllUser(QueryUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);
        int page = request.getPage();
        int size = request.getSize();
        Long subDepartmentCode = null;
        if (StringUtils.isNotBlank(request.getSubDepartmentCode())) {
            subDepartmentCode = Long.valueOf(request.getSubDepartmentCode());
        }
        Page<User> userPage = userDao.findAllUser(request.getUserName(), request.getDepartmentCode(), subDepartmentCode, page, size);
        List<UserResponse> userResponses = userPage.getContent().stream().map(UserResponse::new).collect(Collectors.toList());
        GetAllResponse<UserResponse> response = new GetAllResponse<>();
        response.setTotal(userPage.getTotalElements());
        response.setData(userResponses);

        LOGGER.info("Succeed to find all users, response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_USERS_SUCCESSFULLY}", response);
    }

    @Override
    public GetAllResponse<Map<String, Object>> findAllUserIdAndName() {
        return new GetAllResponse<>(0, userDao.findAllUserIdAndName());
    }

    @Override
    public GeneralResponse modifyDepartment(ModifyDepartmentRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);
        User userInDb = userDao.findById(request.getUserId());
        if (null == userInDb) {
            throw new UnExpectedRequestException("userId {&DOES_NOT_EXIST}");
        }
        // Find department by name.
        Department departmentInDb = departmentDao.findById(request.getDepartment());
        if (null == departmentInDb) {
            throw new UnExpectedRequestException("Department of " + request.getDepartmentName() + " {&DOES_NOT_EXIST}");
        }
        userInDb.setDepartment(departmentInDb);
        userInDb.setDepartmentName(request.getDepartmentName());
        userInDb.setSubDepartmentCode(request.getDepartmentSubId());
        userInDb.setChineseName(request.getChineseName());
        userInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        userInDb.setModifyTime(DateUtils.now());
        userInDb.setUserConfigJson(request.getUserConfigJson());
        // Save user
        User user = userDao.saveUser(userInDb);
        checkPositionRole(user, request.getPositionEn(), request.getPositionZh(), true);

        LOGGER.info("Succeed to modify department, userId: {}, current_user: {}", userInDb.getId(), userInDb.getUsername());
        return new GeneralResponse<>("200", "{&MODIFY_DEPARTMENT_SUCCESSFULLY}", null);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public List<Map<String, Object>> getPositionRoleEnum() {
        return PositionRoleConstant.getPositionRoleEnumList();
    }

    @Override
    public void syncDepartmentName(Department department, String departmentName) {
        List<User> userList = userDao.findByDepartment(department);
        for (User user: userList) {
            String departmentNameInDb = user.getDepartmentName();
            String[] splitDepartmentName = StringUtils.split(departmentNameInDb, SpecCharEnum.SLASH.getValue());
            if (splitDepartmentName.length < 2) {
                continue;
            }
            departmentNameInDb = departmentName + SpecCharEnum.SLASH.getValue() + splitDepartmentName[1];
            user.setDepartmentName(departmentNameInDb);
        }
        userDao.saveAllUser(userList);
    }

    @Override
    public void syncSubDepartmentName(Long subDepartmentCode, String departmentName) {
        List<User> userList = userDao.findBySubDepartmentCode(subDepartmentCode);
        for (User user: userList) {
            String departmentNameInDb = user.getDepartmentName();
            String[] splitDepartmentName = StringUtils.split(departmentNameInDb, SpecCharEnum.SLASH.getValue());
            if (splitDepartmentName.length < 2) {
                continue;
            }
            departmentNameInDb = splitDepartmentName[0] + SpecCharEnum.SLASH.getValue() + departmentName;
            user.setDepartmentName(departmentNameInDb);
        }
        userDao.saveAllUser(userList);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void autoAddUser(String username) throws RoleNotFoundException {
        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPassword(Sha256Encoder.encode(username));
        User savedUser = userDao.saveUser(newUser);

        //新用户----->增加普通用户角色
        addNormalRoleForUser(savedUser);
    }

    private void addNormalRoleForUser(User savedUser) throws RoleNotFoundException {
        Role role = roleDao.findByRoleName(QualitisConstants.PROJECTOR);
        if (role == null) {
            throw new RoleNotFoundException("{&NO_COMMON_ROLE_CONFIGURED}");
        }
        UserRole userRole = new UserRole();
        userRole.setId(UuidGenerator.generate());
        userRole.setRole(role);
        userRole.setUser(savedUser);
        userRole.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        userRole.setCreateTime(LoginServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
        userRoleDao.saveUserRole(userRole);
        LOGGER.info("Succeed to save user_role. uuid: {}, user_id: {}, role_id: {}", userRole.getId(), savedUser.getId(), role.getId());

    }

    private void checkRequest(ModifyDepartmentRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (request.getUserId() == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getUserId().toString(), "user ID");
        checkString(request.getDepartmentName(), "department role id");
        if (StringUtils.isNotBlank(request.getUserConfigJson())) {
            try {
                new ObjectMapper().readValue(request.getUserConfigJson(), Map.class);
            } catch (IOException e) {
                throw new UnExpectedRequestException("Error json format: user_config_json");
            }
        }
    }

    private void checkId(Long id, String idName) throws UnExpectedRequestException {
        if (null == id) {
            throw new UnExpectedRequestException(idName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }


    private void checkRequest(UserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getUserId(), "id");
    }

    private void checkRequest(UserAddRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getUsername(), "username");
        checkString(request.getChineseName(), "ChineseName");
        checkString(request.getDepartmentId().toString(), "Department");
        checkString(request.getPositionEn(), "PositionEn");
        checkString(request.getPositionZh(), "PositionZh");
        if (StringUtils.isNotBlank(request.getUserConfigJson())) {
            try {
                objectMapper.readValue(request.getUserConfigJson(), Map.class);
            } catch (IOException e) {
                throw new UnExpectedRequestException("Error json format: user_config_json");
            }
        }
    }
}
