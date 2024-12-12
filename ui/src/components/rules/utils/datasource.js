/**
 * 数据源，查询 数据源列表、数据库列表、数据表列表、字段列表方法，
 * 方法内部 做了 是否上游表模式、数据源类型模式的差异化处理
 */

import {
    CommonDataSourceApi,
    HiveDataSourceApi,
    NotHiveDataSourceApi,
    UpstreamDataSourceApi,
} from '@/components/rules/api/datasource';


const maxSize = 2147483647;
// HIVE模式的数据源类型列表
const hiveModes = ['HIVE', 'FPS'];
// 指定数据源类型是否为HIVE模式类型数据源
export const isHiveMode = (dataSourceType = '') => hiveModes.includes((dataSourceType || '').toUpperCase());


// const map = {
//     mysql: 1,
//     hive: 4,
//     tdsql: 5,
// };
/**
 * 查询数据源列表
 * @param {Object} params
 * @param {String} params.clusterName 集群名
 * @param {String} params.dataSourceType 数据源类型
 * @param {String} params.proxyUser 代理用户
 * @param {String} params.name 数据源名称，用于模糊搜索
 * @return {Array<Option>} option.label 选项显示文案
 * @return {Array<Option>} option.value 选项的值
 */
export const getDataSources = async ({
    clusterName = '', dataSourceType = '', proxyUser = '', name = '',
}, map) => {
    if (!clusterName || !dataSourceType || isHiveMode(dataSourceType)) return [];
    const resp = await CommonDataSourceApi.getDataSources({
        clusterName,
        proxyUser,
        name,
        typeId: map[dataSourceType] || '',
        currentPage: 0,
        pageSize: 512,
    });
    if (!resp || !Array.isArray(resp.queryList)) {
        return [];
    }
    const result = resp.queryList
        .map(item => ({ ...item, label: item.dataSourceName, value: String(item.id) }));
    return result;
};


/**
 * HIVE模式 查询数据库列表
 * @param {String} clusterName 集群名
 * @param {String} sourceType 集群的数据源类型
 * @param {String} proxyUser 代理用户
 */
