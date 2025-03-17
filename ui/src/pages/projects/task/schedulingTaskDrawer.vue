<template>
    <FDrawer v-model:show="drawerShow" displayDirective="if" :title="drawerTitle" :footer="true" :width="960" :oktext="$t('_.保存')" @ok="finishAssociate" @cancel="closeDrawer">
        <FForm ref="SFormRef" :labelWidth="69" labelPosition="right" :model="schedulingTask" :rules="rules" class="drawer-form">
            <FFormItem :label="$t('_.调度系统')" prop="sys">
                <FSelect v-model="schedulingTask.sys" class="form-select" filterable :options="sysTypeList"></FSelect>
            </FFormItem>
            <FFormItem :label="$t('_.集群')" prop="cluster">
                <FSelect v-model="schedulingTask.cluster" class="form-select" filterable clearable :options="clusterList" @change="clusterChange"></FSelect>
            </FFormItem>
            <FFormItem :label="$t('_.发布用户')" prop="releaseUser">
                <FSelect v-model="schedulingTask.releaseUser" class="form-select" filterable clearable :options="releaseUserList" @change="releaseUserChange"></FSelect>
            </FFormItem>
            <FFormItem :label="$t('_.项目')" prop="project">
                <FSelect v-model="schedulingTask.project" class="form-select" filterable clearable :options="projectList" @change="projectChange"></FSelect>
            </FFormItem>
            <ScheduleTaskForm
                v-if="formType === 'edit'"
                :ref="el => { if (el) scheduleTaskForm[0] = el; }"
                v-model:modelValue="schedulingTask"
                :project="schedulingTask.project"
                :releaseUser="schedulingTask.releaseUser"
                :cluster="schedulingTask.cluster"
                :sys="schedulingTask.sys"
                :workflowList="workflowList"
                :ruleGroupList="ruleGroupList"
                formType="edit"
            />
        </FForm>
        <div v-if="formType === 'create'">
            <div v-for="_,i in schedulingTask.tasks" :key="i" class="task-box" :class="i === (schedulingTask.tasks.length - 1) ? 'last-task' : ''">
                <div class="task-box-bar">
                    <span class="task-box-title">{{$t('_.任务')}}{{i + 1}}</span>
                    <span v-if="schedulingTask.tasks.length > 1" class="delete-btn" @click="deleteTask(i)">
                        <MinusCircleOutlined style="margin-right: 4.67px;" />{{$t('_.删除')}}</span>
                </div>
                <ScheduleTaskForm
                    :ref="el => { if (el) scheduleTaskForm[i] = el; }"
                    v-model:modelValue="schedulingTask.tasks[i]"
                    :project="schedulingTask.project"
                    :releaseUser="schedulingTask.releaseUser"
                    :cluster="schedulingTask.cluster"
                    :sys="schedulingTask.sys"
                    :workflowList="workflowList"
                    :ruleGroupList="ruleGroupList"
                />
            </div>
            <FButton class="add-btn" type="link" @click="addTask">
                <template #icon>
                    <PlusCircleOutlined />
                </template>{{$t('_.添加任务')}}
            </FButton>
        </div>
    </FDrawer>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';

