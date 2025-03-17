package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-12 17:15
 * @description
 */
@Component
@ConfigurationProperties(prefix = "itsm")
public class ItsmConfig {

    @Value("secret_key")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
