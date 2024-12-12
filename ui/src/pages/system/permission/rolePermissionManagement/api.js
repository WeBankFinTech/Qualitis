import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/role_permission';
const api = {
    getRolePermissions: `${base}/all`,
    addRolePermission: `${base}`,
    updateRolePermission: `${base}`,
    deleteRolePermission: `${base}/delete`,
    getRoleNameList: '/api/v1/admin/role/all',
    getPermissionNameList: '/api/v1/admin/permission/all',
};
const defaultPrimaryKey = 'uuid';

/**
 * 查询角色权限列表
 * @param {Object} params 角色权限列表查询条件
 */
export const getRolePermissions = params => FRequest(api.getRolePermissions, params);

/**
 * 新增角色权限
 * @param {Object} params 角色权限对象
 * @param {Object} primaryKey 角色权限对象主键
 */
export const addRolePermission = (params, primaryKey = defaultPrimaryKey) => {
    const data = cloneDeep(params);
    delete data[primaryKey];
    return FRequest(api.addRolePermission, data, 'put');
};

/**
 * 修改角色权限对象
 * @param {Object} params 角色权限对象
 * @param {Object} primaryKey 角色权限对象主键
 */
export const updateRolePermission = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) {
        return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updateRolePermission method`));
    }
    return FRequest(api.updateRolePermission, params);
};

/**
 * 删除角色权限
 * @param {String} id 角色权限主键
 */
export const deleteRolePermission = id => FRequest(api.deleteRolePermission, { [defaultPrimaryKey]: id });

/**
 * 查询权限名列表
 * @param {Object} params 用户权限名列表查询条件
 */
export const getPermissionNameList = params => FRequest(api.getPermissionNameList, params);

/**
  * 查询角色名列表
  * @param {Object} params 用户角色名列表查询条件
  */
export const getRoleNameList = params => FRequest(api.getRoleNameList, params);
