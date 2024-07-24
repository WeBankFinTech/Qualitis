package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowTaskRelationDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledWorkflowTaskRelationRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 10:11
 * @description
 */
@Repository
public class ScheduledWorkflowTaskRelationDaoImpl implements ScheduledWorkflowTaskRelationDao {

    @Autowired
    private ScheduledWorkflowTaskRelationRepository scheduledWorkflowTaskRelationRepository;

    @Override
    public void saveAll(List<ScheduledWorkflowTaskRelation> workflowTaskRelationList) {
        scheduledWorkflowTaskRelationRepository.saveAll(workflowTaskRelationList);
    }

    @Override
    public void save(ScheduledWorkflowTaskRelation workflowTaskRelation) {
        scheduledWorkflowTaskRelationRepository.save(workflowTaskRelation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByScheduledWorkflowList(List<ScheduledWorkflow> scheduledWorkflows) {
        scheduledWorkflowTaskRelationRepository.deleteByScheduledWorkflowIn(scheduledWorkflows);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByScheduledTaskList(List<ScheduledTask> scheduledTaskList) {
        scheduledWorkflowTaskRelationRepository.deleteByScheduledTaskIn(scheduledTaskList);
    }

    @Override
    public void deleteBatch(List<ScheduledWorkflowTaskRelation> taskRelationList) {
        scheduledWorkflowTaskRelationRepository.deleteAll(taskRelationList);
    }

    @Override
    public Boolean isExists(List<Long> ruleGroupIdList) {
        long workflowTaskRelationCount = scheduledWorkflowTaskRelationRepository.countByRuleGroups(ruleGroupIdList);
        return workflowTaskRelationCount > 0;
    }

    @Override
    public List<ScheduledWorkflowTaskRelation> findByWorkflow(ScheduledWorkflow scheduledWorkflow) {
        return scheduledWorkflowTaskRelationRepository.findByScheduledWorkflow(scheduledWorkflow);
    }

    @Override
    public List<ScheduledWorkflowTaskRelation> findByWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList) {
        return scheduledWorkflowTaskRelationRepository.findByScheduledWorkflowIn(scheduledWorkflowList);
    }

    @Override
    public List<ScheduledWorkflowTaskRelation> findByScheduledTaskList(List<ScheduledTask> scheduledTaskList) {
        return scheduledWorkflowTaskRelationRepository.findByScheduledTaskIn(scheduledTaskList);
    }

    @Override
    public List<Map<String, Object>> findRuleGroupMap(Long projectId) {
        return scheduledWorkflowTaskRelationRepository.findRuleGroupMap(projectId);
    }

    @Override
    public List<ScheduledWorkflowTaskRelation> findByRuleGroupIds(List<Long> ruleGroupIdList) {
        return scheduledWorkflowTaskRelationRepository.findByRuleGroupIdIn(ruleGroupIdList);
    }
}
