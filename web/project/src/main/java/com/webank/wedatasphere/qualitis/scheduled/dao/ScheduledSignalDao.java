package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 15:14
 * @description
 */
public interface ScheduledSignalDao {

    /**
     * saveAll
     * @param list
     */
    void saveAll(List<ScheduledSignal> list);

    /**
     * deleteAll
     * @param list
     */
    void deleteAll(List<ScheduledSignal> list);

    /**
     * delete By ScheduledWorkflowList
     * @param scheduledWorkflowList
     */
    void deleteByScheduledWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList);

    /**
     * find By Project
     * @param scheduledProject
     * @return
     */
    List<ScheduledSignal> findByProject(ScheduledProject scheduledProject);

    /**
     * find by Workflow
     * @param scheduledWorkflow
     * @return
     */
    List<ScheduledSignal> findByWorkflow(ScheduledWorkflow scheduledWorkflow);

    /**
     * find by Workflow
     * @param scheduledWorkflowList
     * @return
     */
    List<ScheduledSignal> findByWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList);

}
