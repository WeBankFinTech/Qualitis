package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.RuleMetricTypeConfigDao;
import com.webank.wedatasphere.qualitis.dao.repository.RuleMetricTypeConfigRepository;
import com.webank.wedatasphere.qualitis.entity.RuleMetricTypeConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class RuleMetricTypeConfigDaoImpl implements RuleMetricTypeConfigDao {
    @Autowired
    private RuleMetricTypeConfigRepository ruleMetricTypeConfigRepository;

    @Override
    public List<RuleMetricTypeConfig> findAllRuleMetricTypeConfig() {
      return ruleMetricTypeConfigRepository.findAll();
    }
}
