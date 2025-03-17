package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledFrontBackRuleDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledFrontBackRuleRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class ScheduledFrontBackRuleDaoImpl implements ScheduledFrontBackRuleDao {

    @Autowired
    private ScheduledFrontBackRuleRepository scheduledFrontBackRuleRepository;

    @Override
    public void saveAll(Iterable<ScheduledFrontBackRule> scheduledFrontBackRule) {
        scheduledFrontBackRuleRepository.saveAll(scheduledFrontBackRule);
    }

    @Override
    public void saveScheduledFrontBackRule(ScheduledFrontBackRule scheduledFrontBackRule) {
        scheduledFrontBackRuleRepository.save(scheduledFrontBackRule);
    }

    @Override
    public void deleteScheduledFrontBackRule(ScheduledFrontBackRule scheduledFrontBackRule) {
        scheduledFrontBackRuleRepository.delete(scheduledFrontBackRule);
    }

    @Override
    public void deleteAllScheduledFrontBackRule(List<ScheduledFrontBackRule> scheduledFrontBackRuleList) {
        scheduledFrontBackRuleRepository.deleteInBatch(scheduledFrontBackRuleList);
    }

    @Override
    public List<ScheduledFrontBackRule> findByScheduledTask(ScheduledTask scheduledTask) {
        return scheduledFrontBackRuleRepository.findByScheduledTask(scheduledTask);
    }

    @Override
    public List<ScheduledFrontBackRule> findByScheduledTaskList(List<ScheduledTask> scheduledTaskList) {
        return scheduledFrontBackRuleRepository.findByScheduledTaskIn(scheduledTaskList);
    }

    @Override
    public Boolean isExists(List<Long> ruleGroupIdList) {
        long frontBackRuleCount = scheduledFrontBackRuleRepository.countByRuleGroups(ruleGroupIdList);
        return frontBackRuleCount > 0;
    }

    @Override
    public List<Map<String, Object>> findRuleGroupNotInFrontBackRule(Long projectId) {
        return scheduledFrontBackRuleRepository.findRuleGroupNotInFrontBackRule(projectId);
    }

    @Override
    public ScheduledFrontBackRule findById(Long scheduledTaskFrontBackId) {
        return scheduledFrontBackRuleRepository.findById(scheduledTaskFrontBackId).orElse(null);
    }

    @Override
    public List<ScheduledFrontBackRule> findAllById(List<Long> ids) {
        return scheduledFrontBackRuleRepository.findAllById(ids);
    }

    @Override
    public List<ScheduledFrontBackRule> findByRuleGroup(RuleGroup ruleGroup) {
        return scheduledFrontBackRuleRepository.findByRuleGroup(ruleGroup);
    }

    @Override
    public List<ScheduledFrontBackRule> findScheduledTaskAndRuleGroup(List<Long> scheduledTaskIds, List<Long> ruleGroupIds) {
        return scheduledFrontBackRuleRepository.findScheduledTaskAndRuleGroup(scheduledTaskIds,ruleGroupIds);
    }

}
