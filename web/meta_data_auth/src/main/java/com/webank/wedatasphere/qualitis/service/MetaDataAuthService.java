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

import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.DeleteMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserOfMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataAuthService {

    /**
     * Add meta auth
     * @param addMetaDataAuthRequest
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> addMetaDataAuth(AddMetaDataAuthRequest addMetaDataAuthRequest) throws UnExpectedRequestException;

    /**
     * Delete meta auth
     * @param deleteMetaDataAuthRequest
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteMetaDataAuth(DeleteMetaDataAuthRequest deleteMetaDataAuthRequest) throws UnExpectedRequestException;

    /**
     * Paging get meta auth
     * @param request
     * @return
     */
    List<AuthMetaData> query(QueryUserOfMetaDataAuthRequest request);

    /**
     * Get number of meta auth
     * @param request
     * @return
     */
    long count(QueryUserOfMetaDataAuthRequest request);
}
