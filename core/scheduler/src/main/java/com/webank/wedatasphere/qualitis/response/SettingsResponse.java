package com.webank.wedatasphere.qualitis.response;

/**
 * @author v_gaojiedeng@webank.com
 */
public class SettingsResponse {

    private Integer id;

    private String key;
    private String description;
    private String name;

    private String defaultValue;
    private String validateType;
    private String validateRange;

    private Integer level;
    private String engineType;

    private String treeName;
    private Integer valueId;
    private String configValue;
    private Integer configLabelId;
    private String unit;
    private Boolean isUserDefined;
    private Boolean hidden;
    private Boolean advanced;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public String getValidateRange() {
        return validateRange;
    }

    public void setValidateRange(String validateRange) {
        this.validateRange = validateRange;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public Integer getValueId() {
        return valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public Integer getConfigLabelId() {
        return configLabelId;
    }

    public void setConfigLabelId(Integer configLabelId) {
        this.configLabelId = configLabelId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getAdvanced() {
        return advanced;
    }

    public void setAdvanced(Boolean advanced) {
        this.advanced = advanced;
    }

    public Boolean getIsUserDefined() {
        return isUserDefined;
    }

    public void setIsUserDefined(Boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }
}
