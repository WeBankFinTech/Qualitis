package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-22 16:16
 * @description
 */
public interface SubDepartmentPermissionService {

    /**
     * Get editable status of data
     * @param createUserOfData
     * @param devDepartmentId
     * @param opsDepartmentId
     * @return
     * @throws UnExpectedRequestException
     */
    boolean isEditable(String createUserOfData, Long devDepartmentId, Long opsDepartmentId) throws UnExpectedRequestException;

    /**
     * Get editable status of data
     * @param roleType
     * @param loginUser
     * @param createUserOfData
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param subDepartmentList
     * @return
     */
    boolean isEditable(Integer roleType, User loginUser, String createUserOfData, Long devDepartmentId, Long opsDepartmentId, List<Long> subDepartmentList);

    /**
     * Checking permission of sub department
     * @param roleType
     * @param loginUser
     * @param createUserOfData
     * @param devDepartmentId
     * @param opsDepartmentId
     * @param allVisibility
     * @throws PermissionDeniedRequestException
     * @throws UnExpectedRequestException
     */
    void checkEditablePermission(Integer roleType, User loginUser, String createUserOfData, Long devDepartmentId, Long opsDepartmentId, boolean allVisibility) throws PermissionDeniedRequestException, UnExpectedRequestException;

    /**
     * get ids of sub department by ids of department
     * @param departmentIds
     * @return
     * @throws UnExpectedRequestException
     */
    List<Long> getSubDepartmentIdList(List<Long> departmentIds) throws UnExpectedRequestException;

    /**
     * Checking accessible permission of user
     * @param tableDataId
     * @param tableDataTypeEnum
     * @param dataVisibilityPermissionDto
     * @throws UnExpectedRequestException
     */
    void checkAccessiblePermission(Long tableDataId, TableDataTypeEnum tableDataTypeEnum, DataVisibilityPermissionDto dataVisibilityPermissionDto) throws UnExpectedRequestException;

    /**
     * checking accessible permission
     * @param loginUser
     * @param tableDataId
     * @param tableDataTypeEnum
     * @param dataVisibilityPermissionDto
     * @throws UnExpectedRequestException
     */
    void checkAccessiblePermission(User loginUser, Long tableDataId, TableDataTypeEnum tableDataTypeEnum, DataVisibilityPermissionDto dataVisibilityPermissionDto) throws UnExpectedRequestException;
}
