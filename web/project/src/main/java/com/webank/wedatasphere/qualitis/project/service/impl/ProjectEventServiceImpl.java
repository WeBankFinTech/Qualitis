package com.webank.wedatasphere.qualitis.project.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectEventDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/5 11:10
 */
@Service
public class ProjectEventServiceImpl implements ProjectEventService {

    @Autowired
    private ProjectEventDao projectEventDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectEventServiceImpl.class);

    @Override
    public void record(Project project, String operateUser, String operationContent, OperateTypeEnum operateTypeEnum) {
        ProjectEvent projectEvent = new ProjectEvent(project, operateUser, operationContent
                , QualitisConstants.PRINT_TIME_FORMAT.format(new Date()), operateTypeEnum.getCode());

        try {
            projectEventDao.save(projectEvent);
        } catch (Exception e) {
            LOGGER.error("Failed to record project event.");
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void recordBatch(List<Project> projects, String operateUser, String operationContent, OperateTypeEnum operateTypeEnum) {
        List<ProjectEvent> projectEventList = Lists.newArrayListWithExpectedSize(projects.size());
        String time = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        for (Project project: projects) {
            ProjectEvent projectEvent = new ProjectEvent(project, operateUser, operationContent
                    , time, operateTypeEnum.getCode());
            projectEventList.add(projectEvent);
        }

        try {
            projectEventDao.saveBatch(projectEventList);
        } catch (Exception e) {
            LOGGER.error("Failed to record batch project event.");
            LOGGER.error(e.getMessage(), e);
        }
    }
}
