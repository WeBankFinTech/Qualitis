package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-23 17:48
 * @description
 */
public class ClusterInfoResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("cluster_info_id")
    private long id;

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_type")
    private String clusterType;
    @JsonProperty("linkis_address")
    private String linkisAddress;
    @JsonProperty("linkis_token")
    private String linkisToken;
    @JsonProperty("hive_urn")
    private String hiveUrn;

    @JsonProperty("data_size_limit")
    private String skipDataSize;

    private String wtssJson;

    private String jobserverJson;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("modify_time")
    private String modifyTime;

    public ClusterInfoResponse(ClusterInfo clusterInfo) {
        BeanUtils.copyProperties(clusterInfo, this);
        if (StringUtils.isNotBlank(this.skipDataSize)) {
            this.skipDataSize = this.skipDataSize.replace(" G", "");
        }
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
