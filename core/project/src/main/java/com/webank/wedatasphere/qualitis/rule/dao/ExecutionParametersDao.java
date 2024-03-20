package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;

import java.util.List;

/**
 * @author v_gaojiedeng
 */
public interface ExecutionParametersDao {

    /**
     * find All ExecutionParameters
     * @param projectId
     * @param name
     * @param page
     * @param size
     * @return
     */
    List<ExecutionParameters> findAllExecutionParameters(Long projectId, String name, int page, int size);

    /**
     * find All
     * @param projectId
     * @param page
     * @param size
     * @return
     */
    List<ExecutionParameters> findAll(Long projectId, int page, int size);

    /**
     * find By Id
     * @param executionParametersId
     * @return
     */
    ExecutionParameters findById(Long executionParametersId);

    /**
     * save ExecutionParameters
     * @param executionParameters
     * @return
     */
    ExecutionParameters saveExecutionParameters(ExecutionParameters executionParameters);

    /**
     * delete ExecutionParameters
     * @param executionParameters
     */
    void deleteExecutionParameters(ExecutionParameters executionParameters);

    /**
     * count Total
     * @param projectId
     * @return
     */
    int countTotal(Long projectId);

    /**
     * count Total By Name
     * @param projectId
     * @param name
     * @return
     */
    int countTotalByName(Long projectId, String name);

    /**
     * find By Name And ProjectId
     * @param name
     * @param projectId
     * @return
     */
    ExecutionParameters findByNameAndProjectId(String name, Long projectId);

    /**
     * get All ExecutionParameters
     * @param projectId
     * @return
     */
    List<ExecutionParameters> getAllExecutionParameters(Long projectId);

    /**
     * select All executionParameters
     * @return
     */
    List<ExecutionParameters> selectAllExecutionParameters();

}
