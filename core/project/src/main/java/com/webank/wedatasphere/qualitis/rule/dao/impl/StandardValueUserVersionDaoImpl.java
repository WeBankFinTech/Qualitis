package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueUserVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueUserVersionReponsitory;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueUserVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueUserVersionDaoImpl implements StandardValueUserVersionDao {

    @Autowired
    private StandardValueUserVersionReponsitory standardValueUserVersionReponsitory;


    @Override
    public StandardValueUserVersion saveStandardValueUserVersion(StandardValueUserVersion standardValueUserVersion) {
        return standardValueUserVersionReponsitory.save(standardValueUserVersion);
    }

    @Override
    public List<StandardValueUserVersion> findListStandardValueUserVersion(Long standardVauleVersionId) {
        return standardValueUserVersionReponsitory.findListStandardValueUserVersion(standardVauleVersionId);
    }

    @Override
    public void deleteStandardValueUserVersion(StandardValueUserVersion standardValueUserVersion) {
        standardValueUserVersionReponsitory.delete(standardValueUserVersion);
    }
}
