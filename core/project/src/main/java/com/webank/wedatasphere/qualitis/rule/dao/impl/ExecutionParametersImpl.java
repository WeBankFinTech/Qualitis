package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.ExecutionParametersRepository;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng
 */
@Repository
public class ExecutionParametersImpl implements ExecutionParametersDao {

    @Autowired
    private ExecutionParametersRepository executionParametersRepository;

    @Override
    public List<ExecutionParameters> findAllExecutionParameters(Long projectId, String name, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return executionParametersRepository.findByName(projectId, name, pageable).getContent();
    }

    @Override
    public List<ExecutionParameters> findAll(Long projectId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return executionParametersRepository.findByProjectId(projectId, pageable).getContent();
    }

    @Override
    public ExecutionParameters findById(Long executionParametersId) {
        return executionParametersRepository.findById(executionParametersId).orElse(null);
    }

    @Override
    public ExecutionParameters saveExecutionParameters(ExecutionParameters executionParameters) {
        return executionParametersRepository.save(executionParameters);
    }

    @Override
    public void deleteExecutionParameters(ExecutionParameters executionParameters) {
        executionParametersRepository.delete(executionParameters);
    }

    @Override
    public int countTotal(Long projectId) {
        return executionParametersRepository.countTotal(projectId);
    }

    @Override
    public int countTotalByName(Long projectId, String name) {
        return executionParametersRepository.countTotalByName(projectId, name);
    }

    @Override
    public ExecutionParameters findByNameAndProjectId(String name, Long projectId) {
        return executionParametersRepository.findByNameAndProjectId(name, projectId);
    }

    @Override
    public List<ExecutionParameters> getAllExecutionParameters(Long projectId) {
        return executionParametersRepository.getAllExecutionParameters(projectId);
    }

    @Override
    public List<ExecutionParameters> selectAllExecutionParameters() {
        return executionParametersRepository.findAll();
    }

}
