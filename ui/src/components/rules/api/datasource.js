/**
 * 数据源部分接口
 * 包括在上游表、非上游表，在不同类型数据源模式下 获取数据库、数据表、字段调用的不同接口
 */

import {
    request,
} from '@fesjs/fes';

/**
 * 公共部分接口，不区分是否为上游表，不区分数据源类型
 * getDataSources: 获取数据源列表
 */
const commomApi = {
    getDataSources: '/api/v1/projector/meta_data/data_source/info',
};
const CommonDataSourceApi = {
    getDataSources: params => request(commomApi.getDataSources, params, {
        method: 'get',
        cache: true,
    }),
};


/**
 * HIVE模式数据源 相关接口
 * getDatabases: 获取数据库列表
 * getTables: 获取数据表列表
 * getColumns: 获取字段列表
 */
const hiveApi = {
    getDatabases: '/api/v1/projector/meta_data/db',
    getTables: '/api/v1/projector/meta_data/table',
    getColumns: '/api/v1/projector/meta_data/column',
};
const HiveDataSourceApi = {
    getDatabases: params => request(hiveApi.getDatabases, params, {
        cache: true,
    }),
    getTables: params => request(hiveApi.getTables, params, {
        cache: true,
    }),
    getColumns: params => request(hiveApi.getColumns, params, {
        cache: true,
    }),
};

/**
 * 非HIVE模式数据源 相关接口
 * getDatabases: 获取数据库列表
 * getTables: 获取数据表列表
 * getColumns: 获取字段列表
 */
const notHiveApi = {
    getDatabases: '/api/v1/projector/meta_data/data_source/dbs',
    getTables: '/api/v1/projector/meta_data/data_source/tables',
    getColumns: '/api/v1/projector/meta_data/data_source/columns',
};
const NotHiveDataSourceApi = {
    getDatabases: params => request(notHiveApi.getDatabases, params, {
        method: 'get',
        cache: true,
    }),
    getTables: params => request(notHiveApi.getTables, params, {
        method: 'get',
        cache: true,
    }),
    getColumns: params => request(notHiveApi.getColumns, params, {
        method: 'get',
        cache: true,
    }),
};

/**
 * 上游表模式 相关接口
 * 上游表模式，页面没有数据库选项，数据库在上游系统确定，并且数据表列表、字段列表是查询上游系统
 * getTables: 获取数据表列表
 * getColumns: 获取字段列表
 */
const upstreamApi = {
    getTables: '/api/v1/projector/meta_data/cs_table',
    getColumns: '/api/v1/projector/meta_data/cs_column',
};
const UpstreamDataSourceApi = {
    getTables: params => request(upstreamApi.getTables, params, {
        cache: true,
    }),
    getColumns: params => request(upstreamApi.getColumns, params, {
        cache: true,
    }),
};

export {
    CommonDataSourceApi,
    HiveDataSourceApi,
    NotHiveDataSourceApi,
    UpstreamDataSourceApi,
};
