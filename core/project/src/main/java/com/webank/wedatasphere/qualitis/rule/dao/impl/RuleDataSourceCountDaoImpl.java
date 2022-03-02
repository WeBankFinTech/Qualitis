package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceCountDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceCountRepository;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class RuleDataSourceCountDaoImpl implements RuleDataSourceCountDao {
    @Autowired
    private RuleDataSourceCountRepository ruleDataSourceCountRepository;

    @Override
    public Integer findCount(String datasourceName, Long userId) {
        RuleDataSourceCount  ruleDataSourceCount = ruleDataSourceCountRepository.findByDatasourceNameAndUserId(datasourceName, userId);
        if (ruleDataSourceCount == null) {
            return 0;
        }
        return ruleDataSourceCount.getDatasourceCount();
    }

    @Override
    public RuleDataSourceCount save(RuleDataSourceCount ruleDataSourceCount) {
        return ruleDataSourceCountRepository.save(ruleDataSourceCount);
    }
}
