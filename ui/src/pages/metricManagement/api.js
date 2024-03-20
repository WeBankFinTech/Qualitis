import { request as FRequest } from '@fesjs/fes';

// 指标管理 获取初始化数据（下拉选项数据）
export function fetchOptions() {
    return FRequest('/api/v1/projector/rule_metric/condition', {});
}

// 获取指标列表
export function fetchMetrics(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/query', params);
}

// 导入指标
export function uploadMetric(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/upload', params, {
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
    });
}

// 导出指标
export function downloadMetric(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/download', params, {
        useResponse: true,
        responseType: 'blob',
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
    });
}

// 获取子系统列表
export function fetchSubSystemInfo(params = {}) {
    return Promise.resolve([]);
}

// 获取产品列表
export function fetchProductInfo(params = {}) {
    return Promise.resolve([]);
}

// 获取部门列表
export function fetchDepartments(params = {}) {
    return FRequest('/api/v1/projector/meta_data/system/departmentInfo', params, { cache: true });
}

// 根据部门代码获取科室列表
export function fetchDepartmentsDivisionsByCode(params = '') {
    return FRequest(`/api/v1/projector/meta_data/system/devAndOpsInfo/${params}`, {}, { cache: true, method: 'get' });
}

// 删除指标
export function deleteMetric(id, params = {}) {
    return FRequest(`/api/v1/projector/rule_metric/delete/${id}`, params);
}

// 批量删除指标
export function deleteMetrics(params) {
    return FRequest('/api/v1/projector/rule_metric/delete/batch', params);
}

// 新增指标
export function addMetric(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/add', params);
}

// 编辑指标
export function modifyMetric(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/modify', params);
}

// 查询规则
export function findRule(id, params = {}) {
    return FRequest(`/api/v1/projector/rule_metric/rules/${id}`, params);
}

// 拉取历史值
export function getHistory(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/rule_metric_value', params);
}

// 批量可视化
export function batchHistory(params = {}) {
    return FRequest('/api/v1/projector/rule_metric/rule_metric_value_list', params);
}

// 详情数据
export function metricDetail(id) {
    return FRequest(`/api/v1/projector/rule_metric/detail/${id}`, {});
}
