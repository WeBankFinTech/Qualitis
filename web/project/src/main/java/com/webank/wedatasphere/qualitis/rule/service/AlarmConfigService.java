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

import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;

import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import java.util.List;

/**
 * @author howeye
 */
public interface AlarmConfigService {

    /**
     * Check and save alarm config
     * @param requests
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    List<AlarmConfig> checkAndSaveAlarmVariable(List<AlarmConfigRequest> requests, Rule rule) throws UnExpectedRequestException;

    /**
     * Delete alarm config by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * Check and save custom alarm config
     * @param requests
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    List<AlarmConfig> checkAndSaveCustomAlarmVariable(List<CustomAlarmConfigRequest> requests, Rule rule) throws UnExpectedRequestException;

    /**Check and save file alarm config
     *
     * @param alarmVariable
     * @param savedRule
     * @return
     */
    List<AlarmConfig> checkAndSaveFileAlarmVariable(List<FileAlarmConfigRequest> alarmVariable, Rule savedRule);
}
