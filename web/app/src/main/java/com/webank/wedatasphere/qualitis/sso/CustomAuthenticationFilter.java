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

import com.alibaba.fastjson.JSON;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.authentication.*;
import org.jasig.cas.client.util.*;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author howeye
 */
public class CustomAuthenticationFilter extends AbstractCasFilter {

    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;

    /**
     * 是否使用sso 0 关闭 1 正常
     */
    private String filter = "1";

    /**
     * 注销地址
     */
    private String logoutUrl = "";

    /**
     * 接入系统的访问地址,用来注销
     */
    private String logoutService = "";

    private String systemId = "";

    private static final String SSO_OFF = "0";
    private static final String SSO_ON = "1";

    private static final String XML_FILE_HEADER = "XMLHttpRequest";

    private static final Integer ARRAY_LENGTH = 2;

    private static final String SERVICE_INDEX_FLAG = "service=";

    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();

    private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;

    private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES = new HashMap<String, Class<? extends UrlPatternMatcherStrategy>>();

    static {
        PATTERN_MATCHER_TYPES.put("CONTAINS",
                ContainsPatternUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES
                .put("REGEX", RegexUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES
                .put("EXACT", ExactUrlPatternMatcherStrategy.class);
    }

    @Override
    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            String casServerLoginUrl = getPropertyFromInitParams(filterConfig,"casServerLoginUrl", null);
            filter = getPropertyFromInitParams(filterConfig, "filter", "1");
            logoutService = getPropertyFromInitParams(filterConfig,"logoutService", null);
            systemId = getPropertyFromInitParams(filterConfig,"systemId", null);
            logoutUrl = getPropertyFromInitParams(filterConfig,"logoutUrl", null);
            String propertieFilePath = getPropertyFromInitParams(filterConfig,"propertieFilePath", null);
            String serverName = getPropertyFromInitParams(filterConfig,"serverName", "");
            // 配置文件优先级 web.xml>propertieFilePath>sso.client.properties

            if (StringUtils.isEmpty(casServerLoginUrl)) {
                if (StringUtils.isNotEmpty(propertieFilePath)) {
                    casServerLoginUrl = PropertyUtil.getValue(propertieFilePath, "sso.client.casServerLoginUrl",null);
                    filter = PropertyUtil.getValue(propertieFilePath,"sso.client.filter", "1");
                    logoutService = PropertyUtil.getValue(propertieFilePath,"sso.client.logoutService", null);
                    systemId = PropertyUtil.getValue(propertieFilePath,"sso.client.systemId", null);
                    serverName = PropertyUtil.getValue(propertieFilePath,"sso.client.serverName", "");
                    logoutUrl = PropertyUtil.getValue(propertieFilePath,"sso.client.logoutUrl", "");
                } else {
                    casServerLoginUrl = ConfigUtil.getStringByKey("sso.client.casServerLoginUrl");
                    filter = ConfigUtil.getStringByKey("sso.client.filter");
                    logoutService = ConfigUtil.getStringByKey("sso.client.logoutService");
                    systemId = ConfigUtil.getStringByKey("sso.client.systemId");
                    serverName = ConfigUtil.getStringByKey("sso.client.serverName");
                    logoutUrl = ConfigUtil.getStringByKey("sso.client.logoutUrl");
                }
            }

            //读取启动脚本的logoutService参数   -DlogoutService
            String tmpLogoutService = System.getProperty("logoutService");
            if(StringUtils.isNotEmpty(tmpLogoutService)){
                logoutService=tmpLogoutService;
            }
            //读取启动脚本的systemId参数   -DsystemId
            String tmpSystemId = System.getProperty("systemId");
            if(StringUtils.isNotEmpty(tmpSystemId)){
                systemId=tmpSystemId;
            }
            //读取启动脚本的serverName参数   -DserverName
            String tmpServerName = System.getProperty("serverName");
            if(StringUtils.isNotEmpty(tmpServerName)){
                serverName=tmpServerName;
            }
            //去除空格
            if (StringUtils.isNotEmpty(casServerLoginUrl)) {
                casServerLoginUrl=casServerLoginUrl.trim();
            }
            if (StringUtils.isNotEmpty(filter)) {
                filter=filter.trim();
            }
            if (StringUtils.isNotEmpty(logoutService)) {
                logoutService=logoutService.trim();
            }
            if (StringUtils.isNotEmpty(systemId)) {
                systemId=systemId.trim();
            }
            if (StringUtils.isNotEmpty(serverName)) {
                serverName=serverName.trim();
            }

            setServerName(serverName);
            if (StringUtils.isNotEmpty(casServerLoginUrl)) {
                setCasServerLoginUrl(casServerLoginUrl);
            } else {
                throw new IllegalArgumentException("casServerLoginUrl  cannot be null");
            }

            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig,
                    "renew", "false")));
            logger.trace("Loaded renew parameter: {}", this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig,
                    "gateway", "false")));
            logger.trace("Loaded gateway parameter: {}", this.gateway);
            final String ignorePattern = getPropertyFromInitParams(filterConfig, "ignorePattern", null);
            logger.trace("Loaded ignorePattern parameter: {}", ignorePattern);
            final String ignoreUrlPatternType = getPropertyFromInitParams(filterConfig, "ignoreUrlPatternType", "REGEX");
            logger.trace("Loaded ignoreUrlPatternType parameter: {}",ignoreUrlPatternType);

            handleIgnorePatternAndSome(filterConfig, ignorePattern, ignoreUrlPatternType);
        }
    }

    private void handleIgnorePatternAndSome(FilterConfig filterConfig, String ignorePattern, String ignoreUrlPatternType) {
        if (ignorePattern != null) {
            final Class<? extends UrlPatternMatcherStrategy> ignoreUrlMatcherClass = PATTERN_MATCHER_TYPES
                    .get(ignoreUrlPatternType);
            if (ignoreUrlMatcherClass != null) {
                this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils
                        .newInstance(ignoreUrlMatcherClass.getName());
            } else {
                try {
                    logger.trace(
                            "Assuming {} is a qualified class name...",
                            ignoreUrlPatternType);
                    this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils
                            .newInstance(ignoreUrlPatternType);
                } catch (final IllegalArgumentException e) {
                    logger.error("Could not instantiate class [{}]",
                            ignoreUrlPatternType, e);
                }
            }
            if (this.ignoreUrlPatternMatcherStrategyClass != null) {
                this.ignoreUrlPatternMatcherStrategyClass
                        .setPattern(ignorePattern);
            }
        }

        final String gatewayStorageClass = getPropertyFromInitParams(
                filterConfig, "gatewayStorageClass", null);

        if (gatewayStorageClass != null) {
            this.gatewayStorage = ReflectUtils
                    .newInstance(gatewayStorageClass);
        }

        final String authenticationRedirectStrategyClass = getPropertyFromInitParams(
                filterConfig, "authenticationRedirectStrategyClass", null);

        if (authenticationRedirectStrategyClass != null) {
            this.authenticationRedirectStrategy = ReflectUtils
                    .newInstance(authenticationRedirectStrategyClass);
        }
    }

    @Override
    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl,
                "casServerLoginUrl cannot be null.");
    }

    @Override
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = HttpUtils.getUserName(httpServletRequest);
        if (username != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        logger.debug("come to second filter--AuthenticationFilter");
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 用户自定义使用sso登陆  0 关闭 1 正常
        if (StringUtils.isNotEmpty(filter) && SSO_OFF.equals(filter)) {
            logger.debug("come to AuthenticationFilter chain--sso is closed!");
            filterChain.doFilter(request, response);
            return;
        }

        if (isRequestUrlExcluded(request)) {
            logger.debug("come to AuthenticationFilter chain--Request is ignored!");
            filterChain.doFilter(request, response);
            return;
        }

        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;
        if (assertion != null) {
            logger.debug("come to AuthenticationFilter chain--assertion is not null!");
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        logger.debug("serviceUrl is {}",serviceUrl);
        final String ticket = retrieveTicketFromRequest(request);

        final boolean wasGatewayed = this.gateway&& this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);
        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            logger.debug("come to AuthenticationFilter chain--gateway is not null!");
            filterChain.doFilter(request, response);
            return;
        }
        String urlToRedirectTo = handleModifiedServiceUrl(request, response, serviceUrl);

        //解决session过期 ajax跨域问题
        String ajax=request.getHeader("X-Requested-With");
        if(StringUtils.isNotEmpty(ajax) && XML_FILE_HEADER.equals(ajax)&&StringUtils.isNotEmpty(logoutUrl)){
            Map<String, String> map=new HashMap<String, String>(8);
            map.put("status_code", "302");
            if (logoutUrl.indexOf(SERVICE_INDEX_FLAG) != -1) {
                //重定向到登陆页面
                String[] arr=logoutUrl.split("service=");
                if (arr!=null && ARRAY_LENGTH == arr.length) {
                    map.put("redirect_url", arr[1]);
                }
            }
            logger.debug("ajax domain,response map is {}",JSON.toJSONString(map));
            response.getWriter().write(JSON.toJSONString(map));
            response.getWriter().flush();
        }else{
            logger.debug("no domain, redirection!");
            this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
        }
    }

    private String handleModifiedServiceUrl(HttpServletRequest request, HttpServletResponse response, String serviceUrl) {
        String modifiedServiceUrl;
        logger.debug("no ticket and no assertion found,serviceUrl is {}",serviceUrl);
        if (this.gateway) {
            logger.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }
        logger.debug("modifiedServiceUrl: {}", modifiedServiceUrl);

        String urlToRedirectTo = CommonUtils.constructRedirectUrl(
                this.casServerLoginUrl, getServiceParameterName(),
                modifiedServiceUrl, this.renew, this.gateway);

        logger.debug("construct url is:",urlToRedirectTo);

        if(StringUtils.isNotEmpty(logoutService)&&!logoutService.equals(getServerName())){
            urlToRedirectTo += "&logoutService="+ CommonUtils.urlEncode(constructLogoutServiceUrl(request, response, logoutService));
        }

        if(StringUtils.isNotEmpty(systemId)){
            urlToRedirectTo += "&systemId="+ systemId;
        }

        logger.debug("urlToRedirectTo with logoutService \"{}\"", urlToRedirectTo);

        if ((StringUtils.isNotEmpty(ConfigUtil
                .getStringByKey("sso.client.https")))
                && ("1".equals(ConfigUtil.getStringByKey("sso.client.https")))) {
            urlToRedirectTo = urlToRedirectTo.replace("http", "https");
        }
        return urlToRedirectTo;
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }

    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        if (this.ignoreUrlPatternMatcherStrategyClass == null) {
            return false;
        }
        final StringBuilder urlBuffer = new StringBuilder(request.getRequestURL().toString());
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);

    }

}
