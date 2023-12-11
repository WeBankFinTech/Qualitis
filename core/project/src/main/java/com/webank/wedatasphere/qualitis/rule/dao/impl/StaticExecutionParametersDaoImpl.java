package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StaticExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StaticExecutionParametersRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StaticExecutionParametersDaoImpl implements StaticExecutionParametersDao {

    @Autowired
    private StaticExecutionParametersRepository staticExecutionParametersRepository;

    @Override
    public List<StaticExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters) {
        return staticExecutionParametersRepository.findByExecutionParameters(executionParameters);
    }

    @Override
    public StaticExecutionParameters findById(Long staticExecutionParametersId) {
        return staticExecutionParametersRepository.findById(staticExecutionParametersId).orElse(null);
    }

    @Override
    public Set<StaticExecutionParameters> saveAll(List<StaticExecutionParameters> staticExecutionParameterss) {
        Set<StaticExecutionParameters> result = new HashSet<>();
        result.addAll(staticExecutionParametersRepository.saveAll(staticExecutionParameterss));
        return result;
    }

    @Override
    public void deleteByExecutionParameters(ExecutionParameters executionParametersInDb) {
        staticExecutionParametersRepository.deleteByExecutionParameters(executionParametersInDb);
    }
}
