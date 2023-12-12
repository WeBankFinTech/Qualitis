package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface AlarmArgumentsExecutionParametersDao {

    /**
     * Find AlarmArgumentsExecutionParameters by executionParameters
     * @param executionParameters
     * @return
     */
    List<AlarmArgumentsExecutionParameters> findByExecutionParameters(ExecutionParameters executionParameters);

    /**
     * Find AlarmArgumentsExecutionParameters by id
     * @param alarmArgumentsExecutionParametersId
     * @return
     */
    AlarmArgumentsExecutionParameters findById(Long alarmArgumentsExecutionParametersId);

    /**
     * Save all AlarmArgumentsExecutionParameters .
     * @param alarmArgumentsExecutionParameters
     * @return
     */
    Set<AlarmArgumentsExecutionParameters> saveAll(List<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameters);

    /**
     * Delete AlarmArgumentsExecutionParameters by ExecutionParameters
     * @param executionParametersInDb
     */
    void deleteByExecutionParameters(ExecutionParameters executionParametersInDb);


}
