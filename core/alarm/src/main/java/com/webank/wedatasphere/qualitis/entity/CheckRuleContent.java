package com.webank.wedatasphere.qualitis.entity;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/27 15:05
 */
public class CheckRuleContent {
    private String resultThreshold;
    private String gran;
    private String range;
    private String rules;
    private String ruleStr;

    public CheckRuleContent() {
        // do nothing
    }

    public CheckRuleContent(String resultThreshold, String gran, String range) {
        this.resultThreshold = resultThreshold;
        this.gran = gran;
        this.range = range;
    }

    public CheckRuleContent(String resultThreshold, String gran, String range, String rules) {
        this.resultThreshold = resultThreshold;
        this.gran = gran;
        this.range = range;
        this.rules = rules;
    }

    public String getResultThreshold() {
        return resultThreshold;
    }

    public void setResultThreshold(String resultThreshold) {
        this.resultThreshold = resultThreshold;
    }

    public String getGran() {
        return gran;
    }

    public void setGran(String gran) {
        this.gran = gran;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getRuleStr() {
        return ruleStr;
    }

    public void setRuleStr(String ruleStr) {
        this.ruleStr = ruleStr;
    }
}
