package com.webank.wedatasphere.qualitis.project.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface ProjectEventRepository extends JpaRepository<ProjectEvent, Long>, JpaSpecificationExecutor<ProjectEvent> {

    /**
     * Find project event by project.
     * @param project
     * @param typeId
     * @param pageable
     * @return
     */
    @Query(value = "SELECT pe FROM ProjectEvent pe WHERE pe.project = ?1 AND pe.eventType = ?2")
    Page<ProjectEvent> findByProject(Project project, Integer typeId, Pageable pageable);

    /**
     * Count with project.
     * @param project
     * @param typeId
     * @return
     */
    @Query(value = "SELECT COUNT(id) FROM ProjectEvent pe WHERE pe.project = ?1 AND pe.eventType = ?2")
    long countByProject(Project project, Integer typeId);

}
