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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<TaskResult> findByApplicationAndRule(String applicationId, Long ruleId) {
        return resultRepository.findByApplicationIdAndRuleId(applicationId, ruleId);
    }

    @Override
    public Double findAvgByCreateTimeBetweenAndRule(String begin, String end, Long ruleId) {
        return resultRepository.findAvgByCreateTimeBetween(begin, end, ruleId);
    }

    @Override
    public List<TaskResult> findByApplicationIdAndRuleIn(String applicationId, List<Long> ruleIds) {
        return resultRepository.findByApplicationIdAndRuleIdIn(applicationId, ruleIds);
    }

    @Override
    public TaskResult saveTaskResult(TaskResult taskResult) {
        return resultRepository.save(taskResult);
    }

    @Override
    public List<Long> findRuleByRuleMetric(Long ruleMetricId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return resultRepository.findRuleByRuleMetricId(ruleMetricId, pageable).getContent();
    }

    @Override
    public List<TaskResult> findValuesByRuleMetric(long ruleMetricId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return resultRepository.findValuesByRuleAndRuleMetric(ruleMetricId, pageable).getContent();
    }

    @Override
    public Double findAvgByCreateTimeBetweenAndRuleAndRuleMetric(String start, String end, Long ruleId, Long ruleMetricId) {
        return resultRepository.findAvgByCreateTimeBetween(start, end, ruleId, ruleMetricId);
    }

    @Override
    public TaskResult find(String applicationId, Long ruleId, Long ruleMetricId) {
        return resultRepository.findValue(applicationId, ruleId, ruleMetricId);
    }

    @Override
    public int countValuesByRuleMetric(long ruleMetricId) {
        return resultRepository.countValuesByRuleMetric(ruleMetricId);
    }

    @Override
    public int countRuleByRuleMetric(Long ruleMetricId) {
        return resultRepository.countRulesByRuleMetric(ruleMetricId);
    }
}
