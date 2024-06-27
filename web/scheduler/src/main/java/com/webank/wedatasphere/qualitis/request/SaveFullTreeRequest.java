package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 14:55
 */
public class SaveFullTreeRequest {

    @JsonProperty("full_tree")
    private List<Map<String, Object>> fullTree;

    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("user_name")
    private String userName;

    public SaveFullTreeRequest() {
        // Default do nothing.
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Map<String, Object>> getFullTree() {
        return fullTree;
    }

    public void setFullTree(List<Map<String, Object>> fullTree) {
        this.fullTree = fullTree;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}
