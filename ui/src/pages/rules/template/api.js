import { request as FRequest } from '@fesjs/fes';

// 获取模板列表 根据template_type确认类型 1 单表 2 单/多指标 3跨表 4文件
export function fetchTemplates(params = {}) {
    return FRequest('/api/v1/projector/rule_template/default/all', params);
}

// 添加规则模板 根据template_type确认类型 1 单表 2 单/多指标 3跨表 4文件
export function addTemplate(params = {}) {
    return FRequest('/api/v1/projector/rule_template/default/add', params);
}

// 编辑规则模板 根据template_type确认类型 1 单表 2 单/多指标 3跨表 4文件
export function editTemplate(params = {}) {
    return FRequest('/api/v1/projector/rule_template/default/modify', params);
}

// 获取规则模板详情
export function fetchTemplateDetail(params = {}) {
    return FRequest(`/api/v1/projector/rule_template/modify/detail/${params.template_id}`, {}, 'get');
}
// 删除规则模板
export function deleteTemplate(params = {}) {
    return FRequest(`/api/v1/projector/rule_template/default/delete/${params.template_id}`, {});
}

// 获取跨表校验模板列表
export function fetchMultipleTemplates(params = {}) {
    return FRequest('/api/v1/projector/rule_template/default/all', { ...params, template_type: 3 });
}

// 获取用户部门信息
export function fetchUserDeparment(params = {}) {
    return FRequest('/api/v1/projector/login_user/info', params);
}

// 获取规则部门列表
export function fetchRuleDepartments(params = {}) {
    return FRequest('/api/v1/projector/meta_data/system/departmentInfoWithRole', params, { cache: true });
}

// 根据规则部门代码获取科室列表
export function fetchRuleDepartmentsDivisionsByCode(params = '') {
    return FRequest(`/api/v1/projector/meta_data/system/devAndOpsInfo/${params}`, {}, { cache: true, method: 'get' });
}

// 获取校验类型
export function fetchVerificationType(params = {}) {
    return FRequest('/api/v1/projector/rule_template/check/type/list', params);
}
// 获取校验级别
export function fetchVerificationLevel(params = {}) {
    return FRequest('/api/v1/projector/rule_template/check/level/list', params);
}
// 获取占位符类型
export function fetchInputType(params = {}) {
    return FRequest('/api/v1/projector/rule_template/action/type/list', params);
}
// 获取采样内容
export function fetchActionContent(params = {}) {
    return FRequest('/api/v1/projector/rule_template/file/type/list', params);
}
// 获取统计函数枚举列表
export function fetchCountFunction(params = {}) {
    return FRequest('/api/v1/projector/rule_template/statistical/function/list', params);
}

// 获取占位符数据
export function fetchPlaceholders(params = {}) {
    return FRequest(`/api/v1/projector/rule_template/placeholder/list/${params.template_type}`, {});
}

// 获取用户列表
export function fetchUserList(params = {}) {
    return FRequest('/api/v1/projector/project_user/user', params, { method: 'GET' });
}
// 获取命名方式列表
export function fetchNamingTypeList(params = {}) {
    return FRequest('/api/v1/projector/rule_template/naming/method/list', params, { method: 'POST', cache: true });
}
// 获取模板大类、类别列表
export function fetchTemplateTypeList(params = {}) {
    return FRequest('/api/v1/projector/rule_template/naming/conventions/config', params, { method: 'POST', cache: true });
}
