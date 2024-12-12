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

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RoleDefaultTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateLevelEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDepartmentDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateUserDao;
import com.webank.wedatasphere.qualitis.rule.dao.UserRuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDepartment;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUser;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateMidTableInputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateOutputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateStatisticsInputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMidTableInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateOutputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateStatisticsInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class RuleTemplateServiceImpl implements RuleTemplateService {
    @Autowired
    private RuleMetricDao ruleMetricDao;
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

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TemplateDepartmentDao templateDepartmentDao;

    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;

    @Autowired
    private TemplateUserDao templateUserDao;

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
            RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(template);
            List<Integer> types = templateDataSourceTypeDao.findByTemplate(template).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                Collectors.toList());
            ruleTemplateResponse.setDatasourceType(types);
            responseList.add(ruleTemplateResponse);
        }
        response.setData(responseList);

        LOGGER.info("Succeed to get custom rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_CUSTOM_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public Template addCustomTemplate(AddCustomRuleRequest request) {
        Template newTemplate = new Template();
        newTemplate.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        newTemplate.setSaveMidTable(request.getSaveMidTable());
        String sqlCheckArea = request.getSqlCheckArea();
        if (StringUtils.isNotBlank(sqlCheckArea)) {
            newTemplate.setMidTableAction(sqlCheckArea);
            newTemplate.setTemplateType(RuleTemplateTypeEnum.CUSTOM.getCode());
            newTemplate.setActionType(TemplateActionTypeEnum.SQL.getCode());
            Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
            LOGGER.info("Succeed to save custom template, template_id: {}", savedTemplate.getId());
            // Generate statistics input meta by rule metric
            Set<String> ruleMetricCodeSet = request.getAlarmVariable().stream().map(CustomAlarmConfigRequest::getRuleMetricEnCode).collect(Collectors.toSet());
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>(ruleMetricCodeSet.size());
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>(ruleMetricCodeSet.size());
            for (String enCode : ruleMetricCodeSet) {
                // xx_xx_xx_encode, index is 3.
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(enCode);
                templateStatisticsInputMetas.addAll(templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                    ruleMetricInDb.getName(), FunctionTypeEnum.MAX_FUNCTION.getCode(), ruleMetricInDb.getName(), true, savedTemplate));
                templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(ruleMetricInDb.getName(),
                    FunctionTypeEnum.MAX_FUNCTION.getCode(), true, savedTemplate));
            }
            savedTemplate.setStatisticAction(templateStatisticsInputMetas);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            return savedTemplate;
        } else {
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
    }

    @Override
    public void deleteCustomTemplate(Template template) throws UnExpectedRequestException {
        if (template == null) {
            throw new UnExpectedRequestException("Template {&DOES_NOT_EXIST}");
        }
        if (!template.getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
            throw new UnExpectedRequestException("Template(id:[" + template.getId() + "]) {&IS_NOT_A_CUSTOM_TEMPLATE}");
        }
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(template);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }
        ruleTemplateDao.deleteTemplate(template);
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        TemplatePageRequest.checkRequest(request);

        int size = request.getSize();
        int page = request.getPage();
        String dataSourceType = request.getDataSourceType();
        Integer dataSourceTypeCode = TemplateDataSourceTypeEnum.getCode(dataSourceType);

        List<User> users = new ArrayList<>();
        List<Template> templates;
        List<Department> departments = new ArrayList<>();
        long total = 0;
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
            templates = ruleTemplateDao.findAllDefaultTemplate(page, size);
            total = ruleTemplateDao.countAllDefaultTemplate();

        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            for (UserRole userRole : userRoles) {
                Department department = userRole.getRole().getDepartment();
                if (department != null) {
                    departments.add(department);
                    List<User> userList = userDao.findByDepartment(department);
                    users.addAll(userList);
                }
            }

            templates = ruleTemplateDao.findTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode, page, size);
            total = ruleTemplateDao.countTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode);

        } else {
            Department department = userInDb.getDepartment();
            if (department != null) {
                departments.add(department);
            }
            users.add(userInDb);
            templates = ruleTemplateDao.findTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode, page, size);
            total = ruleTemplateDao.countTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.SINGLE_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode);
        }
        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(template);
            List<Integer> types = templateDataSourceTypeDao.findByTemplate(template).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                Collectors.toList());
            ruleTemplateResponse.setDatasourceType(types);
            responseList.add(ruleTemplateResponse);
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
        List<Integer> types = templateDataSourceTypeDao.findByTemplate(templateInDb).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
            Collectors.toList());
        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateMidTableInputMetas, templateOutputMetas, types);

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
            throw new UnExpectedRequestException("Rule template {&DOES_NOT_EXIST}");
        }
        return template;
    }

    private String getMidTableAction(AddCustomRuleRequest request) {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(request.getFunctionContent());
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
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        TemplatePageRequest.checkRequest(request);

        int size = request.getSize();
        int page = request.getPage();
        String dataSourceType = request.getDataSourceType();
        Integer dataSourceTypeCode = TemplateDataSourceTypeEnum.getCode(dataSourceType);

        List<User> users = new ArrayList<>(1);
        List<Department> departments = new ArrayList<>(1);

        List<Template> templates;
        long total = 0;
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
            templates = ruleTemplateDao.findAllMultiTemplate(dataSourceTypeCode, page, size);
            total = ruleTemplateDao.countAllMultiTemplate(dataSourceTypeCode);

        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            for (UserRole userRole : userRoles) {
                Department department = userRole.getRole().getDepartment();
                if (department != null) {
                    departments.add(department);
                    List<User> userList = userDao.findByDepartment(department);
                    users.addAll(userList);
                }
            }

            templates = ruleTemplateDao.findTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode, page, size);
            total = ruleTemplateDao.countTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode);

        } else {
            Department department = userInDb.getDepartment();
            if (department != null) {
                departments.add(department);
            }
            users.add(userInDb);
            templates = ruleTemplateDao.findTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode, page, size);
            total = ruleTemplateDao.countTemplates(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode(), RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(),
                departments.size() == 0 ? null : departments, users.size() == 0 ? null : users, dataSourceTypeCode);
        }

        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(template);
            List<Integer> types = templateDataSourceTypeDao.findByTemplate(template).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                Collectors.toList());
            ruleTemplateResponse.setDatasourceType(types);
            responseList.add(ruleTemplateResponse);
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
    public GeneralResponse<TemplateMetaResponse> getRuleMultiTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException {
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
        List<Integer> types = templateDataSourceTypeDao.findByTemplate(templateInDb).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
            Collectors.toList());
        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateOutputMetas, types);

        LOGGER.info("Succeed to get rule_template, rule template id: {}", ruleTemplateId);
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
        newTemplate.setMidTableAction(request.getMidTableAction());
        newTemplate.setSaveMidTable(request.getSaveMidTable());
        newTemplate.setShowSql(request.getMidTableAction());
        newTemplate.setTemplateType(request.getTemplateType());
        newTemplate.setImportExportName(UuidGenerator.generate());
        // Save create user info.
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        newTemplate.setCreateUser(userInDb);

        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
        for (Integer type : request.getDatasourceType()) {
            TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(type, savedTemplate);
            templateDataSourceTypeDao.save(templateDataSourceType);
        }
        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());
        // Determine the template level and save association table.
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        // Save department template
        if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
            savedTemplate.setLevel(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode());
        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
            savedTemplate.setLevel(RuleTemplateLevelEnum.DEPARTMENT_TEMPLATE.getCode());
            saveTemplateDepartment(ruleTemplateDao.saveTemplate(savedTemplate), userRoles);
        } else {
            savedTemplate.setLevel(RuleTemplateLevelEnum.PERSONAL_TEMPLATE.getCode());
            saveTemplateUser(userInDb, ruleTemplateDao.saveTemplate(savedTemplate));
        }

        // Save template info.
        createAndSaveTemplateInfo(savedTemplate, request);
        return new RuleTemplateResponse(savedTemplate);
    }

    private void saveTemplateDepartment(Template template, List<UserRole> userRoles) {
        for (UserRole temp : userRoles) {
            Department department = temp.getRole().getDepartment();
            if (department != null) {
                TemplateDepartment templateDepartment = new TemplateDepartment();
                templateDepartment.setDepartment(department);
                templateDepartment.setTemplate(template);
                templateDepartmentDao.saveDepartmentTemplate(templateDepartment);
                LOGGER.info("Succeed to save department template.");
            }
        }
    }

    private void saveTemplateUser(User user, Template template) {
        TemplateUser templateUser = new TemplateUser();
        templateUser.setUser(user);
        templateUser.setTemplate(template);
        templateUserDao.saveTemplateUser(templateUser);
        LOGGER.info("Succeed to save user template.");
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
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        AddRuleTemplateRequest addRuleTemplateRequest = ModifyRuleTemplateRequest.checkRequest(request);
        // Check template existence
        Template templateInDb = checkRuleTemplate(request.getTemplateId());
        // Check user info.
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleDefaultTypeEnum.PROJECTOR.getCode())) {
            LOGGER.info("The projector is going to modify template.");
            if (templateInDb.getLevel().intValue() != RuleTemplateLevelEnum.PERSONAL_TEMPLATE.getCode()) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
            if (! userInDb.equals(templateInDb.getCreateUser())) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
            LOGGER.info("The department admin is going to modify template.");
            if (templateInDb.getLevel().intValue() == RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode()) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
            List<User> createUsers = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                Department department = userRole.getRole().getDepartment();
                if (department != null) {
                    List<User> userList = userDao.findByDepartment(department);
                    createUsers.addAll(userList);
                }
            }
            if (! createUsers.contains(templateInDb.getCreateUser())) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        } else {
            LOGGER.info("The system admin is going to modify template.");
        }
        // delete output meta
        templateOutputMetaService.deleteByTemplate(templateInDb);
        // delete mid_table input meta
        templateMidTableInputMetaService.deleteByTemplate(templateInDb);
        // delete statistics input meta
        templateStatisticsInputMetaService.deleteByTemplate(templateInDb);
        // delete template type relationship
        templateDataSourceTypeDao.deleteByTemplate(templateInDb);
        // Save template.
        if (! templateInDb.getName().equals(request.getTemplateName())) {
            checkTemplateName(request.getTemplateName());
        }
        templateInDb.setName(request.getTemplateName());
        templateInDb.setClusterNum(request.getClusterNum());
        templateInDb.setDbNum(request.getDbNum());
        templateInDb.setTableNum(request.getTableNum());
        templateInDb.setFieldNum(request.getFieldNum());
        templateInDb.setActionType(request.getActionType());

        templateInDb.setMidTableAction(request.getMidTableAction());
        templateInDb.setSaveMidTable(request.getSaveMidTable());
        templateInDb.setShowSql(request.getMidTableAction());
        templateInDb.setTemplateType(request.getTemplateType());
        templateInDb.setModifyUser(userInDb);
        Template savedTemplate = ruleTemplateDao.saveTemplate(templateInDb);
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(savedTemplate);
        List<Integer> templateDataSourceTypeIntegers = templateDataSourceTypes.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList());
        for (Integer type : request.getDatasourceType()) {
            if (! templateDataSourceTypeIntegers.contains(type)) {
                TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(type, savedTemplate);
                templateDataSourceTypeDao.save(templateDataSourceType);
            }

        }
        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());
        // Save template info.
        createAndSaveTemplateInfo(savedTemplate, addRuleTemplateRequest);
        return new RuleTemplateResponse(savedTemplate);
    }

    @Override
    public void deleteRuleTemplate(Long templateId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        // Check operator permission
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleDefaultTypeEnum.PROJECTOR.getCode())) {
            LOGGER.info("The projector is going to delete template.");
            if (templateInDb.getLevel().intValue() != RuleTemplateLevelEnum.PERSONAL_TEMPLATE.getCode()) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
            if (userInDb.getId() != (templateInDb.getCreateUser().getId())) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
            LOGGER.info("The department admin is going to delete template.");
            if (templateInDb.getLevel().intValue() == RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode()) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
            List<User> createUsers = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                Department department = userRole.getRole().getDepartment();
                if (department != null) {
                    List<User> userList = userDao.findByDepartment(department);
                    createUsers.addAll(userList);
                }
            }
            boolean exist = false;
            for (User user : createUsers) {
                if (user.getId() == templateInDb.getCreateUser().getId()) {
                    exist = true;
                    break;
                }
            }
            if (! exist) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        } else {
            if (templateInDb.getLevel().equals(RuleTemplateLevelEnum.DEFAULT_TEMPLATE.getCode())) {
                if (templateInDb.getCreateUser() == null || ! userInDb.getUserName().equals(templateInDb.getCreateUser().getUserName())) {
                    throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
                }
            }
            LOGGER.info("The system admin is going to delete template.");
        }

        // Check rules of template
        ruleService.checkRuleOfTemplate(templateInDb);
        // Delete 'Templatedepartment' or 'TemplateUser'
        clearTemplateUser(templateInDb);

        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }

        ruleTemplateDao.deleteTemplate(templateInDb);
    }

    @Override
    public void deleteFileRuleTemplate(Long templateId) throws UnExpectedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        ruleTemplateDao.deleteTemplate(templateInDb);
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }
    }

    private void clearTemplateUser(Template templateInDb) {
        TemplateUser templateUser = templateUserDao.findByTemplate(templateInDb);
        if (templateUser != null) {
            templateUserDao.delete(templateUser);
        }
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
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        response.setDatasourceType(templateDataSourceTypes.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList()));
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

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public Template addFileTemplate(AddFileRuleRequest request) {
        Template newTemplate = new Template();
        newTemplate.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        newTemplate.setTemplateType(RuleTemplateTypeEnum.FILE_COUSTOM.getCode());
        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
        LOGGER.info("Succeed to save file custom template, template_id: {}", savedTemplate.getId());
        return savedTemplate;
    }

}
