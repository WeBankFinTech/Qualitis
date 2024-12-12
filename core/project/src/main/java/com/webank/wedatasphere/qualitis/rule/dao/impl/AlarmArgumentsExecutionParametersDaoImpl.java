package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.AlarmArgumentsExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmArgumentsExecutionParametersRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class AlarmArgumentsExecutionParametersDaoImpl implements AlarmArgumentsExecutionParametersDao {

    @Autowired
    private AlarmArgumentsExecutionParametersRepository alarmArgumentsExecutionParametersRepository;

    @Override
    public List<AlarmArgumentsExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters) {
        return alarmArgumentsExecutionParametersRepository.findByExecutionParameters(executionParameters);
    }

    @Override
    public AlarmArgumentsExecutionParameters findById(Long alarmArgumentsExecutionParametersId) {
        return alarmArgumentsExecutionParametersRepository.findById(alarmArgumentsExecutionParametersId).orElse(null);
    }

    @Override
    public Set<AlarmArgumentsExecutionParameters> saveAll(List<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameters) {
        Set<AlarmArgumentsExecutionParameters> result = new HashSet<>();
        result.addAll(alarmArgumentsExecutionParametersRepository.saveAll(alarmArgumentsExecutionParameters));
        return result;
    }

    @Override
    public void deleteByExecutionParameters(ExecutionParameters executionParametersInDb) {
        alarmArgumentsExecutionParametersRepository.deleteByExecutionParameters(executionParametersInDb);
    }
}
