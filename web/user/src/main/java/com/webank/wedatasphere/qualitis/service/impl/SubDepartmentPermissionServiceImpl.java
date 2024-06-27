package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-22 16:16
 * @description
 */
@Service
public class SubDepartmentPermissionServiceImpl implements SubDepartmentPermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubDepartmentPermissionServiceImpl.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DataVisibilityService dataVisibilityService;

    @Value("${devOps.enable}")
    private Boolean isDevOpsModel;

    private HttpServletRequest httpServletRequest;

    /**
     * key: departmentCode, value: devAndOpsIds
     */
    private final Cache<String, List<Long>> devAndOpsIdsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public SubDepartmentPermissionServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean isEditable(String createUserOfData, Long devDepartmentIdOfData, Long opsDepartmentIdOfData) throws UnExpectedRequestException {
        String userName = HttpUtils.getUserName(httpServletRequest);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        List<Long> departmentIds = userRoles.stream().map(UserRole::getRole)
                .filter(Objects::nonNull).map(Role::getDepartment)
                .filter(Objects::nonNull).map(Department::getId)
                .collect(Collectors.toList());
        if (Objects.nonNull(loginUser.getDepartment())) {
            departmentIds.add(loginUser.getDepartment().getId());
        }
        List<Long> devAndOpsInfoWithinDeptList = getSubDepartmentIdList(departmentIds);
        return isEditable(roleType, loginUser, createUserOfData, devDepartmentIdOfData
                , opsDepartmentIdOfData, devAndOpsInfoWithinDeptList);
    }

    @Override
    public boolean isEditable(Integer roleType, User loginUser, String createUserOfData, Long devDepartmentIdOfData, Long opsDepartmentIdOfData, List<Long> subDepartmentList) {
        boolean createdBySelf = loginUser.getUsername().equals(createUserOfData);
        boolean isAdmin = RoleSystemTypeEnum.ADMIN.getCode().equals(roleType);
        if (createdBySelf || isAdmin) {
            return true;
        }

        boolean isProjector = RoleSystemTypeEnum.PROJECTOR.getCode().equals(roleType);
        boolean isDepartmentAdmin = RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode().equals(roleType);

        List<Long> editableDevOrOpsDepartmentList = chooseEditableDepartmentByEnvironment(devDepartmentIdOfData, opsDepartmentIdOfData);
        if (isProjector) {
            return editableDevOrOpsDepartmentList.contains(loginUser.getSubDepartmentCode());
        }
        if (isDepartmentAdmin) {
            return subDepartmentList.stream().anyMatch(editableDevOrOpsDepartmentList::contains);
        }
        return false;
    }

    @Override
    public void checkEditablePermission(Integer roleType, User loginUser, String createUserOfData, Long devDepartmentId, Long opsDepartmentId, boolean allVisibility) throws PermissionDeniedRequestException, UnExpectedRequestException {
        boolean createdBySelf = loginUser.getUsername().equals(createUserOfData);
        boolean isAdmin = RoleSystemTypeEnum.ADMIN.getCode().equals(roleType);
        if (createdBySelf || isAdmin) {
            return;
        }

        boolean isProjector = RoleSystemTypeEnum.PROJECTOR.getCode().equals(roleType);
        boolean isDepartmentAdmin = RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode().equals(roleType);

//        If this data is visible for all department, then department_admin or admin can edit it
        if (allVisibility && isProjector) {
            LOGGER.warn("Projector can't to set visibility to all department");
            throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
        }

        List<Long> editableDevOrOpsDepartmentList = chooseEditableDepartmentByEnvironment(devDepartmentId, opsDepartmentId);
        if (isProjector && !editableDevOrOpsDepartmentList.contains(loginUser.getSubDepartmentCode())) {
            throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}: illegal department", 403);
        }
        if (isDepartmentAdmin) {
            List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole)
                    .filter(Objects::nonNull).map(Role::getDepartment)
                    .filter(Objects::nonNull).map(Department::getId)
                    .collect(Collectors.toList());
            departmentIds.add(loginUser.getDepartment().getId());
            List<Long> devAndOpsInfoWithinDeptList = getSubDepartmentIdList(departmentIds);
            if (CollectionUtils.isEmpty(devAndOpsInfoWithinDeptList)) {
                LOGGER.warn("Failed to get sub-departments by department code");
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}: invalid department", 403);
            }
            if (devAndOpsInfoWithinDeptList.stream().noneMatch(editableDevOrOpsDepartmentList::contains)) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}: illegal department", 403);
            }
        }
    }

    /**
     * @param devDepartmentId
     * @param opsDepartmentId
     * @return
     */
    private List<Long> chooseEditableDepartmentByEnvironment(Long devDepartmentId, Long opsDepartmentId) {
        if (isDevOpsModel) {
            return Arrays.asList(opsDepartmentId);
        }
        return Arrays.asList(devDepartmentId, opsDepartmentId);
    }

    @Override
    public List<Long> getSubDepartmentIdList(List<Long> departmentIds) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(departmentIds)) {
            return Collections.emptyList();
        }
        List<Department> departmentList = departmentDao.findByIds(departmentIds);
        List<Long> allDevAndOpsInfoWithinDeptList = Lists.newLinkedList();
        for (Department department : departmentList) {
            String departmentCode = department.getDepartmentCode();
            if (StringUtils.isEmpty(departmentCode)) {
                continue;
            }
            List<Long> devAndOpsInfoWithinDeptList = devAndOpsIdsCache.getIfPresent(departmentCode);
            if (CollectionUtils.isNotEmpty(devAndOpsInfoWithinDeptList)) {
                LOGGER.info("Getting devAndOpsIds from local cache, key: {}", departmentCode);
                allDevAndOpsInfoWithinDeptList.addAll(devAndOpsInfoWithinDeptList);
            } else {
                LOGGER.info("Query department list from CMDB by code, code:{}", departmentCode);
                List<DepartmentSubResponse> devAndOpsInfoList = operateCiService.getDevAndOpsInfo(Integer.valueOf(departmentCode));
                if (CollectionUtils.isNotEmpty(devAndOpsInfoList)) {
                    devAndOpsInfoWithinDeptList = devAndOpsInfoList.stream().map(DepartmentSubResponse::getId).map(Long::valueOf).collect(Collectors.toList());
                    allDevAndOpsInfoWithinDeptList.addAll(devAndOpsInfoWithinDeptList);
                    LOGGER.info("Adding devAndOpsIds to local cache, key: {}", departmentCode);
                    devAndOpsIdsCache.put(departmentCode, devAndOpsInfoWithinDeptList);
                }
            }
        }
        return allDevAndOpsInfoWithinDeptList;
    }

    @Override
    public void checkAccessiblePermission(User loginUser, Long tableDataId, TableDataTypeEnum tableDataTypeEnum, DataVisibilityPermissionDto dataVisibilityPermissionDto) throws UnExpectedRequestException {
        boolean createdBySelf = loginUser.getUsername().equals(dataVisibilityPermissionDto.getCreateUser());
        if (createdBySelf) {
            return;
        }
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);

        boolean isAdmin = RoleSystemTypeEnum.ADMIN.getCode().equals(roleType);
        if (isAdmin) {
            return;
        }
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(tableDataId, tableDataTypeEnum);
        List<Long> visibilitySubDepartmentIds = dataVisibilityList.stream().map(DataVisibility::getDepartmentSubId).distinct().collect(Collectors.toList());
        boolean isVisibleForAllDept = visibilitySubDepartmentIds.contains(DataVisibilityService.ALL_DEPARTMENT_VISIBILITY);
        if (isVisibleForAllDept) {
            return;
        }

        boolean isDepartmentAdmin = RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode().equals(roleType);
        if (isDepartmentAdmin) {
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole)
                    .filter(Objects::nonNull).map(Role::getDepartment)
                    .filter(Objects::nonNull).map(Department::getId)
                    .collect(Collectors.toList());
            if (Objects.nonNull(loginUser.getDepartment())) {
                departmentIds.add(loginUser.getDepartment().getId());
            }
            List<Long> subDepartmentIdList = getSubDepartmentIdList(departmentIds);
            boolean accessible = visibilitySubDepartmentIds.stream().anyMatch(subDepartmentIdList::contains)
                    || subDepartmentIdList.contains(dataVisibilityPermissionDto.getDevDepartmentId())
                    || subDepartmentIdList.contains(dataVisibilityPermissionDto.getOpsDepartmentId());
            if (accessible) {
                return;
            }
        }

        boolean isProjector = RoleSystemTypeEnum.PROJECTOR.getCode().equals(roleType);
        if (isProjector){
            Long departmentSubId = loginUser.getSubDepartmentCode();
            boolean accessible = Objects.nonNull(departmentSubId) && (visibilitySubDepartmentIds.contains(departmentSubId)
                    || departmentSubId.equals(dataVisibilityPermissionDto.getDevDepartmentId())
                    || departmentSubId.equals(dataVisibilityPermissionDto.getOpsDepartmentId()));
            if (accessible) {
                return;
            }
        }
        throw new UnExpectedRequestException("Illegal Access!");
    }

    @Override
    public void checkAccessiblePermission(Long tableDataId, TableDataTypeEnum tableDataTypeEnum, DataVisibilityPermissionDto dataVisibilityPermissionDto) throws UnExpectedRequestException {
        User loginUser = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        checkAccessiblePermission(loginUser, tableDataId, tableDataTypeEnum, dataVisibilityPermissionDto);
    }

}
