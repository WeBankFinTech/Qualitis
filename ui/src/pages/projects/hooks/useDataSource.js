import { onMounted, ref, watch } from 'vue';
import { fetchOptions } from '../api';

export default function useDataSource(opts = []) {
    // 数据库 数据表列表
    const dataBaseList = ref([]);
    const dataTableList = ref([]);
    // const subSystemList = ref([]); // 已移除，改为直接输入

    // 获取数据源列表
    const getDataSourceList = async () => {
        try {
            const { dbs, db_tables: dbTables } = await fetchOptions();
            // 转化所有数据库下表为对象数组[{database_name: [{table_name: m}]}]
            Object.keys(dbTables).forEach((v) => {
                dbTables[v] = dbTables[v].map(m => ({ table_name: m }));
            });
            dataBaseList.value = dbs.filter(v => !!v && v !== 'null')
                .map(m => ({ database_name: m, table: dbTables[m] }));
        } catch (error) {
            console.log('error: ', error);
        }
    };

    // 获取子系统列表 - 已移除，改为直接输入

    // 函数映射
    const handleMap = {
        dataBaseList: getDataSourceList,
        // subSystemList: getSubSystemInfo, // 已移除
    };

    onMounted(() => {
        opts.forEach(item => handleMap[item]());
    });

    return {
        dataBaseList,
        dataTableList,
        // subSystemList, // 已移除
        getDataSourceList,
        // getSubSystemInfo, // 已移除
    };
}
