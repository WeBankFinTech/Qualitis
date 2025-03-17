package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledSignalDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledSignalRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 15:19
 * @description
 */
@Repository
public class ScheduledSignalDaoImpl implements ScheduledSignalDao {

    @Autowired
    private ScheduledSignalRepository scheduledSignalRepository;

    @Override
    public void saveAll(List<ScheduledSignal> list) {
        scheduledSignalRepository.saveAll(list);
    }

    @Override
    public void deleteAll(List<ScheduledSignal> list) {
        scheduledSignalRepository.deleteAll(list);
    }

    @Override
    public void deleteByScheduledWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList) {
        scheduledSignalRepository.deleteByScheduledWorkflowIn(scheduledWorkflowList);
    }

    @Override
    public List<ScheduledSignal> findByProject(ScheduledProject scheduledProject) {
        return scheduledSignalRepository.findByScheduledProject(scheduledProject);
    }

    @Override
    public List<ScheduledSignal> findByWorkflow(ScheduledWorkflow scheduledWorkflow) {
        return scheduledSignalRepository.findByScheduledWorkflow(scheduledWorkflow);
    }

    @Override
    public List<ScheduledSignal> findByWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList) {
        return scheduledSignalRepository.findByScheduledWorkflowIn(scheduledWorkflowList);
    }
}
