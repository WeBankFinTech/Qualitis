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
import com.webank.wedatasphere.qualitis.encoder.Md5Encoder;
import com.webank.wedatasphere.qualitis.entity.AuthList;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String appId = request.getParameter("app_id");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");

        Long currentTimeStamp = System.currentTimeMillis();
        Long lastTimeStamp = Long.valueOf(timestamp);
        Long interval = currentTimeStamp - lastTimeStamp;
        if (interval >= invalidInterval) {
            ServletOutputStream out = servletResponse.getOutputStream();
            GeneralResponse generalResponse = new GeneralResponse<>("401", "Timestamp is invalid", null);
            out.write(objectMapper.writeValueAsBytes(generalResponse));
            out.flush();
            return;
        }

        if (appId != null) {
            // Find appToken by appId
            AuthList authList = authListDao.findByAppId(appId);
            if (authList != null && validateSignature(nonce, timestamp, authList.getAppToken(), appId, signature)) {
                    LOGGER.info("Request accepted, appId='{}', nonce='{}', timestamp='{}', signature='{}'",
                        appId.replace("\r", "").replace("\n", ""),
                        nonce.replace("\r", "").replace("\n", ""),
                        timestamp.replace("\r", "").replace("\n", ""),
                        signature.replace("\r", "").replace("\n", ""));
                    filterChain.doFilter(request, servletResponse);
                    return;
            }
        }

        LOGGER.info("Request forbidden, appId='{}', nonce='{}', timestamp='{}', signature='{}'", appId.replace("\r", "").replace("\n", ""),
                nonce.replace("\r", "").replace("\n", ""),
            timestamp.replace("\r", "").replace("\n", ""),
            signature.replace("\r", "").replace("\n", ""));
        writeToResponse("Forbidden! please check appid and token", servletResponse);
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
        return Md5Encoder.encode(Md5Encoder.encode(appId + nonce + timestamp, true, true) + appToken, true, true);
    }


    @Override
    public void destroy() {
        // Destroy operation
    }

    public static void main(String[] args) {
        Filter2TokenFilter filter2TokenFilter = new Filter2TokenFilter();
        String timeStamp = System.currentTimeMillis() + "";
        System.out.println(timeStamp);
        System.out.println(filter2TokenFilter.getSignature("36975", timeStamp, "a33693de51", "linkis_id"));
    }
}
