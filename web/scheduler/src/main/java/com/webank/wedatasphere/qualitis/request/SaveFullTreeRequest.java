package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 14:56
 */
public class SaveFullTreeRequest {
    @JsonProperty("creator")
    private String creator;

    @JsonProperty("full_tree_queue_name")
    private List<Map> queueName;

    @JsonProperty("full_tree")
    private List<Map> fullTree;

    @JsonProperty("cluster_name")
    private String clusterName;

    public SaveFullTreeRequest() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<Map> getQueueName() {
        return queueName;
    }

    public void setQueueName(List<Map> queueName) {
        this.queueName = queueName;
    }

    public List<Map> getFullTree() {
        return fullTree;
    }

    public void setFullTree(List<Map> fullTree) {
        this.fullTree = fullTree;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}
