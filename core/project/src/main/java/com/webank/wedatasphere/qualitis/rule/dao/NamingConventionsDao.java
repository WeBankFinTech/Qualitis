package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.NamingConventions;

import java.util.List;

/**
 * @author 
 */
public interface NamingConventionsDao {

    /**
     * find All
     * @return
     */
    List<NamingConventions> findAll();
}
