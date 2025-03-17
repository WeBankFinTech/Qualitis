package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long>, JpaSpecificationExecutor<ScheduledTask> {

    /**
     * filter Page
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupList
     * @param dbName
     * @param tableName
     * @param taskType
     * @param projectId
     * @param pageable
     * @param cluster
     * @return
     */
    @Query(value = "select distinct b from ScheduledTask pa " +
            "left join ScheduledFrontBackRule  b on pa=b.scheduledTask  " +
            "left join Rule c on b.ruleGroup=c.ruleGroup  " +
            "left join RuleDataSource  d on c=d.rule  " +
            "where b.id is not null and pa.taskType=?8 and  (?9 is null or pa.project=?9) " +
            "and (?10 is null or pa.clusterName = ?10)  " +
            "and (?1 is null or pa.dispatchingSystemType = ?1)   " +
            "and (?2 is null or pa.projectName = ?2)   " +
            "and (?3 is null or pa.workFlowName = ?3)    " +
            "and (?4 is null or pa.taskName = ?4) " +
            "and (?5 is null or b.ruleGroup IN ?5) " +
            "and (?6 is null or d.dbName = ?6)    " +
            "and (?7 is null or d.tableName = ?7)  ",
            countQuery = "select count(b) from ScheduledTask pa " +
                    " left join ScheduledFrontBackRule  b on pa=b.scheduledTask " +
                    " left join Rule c on b.ruleGroup=c.ruleGroup " +
                    " left join RuleDataSource d on c=d.rule  " +
                    " where b.id is not null and pa.taskType=?8  and (?9 is null or pa.project=?9)  " +
                    "and (?10 is null or pa.clusterName = ?10)  " +
                    "and (?1 is null or pa.dispatchingSystemType = ?1)   " +
                    "and (?2 is null or pa.projectName = ?2)   " +
                    "and (?3 is null or pa.workFlowName = ?3)    " +
                    "and (?4 is null or pa.taskName = ?4) " +
                    "and (?5 is null or b.ruleGroup IN ?5) " +
                    "and (?6 is null or d.dbName = ?6)    " +
                    "and (?7 is null or d.tableName = ?7)  " )
    Page<ScheduledFrontBackRule> filterPage(String systemType, String projectName, String workFlow,
                                            String taskName, Set<Long> ruleGroupList, String dbName, String tableName, Integer taskType, Long projectId, String cluster, Pageable pageable);

    /**
     * count All ScheduledTask
     * @param systemType
     * @param wtssProjectName
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
    @Query(value = "select  count(distinct b.id) from qualitis_scheduled_task a " +
            "left join qualitis_scheduled_front_back_rule b on a.id=b.scheduled_task_id " +
            "left join qualitis_rule c on b.rule_group_id=c.rule_group_id " +
            "left join qualitis_rule_datasource d on c.id=d.rule_id  " +
            "where  a.task_type=?8  AND a.project_id=?9  " +
            "AND if(nullif(?10,'')!='', a.cluster_name= ?10,1=1) " +
            "AND if(nullif(?1,'')!='', a.dispatching_system_type= ?1,1=1)   " +
            "AND if(nullif(?2,'')!='',a.wtss_project_name = ?2,1=1)   " +
            "AND if(nullif(?3,'')!='',a.wtss_work_flow_name = ?3,1=1)   " +
            "AND if(nullif(?4,'')!='',a.wtss_task_name= ?4,1=1)  " +
            "AND (coalesce(?5,null) is null or b.rule_group_id IN (?5) ) " +
            "AND if(nullif(?6,'')!='',d.db_name= ?6,1=1)   " +
            "AND if(nullif(?7,'')!='',d.table_name= ?7,1=1) ", nativeQuery = true)
    Long countAllScheduledTask(String systemType, String wtssProjectName, String workFlow,
                               String taskName, Set<Long> ruleGroupList, String dbName, String tableName, Integer taskType,Long projectId,String cluster);

    /**
     * filterPublishPage
     * @param systemType
     * @param projectName
     * @param workFlow
     * @param taskName
     * @param ruleGroupIds
     * @param clusterName
     * @param projectId
     * @param taskType
     * @param pageable
     * @return
     */
    @Query(value = "select t from ScheduledTask t " +
            "left join ScheduledWorkflowTaskRelation wtr on wtr.scheduledTask = t " +
            "left join RuleDataSource ds on ds.ruleGroup = wtr.ruleGroup " +
            "where (?1 is null or t.dispatchingSystemType = ?1) and (?2 is null or t.projectName=?2)" +
            "and (?3 is null or t.workFlowName=?3) and (?4 is null or t.taskName=?4)" +
            "and (coalesce(?5, null) is null or wtr.ruleGroup in (?5)) and (?6 is null or t.clusterName=?6)" +
            "and (?7 is null or t.project=?7) and (?8 is null or t.taskType=?8)"
            , countQuery= "select count(t) from ScheduledTask t " +
            "left join ScheduledWorkflowTaskRelation wtr on wtr.scheduledTask = t " +
            "left join RuleDataSource ds on ds.ruleGroup = wtr.ruleGroup " +
            "where (?1 is null or t.dispatchingSystemType = ?1) and (?2 is null or t.projectName=?2)" +
            "and (?3 is null or t.workFlowName=?3) and (?4 is null or t.taskName=?4)" +
            "and (coalesce(?5, null) is null or wtr.ruleGroup in (?5)) and (?6 is null or t.clusterName=?6)" +
            "and (?7 is null or t.project=?7) and (?8 is null or t.taskType=?8)")
    Page<ScheduledTask> filterPublishPage(String systemType, String projectName, String workFlow,
                                          String taskName, List<Long> ruleGroupIds, String clusterName,
                                          Long projectId, Integer taskType, Pageable pageable);

    /**
     * find Option Cluster List
     * @param taskType
     * @param dispatchingSystemType
     * @param project
     * @return
     */
    @Query(value = "select distinct clusterName from ScheduledTask where 1 =1 and (?1 is null or taskType=?1) and (?2 is null or dispatchingSystemType=?2) and (?3 is null or project=?3)")
    List<String> findOptionClusterList(Integer taskType, String dispatchingSystemType, Project project);

    /**
     * find Option Project List
     * @param taskType
     * @param dispatchingSystemType
     * @param clusterName
     * @param project
     * @return
     */
    @Query(value = "select distinct projectName from ScheduledTask where 1 =1 and (?1 is null or taskType=?1) and (?2 is null or dispatchingSystemType=?2) " +
            "and (?3 is null or clusterName=?3) and (?4 is null or project=?4)")
    List<String> findOptionProjectList(Integer taskType, String dispatchingSystemType, String clusterName, Project project);

    /**
     * find Option Work flow List
     * @param taskType
     * @param dispatchingSystemType
     * @param clusterName
     * @param projectName
     * @return
     */
    @Query(value = "select distinct workFlowName from ScheduledTask where 1 =1 and (?1 is null or taskType=?1) and (?2 is null or dispatchingSystemType=?2) " +
            "and (?3 is null or clusterName=?3) and (?4 is null or projectName=?4) and (?5 is null or project=?5)")
    List<String> findOptionWorkflowList(Integer taskType, String dispatchingSystemType, String clusterName, String projectName, Project project);

    /**
     * find Option Task List
     * @param taskType
     * @param dispatchingSystemType
     * @param clusterName
     * @param projectName
     * @param workflowName
     * @return
     */
    @Query(value = "select distinct taskName from ScheduledTask where 1 =1 and (?1 is null or taskType=?1) and (?2 is null or dispatchingSystemType=?2) " +
            "and (?3 is null or clusterName=?3) and (?4 is null or projectName=?4) and (?5 is null or workFlowName=?5) and (?6 is null or project=?6)")
    List<String> findOptionTaskList(Integer taskType, String dispatchingSystemType, String clusterName, String projectName, String workflowName, Project project);

    /**
     * find Only Object
     * @param clusterName
     * @param dispatchingSystemType
     * @param wtssProjectName
     * @param workflow
     * @param taskName
     * @param taskType
     * @return
     */
    @Query(value = "select w.* from qualitis_scheduled_task w where " +
            "(?1 is null or w.cluster_name=?1) and (?2 is null or w.dispatching_system_type=?2) and (?3 is null or w.wtss_project_name=?3) " +
            "and (?4 is null or w.wtss_work_flow_name = ?4) and (?5 is null or w.wtss_task_name = ?5) " +
            "and (?6 is null or w.task_type = ?6) "
            , nativeQuery = true)
    ScheduledTask findOnlyObject(String clusterName, String dispatchingSystemType, String wtssProjectName, String workflow, String taskName, Integer taskType);

    /**
     * find Only Object
     *
     * @param clusterName
     * @param dispatchingSystemType
     * @param projectName
     * @param workFlowName
     * @param taskName
     * @return
     */
    @Query(value = "select  b.scheduled_task_id as scheduled_task_id,b.rule_group_id as rule_group_id,b.trigger_type as trigger_type,c.rule_group_name as rule_group_name,w.release_user as release_user  from qualitis_scheduled_task w " +
            "left join qualitis_scheduled_front_back_rule b on w.id=b.scheduled_task_id  " +
            "left join qualitis_rule_group c on c.id=b.rule_group_id " +
            "where w.cluster_name=?1  " +
            "and w.dispatching_system_type=?2  " +
            "and w.wtss_project_name=?3  " +
            "and w.wtss_work_flow_name=?4   " +
            "and w.wtss_task_name=?5 and w.task_type=1 ", nativeQuery = true)
    List<Map<String,Object>> getRelationRuleGroup(String clusterName, String dispatchingSystemType, String projectName, String workFlowName, String taskName);

    /**
     * find RuleGroup ForTable
     *
     * @param projectId
     * @param taskType
     * @return
     */
    @Query(value = "select DISTINCT c.id as rule_group_id,c.rule_group_name as rule_group_name from qualitis_scheduled_task w " +
            "left join qualitis_scheduled_front_back_rule b on w.id=b.scheduled_task_id " +
            "left join qualitis_rule_group c on c.id=b.rule_group_id " +
            "where  w.task_type =?2 and w.project_id =?1", nativeQuery = true)
    List<Map<String, Object>> findRuleGroupForTable(Long projectId,Integer taskType);

    /**
     * findDbAndTables
     * @param projectId
     * @return
     */
    @Query(value = "select DISTINCT d.db_name as db_name,d.table_name as table_name from qualitis_scheduled_task as pa " +
            "left join qualitis_scheduled_workflow_task_relation as b on pa.id = b.scheduled_task_id " +
            "left join qualitis_rule as c on b.rule_group_id = c.rule_group_id " +
            "left join qualitis_rule_datasource as d on c.id = d.rule_id " +
            "where pa.project_id =?1 and d.db_name is not null" , nativeQuery = true)
    List<Map<String, String>> findDbAndTables(Long projectId);

    /**
     * getDbAndTableQuery
     * @param projectId
     * @param taskType
     * @return
     */
    @Query(value = "select DISTINCT d.db_name as db_name,d.table_name as table_name from qualitis_scheduled_task as pa " +
            "left join qualitis_scheduled_front_back_rule as b on pa.id = b.scheduled_task_id " +
            "left join qualitis_rule as c on b.rule_group_id = c.rule_group_id " +
            "left join qualitis_rule_datasource as d on c.id = d.rule_id " +
            "where pa.task_type =?2  AND pa.project_id =?1" , nativeQuery = true)
    List<Map<String, Object>> getDbAndTableQuery(Long projectId,Integer taskType);

    /**
     * findTableListDistinct
     * @param projectId
     * @param taskType
     * @return
     */
    @Query(value = "select DISTINCT cluster_name as cluster_name,wtss_project_name as wtss_project_name,wtss_task_name as wtss_task_name,wtss_work_flow_name as wtss_work_flow_name from qualitis_scheduled_task as pa " +
            "where pa.task_type =?2  AND pa.project_id =?1" , nativeQuery = true)
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
    @Query("select new map(fbr.ruleGroup.id as rule_group_id, st as schedule_task) " +
            "from ScheduledTask st, ScheduledFrontBackRule fbr " +
            "where st = fbr.scheduledTask and fbr.ruleGroup.id in ?1")
    List<Map<String, Object>> findByRuleGroupsInFrontAndBack(Set<Long> ruleGroupIdList);

    /**
     * find schedule task list by rule group with WorkflowTaskRelation
     * @param ruleGroupIdList
     * @return
     */
    @Query("select new map(wtr.ruleGroup.id as rule_group_id, st as schedule_task) " +
            "from ScheduledTask st, ScheduledWorkflowTaskRelation wtr " +
            "where st = wtr.scheduledTask and wtr.ruleGroup.id in ?1")
    List<Map<String, Object>> findByRuleGroupsInWorkflowTaskRelation(Set<Long> ruleGroupIdList);

    /**
     * find By Projects
     * @param projects
     * @param taskType
     * @param sort
     * @return
     */
    @Query("select st from ScheduledTask st where project in (?1) and taskType = ?2")
    List<ScheduledTask> findByProjects(List<Project> projects, Integer taskType, Sort sort);

}
