package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.LinksErrorCodeDao;
import com.webank.wedatasphere.qualitis.dao.repository.LinksErrorCodeRepository;
import com.webank.wedatasphere.qualitis.entity.LinksErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class LinksErrorCodeDaoImpl implements LinksErrorCodeDao {

    @Autowired
    private LinksErrorCodeRepository repository;

    @Override
    public List<LinksErrorCode> findAllLinksErrorCode() {
        return repository.findAll();
    }

    @Override
    public Set<LinksErrorCode> saveAll(List<LinksErrorCode> linksErrorCodes) {
        Set<LinksErrorCode> result = new HashSet<>();
        result.addAll(repository.saveAll(linksErrorCodes));
        return result;
    }
}
