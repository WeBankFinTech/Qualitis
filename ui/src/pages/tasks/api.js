import { request as FRequest } from '@fesjs/fes';

const api = {
    fetchTasksData: '/api/v1/projector/application/filter/status',
    fetchProjects: '/api/v1/projector/project/util/all',
    fetchDataSoruce: '/api/v1/projector/application/datasource',
    generateData: '/api/v1/projector/application/upload',
    stopBatch: '/api/v1/projector/execution/application/batch/kill',
    fetchOptions: '/api/v1/projector/query/conditions',
    fetchRuleGroupList: '/api/v1/projector/rule/group/option/list',
    fetchExecuteUserList: '/api/v1/projector/application/get/All/execute/user',
    fetchTaskIdStatus: '/api/v1/projector/application/filter/application_id',
};

const keepArray = data => (Array.isArray(data) ? data : []);

export function fetchTasksData({ current = 1, size = 10 }) {
    return new Promise((resolve, reject) => {
        FRequest(api.fetchTasksData, { page: current - 1, size }, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    total_num: 0,
                    data: [],
                });
                return;
            }
            const totalNum = res.total || 0;
            const data = keepArray(res.data);
            resolve({
                total_num: totalNum,
                data,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取所有的项目
export function fetchProjects({ page = 1, size = 10 }) {
    return new Promise((resolve, reject) => {
        FRequest(api.fetchProjects, { page, size }, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    total: 0,
                    data: [],
                });
                return;
            }
            const total = res.total_num || 0;
            const data = keepArray(res.data);
            resolve({
                total,
                data,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取数据源
export function fetchDataSoruce({ page = 1, size = 10 }) {
    return new Promise((resolve, reject) => {
        FRequest(api.fetchDataSoruce, { page, size }, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    data: [],
                });
                return;
            }
            const data = keepArray(res);
            resolve({
                data,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取查询的结果
export function fetchSearchData(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, params, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    total: 0,
                    data: [],
                });
                return;
            }
            const total = res.total || 0;
            const data = keepArray(res.data);
            resolve({
                total,
                data,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取查询的结果
export function generateData(params) {
    return new Promise((resolve, reject) => {
        FRequest(api.generateData, params, { method: 'POST' }).then(() => {
            resolve();
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取日志
export function fetchLog(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'GET' }).then((res) => {
            if (!res) {
                resolve({
                    res: [],
                    task_id: '',
                });
                return;
            }
            resolve({
                res,
                task_id: params.tempId,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取状态详情里的状态
export function fetchStatus(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'GET' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
                    task_id: '',
                });
                return;
            }
            resolve({
                res,
                task_id: params.tempId,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取查询的结果
export function stopBatch(params) {
    return new Promise((resolve, reject) => {
        FRequest(api.stopBatch, params, { method: 'POST' }).then(() => {
            resolve();
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取日志
export function fetchFileUpdatorList(url) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'GET' }).then((res) => {
            if (!res) {
                resolve({
                    res: [],
                });
                return;
            }
            resolve({
                res,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取规则查询表单 下拉选项数据
export function fetchOptions() {
    return FRequest(api.fetchOptions, {}, 'get');
}

// 获取规则组名称列表
export function fetchRuleGroupList(param) {
    return FRequest(api.fetchRuleGroupList, param, 'post');
}

// 获取执行用户列表
export function fetchExecuteUserList() {
    return new Promise((resolve, reject) => {
        FRequest(api.fetchExecuteUserList, {}, 'post').then((res) => {
            if (!res) {
                resolve([]);
                return;
            }
            resolve(res);
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取状态日志详情
export function fetchTaskIdStatus(parmas) {
    return FRequest(api.fetchTaskIdStatus, parmas, 'post');
}
