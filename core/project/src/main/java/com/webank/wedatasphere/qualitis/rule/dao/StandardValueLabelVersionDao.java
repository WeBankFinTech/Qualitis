package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueLabelVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueLabelVersionDao {

    /**
     * Save all StandardValueLabelVersion
     *
     * @param standardValueLabelVersion
     */
    void saveAll(Iterable<StandardValueLabelVersion> standardValueLabelVersion);

//    /**
//     * Find by StandardValueLabelVersion by standardValueVersion
//     * @param standardValueVersion
//     * @return
//     */
//    List<StandardValueLabelVersion> findByStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete StandardValueVersion
     * @param standardValueVersion
     */
    void deleteByStandardValueVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete one label
     * @param standardValueLabelVersion
     */
    void delete(StandardValueLabelVersion standardValueLabelVersion);

}
