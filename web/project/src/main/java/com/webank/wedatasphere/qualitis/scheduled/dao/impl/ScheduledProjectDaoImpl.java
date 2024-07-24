package com.webank.wedatasphere.qualitis.scheduled.dao.impl;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledProjectDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.repository.ScheduledProjectRepository;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledTaskRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:22
 * @description
 */
@Repository
public class ScheduledProjectDaoImpl implements ScheduledProjectDao {

    @Autowired
    private ScheduledProjectRepository scheduledProjectRepository;

    @Override
    public ScheduledProject save(ScheduledProject project) {
        return scheduledProjectRepository.save(project);
    }

    @Override
    public void delete(ScheduledProject scheduledProject) {
        scheduledProjectRepository.delete(scheduledProject);
    }

    @Override
    public Optional<ScheduledProject> findById(Long id) {
        Specification<ScheduledProject> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get("id"), id));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
        return scheduledProjectRepository.findOne(specification);
    }

    @Override
    public ScheduledProject findByName(String name) {
        return scheduledProjectRepository.findByName(name);
    }

    @Override
    public List<ScheduledProject> findProjectList(ScheduledTaskRequest request) {
        Specification<ScheduledProject> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(request.getProjectId())) {
                predicateList.add(criteriaBuilder.equal(root.get("project"), request.getProjectId()));
            }
            if (StringUtils.isNotEmpty(request.getScheduleSystem())) {
                predicateList.add(criteriaBuilder.equal(root.get("dispatchingSystemType"), request.getScheduleSystem()));
            }
            if (StringUtils.isNotEmpty(request.getCluster())) {
                predicateList.add(criteriaBuilder.equal(root.get("clusterName"), request.getCluster()));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
        return scheduledProjectRepository.findAll(specification);
    }

    @Override
    public List<ScheduledProject> findByProjectAndNameList(List<Project> projects, List<String> nameList) {
        Sort sort = Sort.by(Sort.Direction.DESC, "project");
        return scheduledProjectRepository.findByProjectInAndNameIn(projects, nameList, sort);
    }

}
