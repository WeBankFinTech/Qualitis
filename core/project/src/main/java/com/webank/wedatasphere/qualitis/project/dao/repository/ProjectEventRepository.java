package com.webank.wedatasphere.qualitis.project.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author allenzhou
 */
public interface ProjectEventRepository extends JpaRepository<ProjectEvent, Long>, JpaSpecificationExecutor<ProjectEvent> {

    /**
     * find By Project
     * @param projectId
     * @param pageable
     * @return
     */
    Page<ProjectEvent> findByProjectId(Long projectId, Pageable pageable);

}
