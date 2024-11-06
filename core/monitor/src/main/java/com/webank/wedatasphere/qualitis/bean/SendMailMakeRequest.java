package com.webank.wedatasphere.qualitis.bean;

import com.webank.wedatasphere.qualitis.report.entity.SubscriptionRecord;

import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public class SendMailMakeRequest {

    private String receiver;
    private List<Map<String, Object>> mapLists;
    private List<SubscriptionRecord> subscriptionRecords;
    private String createUser;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public List<Map<String, Object>> getMapLists() {
        return mapLists;
    }

    public void setMapLists(List<Map<String, Object>> mapLists) {
        this.mapLists = mapLists;
    }

    public List<SubscriptionRecord> getSubscriptionRecords() {
        return subscriptionRecords;
    }

    public void setSubscriptionRecords(List<SubscriptionRecord> subscriptionRecords) {
        this.subscriptionRecords = subscriptionRecords;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    public String toString() {
        return "SendMailMakeRequest{" +
                "receiver='" + receiver + '\'' +
                ", mapLists=" + mapLists +
                ", subscriptionRecords=" + subscriptionRecords +
                ", createUser='" + createUser + '\'' +
                '}';
    }
}
