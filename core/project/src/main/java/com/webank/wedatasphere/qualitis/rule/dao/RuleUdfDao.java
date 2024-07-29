package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleUdf;
import java.util.List;
import java.util.Set;

/**
 * @author allenzhou@webank.com
 */
public interface RuleUdfDao {

    /**
     * Find by rule
     * @param rule
     * @return
     */
    List<RuleUdf> findByRule(Rule rule);

    /**
     * Find by template and udf name
     * @param rule
     * @param udfName
     * @return
     */
    RuleUdf findByRuleAndUdfName(Rule rule, String udfName);

    /**
     * Save all
     * @param ruleUdfs
     * @return
     */
    List<RuleUdf> saveAll(List<RuleUdf> ruleUdfs);

    /**
     * Delete
     * @param ruleUdfs
     * @return
     */
    void deleteAll(Set<RuleUdf> ruleUdfs);

    /**
     * Delete by rule
     * @param rule
     */
    void deleteByRule(Rule rule);
}
