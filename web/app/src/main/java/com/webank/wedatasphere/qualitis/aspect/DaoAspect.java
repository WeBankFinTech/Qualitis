/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.aspect;

import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
@Aspect
@Component
public class DaoAspect {

    @Autowired
    private LocaleParser localeParser;

    private static final String GET_METHOD_PREFIX = "get";
    private static final String SET_METHOD_PREFIX = "set";

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoAspect.class);

    private static final List<String> STEP_IN_METHOD_LIST = Arrays.asList("getTemplateOutputMetas", "getChildTemplate", "getTemplateMidTableInputMetas", "getAlarmConfigs", "getRules", "getTemplateOutputMeta", "getTemplate");

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.project.dao.ProjectDao.*(..))")
    public void projectAspect(){
    }

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.rule.dao.impl.RuleTemplateDaoImpl.*(..))")
    public void ruleTemplateAspect(){
    }

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.rule.dao.impl.TemplateMidTableInputMetaDaoImpl.*(..))")
    public void templateMidTableInputMetaAspect(){
    }

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateStatisticsInputMetaRepository.findById(..))")
    public void templateStatisticsInputMetaAspect(){
    }

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.rule.dao.impl.TemplateOutputMetaDaoImpl.*(..))")
    public void templateOutputMetaAspect(){
    }

    @Pointcut("execution(public * com.webank.wedatasphere.qualitis.rule.dao.impl.RuleDaoImpl.*(..))")
    public void ruleAspect(){
    }

    @AfterReturning(pointcut = "ruleTemplateAspect()", returning = "object")
    public void ruleTemplateAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    @AfterReturning(pointcut = "templateMidTableInputMetaAspect()", returning = "object")
    public void templateMidTableInputMetaAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    @AfterReturning(pointcut = "templateStatisticsInputMetaAspect()", returning = "object")
    public void templateStatisticsInputMetaAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    @AfterReturning(pointcut = "templateOutputMetaAspect()", returning = "object")
    public void templateOutputMetaAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    @AfterReturning(pointcut = "ruleAspect()", returning = "object")
    public void ruleAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    @AfterReturning(pointcut = "projectAspect()", returning = "object")
    public void projectAspectAfter(JoinPoint joinPoint, Object object) throws InvocationTargetException, IllegalAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String localeStr = request.getHeader("Content-Language");
        if (object != null) {
            replaceMessage(object, localeStr);
        }
    }

    private void replaceMessage(Object object, String localeStr) throws InvocationTargetException, IllegalAccessException {
        Class objectClass = object.getClass();
        if (object instanceof Iterable || objectClass.isArray()) {
            for (Object obj : (Iterable<? extends Object>) object) {
                if (obj != null) {
                    replaceMessage(obj, localeStr);
                }
            }
        } else {
            for (Method method : objectClass.getMethods()) {
                if (STEP_IN_METHOD_LIST.contains(method.getName()) && method.getParameterCount() == 0) {
                    // 获取返回值，并调用replaceMessage
                    Object returnObj = method.invoke(object);
                    if (returnObj != null) {
                        replaceMessage(returnObj, localeStr);
                    }
                }
                if (method.getName().startsWith(GET_METHOD_PREFIX) && method.getParameterCount() == 0 && method.getReturnType().equals(String.class)) {
                    String message = (String)method.invoke(object);
                    if (message == null) {
                        continue;
                    }
                    message = localeParser.replacePlaceHolderByLocale(message, localeStr);
                    String setMethodName = method.getName().replace(GET_METHOD_PREFIX, SET_METHOD_PREFIX);
                    try {
                        Method setMethod = objectClass.getMethod(setMethodName, String.class);
                        setMethod.invoke(object, message);
                    } catch (NoSuchMethodException e) {
                        LOGGER.warn("Can not find set method of related get method: {}", method.getName());
                    }
                }
            }
        }
    }


}
