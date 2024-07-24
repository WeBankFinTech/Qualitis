package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:25
 * @description
 */
public interface ScheduledProjectHistoryRepository extends JpaRepository<ScheduledProjectHistory, Long>, JpaSpecificationExecutor<ScheduledProjectHistory> {

}
