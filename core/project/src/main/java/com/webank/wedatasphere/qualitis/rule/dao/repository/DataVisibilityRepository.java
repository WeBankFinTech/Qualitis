package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-14 9:40
 * @description
 */
public interface DataVisibilityRepository extends JpaRepository<DataVisibility, Long> {

    /**
     * delete By Table Data Id And Table Data Type
     * @param tableDataId
     * @param tableDataType
     * @return
     */
    void deleteByTableDataIdAndTableDataType(Long tableDataId, String tableDataType);

    /**
     * find By Table Data Id And Table Data Type
     * @param tableDataId
     * @param tableDataType
     * @return
     */
    List<DataVisibility> findByTableDataIdAndTableDataType(Long tableDataId, String tableDataType);

    /**
     * find By Table Data Id In And Table Data Type
     * @param tableDataIds
     * @param tableDataType
     * @return
     */
    List<DataVisibility> findByTableDataIdInAndTableDataType(List<Long> tableDataIds, String tableDataType);
}
