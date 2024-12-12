package com.webank.wedatasphere.qualitis.response;

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.request.SendLinkisRequst;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v_gaojiedeng
 */
@Component
public class DemandLinkis {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemandLinkis.class);

    private static final String STATUS = "status";

    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> getLinkisResponseBringJsonRetry(SendLinkisRequst sendLinkisRequst, String json) throws Exception {
        HttpEntity<Object> entity = createHttpEntityBringJson(sendLinkisRequst, json);
        LOGGER.info("Start to " + sendLinkisRequst.getLogmessage() + " url: {}, method: {}, body: {}", sendLinkisRequst.getUrl(), javax.ws.rs.HttpMethod.POST, entity);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(linkisConfig.getRestTemplateMaxAttempt());

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(linkisConfig.getRetryTimeInterval());

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        Map<String, Object> response = template.execute(new RetryCallback<Map<String, Object>, Exception>() {
            @Override
            public Map<String, Object> doWithRetry(RetryContext context) throws Exception {
                Map<String, Object> result = restTemplate.postForObject(sendLinkisRequst.getUrl(), entity, Map.class);
                LOGGER.info("第三方接口重试第" + context.getRetryCount() + "次调用结果:{}", new JSONObject(result));
                if (null == result.get(STATUS) || ((Integer) result.get(STATUS)).intValue() != 0) {
                    throw new Exception("调用接口失败，status code: " + result.get("status"));
                }
                return result;
            }
        }, context -> {
            LOGGER.info("第三方接口重试调用兜底方法执行，已经重试了" + context.getRetryCount() + "次");
            String message = extractMessage(context);
            Map<String, Object> map = new HashMap<>(1);
            map.put("msg", message);
            return map;
        });

        return finishLog(sendLinkisRequst, response);
    }

    private String extractMessage(RetryContext context) {
        if (context == null) {
            return "";
        } else {
            if (context.getLastThrowable() == null) {
                return "";
            }
            return context.getLastThrowable().getMessage() == null ? "" : context.getLastThrowable().getMessage();
        }

    }

    private Map<String, Object> finishLog(SendLinkisRequst sendLinkisRequst, Map<String, Object> response) throws JobSubmitException {
        LOGGER.info("Succeed to " + sendLinkisRequst.getLogmessage() + " response: {}", response);
        if (!checkResponse(response)) {
            String message = (String) response.get("message");
            throw new JobSubmitException("Error! Can not submit job, exception: " + message, response.get("errCode") != null ? (Integer) response.get("errCode") : -1);
        }
        return response;
    }

    private boolean checkResponse(Map<String, Object> response) {
        if (null == response.get(STATUS)) {
            return false;
        }
        Integer responseStatus = (Integer) response.get(STATUS);
        return responseStatus == 0;
    }

    private HttpEntity<Object> createHttpEntityBringJson(SendLinkisRequst sendLinkisRequst, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token-User", sendLinkisRequst.getAuthUser());
        headers.add("Token-Code", sendLinkisRequst.getLinkisToken());
        return new HttpEntity<>(json, headers);
    }
}
