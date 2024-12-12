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

package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Enum in file output name of AlarmConfig
 * @author howeye
 */
public enum FileOutputNameEnum {
    /**
     * file count, dir size.
     */
    FILE_COUNT(1,"文件数", "file count", Arrays.asList(Number.class)),
    DIR_SIZE(2,"文件目录大小", "dir size", Arrays.asList(Number.class))
        ;

    private Integer code;
    private String zhMessage;
    private String enMessage;
    private List<Class> classes;

    FileOutputNameEnum(Integer code, String zhMessage, String enMessage, List<Class> classes) {
        this.code = code;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
        this.classes = classes;
    }

    public Integer getCode() {
        return code;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public static String getFileOutputName(Integer code, String local) {
        for (FileOutputNameEnum c : FileOutputNameEnum.values()) {
            if (c.getCode().equals(code)) {
                if (local != null && "en_US".equals(local)) {
                    return c.getEnMessage();
                } else {
                    return c.getZhMessage();
                }
            }
        }
        return null;
    }

    public static Integer getFileOutputNameCode(String checkTemplateName, String local) {
        for (FileOutputNameEnum c : FileOutputNameEnum.values()) {
            if (local != null && "en_US".equals(local)) {
                if (c.getEnMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            } else {
                if (c.getZhMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            }
        }
        return null;
    }
}
