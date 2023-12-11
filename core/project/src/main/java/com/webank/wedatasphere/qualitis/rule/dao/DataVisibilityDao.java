package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-14 9:44
 * @description
 */
public interface DataVisibilityDao {

    /**
     * Save batch DataVisibility
     * @param dataVisibilityList
     */
    void saveAll(List<DataVisibility> dataVisibilityList);

    /**
     * Delete DataVisibility by tableDataId and tableDataType
     * @param tableDataId
     * @param tableDataType
     */
    void delete(Long tableDataId, String tableDataType);

    /**
     * Filter by tableDataId and tableDataType
     * @param tableDataId
     * @param tableDataType
     * @return
     */
    List<DataVisibility> findByTableDataIdAndTableDataType(Long tableDataId, String tableDataType);

    /**
     * Filter by tableDataIds and tableDataType
     * @param tableDataIds
     * @param tableDataType
     * @return
     */
    List<DataVisibility> findByTableDataIdsAndTableDataType(List<Long> tableDataIds, String tableDataType);

}
