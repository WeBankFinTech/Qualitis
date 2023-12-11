package com.webank.wedatasphere.qualitis.project.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author allenzhou
 */
public interface ProjectLabelRepository  extends JpaRepository<ProjectLabel, Long>, JpaSpecificationExecutor<ProjectLabel> {
    /**
     * Find project label by project
     * @param project
     * @return
     */
    List<ProjectLabel> findByProject(Project project);

    /**
     * Delete project label by project
     * @param project
     */
    void deleteByProject(Project project);
}
