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

package com.webank.wedatasphere.qualitis.parser;

import com.webank.wedatasphere.qualitis.LocalConfig;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author howeye
 */
@Component
public class LocaleParser {
    private static final Pattern KEY_WORD_PATTERN = Pattern.compile("\\{&.*?}");
    private static final String EN = "en";

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleParser.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocalConfig localConfig;

    public String replacePlaceHolderByLocale(String message, String localeStr) {
        if (StringUtils.isBlank(message)) {
            return "";
        }
        Locale locale;
        try {
            if (StringUtils.isNotBlank(localeStr)) {
                locale = LocaleUtils.toLocale(localeStr);
            } else {
                if (localConfig.getLocal() != null) {
                    if (localConfig.getLocal().equals(EN)) {
                        locale = Locale.US;
                    } else {
                        locale = Locale.CHINA;
                    }
                } else {
                    locale = Locale.CHINA;
                }
            }
        } catch (Exception e) {
            if (localConfig.getLocal() != null) {
                if (localConfig.getLocal().equals(EN)) {
                    locale = Locale.US;
                } else {
                    locale = Locale.CHINA;
                }
            } else {
                locale = Locale.CHINA;
            }
        }
        Matcher m = KEY_WORD_PATTERN.matcher(message);
        try {
            while (m.find()) {
                String replaceStr = m.group();
                String tmp = messageSource.getMessage(replaceStr, null, locale);
                message = message.replace(replaceStr, tmp);
            }
        } catch (Exception e) {
            LOGGER.warn("Can not replace message: {}, caused by: {}", message, e.getMessage(), e);
        }
        return message;
    }

}
