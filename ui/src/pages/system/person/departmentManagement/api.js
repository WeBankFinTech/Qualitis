import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const base = '/api/v1/admin/department';
const api = {
    getDepartments: `${base}/all`,
    addDepartment: `${base}/add`,
    updateDepartment: `${base}/modify`,
    deleteDepartment: `${base}/delete`,
};
const defaultPrimaryKey = 'department_id';

/**
 * 获取部门列表
 * @param {Object} params 查询参数
 */
export const getDepartments = params => FRequest(api.getDepartments, params);

/**
 * 新增部门
 * @param {Object} params 部门对象
 * @param {String} primaryKey 部门主键
 */
export const addDepartment = (params) => {
    const data = cloneDeep(params);
    return FRequest(api.addDepartment, data);
};

/**
 * 修改部门
 * @param {Object} params 部门对象
 * @param {String} primaryKey 部门主键
 */
export const updateDepartment = (params, primaryKey = defaultPrimaryKey) => {
    if (!params[primaryKey]) return Promise.reject(new Error(`primary key(${primaryKey}) not found in first argument of updateDepartment method`));
    return FRequest(api.updateDepartment, params);
};

/**
 * 删除部门
 * @param {String} id 部门主键
 */
export const deleteDepartment = id => FRequest(`${api.deleteDepartment}/${id}`);

// 获取部门列表
export function fetchDepartments(params = {}) {
    return FRequest('/api/v1/projector/meta_data/system/departmentInfo', params);
}
