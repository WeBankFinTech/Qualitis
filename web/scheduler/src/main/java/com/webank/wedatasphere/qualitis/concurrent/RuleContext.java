package com.webank.wedatasphere.qualitis.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-19 9:49
 * @description
 */
public class RuleContext {

    private Long ruleId;
    private AtomicReference<String> ruleContextIdAtomic;

    public RuleContext(Long ruleId, String ruleContextId) {
        this.ruleId = ruleId;
        this.ruleContextIdAtomic = new AtomicReference<>(ruleContextId);
    }

    public Long getRuleId() {
        return ruleId;
    }

    public String getRuleContextId() {
        return ruleContextIdAtomic.get();
    }

    public boolean isConsistent(RuleContext ruleContext) {
        String ruleContextId = ruleContext.getRuleContextId();
        return this.ruleContextIdAtomic.compareAndSet(ruleContextId, ruleContextId);
    }

    @Override
    public String toString() {
        return "RuleContext{" +
                "ruleId=" + ruleId +
                ", ruleContextId=" + ruleContextIdAtomic.get() +
                '}';
    }
}
