package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProjectHistory;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:35
 * @description
 */
public interface ScheduledProjectHistoryDao {

    /**
     * save
     * @param projectHistory
     * @return
     */
    void save(ScheduledProjectHistory projectHistory);

}
