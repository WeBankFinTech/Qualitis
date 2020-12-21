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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.Set;

/**
 * @author howeye
 */
public class MultiDataSourceConfigRequest {

    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    private String filter;

    public MultiDataSourceConfigRequest() {
    }

    public MultiDataSourceConfigRequest(Set<RuleDataSource> ruleDataSources, Integer index) {
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (!ruleDataSource.getDatasourceIndex().equals(index)) {
                continue;
            }

            this.dbName = ruleDataSource.getDbName();
            this.tableName = ruleDataSource.getTableName();
            this.filter = ruleDataSource.getFilter();
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public static void checkRequest(MultiDataSourceConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getDbName(), "Db name");
        CommonChecker.checkString(request.getTableName(), "Table name");
        CommonChecker.checkString(request.getFilter(), "Filter");
    }
}
