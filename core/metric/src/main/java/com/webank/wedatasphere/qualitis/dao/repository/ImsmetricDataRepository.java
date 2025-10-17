package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ImsmetricData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
public interface ImsmetricDataRepository extends JpaRepository<ImsmetricData, Long> {

    /**
     * query Ims metric Data
     *
     * @param metricIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT  /*slave*/ t.* FROM qualitis_imsmetric_data_new t where t.metric_id in (?1)  " +
            " AND t.data_date >= UNIX_TIMESTAMP(?2) AND t.data_date <= (UNIX_TIMESTAMP(?3))", nativeQuery = true)
    List<ImsmetricData> queryImsmetricData(String metricIds, String startDate, String endDate);

    /**
     * query Ims metric Data
     *
     * @param metricIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT /*slave*/ t.* FROM qualitis_imsmetric_data_new t where t.data_date >= UNIX_TIMESTAMP(?2) AND t.data_date <= (UNIX_TIMESTAMP(?3)) and " +
            "(coalesce(?1,null) is null or t.metric_id in (?1))  order by t.data_date", nativeQuery = true)
    List<ImsmetricData> queryImsmetricDatas(List<Long> metricIds, int startDate, int endDate);

    /**
     * get all proxy users
     *
     * @return
     */
    @Query(value = "select DISTINCT proxy_user from qualitis_imsmetric_collect", nativeQuery = true)
    List<String> findAllDataUsers();

}
