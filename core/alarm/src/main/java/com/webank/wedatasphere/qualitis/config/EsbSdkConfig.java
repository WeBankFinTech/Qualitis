package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author v_gaojiedeng@webank.com
 */
@Configuration
public class EsbSdkConfig {

    @Value("${esbSdk.email_title}")
    private String title;

    @Value("${esbSdk.email_send_suffix_path}")
    private String emailSendSuffixPath;

    @Value("${esbSdk.email_white_list}")
    private String emailWhiteList;

    @Value("${esbSdk.hr_query_suffix_path}")
    private String hrQuerySuffixPath;

    @Value("${esbSdk.email_sender}")
    private String emailSender;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmailSendSuffixPath() {
        return emailSendSuffixPath;
    }

    public void setEmailSendSuffixPath(String emailSendSuffixPath) {
        this.emailSendSuffixPath = emailSendSuffixPath;
    }

    public String getEmailWhiteList() {
        return emailWhiteList;
    }

    public void setEmailWhiteList(String emailWhiteList) {
        this.emailWhiteList = emailWhiteList;
    }

    public String getHrQuerySuffixPath() {
        return hrQuerySuffixPath;
    }

    public void setHrQuerySuffixPath(String hrQuerySuffixPath) {
        this.hrQuerySuffixPath = hrQuerySuffixPath;
    }

    public String getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(String emailSender) {
        this.emailSender = emailSender;
    }
}
