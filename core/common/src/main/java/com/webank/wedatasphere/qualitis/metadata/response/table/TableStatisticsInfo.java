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

package com.webank.wedatasphere.qualitis.metadata.response.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public class TableStatisticsInfo {
    @JsonProperty("table_size")
    private String tableSize;
    @JsonProperty("table_file_count")
    private int tableFileCount;
    @JsonProperty("partitions")
    private List<Map> partitions;

    public TableStatisticsInfo() {
        // Default Constructor
    }

    public String getTableSize() {
        return tableSize;
    }

    public void setTableSize(String tableSize) {
        this.tableSize = tableSize;
    }

    public int getTableFileCount() {
        return tableFileCount;
    }

    public void setTableFileCount(int tableFileCount) {
        this.tableFileCount = tableFileCount;
    }

    public List<Map> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Map> partitions) {
        this.partitions = partitions;
    }
}
