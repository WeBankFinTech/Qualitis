package com.webank.wedatasphere.qualitis.report.dao.repository;

import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscriptionRecordRepository extends JpaRepository<SubscriptionRecord, Long> {


    /**
     * find Match Project And Frequency
     *
     * @param projectId
     * @param executionFrequency
     * @return
     */
    @Query(value = "select * from qualitis_subscription_record where project_id=?1 and execution_frequency=?2", nativeQuery = true)
    SubscriptionRecord findMatchProjectAndFrequency(Long projectId, Integer executionFrequency);
}
