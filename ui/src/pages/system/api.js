import { request as FRequest } from '@fesjs/fes';

// 获取系统设置列表
export function getClusterInfo(params = {}) {
    return FRequest('/api/v1/admin/cluster_info/all', params);
}
// 改变系统设置列表
export function changeClusterInfo(params = {}, method) {
    return FRequest('/api/v1/admin/cluster_info', params, method);
}
// 删除行
export function deleteClusterInfo(params = {}) {
    return FRequest('/api/v1/admin/cluster_info/delete', params);
}
// 获取部门列表（本地）
export function getDepartment(params = {}) {
    return FRequest('/api/v1/admin/department/all', params);
}
// 获取科室列表（本地）
export function getRoom(params = {}) {
    return FRequest('/api/v1/admin/sub_department/all', params, 'post');
}
// 获取用户
export function getUser(params = {}) {
    return FRequest('/api/v1/admin/user/all', params);
}
// 获取用户名
export function getUserName(params = {}) {
    return FRequest('/api/v1/admin/user/name/all', params, 'get');
}
// 获取代理用户
export function getProxyUser(params = {}) {
    return FRequest('/api/v1/admin/proxy_user/all', params);
}
// 获取代理用户名
export function getProxyUserName(params = {}) {
    return FRequest('/api/v1/admin/proxy_user/name/all', params, 'get');
}
// 修改用户
export function modifyUser(params = {}) {
    return FRequest('/api/v1/admin/user/modify_department', params);
}

// 新增用户
export function changeUser(params = {}) {
    return FRequest('/api/v1/admin/user', params, 'put');
}

// 删除用户
export function delUser(params = {}) {
    return FRequest('/api/v1/admin/user/delete', params);
}
// 删除代理用户
export function delProxyUser(params = {}) {
    return FRequest('/api/v1/admin/proxy_user/delete', params);
}
// 改变代理用户
export function changeProxyUser(params = {}, method) {
    return FRequest('/api/v1/admin/proxy_user', params, method);
}
// 拉代理用户成员
export function getUserProxyUser(params = {}, proxyUserName) {
    return FRequest(`/api/v1/admin/user_proxy_user/${proxyUserName}/all`, params);
}
// 添加代理用户成员
export function addUserProxyUser(params = {}) {
    return FRequest('/api/v1/admin/user_proxy_user', params, 'put');
}
// 删除代理用户成员
export function delUserProxyUser(params = {}) {
    return FRequest('/api/v1/admin/user_proxy_user', params, 'delete');
}
// 获取租户
export function getTenantUser(params = {}) {
    return FRequest('/api/v1/admin/tenant_user/all', params);
}
// 删除租户
export function delTenantUser(params = {}) {
    return FRequest('/api/v1/admin/tenant_user/delete', params);
}
// 改变租户
export function changeTenantUser(params = {}, method) {
    return FRequest('/api/v1/admin/tenant_user', params, method);
}
// 拉租户成员
export function getUserTenantUser(params = {}, tenantUserName) {
    return FRequest(`/api/v1/admin/user_tenant_user/${tenantUserName}/all`, params);
}
// 添加租户成员
export function addUserTenantUser(params = {}) {
    return FRequest('/api/v1/admin/user_tenant_user', params, 'put');
}
// 删除租户成员
export function delUserTenantUser(params = {}) {
    return FRequest('/api/v1/admin/user_tenant_user', params, 'delete');
}
// 获取用户职位角色列表
export function getPositionRole(params = {}) {
    return FRequest('/api/v1/position/role/all', params, 'post');
}

// 获取自定义部门列表
export function fetchDepartmentsCustom(params = {}) {
    return FRequest('/api/v1/projector/meta_data/system/departmentInfo', params, { cache: true });
}

// 根据部门代码获取自定义科室列表
export function fetchDepartmentsDivisionsByCodeCustom(params = '') {
    return FRequest(`/api/v1/projector/meta_data/system/devAndOpsInfo/${params}`, {}, { cache: true, method: 'get' });
}
