package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class ModifySystemConfigRequest {

    @JsonProperty("key_name")
    private String keyName;
    private String value;

    public ModifySystemConfigRequest() {
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static void checkRequest(ModifySystemConfigRequest request) throws UnExpectedRequestException {
        checkObject(request, "Request");
        checkString(request.getKeyName(), "KeyName");
        checkString(request.getValue(), "Value");
    }

    private static void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private static void checkObject(Object obj, String objName) throws UnExpectedRequestException {
        if (null == obj) {
            throw new UnExpectedRequestException(objName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

}
