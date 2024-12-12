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

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.publish.QualitisDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.QueryJumpUrlWithDSSJobContentRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/21 14:40
 */
public class QualitisRefQueryOperation extends QualitisDevelopmentOperation<ThirdlyRequestRef.QueryJumpUrlWithDSSJobContentRequestRefImpl, QueryJumpUrlResponseRef>
        implements RefQueryJumpUrlOperation<ThirdlyRequestRef.QueryJumpUrlWithDSSJobContentRequestRefImpl, QueryJumpUrlResponseRef> {

    private static Logger LOGGER = LoggerFactory.getLogger(QualitisRefQueryOperation.class);

    private static final String BASH = "bash";
    private static final String CHECK_ALERT = "checkalert";
    private static final String TABLE_RULES = "TableRules";

    private static final Gson gson = new Gson();

    public String getEnvUrl(String url, String host, int port, QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef, String workflowName, String workflowVersion) throws UnsupportedEncodingException {
        String redirectUrl = "http://" + host + ":" + port + "/#/projects/rules?workflowProject=true&tpl=newSingleTableRule&projectId=" + qualitisOpenRequestRef.getRefProjectId() + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}&dssurl=${dssurl}&workflowName=" + workflowName + "&workflowVersion=" + workflowVersion;

        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + getEnv(qualitisOpenRequestRef).toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    public String getEnvUrlForBash(String url, String host, int port, QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef, String workflowName, String workflowVersion) throws UnsupportedEncodingException {
        String redirectUrl = "http://" + host + ":" + port + "/#/scripts?workflowProject=true&projectId=" + qualitisOpenRequestRef.getRefProjectId() + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}&dssurl=${dssurl}&workflowName=" + workflowName + "&workflowVersion=" + workflowVersion;

        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + getEnv(qualitisOpenRequestRef).toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    public String getEnvUrlForCheckAlert(String url, String host, int port, QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef, String workflowName, String workflowVersion) throws UnsupportedEncodingException {
        String redirectUrl = "http://" + host + ":" + port + "/#/checkAlert?workflowProject=true&projectId=" + qualitisOpenRequestRef.getRefProjectId() + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}&dssurl=${dssurl}&workflowName=" + workflowName + "&workflowVersion=" + workflowVersion;

        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + getEnv(qualitisOpenRequestRef).toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    public String getEnvUrlForTableCheckRules(String url, String host, int port, QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef, String workflowName, String workflowVersion) throws UnsupportedEncodingException {
        String redirectUrl = "http://" + host + ":" + port + "/#/projects/tableGroupRules?workflowProject=true&projectId=" + qualitisOpenRequestRef.getRefProjectId() + "&ruleGroupId=${ruleGroupId}&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}&dssurl=${dssurl}&workflowName=" + workflowName + "&workflowVersion=" + workflowVersion;

        return url + "?redirect=" + URLEncoder.encode(redirectUrl + "&env=" + getEnv(qualitisOpenRequestRef).toLowerCase(), "UTF-8") + "&dssurl=${dssurl}&cookies=${cookies}";
    }

    public String getEnv(QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef) {
        return qualitisOpenRequestRef.getDSSLabels().stream().filter(dssLabel -> dssLabel instanceof EnvDSSLabel).map(dssLabel -> (EnvDSSLabel) dssLabel).findAny().get().getEnv();
    }

    @Override
    public QueryJumpUrlResponseRef query(QueryJumpUrlWithDSSJobContentRequestRefImpl qualitisOpenRequestRef) throws ExternalOperationFailedException {
        try {
            String baseUrl = getAppInstance().getBaseUrl() + "qualitis/api/v1/redirect";
            LOGGER.info("Qualitis query request: " + gson.toJson(qualitisOpenRequestRef));
            LOGGER.info("Get base url from app instance and return redirect url: " + baseUrl);
            URL url = new URL(baseUrl);

            String host = url.getHost();
            int port = url.getPort();
            if (port < 0) {
                port = 80;
            }

            String workflowName = (String) qualitisOpenRequestRef.getDSSJobContent().get(DSSJobContentConstant.ORCHESTRATION_NAME);
            String workflowVersion = (String) qualitisOpenRequestRef.getDSSJobContent().get(DSSJobContentConstant.ORC_VERSION_KEY);

            String retJumpUrl = getEnvUrl(baseUrl, host, port, qualitisOpenRequestRef, workflowName, workflowVersion);
            if (qualitisOpenRequestRef.getType().contains(BASH)) {
                retJumpUrl = getEnvUrlForBash(baseUrl, host, port, qualitisOpenRequestRef, workflowName, workflowVersion);
            } else if (qualitisOpenRequestRef.getType().contains(CHECK_ALERT)) {
                retJumpUrl = getEnvUrlForCheckAlert(baseUrl, host, port, qualitisOpenRequestRef, workflowName, workflowVersion);
            } else if(qualitisOpenRequestRef.getType().contains(TABLE_RULES)){
                retJumpUrl = getEnvUrlForTableCheckRules(baseUrl, host, port, qualitisOpenRequestRef, workflowName, workflowVersion);
            }
            return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(retJumpUrl).success();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }
}
