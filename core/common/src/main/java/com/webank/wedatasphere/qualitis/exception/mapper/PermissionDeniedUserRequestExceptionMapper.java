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

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author allenzhou
 *
 * 作用是PermissionDeniedUserRequestException被直接抛出到前端时，UnExpectedUserRequestExceptionMapper实现的toResponse方法会将该异常作为参数并信息处理之后再返回
 * （比如国际化替换）。
 */
@Provider
public class PermissionDeniedUserRequestExceptionMapper implements ExceptionMapper<PermissionDeniedRequestException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionDeniedUserRequestExceptionMapper.class);

    @Autowired
    private LocaleParser localeParser;

    @Override
    public Response toResponse(PermissionDeniedRequestException exception) {
        String message = localeParser.replacePlaceHolderByLocale(exception.getMessage(), "en_US");
        LOGGER.warn(message, exception);
        return Response.ok(exception.getResponse()).status(exception.getStatus()).build();
    }
}
