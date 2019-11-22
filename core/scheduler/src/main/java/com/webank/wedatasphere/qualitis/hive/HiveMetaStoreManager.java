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

import com.webank.wedatasphere.qualitis.exception.HiveMetaStoreConnectException;
import com.webank.wedatasphere.qualitis.exception.HiveMetaStoreConnectException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * @author howeye
 */
@Component
public class HiveMetaStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveMetaStoreManager.class);

    @Retryable(value = HiveMetaStoreConnectException.class, maxAttempts = 3)
    public HiveMetaStoreClient createHiveMetaClient(String hiveMetaAddress, String username) throws HiveMetaStoreConnectException {
        LOGGER.info("Start to connect to hive meta store, address: {}, username: {}", hiveMetaAddress, username);
        System.setProperty("HADOOP_USER_NAME", username);
        HiveConf conf = new HiveConf();
        conf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaAddress);

        HiveMetaStoreClient tmpClient;
        try {
            tmpClient = new HiveMetaStoreClient(conf);
        } catch (MetaException e) {
            LOGGER.info("Failed to connect to hive meta store, retrying. address: {}, username: {}", hiveMetaAddress, username);
            throw new HiveMetaStoreConnectException("Failed to connect to hive meta store, address: " + hiveMetaAddress);
        }

        LOGGER.info("Succeed to connect to hive meta store. address: {}, username: {}", hiveMetaAddress, username);
        return tmpClient;
    }

    public void closeClient(HiveMetaStoreClient client) {
        if (client != null) {
            client.close();
        }
    }

}
