/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.ref.entity;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/7/12 23:11
 */
public class QualitisExportResponseRef extends AbstractResponseRef {
    private Map<String, Object> resourceMap;

    public QualitisExportResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public Map<String, Object> getResourceMap() {
        return resourceMap;
    }

    public void setResourceMap(Map<String, Object> resourceMap) {
        this.resourceMap = resourceMap;
    }

    @Override
    public Map<String, Object> toMap() {
        return resourceMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
