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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Date;

/**
 * @author howeye
 */
@Service
public class ExecutionServiceImpl implements ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionServiceImpl.class);

    @Autowired
    private OuterExecutionServiceImpl outerExecutionService;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private LocaleParser localeParser;

    @Context
    private HttpServletRequest httpRequest;

    public ExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request) throws UnExpectedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        String username = HttpUtils.getUserName(httpRequest);
        request.setCreateUser(username);

        Date date = new Date();
        Application newApplication = outerExecutionService.generateApplicationInfo(request.getCreateUser(), request.getExecutionUser(), date);
        try {
            return outerExecutionService.projectExecution(request, newApplication, date);
        } catch (UnExpectedRequestException e) {
            newApplication.setStatus(ApplicationStatusEnum.ARGUMENT_NOT_CORRECT.getCode());
            String exceptionMessage = localeParser.replacePlaceHolderByLocale(ExceptionUtils.getStackTrace(e), httpRequest.getHeader("Content-Language"));
            newApplication.setExceptionMessage(exceptionMessage);
            newApplication.setFinishTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
            applicationDao.saveApplication(newApplication);
            LOGGER.info("Succeed to set application status to [{}], application_id: {}", newApplication.getStatus(), newApplication.getId());
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @Override
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request) throws UnExpectedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        String username = HttpUtils.getUserName(httpRequest);
        request.setCreateUser(username);

        Date date = new Date();
        Application newApplication = outerExecutionService.generateApplicationInfo(request.getCreateUser(), request.getExecutionUser(), date);
        try {
            return outerExecutionService.ruleListExecution(request, newApplication, date);
        } catch (UnExpectedRequestException e) {
            newApplication.setStatus(ApplicationStatusEnum.ARGUMENT_NOT_CORRECT.getCode());
            String exceptionMessage = localeParser.replacePlaceHolderByLocale(ExceptionUtils.getStackTrace(e), httpRequest.getHeader("Content-Language"));
            newApplication.setExceptionMessage(exceptionMessage);
            newApplication.setFinishTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
            applicationDao.saveApplication(newApplication);
            LOGGER.info("Succeed to set application status to [{}], application_id: {}", newApplication.getStatus(), newApplication.getId());
            throw new UnExpectedRequestException(e.getMessage());
        }
    }
}
