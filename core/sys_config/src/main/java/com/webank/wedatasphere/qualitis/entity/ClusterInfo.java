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

package com.webank.wedatasphere.qualitis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_config_cluster_info")
public class ClusterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("cluster_info_id")
    private long id;

    @Column(name = "cluster_name", length = 100)
    @JsonProperty("cluster_name")
    private String clusterName;
    @Column(name = "cluster_type", length = 100)
    @JsonProperty("cluster_type")
    private String clusterType;
    @Column(length = 100, name = "linkis_address")
    @JsonProperty("linkis_address")
    private String linkisAddress;
    @Column(name = "linkis_token", length = 500)
    @JsonProperty("linkis_token")
    private String linkisToken;
    @Column(name = "hive_urn", length = 25)
    @JsonProperty("hive_urn")
    private String hiveUrn;

    @Column(name = "skip_data_size")
    private String skipDataSize;

    @Column(name = "wtss_json", columnDefinition = "MEDIUMTEXT")
    private String wtssJson;

    @Column(name = "jobserver_json", columnDefinition = "MEDIUMTEXT")
    private String jobserverJson;

    @JsonProperty("create_user")
    @Column(name = "create_user")
    private String createUser;

    @JsonProperty("create_time")
    @Column(name = "create_time")
    private String createTime;

    @JsonProperty("modify_user")
    @Column(name = "modify_user")
    private String modifyUser;

    @JsonProperty("modify_time")
    @Column(name = "modify_time")
    private String modifyTime;

    public ClusterInfo() {
        // Default Constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getLinkisAddress() {
        return linkisAddress;
    }

    public void setLinkisAddress(String linkisAddress) {
        this.linkisAddress = linkisAddress;
    }

    public String getLinkisToken() {
        return linkisToken;
    }

    public void setLinkisToken(String linkisToken) {
        this.linkisToken = linkisToken;
    }

    public String getHiveUrn() {
        return hiveUrn;
    }

    public void setHiveUrn(String hiveUrn) {
        this.hiveUrn = hiveUrn;
    }

    public String getSkipDataSize() {
        return skipDataSize;
    }

    public void setSkipDataSize(String skipDataSize) {
        this.skipDataSize = skipDataSize;
    }

    public String getWtssJson() {
        return wtssJson;
    }

    public void setWtssJson(String wtssJson) {
        this.wtssJson = wtssJson;
    }

    public String getJobserverJson() {
        return jobserverJson;
    }

    public void setJobserverJson(String jobserverJson) {
        this.jobserverJson = jobserverJson;
    }

    public Map<String, Object> getJobServerJsonMap() {
        if (Strings.isNotEmpty(jobserverJson)) {
            try {
                return new ObjectMapper().readValue(jobserverJson, Map.class);
            } catch (IOException e) {
              //No exception handing
            }
        }
        return Collections.emptyMap();
    }

    public Map<String, Object> getWtssJsonMap() {
        if (Strings.isNotEmpty(wtssJson)) {
            try {
                return new ObjectMapper().readValue(wtssJson, Map.class);
            } catch (IOException e) {
              //No exception handing
            }
        }
        return Collections.emptyMap();
    }
}
