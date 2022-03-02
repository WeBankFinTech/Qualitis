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

package com.webank.wedatasphere.qualitis.handler;

import com.webank.wedatasphere.qualitis.exception.HttpRestTemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author howeye
 */
public class RestErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);

    @Override public void handleError(ClientHttpResponse response)
            throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                LOGGER.error("Client Error! response: status code: {}, status text: {}, header: {}, body: {}, charset: {}",
                        statusCode.value(), response.getStatusText(), response.getHeaders(), new String(getResponseBody(response)), getCharset(response));
                throw new HttpRestTemplateException(String.valueOf(statusCode.value()), statusCode.value() + " " + response.getStatusText());
            case SERVER_ERROR:
                LOGGER.error("Server Error! response: status code: {}, status text: {}, header: {}, body: {}, charset: {}",
                        statusCode.value(), response.getStatusText(), response.getHeaders(), new String(getResponseBody(response)), getCharset(response));
                throw new HttpRestTemplateException(String.valueOf(statusCode.value()), statusCode.value() + " " + response.getStatusText());
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }
}

