package com.webank.wedatasphere.qualitis.client;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.client.request.AskLinkisParameter;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author v_gaojiedeng
 */
@Component
public class RequestLinkis {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLinkis.class);

    private static final String STATUS = "status";

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    @Qualifier("linkisRestTemplate")
    private RestTemplate linkisRestTemplate;

    public Map<String, Object> getLinkisResponseByGet(AskLinkisParameter askLinkisParameter) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntity(askLinkisParameter);
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response;
        try {
            response = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.GET, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            return extractExceptionMessage(askLinkisParameter, e);
        }
        return finishLog(askLinkisParameter, response);
    }

    public Map<String, Object> removeLinkisResponseByDelete(AskLinkisParameter askLinkisParameter) throws MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntity(askLinkisParameter);
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.GET, entity);
        Map<String, Object> response;
        try {
            response = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.DELETE, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            return extractExceptionMessage(askLinkisParameter, e);
        }
        return finishLog(askLinkisParameter, response);
    }

    public Map<String, Object> getLinkisResponseByGetRetry(AskLinkisParameter askLinkisParameter) throws Exception {
        HttpEntity<Object> entity = createHttpEntity(askLinkisParameter);
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.GET, entity);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(linkisConfig.getRestTemplateMaxAttempt());

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(linkisConfig.getRetryTimeInterval());

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        Map<String, Object> response = template.execute(context -> {
            Map<String, Object> result = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.GET, entity, Map.class).getBody();
            LOGGER.info("第三方接口重试第" + context.getRetryCount() + "次调用结果:{}", new JSONObject(result));
            if (null == result.get(STATUS) || ((Integer) result.get(STATUS)).intValue() != 0) {
                throw new Exception("调用接口失败，status code: " + result.get("status"));
            }
            return result;
        }, context -> {
            LOGGER.info("第三方接口重试调用兜底方法执行，已经重试了" + context.getRetryCount() + "次");
            String message = extractMessage(context);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("message", message);
            return map;
        });

        return finishLog(askLinkisParameter, response);
    }

    private String extractMessage(RetryContext context) {
        if (context == null || context.getLastThrowable() == null) {
            return Strings.EMPTY;
        }
        if (context.getLastThrowable().getCause() != null) {
            String messageJson = context.getLastThrowable().getCause().getMessage();
            if (StringUtils.isNotEmpty(messageJson)) {
                try {
                    Map<String, String> msgMap = objectMapper.readValue(messageJson, Map.class);
                    if (msgMap.containsKey("message")) {
                        return msgMap.get("message");
                    }
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return context.getLastThrowable().getMessage() == null ? Strings.EMPTY : context.getLastThrowable().getMessage();
    }

    public Map<String, Object> getLinkisResponseByPost(AskLinkisParameter askLinkisParameter) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntity(askLinkisParameter);
        return getStringObjectMap(askLinkisParameter, entity);
    }

    public Map<String, Object> getLinkisResponseByPostBringJson(AskLinkisParameter askLinkisParameter, JSONObject jsonObject) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntityBringJson(askLinkisParameter, jsonObject);
        return getStringObjectMap(askLinkisParameter, entity);
    }

    public Map<String, Object> getLinkisResponseByPostBringJsonArray(AskLinkisParameter askLinkisParameter, JSONArray jsonArray) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntityBringJsonArray(askLinkisParameter, jsonArray);
        return getStringObjectMap(askLinkisParameter, entity);
    }


    public Map<String, Object> getLinkisResponseByPostBringJsonRetry(AskLinkisParameter askLinkisParameter, JSONObject jsonObject) throws Exception {
        HttpEntity<Object> entity = createHttpEntityBringJson(askLinkisParameter,jsonObject);
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.POST, entity);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(linkisConfig.getRestTemplateMaxAttempt());

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(linkisConfig.getRetryTimeInterval());

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        Map<String, Object> response = template.execute(context -> {
            Map<String, Object> result = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.POST, entity, Map.class).getBody();
            LOGGER.info("第三方接口重试第" + context.getRetryCount() + "次调用结果:{}", new JSONObject(result));
            if (null == result.get(STATUS) || ((Integer) result.get(STATUS)).intValue() != 0) {
                throw new Exception("调用接口失败，status code: " + result.get("status"));
            }
            return result;
        }, context -> {
            LOGGER.info("第三方接口重试调用兜底方法执行，已经重试了" + context.getRetryCount() + "次");
            String message = extractMessage(context);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("message", message);
            return map;
        });

        return finishLog(askLinkisParameter, response);
    }


    private Map<String, Object> getStringObjectMap(AskLinkisParameter askLinkisParameter, HttpEntity<Object> entity) throws MetaDataAcquireFailedException {
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = null;
        try {
            response = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.POST, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            return extractExceptionMessage(askLinkisParameter, e);
        }
        return finishLog(askLinkisParameter, response);
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> extractExceptionMessage(AskLinkisParameter askLinkisParameter, ResourceAccessException e) throws MetaDataAcquireFailedException {
        Map<String, Object> response = null;
//        From LinkisErrorHandler
        String originalMessage = e.getCause().getMessage();
        if (StringUtils.isNotEmpty(originalMessage)) {
            try {
                response = objectMapper.readValue(originalMessage, Map.class);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            if (null != response) {
                return finishLog(askLinkisParameter, response);
            }
        }
        LOGGER.error("Error! ", e.getMessage(), e);
        throw new MetaDataAcquireFailedException("Error! Can not " + askLinkisParameter.getLogmessage() + ", exception: " + e.getMessage(), 500);
    }

    public Map<String, Object> getLinkisResponseByPut(AskLinkisParameter askLinkisParameter) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntity(askLinkisParameter);
        return getMapForPutBringJson(askLinkisParameter, entity);
    }

    private Map<String, Object> getMapForPutBringJson(AskLinkisParameter askLinkisParameter, HttpEntity<Object> entity) throws MetaDataAcquireFailedException {
        LOGGER.info("Start to " + askLinkisParameter.getLogmessage() + " url: {}, method: {}, body: {}", askLinkisParameter.getUrl(), javax.ws.rs.HttpMethod.PUT, entity);
        Map<String, Object> response = null;
        try {
            response = linkisRestTemplate.exchange(askLinkisParameter.getUrl(), HttpMethod.PUT, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            return extractExceptionMessage(askLinkisParameter, e);
        }
        return finishLog(askLinkisParameter, response);
    }

    public Map<String, Object> getLinkisResponseByPutBringJson(AskLinkisParameter askLinkisParameter, JSONObject jsonObject) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntityBringJson(askLinkisParameter,jsonObject);
        return getMapForPutBringJson(askLinkisParameter, entity);
    }

    private boolean checkResponse(Map<String, Object> response) {
        if (null == response.get(STATUS)) {
            return false;
        }
        Integer responseStatus = (Integer) response.get(STATUS);
        return responseStatus == 0;
    }

    private HttpEntity<Object> createHttpEntity(AskLinkisParameter askLinkisParameter) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", askLinkisParameter.getAuthUser());
        headers.add("Token-Code", askLinkisParameter.getLinkisToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Object> createHttpEntityBringJson(AskLinkisParameter askLinkisParameter, JSONObject jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", askLinkisParameter.getAuthUser());
        headers.add("Token-Code", askLinkisParameter.getLinkisToken());
        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private HttpEntity<Object> createHttpEntityBringJsonArray(AskLinkisParameter askLinkisParameter, JSONArray jsonArray) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", askLinkisParameter.getAuthUser());
        headers.add("Token-Code", askLinkisParameter.getLinkisToken());
        return new HttpEntity<>(jsonArray.toString(), headers);
    }

    private Map<String, Object> finishLog(AskLinkisParameter askLinkisParameter, Map<String, Object> response) throws MetaDataAcquireFailedException {
        String traceId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        LOGGER.info("traceId: {}  Finished to {}, url: {}, authUser: {}, response: {}", traceId, askLinkisParameter.getLogmessage(), askLinkisParameter.getUrl(), askLinkisParameter.getAuthUser(), response);
        if (!checkResponse(response)) {
            String content = null;
            if (response.containsKey("message")) {
                content = response.get("message").toString();
            }
            String errorMsg = String.format("Error! Can not get meta data from linkis, traceId: %s, authUser: %s, exception: %s", traceId, askLinkisParameter.getAuthUser(), content);
            LOGGER.error(errorMsg);
            throw new MetaDataAcquireFailedException(content);
        }
        return response;
    }

    public Map<String, Object> getLinkisResponseByPutBringJsonArray(AskLinkisParameter askLinkisParameter, JSONArray jsonArray) throws MetaDataAcquireFailedException {
        HttpEntity<Object> entity = createHttpEntityBringJsonArray(askLinkisParameter, jsonArray);
        return getMapForPutBringJson(askLinkisParameter, entity);
    }
}
