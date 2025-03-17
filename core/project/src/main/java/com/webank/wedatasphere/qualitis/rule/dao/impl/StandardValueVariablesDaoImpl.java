package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVariablesDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.StandardValueVariablesRepository;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-09-11 10:03
 * @description
 */
@Repository
public class StandardValueVariablesDaoImpl implements StandardValueVariablesDao {

    @Autowired
    private StandardValueVariablesRepository standardValueVariablesRepository;

    @Override
    public void save(StandardValueVariables standardValueVariables) {
        standardValueVariablesRepository.save(standardValueVariables);
    }

    @Override
    public void saveAll(List<StandardValueVariables> standardValueVariablesList) {
        standardValueVariablesRepository.saveAll(standardValueVariablesList);
    }

    @Override
    public List<StandardValueVariables> findByRuleId(Long ruleId) {
        return standardValueVariablesRepository.findByRuleId(ruleId);
    }

    @Override
    public void deleteAll(List<StandardValueVariables> standardValueVariablesList) {
        standardValueVariablesRepository.deleteAll(standardValueVariablesList);
    }
}
