package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author
 * @date 2024-07-17 9:51
 * @description
 */
public interface ScheduledWorkflowBusinessRepository extends JpaRepository<ScheduledWorkflowBusiness, Long> {

    /**
     * find by project_id
     * @param projectId
     * @return
     */
    List<ScheduledWorkflowBusiness> findByProjectId(Long projectId);

    /**
     * get by project id and name
     * @param projectId
     * @param name
     * @return
     */
    ScheduledWorkflowBusiness getByProjectIdAndName(Long projectId, String name);
}
