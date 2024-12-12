<template>
    <div class="query-container">
        <div class="query-item">
            <span class="query-label">查询条件</span>
            <FSelect
                v-model="queryCondition"
                :width="160"
                filterable
                clearable
                valueField="value"
                labelField="label"
                :options="queryConditionsList"
                @change="change('condition')"
            ></FSelect>
        </div>
        <!-- 调度任务维度 -->
        <div v-show="queryCondition === 'schedulingTask'" class="query-item">
            <span class="query-label">调度系统类型</span>
            <FSelect
                v-model="queryData.sysType"
                :width="160"
                filterable
                clearable
                :options="sysTypeList"
                @change="change('sys')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'schedulingTask'" class="query-item">
            <span class="query-label">集群</span>
            <FSelect
                v-model="queryData.cluster"
                :width="160"
                filterable
                clearable
                :options="clusterList"
                @change="change('cluster')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'schedulingTask'" class="query-item">
            <span class="query-label">项目</span>
            <FSelect
                v-model="queryData.project"
                :width="160"
                filterable
                clearable
                :options="projectList"
                @change="change('project')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'schedulingTask'" class="query-item">
            <span class="query-label">工作流</span>
            <FSelect
                v-model="queryData.workflow"
                :width="160"
                filterable
                clearable
                :options="workflowList"
                @change="change('workflow')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'schedulingTask'" class="query-item">
            <span class="query-label">任务</span>
            <FSelect
                v-model="queryData.task"
                :width="160"
                filterable
                clearable
                :options="taskList"
                @change="change('task')"
            ></FSelect>
        </div>
        <!-- 规则组维度 -->
        <div v-show="queryCondition === 'ruleGroup'" class="query-item">
            <span class="query-label">规则组名称</span>
            <FSelect
                v-model="queryData.ruleGroup"
                :width="160"
                filterable
                clearable
                :options="ruleGroupList"
                @change="change('rule_group_name')"
            ></FSelect>
        </div>
        <!-- 数据维度 -->
        <div v-show="queryCondition === 'data'" class="query-item">
            <span class="query-label">集群</span>
            <FSelect
                v-model="queryData.cluster"
                :width="160"
                filterable
                clearable
                :options="clusterList"
                @change="change('cluster')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'data'" class="query-item">
            <span class="query-label">数据库</span>
            <FSelect
                v-model="queryData.database"
                :width="160"
                filterable
                clearable
                :options="databaseList"
                @change="change('database')"
            ></FSelect>
        </div>
        <div v-show="queryCondition === 'data'" class="query-item">
            <span class="query-label">数据表</span>
            <FSelect
                v-model="queryData.dataTable"
                :width="160"
                filterable
                clearable
                :options="dataTableList"
                @change="change('data_table')"
            ></FSelect>
        </div>
        <div class="query-item">
            <FButton type="primary" class="operation-item white" @click="search">查询</FButton>
            <FButton class="operation-item" @click="reset">重置</FButton>
        </div>
    </div>
</template>
<script setup>
import {
    defineEmits, defineProps, onMounted, ref,
} from 'vue';
import { request as FRequest } from '@fesjs/fes';
import {
    loadDatasource, loadRuleGroupList, loadSysTypeList, loadClusterList, loadProjectList, loadWorkflowList, loadTaskList, loadPublishClusterList, loadDBTable, loadInitOptions,
} from '../task/loadListData';
import {
    getQueryOptionsRuleGroup, fetchClusterList,
} from '../api';

const props = defineProps({
    // 待查询任务列表所在的项目ID
    queryFromProjectID: {
        type: Number,
        required: true,
    },
    // 查询组件类型：1.调度任务 2.发布调度任务
    type: {
        type: Number,
        required: true,
    },
});
const emit = defineEmits(['search', 'reset']);

const queryConditionsList = ref([
    {
        value: 'schedulingTask',
        label: '调度任务',
    },
    {
        value: 'ruleGroup',
        label: '规则组',
    },
    {
        value: 'data',
        label: '数据',
    },
]);

// 查询组件
const queryCondition = ref('');
const queryData = ref({
    sysType: '',
    cluster: '',
    project: '',
    workflow: '',
    task: '',
    ruleGroup: '',
    database: '',
    dataTable: '',
});
const emptyQueryData = {
    sysType: '',
    cluster: '',
    project: '',
    workflow: '',
    task: '',
    ruleGroup: '',
    database: '',
    dataTable: '',
};
const search = () => {
    emit('search', queryData.value);
};
const resetQueryData = () => {
    Object.assign(queryData.value, emptyQueryData);
};

