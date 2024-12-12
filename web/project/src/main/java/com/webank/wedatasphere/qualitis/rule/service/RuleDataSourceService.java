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

package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;

import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
public interface RuleDataSourceService {

    /**
     * Check and save ruleDatasource
     * @param requests
     * @param rule
     * @param cs
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    List<RuleDataSource> checkAndSaveRuleDataSource(List<DataSourceRequest> requests, Rule rule, boolean cs, String loginUser)
        throws UnExpectedRequestException;

    /**
     * Delete ruleDatasource by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * Check and save custom ruleDatasource.
     * @param clusterName
     * @param proxyUser
     * @param loginUser
     * @param savedRule
     * @param cs
     * @param sqlCheck
     * @param linkisDataSourceId
     * @param linkisDataSourceVersionId
     * @param linkisDataSourceName
     * @param linkisDataSourceType
     * @return
     * @throws UnExpectedRequestException
     */
    @Transactional(rollbackFor = {RuntimeException.class, SemanticException.class, ParseException.class})
    List<RuleDataSource> checkAndSaveCustomRuleDataSource(String clusterName, String proxyUser, String loginUser, Rule savedRule, boolean cs
        , boolean sqlCheck, Long linkisDataSourceId, Long linkisDataSourceVersionId, String linkisDataSourceName, String linkisDataSourceType)
        throws UnExpectedRequestException;

    /**
     * Check cluster name supported
     * @param submittedClusterName
     * @throws UnExpectedRequestException
     */
    void checkDataSourceClusterSupport(String submittedClusterName)  throws UnExpectedRequestException;

    /**
     * Check cluster name supported
     * @param submittedClusterNames
     * @throws UnExpectedRequestException
     */
    void checkDataSourceClusterSupport(Set<String> submittedClusterNames)  throws UnExpectedRequestException;

    /**
     * Check and save file rule datasource
     * @param datasource
     * @param savedRule
     * @param cs
     * @return
     * @throws UnExpectedRequestException
     */
    RuleDataSource checkAndSaveFileRuleDataSource(DataSourceRequest datasource, Rule savedRule, boolean cs) throws UnExpectedRequestException;

    /**
     * Updata rule datasource count.
     * @param ruleInDb
     * @param varyAmount
     */
    void updateRuleDataSourceCount(Rule ruleInDb, Integer varyAmount);
}
