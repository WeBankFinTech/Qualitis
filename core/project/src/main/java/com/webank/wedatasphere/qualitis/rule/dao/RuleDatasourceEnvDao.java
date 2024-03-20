package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-03 16:00
 * @description
 */
public interface RuleDatasourceEnvDao {

    /**
     * Find by env id.
     * @param envId
     * @return
     */
    RuleDataSourceEnv findByEnvId(Long envId);

    /**
     * Save all
     * @param datasourceEnvList
     */
    void saveAllRuleDataSourceEnv(List<RuleDataSourceEnv> datasourceEnvList);

    /**
     * Find all
     * @return
     */
    List<String> findAllEnvName();

    /**
     * Delete all
     * @param datasourceEnvList
     */
    void deleteAll(List<RuleDataSourceEnv> datasourceEnvList);

    /**
     * Delete by id
     * @param datasourceId
     */
    void deleteByDataSourceId(Long datasourceId);

    /**
     * find By Rule Data Source List
     * @param ruleDataSourceList
     * @return
     */
    List<RuleDataSourceEnv> findByRuleDataSourceList(List<RuleDataSource> ruleDataSourceList);
}
