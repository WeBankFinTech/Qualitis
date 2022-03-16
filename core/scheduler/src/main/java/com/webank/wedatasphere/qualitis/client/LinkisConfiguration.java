package com.webank.wedatasphere.qualitis.client;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 14:00
 */
@Component
public class LinkisConfiguration {
    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisConfiguration.class);

    public Map getFullTree(String clusterName, String user) throws UnExpectedRequestException {
        ClusterInfo clusterInfoInDb = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfoInDb == null) {
            throw new UnExpectedRequestException("cluster name {&DOES_NOT_EXIST}");
        }

        String url = UriBuilder.fromUri(clusterInfoInDb.getLinkisAddress()).path(linkisConfig.getPrefix()).path(linkisConfig.getGetFullTree())
            .queryParam("creator", linkisConfig.getAppName())
            .queryParam("engineType", "spark")
            .queryParam("version", "2.4.3").toString();

        String urlQueueName = UriBuilder.fromUri(clusterInfoInDb.getLinkisAddress()).path(linkisConfig.getPrefix()).path(linkisConfig.getGetFullTree())
            .queryParam("creator", linkisConfig.getAppName()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", user);
        headers.add("Token-Code", clusterInfoInDb.getLinkisToken());

        HttpEntity entity = new HttpEntity<>(headers);
        HttpEntity entityQueueName = new HttpEntity<>(headers);

        Map response = null;
        Map responseQueueName = null;

        LOGGER.info("Start to get configuration from linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.GET, entity);
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            responseQueueName = restTemplate.exchange(urlQueueName, HttpMethod.GET, entityQueueName, Map.class).getBody();
            LOGGER.info("Finish to get configuration from linkis. response: {}", response);
            Integer code = (Integer) response.get("status");
            if (code != 0) {
                throw new UnExpectedRequestException("Failed to get configuration from linkis.");
            }
            Integer codeQueueName = (Integer) responseQueueName.get("status");
            if (codeQueueName != 0) {
                throw new UnExpectedRequestException("Failed to get configuration from linkis.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("Failed to get configuration from linkis.");
        }
        Map<String, Map> responseMap = new HashMap<>(2);
        responseMap.put("fule_tree", (Map) response.get("data"));
        responseMap.put("full_tree_queue_name", (Map) responseQueueName.get("data"));

        return responseMap;
    }

    public Map saveFullTree(String clusterName, String creator, List<Map> fullTreeQueueName, List<Map> fullTree, String userName) throws UnExpectedRequestException {
        ClusterInfo clusterInfoInDb = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfoInDb == null) {
            throw new UnExpectedRequestException("cluster name {&ALREADY_EXIST}");
        }
        String url = UriBuilder.fromUri(clusterInfoInDb.getLinkisAddress()).path(linkisConfig.getPrefix()).path(linkisConfig.getSaveFullTree())
            .toString();
        Gson gson = new Gson();

        Map<String, Object> map = new HashMap<>(2);
        map.put("creator", creator);
        map.put("fullTree", fullTree);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", userName);
        headers.add("Token-Code", clusterInfoInDb.getLinkisToken());

        HttpEntity entity = new HttpEntity<>(gson.toJson(map), headers);
        Map response = null;
        Map responseQueueName = null;

        LOGGER.info("Start to save configuration to linkis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
            LOGGER.info("Finish to save configuration to linkis. response: {}", response);
            Integer code = (Integer) response.get("status");
            if (code != 0) {
                throw new UnExpectedRequestException("Failed to get configuration from linkis.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("Failed to get configuration from linkis.");
            throw new UnExpectedRequestException("Failed to get configuration from linkis.");
        }

        return (Map) response.get("data");
    }
}
