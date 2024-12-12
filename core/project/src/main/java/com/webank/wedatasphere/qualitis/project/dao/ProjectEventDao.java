package com.webank.wedatasphere.qualitis.project.dao;

import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import org.springframework.data.domain.Page;

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
     * Save Batch.
     * @param projectEvents
     * @return
     */
    void saveBatch(List<ProjectEvent> projectEvents);

    /**
     * find With Page
     * @param page
     * @param size
     * @param projectId
     * @return
     */
    Page<ProjectEvent> findWithPage(int page, int size, Long projectId);
}
