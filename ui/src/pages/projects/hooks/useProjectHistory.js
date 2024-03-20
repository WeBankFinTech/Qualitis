import { ref, reactive } from 'vue';
import { fetchProjectHistory } from '../api';

export default function useProjectHistory(projectId) {
    const historyPagination = reactive({
        current: 1,
        size: 10,
        total: 0,
    });
    const history = ref([]);
    const getProjectHistory = async () => {
        try {
            const params = {
                project_id: projectId,
                page: historyPagination.current - 1,
                size: historyPagination.size,
            };
            const { data, total } = await fetchProjectHistory(projectId, params);
            history.value = data;
            historyPagination.total = total;
        } catch (error) {
            console.error('ERROR in getProjectHistory: ', error);
        }
    };

    const historyPageChange = (reset = false) => {
        if (reset) {
            historyPagination.current = 1;
        }
        getProjectHistory();
    };

    const resetHistoryList = () => {
        historyPagination.current = 1;
        historyPagination.size = 10;
    };

    return {
        history,
        historyPagination,
        getProjectHistory,
        historyPageChange,
        resetHistoryList,
    };
}
