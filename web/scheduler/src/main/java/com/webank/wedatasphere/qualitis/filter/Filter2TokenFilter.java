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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.dao.AuthListDao;
import com.webank.wedatasphere.qualitis.encoder.Sha256Encoder;
import com.webank.wedatasphere.qualitis.entity.AuthList;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author howeye
 */
public class Filter2TokenFilter implements Filter {

    @Autowired
    private AuthListDao authListDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(Filter2TokenFilter.class);

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
            // Find appToken by appId
            AuthList authList = authListDao.findByAppId(appId);
            if (authList != null && validateSignature(nonce, timestamp, authList.getAppToken(),
                appId, signature)) {
                LOGGER.info(
                    "Request accepted, appId='{}', nonce='{}', timestamp='{}', signature='{}'",
                    appId, nonce, timestamp, signature);
                filterChain.doFilter(request, response);
                return;
            }
        }

        LOGGER.info("Request forbidden, appId='{}', nonce='{}', timestamp='{}', signature='{}'",
            appId, nonce, timestamp, signature);
        writeToResponse("Forbidden! please check appid and token", response);
    }

    private void writeToResponse(String message, ServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        GeneralResponse generalResponse = new GeneralResponse<>("401", message, null);
        out.write(objectMapper.writeValueAsBytes(generalResponse));
        out.flush();
    }

    public boolean validateSignature(String nonce, String timestamp, String appToken,
        String appId, String signature) {
        return getSignature(nonce, timestamp, appToken, appId).equals(signature);
    }

    public String getSignature(String nonce, String timestamp, String appToken, String appId) {
        return Sha256Encoder
            .encode(Sha256Encoder.encode(appId + nonce + timestamp) + appToken);
    }


    @Override
    public void destroy() {
        // Destroy operation
    }

    public static void main(String[] args) {
        Filter2TokenFilter filter2TokenFilter = new Filter2TokenFilter();

        String nonce = "16895";
        String timeStamp = String.valueOf(System.currentTimeMillis());
        System.out.println(timeStamp);
        MessageDigest hash;

        StringBuffer resultInner = new StringBuffer();
        StringBuffer resultOuter = new StringBuffer();

        String plain = "linkis_id" + nonce + timeStamp;

        try {
            hash = MessageDigest.getInstance("SHA-256");
            hash.update(plain.getBytes("UTF-8"));
            resultInner.append(new BigInteger(1, hash.digest()).toString(16));
            String inner = StringUtils.leftPad(resultInner.toString(), 32, '0');

            hash.reset();
            hash.update(inner.concat("a33693de51").getBytes("UTF-8"));
            resultOuter.append(new BigInteger(1, hash.digest()).toString(16));
            String outer = StringUtils.leftPad(resultOuter.toString(), 32, '0');

            System.out.println(
                filter2TokenFilter.getSignature(nonce, timeStamp, "a33693de51", "linkis_id"));
            System.out.println(outer);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Signature exception.", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Upsupported encoding exception.", e);
        }
    }

}
