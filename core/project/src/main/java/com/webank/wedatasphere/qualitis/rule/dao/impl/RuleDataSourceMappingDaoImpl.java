/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceMappingRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Repository
public class RuleDataSourceMappingDaoImpl implements RuleDataSourceMappingDao {

    @Autowired
    private RuleDataSourceMappingRepository ruleDataSourceMappingRepository;

    @Override
    public RuleDataSourceMapping saveRuleDataSourceMapping(RuleDataSourceMapping ruleDataSourceMapping) {
        return ruleDataSourceMappingRepository.save(ruleDataSourceMapping);
    }

    @Override
    public List<RuleDataSourceMapping> saveAll(List<RuleDataSourceMapping> ruleDataSourceMappingList) {
        return ruleDataSourceMappingRepository.saveAll(ruleDataSourceMappingList);
    }

    @Override
    public void deleteByRule(Rule rule) {
        List<RuleDataSourceMapping> ruleDataSourceMappingList = ruleDataSourceMappingRepository.findByRule(rule);
        if (CollectionUtils.isEmpty(ruleDataSourceMappingList)) {
            return;
        }
        List<Long> ids = ruleDataSourceMappingList.stream().map(RuleDataSourceMapping::getId).collect(Collectors.toList());
        ruleDataSourceMappingRepository.deleteByIdIn(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByRuleList(List<Rule> rules) {
        ruleDataSourceMappingRepository.deleteByRuleIn(rules);
    }

    @Override
    public List<RuleDataSourceMapping> findByRuleList(List<Rule> ruleList) {
        return ruleDataSourceMappingRepository.findByRuleIn(ruleList);
    }
}
