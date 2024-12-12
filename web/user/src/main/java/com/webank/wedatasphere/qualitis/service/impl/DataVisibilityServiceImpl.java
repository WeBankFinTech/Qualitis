package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.DataVisibilityDao;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-15 10:16
 * @description
 */
@Service
public class DataVisibilityServiceImpl implements DataVisibilityService {

    @Autowired
    private DataVisibilityDao dataVisibilityDao;

    @Override
    public void delete(Long tableDataId, TableDataTypeEnum tableDataType) {
        dataVisibilityDao.delete(tableDataId, tableDataType.getCode());
    }

    @Override
    public List<DataVisibility> filter(Long tableDataId, TableDataTypeEnum tableDataType) {
        return dataVisibilityDao.findByTableDataIdAndTableDataType(tableDataId, tableDataType.getCode());
    }

    @Override
    public List<DataVisibility> filterByIds(List<Long> tableDataIds, TableDataTypeEnum tableDataType) {
        return dataVisibilityDao.findByTableDataIdsAndTableDataType(tableDataIds, tableDataType.getCode());
    }

    @Override
    public List<DepartmentSubInfoResponse> saveBatch(Long dataId, TableDataTypeEnum tableDataTypeEnum, List<DepartmentSubInfoRequest> visibilityDepartmentNameList) {
        if (CollectionUtils.isEmpty(visibilityDepartmentNameList)) {
            return Collections.emptyList();
        }
        List<DataVisibility> dataVisibilityList = visibilityDepartmentNameList.stream().map(departmentInfoRequest -> {
            DataVisibility dataVisibility = new DataVisibility();
            dataVisibility.setTableDataId(dataId);
            dataVisibility.setTableDataType(tableDataTypeEnum.getCode());
            dataVisibility.setDepartmentSubId(departmentInfoRequest.getId());
            dataVisibility.setDepartmentSubName(departmentInfoRequest.getName());
            return dataVisibility;
        }).collect(Collectors.toList());
        dataVisibilityDao.saveAll(dataVisibilityList);

        return dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
    }

}
