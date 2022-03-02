package com.webank.wedatasphere.qualitis.util;

import java.util.Map;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 10:45
 */
public class JexlUtil {
    private static JexlEngine jexlEngine = new Engine();

    public static Object executeExpression(String jexlExpression, Map<String, Object> map) {
        JexlExpression expression = jexlEngine.createExpression(jexlExpression);
        JexlContext context = new MapContext();
        if (map !=null && ! map.isEmpty()) {
            map.forEach(context::set);
        }
        return expression.evaluate(context);
    }
}
