import { onMounted, ref } from 'vue';
import {
    fetchWorkflowFilterList,
} from '../api';

export default function useWorkflowRelList(projectId, type = 1) { // type=1 工作流项目筛选下拉 type=2 数据告警筛选下拉 type=-1 非工作流项目，不请求
    const workflowSpaceList = ref([]);
    const workflowNameList = ref([]);
    const workflowProjectList = ref([]);
    const workflowTaskList = ref([]);
    const alarmTableList = ref([]);

    const getWorkflowFilterList = async () => {
        try {
            const result = await fetchWorkflowFilterList({ project_id: projectId, type });
            workflowSpaceList.value = result?.work_flow_space.map(item => ({ value: item, label: item }));
            workflowNameList.value = result?.work_flow_name.map(item => ({ value: item, label: item }));
            workflowProjectList.value = result?.work_flow_project.map(item => ({ value: item, label: item }));
            workflowTaskList.value = result?.node_name.map(item => ({ value: item, label: item }));
            alarmTableList.value = result?.alert_table.map(item => ({ value: item, label: item }));
        } catch (error) {
            console.log('getworkflowSpaceList error:', error);
        }
    };

    onMounted(() => {
        if (type !== -1) {
            getWorkflowFilterList();
        }
    });

    return {
        workflowSpaceList,
        workflowNameList,
        workflowProjectList,
        workflowTaskList,
        alarmTableList,
        getWorkflowFilterList,
    };
}
