package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueRepository;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class StandardValueDaoImpl implements StandardValueDao {

    @Autowired
    private StandardValueRepository standardValueRepository;


    @Override
    public StandardValue saveStandardValue(StandardValue standardValue) {
        return standardValueRepository.save(standardValue);
    }

    @Override
    public void deleteStandardValue(StandardValue standardValue) {
        standardValueRepository.delete(standardValue);
    }
}
