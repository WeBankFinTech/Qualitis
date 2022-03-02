package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.TaskRuleAlarmConfigDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleAlarmConfigRepository;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class TaskRuleAlarmConfigDaoImpl implements TaskRuleAlarmConfigDao {
    @Autowired
    private TaskRuleAlarmConfigRepository taskRuleAlarmConfigRepository;

    @Override
    public TaskRuleAlarmConfig findById(Long taskRuleAlarmConfigId) {
        return taskRuleAlarmConfigRepository.findTaskRuleAlarmConfig(taskRuleAlarmConfigId);
    }
}
