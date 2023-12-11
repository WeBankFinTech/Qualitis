package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.TaskResultStatusDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskResultStatusRepository;
import com.webank.wedatasphere.qualitis.entity.TaskResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-14 10:01
 * @description
 */
@Repository
public class TaskResultStatusDaoImpl implements TaskResultStatusDao {

    @Autowired
    private TaskResultStatusRepository taskResultStatusRepository;

    @Override
    public List<TaskResultStatus> findByStatus(String applicationId, Long ruleId, Integer status) {
        return taskResultStatusRepository.findByApplicationIdAndRuleIdAndStatus(applicationId, ruleId, status);
    }

    @Override
    public void saveBatch(List<TaskResultStatus> taskResultStatusList) {
        taskResultStatusRepository.saveAll(taskResultStatusList);
    }
}
