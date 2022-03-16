import Vue from 'vue';

// 需要等待 FesApi挂载到Vue原型上才 能成功赋值给api
let api = null;
let app = null;
let commonParams = {
    clusterName: '',
    proxyUser: ''
};
setTimeout(() => {
    api = Vue.prototype.FesApi;
    app = Vue.prototype.FesApp;
});

const getSystemNameNew = (data) => {
    return data.full_cn_name
        || data.subSystemFullCnName
        || data.subSystemId;
};

export const setCommonParams = (params) => {
    if (!params) return;
    commonParams.clusterName = params.clusterName || '';
    commonParams.proxyUser = params.proxyUser || '';
};

export const clearCommonParams = () => {
    commonParams.clusterName = '';
    commonParams.proxyUser = '';
};

// 数据源类型过滤器，只保留Enum('1', 'mysql')、Enum('5', 'tdsql')类型的数据源
export const dataSourceTypesAcceptor = (data) => {
    if (!Array.isArray(data)) return [];
    return data.filter((item) => ['1', '5', 1, 5].includes(item.id));
};

/**
 * 获取环境列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @return {List<Env>} 环境列表
 */
export const getEnvs = () => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/env', commonParams, 'get').then((res) => {
            if (!Array.isArray(res.query_list)) resolve([]);
            resolve(res.query_list);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取集群列表
 * @return {List<String>} 集群名称列表
 */
export const getClusters = () => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/cluster', {}).then((res) => {
            const clusters = res.optional_clusters;
            if (!Array.isArray(clusters)) resolve([]);
            resolve(clusters);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取代理用户列表
 * @return {List<String>} 代理用户列表
 */
export const getProxyUsers = () => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/proxy_user', {}, 'get').then((res) => {
            let proxyUsers;
            if (Array.isArray(res) && res.length) {
                proxyUsers = res;
            } else {
                proxyUsers = [app.get('FesUserName')];
            }
            resolve(proxyUsers);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取子系统列表
 * @return {List<SubSystem>} 子系统列表
 */
export const getSubSystems = () => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/subSystemInfo', {}).then(res => {
            if (!Array.isArray(res)) resolve([]);
            const subSystemList = res.map((item) => {
                const cnName = getSystemNameNew(item);
                return Object.assign({}, item, {
                    subSystemName: cnName,
                    enName: item.subSystemName,
                    cnName: cnName
                });
            });
            resolve(subSystemList);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取数据源类型列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @return {List<DataSourceType>} 数据源类型列表
 */
export const getDataSourceTypes = (acceptor) => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/types/all', commonParams, 'get').then((res) => {
            if (!Array.isArray(res.type_list)) resolve([]);
            const dataSourceTypes = acceptor instanceof Function ? acceptor(res.type_list) : res.type_list;
            resolve(dataSourceTypes);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取数据源列表
 * @param {String} dataSourceName 数据源名称
 * @param {String} dataSourceTypeId 数据源类型ID
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {Number} current 当前页码
 * @param {Number} size 分页大小
 * @return {List<DataSource>} 数据源列表
 */
