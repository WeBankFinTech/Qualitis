package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StaticExecutionParametersDao {

    /**
     * Find StaticExecutionParameters by executionParameters
     * @param executionParameters
     * @return
     */
    List<StaticExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Find StaticExecutionParameters by id
     * @param staticExecutionParametersId
     * @return
     */
    StaticExecutionParameters findById(Long staticExecutionParametersId);

    /**
     * Save all StaticExecutionParameters .
     * @param staticExecutionParameterss
     * @return
     */
    Set<StaticExecutionParameters> saveAll(List<StaticExecutionParameters> staticExecutionParameterss);

    /**
     * Delete StaticExecutionParameters by ExecutionParameters
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);
}
