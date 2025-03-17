package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVariablesDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVariables;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import com.webank.wedatasphere.qualitis.rule.request.StandardValueVariableRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-02-23 14:40
 * @description
 */
public abstract class AbstractRuleService {

    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private StandardValueVariablesDao standardValueVariablesDao;
    @Autowired
    private StandardValueVersionDao standardValueVersionDao;

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


    protected void saveStandardValueVariables(Rule savedRule, List<StandardValueVariableRequest> standardValueVariableRequests) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(standardValueVariableRequests)) {
            return;
        }
        List<StandardValueVariables> standardValueVariablesListInDb = standardValueVariablesDao.findByRuleId(savedRule.getId());
        if (CollectionUtils.isNotEmpty(standardValueVariablesListInDb)) {
            standardValueVariablesDao.deleteAll(standardValueVariablesListInDb);
        }
        List<StandardValueVariables> standardValueVariablesList = Lists.newArrayListWithCapacity(standardValueVariableRequests.size());
        for (StandardValueVariableRequest standardValueVariableRequest: standardValueVariableRequests) {
            Long standardValueVariablesId = standardValueVariableRequest.getStandardValueVariablesId();
            String standardValueVersionVariablesName = standardValueVariableRequest.getStandardValueVersionVariablesName();
            StandardValueVariables standardValueVariables = new StandardValueVariables();
//        Tips: The standardValueVariablesId will be null if the request from bdp-client
            if (standardValueVariablesId == null) {
                StandardValueVersion standardValueVersion = standardValueVersionDao.findLatestVersion(standardValueVariableRequest.getStandardValueVersionVariablesName());
                if (standardValueVersion == null) {
                    throw new UnExpectedRequestException("Standard value ${DOES_NOT_EXIST}");
                }
                standardValueVariablesId = standardValueVersion.getId();
            } else {
                StandardValueVersion standardValueVersion = standardValueVersionDao.findById(standardValueVariablesId);
                standardValueVersionVariablesName = standardValueVersion != null ? standardValueVersion.getEnName() : "";
            }
            standardValueVariables.setRuleId(savedRule.getId());
            standardValueVariables.setStandardValueVersionId(standardValueVariablesId);
            standardValueVariables.setStandardValueVersionEnName(standardValueVersionVariablesName);
            standardValueVariablesList.add(standardValueVariables);
        }
        standardValueVariablesDao.saveAll(standardValueVariablesList);
    }

}
