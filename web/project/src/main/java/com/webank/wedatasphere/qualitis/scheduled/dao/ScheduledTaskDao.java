package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledTaskDao {

    /**
     * save ScheduledTask
     * @param scheduledTask
     * @return
     */
    ScheduledTask saveScheduledTask(ScheduledTask scheduledTask);

    /**
     * save All
     * @param scheduledTaskList
     * @return
     */
    void saveAll(List<ScheduledTask> scheduledTaskList);

    /**
     * delete ScheduledTask
     * @param scheduledTask
     * @return
     */
    void deleteScheduledTask(ScheduledTask scheduledTask);

    /**
     * Find ScheduledTask by id
     * @param scheduledTaskId
     * @return
     */
    ScheduledTask findById(Long scheduledTaskId);

    /**
     * filter Page
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupList
     * @param dbName
     * @param tableName
     * @param page
     * @param size
     * @param taskType
     * @param projectId
     * @param cluster
     * @return
     */
    List<ScheduledFrontBackRule> filterPage(String systemType, String projectName, String workFlow,
                                            String taskName, Set<Long> ruleGroupList, String dbName, String tableName, int page, int size, Integer taskType, Long projectId, String cluster);

    /**
     * count All ScheduledTask
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupList
     * @param dbName
     * @param tableName
     * @param taskType
     * @param projectId
     * @param cluster
     * @return
     */
    Long  countAllScheduledTask(String systemType, String projectName, String workFlow,
                                String taskName, Set<Long> ruleGroupList, String dbName, String tableName,Integer taskType,Long projectId,String cluster);

    /**
     * query publish task with page
     * @param projectId
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupIds
     * @param clusterName
     * @param taskType
     * @param page
     * @param size
     * @return
     */
    Page<ScheduledTask> filterWithPage(Long projectId, String systemType, String projectName, String workFlow,
                                          String taskName, List<Long> ruleGroupIds, String clusterName, Integer taskType, int page, int size);
    /**
     * Batch delete ScheduledTask
     * @param scheduledTaskList
     * @return
     */
    void deleteScheduledTaskBatch(List<ScheduledTask> scheduledTaskList);

    /**
     * find cluster list by scheduled system
     * @param taskType
     * @param scheduleSystem
     * @param project
     * @return
     */
    List<String> findOptionClusterList(Integer taskType, String scheduleSystem, Project project);

    /**
     * find project list by cluster
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param project
     * @return
     */
    List<String> findOptionProjectList(Integer taskType, String scheduleSystem, String clusterName, Project project);

    /**
     * find workflow list by project
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param projectName
     * @return
     */
    List<String> findOptionWorkflowList(Integer taskType, String scheduleSystem, String clusterName, String projectName, Project project);

    /**
     * find task list by workflow
     * @param taskType
     * @param scheduleSystem
     * @param clusterName
     * @param projectName
     * @param workflowName
     * @return
     */
    List<String> findOptionTaskList(Integer taskType, String scheduleSystem, String clusterName, String projectName, String workflowName, Project project);

    /**
     * count By Project Name
     * @param ids
     * @return
     */
    List<ScheduledTask> findByIds(List<Long> ids);

    /**
     * find Only Object
     *
     * @param clusterName
     * @param dispatchingSystemType
     * @param projectName
     * @param workFlowName
     * @param taskName
     * @param taskType
     * @return
     */
    ScheduledTask findOnlyObject(String clusterName, String dispatchingSystemType, String projectName, String workFlowName, String taskName, Integer taskType);

    /**
     * get Relation RuleGroup
     *
     * @param clusterName
     * @param dispatchingSystemType
     * @param projectName
     * @param workFlowName
     * @param taskName
     * @return
     */
    List<Map<String,Object>> getRelationRuleGroup(String clusterName, String dispatchingSystemType, String projectName, String workFlowName, String taskName);


    /**
     * find Rule Group ForTable
     * @param projectId
     * @param taskType
     * @return
     */
    List<Map<String,Object>> findRuleGroupForTable(Long projectId,Integer taskType);

    /**
     * find Db And Tables
     * @param projectId
     * @return
     */
    List<Map<String, String>> findDbAndTables(Long projectId);

    /**
     * getDbAndTableQuery
     * @param projectId
     * @param taskType
     * @return
     */
    List<Map<String, Object>> getDbAndTableQuery(Long projectId,Integer taskType);

    /**
     * findTableListDistinct
     * @param projectId
     * @param taskType
     * @return
     */
    List<Map<String, String>> findTableListDistinct(Long projectId, Integer taskType);

    /**
     * find Match ScheduledTask
     * @param projectName
     * @param taskType
     * @return
     */
    List<ScheduledTask> findByProjectNameAndTaskType(String projectName, Integer taskType);

    /**
     * find schedule task list by rule group with FrontBack
     * @param ruleGroupIdList
     * @return
     */
    List<Map<String, Object>> findByRuleGroupsInFrontAndBack(Set<Long> ruleGroupIdList);

    /**
     * find schedule task list by rule group with WorkflowTaskRelation
     * @param ruleGroupIdList
     * @return
     */
    List<Map<String, Object>> findByRuleGroupsInWorkflowTaskRelation(Set<Long> ruleGroupIdList);

    /**
     * find by conditions in project
     * @param projectId
     * @param taskType
     * @param dispatchSystem
     * @param cluster
     * @param wtssProjectName
     * @param workflowName
     * @param taskName
     * @param releaseStatus
     * @return
     */
    List<ScheduledTask> findByCondition(Long projectId, Integer taskType, String dispatchSystem, String cluster, String wtssProjectName, String workflowName, String taskName, Integer releaseStatus);

    /**
     * find by project id
     * @param projects
     * @param taskType
     * @return
     */
    List<ScheduledTask> findByProjects(List<Project> projects, Integer taskType);

    /**
     * find By Work flow List
     * @param projectId
     * @param taskType
     * @param dispatchSystem
     * @param cluster
     * @param wtssProjectName
     * @param workflowList
     * @return
     */
    List<ScheduledTask> findByWorkflowList(Long projectId, Integer taskType, String dispatchSystem, String cluster, String wtssProjectName, List<String> workflowList);

}
