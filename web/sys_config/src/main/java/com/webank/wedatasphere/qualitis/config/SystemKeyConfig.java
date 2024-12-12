package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author howeye
 */
@Configuration
@ConfigurationProperties(prefix = "system.config")
public class SystemKeyConfig {

    @Value("${system.config.save_database_pattern}")
    private String saveDatabasePattern;

    public SystemKeyConfig() {
    }

    public String getSaveDatabasePattern() {
        return saveDatabasePattern;
    }

    public void setSaveDatabasePattern(String saveDatabasePattern) {
        this.saveDatabasePattern = saveDatabasePattern;
    }
}
