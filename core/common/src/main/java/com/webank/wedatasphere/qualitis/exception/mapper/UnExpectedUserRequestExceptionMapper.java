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

package com.webank.wedatasphere.qualitis.exception.mapper;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author howeye
 */
@Provider
public class UnExpectedUserRequestExceptionMapper implements ExceptionMapper<UnExpectedRequestException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnExpectedUserRequestExceptionMapper.class);

    @Autowired
    private LocaleParser localeParser;

    @Override
    public Response toResponse(UnExpectedRequestException exception) {
        String message = localeParser.replacePlaceHolderByLocale(exception.getMessage(), "en_US");
        LOGGER.warn(message, exception);
        return Response.ok(exception.getResponse()).status(200).build();
    }
}
