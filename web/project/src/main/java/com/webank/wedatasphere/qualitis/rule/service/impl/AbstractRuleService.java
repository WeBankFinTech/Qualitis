package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author v_minminghe@webank.com
 * @date 2023-02-23 14:40
 * @description
 */
public abstract class AbstractRuleService {

    @Autowired
    private ProjectEventService projectEventService;

    private final String CONTENT_FORMAT = "规则名称: %s, 规则ID: %d";

    /**
     * record operation of user or outer
     * @param loginUser
     * @param rule
     * @param operateTypeEnum
     */
    protected void recordEvent(String loginUser, Rule rule, OperateTypeEnum operateTypeEnum) {
        String content = String.format(CONTENT_FORMAT, rule.getName(), rule.getId());
        projectEventService.record(rule.getProject(), loginUser, content, operateTypeEnum);
    }

}