export const getDataSources = (
    dataSourceName,
    dataSourceTypeId,
    current,
    size
) => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/info', Object.assign({
            name: dataSourceName || '',
            typeId: dataSourceTypeId || '',
            currentPage: current || 1,
            pageSize: size || 10
        }, commonParams), 'get').then((res) => {
            if (!Array.isArray(res.query_list)) {
                resolve({
                    total: 0,
                    data: []
                });
            }
            resolve({
                total: res.totalPage,
                data: res.query_list
            });
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取数据源详情信息
 * @param {String} dataSourceId 数据源ID
 * @param {String} versionId 数据源版本ID
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @return {DataSource} 数据源详情信息
 */
export const getDataSourceDetail = (
    dataSourceId,
    versionId
) => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/info/detail', Object.assign({
            dataSourceId: dataSourceId || '',
            versionId: versionId || ''
        }, commonParams), 'get').then((res) => {
            resolve(res.info);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 获取指定数据源的版本列表
 * @param {String} dataSourceId 数据源ID
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @return {List<DataSourceVersion>} 数据源版本列表
 */
export const getVersions = (dataSourceId) => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/versions', Object.assign({
            dataSourceId: dataSourceId || ''
        }, commonParams), 'get').then((res) => {
            if (!Array.isArray(res.versions)) resolve([]);
            resolve(res.versions);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 测试数据源连接
 * @param {String} urlSearchParams url查询参数，包含集群名、代理用户
 * @param {DataSource} dataSource 数据源信息
 */
export const testConnection = (urlSearchParams, dataSource) => {
    return api.fetch(`/api/v1/projector/meta_data/data_source/connect?${urlSearchParams}`, dataSource);
};

/**
 * 过期作废数据源
 * @param {String} urlSearchParamsStr url查询参数，包含数据源ID、集群名、代理用户
 */
export const expireDataSource = (urlSearchParamsStr) => {
    return api.fetch(`/api/v1/projector/meta_data/data_source/expire?${urlSearchParamsStr}`);
};

/**
 * 发布数据源
 * @param {String} urlSearchParamsStr url查询参数，包含数据源ID、数据源版本ID、集群名、代理用户
 */
export const publishDataSource = (urlSearchParamsStr) => {
    return api.fetch(`/api/v1/projector/meta_data/data_source/publish?${urlSearchParamsStr}`);
};

/**
 * 更新数据源连接信息
 * @param {String} urlSearchParamsStr url查询参数，包含数据源ID、集群名、代理用户
 * @param {DataSourceConnectParams} connectParams 数据源连接信息
 * @param {String} comment 更新日志
 */
export const updateDataSourceConnectionParams = (urlSearchParamsStr, connectParams, comment) => {
    return api.fetch(`/api/v1/projector/meta_data/data_source/param/modify?${urlSearchParamsStr}`, {
        comment: comment || '',
        connectParams: connectParams || {}
    });
};

/**
 * 获取指定数据源类型的 连接信息字段配置列表
 * @param {String} dataSourceTypeId 数据源类型ID
 * @return {List<KeyDefine>} 数据源连接信息 字段列表
 */
export const getKeyDefines = (dataSourceTypeId) => {
    return new Promise((resolve, reject) => {
        api.fetch('/api/v1/projector/meta_data/data_source/key_define/type', Object.assign({
            keyId: dataSourceTypeId,
        }, commonParams), 'get').then((res) => {
            if (!Array.isArray(res.key_define)) resolve([]);
            resolve(res.key_define);
        }).catch((error) => {
            reject(error);
        })
    });
};

/**
 * 新增数据源
 * @param {String} urlSearchParamsStr url查询参数，包含集群名、代理用户
 * @param {Object} data 数据源信息
 */
export const createDataSource = (urlSearchParamsStr, data = {}) => {
    return new Promise((resolve, reject) => {
        api.fetch(`/api/v1/projector/meta_data/data_source/create?${urlSearchParamsStr}`, data).then((res) => {
            const result = Object.assign({}, res, {id: res.insert_id});
            resolve(result);
        }).catch((error) => {
            reject(error);
        });
    });
};

/**
 * 更新数据源
 * @param {String} urlSearchParamsStr url查询参数，包含数据源ID、集群名、代理用户
 * @param {Object} data 数据源信息
 */
export const updateDataSource = (urlSearchParamsStr, data = {}) => {
    return new Promise((resolve, reject) => {
        api.fetch(`/api/v1/projector/meta_data/data_source/modify?${urlSearchParamsStr}`, data).then((res) => {
            const result = Object.assign({}, res, {id: res.update_id});
            resolve(result);
        }).catch((error) => {
            reject(error);
        });
    });
};
