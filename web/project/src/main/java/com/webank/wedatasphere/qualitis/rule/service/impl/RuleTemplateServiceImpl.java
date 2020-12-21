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
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateMidTableInputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateOutputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateStatisticsInputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMidTableInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateOutputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateStatisticsInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
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
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private RuleService ruleService;

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
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_SUCCESSFULLY}", response);
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
        if (request.getFunctionContent().equals("*")) {
            sb.append(request.getFunctionContent());
        } else {
            sb.append(request.getFunctionContent()).append(" AS ").append(getFunctionAlias(request.getFunctionType()));
        }
//
//        if (request.getSaveMidTable()) {
//        } else {
//            sb.append(FunctionTypeEnum.getByCode(request.getFunctionType()).getFunction())
//                    .append("(")
//                    .append(request.getFunctionContent())
//                    .append(") AS ")
//                    .append(getFunctionAlias(request.getFunctionType()));
//        }
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

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public RuleTemplateResponse addRuleTemplate(AddRuleTemplateRequest request)
        throws UnExpectedRequestException {
        AddRuleTemplateRequest.checkRequest(request, false);
        LOGGER.info("Add default rule template request detail: {}", request.toString());

        // Save template.
        checkTemplateName(request.getTemplateName());
        Template newTemplate = new Template();
        newTemplate.setName(request.getTemplateName());
        newTemplate.setClusterNum(request.getClusterNum());
        newTemplate.setDbNum(request.getDbNum());
        newTemplate.setTableNum(request.getTableNum());
        newTemplate.setFieldNum(request.getFieldNum());
        newTemplate.setActionType(request.getActionType());
        newTemplate.setDatasourceType(request.getDatasourceType());
        newTemplate.setMidTableAction(request.getMidTableAction());
        newTemplate.setSaveMidTable(request.getSaveMidTable());
        newTemplate.setShowSql(request.getMidTableAction());
        newTemplate.setTemplateType(request.getTemplateType());

        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());
        createAndSaveTemplateInfo(savedTemplate, request);
        return new RuleTemplateResponse(savedTemplate);
    }

    private void createAndSaveTemplateInfo(Template savedTemplate, AddRuleTemplateRequest request) {
        // Save template output meta.
        Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>();
        for (TemplateOutputMetaRequest templateOutputMetaRequest : request.getTemplateOutputMetaRequests()) {
            templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(templateOutputMetaRequest.getOutputName(),
                FunctionTypeEnum.getFunctionTypeByName(templateOutputMetaRequest.getFieldName()),
                request.getSaveMidTable(), savedTemplate));
        }
        savedTemplate.setTemplateOutputMetas(templateOutputMetas);
        LOGGER.info("Success to save template output meta. TemplateOutputMetas: {}", savedTemplate.getTemplateOutputMetas());

        // Save template mid_table input meta
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = new ArrayList<>();
        for (TemplateMidTableInputMetaRequest  templateMidTableInputMetaRequest : request.getTemplateMidTableInputMetaRequests()) {
            TemplateMidTableInputMeta templateMidTableInputMeta = new TemplateMidTableInputMeta();
            templateMidTableInputMeta.setName(templateMidTableInputMetaRequest.getName());
            templateMidTableInputMeta.setFieldType(templateMidTableInputMetaRequest.getFieldType());
            templateMidTableInputMeta.setInputType(templateMidTableInputMetaRequest.getInputType());
            templateMidTableInputMeta.setPlaceholder(templateMidTableInputMetaRequest.getPlaceholder());
            templateMidTableInputMeta.setPlaceholderDescription(templateMidTableInputMetaRequest.getPlaceholderDescription());
            templateMidTableInputMeta.setRegexpType(templateMidTableInputMetaRequest.getRegexpType());
            templateMidTableInputMeta.setReplaceByRequest(templateMidTableInputMetaRequest.getReplaceByRequest());
            templateMidTableInputMeta.setTemplate(savedTemplate);
            templateMidTableInputMetas.add(templateMidTableInputMeta);
        }
        savedTemplate.setTemplateMidTableInputMetas(templateMidTableInputMetaService.saveAll(templateMidTableInputMetas));
        LOGGER.info("Success to save template mid_table input meta. TemplateMidTableInputMetas: {}", savedTemplate.getTemplateMidTableInputMetas());

        // Save template statistics input meta
        List<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new ArrayList<>();
        for (TemplateStatisticsInputMetaRequest templateStatisticsInputMetaRequest : request.getTemplateStatisticsInputMetaRequests()) {
            TemplateStatisticsInputMeta templateStatisticsInputMeta = new TemplateStatisticsInputMeta();
            templateStatisticsInputMeta.setName(templateStatisticsInputMetaRequest.getName());
            templateStatisticsInputMeta.setFuncName(templateStatisticsInputMetaRequest.getFuncName());
            templateStatisticsInputMeta.setResultType(templateStatisticsInputMetaRequest.getResultType());
            templateStatisticsInputMeta.setValue(templateStatisticsInputMetaRequest.getValue());
            templateStatisticsInputMeta.setValueType(templateStatisticsInputMetaRequest.getValueType());
            templateStatisticsInputMeta.setTemplate(savedTemplate);
            templateStatisticsInputMetas.add(templateStatisticsInputMeta);
        }
        savedTemplate.setStatisticAction(templateStatisticsInputMetaService.saveAll(templateStatisticsInputMetas));
        LOGGER.info("Success to save template statistics input meta. templateStatisticsInputMetas: {}", savedTemplate.getStatisticAction());
    }
    private void checkTemplateName(String templateName) throws UnExpectedRequestException {
        List<Template> templates = ruleTemplateDao.getAllTemplate();
        LOGGER.info("Number of templates in database is {}", templates.size());
        for (Template template : templates) {
            if (templateName.equals(template.getName())) {
                throw new UnExpectedRequestException("Template name {&ALREADY_EXIST}");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleTemplateResponse modifyRuleTemplate(ModifyRuleTemplateRequest request)
        throws UnExpectedRequestException, InvocationTargetException, IllegalAccessException {
        AddRuleTemplateRequest addRuleTemplateRequest = ModifyRuleTemplateRequest.checkRequest(request, "modifyDefaultRuleTemplateRequest");
        // Check template existence
        Template templateInDb = checkRuleTemplate(request.getTemplateId());
        // delete output meta
        templateOutputMetaService.deleteByTemplate(templateInDb);
        // delete mid_table input meta
        templateMidTableInputMetaService.deleteByTemplate(templateInDb);
        // delete statistics input meta
        templateStatisticsInputMetaService.deleteByTemplate(templateInDb);
        // Save template.
        templateInDb.setClusterNum(request.getClusterNum());
        templateInDb.setDbNum(request.getDbNum());
        templateInDb.setTableNum(request.getTableNum());
        templateInDb.setFieldNum(request.getFieldNum());
        templateInDb.setActionType(request.getActionType());
        templateInDb.setDatasourceType(request.getDatasourceType());
        templateInDb.setMidTableAction(request.getMidTableAction());
        templateInDb.setSaveMidTable(request.getSaveMidTable());
        templateInDb.setShowSql(request.getMidTableAction());
        templateInDb.setTemplateType(request.getTemplateType());

        Template savedTemplate = ruleTemplateDao.saveTemplate(templateInDb);
        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());


        createAndSaveTemplateInfo(savedTemplate, addRuleTemplateRequest);
        return new RuleTemplateResponse(savedTemplate);
    }

    @Override
    public void deleteRuleTemplate(Long templateId) throws UnExpectedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        // Check rules of template
        ruleService.checkRuleOfTemplate(templateInDb);
        ruleTemplateDao.deleteTemplate(templateInDb);
    }

    @Override
    public RuleTemplateResponse getModifyRuleTemplateDetail(Long templateId) throws UnExpectedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        RuleTemplateResponse response = new RuleTemplateResponse(templateInDb);
        response.setClusterNum(templateInDb.getClusterNum());
        response.setDbNum(templateInDb.getDbNum());
        response.setTableNum(templateInDb.getTableNum());
        response.setFieldNum(templateInDb.getFieldNum());
        response.setDatasourceType(templateInDb.getDatasourceType());
        response.setActionType(templateInDb.getActionType());
        response.setMidTableAction(templateInDb.getMidTableAction());
        response.setSaveMidTable(templateInDb.getSaveMidTable());
        List<TemplateOutputMetaResponse> outputMetaResponses = new ArrayList<>(1);
        List<TemplateMidTableInputMetaResponse> midTableInputMetaResponses = new ArrayList<>(2);
        List<TemplateStatisticsInputMetaResponse> statisticsInputMetaResponses = new ArrayList<>(1);
        for (TemplateOutputMeta templateOutputMeta : templateInDb.getTemplateOutputMetas()) {
            TemplateOutputMetaResponse templateOutputMetaResponse = new TemplateOutputMetaResponse();
            templateOutputMetaResponse.setOutputName(templateOutputMeta.getOutputName());
            outputMetaResponses.add(templateOutputMetaResponse);
        }
        response.setTemplateOutputMetaResponses(outputMetaResponses);
        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateInDb.getTemplateMidTableInputMetas()) {
            TemplateMidTableInputMetaResponse templateMidTableInputMetaResponse = new TemplateMidTableInputMetaResponse();
            templateMidTableInputMetaResponse.setName(templateMidTableInputMeta.getName());
            templateMidTableInputMetaResponse.setPlaceholder(templateMidTableInputMeta.getPlaceholder());
            templateMidTableInputMetaResponse.setPlaceholderDescription(templateMidTableInputMeta.getPlaceholderDescription());
            templateMidTableInputMetaResponse.setInputType(templateMidTableInputMeta.getInputType());
            midTableInputMetaResponses.add(templateMidTableInputMetaResponse);
        }
        response.setTemplateMidTableInputMetaResponses(midTableInputMetaResponses);
        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : templateInDb.getStatisticAction()) {
            TemplateStatisticsInputMetaResponse templateStatisticsInputMetaResponse = new TemplateStatisticsInputMetaResponse();
            templateStatisticsInputMetaResponse.setName(templateStatisticsInputMeta.getName());
            templateStatisticsInputMetaResponse.setFuncName(templateStatisticsInputMeta.getFuncName());
            templateStatisticsInputMetaResponse.setValue(templateStatisticsInputMeta.getValue());
            templateStatisticsInputMetaResponse.setValueType(templateStatisticsInputMeta.getValueType());
            statisticsInputMetaResponses.add(templateStatisticsInputMetaResponse);
        }
        response.setTemplateStatisticsInputMetaResponses(statisticsInputMetaResponses);
        return response;
    }
}
