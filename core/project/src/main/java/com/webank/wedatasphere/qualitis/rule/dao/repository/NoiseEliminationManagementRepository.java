package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface NoiseEliminationManagementRepository extends JpaRepository<NoiseEliminationManagement, Long> {

    /**
     * Find NoiseEliminationManagement by executionParameters
     * @param executionParameters
     * @return
     */
    List<NoiseEliminationManagement> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Delete NoiseEliminationManagement by executionParametersInDb
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);

    /**
     * delete by ExecutionParametersId
     * @param id
     */
    @Modifying
    @Query(value = "delete from qualitis_noise_elimination_management where execution_parameters_id = ?1", nativeQuery = true)
    void deleteByExecutionParametersId(Long id);
}