const sysTypeList = ref([]);
const clusterList = ref([]);
const projectList = ref([]);
const workflowList = ref([]);
const taskList = ref([]);
const ruleGroupList = ref([]);
const datasourceList = ref([]);
const databaseList = ref([]);
const dataTableList = ref([]);
const resetOptions = () => {
    clusterList.value = [];
    projectList.value = [];
    workflowList.value = [];
    taskList.value = [];
    ruleGroupList.value = [];
    datasourceList.value = [];
    databaseList.value = [];
    dataTableList.value = [];
};
const initOptions = async () => {
    resetOptions();
    const result = await loadInitOptions({ task_type: 1, project_id: props.queryFromProjectID });
    clusterList.value = result?.cluster_name?.map(item => ({
        value: item,
        label: item,
    })) || [];
    projectList.value = result?.wtss_project_name?.map(item => ({
        value: item,
        label: item,
    })) || [];
    taskList.value = result?.wtss_task_name?.map(item => ({
        value: item,
        label: item,
    })) || [];
    workflowList.value = result?.wtss_work_flow_name?.map(item => ({
        value: item,
        label: item,
    })) || [];
    datasourceList.value = await loadDBTable({ project_id: props.queryFromProjectID, task_type: 1 });
    databaseList.value = datasourceList.value?.map(item => ({
        value: item.db,
        label: item.db,
    })) || [];
    datasourceList.value?.forEach((item) => {
        item?.table_list?.forEach(table => (dataTableList.value.push({
            value: table.table_name,
            label: table.table_name,
        })));
    });
    console.log(clusterList.value, 'clusetr');
    console.log(projectList.value, 'project');
    console.log(workflowList.value, 'workflow');
    console.log(taskList.value, 'task');
    console.log(databaseList.value, 'database');
    console.log(dataTableList.value, 'dataTable');
};
// TODO 进来都先初始化
if (props.type === 1) {
    initOptions();
}


// 查询条件各list的数据加载
const genDatabaseList = () => {
    databaseList.value = datasourceList.value.map(item => ({
        label: item.db,
        value: item.db,
    }));
};

const genDataTabelList = async () => {
    dataTableList.value = datasourceList.value.reduce((pre, cur) => pre.concat(
        cur?.table_list.map(item => ({ value: item.table_name, label: item.table_name })),
    ), []) || [];
};

const genRuleGroupList = async () => {
    const result = await loadRuleGroupList({ projectId: props.queryFromProjectID });
    ruleGroupList.value = result ? result.map(item => ({
        label: item.rule_group_name,
        value: item.rule_group_id,
    })) : [];
};

const genSysTypeList = async () => {
    const result = await loadSysTypeList();
    sysTypeList.value = result ? result.map(item => ({
        label: item.message,
        value: item.message,
    })) : [];
};

const genClusterList = async () => {
    const result = await loadClusterList({
        project_id: props.queryFromProjectID,
        schedule_system: queryData.value.sysType || '',
        task_type: props.type,
    });
    clusterList.value = result ? result.map(item => ({
        label: item,
        value: item,
    })) : [];
};

const genProjectList = async () => {
    const result = await loadProjectList({
        project_id: props.queryFromProjectID,
        schedule_system: queryData.value.sysType || '',
        task_type: props.type,
        cluster: queryData.value.cluster || '',
    });
    projectList.value = result ? result.map(item => ({
        label: item,
        value: item,
    })) : [];
};

const genWorkflowList = async () => {
    const result = await loadWorkflowList({
        project_id: props.queryFromProjectID,
        schedule_system: queryData.value.sysType || '',
        task_type: props.type,
        cluster: queryData.value.cluster || '',
        project_name: queryData.value.project || '',
    });
    workflowList.value = result ? result.map(item => ({
        label: item,
        value: item,
    })) : [];
};

const genTaskList = async () => {
    const result = await loadTaskList({
        project_id: props.queryFromProjectID,
        schedule_system: queryData.value.sysType || '',
        task_type: props.type,
        cluster: queryData.value.cluster || '',
        project_name: queryData.value.project || '',
        work_flow: queryData.value.workflow || '',
    });
    taskList.value = result ? result.map(item => ({
        label: item,
        value: item,
    })) : [];
};

const genDataClusterList = async () => {
    const result = await loadPublishClusterList();
    clusterList.value = result ? result.optional_clusters.map(item => ({
        label: item,
        value: item,
    })) : [];
};


const initAllQueryList = async () => {
    genDatabaseList();
    genDataTabelList();
    await genRuleGroupList();
    await genSysTypeList();
    await genProjectList();
    await genWorkflowList();
    await genTaskList();
};

const reset = async () => {
    resetQueryData();
    queryCondition.value = '';
    if (props.type === 2) {
        await initAllQueryList();
    } else {
        await initOptions();
    }
    emit('reset', queryData.value);
};

const handleConditionChange = async () => {
    if (queryCondition.value === 'ruleGroup') {
        // 请求ruleGroupList
        if (props.type === 1) {
            const res = await getQueryOptionsRuleGroup({
                project_id: props.queryFromProjectID,
                task_type: 1,
            });
            ruleGroupList.value = Array.isArray(res) ? res : [];
            ruleGroupList.value.forEach((item) => {
                item.label = item.rule_group_name;
                item.value = item.rule_group_id;
            });
        } else {
            await genRuleGroupList();
        }
    } else if (queryCondition.value === 'data') {
        // 请求clusterList
        if (props.type === 2) {
            await genDataClusterList();
        }
    } else if (queryCondition.value === 'schedulingTask') {
        await genSysTypeList();
        await genClusterList();
    }
    resetQueryData();
};

