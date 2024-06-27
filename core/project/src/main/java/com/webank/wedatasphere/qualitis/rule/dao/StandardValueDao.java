package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValue;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueDao {

    /**
     * save StandardValue
     * @param standardValue
     * @return
     */
    StandardValue saveStandardValue(StandardValue standardValue);

    /**
     * delete StandardValue
     * @param standardValue
     * @return
     */
    void deleteStandardValue(StandardValue standardValue);
}
