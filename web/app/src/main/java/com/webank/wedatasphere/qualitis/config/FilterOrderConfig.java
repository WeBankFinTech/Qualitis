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

import com.webank.wedatasphere.qualitis.filter.Filter1AuthorizationFilter;
import com.webank.wedatasphere.qualitis.filter.Filter2TokenFilter;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * @author howeye
 */
@Configuration
public class FilterOrderConfig {

    @Bean
    public Filter filter1AuthorizationFilter() {
        return new Filter1AuthorizationFilter();
    }

    @Bean
    public Filter filter2TokenFilter() { return new Filter2TokenFilter(); }

    @Bean
    public FilterRegistrationBean characterEncodingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CharacterEncodingFilter("UTF-8"));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean1() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter1AuthorizationFilter());
        filterRegistrationBean.addUrlPatterns(JerseyConfig.APPLICATION_PATH  + "/api/v1/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean2() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter2TokenFilter());
        filterRegistrationBean.addUrlPatterns(JerseyConfig.APPLICATION_PATH  + "/outer/*");
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}
