package com.webank.wedatasphere.qualitis.job;

import cn.webank.bdp.wedatasphere.biz.external.um.service.impl.AbstractUMServiceImpl;
import cn.webank.bdp.wedatasphere.biz.pojo.um.WdsUMPermission;
import cn.webank.bdp.wedatasphere.biz.pojo.um.WdsUMRole;
import cn.webank.bdp.wedatasphere.biz.pojo.um.WdsUMRolePermission;
import cn.webank.bdp.wedatasphere.biz.pojo.um.WdsUMUserRole;
import com.webank.um.bean.*;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 
 * @date 2024-11-20 10:14
 * @description sync user、permission、role from UM
 */
@Primary
@Service
public class UMDataSyncHandler extends AbstractUMServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(UMDataSyncHandler.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RolePermissionDao rolePermissionDao;
    @Autowired
    private DepartmentDao departmentDao;

    @Value("${overseas_external_version.enable:false}")
    private Boolean overseasVersionEnabled;

    private final String DELETE_FLAG = "1";
    private final String USER_STATUS_LEAVE = "3";
    private final String CREATE_USER = "um_user";

    @Override
    public List<WdsUMPermission> relatedInsertingLocalPermissionConvert() {
        return null;
    }

    @Override
    public List<WdsUMRole> relatedInsertingLocalRoleConvert() {
        return null;
    }

    @Override
    public List<WdsUMRolePermission> relatedInsertingLocalRolePermissionConvert() {
        return null;
    }

    @Override
    public List<WdsUMUserRole> relatedInsertingLocalUserRoleConvert() {
        return null;
    }

    @Override
    public List<WdsUMPermission> relatedUpdatingLocalPermissionConvert() {
        return null;
    }

    @Override
    public List<WdsUMRole> relatedUpdatingLocalRoleConvert() {
        return null;
    }

    @Override
    public List<WdsUMRolePermission> relatedUpdatingLocalRolePermissionConvert() {
        return null;
    }

    @Override
    public List<WdsUMUserRole> relatedUpdatingLocalUserRoleConvert() {
        return null;
    }

    /**
     * @param systemPermission
     */
    @Override
    public void relatedIncSync(SystemPermission systemPermission) {
        if (overseasVersionEnabled){
            LOGGER.info(" skip sync data from UM.");
            return;
        }
        LOGGER.info("====== Start to incrementally sync data from UM ======");
        LOGGER.info("systemPermission data : {}", CustomObjectMapper.transObjectToJson(systemPermission));
        saveUsers(systemPermission.getUsers());
        saveRole(systemPermission.getRoles());
        savePermissions(systemPermission.getPermissions());
        saveUserRoles(systemPermission.getUserRoles());
        saveRolePermissions(systemPermission.getRolePermissions());
        LOGGER.info("====== Sync data from UM is complete ======");
    }

    @Override
    public void relatedFullSync(SystemPermission systemPermission) throws Exception {
// doing nothing
    }

    public void saveUsers(List<UMUser> umUsers) {
        if (CollectionUtils.isEmpty(umUsers)) {
            return;
        }
        LOGGER.info("UM users: {}", CustomObjectMapper.transObjectToJson(umUsers));
        for (UMUser umUser : umUsers) {
            try {
                User userMapping = convert2User(umUser);
                User userInDb = userDao.findByUsername(userMapping.getUsername());
//            离职
                if (USER_STATUS_LEAVE.equals(umUser.getStatus())) {
                    if (userInDb != null) {
                        userRoleDao.deleteByUser(userInDb);
                    }
                    continue;
                }

                if (userInDb == null) {
                    User user = userMapping;
                    user.setCreateUser(CREATE_USER);
                    user.setCreateTime(DateUtils.now());
                    userDao.saveUser(user);
                } else {
                    userInDb.setDepartment(userMapping.getDepartment());
                    userInDb.setDepartmentName(userMapping.getDepartmentName());
                    userInDb.setChineseName(userMapping.getChineseName());
                    userInDb.setModifyUser(CREATE_USER);
                    userInDb.setModifyTime(DateUtils.now());
                    userDao.saveUser(userInDb);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to handle user. UMUser: {}, error: {}", umUser.toString(), e.getMessage());
            }
        }

        LOGGER.info("Success to sync users.");
    }

    public void saveRole(List<UMRole> umRoles) {
        if (CollectionUtils.isEmpty(umRoles)) {
            return;
        }
        LOGGER.info("UM roles: {}", CustomObjectMapper.transObjectToJson(umRoles));
        for (UMRole umRole : umRoles) {
            try {
                Role roleMapping = convert2Role(umRole);
                Role roleInDb = roleDao.findByRoleName(roleMapping.getName());

                if (DELETE_FLAG.equals(umRole.getDelFlag())) {
                    if (roleInDb != null) {
                        roleDao.deleteRole(roleInDb);
                    }
                    continue;
                }

                if (roleInDb == null) {
                    Role role = roleMapping;
                    role.setCreateUser(CREATE_USER);
                    role.setCreateTime(DateUtils.now());
                    roleDao.saveRole(role);
                } else {
                    roleInDb.setZnName(roleMapping.getZnName());
                    roleInDb.setDepartment(roleMapping.getDepartment());
                    roleInDb.setModifyUser(CREATE_USER);
                    roleInDb.setModifyTime(DateUtils.now());
                    roleDao.saveRole(roleInDb);
                }

            } catch (Exception e) {
                LOGGER.warn("Failed to handle role. UMRole: {}, error: {}", umRole.toString(), e.getMessage());
            }
        }

        LOGGER.info("Success to sync roles.");
    }

    public void savePermissions(List<UMPermission> umPermissions) {
        if (CollectionUtils.isEmpty(umPermissions)) {
            return;
        }
        LOGGER.info("UM permission: {}", CustomObjectMapper.transObjectToJson(umPermissions));
        for (UMPermission umPermission : umPermissions) {
            try {
                Permission permissionMapping = convert2Permission(umPermission);
                Permission permissionInDb = permissionDao.findByResCode(permissionMapping.getResCode());

                if (DELETE_FLAG.equals(umPermission.getDelFlag())) {
                    if (permissionInDb != null) {
                        permissionDao.deletePermission(permissionInDb);
                    }
                    continue;
                }

                if (permissionInDb == null) {
                    Permission permission = permissionDao.findByMethodAndUrl(permissionMapping.getMethod(), permissionMapping.getUrl());
                    if (permission != null) {
                        throw new UnExpectedRequestException("Exists same method and url.");
                    }
                    permission = permissionMapping;
                    permission.setCreateUser(CREATE_USER);
                    permission.setCreateTime(DateUtils.now());
                    permissionDao.savePermission(permission);
                } else {
                    permissionInDb.setMethod(permissionMapping.getMethod());
                    permissionInDb.setUrl(permissionMapping.getUrl());
                    permissionInDb.setEnName(permissionMapping.getEnName());
                    permissionInDb.setCnName(permissionMapping.getCnName());
                    permissionInDb.setModifyUser(CREATE_USER);
                    permissionInDb.setModifyTime(DateUtils.now());
                    permissionDao.savePermission(permissionInDb);
                }
            } catch (UnExpectedRequestException e) {
                LOGGER.warn("Failed to handle permission due to invalid parameters. UMPermission: {}, error: {}", umPermission.toString(), e.getMessage());
            } catch (Exception e) {
                LOGGER.warn("Failed to handle permission. UMPermission: {}, error: {}", umPermission.toString(), e.getMessage());
            }

        }

        LOGGER.info("Success to sync permissions.");
    }

    public void saveUserRoles(List<UMUserRole> umUserRoles) {
        if (CollectionUtils.isEmpty(umUserRoles)) {
            return;
        }
        LOGGER.info("UM user roles: {}", CustomObjectMapper.transObjectToJson(umUserRoles));
        for (UMUserRole umUserRole : umUserRoles) {
            try {
                UserRole userRoleMapping = convert2UserRole(umUserRole);
                if (userRoleMapping == null) {
                    throw new UnExpectedRequestException("Cannot to bind user and role.");
                }
                UserRole userRoleInDb = userRoleDao.findByUserAndRole(userRoleMapping.getUser(), userRoleMapping.getRole());
                if (DELETE_FLAG.equals(umUserRole.getDelFlag())) {
                    if (userRoleInDb != null) {
                        userRoleDao.deleteUserRole(userRoleInDb);
                    }
                    continue;
                }

                if (userRoleInDb == null) {
                    UserRole userRole = userRoleMapping;
                    userRole.setId(UuidGenerator.generate());
                    userRole.setCreateUser(CREATE_USER);
                    userRole.setCreateTime(DateUtils.now());
                    userRoleDao.saveUserRole(userRole);
                } else {
                    userRoleInDb.setUser(userRoleMapping.getUser());
                    userRoleInDb.setRole(userRoleInDb.getRole());
                    userRoleInDb.setModifyUser(CREATE_USER);
                    userRoleInDb.setModifyTime(DateUtils.now());
                    userRoleDao.saveUserRole(userRoleInDb);
                }
            } catch (UnExpectedRequestException e) {
                LOGGER.warn("Failed to handle userRole due incorrect parameters. UMUserRole: {}, error: {}", umUserRole.toString(), e.getMessage());
            } catch (Exception e) {
                LOGGER.warn("Failed to handle userRole. UMUserRole: {}, error: {}", umUserRole.toString(), e.getMessage());
            }
        }

        LOGGER.info("Success to sync user_roles.");
    }

    public void saveRolePermissions(List<UMRolePermission> umRolePermissions) {
        if (CollectionUtils.isEmpty(umRolePermissions)) {
            return;
        }
        LOGGER.info("UM role permissions: {}", CustomObjectMapper.transObjectToJson(umRolePermissions));
        for (UMRolePermission umRolePermission : umRolePermissions) {
            try {
                RolePermission rolePermissionMapping = convert2RolePermission(umRolePermission);
                if (rolePermissionMapping == null) {
                    throw new UnExpectedRequestException("Cannot to bind role and permission.");
                }
                RolePermission rolePermissionInDb = rolePermissionDao.findByRoleAndPermission(rolePermissionMapping.getRole(), rolePermissionMapping.getPermission());
                if (DELETE_FLAG.equals(umRolePermission.getDelFlag())) {
                    if (rolePermissionInDb != null) {
                        rolePermissionDao.deleteRolePermission(rolePermissionInDb);
                    }
                    continue;
                }

                if (rolePermissionInDb == null) {
                    RolePermission rolePermission = rolePermissionMapping;
                    rolePermission.setId(UuidGenerator.generate());
                    rolePermission.setCreateUser(CREATE_USER);
                    rolePermission.setCreateTime(DateUtils.now());
                    rolePermissionDao.saveRolePermission(rolePermission);
                } else {
                    rolePermissionInDb.setPermission(rolePermissionMapping.getPermission());
                    rolePermissionInDb.setRole(rolePermissionMapping.getRole());
                    rolePermissionInDb.setModifyUser(CREATE_USER);
                    rolePermissionInDb.setModifyTime(DateUtils.now());
                    rolePermissionDao.saveRolePermission(rolePermissionInDb);
                }
            } catch (UnExpectedRequestException e) {
                LOGGER.warn("Failed to handle rolePermission due incorrect parameters. UMRolePermission: {}, error: {}", umRolePermission.toString(), e.getMessage());
            } catch (Exception e) {
                LOGGER.warn("Failed to handle rolePermission. UMRolePermission: {}, error: {}", umRolePermission.toString(), e.getMessage());
            }
        }

        LOGGER.info("Success to sync role_permissions.");
    }

    public User convert2User(UMUser umUser) {
        User user = new User();
        user.setUsername(umUser.getUserId());
        user.setChineseName(umUser.getUserName());
        if(StringUtils.isNotBlank(umUser.getDeptCode())) {
            Department department = departmentDao.findByCode(umUser.getDeptCode());
            if (department != null) {
                user.setDepartmentName(department.getName());
                user.setDepartment(department);
            }
        }
        return user;
    }

    public Role convert2Role(UMRole umRole) {
        Role role = new Role();
        role.setName(umRole.getRoleCode());
        role.setZnName(umRole.getRoleNameCn());
        if (role.getName().contains(RoleSystemTypeEnum.PROJECTOR.getMessage())
                || role.getName().contains(RoleSystemTypeEnum.ADMIN.getMessage())) {
            role.setRoleType(RoleTypeEnum.SYSTEM_ROLE.getCode());
        } else {
            role.setRoleType(RoleTypeEnum.POSITION_ROLE.getCode());
        }

//        仅录入部门，且只有系统角色
        if (StringUtils.isNotBlank(umRole.getDeptList())) {
            String[] deptCodes = StringUtils.split(umRole.getDeptList(), SpecCharEnum.COMMA.getValue());
            if (deptCodes.length == 1 && RoleTypeEnum.SYSTEM_ROLE.getCode().equals(role.getRoleType())) {
                Department department = departmentDao.findByCode(deptCodes[0]);
                if (department != null && department.getParentId() == null) {
                    role.setDepartment(department);
                }
            }
        }
        return role;
    }

    /**
     *  res_content = method+url,例如：POST:/qualitis/api/v1/ai/**
     * 判重res_content，重复抛错
     * 去掉en_name的重复判断方式
     * @param umPermission
     * @return
     */
    public Permission convert2Permission(UMPermission umPermission) throws UnExpectedRequestException {
        Permission permission = new Permission();
        String resContent = umPermission.getResContent();
        String[] resources = StringUtils.split(resContent, SpecCharEnum.COLON.getValue());
        if (resources.length != 2) {
            throw new UnExpectedRequestException("resContent is incorrect format: " + resContent);
        }
        permission.setResCode(umPermission.getResCode());
        permission.setMethod(resources[0]);
        permission.setUrl(resources[1]);
        permission.setCnName(umPermission.getResNameCn());
        permission.setEnName(umPermission.getResName());
        return permission;
    }

    public UserRole convert2UserRole(UMUserRole umUserRole) {
        String username = umUserRole.getUserId();
        String roleName = umUserRole.getRoleCode();
        User user = userDao.findByUsername(username);
        Role role = roleDao.findByRoleName(roleName);
        if (user == null || role == null) {
            return null;
        }

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(user);
        return userRole;
    }

    /**
     * @param umRolePermission
     * @return
     */
    public RolePermission convert2RolePermission(UMRolePermission umRolePermission) {
        Role role = roleDao.findByRoleName(umRolePermission.getRoleCode());
        Permission permission = permissionDao.findByResCode(umRolePermission.getResCode());
        if (role == null || permission == null) {
            return null;
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        return rolePermission;
    }

}
