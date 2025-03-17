package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface AlarmArgumentsExecutionParametersRepository extends JpaRepository<AlarmArgumentsExecutionParameters, Long> {

    /**
     * Find AlarmArgumentsExecutionParameters by executionParameters
     * @param executionParameters
     * @return
     */
    List<AlarmArgumentsExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Delete AlarmArgumentsExecutionParameters by executionParametersInDb
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);

    /**
     * deleteByExecutionParametersId
     * @param id
     */
    @Modifying
    @Query(value = "delete from qualitis_alarm_arguments_execution_parameters where execution_parameters_id = ?1", nativeQuery = true)
    void deleteByExecutionParametersId(Long id);
}
