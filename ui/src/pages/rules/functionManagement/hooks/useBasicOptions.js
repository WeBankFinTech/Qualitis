import { ref } from 'vue';
import { request } from '@fesjs/fes';

export default function useBasicOptions() {
    const clusterList = ref([]);
    const engineList = ref([{ value: 1, label: 'Spark' }, { value: 2, label: 'Hive' }, { value: 3, label: 'Flink' }]);
    const implTypeList = ref([{ value: 1, label: 'jar' }, { value: 2, label: 'scala' }, { value: 3, label: 'python' }]);
    const dirList = ref([]);
    async function getClusterList() {
        const resp = await request('/api/v1/projector/meta_data/cluster', { start_index: 0, page_size: 100 });
        if (Array.isArray(resp.data) && resp.optional_clusters) {
            resp.data.forEach((item) => {
                item.disabled = resp.optional_clusters.indexOf(item.cluster_name) < 0;
            });
            clusterList.value = resp.data;
        } else {
            clusterList.value = [];
        }
    }

    // eslint-disable-next-line camelcase
    async function handleClusterChange(cluster_name = '') {
        try {
            const res = await request('/api/v1/projector/meta_data/udf/directory', { category: 'udf', cluster_name }, 'get');
            if (Array.isArray(res)) {
                dirList.value = res.map(item => ({ value: item, label: item }));
            } else {
                dirList.value = [];
            }
        } catch (err) {
            console.warn(err);
        }
    }
    return {
        clusterList,
        engineList,
        implTypeList,
        dirList,
        getClusterList,
        handleClusterChange,
    };
}
