package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 15:14
 * @description
 */
public interface ScheduledSignalRepository extends JpaRepository<ScheduledSignal, Long> {

    /**
     * find By Scheduled Project
     * @param scheduledProject
     * @return
     */
    List<ScheduledSignal> findByScheduledProject(ScheduledProject scheduledProject);

    /**
     * find By Workflow
     * @param scheduledWorkflow
     * @return
     */
    List<ScheduledSignal> findByScheduledWorkflow(ScheduledWorkflow scheduledWorkflow);

    /**
     * find By Workflow
     * @param scheduledWorkflowList
     * @return
     */
    List<ScheduledSignal> findByScheduledWorkflowIn(List<ScheduledWorkflow> scheduledWorkflowList);

    /**
     * delete by workflows
     * @param scheduledWorkflowList
     */
    void deleteByScheduledWorkflowIn(List<ScheduledWorkflow> scheduledWorkflowList);
}
