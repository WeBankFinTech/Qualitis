package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface NoiseEliminationManagementDao {

    /**
     * Find NoiseEliminationManagement by executionParameters
     * @param executionParameters
     * @return
     */
    List<NoiseEliminationManagement> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Find NoiseEliminationManagement by id
     * @param noiseEliminationManagementId
     * @return
     */
    NoiseEliminationManagement findById(Long noiseEliminationManagementId);

    /**
     * Save all NoiseEliminationManagement .
     * @param noiseEliminationManagement
     * @return
     */
    Set<NoiseEliminationManagement> saveAll(List<NoiseEliminationManagement> noiseEliminationManagement);

    /**
     * Delete NoiseEliminationManagement by ExecutionParameters
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);


}
