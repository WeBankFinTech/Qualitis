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

package com.webank.wedatasphere.qualitis.hive.dao.impl;

import com.webank.wedatasphere.qualitis.hive.client.HiveJdbcConnectionFactory;
import com.webank.wedatasphere.qualitis.hive.dao.HiveMetaDataDao;
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;

import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Component
public class HiveMetaDataDaoImpl implements HiveMetaDataDao {

    @Autowired
    private HiveJdbcConnectionFactory hiveJdbcConnectionFactory;

    @Override
    public List<String> getHiveDbByCluster(String clusterName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<String> hiveDbs = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = hiveJdbcConnectionFactory.getConnection(clusterName);
            String sql = "SELECT NAME FROM DBS";
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String dbName = rs.getString("NAME");
                hiveDbs.add(dbName);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return hiveDbs;
    }

    @Override
    public List<String> getHiveTableByClusterAndDb(String clusterName, String dbName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<String> hiveTables = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = hiveJdbcConnectionFactory.getConnection(clusterName);
            String sql = "SELECT TBL_NAME FROM DBS d, TBLS t WHERE d.DB_ID = t.DB_ID AND d.NAME = \"" + dbName + "\"";
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString("TBL_NAME");
                hiveTables.add(tableName);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return hiveTables;
    }

    @Override
    public List<HiveColumn> getHiveColumnByClusterAndDbAndTable(String clusterName, String dbName, String tableName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<HiveColumn> hiveColumns = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = hiveJdbcConnectionFactory.getConnection(clusterName);
            String normalColumnSql = "SELECT COLUMN_NAME, TYPE_NAME FROM DBS d, TBLS t, SDS s, CDS c, COLUMNS_V2 col WHERE " +
                    "d.DB_ID = t.DB_ID AND t.SD_ID = s.SD_ID AND s.CD_ID = c.CD_ID and c.CD_ID = col.CD_ID " +
                    "AND d.NAME = \"" + dbName + "\" AND t.TBL_NAME = \"" + tableName + "\"";
            statement = connection.createStatement();
            rs = statement.executeQuery(normalColumnSql);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                hiveColumns.add(new HiveColumn(columnName, typeName));
            }

            String partitionColumnSql = "SELECT PKEY_NAME, PKEY_TYPE FROM DBS d, TBLS t, PARTITION_KEYS p WHERE " +
                    "d.DB_ID = t.DB_ID AND t.TBL_ID = p.TBL_ID " +
                    "AND d.NAME = \"" + dbName + "\" AND t.TBL_NAME = \"" + tableName + "\"";
            ResultSet partitionRs = statement.executeQuery(partitionColumnSql);
            while (partitionRs.next()) {
                String columnName = partitionRs.getString("PKEY_NAME");
                String typeName = partitionRs.getString("PKEY_TYPE");
                hiveColumns.add(new HiveColumn(columnName, typeName));
            }

        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        }


        return hiveColumns;
    }
}
