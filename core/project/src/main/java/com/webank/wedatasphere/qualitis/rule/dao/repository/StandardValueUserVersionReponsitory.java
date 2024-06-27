package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueUserVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueUserVersionReponsitory extends JpaRepository<StandardValueUserVersion, Long>, JpaSpecificationExecutor<StandardValueUserVersion> {

    /**
     * find StandardValueUserVersion by standardVauleVersionId
     * @param standardVauleVersionId
     * @return
     */
    @Query(value = "select td.* from qualitis_standard_value_user_version td where td.standard_value_version_id = ?1", nativeQuery = true)
    List<StandardValueUserVersion> findListStandardValueUserVersion(Long standardVauleVersionId);



}
