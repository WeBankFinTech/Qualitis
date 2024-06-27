package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueLabelVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueLabelVersionReponsitory;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueLabelVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueLabelVersionDaoImpl implements StandardValueLabelVersionDao {

    @Autowired
    private StandardValueLabelVersionReponsitory standardValueLabelVersionReponsitory;

    @Override
    public void saveAll(Iterable<StandardValueLabelVersion> standardValueLabelVersion) {
        standardValueLabelVersionReponsitory.saveAll(standardValueLabelVersion);
    }

//    @Override
//    public List<StandardValueLabelVersion> findByStandardValueVersion(StandardValueVersion standardValueVersion) {
//        return standardValueLabelVersionReponsitory.findByStandardValueActionVersion(standardValueVersion);
//    }

    @Override
    public void deleteByStandardValueVersion(StandardValueVersion standardValueVersion) {
        standardValueLabelVersionReponsitory.deleteByStandardValueVersion(standardValueVersion);
    }

    @Override
    public void delete(StandardValueLabelVersion standardValueLabelVersion) {
        standardValueLabelVersionReponsitory.delete(standardValueLabelVersion);
    }
}
