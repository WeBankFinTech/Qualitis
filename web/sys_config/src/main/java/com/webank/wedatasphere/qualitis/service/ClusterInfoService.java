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

import com.webank.wedatasphere.qualitis.request.AddClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.DeleteClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyClusterInfoRequest;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.request.AddClusterInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyClusterInfoRequest;

/**
 * @author howeye
 */
public interface ClusterInfoService {

    /**
     * Add clusterInfo
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ClusterInfo> addClusterInfo(AddClusterInfoRequest request) throws UnExpectedRequestException;

    /**
     * Delete clusterInfo
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteClusterInfo(DeleteClusterInfoRequest request) throws UnExpectedRequestException;

    /**
     * Modify clusterInfo
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyClusterInfo(ModifyClusterInfoRequest request) throws UnExpectedRequestException;

    /**
     * Paging find all clusterInfos
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ClusterInfo>> findAllClusterInfo(PageRequest request) throws UnExpectedRequestException;

}
