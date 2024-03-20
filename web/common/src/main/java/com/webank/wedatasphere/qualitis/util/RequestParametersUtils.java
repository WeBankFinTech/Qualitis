package com.webank.wedatasphere.qualitis.util;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-21 11:13
 * @description
 */
public class RequestParametersUtils {

    /**
     * Convert all string-type fields with empty value to null
     * @param target
     */
    public static void emptyStringToNull(Object target) {
        ReflectionUtils.doWithFields(target.getClass(), (field) -> {
            field.setAccessible(true);
            if (ClassUtils.isAssignable(field.getType(), String.class)) {
                String value = (String) field.get(target);
                field.set(target, StringUtils.trimToNull(value));
            }
        });
    }
}
