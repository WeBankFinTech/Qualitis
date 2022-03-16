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

import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleGroupRepository;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleGroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author howeye
 */
@Repository
public class RuleGroupDaoImpl implements RuleGroupDao {

    @Autowired
    private RuleGroupRepository ruleGroupRepository;

    @Override
    public RuleGroup saveRuleGroup(RuleGroup ruleGroup) {
        return ruleGroupRepository.save(ruleGroup);
    }

    @Override
    public RuleGroup findById(Long id) {
        return ruleGroupRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(RuleGroup ruleGroup) {
        ruleGroupRepository.delete(ruleGroup);
    }

    @Override
    public RuleGroup findByRuleGroupNameAndProjectId(String ruleGroupName, Long projectId) {
        return ruleGroupRepository.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
    }

    @Override
    public List<RuleGroup> findByProjectId(Long projectId) {
        return ruleGroupRepository.findByProjectId(projectId);
    }
}
