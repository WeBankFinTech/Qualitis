package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ImsmetricIdentify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
public interface ImsmetricIdentifyRepository extends JpaRepository< ImsmetricIdentify, String > {

    /**
     * query Identify
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT /*slave*/ t.* from qualitis_imsmetric_identify t WHERE t.update_time > ?1  and t.update_time < ?2 group by t.metric_id", nativeQuery = true)
    List< ImsmetricIdentify > queryIdentify(String startDate, String endDate);

    /**
     * query Identify
     * @param metricIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT  /*slave*/ t.* from qualitis_imsmetric_identify t WHERE t.update_time > ?1  and t.update_time < ?2 and t.metric_id in (?3) group by t.metric_id", nativeQuery = true)
    List< ImsmetricIdentify > queryIdentify(String startDate, String endDate, String metricIds);

    /**
     * query Identify
     * @param metricIds
     * @return
     */
    @Query(value = "SELECT /*slave*/ t.* from qualitis_imsmetric_identify t WHERE coalesce(?1,null) is null or t.metric_id in (?1) ", nativeQuery = true)
    List< ImsmetricIdentify > queryIdentify(List< Long > metricIds);

}
