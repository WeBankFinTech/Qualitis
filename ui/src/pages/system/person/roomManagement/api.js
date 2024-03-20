import { request as FRequest } from '@fesjs/fes';

// 删除科室
export function deleteRoom(subDepartmentId = '') {
    return FRequest(`/api/v1/admin/sub_department/delete/${subDepartmentId}`, {}, 'post');
}

// 获取科室管理表格数据
export function fetchRoomTabelData(params = {}) {
    return FRequest('/api/v1/admin/sub_department/all', params, 'post');
}

// 新增科室
export function addRoom(params = {}) {
    return FRequest('/api/v1/admin/sub_department/add', params, 'post');
}

// 编辑科室
export function editRoom(params = {}) {
    return FRequest('/api/v1/admin/sub_department/modify', params, 'post');
}

// 根据规则部门代码获取科室列表
export function fetchRoomListByCode(params = {}) {
    return FRequest(`/api/v1/projector/meta_data/system/devAndOpsInfo/${params.department_code}`, {}, { cache: true, method: 'get' });
}
