package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledWorkflowRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:41
 * @description
 */
@Repository
public class ScheduledWorkflowDaoImpl implements ScheduledWorkflowDao {

    @Autowired
    private ScheduledWorkflowRepository scheduledWorkflowRepository;

    @Override
    public List<ScheduledWorkflow> saveAll(List<ScheduledWorkflow> workflowList) {
        return scheduledWorkflowRepository.saveAll(workflowList);
    }

    @Override
    public List<ScheduledWorkflow> findByScheduledProject(ScheduledProject scheduledProject) {
        return scheduledWorkflowRepository.findByScheduledProject(scheduledProject);
    }

    @Override
    public List<ScheduledWorkflow> findByScheduledProjectAndWorkflowNameList(ScheduledProject scheduledProject, List<String> workflowNameList) {
        return scheduledWorkflowRepository.findByScheduledProjectAndNameIn(scheduledProject, workflowNameList);
    }

    @Override
    public ScheduledWorkflow findByScheduledProjectAndWorkflowName(ScheduledProject scheduledProject, String workflowName) {
        return scheduledWorkflowRepository.findByScheduledProjectAndName(scheduledProject, workflowName);
    }

    @Override
    public Optional<ScheduledWorkflow> findById(Long id) {
        return scheduledWorkflowRepository.findById(id);
    }

    @Override
    public void deleteAll(List<ScheduledWorkflow> scheduledWorkflowList) {
        scheduledWorkflowRepository.deleteAll(scheduledWorkflowList);
    }

}
