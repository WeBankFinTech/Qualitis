package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.RuleMetricDepartmentUserDao;
import com.webank.wedatasphere.qualitis.dao.repository.RuleMetricDepartmentUserRepository;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.RuleMetricDepartmentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class RuleMetricDepartmentUserDaoImpl implements RuleMetricDepartmentUserDao {
    @Autowired
    private RuleMetricDepartmentUserRepository ruleMetricDepartmentUserRepository;

    @Override
    public RuleMetricDepartmentUser add(RuleMetricDepartmentUser ruleMetricDepartmentUser) {
        return ruleMetricDepartmentUserRepository.save(ruleMetricDepartmentUser);
    }

    @Override
    public RuleMetricDepartmentUser modify(RuleMetricDepartmentUser ruleMetricDepartmentUser) {
        return ruleMetricDepartmentUserRepository.save(ruleMetricDepartmentUser);
    }

    @Override
    public void delete(RuleMetricDepartmentUser ruleMetricDepartmentUser) {
      ruleMetricDepartmentUserRepository.delete(ruleMetricDepartmentUser);
    }

    @Override
    public RuleMetricDepartmentUser findByRuleMetric(RuleMetric ruleMetricInDb) {
        return ruleMetricDepartmentUserRepository.findByRuleMetric(ruleMetricInDb);
    }
}
