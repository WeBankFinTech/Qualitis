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

import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmConfigRepository;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.AlarmConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class AlarmConfigDaoImpl implements AlarmConfigDao {

    @Autowired
    private AlarmConfigRepository alarmConfigRepository;

    @Override
    public List<AlarmConfig> saveAllAlarmConfig(List<AlarmConfig> alarmConfigs) {
        return alarmConfigRepository.saveAll(alarmConfigs);
    }

    @Override
    public List<AlarmConfig> getByRuleMetric(RuleMetric ruleMetric) {
        return alarmConfigRepository.getByRuleMetric(ruleMetric);
    }
}
