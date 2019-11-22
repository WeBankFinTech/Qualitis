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

package com.webank.wedatasphere.qualitis.filter;

import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author howeye
 */
@Provider
public class InternationalResponseFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternationalResponseFilter.class);

    @Autowired
    private LocaleParser localeParser;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object responseEntity = responseContext.getEntity();
        if (responseEntity instanceof GeneralResponse) {
            GeneralResponse generalResponse = (GeneralResponse) responseContext.getEntity();
            String message = generalResponse.getMessage();

            String localeStr = requestContext.getHeaderString("Content-Language");
            message = localeParser.replacePlaceHolderByLocale(message, localeStr);

            generalResponse.setMessage(message);
            responseContext.setEntity(generalResponse);
        }
    }
}
