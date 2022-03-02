package com.webank.wedatasphere.qualitis.project.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import java.util.List;

/**
 * @author allenzhou
 */
public interface ProjectEventDao {

    /**
     * Save.
     * @param projectEvent
     * @return
     */
    ProjectEvent save(ProjectEvent projectEvent);

    /**
     * Paging find by project.
     * @param page
     * @param size
     * @param project
     * @param typeId
     * @return
     */
    List<ProjectEvent> find(int page, int size, Project project, Integer typeId);

    /**
     * Count by project.
     * @param project
     * @param typeId
     * @return
     */
    long count(Project project, Integer typeId);
}
