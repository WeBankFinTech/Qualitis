package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueLabelVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueLabelVersionReponsitory extends JpaRepository<StandardValueLabelVersion, Long>, JpaSpecificationExecutor<StandardValueLabelVersion> {

//    /**
//     *
//     * @param standardValueVersion
//     * @return
//     */
//    List<StandardValueLabelVersion> findByStandardValueActionVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete StandardValueLabelVersion Action by StandardValueVersion
     * @param standardValueVersion
     */
    void deleteByStandardValueVersion(StandardValueVersion standardValueVersion);






}
