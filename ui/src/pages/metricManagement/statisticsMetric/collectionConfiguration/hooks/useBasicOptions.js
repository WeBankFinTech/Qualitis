import { ref, onMounted } from 'vue';
import { request } from '@fesjs/fes';

export default function useBasicOptions() {
    // 采集配置
    const clusterVivisions = ref([]);
    const buildDivisionOption = data => ({
        type: 'cluster',
        label: data,
        value: data,
        isLeaf: false,
        children: [],
    });
    async function getClusterLists() {
        try {
            const resp = await request('api/v1/projector/imsmetric/data_source/conditions', {}, { method: 'get' });
            return resp;
        } catch (err) {
            console.warn(err);
            return null;
        }
    }
    async function getDbsLists(cluster = '', db = '') {
        try {
            const resp = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster }, { method: 'get' });
            if (Array.isArray(resp.dbs)) {
                return resp.dbs.map(item => ({ value: item, label: item }));
            }
            return [];
        } catch (err) {
            console.warn(err);
            return [];
        }
    }
    async function getPartitionList({ cluster_name, database, table }) {
        try {
            const res = await request('api/v1/projector/imsmetric/collect/partition/list', { cluster_name, database, table });
            if (Array.isArray(res)) {
                return res.map(item => ({ value: item, label: item }));
            }
            return [];
        } catch (err) {
            console.warn(err);
            return [];
        }
    }

    const clusterList = ref([]);
    const dbsLists = ref([]);
    const tableList = ref([]);
    const partitionLists = ref([]);
    const getClusterList = async () => {
        const res = await getClusterLists();
        clusterList.value = res?.clusters?.map(item => ({ label: item, value: item })) ?? [];
        dbsLists.value = res?.dbs?.map(item => ({ label: item, value: item })) ?? [];
        tableList.value = res?.tables?.map(item => ({ label: item, value: item })) ?? [];
    };
        // 手动触发下一级节点列表，返回列表的形式
    const getClusterVivisions = async () => {
        const res = await request('/api/v1/projector/meta_data/cluster', {});
        clusterVivisions.value = (res?.optional_clusters || []).map(buildDivisionOption);
    };
    const loadData = async (node) => {
        if (node) {
            switch (node.type) {
                case 'cluster':
                    // eslint-disable-next-line no-case-declarations
                    const { data = [] } = await request('/api/v1/projector/meta_data/db', {
                        cluster_name: node.value,
                        page_size: 2147483647,
                        proxy_user: '',
                        source_type: '',
                        start_index: 0,
                    });
                    // eslint-disable-next-line no-case-declarations
                    const dbsListmiddle = (data || []).map(item => ({
                        type: 'dbs',
                        label: item.db_name,
                        value: item.db_name,
                        isLeaf: false,
                        children: [],
                        cut: `${node.value}&&${item.db_name}`,
                    }));
                    node.children = dbsListmiddle;
                    return node.children;
                case 'dbs':
                    // eslint-disable-next-line no-case-declarations
                    const paramss = node.cut.split('&&');
                    // eslint-disable-next-line no-case-declarations
                    const resss = await request('/api/v1/projector/meta_data/table', {
                        cluster_name: paramss[0],
                        db_name: paramss[1],
                        page_size: 2147483647,
                        proxy_user: '',
                        source_type: '',
                        start_index: 0,
                    });
                    // eslint-disable-next-line no-case-declarations
                    const tablelast = (resss.data || []).map(item => ({
                        type: 'table',
                        label: item.table_name,
                        value: item.table_name,
                        isLeaf: true,
                    }));
                    node.children = tablelast;
                    return node.children;
                default:
                    break;
            }
        }
    };
    const userList = ref([]);
    const getUserLists = async () => {
        const res = await request('/api/v1/projector/project_user/user', {}, { method: 'get' });
        userList.value = res.filter(i => i).map(item => ({
            label: item,
            value: item,
        }));
        const respartition = await request('/api/v1/projector/imsmetric/collect/list', {});
        partitionLists.value = respartition?.data?.map(item => ({ value: item.partition, label: item.partition })) ?? [];
    };
    // 采集算子LIST
    const templateList = ref([]);
    const getTemplateList = async () => {
        const res = await request('api/v1/projector/imsmetric/collect/template/list', { page: 0, size: 9999 });
        templateList.value = res.data.map((item) => {
            if (item.template_name === '采集表行数统计') {
                return { ...item, disabled: true };
            }
            return item;
        });
    };
    // 采集调度
    const scheduleCluster = ref([]);
    onMounted(() => {
        getClusterList();
        getUserLists();
        getTemplateList();
        getClusterVivisions();
    });

    return {
        clusterList,
        tableList,
        dbsLists,
        partitionLists,
        clusterVivisions,
        getPartitionList,
        getDbsLists,
        loadData,
        userList,
        templateList,
        getClusterLists,
    };
}
