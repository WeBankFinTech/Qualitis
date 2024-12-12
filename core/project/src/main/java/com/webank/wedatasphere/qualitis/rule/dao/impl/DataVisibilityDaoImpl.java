package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.DataVisibilityDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.DataVisibilityRepository;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-14 9:44
 * @description
 */
@Repository
public class DataVisibilityDaoImpl implements DataVisibilityDao {

    @Autowired
    private DataVisibilityRepository dataVisibilityRepository;

    @Override
    public void saveAll(List<DataVisibility> dataVisibilityList) {
        dataVisibilityRepository.saveAll(dataVisibilityList);
    }

    @Override
    public void delete(Long tableDataId, String tableDataType) {
        dataVisibilityRepository.deleteByTableDataIdAndTableDataType(tableDataId, tableDataType);
    }

    @Override
    public List<DataVisibility> findByTableDataIdAndTableDataType(Long tableDataId, String tableDataType) {
        return dataVisibilityRepository.findByTableDataIdAndTableDataType(tableDataId, tableDataType);
    }

    @Override
    public List<DataVisibility> findByTableDataIdsAndTableDataType(List<Long> tableDataIds, String tableDataType) {
        return dataVisibilityRepository.findByTableDataIdInAndTableDataType(tableDataIds, tableDataType);
    }
}
