/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.ref.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisOpenResponseRef;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisOpenRequestRef;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefQueryOperation implements RefQueryOperation<OpenRequestRef> {

    DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        QualitisOpenRequestRef qualitisOpenRequestRef = (QualitisOpenRequestRef) ref;
        try {
            String baseUrl = qualitisOpenRequestRef.getParameter("redirectUrl").toString();
            URL url = new URL(baseUrl);
            String host = url.getHost();
            int port = url.getPort();
            String retJumpUrl = getEnvUrl(baseUrl, host, port, qualitisOpenRequestRef);
            Map<String,String> retMap = new HashMap<>();
            retMap.put("jumpUrl",retJumpUrl);
            return new QualitisOpenResponseRef(DSSCommonUtils.COMMON_GSON.toJson(retMap), 0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }

    public String getEnvUrl(String url, String host, int port, QualitisOpenRequestRef qualitisOpenRequestRef) throws UnsupportedEncodingException {
        String env = ((Map<String, Object>) qualitisOpenRequestRef.getParameter("params")).get("labels").toString();
        Long projectId = (Long) qualitisOpenRequestRef.getParameter("projectId");
        String redirectUrl = "http://" + host + ":" + port + "/#/addGroupTechniqueRule?tableType=1&id=" + projectId + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}";
        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + env.toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
