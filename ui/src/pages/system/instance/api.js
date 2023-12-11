import { request as FRequest } from '@fesjs/fes';


// 实例查询
export function instanceConfigData(params) {
    return FRequest('/api/v1/admin/service_info/all', params);
}
// 实例删除
export function instanceConfigDelete(params) {
    return FRequest('/api/v1/admin/service_info/delete', params);
}

// 实例上下线
export function instanceConfigModify(params) {
    return FRequest('/api/v1/admin/service_info/modify', params);
}
// 实例新增
export function instanceConfigAdd(params) {
    return FRequest('/api/v1/admin/service_info/add', params);
}
