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

/**
 * @author v_minminghe
 */
public class LinkisErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisErrorHandler.class);

    @Override public void handleError(ClientHttpResponse response)
            throws IOException {
        String body;
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                body = new String(getResponseBody(response));
                LOGGER.error("Client Error! response: status code: {}, status text: {}, header: {}, body: {}, charset: {}",
                        statusCode.value(), response.getStatusText(), response.getHeaders(), body, getCharset(response));
                throw new HttpRestTemplateException(String.valueOf(statusCode.value()), body);
            case SERVER_ERROR:
                body = new String(getResponseBody(response));
                LOGGER.error("Server Error! response: status code: {}, status text: {}, header: {}, body: {}, charset: {}",
                        statusCode.value(), response.getStatusText(), response.getHeaders(), body, getCharset(response));
                throw new HttpRestTemplateException(String.valueOf(statusCode.value()), body);
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }
}

