package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.TaskResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-14 9:59
 * @description
 */
public interface TaskResultStatusRepository extends JpaRepository<TaskResultStatus, Long> {

    /**
     * findByApplicationIdAndRuleIdAndStatus
     * @param applicationId
     * @param ruleId
     * @param status
     * @return
     */
    List<TaskResultStatus> findByApplicationIdAndRuleIdAndStatus(String applicationId, Long ruleId, Integer status);
}
