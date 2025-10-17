package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ImsmetricCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
public interface ImsmetricCheckResultRepository extends JpaRepository< ImsmetricCheckResult, Long > {

    /**
     * query Ims metric Check Result
     * @param metricIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT /*slave*/ t.* FROM qualitis_imsmetric_check_result t where date_format(t.create_time,'%Y%m%d') >= ?2 AND date_format(t.create_time,'%Y%m%d') <= ?3 and " +
            " (coalesce(?1,null) is null or t.metric_id in (?1))  order by t.create_time ", nativeQuery = true)
    List< ImsmetricCheckResult > queryImsmetricCheckResult(List< Long > metricIds, String startDate, String endDate);
}
