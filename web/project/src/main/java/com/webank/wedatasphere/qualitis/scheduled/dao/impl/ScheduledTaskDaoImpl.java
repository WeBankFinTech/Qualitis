package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledTaskDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledTaskRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class ScheduledTaskDaoImpl implements ScheduledTaskDao {

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Override
    public ScheduledTask saveScheduledTask(ScheduledTask scheduledTask) {
        return scheduledTaskRepository.save(scheduledTask);
    }

    @Override
    public void saveAll(List<ScheduledTask> scheduledTaskList) {
        scheduledTaskRepository.saveAll(scheduledTaskList);
    }

    @Override
    public void deleteScheduledTask(ScheduledTask scheduledTask) {
        scheduledTaskRepository.delete(scheduledTask);
    }

    @Override
    public ScheduledTask findById(Long scheduledTaskId) {
        return scheduledTaskRepository.findById(scheduledTaskId).orElse(null);
    }

    @Override
    public List<ScheduledFrontBackRule> filterPage(String systemType, String projectName, String workFlow, String taskName, Set<Long> ruleGroupList, String dbName, String tableName, int page, int size, Integer taskType, Long projectId, String cluster) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return scheduledTaskRepository.filterPage(systemType, projectName, workFlow, taskName, ruleGroupList, dbName, tableName, taskType,projectId,cluster, pageable).getContent();
    }

    @Override
    public Long countAllScheduledTask(String systemType, String wtssProjectName, String workFlow, String taskName, Set<Long> ruleGroupList, String dbName, String tableName, Integer taskType,Long projectId,String cluster) {
        return scheduledTaskRepository.countAllScheduledTask(systemType, wtssProjectName, workFlow, taskName, ruleGroupList, dbName, tableName, taskType,projectId,cluster);
    }

    @Override
    public Page<ScheduledTask> filterWithPage(Long projectId, String systemType, String projectName, String workFlow, String taskName
            , List<Long> ruleGroupIds, String clusterName, Integer taskType, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return scheduledTaskRepository.filterPublishPage(systemType, projectName, workFlow, taskName, ruleGroupIds, clusterName, projectId, taskType, pageable);
    }

    @Override
    public void deleteScheduledTaskBatch(List<ScheduledTask> scheduledTaskList) {
        scheduledTaskRepository.deleteAll(scheduledTaskList);
    }

    @Override
    public List<String> findOptionClusterList(Integer taskType, String scheduleSystem, Project project) {
        return scheduledTaskRepository.findOptionClusterList(taskType, scheduleSystem, project);
    }

    @Override
    public List<String> findOptionProjectList(Integer taskType, String scheduleSystem, String clusterName, Project project) {
        return scheduledTaskRepository.findOptionProjectList(taskType, scheduleSystem, clusterName, project);
    }

    @Override
    public List<String> findOptionWorkflowList(Integer taskType, String scheduleSystem, String clusterName, String projectName, Project project) {
        return scheduledTaskRepository.findOptionWorkflowList(taskType, scheduleSystem, clusterName, projectName, project);
    }

    @Override
    public List<String> findOptionTaskList(Integer taskType, String scheduleSystem, String clusterName, String projectName, String workflowName, Project project) {
        return scheduledTaskRepository.findOptionTaskList(taskType, scheduleSystem, clusterName, projectName, workflowName, project);
    }

    @Override
    public List<ScheduledTask> findByIds(List<Long> ids) {
        return scheduledTaskRepository.findAllById(ids);
    }

    @Override
    public ScheduledTask findOnlyObject(String clusterName, String dispatchingSystemType, String projectName, String workFlowName, String taskName, Integer taskType) {
        return scheduledTaskRepository.findOnlyObject(clusterName, dispatchingSystemType, projectName, workFlowName, taskName, taskType);
    }

    @Override
    public List<Map<String, Object>> getRelationRuleGroup(String clusterName, String dispatchingSystemType, String projectName, String workFlowName, String taskName) {
        return scheduledTaskRepository.getRelationRuleGroup(clusterName,dispatchingSystemType,projectName,workFlowName,taskName);
    }

    @Override
    public List<Map<String, Object>> findRuleGroupForTable(Long projectId, Integer taskType) {
        return scheduledTaskRepository.findRuleGroupForTable(projectId, taskType);
    }

    @Override
    public List<Map<String, String>> findDbAndTables(Long projectId) {
        return scheduledTaskRepository.findDbAndTables(projectId);
    }

    @Override
    public List<Map<String, Object>> getDbAndTableQuery(Long projectId, Integer taskType) {
        return scheduledTaskRepository.getDbAndTableQuery(projectId, taskType);
    }

    @Override
    public List<Map<String, String>> findTableListDistinct(Long projectId, Integer taskType) {
        return scheduledTaskRepository.findTableListDistinct(projectId, taskType);
    }

    @Override
    public List<ScheduledTask> findByProjectNameAndTaskType(String projectName, Integer taskType) {
        return scheduledTaskRepository.findByProjectNameAndTaskType(projectName, taskType);
    }

    @Override
    public List<Map<String, Object>> findByRuleGroupsInFrontAndBack(Set<Long> ruleGroupIdList) {
        return scheduledTaskRepository.findByRuleGroupsInFrontAndBack(ruleGroupIdList);
    }

    @Override
    public List<Map<String, Object>> findByRuleGroupsInWorkflowTaskRelation(Set<Long> ruleGroupIdList) {
        return scheduledTaskRepository.findByRuleGroupsInWorkflowTaskRelation(ruleGroupIdList);
    }

    @Override
    public List<ScheduledTask> findByCondition(Long projectId, Integer taskType, String dispatchSystem, String cluster, String wtssProjectName
            , String workflowName, String taskName, Integer releaseStatus) {
        Specification<ScheduledTask> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(projectId)) {
                predicateList.add(criteriaBuilder.equal(root.get("project"), projectId));
            }
            if (StringUtils.isNotEmpty(dispatchSystem)) {
                predicateList.add(criteriaBuilder.equal(root.get("dispatchingSystemType"), dispatchSystem));
            }
            if (StringUtils.isNotEmpty(cluster)) {
                predicateList.add(criteriaBuilder.equal(root.get("clusterName"), cluster));
            }
            if (Objects.nonNull(taskType)) {
                predicateList.add(criteriaBuilder.equal(root.get("taskType"), taskType));
            }
            if (StringUtils.isNotEmpty(wtssProjectName)) {
                predicateList.add(criteriaBuilder.equal(root.get("projectName"), wtssProjectName));
            }
            if (StringUtils.isNotEmpty(workflowName)) {
                predicateList.add(criteriaBuilder.equal(root.get("workFlowName"), workflowName));
            }
            if (StringUtils.isNotEmpty(taskName)) {
                predicateList.add(criteriaBuilder.equal(root.get("taskName"), taskName));
            }
            if (Objects.nonNull(releaseStatus)) {
                predicateList.add(criteriaBuilder.equal(root.get("releaseStatus"), releaseStatus));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
        return scheduledTaskRepository.findAll(specification);
    }

    @Override
    public List<ScheduledTask> findByProjects(List<Project> projects, Integer taskType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "project");
        return scheduledTaskRepository.findByProjects(projects, taskType, sort);
    }

    @Override
    public List<ScheduledTask> findByWorkflowList(Long projectId, Integer taskType, String dispatchSystem, String cluster, String wtssProjectName, List<String> workflowList) {
        Specification<ScheduledTask> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(projectId)) {
                predicateList.add(criteriaBuilder.equal(root.get("project"), projectId));
            }
            if (StringUtils.isNotEmpty(dispatchSystem)) {
                predicateList.add(criteriaBuilder.equal(root.get("dispatchingSystemType"), dispatchSystem));
            }
            if (StringUtils.isNotEmpty(cluster)) {
                predicateList.add(criteriaBuilder.equal(root.get("clusterName"), cluster));
            }
            if (Objects.nonNull(taskType)) {
                predicateList.add(criteriaBuilder.equal(root.get("taskType"), taskType));
            }
            if (StringUtils.isNotEmpty(wtssProjectName)) {
                predicateList.add(criteriaBuilder.equal(root.get("projectName"), wtssProjectName));
            }
            if (CollectionUtils.isNotEmpty(workflowList)) {
                predicateList.add(root.get("workFlowName").in(workflowList));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
        return scheduledTaskRepository.findAll(specification);
    }

}
