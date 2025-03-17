import { ref } from 'vue';
import {
    loadClusterList, loadProjectList, loadWorkflowList, loadTaskList,
} from '../loadListData';
import {
    getAllWtssTask, fetchClusterList, getAllWtssProject, getAllWtssWorkFlow, getReleaseUser,
} from '../../api';

export default function getFormatOptions() {
    const clusterList = ref([]);
    const projectList = ref([]);
    const workflowList = ref([]);
    const taskList = ref([]);
    const releaseUserList = ref([]);

    const getQualitisCluster = async (projectId, type) => {
        const result = await loadClusterList({
            project_id: projectId,
            schedule_system: 'WTSS',
            task_type: type,
        });
        clusterList.value = result ? result.map(item => ({
            label: item,
            value: item,
        })) : [];
    };
    const getQualitisProject = async (projectId, type, cluster) => {
        const result = await loadProjectList({
            project_id: projectId,
            schedule_system: 'WTSS',
            task_type: type,
            cluster,
        });
        projectList.value = result ? result.map(item => ({
            label: item,
            value: item,
        })) : [];
    };
    const getQualitisWorkflow = async (type, cluster, project) => {
        const result = await loadWorkflowList({
            schedule_system: 'WTSS',
            task_type: type,
            cluster,
            project_name: project,
        });
        workflowList.value = result ? result.map(item => ({
            label: item,
            value: item,
        })) : [];
    };
    const getQualitisTask = async (type, cluster, project, workflow) => {
        const result = await loadTaskList({
            schedule_system: 'WTSS',
            task_type: type,
            cluster,
            project_name: project,
            work_flow: workflow,
        });
        taskList.value = result ? result.map(item => ({
            label: item,
            value: item,
        })) : [];
    };

    async function getClusterList() {
        try {
            const { data = [] } = await fetchClusterList();
            clusterList.value = data.map(item => ({
                value: item.cluster_name,
                label: item.cluster_name,
            }));
        } catch (error) {
            console.warn(error);
        }
    }

    async function getReleaseUserList(cluster) {
        try {
            const params = {
                cluster,
            };
            const { users = [] } = await getReleaseUser(params);
            releaseUserList.value = users.map(item => ({
                value: item,
                label: item,
            }));
        } catch (error) {
            console.warn(error);
        }
    }
    async function getProjectList(releaseUser, cluster) {
        try {
            const params = {
                release_user: releaseUser || '',
                cluster,
            };
            const { projects = [] } = await getAllWtssProject(params);
            projectList.value = projects.map(item => ({
                value: item,
                label: item,
            }));
        } catch (error) {
            console.warn(error);
        }
    }
    async function getWorkFlowList(releaseUser, project, cluster) {
        try {
            const params = { release_user: releaseUser || '', project_name: project, cluster };
            const { flows = [] } = await getAllWtssWorkFlow(params);
            workflowList.value = flows.map(item => ({
                value: item.flowId,
                label: item.flowId,
            }));
        } catch (error) {
            console.warn(error);
        }
    }

    async function getTaskList(project, workflow, releaseUser, cluster) {
        try {
            const params = {
                project,
                flow: workflow,
                release_user: releaseUser,
                cluster,
            };
            const { nodes = [] } = await getAllWtssTask(params);
            taskList.value = nodes.map(item => ({
                label: item.id,
                value: item.id,
            }));
        } catch (error) {
            console.warn(error);
        }
    }
    return {
        clusterList,
        releaseUserList,
        projectList,
        workflowList,
        taskList,
        getQualitisCluster,
        getQualitisProject,
        getQualitisWorkflow,
        getQualitisTask,
        getClusterList,
        getReleaseUserList,
        getProjectList,
        getWorkFlowList,
        getTaskList,
    };
}
