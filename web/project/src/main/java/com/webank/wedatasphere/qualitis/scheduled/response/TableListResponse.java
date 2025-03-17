package com.webank.wedatasphere.qualitis.scheduled.response;

import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public class TableListResponse {

    Set<String> cluster_name;

    Set<String> wtss_project_name;

    Set<String> wtss_task_name;

    Set<String> wtss_work_flow_name;

    public Set<String> getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(Set<String> cluster_name) {
        this.cluster_name = cluster_name;
    }

    public Set<String> getWtss_project_name() {
        return wtss_project_name;
    }

    public void setWtss_project_name(Set<String> wtss_project_name) {
        this.wtss_project_name = wtss_project_name;
    }

    public Set<String> getWtss_task_name() {
        return wtss_task_name;
    }

    public void setWtss_task_name(Set<String> wtss_task_name) {
        this.wtss_task_name = wtss_task_name;
    }

    public Set<String> getWtss_work_flow_name() {
        return wtss_work_flow_name;
    }

    public void setWtss_work_flow_name(Set<String> wtss_work_flow_name) {
        this.wtss_work_flow_name = wtss_work_flow_name;
    }
}
