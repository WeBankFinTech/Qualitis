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

import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.publish.QualitisDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.QueryJumpUrlRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefQueryOperation extends QualitisDevelopmentOperation<ThirdlyRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef>
    implements RefQueryJumpUrlOperation<QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef> {

    private static Logger LOGGER = LoggerFactory.getLogger(QualitisRefQueryOperation.class);

    private static final String BASH = "bash";

    public String getEnvUrl(String url, String host, int port, QueryJumpUrlRequestRefImpl qualitisOpenRequestRef) throws UnsupportedEncodingException {
        String env = qualitisOpenRequestRef.getDSSLabels().stream().filter(dssLabel -> dssLabel instanceof EnvDSSLabel)
            .map(dssLabel -> (EnvDSSLabel) dssLabel).findAny().get().getEnv();
        Long projectId = qualitisOpenRequestRef.getRefProjectId();
        String redirectUrl = "http://" + host + ":" + port + "/#/addGroupTechniqueRule?tableType=1&id=" + projectId + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}";
        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + env.toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    public String getEnvUrlForBash(String url, String host, int port, QueryJumpUrlRequestRefImpl qualitisOpenRequestRef) throws UnsupportedEncodingException {
        String env = qualitisOpenRequestRef.getDSSLabels().stream().filter(dssLabel -> dssLabel instanceof EnvDSSLabel)
            .map(dssLabel -> (EnvDSSLabel) dssLabel).findAny().get().getEnv();
        Long projectId = qualitisOpenRequestRef.getRefProjectId();
        String redirectUrl = "http://" + host + ":" + port + "/#/scripts?workflowProject=true&projectId=" + projectId + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}";
        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + env.toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    @Override
    public QueryJumpUrlResponseRef query(QueryJumpUrlRequestRefImpl qualitisOpenRequestRef) throws ExternalOperationFailedException {
        try {
            String baseUrl = getAppInstance().getBaseUrl() + "qualitis/api/v1/redirect";
            LOGGER.info("Get base url from app instancd and retur redirect url: " + baseUrl);
            URL url = new URL(baseUrl);
            String host = url.getHost();
            int port = url.getPort();
            String retJumpUrl = getEnvUrl(baseUrl, host, port, qualitisOpenRequestRef);
            return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(retJumpUrl).success();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }
}
