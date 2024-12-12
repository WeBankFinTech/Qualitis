import { request as FRequest } from '@fesjs/fes';

// 集群 获取初始化数据（下拉选项数据）
export function getClusterListData() {
    return FRequest('/api/v1/projector/meta_data/cluster', {});
}

// 集群查询
export function configurationData(params) {
    return FRequest('/api/v1/projector/configuration', params, 'get');
}
// 集群设置保存
export function configurationSave(params) {
    return FRequest('/api/v1/projector/configuration', params);
}
// 获取代理用户
export function getProxyUsers(params = {}) {
    return FRequest('/api/v1/projector/proxy_user', params, 'get');
}
