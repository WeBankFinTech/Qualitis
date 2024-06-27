package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueActionVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueActionVersionRepository extends JpaRepository<StandardValueActionVersion, Long>, JpaSpecificationExecutor<StandardValueActionVersion> {

//    /**
//     *
//     * @param standardValueVersion
//     * @return
//     */
//    List<StandardValueActionVersion> findByStandardValueActionVersion(StandardValueVersion standardValueVersion);

    /**
     * Delete StandardValueActionVersion Action by StandardValueVersion
     * @param standardValueVersion
     */
    void deleteByStandardValueVersion(StandardValueVersion standardValueVersion);
}
