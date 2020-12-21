package com.webank.wedatasphere.qualitis.project.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import java.util.List;

/**
 * @author allenzhou
 */
public interface ProjectLabelDao {
    /**
     * Save all project labels
     * @param ProjectLabels
     */
    void saveAll(Iterable<ProjectLabel> ProjectLabels);

    /**
     * Find by project label by project
     * @param project
     * @return
     */
    List<ProjectLabel> findByProject(Project project);

    /**
     * Delete project label
     * @param project
     */
    void deleteByProject(Project project);

    /**
     * Delete one label
     * @param projectLabel
     */
    void delete(ProjectLabel projectLabel);

    /**
     * flush changes
     */
    void flush();
}
