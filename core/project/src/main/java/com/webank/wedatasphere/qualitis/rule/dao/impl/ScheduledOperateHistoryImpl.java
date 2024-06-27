package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.ScheduledOperateHistoryDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.ScheduledOperateRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledOperateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-13 17:42
 * @description
 */
@Repository
public class ScheduledOperateHistoryImpl implements ScheduledOperateHistoryDao {

    @Autowired
    private ScheduledOperateRepository scheduledOperateRepository;

    @Override
    public void add(ScheduledOperateHistory scheduledOperateHistory) {
        scheduledOperateRepository.save(scheduledOperateHistory);
    }
}
