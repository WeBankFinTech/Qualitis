package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValue;

/**
 * @author
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
