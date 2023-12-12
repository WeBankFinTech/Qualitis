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

package com.webank.wedatasphere.qualitis.timer;
import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
public class PendingJobCheckerRunnable implements Runnable {
    private String ip;
    private ApplicationDao applicationDao;
    private OuterExecutionService outerExecutionService;

    private static final Logger LOGGER = LoggerFactory.getLogger("pending job check");

    private static final List<Integer> PENDING_APPLICATION_STATUS_LIST = Arrays.asList(ApplicationStatusEnum.SUBMIT_PENDING.getCode());
    public PendingJobCheckerRunnable(ApplicationDao applicationDao, OuterExecutionService outerExecutionService, String ip) {
        this.ip = ip;
        this.applicationDao = applicationDao;
        this.outerExecutionService = outerExecutionService;
    }

    private static final Gson GSON = new Gson();

    @Override
    public void run() {
        try {
            LOGGER.info("Start to check pending applications.");
            List<Application> pengindApplications = applicationDao.findByStatusIn(PENDING_APPLICATION_STATUS_LIST);
            pengindApplications = pengindApplications.stream().filter(application -> ip.equals(application.getIp())).collect(Collectors.toList());
            LOGGER.info("Succeed to find applications that are pending. Application: {}", pengindApplications);

            for (Application application : pengindApplications) {
                LOGGER.info("Start to submit application: {{}}.", application.getId());

                if (StringUtils.isNotBlank(application.getNodeName()) && application.getNodeName().startsWith(QualitisConstants.CHECKALERT_NODE_NAME_PREFIX)) {
                    outerExecutionService.reSubmitCheckAlertGroup(application.getExecuteUser(), application.getRuleGroupId(), application.getProjectId(), application.getExecutionParam());
                    return;
                }

                if (StringUtils.isEmpty(application.getRuleIds())) {
                    application.setStatus(ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode());
                    applicationDao.saveApplication(application);
                    continue;
                }

                List<Long> ruleIds = new ArrayList<>(4);
                String ruleIdsStr = application.getRuleIds().replace("[", "").replace("]", "");
                String[] ruleIdStrs = ruleIdsStr.split(SpecCharEnum.COMMA.getValue());
                for (String ruleIdStr : ruleIdStrs) {
                    ruleIds.add(Long.parseLong(ruleIdStr));
                }

                outerExecutionService.submitRulesAndUpdateRule(application.getJobId()
                    , ruleIds
                    , StringUtils.isNotBlank(application.getPartition()) ? new StringBuilder(application.getPartition()) : new StringBuilder("")
                    , application.getCreateUser(), application.getExecuteUser(), application.getNodeName(), application.getProjectId(), application.getRuleGroupId()
                    , application.getFpsFileId(), application.getFpsHashValue(), application.getStartupParam(), application.getClusterName(), application.getSetFlag()
                    , GSON.fromJson(application.getExecutionParamJson(), Map.class), application.getExecutionParam()
                    , StringUtils.isNotBlank(application.getRunDate()) ? new StringBuilder(application.getRunDate()) : new StringBuilder("")
                    , StringUtils.isNotBlank(application.getSplitBy()) ? new StringBuilder(application.getSplitBy()) : new StringBuilder("")
                    , application.getInvokeType(), application, application.getSubSystemId(), null,application.getEngineReuse());

                LOGGER.info("Finish to submit application: {{}}.", application.getId());

                Thread.sleep(5000);
            }

            LOGGER.info("Finish to check pending applications.");
        } catch (Exception e) {
            LOGGER.error("Failed to check pending applications, caused by: {}", e.getMessage(), e);
        }
    }

}
