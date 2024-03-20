package com.webank.wedatasphere.qualitis.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/1 17:56
 */
@Configuration
public class OperateCiConfig {
  @Value("${cmdb.host}")
  private String host;

  @Value("${cmdb.url}")
  private String url;

  @Value("${cmdb.integrateUrl}")
  private String integrateUrl;

  @Value("${cmdb.userAuthKey}")
  private String userAuthKey;

  @Value("${cmdb.newUserAuthKey}")
  private String newUserAuthKey;

  @Value("${cmdb.onlySlave}")
  private Boolean onlySlave;

  @Value("${ef.host}")
  private String efHost;

  @Value("${ef.url}")
  private String efUrl;

  @Value("${ef.app_id}")
  private String efAppId;

  @Value("${ef.app_token}")
  private String efAppToken;

  public OperateCiConfig() {
    // Do nothing.
  }

  public String getUserAuthKey() {
    return userAuthKey;
  }

  public void setUserAuthKey(String userAuthKey) {
    this.userAuthKey = userAuthKey;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIntegrateUrl() {
    return integrateUrl;
  }

  public void setIntegrateUrl(String integrateUrl) {
    this.integrateUrl = integrateUrl;
  }

  public String getNewUserAuthKey() {
    return newUserAuthKey;
  }

  public void setNewUserAuthKey(String newUserAuthKey) {
    this.newUserAuthKey = newUserAuthKey;
  }

  public Boolean getOnlySlave() {
    return onlySlave;
  }

  public void setOnlySlave(Boolean onlySlave) {
    this.onlySlave = onlySlave;
  }

  public String getEfHost() {
    return efHost;
  }

  public void setEfHost(String efHost) {
    this.efHost = efHost;
  }

  public String getEfUrl() {
    return efUrl;
  }

  public void setEfUrl(String efUrl) {
    this.efUrl = efUrl;
  }

  public String getEfAppId() {
    return efAppId;
  }

  public void setEfAppId(String efAppId) {
    this.efAppId = efAppId;
  }

  public String getEfAppToken() {
    return efAppToken;
  }

  public void setEfAppToken(String efAppToken) {
    this.efAppToken = efAppToken;
  }
}
