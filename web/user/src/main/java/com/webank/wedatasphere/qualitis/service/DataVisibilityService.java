package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-15 10:12
 * @description
 */
public interface DataVisibilityService {

    Long ALL_DEPARTMENT_VISIBILITY = 0L;

    /**
     * Removing
     * @param tableDataId
     * @param tableDataType
     */
    void delete(Long tableDataId, TableDataTypeEnum tableDataType);

    /**
     * Finding by condition
     * @param tableDataId
     * @param tableDataType
     * @return
     */
    List<DataVisibility> filter(Long tableDataId, TableDataTypeEnum tableDataType);

    /**
     * filter By Ids
     * @param tableDataIds
     * @param tableDataType
     * @return
     */
    List<DataVisibility> filterByIds(List<Long> tableDataIds, TableDataTypeEnum tableDataType);

    /**
     * Saving batch, and return response
     * @param dataId
     * @param tableDataTypeEnum
     * @param visibilityDepartmentNameList
     * @return
     */
    List<DepartmentSubInfoResponse> saveBatch(Long dataId, TableDataTypeEnum tableDataTypeEnum, List<DepartmentSubInfoRequest> visibilityDepartmentNameList);

}
