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
import com.webank.wedatasphere.qualitis.request.AddServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.DeleteServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.FindServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyServiceInfoRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.ServiceInfoResponse;

/**
 * @author allenzhou
 */
public interface ServiceInfoService {

    /**
     * Add
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ServiceInfoResponse> addServiceInfo(AddServiceInfoRequest request) throws UnExpectedRequestException;

    /**
     * Delete
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteServiceInfo(DeleteServiceInfoRequest request) throws UnExpectedRequestException;

    /**
     * Modify: online, offline
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ServiceInfoResponse> modifyServiceInfo(ModifyServiceInfoRequest request) throws UnExpectedRequestException;

    /**
     * Paging find
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ServiceInfoResponse>> findAllServiceInfo(FindServiceInfoRequest request) throws UnExpectedRequestException;


}
