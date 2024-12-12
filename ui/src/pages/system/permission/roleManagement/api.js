import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/role';
const api = {
    getRoles: `${base}/all`,
    getRoleTypes: `${base}/type/all`,
    addAndUpdateRole: `${base}`,
    deleteRole: `${base}/delete`,
};
const defaultPrimaryKey = 'role_id';

/**
 * 获取角色管理列表
 * @param {Object} params 入参，包含分页参数
 */
export const getRoles = params => FRequest(api.getRoles, params);

/**
 * 获取角色类型枚举值列表
 * @param {Object} params 入参，包含分页参数
 */
export const getRoleTypes = params => FRequest(api.getRoleTypes, params);

/**
 * 新增角色
 * @param {Object} params 角色对象
 * @param {String} primaryKey 角色对象id
 */
export const addRole = (params, primaryKey = defaultPrimaryKey) => {
    const data = cloneDeep(params);
    delete data[primaryKey];
    return FRequest(api.addAndUpdateRole, data, 'put');
};

/**
 * 修改角色
 * @param {Object} params 角色对象
 * @param {String} primaryKey 角色对象id
 */
export const updateRole = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) {
        return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updateRole method`));
    }
    return FRequest(api.addAndUpdateRole, params);
};

/**
 * 删除角色
 * @param {String} id 角色对象id
 */
export const deleteRole = id => FRequest(api.deleteRole, { [defaultPrimaryKey]: id });
