import { ref } from 'vue';
import {
    getProxyUsers, getClusterList,
} from '@/components/rules/api';

export default function useProxyUsers() {
    const proxyUserList = ref([]);
    /**
     * 获取代理用户列表
     * @return {List<String>} 代理用户列表
     */
    const updateProxyUserList = async () => {
        const res = await getProxyUsers();
        if (Array.isArray(res) && res.length !== 0) {
            proxyUserList.value = res;
        } else if (sessionStorage.getItem('FesUserName')) {
            proxyUserList.value = [sessionStorage.getItem('FesUserName')];
        } else {
            proxyUserList.value = [];
        }
    };

    // 获取集群列表
    const clusterList = ref([]);
    const loadClusterList = async () => {
        const resp = await getClusterList({ start_index: 0, page_size: 100 });
        if (Array.isArray(resp.data) && resp.optional_clusters) {
            resp.data.forEach((item) => {
                item.disabled = resp.optional_clusters.indexOf(item.cluster_name) < 0;
            });
            clusterList.value = resp.data;
        } else {
            clusterList.value = [];
        }
    };

    return {
        proxyUserList,
        updateProxyUserList,
        clusterList,
        loadClusterList,
    };
}
