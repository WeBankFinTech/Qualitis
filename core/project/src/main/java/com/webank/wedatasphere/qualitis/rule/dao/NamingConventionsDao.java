package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.NamingConventions;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface NamingConventionsDao {

    /**
     * find All
     * @return
     */
    List<NamingConventions> findAll();
}
