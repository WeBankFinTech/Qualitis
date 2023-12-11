package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ExecutionVariableRepository extends JpaRepository<ExecutionVariable, Long> {

    /**
     * Find ExecutionVariable by executionParameters
     * @param executionParameters
     * @return
     */
    List<ExecutionVariable> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Delete ExecutionVariable by executionParametersInDb
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);

    /**
     * select Mate ExecutionVariable
     *
     * @param type
     * @param name
     * @param value
     * @param executionParametersInDb
     * @return
     */
    @Query(value = "SELECT ac from ExecutionVariable ac WHERE ac.variableType = ?1 and ac.variableName=?2 and ac.variableValue=?3 and ac.executionParameters=?4 ")
    List<ExecutionVariable> selectMateExecutionVariable(Integer type, String name, String value, ExecutionParameters executionParametersInDb);
}
