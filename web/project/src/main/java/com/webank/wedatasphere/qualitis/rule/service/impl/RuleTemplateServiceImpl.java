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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.UserRuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.UserRuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
@Service
public class RuleTemplateServiceImpl implements RuleTemplateService {

    @Autowired
    private RuleTemplateDao ruleTemplateDao;

    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;

    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;

    @Autowired
    private UserRuleTemplateDao userRuleTemplateDao;

    @Autowired
    private RegexpExprMapperRepository regexpExprMapperRepository;

    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleTemplateServiceImpl.class);

    public RuleTemplateServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getCustomRuleTemplateByUser(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Get userId
        Long userId = HttpUtils.getUserId(httpServletRequest);

        int size = request.getSize();
        int page = request.getPage();
        List<Template> templates = userRuleTemplateDao.findByUserId(userId, page, size);
        long total = userRuleTemplateDao.countByUserId(userId);
        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            responseList.add(new RuleTemplateResponse(template));
        }
        response.setData(responseList);

        LOGGER.info("Succeed to get custom rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_CUSTOM_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int size = request.getSize();
        int page = request.getPage();
        List<Template> templates = ruleTemplateDao.findAllDefaultTemplate(page, size);
        long total = ruleTemplateDao.countAllDefaultTemplate();
        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            responseList.add(new RuleTemplateResponse(template));
        }
        response.setData(responseList);

        LOGGER.info("Succeed to find default rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_DEFAULT_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException {
        // Find rule template by id
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Find input meta data by template
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = templateMidTableInputMetaDao.findByRuleTemplate(templateInDb);
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(templateInDb);

        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateMidTableInputMetas, templateOutputMetas);

        LOGGER.info("Succeed to get rule_template. rule_template_id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_META_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<TemplateInputDemandResponse> getRuleTemplateInputMeta(Long ruleTemplateId) throws UnExpectedRequestException {
        // Check existence of rule template
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&DOES_NOT_EXIST}");
        }

        TemplateInputDemandResponse templateInputDemandResponse = new TemplateInputDemandResponse(templateInDb, regexpExprMapperRepository);
        LOGGER.info("Succeed to get the input of rule_template. rule_template_id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_TEMPLATE_RULE_DEMAND_SUCCESSFULLY}", templateInputDemandResponse);
    }

    @Override
    public Template checkRuleTemplate(Long ruleTemplateId) throws UnExpectedRequestException {
        Template template =  ruleTemplateDao.findById(ruleTemplateId);
        if (template == null) {
            throw new UnExpectedRequestException("rule_template_id {&DOES_NOT_EXIST}");
        }
        return template;
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public Template addCustomTemplate(AddCustomRuleRequest request) {
        Template newTemplate = new Template();
        newTemplate.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        newTemplate.setDatasourceType(TemplateDataSourceTypeEnum.HIVE.getCode());
        newTemplate.setSaveMidTable(request.getSaveMidTable());
        newTemplate.setMidTableAction(getMidTableAction(request));
        newTemplate.setTemplateType(RuleTemplateTypeEnum.CUSTOM.getCode());
        newTemplate.setActionType(TemplateActionTypeEnum.SQL.getCode());
        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
        LOGGER.info("Succeed to save custom template, template_id: {}", savedTemplate.getId());

        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                request.getOutputName(), request.getFunctionType(), request.getFunctionContent(), request.getSaveMidTable(), savedTemplate);
        savedTemplate.setStatisticAction(templateStatisticsInputMetas);

        Set<TemplateOutputMeta> templateOutputMetas = templateOutputMetaService.getAndSaveTemplateOutputMeta(request.getOutputName(),
                request.getFunctionType(), request.getSaveMidTable(), savedTemplate);
        savedTemplate.setTemplateOutputMetas(templateOutputMetas);

        return savedTemplate;
    }

    private String getMidTableAction(AddCustomRuleRequest request) {
        StringBuilder sb = new StringBuilder("SELECT ");
        if (request.getSaveMidTable()) {
            sb.append(request.getFunctionContent());
        } else {
            sb.append(FunctionTypeEnum.getByCode(request.getFunctionType()).getFunction())
                    .append("(")
                    .append(request.getFunctionContent())
                    .append(") AS ")
                    .append(getFunctionAlias(request.getFunctionType()));
        }
        sb.append(" FROM ").append(request.getFromContent()).append(" WHERE ").append("${filter}");
        // Check SQL grammar
        String sql = sb.toString();
        return sql;
    }

    @Override
    public String getFunctionAlias(Integer code) {
        return "my" + FunctionTypeEnum.getByCode(code).getFunction();
    }

    @Override
    public void deleteCustomTemplate(Template template) throws UnExpectedRequestException {
        if (template == null) {
            throw new UnExpectedRequestException("Template {&DOES_NOT_EXIST}");
        }
        if (!template.getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
            throw new UnExpectedRequestException("Template(id:[" + template.getId() + "]) {&IS_NOT_A_CUSTOM_TEMPLATE}");
        }

        ruleTemplateDao.deleteTemplate(template);
    }

    @Override
    public GeneralResponse<?> getMultiSourceTemplateMeta(Long templateId) throws UnExpectedRequestException {
        // Check existence of template and if multi-table rule template
        Template templateInDb = checkRuleTemplate(templateId);
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template : [" + templateId + "] {&IS_NOT_A_MULTI_TEMPLATE}");
        }

        // return show_sql of template and template output
        TemplateInputDemandResponse templateInputDemandResponse = new TemplateInputDemandResponse(templateInDb, RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode());
        LOGGER.info("Succeed to get the meta of multi_rule_template. rule_template_id: {}", templateId);
        return new GeneralResponse<>("200", "{&GET_META_OF_MULTI_RULE_TEMPLATE_SUCCESSFULLY}", templateInputDemandResponse);
    }

    /**
     * Paging get template
     * @param request
     * @return
     */
    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(
        PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int size = request.getSize();
        int page = request.getPage();
        List<Template> templates = ruleTemplateDao.findAllMultiTemplate(page, size);
        long total = ruleTemplateDao.countAllMultiTemplate();
        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            responseList.add(new RuleTemplateResponse(template));
        }
        response.setData(responseList);

        LOGGER.info("Succeed to find multi rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_MULTI_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    /**
     * Get meta data information by template_id
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    public GeneralResponse<TemplateMetaResponse> getRuleMultiTemplateMeta(Long ruleTemplateId)
        throws UnExpectedRequestException {
        // Find rule template by rule template id
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Find meta data of template
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(templateInDb);
        // Add child template
        if (templateInDb.getChildTemplate() != null) {
            templateOutputMetas.addAll(templateOutputMetaDao.findByRuleTemplate(templateInDb.getChildTemplate()));
        }

        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateOutputMetas);

        LOGGER.info("Succeed to get rule_template. rule_template_id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_META_SUCCESSFULLY}", response);
    }
}