import {
    computed,
    reactive,
    ref,
    defineEmits,
    defineProps,
    onMounted,
} from 'vue';
import { COMMON_REG } from '@/assets/js/const';
import { FCheckbox, FCheckboxGroup } from '@fesjs/fes-design';
import { PlusCircleOutlined, MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { cloneDeep } from 'lodash-es';
import {
    addScheduledTask, getAllWtssTask, getScheduledTaskRuleGroup, getScheduledTaskRules, modifyScheduledTask, fetchClusterList, getAllWtssProject, getAllWtssWorkFlow, getReleaseUser,
} from '../api';
import {
    loadClusterList, loadProjectList, loadWorkflowList, loadTaskList,
} from './loadListData';
import getFormatOptions from './hooks/useOption';
import selector from '../components/selector.vue';
import ScheduleTaskForm from './scheduleTaskForm.vue';


const { t: $t } = useI18n();

const props = defineProps({
    projectId: {
        type: Number,
        required: true,
    },
    formType: {
        type: String,
        required: true,
    },
    show: {
        type: Boolean,
        required: true,
    },
});
const emit = defineEmits(['finishAssociate', 'update:show']);
const drawerShow = computed({
    get: () => props.show,
    set: (value) => {
        emit('update:show', value);
    },
});
const {
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
} = getFormatOptions();
const drawerTitle = ref(props.formType === 'create' ? $t('_.新建关联调度') : $t('_.编辑调度任务'));
const sysTypeList = ref([{ label: 'WTSS', value: 'WTSS' }]);
const ruleGroupList = ref([]);
const databaseList = ref([]);
const dataTableList = ref([]);
// 关联调度任务初始化
const getInitialSchedulingTask = () => ({
    projectId: props.projectId,
    releaseUser: '',
    sys: 'WTSS',
    cluster: '',
    project: '',
    workflow: '',
    task: '',
    checkPosition: ['pre', 'post'], // 编辑时默认将两个位置都打开
    preCheckRuleGroup: [],
    postCheckRuleGroup: [],
    scheduledTaskId: '',
    tasks: [{
        workflow: '',
        task: '',
        checkPosition: [],
        preCheckRuleGroup: [],
        postCheckRuleGroup: [],
    }],
});
const schedulingTask = reactive(getInitialSchedulingTask());
const resetSchedulingTask = () => {
    Object.assign(schedulingTask, getInitialSchedulingTask());
};

// 表单校验
const SFormRef = ref(null);
const validateRuleGroup = () => Boolean(schedulingTask.preCheckRuleGroup.length > 0 || schedulingTask.postCheckRuleGroup.length > 0);
const rules = computed(() => ({
    sys: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
    },
    cluster: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择集群'),
    },
    releaseUser: {
        type: 'string',
        trigger: ['blur', 'change'],
    },
    project: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择项目'),
    },
    workflow: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择工作流'),
    },
    task: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择任务'),
    },
    checkPosition: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.至少选择一个校验位置'),
    },
    preCheckRuleGroup: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.校验规则组不能同时为空'),
    },
    postCheckRuleGroup: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.校验规则组不能为空'),
    },
}));
const scheduleTaskForm = ref([]);
const resetTasks = () => {
    scheduleTaskForm.value = [];
    schedulingTask.tasks = [{
        workflow: '',
        task: '',
        checkPosition: [],
        preCheckRuleGroup: [],
        postCheckRuleGroup: [],
    }];
    console.log('resetTasks');
};
const emptyTask = {
    workflow: '',
    task: '',
    checkPosition: [],
    preCheckRuleGroup: [],
    postCheckRuleGroup: [],
};
const addTask = () => {
    schedulingTask.tasks.push(cloneDeep(emptyTask));
};
const deleteTask = (index) => {
    schedulingTask.tasks.splice(index, 1);
    scheduleTaskForm.value.splice(index, 1);
    console.log('delete', index);
};
// 新增关联调度任务
const finishAssociate = async () => {
    try {
        await SFormRef.value.validate();
        const tasksValidResult = await Promise.all(scheduleTaskForm.value.map(item => item.valid()));
        if (tasksValidResult.includes(false)) {
            console.warn('校验未通过，请检查是否有字段不符合要求或为空');
            return;
        }
        console.log(schedulingTask);
        const {
            projectId, sys, cluster, releaseUser, project, workflow, task, preCheckRuleGroup, postCheckRuleGroup, checkPosition, tasks,
        } = schedulingTask;
        const params = {
            project_id: projectId,
            schedule_system: sys,
            cluster,
            project_name: project,
            work_flow: workflow,
            task_name: task,
            checkPosition,
            front_rule_group: checkPosition.includes('pre') ? preCheckRuleGroup : [],
            back_rule_group: checkPosition.includes('post') ? postCheckRuleGroup : [],
            release_user: releaseUser || '',
            task_list: tasks.map(item => ({
                work_flow: item.workflow,
                task_name: item.task,
                front_rule_group: item.checkPosition.includes('pre') ? item.preCheckRuleGroup : [],
                back_rule_group: item.checkPosition.includes('post') ? item.postCheckRuleGroup : [],
            })),
        };
        if (props.formType === 'create') {
            await addScheduledTask(params);
        } else {
            params.scheduled_task_id = schedulingTask.scheduledTaskId;
            await modifyScheduledTask(params);
        }
        drawerShow.value = false;
        emit('finishAssociate');
        resetSchedulingTask();
    } catch (err) {
        console.warn(err);
    }
};

