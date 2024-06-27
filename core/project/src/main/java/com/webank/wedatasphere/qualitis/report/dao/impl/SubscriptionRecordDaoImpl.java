package com.webank.wedatasphere.qualitis.report.dao.impl;

import com.webank.wedatasphere.qualitis.report.dao.SubscriptionRecordDao;
import com.webank.wedatasphere.qualitis.report.dao.repository.SubscriptionRecordRepository;
import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class SubscriptionRecordDaoImpl implements SubscriptionRecordDao {

    @Autowired
    private SubscriptionRecordRepository subscriptionRecordRepository;

    @Override
    public SubscriptionRecord save(SubscriptionRecord subscriptionRecord) {
        return subscriptionRecordRepository.save(subscriptionRecord);
    }

    @Override
    public SubscriptionRecord findById(Long subscriptionRecordId) {
        if (subscriptionRecordRepository.findById(subscriptionRecordId).isPresent()) {
            return subscriptionRecordRepository.findById(subscriptionRecordId).get();
        } else {
            return null;
        }
    }

    @Override
    public SubscriptionRecord findMatchProjectAndFrequency(Long projectId, Integer executionFrequency) {
        return subscriptionRecordRepository.findMatchProjectAndFrequency(projectId, executionFrequency);
    }

    @Override
    public void delete(SubscriptionRecord subscriptionRecord) {
        subscriptionRecordRepository.delete(subscriptionRecord);
    }
}
