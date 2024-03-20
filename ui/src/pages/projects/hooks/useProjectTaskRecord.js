import { ref, reactive } from 'vue';
import { format } from 'date-fns';
import { fetchProjecTaskRecord } from '../api';

export default function useProjectHistory(projectId) {
    const taskRecordsPagination = reactive({
        current: 1,
        size: 10,
        total: 0,
    });
    const taskRecords = ref([]);
    const getProjectTaskRecord = async (queryData = {}) => {
        try {
            const params = {
                project_id: projectId,
                page: taskRecordsPagination.current - 1,
                size: taskRecordsPagination.size,
                ...queryData,
            };
            if (queryData?.taskEndTime?.length > 0) {
                params.start_finish_time = format(new Date(queryData.taskEndTime[0]), 'yyyy-MM-dd HH:mm:ss');
                params.end_finish_time = format(new Date(queryData.taskEndTime[1]), 'yyyy-MM-dd HH:mm:ss');
            }
            const { data, total } = await fetchProjecTaskRecord(params);
            taskRecords.value = data;
            taskRecordsPagination.total = total;
        } catch (error) {
            console.error('ERROR in getProjectTaskRecord: ', error);
        }
    };

    const resetTaskRecords = () => {
        taskRecordsPagination.current = 1;
        taskRecordsPagination.size = 10;
    };

    return {
        taskRecords,
        taskRecordsPagination,
        getProjectTaskRecord,
        resetTaskRecords,
    };
}
