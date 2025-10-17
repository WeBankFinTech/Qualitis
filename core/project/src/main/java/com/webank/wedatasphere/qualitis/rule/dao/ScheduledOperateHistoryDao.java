package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledOperateHistory;

/**
 * @author
 * @date 2023-04-13 17:42
 * @description
 */
public interface ScheduledOperateHistoryDao {

    /**
     * add
     * @param scheduledOperateHistory
     */
    void add(ScheduledOperateHistory scheduledOperateHistory);
}
