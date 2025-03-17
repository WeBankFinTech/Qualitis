package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVariables;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-10 15:26
 * @description
 */
public interface StandardValueVariablesDao {

    void save(StandardValueVariables standardValueVariables);

    void saveAll(List<StandardValueVariables> standardValueVariablesList);

    List<StandardValueVariables> findByRuleId(Long ruleId);

    void deleteAll(List<StandardValueVariables> standardValueVariablesList);

}
