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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskResultRepository;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class TaskResultDaoImpl implements TaskResultDao {

    @Autowired
    private TaskResultRepository resultRepository;

    @Override
    public TaskResult findByApplicationIdAndRuleId(String applicationId, Long ruleId) {
        return resultRepository.findByApplicationIdAndRuleId(applicationId, ruleId);
    }

    @Override
    public Double findAvgByCreateTimeBetweenAndRuleId(String begin, String end, Long ruleId) {
        return resultRepository.findAvgByCreateTimeBetweenAndRuleId(begin, end, ruleId);
    }

    @Override
    public List<TaskResult> findByApplicationIdAndRuleIdIn(String applicationId, List<Long> ruleIds) {
        return resultRepository.findByApplicationIdAndRuleIdIn(applicationId, ruleIds);
    }
}
