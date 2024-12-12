package com.webank.wedatasphere.qualitis.project.dao.impl;

import com.webank.wedatasphere.qualitis.project.dao.ProjectEventDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectEventRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/20 17:12
 */
@Repository
public class ProjectEventDaoImpl implements ProjectEventDao {
    @Autowired
    private ProjectEventRepository projectEventRepository;

    @Override
    public ProjectEvent save(ProjectEvent projectEvent) {
        return projectEventRepository.save(projectEvent);
    }

    @Override
    public void saveBatch(List<ProjectEvent> projectEvents) {
        projectEventRepository.saveAll(projectEvents);
    }

    @Override
    public Page<ProjectEvent> findWithPage(int page, int size, Long projectId) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectEventRepository.findByProjectId(projectId, pageable);
    }


}
