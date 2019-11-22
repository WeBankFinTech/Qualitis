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

package com.webank.wedatasphere.qualitis.factory;

import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.hive.DatabaseChecker;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author howeye
 */
public class CheckHiveDataBaseExistCallable implements Callable<Boolean> {

    private DatabaseChecker databaseChecker;
    private String database;
    private String metaStoreAddress;
    private String hiveServer2Address;
    private String hiveUsername;
    private String hivePassword;
    private String currentUser;

    public CheckHiveDataBaseExistCallable(DatabaseChecker databaseChecker, String database, String metaStoreAddress,
                                          String hiveServer2Address, String hiveUsername, String hivePassword, String currentUser) {
        this.databaseChecker = databaseChecker;
        this.database = database;
        this.metaStoreAddress = metaStoreAddress;
        this.hiveServer2Address = hiveServer2Address;
        this.hiveUsername = hiveUsername;
        this.hivePassword = hivePassword;
        this.currentUser = currentUser;
    }

    @Override
    public Boolean call() throws Exception {
        return databaseChecker.createDataBaseIfNotExist(database, metaStoreAddress, hiveServer2Address, hiveUsername, hivePassword, currentUser);
    }
}
