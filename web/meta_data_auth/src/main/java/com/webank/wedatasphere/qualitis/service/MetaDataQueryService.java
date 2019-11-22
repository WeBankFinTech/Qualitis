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

package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaTableRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public interface MetaDataQueryService {

    /**
     * Paging get cluster information
     * @param request
     * @return
     */
    List<MetaDataCluster> queryCluster(PageRequest request);

    /**
     * Paging get database information
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    List<MetaDataDb> queryDb(QueryMetaDbRequest request) throws UnExpectedRequestException;

    /**
     * Paging get table information
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    List<MetaDataTable> queryTable(QueryMetaTableRequest request) throws UnExpectedRequestException;

    /**
     * Paging get column information
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    List<MetaDataColumn> queryColumn(QueryMetaColumnRequest request)
        throws UnExpectedRequestException;

    /**
     * Get total number of cluster
     * @return
     */
    long countClusterAll();

    /**
     * Get total number of database
     * @param clusterName
     * @return
     * @throws UnExpectedRequestException
     */
    long countDbByCluster(QueryMetaDbRequest clusterName) throws UnExpectedRequestException;

    /**
     * Get total number of table
     * @param dbName
     * @return
     * @throws UnExpectedRequestException
     */
    long countTableByDb(QueryMetaTableRequest dbName) throws UnExpectedRequestException;

    /**
     * Get total number of field
     * @param tableName
     * @return
     * @throws UnExpectedRequestException
     */
    long countColumnByTable(QueryMetaColumnRequest tableName) throws UnExpectedRequestException;
}
