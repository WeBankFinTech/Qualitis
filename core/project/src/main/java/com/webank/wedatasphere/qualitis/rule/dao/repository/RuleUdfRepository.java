package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleUdf;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author allenzhou@webank.com
 */
public interface RuleUdfRepository extends JpaRepository<RuleUdf, Long> {

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
     * Delete by rule
     * @param rule
     */
    void deleteByRule(Rule rule);
}
