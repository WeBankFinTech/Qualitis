import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/user_role';
const api = {
    getUserRoles: `${base}/all`,
    addUserRole: `${base}`,
    updateUserRole: `${base}`,
    deleteUserRole: `${base}/delete`,
    getUserNameList: '/api/v1/admin/user/all',
    getRoleNameList: '/api/v1/admin/role/all',
};
const defaultPrimaryKey = 'uuid';

/**
 * 查询用户角色列表
 * @param {Object} params 用户角色列表查询条件
 */
export const getUserRoles = params => FRequest(api.getUserRoles, params);


/**
 * 新增用户角色
 * @param {Object} params 用户角色对象
 * @param {String}} primaryKey 用户角色对象主键
 */
export const addUserRole = (params, primaryKey = defaultPrimaryKey) => {
    const data = cloneDeep(params);
    delete data[primaryKey];
    return FRequest(api.addUserRole, data, 'put');
};

/**
 * 修改用户角色
 * @param {Object} params 用户角色对象
 * @param {String} primaryKey 用户角色对象主键
 */
export const updateUserRole = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) {
        return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updateUserRole method`));
    }
    return FRequest(api.updateUserRole, params);
};

/**
 * 删除用户角色
 * @param {String} id 用户角色对象主键
 */
export const deleteUserRole = id => FRequest(api.deleteUserRole, { [defaultPrimaryKey]: id });

/**
 * 查询用户名列表
 * @param {Object} params 用户用户名列表查询条件
 */
export const getUserNameList = params => FRequest(api.getUserNameList, params, { cache: true });

/**
 * 查询角色名列表
 * @param {Object} params 用户角色名列表查询条件
 */
export const getRoleNameList = params => FRequest(api.getRoleNameList, params);
