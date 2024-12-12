package com.webank.wedatasphere.qualitis.timer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/10 11:30
 */
public class RuleBashThreadResponse {
    private List<Long> ruleIdList;
    private StringBuilder exceptionMess;

    public RuleBashThreadResponse() {
        ruleIdList = new ArrayList<>();
        exceptionMess = new StringBuilder();
    }

    public List<Long> getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List<Long> ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public StringBuilder getExceptionMess() {
        return exceptionMess;
    }

    public void setExceptionMess(StringBuilder exceptionMess) {
        this.exceptionMess = exceptionMess;
    }
}
