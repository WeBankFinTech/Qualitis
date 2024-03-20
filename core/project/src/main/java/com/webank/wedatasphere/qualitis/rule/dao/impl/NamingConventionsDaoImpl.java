package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.NamingConventionsDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.NamingConventionsRepository;
import com.webank.wedatasphere.qualitis.rule.entity.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class NamingConventionsDaoImpl implements NamingConventionsDao {

    @Autowired
    private NamingConventionsRepository namingConventionsRepository;

    @Override
    public List<NamingConventions> findAll() {
        return namingConventionsRepository.findAll();
    }
}
