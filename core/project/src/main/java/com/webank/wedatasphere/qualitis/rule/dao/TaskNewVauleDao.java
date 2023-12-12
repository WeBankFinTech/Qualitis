package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.TaskNewValue;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TaskNewVauleDao {

    /**
     * Save TaskNewValue
     *
     * @param taskNewValue
     * @return
     */
    TaskNewValue saveTaskNewValue(TaskNewValue taskNewValue);

    /**
     * Delete TaskNewValue
     *
     * @param taskNewValue
     */
    void delete(TaskNewValue taskNewValue);

    /**
     * Find by taskNewValue.
     *
     * @param id
     * @return
     */
    TaskNewValue findById(Long id);

    /**
     * find All taskNewValue
     *
     * @param ruleId
     * @param page
     * @param size
     * @return
     */
    List<TaskNewValue> findAllTaskNewValue(Long ruleId, int page, int size);

    /**
     * count standardValue
     *
     * @param ruleId
     * @return
     */
    Integer countAllStandardValue(Long ruleId);

    /**
     * find TaskNewValue
     *
     * @param ruleId
     * @return
     */
    Long findMatchTaskNewValue(Long ruleId);

    /**
     * find TaskNewValue
     *
     * @param ruleId
     * @param status
     * @param resultValue
     * @return
     */
    List<TaskNewValue> selectExactTaskNewValue(Long ruleId, Long status, String resultValue);
}
