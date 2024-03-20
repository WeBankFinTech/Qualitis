package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-03 16:01
 * @description
 */
public interface RuleDatasourceEnvRepository extends JpaRepository<RuleDataSourceEnv, Long>, JpaSpecificationExecutor<RuleDataSourceEnv> {

    /**
     * FindByEnvId
     * @param envId
     * @return
     */
    RuleDataSourceEnv findByEnvId(Long envId);

    /**
     * Find All Env Name
     * @return
     */
    @Query(value = "SELECT rdse.envName FROM RuleDataSourceEnv rdse")
    List<String> findAllEnvName();

    /**
     * deleteByDataSourceId
     * @param datasourceId
     */
    @Modifying
    @Query(value = "delete from qualitis_rule_datasource_env where rule_data_source_id = ?1", nativeQuery = true)
    void deleteByDataSourceId(Long datasourceId);

    /**
     * find By Rule Data Source In
     * @param ruleDataSourceList
     * @return
     */
    List<RuleDataSourceEnv> findByRuleDataSourceIn(List<RuleDataSource> ruleDataSourceList);
}
