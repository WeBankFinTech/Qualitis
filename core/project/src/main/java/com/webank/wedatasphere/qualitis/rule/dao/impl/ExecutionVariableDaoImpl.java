package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.ExecutionVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.ExecutionVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class ExecutionVariableDaoImpl implements ExecutionVariableDao {

    @Autowired
    private ExecutionVariableRepository executionVariableRepository;

    @Override
    public List<ExecutionVariable> findByExecutionParameters(ExecutionParameters executionParameters) {
        return executionVariableRepository.findByExecutionParameters(executionParameters);
    }

    @Override
    public ExecutionVariable findById(Long executionVariableId) {
        return executionVariableRepository.findById(executionVariableId).orElse(null);
    }

    @Override
    public Set<ExecutionVariable> saveAll(List<ExecutionVariable> executionVariables) {
        Set<ExecutionVariable> result = new HashSet<>();
        result.addAll(executionVariableRepository.saveAll(executionVariables));
        return result;
    }

    @Override
    public void deleteByExecutionParameters(ExecutionParameters executionParametersInDb) {
        executionVariableRepository.deleteByExecutionParameters(executionParametersInDb);
    }

    @Override
    public List<ExecutionVariable> selectMateExecutionVariable(Integer type, String name, String value, ExecutionParameters executionParametersInDb) {
        return executionVariableRepository.selectMateExecutionVariable(type, name, value, executionParametersInDb);
    }
}
