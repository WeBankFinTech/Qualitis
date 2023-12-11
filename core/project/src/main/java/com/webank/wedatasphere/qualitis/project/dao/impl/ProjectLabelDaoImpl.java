package com.webank.wedatasphere.qualitis.project.dao.impl;

import com.webank.wedatasphere.qualitis.project.dao.ProjectLabelDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectLabelRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class ProjectLabelDaoImpl implements ProjectLabelDao {
    @Autowired
    private ProjectLabelRepository projectLabelRepository;

    @Override
    public void saveAll(Iterable<ProjectLabel> projectLabels) {
        projectLabelRepository.saveAll(projectLabels);
    }

    @Override
    public List<ProjectLabel> findByProject(Project project) {
        return projectLabelRepository.findByProject(project);
    }

    @Override
    public void deleteByProject(Project project) {
        projectLabelRepository.deleteByProject(project);
    }

    @Override
    public void delete(ProjectLabel projectLabel) {
        projectLabelRepository.delete(projectLabel);
    }

    @Override
    public void flush() {
        projectLabelRepository.flush();
    }
}
