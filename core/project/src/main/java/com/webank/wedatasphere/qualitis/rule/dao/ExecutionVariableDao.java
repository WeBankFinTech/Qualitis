package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ExecutionVariableDao {

    /**
     * Find ExecutionVariable by executionParameters
     * @param executionParameters
     * @return
     */
    List<ExecutionVariable> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Find ExecutionVariable by id
     * @param executionVariableId
     * @return
     */
    ExecutionVariable findById(Long executionVariableId);

    /**
     * Save all ExecutionVariable .
     * @param executionVariables
     * @return
     */
    Set<ExecutionVariable> saveAll(List<ExecutionVariable> executionVariables);

    /**
     * Delete StaticExecutionParameters by ExecutionParameters
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);

    /**
     * select Mate ExecutionVariable
     * @param type
     * @param name
     * @param value
     * @param executionParametersInDb
     * @return
     */
    List<ExecutionVariable>  selectMateExecutionVariable(Integer type,String name,String value,ExecutionParameters executionParametersInDb);


}
