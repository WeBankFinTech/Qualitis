package com.webank.wedatasphere.qualitis.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-11 18:10
 * @description
 * 背景：多线程并发请求createOrModifyAndSubmitRule，且是修改同一个Rule，过程中会经历两个阶段：修改规则阶段（其他分支不考虑）、提交规则阶段。
 *      修改规则阶段会修改Rule，提交规则阶段会修改RuleDataSource。
 * 冲突：由于没有采用事务，可能导致，最终更新落库的Rule和RuleDataSource并非由同一个线程操作。
 * 解决：在操作规则阶段结束后，采用RuleContextManager保存最后一次更新落库Rule的线程；
 *      当流程进入到提交规则阶段后，判断当前线程是否存在于RuleContextManager，存在则更新RuleDataSource，不存在则略过。
 * 功能设计文档：***REMOVED***
 */
public class RuleContextManager {

    /**
     * key: ruleId
     */
    private static ConcurrentMap<Long, RuleContext> RULE_CONTEXT_MAP = new ConcurrentHashMap<>();

    public static void add(RuleContext ruleContext) {
        RULE_CONTEXT_MAP.put(ruleContext.getRuleId(), ruleContext);
    }

    public static void remove(RuleContext ruleContext) {
        if (checkIfLatest(ruleContext)) {
            RULE_CONTEXT_MAP.remove(ruleContext.getRuleId());
        }
    }

    public static boolean checkIfLatest(RuleContext ruleContext) {
        return RULE_CONTEXT_MAP.containsKey(ruleContext.getRuleId())
                && RULE_CONTEXT_MAP.get(ruleContext.getRuleId()).isConsistent(ruleContext);
    }

}