const handleSysChange = async () => {
    projectList.value = [];
    workflowList.value = [];
    taskList.value = [];
    queryData.value.cluster = '';
    queryData.value.project = '';
    queryData.value.workflow = '';
    queryData.value.task = '';
    if (queryData.value.sysType) {
        await genClusterList();
    } else {
        clusterList.value = [];
        if (props.type === 1) {
            await initOptions();
        }
    }
};

const handleClusterChange = async () => {
    workflowList.value = [];
    taskList.value = [];
    queryData.value.project = '';
    queryData.value.workflow = '';
    queryData.value.task = '';
    if (queryCondition.value === 'schedulingTask') {
        // 重置项目、工作流、任务, 请求project列表, 重置workflow、task列表
        if (queryData.value.cluster) {
            await genProjectList();
        } else {
            projectList.value = [];
        }
    } else if (queryCondition.value === 'data' && props.type === 1) {
        queryData.value.database = '';
        queryData.value.dataTable = '';
        databaseList.value = [];
        dataTableList.value = [];
        if (queryData.value.cluster) {
            databaseList.value = datasourceList.value?.map(item => ({
                value: item.db,
                label: item.db,
            })) || [];
        } else {
            databaseList.value = datasourceList.value?.map(item => ({
                value: item.db,
                label: item.db,
            })) || [];
            datasourceList.value?.forEach((item) => {
                item?.table_list?.forEach(table => (dataTableList.value.push({
                    value: table.table_name,
                    label: table.table_name,
                })));
            });
        }
    }
};

const handleProjectChange = async () => {
    if (queryData.value.project) {
        await genWorkflowList();
    } else {
        workflowList.value = [];
    }
    taskList.value = [];
    queryData.value.workflow = '';
    queryData.value.task = '';
};

const handleWorkflowChange = async () => {
    if (queryData.value.workflow) {
        await genTaskList();
    } else {
        taskList.value = [];
    }
    queryData.value.task = '';
};

const handleDatabaseChange = async () => {
    if (queryData.value.database) {
        if (props.type === 1) {
            dataTableList.value = datasourceList.value
                ?.filter(item => item.db === queryData.value.database)[0]?.table_list
                ?.map(item => ({ value: item.table_name, label: item.table_name })) || [];
        } else {
            const temp = datasourceList.value.filter(database => database.db === queryData.value.database);
            if (temp.length > 0) {
                dataTableList.value = temp[0]?.table_list.map(item => ({
                    label: item.table_name,
                    value: item.table_name,
                }));
            } else {
                dataTableList.value = [];
            }
        }
    } else if (props.type === 1 && !queryData.value.database && queryData.value.cluster) {
        dataTableList.value = [];
    } else if (props.type === 1 && !queryData.value.database && !queryData.value.cluster) {
        dataTableList.value = [];
        datasourceList.value?.forEach((item) => {
            item?.table_list?.forEach(table => (dataTableList.value.push({
                value: table.table_name,
                label: table.table_name,
            })));
        });
    } else if (props.type === 2 && !queryData.value.database) {
        dataTableList.value = [];
    }
    queryData.value.dataTable = '';
};

const change = async (type) => {
    console.log(queryData.value);
    switch (type) {
        // 重置整个查询条件对象
        case 'condition':
            await handleConditionChange();
            break;
        // 重置集群、项目、工作流、任务，请求cluster列表, 重置project，workflow，task列表
        case 'sys':
            await handleSysChange();
            break;
        case 'cluster':
            await handleClusterChange();
            break;
        // 重置工作流、任务，请求workflow列表，重置task列表
        case 'project':
            await handleProjectChange();
            break;
        // 重置任务，请求task列表
        case 'workflow':
            await handleWorkflowChange();
            break;
        // 重置数据表, 请求dataTable列表
        case 'database':
            await handleDatabaseChange();
            break;
        default:
            break;
    }
};

onMounted(async () => {
    if (props.type === 2) {
        const result = await loadDatasource({ project_id: props.queryFromProjectID });
        datasourceList.value = result || [];
        await initAllQueryList();
    }
});

</script>
<style lang="less" scoped>
.query-container {
    display: flex;
    flex-wrap: wrap;
    .query-item,
    .operation-item {
        margin-right: 22px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0f1222;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        &.white {
            color: #ffffff;
        }
    }
    .query-item {
        margin-bottom: 22px;
        display: flex;
        align-items: center;
    }
    .selected-count {
        background: rgba(83, 132, 255, 0.06);
        border: 1px solid #5384ff;
        color: #5384ff;
    }
    .query-input {
        width: 160px;
    }
    .query-label {
        margin-right: 16px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0f1222;
    }
    .fes-select {
        width: 160px;
    }
}
</style>
