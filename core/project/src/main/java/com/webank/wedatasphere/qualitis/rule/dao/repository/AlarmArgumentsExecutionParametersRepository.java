package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
