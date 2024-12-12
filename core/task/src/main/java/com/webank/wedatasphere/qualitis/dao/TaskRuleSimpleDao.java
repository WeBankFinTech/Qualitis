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

package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface TaskRuleSimpleDao {

    /**
     * Count yesterday execute num.
     * @param ruleId
     * @return
     */
    List<TaskRuleSimple> findBetweenYesterday(Long ruleId);

    /**
     * Find task rule simple by creator and project id
     * @param createUser
     * @param projectId
     * @param page
     * @param size
     * @return
     */
    List<TaskRuleSimple> findByCreateUserAndProjectId(String createUser, Long projectId, Integer page, Integer size);

    /**
     * Count task rule simple by creator and project id
     * @param createUser
     * @param projectId
     * @return
     */
    int countByCreateUserAndProjectId(String createUser, Long projectId);

    /**
     * Find task rule simple by task
     * 根据task寻找taskRuleSimple
     * @param task
     * @return
     */
    List<TaskRuleSimple> findByTask(Task task);
}
