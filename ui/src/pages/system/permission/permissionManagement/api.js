import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/permission';
const api = {
    getPermissions: `${base}/all`,
    addPermission: `${base}`,
    updatePermission: `${base}`,
    deletePermission: `${base}/delete`,
};
const defaultPrimaryKey = 'permission_id';

/**
 * 查询权限列表
 * @param {Object} params 查询权限列表的查询条件
 */
export const getPermissions = params => FRequest(api.getPermissions, params);

/**
 * 新增权限
 * @param {Object} params 权限对象
 * @param {String} primaryKey 权限对象主键
 */
export const addPermission = (params, primaryKey = defaultPrimaryKey) => {
    const data = cloneDeep(params);
    delete data[primaryKey];
    return FRequest(api.addPermission, data, 'put');
};

/**
 * 修改权限对象
 * @param {Object} params 权限对象
 * @param {String} primaryKey 权限对象主键
 */
export const updatePermission = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) {
        return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updatePermission method`));
    }
    return FRequest(api.updatePermission, params);
};

/**
 * 删除权限
 * @param {String} id 权限对象主键
 */
export const deletePermission = id => FRequest(api.deletePermission, { [defaultPrimaryKey]: id });
