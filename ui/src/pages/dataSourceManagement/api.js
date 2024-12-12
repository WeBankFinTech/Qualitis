import { request as FRequest } from '@fesjs/fes';

const api = {
    clusterList: '/api/v1/projector/meta_data/cluster',
    proxyUserList: '/api/v1/projector/proxy_user',
};

// 获取集群列表
// /api/v1/projector/meta_data/cluster
export function fetchClusterList() {
    return new Promise((resolve, reject) => {
        FRequest(api.clusterList, {}, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 获取proxyUser列表
export function fetchProxyUserList() {
    return new Promise((resolve, reject) => {
        FRequest(api.proxyUserList, {}, { method: 'GET' }).then((res) => {
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


// 选择集群之后，使用该接口获取 数据源名称 列表
// /api/v1/projector/meta_data/data_source/env?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu
// clusterName必须，proxyUser非必须
export function fetchDataSourceName(url) {
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

// 选择集群之后，使用该接口获取 数据源类型 列表
// /api/v1/projector/meta_data/data_source/types/all?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu
// clusterName必须，proxyUser非必须
export function fetchDataSourceType(url) {
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

// 点击某一数据源的“版本”按钮，获取版本列表，展示在drawer里
// /api/v1/projector/meta_data/data_source/versions?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu&dataSourceId=1
// clusterName必须，proxyUser非必须，dataSourceId必须
export function fetchVersionList(url) {
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

// 数据源详情
// /api/v1/projector/meta_data/data_source/info/detail?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu&dataSourceId=55&versionId=
// clusterName必须，proxyUser非必须，dataSourceId必须，versionId必须
export function fetchDataSourceDetail(url) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'GET' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 发布 数据源
// /api/v1/projector/meta_data/data_source/publish?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu&dataSourceId=7&versionId=1
// clusterName必须，proxyUser非必须，dataSourceId必须，versionId必须
export function fetchPublishDataSource(url) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 废弃数据源
// /api/v1/projector/meta_data/data_source/expire?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu&dataSourceId=7
// clusterName必须，proxyUser非必须，dataSourceId必须
export function fetchExpireDataSource(url) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 测试连接 数据源
// /api/v1/projector/meta_data/data_source/connect?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu
// clusterName必须，proxyUser非必须
// params里面有很多项
export function fetchTestConnection(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, params, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 编辑 数据源
// /api/v1/projector/meta_data/data_source/modify?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu&dataSourceId=9
// clusterName必须，proxyUser非必须，dataSourceId必须
// params里面有很多项
export function fetchEditDataSource(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, params, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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

// 新增 数据源
// /api/v1/projector/meta_data/data_source/create?clusterName=TEST_BDAP_Cluster_A&proxyUser=neiljianliu
// clusterName必须，proxyUser非必须
// params里面有很多项
export function fetchAddDataSource(url, params) {
    return new Promise((resolve, reject) => {
        FRequest(url, params, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    res: {},
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
// 数据源版本更新
// api/v1/projector/meta_data/data_source/param/modify?clusterName=LINKIS_ONE_BDAP_DEV&proxyUser=neiljianliu&dataSourceId=241
export async function fetchUpdateDataSourceVersion(url, params) {
    try {
        await FRequest(url, params, { method: 'POST' });
        return Promise.resolve();
    } catch (err) {
        console.warn(err);
        return Promise.reject();
    }
}

// 数据源列表
// /api/v1/projector/meta_data/data_source/info?clusterName={clusterName}&proxyUser={proxyUser}&currentPage=1&pageSize=10&typeId={typeID}&name={searchName}
// clusterName必须，proxyUser非必须，typeId必须，name必须，currentPage必须，pageSize必须
export function fetchDataSourceList(url) {
    return new Promise((resolve, reject) => {
        FRequest(url, {}, { method: 'GET' }).then((res) => {
            if (!res) {
                resolve({
                    total: 0,
                    data: [],
                });
                return;
            }
            resolve({
                total: res?.length || 0,
                data: res,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 查询数据源表格列表
export function queryDataSourceList(body) {
    return new Promise((resolve, reject) => {
        FRequest('/api/v1/projector/meta_data/data_source/info/advance', body, { method: 'POST' }).then((res) => {
            if (!res) {
                resolve({
                    total: 0,
                    data: [],
                });
                return;
            }
            resolve({
                total: res.totalPage || 0,
                data: res.queryList,
            });
        }).catch((error) => {
            reject(error);
        });
    });
}

// 获取子系统列表
export function fetchSubSystemInfo(params = {}) {
    return Promise.resolve([]);
}

export function getDcnData(params = {}) {
    return FRequest(`/api/v1/projector/meta_data/dcn?sub_system_id=${params.subSystemId}`, {}, 'post');
    // return FRequest(`/api/v1/projector/meta_data/dcn?sub_system_id=2819`, {}, 'post');
}
