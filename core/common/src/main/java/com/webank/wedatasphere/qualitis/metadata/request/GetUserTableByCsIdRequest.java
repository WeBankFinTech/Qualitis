package com.webank.wedatasphere.qualitis.metadata.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class GetUserTableByCsIdRequest {
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("start_index")
    private Integer startIndex;
    @JsonProperty("page_size")
    private Integer pageSize;

    private String loginUser;

    public GetUserTableByCsIdRequest() {
        startIndex = DEFAULT_START_INDEX;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getNodeName() { return nodeName; }

    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }
}
