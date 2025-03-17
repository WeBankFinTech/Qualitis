import { ref, onMounted, reactive } from 'vue';
import dayjs from 'dayjs';
import { cloneDeep } from 'lodash-es';
import {
    fetchImsMetricList, fetchImsMetricOptions, fetchImsMetricTemplates, fetchImsMetricUsers,
} from './api';

export default function useMetricList() {
    const pagination = reactive({
        page: 1,
        size: 10,
        total: 0,
    });
    const tempParams = ref({});
    // 下拉框数据
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
    const columns = ref([]);
    const tableData = ref([]);
    const templates = ref([]);
    const users = ref([]);

    // 更新临时查询参数
    // eslint-disable-next-line camelcase
    const updateTempParams = (params) => {
        tempParams.value = { ...params };
    };
    // 获取指标列表
    const fetchTableData = async () => {
        try {
            const { page, size } = pagination;
            const body = { ...cloneDeep(tempParams.value), page: page - 1, size };

            if (body.gather_time && body.gather_time.length > 0) {
                body.start_date = dayjs(body.gather_time[0]).format('YYYY-MM-DD');
                body.end_date = dayjs(body.gather_time[1]).format('YYYY-MM-DD');
                delete body.gather_time;
            }
            const res = await fetchImsMetricList(body);
            pagination.total = res.total;
            tableData.value = res.data;
        } catch (err) {
            console.warn(err);
        }
    };
    // 获取下拉框列表
    const InitOptions = async () => {
        try {
            const res = await fetchImsMetricOptions();
            clusters.value = res.clusters.map(item => ({ label: item, value: item }));
            databases.value = res.dbs.map(item => ({ label: item, value: item }));
            tables.value = res.tables.map(item => ({ label: item, value: item }));
            columns.value = res.columns.map(item => ({ label: item, value: item }));
            const templateRes = await fetchImsMetricTemplates();
            templates.value = templateRes.data.map(item => ({ label: item.template_name, value: item.template_id }));
            const userRes = await fetchImsMetricUsers();
            users.value = userRes.map(item => ({ label: item, value: item }));
            // const columnsRes = await fetchImsMetricColumns();
            // columns.value = columnsRes.map(item => ({ label: item, value: item }));
            // columns.value = res.columns;
        } catch (err) {
            console.warn(err);
        }
    };
    // eslint-disable-next-line camelcase
    // const getColumns = async (cluster_name, db_name, table_name) => {
    //     try {
    //         const res = await fetchImsMetricColumns({ cluster_name, db_name, table_name });
    //         columns.value = res.columns.map(item => ({ label: item, value: item }));
    //     } catch (err) {
    //         console.warn(err);
    //     }
    // };
    // onMounted(() => {
    //     InitOptions();
    // });
    return {
        clusters,
        dataSourceTypes,
        databases,
        tables,
        columns,
        tableData,
        fetchTableData,
        updateTempParams,
        pagination,
        templates,
        users,
        InitOptions,
    };
}