const getDatabasesHive = async (clusterName, sourceType, proxyUser) => {
    const resp = await HiveDataSourceApi.getDatabases({
        cluster_name: clusterName,
        source_type: sourceType,
        proxy_user: proxyUser,
        start_index: 0,
        page_size: 2147483647,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 非HIVE模式 查询数据库列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} dataSourceId 数据源id
 */
const getDatabasesNotHive = async (clusterName, proxyUser, dataSourceId, envId) => {
    if (!dataSourceId || dataSourceId === 'null') return [];
    const resp = await NotHiveDataSourceApi.getDatabases({
        clusterName,
        proxyUser,
        dataSourceId,
        envId,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 查询数据库列表
 * @description 页面调用的方法，方法内部做差异化处理
 * @param {Object} params 查询参数
 * @param {String} params.clusterName 集群名
 * @param {String} params.sourceType 集群的数据源类型
 * @param {String} params.proxyUser 代理用户
 * @param {String} params.dataSourceType 数据源类型
 * @param {String} params.dataSourceId 数据源id
 */
export const getDatabases = async ({
    clusterName = '', proxyUser = '', sourceType = '', dataSourceType = '', dataSourceId = '', envId = '',
}) => {
    if (!clusterName) return [];
    if (!isHiveMode(dataSourceType)) return getDatabasesNotHive(clusterName, proxyUser, dataSourceId, envId);
    return getDatabasesHive(clusterName, sourceType, proxyUser);
};


/**
 * HIVE模式 查询数据表列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} dbName 数据库名
 */
const getTablesHive = async (clusterName, proxyUser, dbName) => {
    if (!dbName) return [];
    const resp = await HiveDataSourceApi.getTables({
        cluster_name: clusterName,
        proxy_user: proxyUser,
        db_name: dbName,
        start_index: 0,
        page_size: 2147483647,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 非HIVE模式 查询数据表列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} dataSourceId 数据源id
 * @param {String} dbName 数据库名
 */
const getTablesNotHive = async (clusterName, proxyUser, dataSourceId, dbName, envId) => {
    if (!dataSourceId || dataSourceId === 'null' || !dbName) return [];
    const resp = await NotHiveDataSourceApi.getTables({
        clusterName,
        proxyUser,
        dataSourceId,
        dbName,
        envId,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 上游表模式 查询数据表列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {Object} param2 上游表查询相关数据
 */
const getTablesUpstream = async (clusterName, proxyUser, { contextID = '', nodeName = '' } = {}) => {
    const resp = await UpstreamDataSourceApi.getTables({
        cs_id: contextID,
        node_name: nodeName,
        cluster_name: clusterName,
        start_index: 0,
        page_size: maxSize,
        proxy_user: proxyUser,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 查询数据表列表
 * @description 页面调用的方法，方法内部做差异化处理
 * @param {Object} params 查询参数
 * @param {String} params.clusterName 集群名
 * @param {String} params.proxyUser 代理用户
 * @param {String} params.dataSourceType 数据源类型
 * @param {String} params.dataSourceId 数据源id
 * @param {String} params.dbName 数据库名
 * @param {Object} params.upstreamParams 上游表查询相关数据
 */
export const getTables = async ({
    clusterName = '', proxyUser = '', dataSourceType = '', dataSourceId = '', dbName = '', upstreamParams, envId = '',
}) => {
    if (!clusterName) return [];
    if (upstreamParams?.isUpStream) return getTablesUpstream(clusterName, proxyUser, upstreamParams);
    if (!isHiveMode(dataSourceType)) return getTablesNotHive(clusterName, proxyUser, dataSourceId, dbName, envId);
    return getTablesHive(clusterName, proxyUser, dbName);
};


/**
 * HIVE模式 查询字段列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} dbName 数据库名
 * @param {String} tableName 数据表名
 */
const getColumnsHive = async (clusterName = '', proxyUser = '', dbName = '', tableName = '') => {
    if (!dbName || !tableName) return [];
    const resp = await HiveDataSourceApi.getColumns({
        cluster_name: clusterName,
        proxy_user: proxyUser,
        db_name: dbName,
        table_name: tableName,
        start_index: 0,
        page_size: maxSize,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 非HIVE模式 查询字段列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} dataSourceId 数据源id
 * @param {String} dbName 数据库名
 * @param {String} tableName 数据表名
 */
const getColumnsNotHive = async (clusterName = '', proxyUser = '', dataSourceId = '', dbName = '', tableName = '', envId = '') => {
    if (!dataSourceId || dataSourceId === 'null' || !dbName || !tableName) return [];
    try {
        const resp = await NotHiveDataSourceApi.getColumns({
            clusterName,
            proxyUser,
            dataSourceId,
            dbName,
            tableName,
            envId,
        });
        if (!Array.isArray(resp.data)) return [];
        return resp.data;
    } catch (error) {
        console.log(error);
        return [];
    }
};
/**
 * 上游表模式 查询字段列表
 * @param {String} clusterName 集群名
 * @param {String} proxyUser 代理用户
 * @param {String} param2 上游表查询相关数据
 */
const getColumnsUpStream = async (clusterName, proxyUser, { contextID, nodeName, contextKey } = {}) => {
    if (!contextKey) {
        return;
    }
    const resp = await UpstreamDataSourceApi.getColumns({
        cs_id: contextID,
        node_name: nodeName,
        context_key: contextKey,
        cluster_name: clusterName,
        proxy_user: proxyUser,
        start_index: 0,
        page_size: maxSize,
    });
    if (!Array.isArray(resp.data)) return [];
    return resp.data;
};
/**
 * 查询字段列表
 * @description 页面调用的方法，方法内部做差异化处理
 * @param {Object} params 查询参数
 * @param {String} params.clusterName 集群名
 * @param {String} params.proxyUser 代理用户
 * @param {String} params.dataSourceType 数据源类型
 * @param {String} params.dataSourceId 数据源id
 * @param {String} params.dbName 数据库名
 * @param {String} params.tableName 数据表名
 * @param {Object} params.upstreamParams 上游表查询相关数据
 */
export const getColumns = async ({
    clusterName = '', proxyUser = '', dataSourceType = '', dataSourceId = '', dbName = '', tableName = '', upstreamParams, envId = '',
}) => {
    if (!clusterName) return [];
    if (upstreamParams?.isUpStream) return getColumnsUpStream(clusterName, proxyUser, upstreamParams);
    if (!isHiveMode(dataSourceType)) return getColumnsNotHive(clusterName, proxyUser, dataSourceId, dbName, tableName, envId);
    return getColumnsHive(clusterName, proxyUser, dbName, tableName);
};
