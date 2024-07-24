package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;

import java.util.List;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:40
 * @description
 */
public interface ScheduledWorkflowDao {

    /**
     * save All
     * @param workflowList
     * @return
     */
    List<ScheduledWorkflow> saveAll(List<ScheduledWorkflow> workflowList);

    /**
     * find By Project
     * @param scheduledProject
     * @return
     */
    List<ScheduledWorkflow> findByScheduledProject(ScheduledProject scheduledProject);

    /**
     * find By scheduledProjectList and workflowNameList
     * @param scheduledProject
     * @param workflowNameList
     * @return
     */
    List<ScheduledWorkflow> findByScheduledProjectAndWorkflowNameList(ScheduledProject scheduledProject, List<String> workflowNameList);

    /**
     * find By scheduledProject and workflowName
     * @param scheduledProject
     * @param workflowName
     * @return
     */
    ScheduledWorkflow findByScheduledProjectAndWorkflowName(ScheduledProject scheduledProject, String workflowName);

    /**
     * find ById
     * @param id
     * @return
     */
    Optional<ScheduledWorkflow> findById(Long id);

    /**
     * delete By Workflow List
     * @param scheduledWorkflowList
     * @return
     */
    void deleteAll(List<ScheduledWorkflow> scheduledWorkflowList);
}
