import { request as FRequest } from '@fesjs/fes';

// 获取代理用户
export function getProxyUsers(params = {}) {
    return FRequest('/api/v1/projector/proxy_user', params, 'get');
}

export function getClusterList(params = {}) {
    return FRequest('/api/v1/projector/meta_data/cluster', params);
}

export function getDBList(params = {}) {
    return FRequest('/api/v1/projector/meta_data/db', params);
}

export function getDBListByResourceID(params = {}) {
    return FRequest('/api/v1/projector/meta_data/data_source/dbs', params, 'get');
}

export function getColumnList(params = {}) {
    return FRequest('/api/v1/projector/meta_data/column', params);
}

export function getColumnListBySourceID(params = {}) {
    return FRequest('/api/v1/projector/meta_data/data_source/columns', params, 'get');
}

export function getEnvListBySourceID(url) {
    return FRequest(url, {}, { method: 'get', cache: true });
}

// 获取模板列表 根据template_type确认类型 1 单表 2 单/多指标 3跨表 4文件
export function fetchTemplatesWithCache(params = {}) {
    return FRequest('/api/v1/projector/rule_template/default/all', params, { cache: true });
}

// 基于规则模版的获取模板列表 根据template_type确认类型 1 单表 2 单/多指标 3跨表 4文件
export function fetchTemplatesOfListByTemplate(params = {}) {
    return FRequest('/api/v1/projector/rule_template/user/option/list', params, { cache: true, method: 'get' });
}

// 规则上锁,解锁操作由后端自行判断
export function lockRule(params = {}) {
    if (!params.rule_lock_id) {
        return;
    }
    return FRequest('/api/v1/projector/rule/lock/acquire', params, 'get');
}

// 释放规则锁
export function unLockRule(params = {}) {
    if (!params.rule_lock_id) {
        return;
    }
    return FRequest('/api/v1/projector/rule/lock/release', params, 'get');
}

// 基于规则模板批量创建规则
export function batchAddRule(params = {}) {
    return FRequest('/api/v1/projector/rule/group/batch/rule/add', params);
}
