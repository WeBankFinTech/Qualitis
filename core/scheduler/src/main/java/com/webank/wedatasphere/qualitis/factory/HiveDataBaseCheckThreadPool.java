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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * @author howeye
 */
@Component
public class HiveDataBaseCheckThreadPool {

    @Autowired
    private TaskExecuteLimitConfig taskExecuteLimitConfig;

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("Hive DataBase Checker Limiter Thread").build();
        executor = new ThreadPoolExecutor(taskExecuteLimitConfig.getTaskExecuteLimitThread(), taskExecuteLimitConfig.getTaskExecuteLimitThread(), 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
    }

    public Future<Boolean> submitCallable(CheckHiveDataBaseExistCallable checkHiveDataBaseExistCallable) {
        return executor.submit(checkHiveDataBaseExistCallable);
    }

    public Boolean getResult(Future<Boolean> future) throws ExecutionException, InterruptedException {
        return future.get();
    }



}
