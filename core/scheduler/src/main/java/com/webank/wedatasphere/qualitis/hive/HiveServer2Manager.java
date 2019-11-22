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

package com.webank.wedatasphere.qualitis.hive;

import com.webank.wedatasphere.qualitis.exception.HiveServer2ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author howeye
 */
@Component
public class HiveServer2Manager {

    private final String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveServer2Manager.class);

    @Retryable(value = HiveServer2ConnectException.class, maxAttempts = 3)
    public Connection createConnection(String url, String username, String password) throws HiveServer2ConnectException {
        LOGGER.info("Start to connect to hiveServer2, address: {}, username: {}", url, username);
        Connection con;
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            LOGGER.info("Failed to connect to hive server2, retrying. address: {}, username: {}", url, username);
            throw new HiveServer2ConnectException("Failed to connect to hive server2, address: " + url);
        }

        LOGGER.info("Succeed to connect to hive server2, address: {}, username: {}", url, username);
        return con;
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Failed to close hive session",  e);
            }
        }
    }
}