// 拉规则组
const preCheckRuleGroupList = ref([]);
const postCheckRuleGroupList = ref([]);
// 重置规则组
function resetRuleGroup() {
    preCheckRuleGroupList.value = [];
    postCheckRuleGroupList.value = [];
    schedulingTask.preCheckRuleGroup = [];
    schedulingTask.postCheckRuleGroup = [];
}
async function getRuleGroup(id) {
    try {
        const res = await getScheduledTaskRuleGroup(id);
        ruleGroupList.value = Array.isArray(res) ? res : [];
        ruleGroupList.value.forEach((item) => {
            item.label = item.rule_group_name;
            item.value = item.rule_group_id;
        });
        // 深拷贝为前置规则组列表和后置规则组列表
        preCheckRuleGroupList.value = ruleGroupList.value.map(i => Object.assign({}, i));
        postCheckRuleGroupList.value = ruleGroupList.value.map(i => Object.assign({}, i));
    } catch (error) {
        console.warn(error);
    }
}
// 根据集群来获取项目列表和发布用户列表
function clusterChange(value) {
    schedulingTask.releaseUser = '';
    schedulingTask.project = '';
    schedulingTask.workflow = '';
    schedulingTask.task = '';
    releaseUserList.value = [];
    projectList.value = [];
    workflowList.value = [];
    taskList.value = [];
    resetTasks();
    if (value) {
        getReleaseUserList(schedulingTask.cluster);
        if (props.formType === 'edit') {
            resetRuleGroup();
            getQualitisProject(props.projectId, 1, schedulingTask.cluster);
        } else {
            getProjectList(schedulingTask.releaseUser || '', value);
        }
    }
}
// 根据发布用户和集群来获取项目列表(新建)
function releaseUserChange(value) {
    if (props.formType === 'create') {
        schedulingTask.project = '';
        schedulingTask.workflow = '';
        schedulingTask.task = '';
        workflowList.value = [];
        taskList.value = [];
        // if (props.formType === 'edit') {
        //     resetRuleGroup();
        // }
        resetTasks();
        if (schedulingTask.cluster) {
            getProjectList(value || '', schedulingTask.cluster);
        }
    }
}
// 根据集群、项目和发布用户来获取工作流列表
function projectChange(value) {
    schedulingTask.workflow = '';
    schedulingTask.task = '';
    workflowList.value = [];
    taskList.value = [];
    resetTasks();
    if (value) {
        const { cluster, releaseUser, project } = schedulingTask;
        if (props.formType === 'edit') {
            resetRuleGroup();
            getQualitisWorkflow(1, cluster, project);
        } else {
            getWorkFlowList(releaseUser, project, cluster);
        }
    }
}
// 根据集群、发布用户、项目和工作流来获取任务列表
function workflowChange(value) {
    schedulingTask.task = '';
    taskList.value = [];
    if (value) {
        const {
            project,
            workflow,
            releaseUser,
            cluster,
        } = schedulingTask;
        if (props.formType === 'edit') {
            resetRuleGroup();
            getQualitisTask(1, cluster, project, workflow);
        } else {
            getTaskList(
                project,
                workflow,
                releaseUser,
                cluster,
            );
        }
    }
}
// 编辑模式下根据参数来获取具体任务
async function taskChange() {
    if (schedulingTask.task && props.formType === 'edit') {
        const params = {
            schedule_system: schedulingTask.sys,
            cluster: schedulingTask.cluster,
            project_name: schedulingTask.project,
            work_flow: schedulingTask.workflow,
            task_name: schedulingTask.task,
        };
        const res = await getScheduledTaskRules(params);
        schedulingTask.scheduledTaskId = res.scheduled_task_id;
        schedulingTask.releaseUser = res.release_user;
        schedulingTask.preCheckRuleGroup = [];
        schedulingTask.postCheckRuleGroup = [];
        res.rule_group_type.forEach((item) => {
            if (item.trigger_type === 1) {
                // 前置
                schedulingTask.preCheckRuleGroup.push(item.rule_group_id);
            } else {
                // 后置
                schedulingTask.postCheckRuleGroup.push(item.rule_group_id);
            }
        });
    } else if (!schedulingTask.task && props.formType === 'edit') {
        schedulingTask.preCheckRuleGroup = [];
        schedulingTask.postCheckRuleGroup = [];
    }
}

onMounted(async () => {
    if (props.formType === 'create') {
        await getClusterList();
    } else {
        await getQualitisCluster(props.projectId, 1);
    }
    await getRuleGroup(props.projectId);
});
// 关闭drawer
function closeDrawer() {
    resetSchedulingTask();
}

</script>
<style scoped lang="less">
:deep(.label-text) {
    text-align: right;
    margin-top: 3px;
}
.task-box{
    border: 1px solid #F1F1F2;
    border-radius: 4px;
    padding: 16px 16px 0 16px;
    margin-bottom: 22px;
    &-bar{
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
    }
    &-title{
        color: #0F1222;
    }
    .delete-btn{
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }
    &.last-task{
        margin-bottom: 0;
    }
}
.add-btn{
    padding: 0;
    height: fit-content;
    margin-top: 8px;
}
</style>
