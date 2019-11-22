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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public class QueryMetaClusterResponse {

  private long total;
  private List<MetaClusterResponse> clusters;

  public QueryMetaClusterResponse() {
    // Default Constructor
  }

  public QueryMetaClusterResponse(List<MetaDataCluster> metaDataClusters, long total) {
    if (CollectionUtils.isNotEmpty(metaDataClusters)) {
      clusters = new ArrayList<>();
      for (MetaDataCluster data : metaDataClusters) {
        clusters.add(new MetaClusterResponse(data.getClusterName()));
      }
    }
    this.total = total;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<MetaClusterResponse> getClusters() {
    return clusters;
  }

  public void setClusters(List<MetaClusterResponse> clusters) {
    this.clusters = clusters;
  }


  class MetaClusterResponse {
    @JsonProperty("cluster_name")
    private String clusterName;

    MetaClusterResponse(String clusterName) {
      this.clusterName = clusterName;
    }

    public String getClusterName() {
      return clusterName;
    }

    public void setClusterName(String clusterName) {
      this.clusterName = clusterName;
    }
  }
}
