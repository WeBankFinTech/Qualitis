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

package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author allenzhou
 */
public class ExcelDatasourceEnv extends BaseRowModel {

    @ExcelProperty(value = "Datasource Json Object", index = 0)
    private String datasourceJsonObject;
    @ExcelProperty(value = "Datasource Env Json Object", index = 1)
    private String datasourceEnvNameListJsonObject;
    @ExcelProperty(value = "Datasource DCN Type Json Object", index = 2)
    private String dcnRangeValueListJsonObject;
    @ExcelProperty(value = "DataVisibility Json Object", index = 3)
    private String dataVisibilityListJsonObject;

    public ExcelDatasourceEnv() {
        // Default
    }

    public static ExcelDatasourceEnv fromLinkisDataSource(LinkisDataSource linkisDataSource, List<DataVisibility> dataVisibilityList) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelDatasourceEnv excelDatasourceEnv = new ExcelDatasourceEnv();
        linkisDataSource.setId(null);
        excelDatasourceEnv.setDatasourceJsonObject(objectMapper.writeValueAsString(linkisDataSource));
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            dataVisibilityList.forEach(dataVisibility -> {
                dataVisibility.setTableDataId(null);
                dataVisibility.setId(null);
            });
            excelDatasourceEnv.setDataVisibilityListJsonObject(objectMapper.writeValueAsString(dataVisibilityList));
        }
        return excelDatasourceEnv;
    }

    public String getDcnRangeValueListJsonObject() {
        return dcnRangeValueListJsonObject;
    }

    public void setDcnRangeValueListJsonObject(String dcnRangeValueListJsonObject) {
        this.dcnRangeValueListJsonObject = dcnRangeValueListJsonObject;
    }

    public String getDatasourceEnvNameListJsonObject() {
        return datasourceEnvNameListJsonObject;
    }

    public void setDatasourceEnvNameListJsonObject(String datasourceEnvNameListJsonObject) {
        this.datasourceEnvNameListJsonObject = datasourceEnvNameListJsonObject;
    }

    public String getDataVisibilityListJsonObject() {
        return dataVisibilityListJsonObject;
    }

    public void setDataVisibilityListJsonObject(String dataVisibilityListJsonObject) {
        this.dataVisibilityListJsonObject = dataVisibilityListJsonObject;
    }

    public String getDatasourceJsonObject() {
        return datasourceJsonObject;
    }

    public void setDatasourceJsonObject(String datasourceJsonObject) {
        this.datasourceJsonObject = datasourceJsonObject;
    }

    public LinkisDataSource getLinkisDataSource(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(this.datasourceJsonObject, new TypeReference<LinkisDataSource>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return null;
    }

    public List<DataVisibility> getDataVisibilityList(ObjectMapper objectMapper) {
        if (StringUtils.isBlank(this.dataVisibilityListJsonObject)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(this.dataVisibilityListJsonObject, new TypeReference<List<DataVisibility>>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return Collections.emptyList();
    }

    public List<String> getLinkisDataSourceEnvNameList(ObjectMapper objectMapper) {
        if (StringUtils.isBlank(this.datasourceEnvNameListJsonObject)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(this.datasourceEnvNameListJsonObject, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return Collections.emptyList();
    }

    public List<String> getDcnRangeValuesList(ObjectMapper objectMapper) {
        if (StringUtils.isBlank(this.dcnRangeValueListJsonObject)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(this.dcnRangeValueListJsonObject, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return Collections.emptyList();
    }


}
