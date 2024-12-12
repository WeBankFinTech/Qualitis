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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceMappingService;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RuleDataSourceMappingServiceImpl implements RuleDataSourceMappingService {

    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public List<RuleDataSourceMapping>  checkAndSaveRuleDataSourceMapping(List<MultiDataSourceJoinConfigRequest> mappings, Rule rule) {
        List<RuleDataSourceMapping> result = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest mapping : mappings) {
            RuleDataSourceMapping ruleDataSourceMapping = new RuleDataSourceMapping();
            ruleDataSourceMapping.setLeftStatement(mapping.getLeftStatement());
            ruleDataSourceMapping.setRightStatement(mapping.getRightStatement());
            ruleDataSourceMapping.setOperation(mapping.getOperation());

            ruleDataSourceMapping.setLeftColumnNames(String.join(",", mapping.getLeft().stream()
                    .map(MultiDataSourceJoinColumnRequest::getColumnName).collect(Collectors.toList())));
            ruleDataSourceMapping.setLeftColumnTypes(String.join("|", mapping.getLeft().stream()
                .map(MultiDataSourceJoinColumnRequest::getColumnType).collect(Collectors.toList())));
            ruleDataSourceMapping.setRightColumnNames(String.join(",", mapping.getRight().stream()
                    .map(MultiDataSourceJoinColumnRequest::getColumnName).collect(Collectors.toList())));
            ruleDataSourceMapping.setRightColumnTypes(String.join("|", mapping.getRight().stream()
                .map(MultiDataSourceJoinColumnRequest::getColumnType).collect(Collectors.toList())));
            ruleDataSourceMapping.setRule(rule);

            RuleDataSourceMapping savedRuleDataSourceMapping = ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
            result.add(savedRuleDataSourceMapping);
        }
        return result;
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleDataSourceMappingDao.deleteByRule(rule);
    }
}
