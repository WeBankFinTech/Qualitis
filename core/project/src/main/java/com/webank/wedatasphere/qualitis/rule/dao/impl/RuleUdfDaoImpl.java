package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleUdfDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleUdfRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleUdf;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/21 15:03
 */
@Repository
public class RuleUdfDaoImpl implements RuleUdfDao {
    @Autowired
    private RuleUdfRepository ruleUdfRepository;

    @Override
    public List<RuleUdf> findByRule(Rule rule) {
        return ruleUdfRepository.findByRule(rule);
    }

    @Override
    public RuleUdf findByRuleAndUdfName(Rule rule, String udfName) {
        return null;
    }

    @Override
    public List<RuleUdf> saveAll(List<RuleUdf> ruleUdfs) {
        return ruleUdfRepository.saveAll(ruleUdfs);
    }

    @Override
    public void deleteAll(Set<RuleUdf> ruleUdfs) {
        ruleUdfRepository.deleteAll(ruleUdfs);
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleUdfRepository.deleteByRule(rule);
    }
}
