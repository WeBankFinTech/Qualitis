package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueActionVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueActionVersionDao {

    /**
     * Save all standardValueActionVersion
     *
     * @param standardValueActionVersion
     */
    void saveAll(Iterable<StandardValueActionVersion> standardValueActionVersion);

//    /**
//     * Find by StandardValueActionVersion by standardValueVersion
//     * @param standardValueVersion
//     * @return
//     */
//    List<StandardValueActionVersion> findByStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete StandardValueVersion
     * @param standardValueVersion
     */
    void deleteByStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete one label
     * @param standardValueActionVersion
     */
    void delete(StandardValueActionVersion standardValueActionVersion);
}
