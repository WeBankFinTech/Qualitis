package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.TaskResultStatus;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-14 10:01
 * @description
 */
public interface TaskResultStatusDao {

    /**
     * Finding by applicationId and status and ruleId
     * @param applicationId
     * @param ruleId
     * @param status
     * @return
     */
    List<TaskResultStatus> findByStatus(String applicationId, Long ruleId, Integer status);

    /**
     * Saving batch data
     * @param taskResultStatusList
     */
    void saveBatch(List<TaskResultStatus> taskResultStatusList);
}
