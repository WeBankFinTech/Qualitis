import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/user_spec_permission';
const api = {
    getUserSpecialPermissions: `${base}/all`,
    addUserSpecialPermission: `${base}`,
    updateUserSpecialPermission: `${base}`,
    deleteUserSpecialPermission: `${base}/delete`,
};
const defaultPrimaryKey = 'uuid';

/**
 * 查询用户特殊权限列表
 * @param {Object} params 用户特殊权限列表查询条件
 */
export const getUserSpecialPermissions = params => FRequest(api.getUserSpecialPermissions, params);

/**
 * 新增用户特殊权限
 * @param {Object} params 用户特殊权限对象
 * @param {Stirng} primaryKey 用户特殊权限对象主键
 */
export const addUserSpecialPermission = (params, primaryKey = defaultPrimaryKey) => {
    const data = cloneDeep(params);
    delete data[primaryKey];
    return FRequest(api.addUserSpecialPermission, data, 'put');
};

/**
 * 修改用户特殊权限
 * @param {Object} params 用户特殊权限对象
 * @param {String} primaryKey 用户特殊权限对象主键
 */
export const updateUserSpecialPermission = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) {
        return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updateUserSpecialPermission method`));
    }
    return FRequest(api.updateUserSpecialPermission, params);
};

/**
 * 删除用户特殊权限
 * @param {String} id 用户特殊权限对象主键
 */
export const deleteUserSpecialPermission = id => FRequest(api.deleteUserSpecialPermission, { [defaultPrimaryKey]: id });
