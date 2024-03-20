import { request as FRequest } from '@fesjs/fes';

// 获取规则查询表单 下拉选项数据
export function fetchOptions() {
    return FRequest('/api/v1/projector/query/conditions', {}, 'get');
}

// 规则查询 查询接口
export function fetchRuleQueryData(params = {}) {
    return FRequest('/api/v1/projector/query/query', params);
}

// 获取规则查询表单 全部查询接口
export function fetchAllRuleQueryData(params = {}) {
    return FRequest('/api/v1/projector/query/all', params);
}

// 获取表详情字段列表
export function fetchColumnsOfTableDetail(params = {}) {
    return FRequest('/api/v1/projector/query/columns', params);
}

// 获取子系统列表
export function fetchSubSystemInfo(params = {}) {
    return Promise.resolve([]);
}

// 获取关联规则
// 修改为表规则、字段规则查询
export function fetctRelatedRulesOfTableDetail(params = {}) {
    return FRequest('/api/v1/projector/query/rules', params);
}

// 批量导出规则
export function downloadRelatedRulesOfTableDetail(params = {}) {
    return FRequest('/api/v1/projector/rule/batch/download', params, {
        useResponse: true,
        responseType: 'blob',
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
    });
}

// 获取校验模板数据
export function fetctRuleTemplateList() {
    return FRequest('/api/v1/projector/rule_template/option/list', {}, 'get');
}

// 获取数据标签数据
export function fetctdataLabels() {
    return FRequest('/api/v1/projector/query/tags', {});
}

// 元数据同步
export function fetctmetadataSync() {
    return FRequest('/api/v1/projector/rule/datasource/metadata/sync', {}, 'get');
}

// 血缘拼接参数查询
export function fetctBloodParams(params = {}) {
    return FRequest('/api/v1/projector/query/lineage/parameter', params);
}
