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

import com.webank.wedatasphere.qualitis.dao.TaskRuleSimpleDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleSimpleRepository;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
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
public class TaskRuleSimpleDaoImpl implements TaskRuleSimpleDao {

    @Autowired
    private TaskRuleSimpleRepository taskRuleSimpleRepository;

    @Override
    public List<TaskRuleSimple> findBetweenYesterday(Long ruleId) {
        return taskRuleSimpleRepository.findBetweenYesterday(ruleId);
    }

    @Override
    public List<TaskRuleSimple> findByCreateUserAndProjectId(String createUser, Long projectId, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRuleSimpleRepository.findByCreateUserAndProjectId(createUser, projectId, pageable);
    }

    @Override
    public int countByCreateUserAndProjectId(String createUser, Long projectId) {
        return taskRuleSimpleRepository.countByCreateUserAndProjectId(createUser, projectId).size();
    }

    @Override
    public List<TaskRuleSimple> findByTask(Task task) {
        return taskRuleSimpleRepository.findByTask(task);
    }
}
