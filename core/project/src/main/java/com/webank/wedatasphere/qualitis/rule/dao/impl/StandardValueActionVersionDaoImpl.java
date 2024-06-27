package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueActionVersionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueActionVersionRepository;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueActionVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueActionVersionDaoImpl implements StandardValueActionVersionDao {

    @Autowired
    private StandardValueActionVersionRepository standardValueActionVersionRepository;

    @Override
    public void saveAll(Iterable<StandardValueActionVersion> standardValueActionVersion) {
        standardValueActionVersionRepository.saveAll(standardValueActionVersion);
    }

//    @Override
//    public List<StandardValueActionVersion> findByStandardValueVersion(StandardValueVersion standardValueVersion) {
//        return standardValueActionVersionRepository.findByStandardValueActionVersion(standardValueVersion);
//    }

    @Override
    public void deleteByStandardValueVersion(StandardValueVersion standardValueVersion) {
        standardValueActionVersionRepository.deleteByStandardValueVersion(standardValueVersion);
    }

    @Override
    public void delete(StandardValueActionVersion standardValueActionVersion) {
        standardValueActionVersionRepository.delete(standardValueActionVersion);
    }
}
