package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:40
 * @description
 */
public interface ScheduledWorkflowRepository extends JpaRepository<ScheduledWorkflow, Long>, JpaSpecificationExecutor<ScheduledWorkflow> {

    /**
     * find By Scheduled Project
     * @param scheduledProject
     * @return
     */
    List<ScheduledWorkflow> findByScheduledProject(ScheduledProject scheduledProject);

    /**
     * find By Scheduled Project
     * @param scheduledProject
     * @param workflowNameList
     * @return
     */
    List<ScheduledWorkflow> findByScheduledProjectAndNameIn(ScheduledProject scheduledProject, List<String> workflowNameList);

    /**
     * find By Scheduled Project
     * @param scheduledProject
     * @param name
     * @return
     */
    ScheduledWorkflow findByScheduledProjectAndName(ScheduledProject scheduledProject, String name);

}
