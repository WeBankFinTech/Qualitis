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
import com.webank.wedatasphere.qualitis.metadata.response.DataMapResultInfo;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvMappingRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public interface RuleDataSourceService {

    /**
     * Check and save ruleDatasource
     * @param requests
     * @param rule
     * @param ruleGroup
     * @param cs
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    List<RuleDataSource> checkAndSaveRuleDataSource(List<DataSourceRequest> requests, Rule rule,
        RuleGroup ruleGroup, boolean cs, String loginUser)
        throws UnExpectedRequestException;

    /**
     * Delete ruleDatasource by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * Delete ruleDatasource by rule group
     * @param ruleGroup
     */
    void deleteByRuleGroup(RuleGroup ruleGroup);

    /**
     * Check and save custom ruleDatasource.
     * @param clusterName
     * @param fileId
     * @param fpsTableDesc
     * @param fileDb
     * @param fileTable
     * @param fileDelimiter
     * @param fileType
     * @param fileHeader
     * @param proxyUser
     * @param fileHashValues
     * @param loginUser
     * @param savedRule
     * @param cs
     * @param fps
     * @param sqlCheck
     * @param linkisDataSourceId
     * @param linkisDataSourceVersionId
     * @param linkisDataSourceName
     * @param linkisDataSourceType
     * @param dataSourceEnvRequests
     * @param dataSourceEnvMappingRequests
     * @param dcnRangeType
     * @return
     * @throws UnExpectedRequestException
     */
    @Transactional(rollbackFor = {RuntimeException.class, SemanticException.class, ParseException.class})
    List<RuleDataSource> checkAndSaveCustomRuleDataSource(String clusterName, String fileId, String fpsTableDesc, String fileDb,
        String fileTable, String fileDelimiter, String fileType, Boolean fileHeader, String proxyUser, String fileHashValues, String loginUser
        , Rule savedRule, boolean cs, boolean fps, boolean sqlCheck, Long linkisDataSourceId, Long linkisDataSourceVersionId,
        String linkisDataSourceName
        , String linkisDataSourceType, List<DataSourceEnvRequest> dataSourceEnvRequests,
        List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests, String dcnRangeType) throws UnExpectedRequestException;

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
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    RuleDataSource checkAndSaveFileRuleDataSource(DataSourceRequest datasource, Rule savedRule, boolean cs, String loginUser) throws UnExpectedRequestException;

    /**
     * Updata rule datasource count.
     * @param ruleInDb
     * @param varyAmount
     */
    void updateRuleDataSourceCount(Rule ruleInDb, Integer varyAmount);

    /**
     * update In Lock
     * @param datasourceName
     * @param userId
     * @param varyAmount
     * @param ruleInDb
     */
    void updateInLock(StringBuilder datasourceName, Long userId, Integer varyAmount, Rule ruleInDb);

    /**
     * add some fields from dms: sub_system_id,tag_code,depart_name
     * @param userName
     * @return
     */
    GeneralResponse<DataMapResultInfo<String>> syncMetadata(String userName);
}
