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

package com.webank.wedatasphere.qualitis.bean;

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class LogResult {

    private String log;
    private Integer begin;
    private Integer end;
    private Boolean last;

    private String infoString;
    private String warnString;
    private String errorString;

    private Integer infoCount;
    private Integer warnCount;
    private Integer errorCount;

    private Long taskId;
    private String zhMessage;
    private String enMessage;

    public LogResult() {
    }

    public LogResult(String log, Integer begin, Integer end, Boolean last) {
        this.log = log;
        this.begin = begin;
        this.end = end;
        this.last = last;

        StringBuilder info = new StringBuilder();
        StringBuilder warn = new StringBuilder();
        StringBuilder error = new StringBuilder();

        Integer infoAmount = 0;
        Integer warnAmount = 0;
        Integer errorAmount = 0;

        if (StringUtils.isNotBlank(log)) {
            String[] logSplit = log.split("\\n");

            for (String str : logSplit) {
                if (str.contains(QualitisConstants.LOG_INFO)) {
                    info.append(str).append("\n");
                    infoAmount++;
                } else if (str.contains(QualitisConstants.LOG_WARN)) {
                    warn.append(str).append("\n");
                    warnAmount++;
                } else if (str.contains(QualitisConstants.LOG_ERROR)) {
                    error.append(str).append("\n");
                    errorAmount++;
                } else {
                    if (str.startsWith("\tat") || str.startsWith("  at ") || str.startsWith("Caused by: ")) {
                        error.append(str).append("\n");
                        errorAmount++;
                    } else {
                        info.append(str).append("\n");
                        infoAmount++;
                    }
                }
            }
        }
        this.infoString = info.toString();
        this.warnString = warn.toString();
        this.errorString = error.toString();

        this.infoCount = infoAmount;
        this.warnCount = warnAmount;
        this.errorCount = errorAmount;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public String getWarnString() {
        return warnString;
    }

    public void setWarnString(String warnString) {
        this.warnString = warnString;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public Integer getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(Integer infoCount) {
        this.infoCount = infoCount;
    }

    public Integer getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(Integer warnCount) {
        this.warnCount = warnCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public void setZhMessage(String zhMessage) {
        this.zhMessage = zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public void setEnMessage(String enMessage) {
        this.enMessage = enMessage;
    }
}
