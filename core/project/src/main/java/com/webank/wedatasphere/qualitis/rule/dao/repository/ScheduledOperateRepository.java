package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledOperateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-13 17:42
 * @description
 */
public interface ScheduledOperateRepository extends JpaRepository<ScheduledOperateHistory, Long>, JpaSpecificationExecutor<ScheduledOperateHistory> {

}
