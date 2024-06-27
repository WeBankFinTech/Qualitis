package com.webank.wedatasphere.qualitis.report.dao;

import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscriptionRecordDao {

    /**
     * Save
     *
     * @param subscriptionRecord
     * @return
     */
    SubscriptionRecord save(SubscriptionRecord subscriptionRecord);

    /**
     * find by id
     *
     * @param subscriptionRecordId
     * @return
     */
    SubscriptionRecord findById(Long subscriptionRecordId);

    /**
     * find Match Project And Frequency
     *
     * @param projectId
     * @param executionFrequency
     * @return
     */
    SubscriptionRecord findMatchProjectAndFrequency(Long projectId, Integer executionFrequency);

    /**
     * delete
     *
     * @param subscriptionRecord
     */
    void delete(SubscriptionRecord subscriptionRecord);

}
