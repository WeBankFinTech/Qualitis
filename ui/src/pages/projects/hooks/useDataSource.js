import { onMounted, ref, watch } from 'vue';
import { fetchOptions, fetchSubSystemInfo } from '../api';

export default function useDataSource(opts = []) {
    // 数据库 数据表 子系统列表
    const dataBaseList = ref([]);
    const dataTableList = ref([]);
    const subSystemList = ref([]);

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

    // 获取子系统中文名
    const getSystemNameNew = (data, tr) => tr.full_cn_name || tr.subSystemFullCnName || data;

    // 获取子系统列表
    const getSubSystemInfo = async () => {
        try {
            const res = await fetchSubSystemInfo();
            const list = res || [];
            subSystemList.value = list.map((item) => {
                const cnName = getSystemNameNew(item.subSystemId, item);
                return Object.assign({}, item, {
                    subSystemName: cnName,
                    enName: item.subSystemName,
                    cnName,
                    value: String(item.subSystemId),
                    label: item.subSystemName,
                });
            });
        } catch (error) {
            console.log('error: ', error);
        }
    };

    // 函数映射
    const handleMap = {
        dataBaseList: getDataSourceList,
        subSystemList: getSubSystemInfo,
    };

    onMounted(() => {
        opts.forEach(item => handleMap[item]());
    });

    return {
        dataBaseList,
        dataTableList,
        subSystemList,
        getDataSourceList,
        getSubSystemInfo,
    };
}
