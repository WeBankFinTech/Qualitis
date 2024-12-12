package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TaskNewVauleDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TaskNewValueRepository;
import com.webank.wedatasphere.qualitis.rule.entity.TaskNewValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class TaskNewValueDaoImpl implements TaskNewVauleDao {

    @Autowired
    private TaskNewValueRepository taskNewValueRepository;


    @Override
    public TaskNewValue saveTaskNewValue(TaskNewValue taskNewValue) {
        return taskNewValueRepository.save(taskNewValue);
    }

    @Override
    public void delete(TaskNewValue taskNewValue) {
        taskNewValueRepository.delete(taskNewValue);
    }

    @Override
    public TaskNewValue findById(Long id) {
        return taskNewValueRepository.findById(id).orElse(null);
    }

    @Override
    public List<TaskNewValue> findAllTaskNewValue(Long ruleId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskNewValueRepository.findAllData(ruleId, pageable).getContent();
    }

    @Override
    public Integer countAllStandardValue(Long ruleId) {
        return taskNewValueRepository.countTotal(ruleId);
    }

    @Override
    public Long findMatchTaskNewValue(Long ruleId) {
        return taskNewValueRepository.findMatchTaskNewValue(ruleId);
    }

    @Override
    public List<Map<String, Long>> findMatchTaskNewValueByRuleIds(List<Long> ruleIds) {
        return taskNewValueRepository.findMatchTaskNewValueByRuleIds(ruleIds);
    }

    @Override
    public List<TaskNewValue> selectExactTaskNewValue(Long ruleId, Long status, String resultValue) {
        List<TaskNewValue> taskNewValues = taskNewValueRepository.selectExactTaskNewValue(ruleId, status, resultValue);
        return taskNewValues;
    }

}
