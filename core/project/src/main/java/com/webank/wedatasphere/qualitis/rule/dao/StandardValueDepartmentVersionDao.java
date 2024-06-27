package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueDepartmentVersion;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueDepartmentVersionDao {

    /**
     * save StandardValueDepartmentVersion
     * @param standardValueDepartmentVersion
     * @return
     */
    StandardValueDepartmentVersion saveStandardValueDepartment(StandardValueDepartmentVersion standardValueDepartmentVersion);

    /**
     * find StandardValueDepartmentVersion
     * @param standardVauleVersionId
     * @return
     */
    List<StandardValueDepartmentVersion> findListStandardValueDepartmentVersion(Long standardVauleVersionId);

    /**
     * delete StandardValueDepartmentVersion
     * @param standardValueDepartmentVersion
     */
    void deleteStandardValueDepartmentVersion(StandardValueDepartmentVersion standardValueDepartmentVersion);


}
