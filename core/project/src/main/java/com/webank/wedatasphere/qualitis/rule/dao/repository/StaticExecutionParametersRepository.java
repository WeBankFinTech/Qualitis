package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StaticExecutionParametersRepository extends JpaRepository<StaticExecutionParameters, Long> {

    /**
     * Find StaticExecutionParameters by executionParameters
     * @param executionParameters
     * @return
     */
    List<StaticExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Delete StaticExecutionParameters by executionParametersInDb
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);

    /**
     * delete by ExecutionParametersId
     * @param id
     */
    @Modifying
    @Query(value = "delete from qualitis_static_execution_parameters where execution_parameters_id = ?1", nativeQuery = true)
    void deleteByExecutionParametersId(Long id);
}
