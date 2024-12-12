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

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * @author howeye
 */
@Component
@ApplicationPath(JerseyConfig.APPLICATION_PATH)
public class JerseyConfig extends ResourceConfig {

    public static final String APPLICATION_PATH = "/qualitis";
    public static final String RESOURCE_PACKAGE_NAME = "com.webank.wedatasphere.qualitis";

    public JerseyConfig() {
        register(RequestContextFilter.class);
        register(MultiPartFeature.class);
        packages(RESOURCE_PACKAGE_NAME);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }

}
