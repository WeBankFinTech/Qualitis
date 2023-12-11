package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-16 17:29
 * @description
 */
public class ParameterChecker {

    private ParameterChecker() {
    }

    /**
     * Don't to participate the deep check
     */
    private static List<String> generalTypeList = Arrays.asList("Long", "Integer", "String", "BigDecimal", "Float", "Double");

    public static void checkEmpty(Object obj) throws UnExpectedRequestException, IllegalAccessException {
        checkEmpty(obj, false);
    }

    /**
     * @param targetObj
     * @param enableDeepCheck If it is true, the process will be called in-loop
     * @throws UnExpectedRequestException
     */
    public static void checkEmpty(Object targetObj, boolean enableDeepCheck) throws UnExpectedRequestException, IllegalAccessException {
        if (null == targetObj) {
            throw new UnExpectedRequestException("Target object must not be null!");
        }
        Class<?> clz = targetObj.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                field.setAccessible(true);
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                Class<?> type = field.getType();
                Object returnValue = field.get(targetObj);
                if (jsonProperty.required()) {
                    checkFieldIfEmpty(jsonProperty, type, returnValue);
                }
                if (enableDeepCheck) {
                    deepCheck(type, returnValue);
                }
            }
        }
    }

    private static void checkFieldIfEmpty(JsonProperty jsonProperty, Class<?> type, Object returnValue) throws UnExpectedRequestException {
        if (type.isAssignableFrom(String.class)) {
            CommonChecker.checkString((String) returnValue, jsonProperty.value());
        } else if (type.isAssignableFrom(List.class) || type.isAssignableFrom(Set.class)) {
            CommonChecker.checkCollections((Collection) returnValue, jsonProperty.value());
        } else {
            CommonChecker.checkObject(returnValue, jsonProperty.value());
        }
    }

    private static void deepCheck(Class<?> type, Object returnValue) throws UnExpectedRequestException, IllegalAccessException {
        if (null == returnValue || generalTypeList.contains(type.getSimpleName())) {
            return;
        }
        if (type.isAssignableFrom(List.class) || type.isAssignableFrom(Set.class)) {
            Collection<Object> collection = (Collection) returnValue;
            for (Object item : collection) {
                checkEmpty(item, true);
            }
        } else {
            checkEmpty(returnValue, true);
        }
    }

}
