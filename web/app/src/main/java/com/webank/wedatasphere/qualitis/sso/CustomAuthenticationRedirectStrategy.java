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

package com.webank.wedatasphere.qualitis.sso;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.config.AuthFilterUrlConfig;
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RedirectResponse;
import com.webank.wedatasphere.qualitis.util.SpringContextUtil;
import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.util.ResourceLoader;
import org.springframework.http.HttpMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class CustomAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<String> permitUrlList = null;

    private void init() {
        AuthFilterUrlConfig authFilterUrlConfig = SpringContextUtil.getApplicationContext().getBean(AuthFilterUrlConfig.class);
        permitUrlList = authFilterUrlConfig.getUnFilterUrls();
        if (permitUrlList == null) {
            permitUrlList = new ArrayList<>();
        }
    }

    @Override
    public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl) throws IOException {
        String requestUrl = request.getRequestURI();
        init();
        if (!request.getMethod().equals(HttpMethod.OPTIONS) && !permitUrlList.contains(requestUrl)) {
            response.setContentType("application/json");
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());

            ServletOutputStream out = response.getOutputStream();
            FrontEndConfig frontEndConfig = (FrontEndConfig) SpringContextUtil.getApplicationContext().getBean("frontEndConfig");
            String logoutUrl = ResourceLoader.getInstance().getPropFromFS("sso.client.properties").getProperty("sso.client.logoutUrl")
                    + "?service=" + frontEndConfig.getHomePage().replace("{IP}", QualitisConstants.QUALITIS_SERVER_HOST);
            String domainName = frontEndConfig.getDomainName().replace("{IP}", QualitisConstants.QUALITIS_SERVER_HOST);
            URL url = new URL(potentialRedirectUrl);
            String query = url.getQuery();
            String newQuery = replaceQuery(query, domainName);
            String redirectUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath() + "?" + newQuery;
            RedirectResponse redirectResponse = new RedirectResponse(redirectUrl, logoutUrl);
            GeneralResponse generalResponse = new GeneralResponse<>("401", "please login", redirectResponse);
            out.write(objectMapper.writeValueAsBytes(generalResponse));
            out.flush();
        }
    }

    private String replaceQuery(String oldQuery, String domainName) throws UnsupportedEncodingException, MalformedURLException {
        String decodeQuery = URLDecoder.decode(oldQuery, "UTF-8");
        URL url = new URL(decodeQuery.substring(decodeQuery.indexOf('=') + 1));
        String str = domainName + url.getPath();
        return decodeQuery.substring(0, decodeQuery.indexOf('=') + 1) + URLEncoder.encode(str, "UTF-8");
    }

}
