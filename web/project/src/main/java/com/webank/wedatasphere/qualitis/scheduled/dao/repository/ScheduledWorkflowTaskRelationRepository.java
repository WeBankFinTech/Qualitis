package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 10:08
 * @description
 */
public interface ScheduledWorkflowTaskRelationRepository extends JpaRepository<ScheduledWorkflowTaskRelation, Long>, JpaSpecificationExecutor<ScheduledWorkflowTaskRelation> {

    /**
     * find ByRule GroupId In
     * @param ruleGroupIdList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByRuleGroupIdIn(List<Long> ruleGroupIdList);

    /**
     * delete By Scheduled Workflow
     * @param scheduledWorkflowList
     * @return
     */
    void deleteByScheduledWorkflowIn(List<ScheduledWorkflow> scheduledWorkflowList);

    /**
     * delete By Scheduled Task In
     * @param scheduledTaskList
     * @return
     */
    void deleteByScheduledTaskIn(List<ScheduledTask> scheduledTaskList);

    /**
     * find By Scheduled Workflow
     * @param scheduledWorkflow
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByScheduledWorkflow(ScheduledWorkflow scheduledWorkflow);

    /**
     * find By Scheduled Workflow
     * @param scheduledWorkflowList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByScheduledWorkflowIn(List<ScheduledWorkflow> scheduledWorkflowList);

    /**
     * findByScheduledTaskList
     * @param scheduledTaskList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByScheduledTaskIn(List<ScheduledTask> scheduledTaskList);

    /**
     * find All With Page
     * @param taskType
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupId
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param pageable
     * @return
     */
    /**
     * find Rule Group Scheduled Work flow TaskRelation
     * @param projectId
     * @return
     */
    @Query(value = "select qw.id as rule_group_id ,qw.rule_group_name as rule_group_name from qualitis_rule_group qw " +
            " where qw.project_id =?1 and  qw.id NOT in(select " +
            " b.rule_group_id " +
            " from qualitis_scheduled_task q " +
            " left join qualitis_scheduled_front_back_rule b on q.id=b.scheduled_task_id where " +
            " q.project_id =?1 and b.rule_group_id is NOT NULL " +
            " GROUP by b.rule_group_id " +
            " UNION ALL " +
            " select " +
            " w.rule_group_id " +
            " from qualitis_scheduled_task q " +
            " left join qualitis_scheduled_workflow_task_relation w on q.id = w.scheduled_task_id " +
            " where " +
            " q.project_id =?1 and w.rule_group_id is NOT NULL " +
            " GROUP by w.rule_group_id )"
            , nativeQuery = true)
    List<Map<String, Object>> findRuleGroupMap(Long projectId);

    /**
     * count By Rule Groups
     * @param ruleGroupIds
     * @return
     */
    @Query(value = "select count(0) from qualitis_scheduled_workflow_task_relation where rule_group_id in (?1)", nativeQuery = true)
    long countByRuleGroups(List<Long> ruleGroupIds);


    /**
     * find By Scheduled Workflow
     * @param scheduledWorkflowIdList
     * @param ruleGroupIdList
     * @return
     */
    @Query(value = "select * from qualitis_scheduled_workflow_task_relation where scheduled_workflow_id in (?1) and rule_group_id in (?2)", nativeQuery = true)
    List<ScheduledWorkflowTaskRelation> findByScheduledWorkFlowIdAndRuleGroupId(List<Long> scheduledWorkflowIdList, List<Long> ruleGroupIdList);


}
