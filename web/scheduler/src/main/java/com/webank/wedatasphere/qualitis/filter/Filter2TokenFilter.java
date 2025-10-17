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

import cn.hutool.crypto.digest.DigestUtil;
import cn.webank.bdp.wedatasphere.biz.components.servicis.ServicisApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.config.ItsmConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.repository.AuthListRepository;
import com.webank.wedatasphere.qualitis.encoder.Sha256Encoder;
import com.webank.wedatasphere.qualitis.entity.AuthList;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RetResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author howeye
 */
public class Filter2TokenFilter implements Filter {

    @Autowired
    private ServicisApi servicisApi;
    @Autowired
    private AuthListRepository authListRepository;
    @Value("${itsm.path}")
    private String itsmPath;
    @Value("${metric.white_list.paths:}")
    private List<String> whiteListPaths;
    @Value("${metric.white_list.ips:}")
    private List<String> whiteListIps;
    @Value("${servicis.api_urls.validate_signature.enable:false}")
    private Boolean servicisValidateSignature;
    @Autowired
    private ItsmConfig itsmConfig;
    /**
     * 5min
     */
    private static final long SIGN_VALIDITY_PERIOD = 5 * 60 * 1000L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Filter2TokenFilter.class);

    private AtomicInteger counter = new AtomicInteger(0);

    private ObjectMapper objectMapper = new ObjectMapper();

    private Long invalidInterval;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        invalidInterval = 7 * 24 * 60 * 60 * 1000L;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//      Verify request from ITSM
        if (request.getRequestURI().startsWith(itsmPath)) {
            LOGGER.info("The request come from ITSM. url='{}', remote url='{}'", request.getRequestURL().toString(), request.getRemoteAddr() + ":" + request.getRemotePort());
            RetResponse retResponse = verifyRequestFromITSM(request);
            if (retResponse.getRetCode() != 0) {
                ServletOutputStream out = response.getOutputStream();
                out.write(objectMapper.writeValueAsBytes(retResponse));
                out.flush();
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

//      Verify request access to endpoints in the metricRateLimitPaths
        String clientIp = QualitisConstants.getIp(request);
        String requestUri = request.getRequestURI();
        if (whiteListPaths.contains(requestUri)) {
            LOGGER.info("The ip [{}] trying to access [{}]", clientIp, request.getRequestURI());
            if (!whiteListIps.contains(clientIp)) {
                LOGGER.warn("The ip [{}] not allowed to access [{}]", clientIp, request.getRequestURI());
                ServletOutputStream out = response.getOutputStream();
                GeneralResponse generalResponse = new GeneralResponse<>("403", "IP access is restricted. Please contact the administrator to enable the whitelist.",
                        null);
                out.write(objectMapper.writeValueAsBytes(generalResponse));
                out.flush();
                return;
            }
        }

        String appId = request.getParameter("app_id");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");

        Long currentTimeStamp = System.currentTimeMillis();
        Long lastTimeStamp = Long.valueOf(timestamp);
        Long interval = currentTimeStamp - lastTimeStamp;
        if (interval >= invalidInterval) {
            ServletOutputStream out = response.getOutputStream();
            GeneralResponse generalResponse = new GeneralResponse<>("401", "Timestamp is invalid",
                    null);
            out.write(objectMapper.writeValueAsBytes(generalResponse));
            out.flush();
            return;
        }

        if (appId != null) {
            boolean passed = false;

            if (servicisValidateSignature) {
                try {
                    passed = servicisApi.validateSignature(appId, nonce, timestamp, null, signature);
                } catch (Exception e) {
                    LOGGER.error("Validate signature via Servicis failed with error: ", e);
                    throw new ServletException(e);
                }
            } else {
                // Find appToken by appId
                LOGGER.info("Check signature with owner db");
                AuthList authList = authListRepository.findByAppId(appId);
                if (authList != null && validateSignature(nonce, timestamp, authList.getAppToken(),
                        appId, signature)) {
                    passed = true;
                }
            }

            if (passed) {
                if (request.getRequestURI().contains("create_and_submit")) {
                    counter.incrementAndGet();
                    LOGGER.info("Add----------" + counter.get());
                }
                LOGGER.info(
                        "Request accepted, appId='{}', nonce='{}', timestamp='{}', signature='{}', url='{}', remote url='{}'",
                        appId, nonce, timestamp, signature, request.getRequestURL().toString(), request.getRemoteAddr() + ":" + request.getRemotePort());
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    if (request.getRequestURI().contains("create_and_submit")) {
                        counter.decrementAndGet();
                        LOGGER.info("Decrement----------" + counter.get());
                    }
                }
                return;
            }
        }

        LOGGER.info("Request forbidden, appId='{}', nonce='{}', timestamp='{}', signature='{}'",
                appId, nonce, timestamp, signature);
        writeToResponse("Forbidden! please check appid and token", response);
    }

    public boolean validateSignature(String nonce, String timestamp, String appToken,
                                     String appId, String signature) {
        return getSignature(nonce, timestamp, appToken, appId).equals(signature);
    }

    public String getSignature(String nonce, String timestamp, String appToken, String appId) {
        return Sha256Encoder
                .encode(Sha256Encoder.encode(appId + nonce + timestamp) + appToken);
    }

    private RetResponse verifyRequestFromITSM(HttpServletRequest httpServletRequest)  {
        try {
            String requestSign = httpServletRequest.getHeader("sign");
            String requestTimestamp = httpServletRequest.getHeader("timeStamp");
            if (StringUtils.isBlank(requestSign)) {
                throw new IllegalAccessException("The sign must be not null.");
            }
            if (StringUtils.isBlank(requestTimestamp)) {
                throw new IllegalAccessException("The timeStamp must be not null.");
            }
            long timeStamp = System.currentTimeMillis();
            if ((timeStamp - Long.valueOf(requestTimestamp)) > SIGN_VALIDITY_PERIOD) {
                throw new IllegalAccessException("The request has expired.");
            }
            String sign = DigestUtil.sha256Hex(itsmConfig.getSecretKey() + requestTimestamp);
            if (!sign.equals(requestSign)) {
                throw new IllegalAccessException("Forbidden!please check your sign.");
            }
        } catch (IllegalAccessException e) {
            return new RetResponse(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new RetResponse(HttpServletResponse.SC_UNAUTHORIZED, "Forbidden!please check your sign and timestamp.", null);
        }
        return new RetResponse(null);
    }

    private void writeToResponse(String message, ServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        GeneralResponse generalResponse = new GeneralResponse<>("401", message, null);
        out.write(objectMapper.writeValueAsBytes(generalResponse));
        out.flush();
    }

    @Override
    public void destroy() {
        // Destroy operation
    }

}
