package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueDepartmentVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueDepartmentVersionReposiory;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueDepartmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueDepartmentVersionDaoImpl implements StandardValueDepartmentVersionDao {

    @Autowired
    private StandardValueDepartmentVersionReposiory standardValueDepartmentVersionReposiory;

    @Override
    public StandardValueDepartmentVersion saveStandardValueDepartment(StandardValueDepartmentVersion standardValueDepartmentVersion) {
        return standardValueDepartmentVersionReposiory.save(standardValueDepartmentVersion);
    }

    @Override
    public List<StandardValueDepartmentVersion> findListStandardValueDepartmentVersion(Long standardVauleVersionId) {
        return standardValueDepartmentVersionReposiory.findListStandardValueDepartmentVersion(standardVauleVersionId);
    }

    @Override
    public void deleteStandardValueDepartmentVersion(StandardValueDepartmentVersion standardValueDepartmentVersion) {
        standardValueDepartmentVersionReposiory.delete(standardValueDepartmentVersion);
    }
}
