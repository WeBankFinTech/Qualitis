package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledFrontBackRuleRepository extends JpaRepository<ScheduledFrontBackRule, Long>, JpaSpecificationExecutor<ScheduledFrontBackRule> {

    /**
     * Find by scheduledTask to ScheduledFrontBackRule.
     * @param scheduledTask
     * @return
     */
    List<ScheduledFrontBackRule> findByScheduledTask(ScheduledTask scheduledTask);

    /**
     * Find by scheduledTask to ScheduledFrontBackRule.
     * @param scheduledTasks
     * @return
     */
    List<ScheduledFrontBackRule> findByScheduledTaskIn(List<ScheduledTask> scheduledTasks);

    /**
     * Find rule group by project id
     * condition1: Not exist in qualitis_scheduled_front_back_rule
     * condition2: At least one Rule
     * @param projectId
     * @return
     */
    @Query(value = "select distinct rg.id as rule_group_id, rg.rule_group_name as rule_group_name from qualitis_rule_group rg " +
            "left join qualitis_scheduled_front_back_rule fbr on rg.id = fbr.rule_group_id " +
            "INNER join qualitis_rule qr on qr.rule_group_id = rg.id " +
            "where rg.project_id = ?1 and fbr.rule_group_id is null"
            , nativeQuery = true)
    List<Map<String, Object>> findRuleGroupNotInFrontBackRule(Long projectId);

    /**
     * find By Rule Group
     * @param ruleGroup
     * @return
     */
    List<ScheduledFrontBackRule> findByRuleGroup(RuleGroup ruleGroup);

    /**
     * count By Rule Groups
     * @param ruleGroupIds
     * @return
     */
    @Query(value = "select count(0) from qualitis_scheduled_front_back_rule where rule_group_id in (?1)", nativeQuery = true)
    long countByRuleGroups(List<Long> ruleGroupIds);

}
