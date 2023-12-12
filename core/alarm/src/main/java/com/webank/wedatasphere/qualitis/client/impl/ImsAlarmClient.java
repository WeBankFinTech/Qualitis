package com.webank.wedatasphere.qualitis.client.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.client.AlarmClient;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.entity.ReportBatchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */

@Component
public class ImsAlarmClient implements AlarmClient {
    @Autowired
    private ImsConfig imsConfig;

    @Autowired
    private RestTemplate restTemplate;

    private static final String IMS_RES_CODE = "resCode";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImsAlarmClient.class);
    @Override
    public void sendAlarm(String receiver, String alertTitle, String alertInfo, String alertLevel) {
        String url = imsConfig.getUrl() + imsConfig.getSendAlarmPath();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.TEXT_HTML.toString());

        Map<String, List<Map<String, String>>> outerMap = new HashMap<>(2);
        Map<String, String> innerMap = new HashMap<>(8);
        innerMap.put("alert_title", alertTitle);
        innerMap.put("sub_system_id", imsConfig.getSystemId());
        innerMap.put("alert_level", alertLevel);
        innerMap.put("alert_info", alertInfo);
        innerMap.put("alert_way", imsConfig.getAlertWay());
        innerMap.put("alert_reciver", receiver);
        outerMap.put("alertList", Arrays.asList(innerMap));
        Gson gson = new Gson();
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(outerMap), headers);

        LOGGER.info("Start to send ims. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity);
        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("Failed to send ims alarm. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity, e);
            return;
        }
        LOGGER.info("Succeed to send ims. response: {}", response);
    }

    @Override
    public void sendNewAlarm(List<Map<String, Object>> requestList) {
        String url = imsConfig.getUrl() + imsConfig.getSendAlarmPath();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.TEXT_HTML.toString());
        Map<String, Object> requestMap = new HashMap<>(2);
        requestMap.put("userAuthKey", imsConfig.getUserAuthKey());
        requestMap.put("alertList", requestList);
        Gson gson = new Gson();
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestMap), headers);

        LOGGER.info("Start to send ims. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity);
        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("Failed to send ims alarm. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity, e);
            return;
        }
        LOGGER.info("Succeed to send ims. response: {}", response);
    }

    @Override
    public void report(ReportBatchInfo reportBatchInfo) {
        String url = imsConfig.getUrl() + imsConfig.getSendReportPath();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);

        Gson gson = new Gson();
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(reportBatchInfo), headers);

        LOGGER.info("Start to send ims rule metric report. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity);
        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("Failed to send ims alarm rule metric report. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity, e);
            return;
        }
        LOGGER.info("Succeed to send ims rule metric report. response: {}", response);
    }

    @Override
    public void sendAbnormalDataRecordAlarm(ImsConfig imsConfig, List<Map<String, Object>> data) {
        String url = imsConfig.getFullUrlAbnormalDataRecord();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        Map<String, Object> map = new HashMap<>(2);
        map.put("userAuthKey", imsConfig.getUserAuthKey());
        map.put("data", data);
        Gson gson = new Gson();
        HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(map), headers);

        LOGGER.info("Start to send abnormal data record ims. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity);
        Map<String, Object> response;
        try {
            response = restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            LOGGER.error("Failed to send abnormal data record ims. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity, e);
            return;
        }
        if (0 == Integer.parseInt(response.get(IMS_RES_CODE).toString())) {
            LOGGER.info("Success to send abnormal data record ims. response: {}", response);
        } else {
            LOGGER.error("Failed to send abnormal data record ims. response: {}", response);
        }
    }
}
