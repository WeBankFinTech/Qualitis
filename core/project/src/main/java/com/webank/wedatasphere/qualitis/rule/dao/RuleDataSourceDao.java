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

package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleDataSourceDao {

    /**
     * Save all rule datasource
     * @param ruleDataSources
     * @return
     */
    List<RuleDataSource> saveAllRuleDataSource(List<RuleDataSource> ruleDataSources);

    /**
     * Find rule datasource by rule
     * @param rule
     * @return
     */
    List<RuleDataSource> findByRule(Rule rule);

    /**
     * Find rule datasource by project id
     * @param projectId
     * @return
     */
  List<RuleDataSource> findByProjectId(Long projectId);


    /**
     * Find rule datasoruce by project id and datasource(cluster, db, table)
     * @param projectId
     * @param cluster
     * @param db
     * @param table
     * @return
     */
    List<RuleDataSource> findByProjectUser(Long projectId, String cluster, String db, String table);

    /**
     * Find project datasource by user
     * @param user
     * @return
     */
    List<Map<String, String>> findProjectDsByUser(String user);
}
