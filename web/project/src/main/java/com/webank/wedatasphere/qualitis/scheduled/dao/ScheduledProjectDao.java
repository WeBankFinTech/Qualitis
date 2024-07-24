package com.webank.wedatasphere.qualitis.scheduled.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledTaskRequest;

import java.util.List;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:21
 * @description
 */
public interface ScheduledProjectDao {

    /**
     * save
     * @param project
     * @return
     */
    ScheduledProject save(ScheduledProject project);

    /**
     * delete
     * @param scheduledProject
     * @return
     */
    void delete(ScheduledProject scheduledProject);

    /**
     * find ById
     * @param id
     * @return
     */
    Optional<ScheduledProject> findById(Long id);

    /**
     * Find by name
     * @param name
     * @return
     */
    ScheduledProject findByName(String name);

    /**
     * get Project List
     * @param request
     * @return
     */
    List<ScheduledProject> findProjectList(ScheduledTaskRequest request);

    /**
     * find by Project and name
     * @param projects
     * @param nameList
     * @return
     */
    List<ScheduledProject> findByProjectAndNameList(List<Project> projects, List<String> nameList);

}
