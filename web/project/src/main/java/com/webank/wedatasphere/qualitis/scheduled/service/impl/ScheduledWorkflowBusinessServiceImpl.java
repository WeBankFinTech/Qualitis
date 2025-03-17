package com.webank.wedatasphere.qualitis.scheduled.service.impl;

//import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
//import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
//import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowBusinessDao;
//import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
//import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledWorkflowBusinessRequest;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledWorkflowBusinessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 10:02
 * @description
 */
@Service
public class ScheduledWorkflowBusinessServiceImpl implements ScheduledWorkflowBusinessService {

//    @Autowired
//    private ScheduledWorkflowBusinessDao scheduledWorkflowBusinessDao;
//
//    @Override
//    public void add(ScheduledWorkflowBusinessRequest request) throws UnExpectedRequestException {
//        request.checkRequest();
//        ScheduledWorkflowBusiness workflowBusinessInDb = scheduledWorkflowBusinessDao.getByProjectIdAndName(request.getProjectId(), request.getName());
//        if (workflowBusinessInDb != null) {
//            throw new UnExpectedRequestException("Name has existed.");
//        }
//        ScheduledWorkflowBusiness scheduledWorkflowBusiness = new ScheduledWorkflowBusiness();
//        BeanUtils.copyProperties(request, scheduledWorkflowBusiness);
//        scheduledWorkflowBusinessDao.save(scheduledWorkflowBusiness);
//    }
//
//    @Override
//    public void modify(ScheduledWorkflowBusinessRequest request) throws UnExpectedRequestException {
//        Optional<ScheduledWorkflowBusiness> workflowBusinessInDbOptional = scheduledWorkflowBusinessDao.get(request.getId());
//        if (!workflowBusinessInDbOptional.isPresent()) {
//            throw new UnExpectedRequestException("data doesn't exists!");
//        }
//        if (!workflowBusinessInDbOptional.get().getName().equals(request.getName())) {
//            ScheduledWorkflowBusiness workflowBusinessInDb = scheduledWorkflowBusinessDao.getByProjectIdAndName(request.getProjectId(), request.getName());
//            if (workflowBusinessInDb != null) {
//                throw new UnExpectedRequestException("Name has existed.");
//            }
//        }
//        ScheduledWorkflowBusiness scheduledWorkflowBusiness = new ScheduledWorkflowBusiness();
//        BeanUtils.copyProperties(request, scheduledWorkflowBusiness);
//        scheduledWorkflowBusinessDao.save(scheduledWorkflowBusiness);
//    }
//
//    @Override
//    public void delete(Long id) {
//        scheduledWorkflowBusinessDao.delete(id);
//    }
//
//    @Override
//    public List<ScheduledWorkflowBusiness> list(Long projectId) throws UnExpectedRequestException {
//        CommonChecker.checkObject(projectId, "project_id");
//        return scheduledWorkflowBusinessDao.getByProjectId(projectId);
//    }
}
