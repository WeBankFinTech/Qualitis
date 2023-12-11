package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
