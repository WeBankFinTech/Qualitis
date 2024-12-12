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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

/**
 * @author howeye
 */
public interface TransferUserService {

    /**
     * Administrator simulates other user
     * If transfer successfully, admin will "become" the specific user
     * @param user
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> transferUser(String user) throws UnExpectedRequestException;

    /**
     * Administrator exit the status of simulation
     * @return
     */
    GeneralResponse<Object> exitUser();

}
