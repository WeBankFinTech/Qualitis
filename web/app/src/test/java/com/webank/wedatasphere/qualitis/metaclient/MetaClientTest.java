package com.webank.wedatasphere.qualitis.metaclient;

import com.webank.wedatasphere.qualitis.config.DpmConfig;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaClientTest {
    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DpmConfig dpmConfig;

    private static final Logger logger = LoggerFactory.getLogger(MetaClientTest.class);

    @Test
    public void testPartitionStatisticInfo() {
        String url = "http://10.107.116.91:8088/api/rest_j/v1/datasource/getPartitionStatisticInfo?database=bdp_test_ods_mask&tableName=100threepartiontest&partitionPath=/ds=20180830";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", "neiljianliu");
        headers.add("Token-Code", "QUALITIS-AUTH");

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
    }
}
