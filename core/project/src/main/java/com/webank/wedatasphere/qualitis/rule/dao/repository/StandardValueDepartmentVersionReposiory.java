package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueDepartmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueDepartmentVersionReposiory extends JpaRepository<StandardValueDepartmentVersion, Long>, JpaSpecificationExecutor<StandardValueDepartmentVersion> {

    /**
     * find StandardValueDepartmentVersion by standardVauleVersionId
     * @param standardVauleVersionId
     * @return
     */
    @Query(value = "select td.* from qualitis_standard_value_department_version td where td.standard_value_version_id = ?1",nativeQuery = true)
    List<StandardValueDepartmentVersion> findListStandardValueDepartmentVersion(Long standardVauleVersionId);

}
