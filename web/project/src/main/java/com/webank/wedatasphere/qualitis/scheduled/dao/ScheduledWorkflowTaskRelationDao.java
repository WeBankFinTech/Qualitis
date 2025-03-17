package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 10:08
 * @description
 */
public interface ScheduledWorkflowTaskRelationDao {

    /**
     * save All
     * @param workflowTaskRelationList
     * @return
     */
    void saveAll(List<ScheduledWorkflowTaskRelation> workflowTaskRelationList);

    /**
     * save
     * @param workflowTaskRelation
     * @return
     */
    void save(ScheduledWorkflowTaskRelation workflowTaskRelation);

    /**
     * delete By Scheduled Workflow
     * @param scheduledWorkflows
     * @return
     */
    void deleteByScheduledWorkflowList(List<ScheduledWorkflow> scheduledWorkflows);

    /**
     * delete By Scheduled Task List
     * @param scheduledTaskList
     * @return
     */
    void deleteByScheduledTaskList(List<ScheduledTask> scheduledTaskList);

    /**
     * delete batch
     * @param taskRelationList
     */
    void deleteBatch(List<ScheduledWorkflowTaskRelation> taskRelationList);

    /**
     * is Exists
     * @param ruleGroupIdList
     * @return
     */
    Boolean isExists(List<Long> ruleGroupIdList);

    /**
     * find By Workflow
     * @param scheduledWorkflow
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByWorkflow(ScheduledWorkflow scheduledWorkflow);

    /**
     * find By Workflow
     * @param scheduledWorkflowList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByWorkflowList(List<ScheduledWorkflow> scheduledWorkflowList);

    /**
     * find By scheduledTaskList
     * @param scheduledTaskList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByScheduledTaskList(List<ScheduledTask> scheduledTaskList);
    /**
     * find Rule Group Scheduled Work flow TaskRelation
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findRuleGroupMap(Long projectId);

    /**
     * findWorkflowTaskRelation
     * @param ruleGroupIdList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByRuleGroupIds(List<Long> ruleGroupIdList);

    /**
     * find By Scheduled Work Flow Id And Rule Group Id
     * @param scheduledWorkflowIdList
     * @param ruleGroupIdList
     * @return
     */
    List<ScheduledWorkflowTaskRelation> findByScheduledWorkFlowIdAndRuleGroupId(List<Long> scheduledWorkflowIdList,List<Long> ruleGroupIdList);

}
