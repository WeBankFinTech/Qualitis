package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledProjectHistoryDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledProjectHistoryRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProjectHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:36
 * @description
 */
@Repository
public class ScheduledProjectHistoryDaoImpl implements ScheduledProjectHistoryDao {

    @Autowired
    private ScheduledProjectHistoryRepository scheduledProjectHistoryRepository;

    @Override
    public void save(ScheduledProjectHistory projectHistory) {
        scheduledProjectHistoryRepository.save(projectHistory);
    }
}
