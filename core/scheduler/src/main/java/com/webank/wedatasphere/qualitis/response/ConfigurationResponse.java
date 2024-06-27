package com.webank.wedatasphere.qualitis.response;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ConfigurationResponse {

    private String name;

    private String description;

    private List<SettingsResponse> settings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SettingsResponse> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingsResponse> settings) {
        this.settings = settings;
    }
}
