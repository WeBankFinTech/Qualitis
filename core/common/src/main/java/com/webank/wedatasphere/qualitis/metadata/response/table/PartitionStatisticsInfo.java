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
public class PartitionStatisticsInfo {
    @JsonProperty("partition_size")
    private String partitionSize;
    @JsonProperty("partition_child_count")
    private int partitionChildCount;
    @JsonProperty("partitions")
    private List<Map> partitions;

    public PartitionStatisticsInfo() {
        // Default Constructor
    }

    public String getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(String partitionSize) {
        this.partitionSize = partitionSize;
    }

    public int getPartitionChildCount() {
        return partitionChildCount;
    }

    public void setPartitionChildCount(int partitionChildCount) {
        this.partitionChildCount = partitionChildCount;
    }

    public List<Map> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Map> partitions) {
        this.partitions = partitions;
    }
}
