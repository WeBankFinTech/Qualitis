package com.webank.wedatasphere.qualitis.project.service.impl;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectEventDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/5 11:10
 */
@Service
public class ProjectEventServiceImpl implements ProjectEventService {
    @Autowired
    private ProjectEventDao projectEventDao;
    @Autowired
    private ProjectDao projectDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectEventServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void record(Long projectId, String userName, String operation, String content, Integer typeId) {
        Project projectInDb = projectDao.findById(projectId);

        ProjectEvent projectEvent = new ProjectEvent(projectInDb, userName, operation + " " + content
            , ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()), typeId);

        try {
            projectEventDao.save(projectEvent);
        } catch (Exception e) {
            LOGGER.error("Failed to record project event.");
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void recordModifyProject(Project projectInDb, String userName, String field, String beforeModify, String afterModify, Integer typeId) {

        ProjectEvent projectEvent = new ProjectEvent(projectInDb, userName, field, beforeModify, afterModify, ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()), typeId);

        try {
            projectEventDao.save(projectEvent);
        } catch (Exception e) {
            LOGGER.error("Failed to record project event.");
            LOGGER.error(e.getMessage(), e);
        }
    }
}
