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

import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author howeye
 */
@Service
public class RuleVariableServiceImpl implements RuleVariableService {

    @Autowired
    private RuleVariableRepository ruleVariableRepository;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public List<RuleVariable> saveRuleVariable(List<RuleVariable> ruleVariables) {
        return ruleVariableRepository.saveAll(ruleVariables);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteByRule(Rule rule) {
        ruleVariableRepository.deleteByRule(rule);
    }

}
