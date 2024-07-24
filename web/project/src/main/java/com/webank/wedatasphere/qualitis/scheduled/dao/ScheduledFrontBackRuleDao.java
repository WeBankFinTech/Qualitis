package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledFrontBackRuleDao {

    /**
     * Save all ScheduledFrontBackRule
     *
     * @param scheduledFrontBackRule
     */
    void saveAll(Iterable<ScheduledFrontBackRule> scheduledFrontBackRule);

    /**
     * Save ScheduledFrontBackRule
     *
     * @param scheduledFrontBackRule
     */
    void saveScheduledFrontBackRule(ScheduledFrontBackRule scheduledFrontBackRule);

    /**
     * delete ScheduledFrontBackRule
     * @param scheduledFrontBackRule
     * @return
     */
    void deleteScheduledFrontBackRule(ScheduledFrontBackRule scheduledFrontBackRule);

    /**
     * delete All ScheduledFrontBackRule
     * @param scheduledFrontBackRuleList
     * @return
     */
    void deleteAllScheduledFrontBackRule(List<ScheduledFrontBackRule> scheduledFrontBackRuleList);

    /**
     * find ScheduledFrontBackRule
     * @param scheduledTask
     * @return
     */
    List<ScheduledFrontBackRule> findByScheduledTask(ScheduledTask scheduledTask);

    /**
     * find ScheduledFrontBackRule
     * @param scheduledTaskList
     * @return
     */
    List<ScheduledFrontBackRule> findByScheduledTaskList(List<ScheduledTask> scheduledTaskList);

    /**
     * check ruleGroupId if exits
     * @param ruleGroupIdList
     * @return
     */
    Boolean isExists(List<Long> ruleGroupIdList);

    /**
     * get ruleGroup that not in ScheduledFrontBackRule
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findRuleGroupNotInFrontBackRule(Long projectId);

    /**
     * Find scheduledTaskFrontBackId by id
     * @param scheduledTaskFrontBackId
     * @return
     */
    ScheduledFrontBackRule findById(Long scheduledTaskFrontBackId);

    /**
     * findAllById
     * @param ids
     * @return
     */
    List<ScheduledFrontBackRule> findAllById(List<Long> ids);

    /**
     * findByRuleGroup
     * @param ruleGroup
     * @return
     */
    List<ScheduledFrontBackRule> findByRuleGroup(RuleGroup ruleGroup);


}
