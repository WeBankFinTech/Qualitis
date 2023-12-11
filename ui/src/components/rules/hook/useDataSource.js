import {
    ref, unref,
} from 'vue';
import { useStore } from 'vuex';
import {
    getDataSources,
    getDatabases,
    getTables,
    getColumns,
    isHiveMode,
} from '@/components/rules/utils/datasource';
import { getEnvListBySourceID } from '@/components/rules/api';
import eventbus from '@/common/useEvents';
import { cloneDeep } from 'lodash-es';

export default function useDataSource(upstream = {}) {
    const connections = ref([]);
    const dbs = ref([]);
    // 二维数组，维护的是环境库表映射中数据库下拉框的数据列表
    const dbsList = ref([]);
    const tables = ref([]);
    const columns = ref([]);
    const envs = ref([]);
    let dataSource = {};
    const upstreamParams = upstream;
    const store = useStore();
    // 为了区别target，source
    const updateDataSource = (data, type = 'single', module = 'verfiyObject') => {
        if (module === 'advanceSetting') {
            dataSource = data;
            return;
        }
        switch (type) {
            case 'single':
                dataSource = data?.datasource[0] || {};
                break;
            case 'target':
                dataSource = data?.target || {};
                break;
            case 'source':
                dataSource = data?.source || {};
                break;
            // copy组件里面没有datasource[0]，直接取的datasource
            case 'copy':
                dataSource = data;
                break;
            default:
                break;
        }
    };
    const updateUpstreamParams = (isUpStream, rule_name = '', cs_id = '') => {
        // eslint-disable-next-line camelcase
        // if (rule_name) upstreamParams.nodeName = rule_name;
        // eslint-disable-next-line camelcase
        if (cs_id) upstreamParams.contextID = cs_id;
        upstreamParams.isUpStream = isUpStream;
    };
    const handleDataSourcesDependenciesChange = async (map) => {
        const {
            cluster_name: clusterName, type: dataSourceType, proxy_user: proxyUser,
        } = dataSource;
        console.group('CommonDataSource: ruleForms-数据源列表数据依赖项改变');
        console.log('clusterName: ', clusterName);
        console.log('dataSourceType: ', dataSourceType);
        console.log('proxyUser: ', proxyUser);
        // if (unref(isInited)) {
        //     console.log('执行 数据源列表 数据源字段相关clear逻辑');
        //     dataSource.linkis_datasource_id = '';
        //     dataSource.linkis_datasource_name = '';
        //     connections.value = [];
        // }
        // 已选 集群、数据源类型时，才获取数据源列表数据
        if (!clusterName || !dataSourceType) {
            console.groupEnd();
            return;
        }
        console.log('重新获取数据源列表数据');
        console.groupEnd();
        try {
            connections.value = await getDataSources({ clusterName, dataSourceType, proxyUser: '' }, map);
        } catch (e) {
            console.warn('获取数据源列表数据接口异常: ', e.message);
        }
        console.log('新数据源列表数据: ', connections.value);
    };

    const handleDbsDependenciesChange = async (index = -1) => {
        const {
            cluster_name: clusterName, type: dataSourceType, proxy_user: proxyUser, linkis_datasource_id: connection,
        } = dataSource;
        // eslint-disable-next-line camelcase
        let linkis_datasource_envs = [];
        if (index === -1) {
            // -1时为非环境映射的情况
            // eslint-disable-next-line camelcase
            linkis_datasource_envs = dataSource.linkis_datasource_envs;
        } else {
            // eslint-disable-next-line camelcase
            linkis_datasource_envs = dataSource.linkis_datasource_envs_mappings[index].datasource_envs;
        }
        console.group('CommonDataSource: ruleForms-数据库列表数据依赖项改变');
        console.log('clusterName: ', clusterName);
        console.log('dataSourceType: ', dataSourceType);
        console.log('proxyUser: ', proxyUser);
        console.log('connection: ', connection);
        console.log('linkis_datasource_envs: ', linkis_datasource_envs);
        // if (unref(isInited)) {
        //     console.log('执行数据库、数据表、字段相关clear逻辑');
        //     dataSource.db_name = '';
        //     dataSource.table_name = '';
        //     dataSource.col_names = [];
        //     dbs.value = [];
        //     tables.value = [];
        //     columns.value = [];
        // }
        // 已选 集群、数据源类型时，才获取数据库列表数据
        if (!clusterName || !dataSourceType) {
            console.groupEnd();
            return;
        }

        // HIVE模式数据源类型，并且没有选择 连接(数据源时)，不拉取数据库列表数据
        if (!isHiveMode(dataSourceType) && !connection) {
            console.groupEnd();
            return;
        }
        console.log('重新获取数据库列表数据');
        console.groupEnd();
        if (unref(dataSource.isUpStream)) {
            // 上游表不需要选择数据库
            return;
        }
        try {
            if (index === -1) {
                dbs.value = await getDatabases({
                    clusterName, sourceType: '', proxyUser, dataSourceType, dataSourceId: connection, envId: linkis_datasource_envs?.length ? linkis_datasource_envs[0].env_id : '',
                });
            } else {
                dbsList.value[index] = await getDatabases({
                    clusterName, sourceType: '', proxyUser, dataSourceType, dataSourceId: connection, envId: linkis_datasource_envs?.length ? linkis_datasource_envs[0].env_id : '',
                });
            }
        } catch (e) {
            console.warn('获取数据库列表数据接口异常: ', e.message);
        }
        console.log('新数据库列表数据: ', dbs.value);
    };

    const handleDbChange = async (index = -1) => {
        // if (unref(isInited)) {
        //     dataSource.table_name = '';
        //     dataSource.col_names = [];
        //     tables.value = [];
        //     columns.value = [];
        // }
        try {
            const {
                cluster_name: clusterName, type: dataSourceType, proxy_user: proxyUser, linkis_datasource_id: connection,
            } = dataSource;
            let dbName;
            // eslint-disable-next-line camelcase
            let linkis_datasource_envs = [];
            if (index === -1) {
                // -1时为非环境映射的情况
                dbName = dataSource.db_name;
                // eslint-disable-next-line camelcase
                linkis_datasource_envs = dataSource.linkis_datasource_envs;
                tables.value = await getTables({
                    clusterName,
                    proxyUser,
                    dataSourceType,
                    dataSourceId: connection,
                    dbName,
                    envId: linkis_datasource_envs?.length ? linkis_datasource_envs[0].env_id : '',
                    upstreamParams,
                });
            }
        } catch (e) {
            console.log(e.message);
        }
    };

    const handleTableChange = async (target) => {
        // if (unref(isInited)) {
        //     dataSource.col_names = [];
        //     columns.value = [];
        // }
        try {
            if (upstreamParams && upstreamParams.isUpStream) {
                const targetTable = tables.value.filter(item => item.table_name === dataSource.table_name);
                if (targetTable.length > 0) {
                    upstreamParams.contextKey = targetTable[0].context_Key;
                }
            }
            if (dataSource.type !== 'fps') {
                columns.value = await getColumns({
                    clusterName: dataSource.cluster_name,
                    proxyUser: dataSource.proxy_user,
                    dataSourceType: dataSource.type,
                    dataSourceId: dataSource.linkis_datasource_id,
                    dbName: dataSource.db_name,
                    tableName: dataSource.table_name,
                    // hive 没有数据源环境
                    envId: dataSource.linkis_datasource_envs?.length ? dataSource.linkis_datasource_envs[0].env_id : '',
                    upstreamParams,
                });
            } else {
                columns.value = cloneDeep(dataSource.file_table_desc);
            }
            // 更新store, 给表规则组的规则模块使用
            store.commit('rule/setVerifyColumns', cloneDeep(columns.value));
            eventbus.emit('UPDATE_COLUMN_LIST', { data: cloneDeep(columns.value), target });
        } catch (e) {
            console.log(e);
        }
    };
    const handleColumnChange = async () => {
    };
    const handleEnvChange = async () => {
        try {
            console.log('CommonDataSource: ruleForms-数据源环境列表数据依赖项改变');
            const urlParams = {
                clusterName: dataSource.cluster_name, // 必须有值
                proxyUser: dataSource.proxy_user || '', // 可以为空，但不要传undefine null值
                dataSourceId: dataSource.linkis_datasource_id, // 必须有值
            };
            const url = `/api/v1/projector/meta_data/data_source/env/list?clusterName=${urlParams.clusterName}&proxyUser=${urlParams.proxyUser}&dataSourceId=${urlParams.dataSourceId}`;
            const res = await getEnvListBySourceID(url);
            envs.value = res;
            envs.value.forEach((item) => {
                // 接口给的name id字段名未统一
                item.env_id = item.id;
                item.env_name = item.envName;
                item.value = {
                    env_id: item.id,
                    env_name: item.envName,
                };
            });
            console.log('handleEnvChange-envs', envs);
            // 数据源详情的环境数据反写到编辑框中，value值为对象，必须与option-value为同一引用地址，才能反显label。
            if (dataSource.linkis_datasource_envs) {
                (dataSource.linkis_datasource_envs || []).forEach((ditem, dindex) => {
                    const index = envs.value.findIndex(env => env.env_id === ditem.env_id);
                    if (index !== -1) {
                        dataSource.linkis_datasource_envs[dindex] = envs.value[index].value;
                    }
                });
            }
        } catch (e) {
            console.log(e.message);
        }
    };
    return {
        connections,
        dbs,
        tables,
        columns,
        envs,
        dbsList,
        handleDbChange,
        handleTableChange,
        handleDbsDependenciesChange,
        handleDataSourcesDependenciesChange,
        handleColumnChange,
        handleEnvChange,
        updateDataSource,
        updateUpstreamParams,
    };
}
