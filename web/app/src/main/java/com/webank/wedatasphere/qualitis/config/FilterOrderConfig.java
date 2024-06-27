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

package com.webank.wedatasphere.qualitis.config;

import cn.webank.bdp.microfrontend.sso.*;
import com.webank.wedatasphere.dss.standard.app.sso.origin.filter.spring.SpringOriginSSOPluginFilter;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.SSOPluginFilter;
import com.webank.wedatasphere.qualitis.filter.*;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author howeye
 */
@Configuration
public class FilterOrderConfig {

    @Value("${facade.gov-core.ips}")
    private String facadeGovCoreIPs;

    @Value("${dss.origin-urls}")
    private String dssOriginUrls;

    @Bean
    public Filter filter1AuthorizationFilter() {
        return new Filter1AuthorizationFilter();
    }

    @Bean
    public Filter filter2TokenFilter() {
        return new Filter2TokenFilter();
    }

    @Bean
    public Filter unFilterUrlFilter() {
        return new UnFilterUrlFilter();
    }

    @Bean
    public Filter jobRoleFilter() {
        return new JobRoleFilter();
    }

    @ConditionalOnProperty(name = "local.startup", havingValue = "true")
    @Bean
    public Filter localAuthorizationFilter() {
        return new LocalAuthorizationFilter();
    }

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> ssoListener() {
        return new ServletListenerRegistrationBean<>(new SingleSignOutHttpSessionListener());
    }

    /**
     * Local environment filters starts
     * @return
     */
    @ConditionalOnProperty(name = "local.startup", havingValue = "true")
    @Bean
    public FilterRegistrationBean localFilterBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(localAuthorizationFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.setOrder(0);
        return registration;
    }

    /**
     * DSS AppConn filters starts
     * @return
     */
    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean<SSOPluginFilter> dssSinglePointFilter() {
        FilterRegistrationBean<SSOPluginFilter> filter = new FilterRegistrationBean<>();
        filter.setName("dssSinglePointFilter");
        filter.setFilter(new SpringOriginSSOPluginFilter());
        filter.setOrder(-1);
        return filter;
    }
    // *******************  DSS AppConn filters ends  **********************

    /**
     * DQM customized by-pass filters starts
     * @return
     */
    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean unFilterUrlFilterBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(unFilterUrlFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.setOrder(0);
        return registration;
    }
    // *******************  DQM customized by-pass filters ends  **********************

    /**
     * Security/Micro-Frontend SSO filters starts
     * @return
     */
    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean casSingleSignOutFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomSingleSignOutFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        registration.setName("CAS Single Sign Out Filter");
        registration.setOrder(1);
        return registration;
    }

    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean casFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        customAuthenticationFilter.setCompatiblePenetrationUrlList(dssOriginUrls);
        customAuthenticationFilter.setGovCoreIPList(facadeGovCoreIPs);
        registration.setFilter(customAuthenticationFilter);
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        registration.setName("CAS Filter");
        registration.setOrder(2);
        return registration;
    }

    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean casValidationFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomCas20ProxyReceivingTicketValidationFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        registration.setName("CAS Validation Filter");
        registration.setOrder(3);
        return registration;
    }

    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomHttpServletRequestWrapperFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        registration.setName("CAS HttpServletRequest Wrapper Filter");
        registration.setOrder(4);
        return registration;
    }

    @ConditionalOnMissingBean(name = "localAuthorizationFilter")
    @Bean
    public FilterRegistrationBean casAssertionThreadLocalFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomAssertionThreadLocalFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        registration.setName("CAS Assertion Thread Local Filter");
        registration.setOrder(5);
        return registration;
    }
    // *******************  Security/Micro-Frontend SSO filters ends  **********************


    /**
     * DQM customized logic filters starts
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean1() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter1AuthorizationFilter());
        filterRegistrationBean.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        filterRegistrationBean.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/auth/common/*");
        filterRegistrationBean.setOrder(6);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean2() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter2TokenFilter());
        filterRegistrationBean.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/outer/*");
        filterRegistrationBean.setOrder(7);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean jobRoleFilterBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(jobRoleFilter());
        registration.addUrlPatterns(JerseyConfig.APPLICATION_PATH + "/api/v1/*");
        registration.setOrder(8);
        return registration;
    }
    // *******************  DQM customized logic filters ends  **********************

}
