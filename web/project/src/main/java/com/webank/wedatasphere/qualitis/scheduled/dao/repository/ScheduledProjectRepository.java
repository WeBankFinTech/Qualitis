package com.webank.wedatasphere.qualitis.scheduled.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:25
 * @description
 */
public interface ScheduledProjectRepository extends JpaRepository<ScheduledProject, Long>, JpaSpecificationExecutor<ScheduledProject> {

    /**
     * Find by name
     * @param name
     * @return
     */
    ScheduledProject findByName(String name);

    /**
     * find project list and workflow list
     * @param projects
     * @param nameList
     * @param sort
     * @return
     */
    List<ScheduledProject> findByProjectInAndNameIn(List<Project> projects, List<String> nameList, Sort sort);
}
