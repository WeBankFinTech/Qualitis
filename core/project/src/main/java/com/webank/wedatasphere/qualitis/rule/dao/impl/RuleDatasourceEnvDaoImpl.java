package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDatasourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDatasourceEnvRepository;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-03 16:01
 * @description
 */
@Repository
public class RuleDatasourceEnvDaoImpl implements RuleDatasourceEnvDao {

    @Autowired
    private RuleDatasourceEnvRepository ruleDatasourceEnvRepository;

    @Override
    public RuleDataSourceEnv findByEnvId(Long envId) {
        return ruleDatasourceEnvRepository.findByEnvId(envId);
    }

    @Override
    public void deleteByEnvId(Long envId) {
        ruleDatasourceEnvRepository.deleteByEnvId(envId);
    }

    @Override
    public void saveAllRuleDataSourceEnv(List<RuleDataSourceEnv> datasourceEnvList) {
        ruleDatasourceEnvRepository.saveAll(datasourceEnvList);
    }

    @Override
    public List<String> findAllEnvName() {
        return ruleDatasourceEnvRepository.findAllEnvName();
    }

    @Override
    public void deleteAll(List<RuleDataSourceEnv> datasourceEnvList) {
        ruleDatasourceEnvRepository.deleteAll(datasourceEnvList);
    }

    @Override
    public void deleteByDataSourceId(Long datasourceId) {
        ruleDatasourceEnvRepository.deleteByDataSourceId(datasourceId);
    }

    @Override
    public List<RuleDataSourceEnv> findByRuleDataSourceList(List<RuleDataSource> ruleDataSourceList) {
        return ruleDatasourceEnvRepository.findByRuleDataSourceIn(ruleDataSourceList);
    }

    @Override
    public List<RuleDataSourceEnv> findByDataSourceId(Long datasourceId) {
        return ruleDatasourceEnvRepository.findByDataSourceId(datasourceId);
    }

}
