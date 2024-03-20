import { ref, onMounted } from 'vue';
import { fetchOptions, fetchSubSystemInfo, fetctdataLabels } from '../api';

export default function useRuleQueryInit() {
    const clusters = ref([]);
    const dataSourceTypes = [
        { label: 'HIVE', value: 1 },
        { label: 'MYSQL', value: 2 },
        { label: 'TDSQL', value: 3 },
        { label: 'KAFKA', value: 4 },
        { label: 'FPS', value: 5 },
    ];
    const databases = ref([]);
    const tables = ref([]);
    const subSystems = ref([]);
    const dataLabels = ref([]);
    const dcns = ref([]); // 等接口

    // 获取子系统中文名
    const getSystemNameNew = (data, tr) => tr.full_cn_name || tr.subSystemFullCnName || data;

    onMounted(async () => {
        try {
            const subSystemInfoRes = await fetchSubSystemInfo();
            const subSystemInfoResList = subSystemInfoRes || [];
            subSystems.value = subSystemInfoResList.map((item) => {
                const cnName = getSystemNameNew(item.subSystemId, item);
                return Object.assign({}, item, {
                    subSystemName: cnName,
                    enName: item.subSystemName,
                    cnName,
                    value: String(item.subSystemId),
                    label: item.subSystemName,
                });
            });
            const dataLabelsRes = await fetctdataLabels();
            const dataLabelsResList = dataLabelsRes.content || [];
            dataLabels.value = dataLabelsResList.map(item => Object.assign({}, item, {
                value: item.tag_code,
                label: item.tag_name,
            }));
            const {
                clusters: tempClusters,
                dbs: tempDatabases,
                tables: tempTables,
                envNames: tempEnvNames,
            } = await fetchOptions();
            if (Array.isArray(tempClusters)) clusters.value = tempClusters.filter(item => !!item);
            if (Array.isArray(tempDatabases)) databases.value = tempDatabases.filter(item => !!item);
            if (Array.isArray(tempTables)) {
                const tempFilterTables = tempTables.filter(item => !!item);
                tables.value = tempFilterTables.map(item => ({ label: item, value: item }));
            }
            if (Array.isArray(tempEnvNames)) {
                const tempFileterEnvNames = tempEnvNames.filter(item => !!item);
                dcns.value = tempFileterEnvNames.map(item => ({ label: item, value: item }));
            }
        } catch (error) {
            console.error(error);
        }
    });
    return {
        clusters,
        dataSourceTypes,
        databases,
        tables,
        dcns,
        subSystems,
        dataLabels,
    };
}
