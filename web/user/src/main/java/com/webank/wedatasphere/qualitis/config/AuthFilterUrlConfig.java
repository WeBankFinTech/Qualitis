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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-19
 */
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthFilterUrlConfig {

    @Value("unFilterUrls")
    private List<String> unFilterUrls = new ArrayList<>();

    @Value("uploadUrls")
    private List<String> uploadUrls = new ArrayList<>();

    public List<String> getUnFilterUrls() {
        return unFilterUrls;
    }

    public List<String> getUploadUrls() {
        return uploadUrls;
    }
}

