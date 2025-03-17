<template>
    <div class="wd-content">
        <div class="wd-project-header">
            <LeftOutlined class="back" @click="backToProjectList" />
            <div class="name">{{$t('_.关联调度任务')}}</div>
        </div>
        <div class="wd-content-body">
            <query-filter
                :queryFromProjectID="projectId"
                :type="1"
                @search="queryFilterSearch"
                @reset="queryFilterReset"
            ></query-filter>
            <div class="division"></div>
            <FButton
                type="primary"
                class="button"
                @click="associateScheduleTask"
            >
                <PlusOutlined />{{$t('_.新建关联调度')}}
            </FButton>
            <FButton
                type="primary"
                class="button"
                @click="publishScheduledTask"
            >
                <fes-icon type="publish" class="button-icon" />{{$t('_.发布关联调度')}}
            </FButton>
            <FButton type="default" class="button" @click="editScheduleTask">
                <EditOutlined />{{$t('_.编辑关联调度')}}
            </FButton>
            <div class="table-container">
                <div v-if="isLoading">
                    <BPageLoading :loadingText="{ loading: '' }" />
                </div>
                <template v-else>
                    <f-table
                        ref="taskTable"
                        rowKey="rule_group_name"
                        :data="scheduledTaskTableData"
                        :no-data-text="$t('common.noData')"
                    >
                        <template #empty>
                            <BPageLoading
                                :actionType="
                                    resultByInit
                                        ? 'emptyInitResult'
                                        : 'emptyQueryResult'
                                "
                            />
                        </template>
                        <f-table-column
                            v-slot="{ row }"
                            prop="rule_group_name"
                            :label="$t('schedulingTask.ruleGroupName')"
                            align="left"
                            :colStyle="ruleColStyle"
                            :minWidth="160"
                            ellipsis
                        >
                            <span
                                class="linkProject"
                                @click="clickRuleGroup(row)"
                            >{{row.rule_group_name}}</span
                            >
                        </f-table-column>
                        <f-table-column
                            v-slot="{ row }"
                            prop="front_or_back"
                            :label="$t('schedulingTask.frontOrBack')"
                            align="left"
                            :minWidth="200"
                            ellipsis
                        >
                            <span>{{row.front_or_back === 1
                                ? $t('_.前置规则组')
                                : $t('_.后置规则组')}}</span>
                        </f-table-column>
                        <f-table-column
                            prop="database"
                            :label="$t('schedulingTask.database')"
                            align="left"
                            :minWidth="200"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="scheduling_system_type"
                            :label="$t('schedulingTask.schedulingSystemType')"
                            align="left"
                            :minWidth="116"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="cluster"
                            :label="$t('schedulingTask.cluster')"
                            align="left"
                            :minWidth="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="project"
                            :label="$t('schedulingTask.project')"
                            align="left"
                            :minWidth="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="workflow"
                            :label="$t('schedulingTask.workflow')"
                            align="left"
                            :minWidth="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="task"
                            :label="$t('schedulingTask.task')"
                            align="left"
                            :minWidth="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            v-slot="{ row }"
                            prop="pubStatus"
                            :label="$t('schedulingTask.pubStatus')"
                            align="left"
                            :minWidth="80"
                            ellipsis
                        >
                            <span :class="row.pubStatus ? 'pub' : 'unpub'">
                                {{formmaterPubStatud(row.pubStatus)}}
                            </span>
                        </f-table-column>
                        <f-table-column
                            #default="{ row } = {}"
                            prop="operation"
                            :label="$t('schedulingTask.operation')"
                            align="left"
                            :minWidth="104"
                            ellipsis
                            fixed="right"
                        >
                            <span class="edit" @click="editTask(row)"
                            >{{$t('_.编辑')}}</span
                            >
                            <span class="delete" @click="deleteTask(row)"
                            >{{$t('_.删除')}}</span
                            >
                        </f-table-column>
                    </f-table>
                    <div class="table-pagination-container">
                        <FPagination
                            v-model:currentPage="pagination.page"
                            v-model:pageSize="pagination.size"
                            show-size-changer
                            show-total
                            :total-count="pagination.total"
                            @change="loadScheduledTaskTableData"
                            @pageSizeChange="loadScheduledTaskTableData"
                        ></FPagination>
                    </div>
                </template>
            </div>
        </div>
        <scheduling-task-drawer
            v-if="drawerShow"
            v-model:show="drawerShow"
            :formType="formType"
            :projectId="projectId"
            @finishAssociate="finishAssociate"
        />
        <FModal
            v-model:show="showModal"
            :title="$t('_.编辑调度任务')"
            :maskClosable="false"
            @ok="save"
            @cancel="cancel"
        >
            <FForm
                ref="ModalFormRef"
                :labelWidth="66"
                labelPosition="right"
                :model="modalTask"
                :rules="modelRules"
            >
                <FFormItem :label="$t('_.调度系统')" prop="sys">
                    <FSelect
                        v-model="modalTask.sys"
                        class="form-select"
                        filterable
                        :options="sysTypeList"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.集群')" prop="cluster">
                    <FSelect
                        v-model="modalTask.cluster"
                        class="form-select"
                        filterable
                        clearable
                        :options="clusterList"
                        @change="clusterChange"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.发布用户')" prop="releaseUser">
                    <FSelect
                        v-model="modalTask.releaseUser"
                        class="form-select"
                        filterable
                        clearable
                        :options="releaseUserList"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.项目')" prop="project">
                    <FSelect
                        v-model="modalTask.project"
                        class="form-select"
                        filterable
                        clearable
                        :options="projectList"
                        @change="projectChange"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.工作流')" prop="workflow">
                    <FSelect
                        v-model="modalTask.workflow"
                        class="form-select"
                        filterable
                        clearable
                        :options="workflowList"
                        @change="workflowChange"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.任务')" prop="task_name">
                    <FSelect
                        v-model="modalTask.task_name"
                        class="form-select"
                        filterable
                        clearable
                        :options="taskList"
                    ></FSelect>
                </FFormItem>
                <FFormItem
                    labelClass="label-text"
                    :label="
                        modalTask.front_or_back === 1
                            ? $t('_.前置校验规则组')
                            : $t('_.后置校验规则组')
                    "
                    prop="show_rule"
                >
                    <FInput
                        :modelValue="modalTask.show_rule"
                        class="form-select"
                        :disabled="true"
                    />
                </FFormItem>
            </FForm>
        </FModal>
        <PubScheduledModal
            v-model:show="showPubScheduledModal"
            :projectId="projectId"
            :sysTypeList="sysTypeList"
            @finishPub="loadScheduledTaskTableData"
        />
        <FModal v-model:show="showConfirmModal" width="400px">
            <template #title>
                <div style="display: flex"><div class="fes-modal-icon fes-modal-status-confirm"><span role="img" class="fes-design-icon"><svg width="200" height="200" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path d="M512 42.667C771.2 42.667 981.333 252.8 981.333 512S771.2 981.333 512 981.333 42.667 771.2 42.667 512 252.8 42.667 512 42.667zm0 626.474a42.667 42.667 0 1 0 0 85.334 42.667 42.667 0 0 0 0-85.334zm10.667-417.365h-21.334A21.333 21.333 0 0 0 480 273.109v311.979a21.333 21.333 0 0 0 21.333 21.333h21.334A21.333 21.333 0 0 0 544 585.088V273.067a21.333 21.333 0 0 0-21.333-21.334z"></path></svg></span></div><div style="font-weight: 500">{{$t('_.提示')}}</div><!----></div>
            </template>
            <div style="padding-left: 28px">{{$t('_.确认删除该关联调度任务吗？')}}</div>
            <template #footer>
                <div>
                    <FButton style="margin-right: 16px" @click="firstCancel">{{$t('_.取消')}}</FButton>
                    <FTooltip
                        v-model="showTooltip"
                        mode="confirm"
                        :disabled="tooltipDisable"
                        :confirmOption="{ okText: '确认删除', cancelText: '暂不删除' }"
                        @ok="secondConfirm"
                        @cancel="secondCancel"
                    >
                        <FButton type="primary" @click="firstConfirm">{{$t('_.确定')}}</FButton>
                        <template #title>
                            <div style="width: 200px">{{$t('_.WTSS被关联的工作流不存在，是否清理该条记录')}}</div>
                        </template>
                    </FTooltip>
                </div>
            </template>
        </FModal>
    </div>
</template>
<script setup>

import {
    LeftOutlined,
    PlusOutlined,
    EditOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    useI18n, useRouter, useRoute, request as FRequest,
} from '@fesjs/fes';
import { computed, reactive, ref } from 'vue';
import { FModal, FMessage } from '@fesjs/fes-design';
import { COMMON_REG } from '@/assets/js/const';
import { BPageLoading } from '@fesjs/traction-widget';
import queryFilter from '../components/queryFilter.vue';
import schedulingTaskDrawer from './schedulingTaskDrawer.vue';
import {
    queryScheduledTaskList,
    queryScheduledTask,
    modifyScheduledTask,
    modifyTableScheduledTask,
    deleteScheduledTask,
    getAllWtssProject,
    getAllWtssWorkFlow,
    getAllWtssTask,
    getReleaseUser,
} from '../api';
import {
    loadClusterList,
    loadProjectList,
    loadWorkflowList,
    loadTaskList,
} from './loadListData';
import getFormatOptions from './hooks/useOption';
import selector from '../components/selector.vue';
import PubScheduledModal from './pubScheduleModal.vue';

const router = useRouter();
const route = useRoute();
const { t: $t } = useI18n();
const backToProjectList = () => {
    router.back();
};
const projectId = ref(Number(route.query.projectId));
// 表格样式
const ruleColStyle = {
    color: '#5384FF',
};

// 是否加载
const isLoading = ref(true);

// 数据源
const scheduledTaskTableData = ref([]);
const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
});
// 编辑调度任务Modal
const showModal = ref(false);
const ModalFormRef = ref();
const modalTask = reactive({
    sys: 'WTSS',
    cluster: '',
    releaseUser: '',
    project: '',
    workflow: '',
    scheduled_task_id: '',
    project_id: '',
    task_name: '',
    pre_rule_id_arr: [],
    post_rule_id_arr: [],
    show_rule: '',
    // 1前置 2后置
    front_or_back: 1,
    scheduled_task_front_back_id: '',
});
function cancel() {
    showModal.value = false;
    ModalFormRef.value.resetFields();
}
async function save() {
    const {
        scheduled_task_front_back_id,
        sys,
        cluster,
        project,
        workflow,
        task_name,
        releaseUser,
    } = modalTask;
    const params = {
        scheduled_task_front_back_id,
        schedule_system: sys,
        cluster,
        project_name: project,
        work_flow: workflow,
        task_name,
        release_user: releaseUser,
    };
    try {
        await ModalFormRef.value.validate();
        await modifyTableScheduledTask(params);
        // eslint-disable-next-line no-use-before-define
        await loadScheduledTaskTableData();
        cancel();
    } catch (err) {
        console.warn(err);
    }
}
const modelRules = computed(() => ({
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
    task_name: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择任务'),
    },
    show_rule: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择规则组'),
    },
}));
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
    getReleaseUserList,
} = getFormatOptions();

// 操作
const editTask = async (row) => {
    try {
        modalTask.pre_rule_id_arr = [];
        modalTask.post_rule_id_arr = [];
        const {
            project_name,
            cluster,
            work_flow,
            scheduled_task_id,
            task_name,
            project_id,
            datasource,
            release_user,
        } = await queryScheduledTask(row.scheduled_task_front_back_id);
        modalTask.cluster = cluster;
        // eslint-disable-next-line camelcase
        modalTask.project = project_name;
        // eslint-disable-next-line camelcase
        modalTask.workflow = work_flow;
        // eslint-disable-next-line camelcase
        modalTask.scheduled_task_id = scheduled_task_id;
        // eslint-disable-next-line camelcase
        modalTask.task_name = task_name;
        // eslint-disable-next-line camelcase
        modalTask.project_id = project_id;
        modalTask.scheduled_task_front_back_id = row.scheduled_task_front_back_id;
        // eslint-disable-next-line camelcase
        modalTask.releaseUser = release_user;
        // eslint-disable-next-line camelcase
        datasource.forEach((item) => {
            if (item.frontOrBack === 1) {
                modalTask.pre_rule_id_arr.push(item.ruleGroupId);
            }
        });
        datasource.forEach((item) => {
            if (item.frontOrBack === 2) {
                modalTask.post_rule_id_arr.push(item.ruleGroupId);
            }
        });
        modalTask.show_rule = row.rule_group_name;
        modalTask.front_or_back = row.front_or_back;
        getQualitisCluster(projectId.value, 1);
        getReleaseUserList(cluster);
        getQualitisProject(projectId.value, 1, cluster);
        getQualitisWorkflow(1, cluster, project_name);
        getQualitisTask(1, cluster, project_name, work_flow);
    } catch (err) {
        console.warn(err);
    }
    showModal.value = true;
};
const showConfirmModal = ref(false);
const currentRow = ref(null);
const showTooltip = ref(false);
const tooltipDisable = ref(true);
const firstConfirm = async () => {
    // TODO: 调用接口，根据情况判断二次确认是否需要
    try {
        const { scheduled_task_front_back_id, release_user } = currentRow.value;
        const params = { scheduled_task_front_back_id, release_user, confirm_clear: false };
        if (currentRow.value.release_user) {
            params.release_user = currentRow.value.release_user;
        }
        const res = await deleteScheduledTask(params);
        if (res && res.code === '5101') {
            tooltipDisable.value = false;
            showTooltip.value = true;
        } else {
            // eslint-disable-next-line no-use-before-define
            loadScheduledTaskTableData();
            showConfirmModal.value = false;
            currentRow.value = null;
            FMessage.success($t('_.删除成功'));
        }
    } catch (err) {
        console.warn(err);
    }
};
const firstCancel = () => {
    showConfirmModal.value = false;
    showTooltip.value = false;
    tooltipDisable.value = true;
    currentRow.value = null;
};
const secondConfirm = async () => {
    // TODO: 二次确认删除，关闭modal
    try {
        const { scheduled_task_front_back_id, release_user } = currentRow.value;
        const params = { scheduled_task_front_back_id, release_user, confirm_clear: true };
        if (currentRow.value.release_user) {
            params.release_user = currentRow.value.release_user;
        }
        const res = await deleteScheduledTask(params);
        FMessage.success($t('_.删除成功'));
        // eslint-disable-next-line no-use-before-define
        loadScheduledTaskTableData();
    } catch (err) {
        console.warn(err);
    }
    currentRow.value = null;
    showTooltip.value = false;
    showConfirmModal.value = false;
    tooltipDisable.value = true;
};
const secondCancel = () => {
    showTooltip.value = false;
    tooltipDisable.value = true;
};
const deleteTask = async (row) => {
    currentRow.value = row;
    showConfirmModal.value = true;
    // FModal.confirm({
    //     title: '提示',
    //     content: '确认删除该关联调度任务？',
    //     maskClosable: true,
    //     closable: true,
    //     async onOk() {
    //         try {
    //             const { scheduled_task_front_back_id, release_user } = row;
    //             const params = { scheduled_task_front_back_id, release_user };
    //             if (row.release_user) {
    //                 params.release_user = row.release_user;
    //             }
    //             await deleteScheduledTask(params);
    //             // eslint-disable-next-line no-use-before-define
    //             loadScheduledTaskTableData();
    //         } catch (err) {
    //             console.warn(err);
    //         }
    //     },
    //     onCancel() {
    //         console.log('cancel');
    //     }
    // });
};

// 查询组件
const queryCondition = ref({
    value: '',
});
const queryData = ref({});
const sysTypeList = ref([{ label: 'WTSS', value: 'WTSS' }]);
const ruleGroupList = ref([]);
const databaseList = ref([]);
const dataTableList = ref([]);

const formmaterPubStatud = value => (value ? $t('_.已发布') : $t('_.未发布'));
// 加载表格数据
const loadScheduledTaskTableData = async () => {
    try {
        isLoading.value = true;
        scheduledTaskTableData.value = [];
        const {
            project = '',
            workflow = '',
            ruleGroup = '',
            task = '',
            cluster = '',
            database = '',
            dataTable = '',
        } = queryData.value;
        const params = {
            project_name: project,
            work_flow: workflow,
            rule_group_id: ruleGroup ? [ruleGroup] : '',
            task_name: task,
            cluster,
            db_name: database,
            table_name: dataTable,
            project_id: projectId.value,
            page: pagination.value.page - 1,
            size: pagination.value.size,
            task_type: 1,
        };
        // 去除空字符串
        const properties = Object.keys(params);
        properties.forEach((key) => {
            if (params[key] === '') {
                delete params[key];
            }
        });
        const result = await queryScheduledTaskList(params);
        result.data.forEach((item) => {
            item.datasource.forEach((rule) => {
                scheduledTaskTableData.value.push({
                    rule_group_name: rule.ruleGroupName,
                    database: `${rule.hiveDataSource[0]?.db ?? ''}/${
                        rule.hiveDataSource[0]?.table ?? ''
                    }`,
                    scheduling_system_type: item.schedule_system,
                    cluster: item.cluster,
                    project: item.project_name,
                    workflow: item.work_flow,
                    task: item.task_name,
                    scheduled_task_id: item.scheduled_task_id,
                    project_id: item.project_id,
                    rule_group_id: rule.ruleGroupId,
                    is_table_group: rule.tableGroup
                        ? 'tableGroupRules'
                        : 'rules',
                    front_or_back: rule.frontOrBack,
                    scheduled_task_front_back_id: rule.scheduledTaskFrontBackId,
                    pubStatus: item.release_status,
                });
            });
        });
        pagination.value.total = result.total;
        isLoading.value = false;
    } catch (error) {
        console.log('loadTimingTaskTableData Error: ', error);
    }
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查询
const queryFilterSearch = async (data) => {
    scheduledTaskTableData.value = [];
    queryData.value = data;
    pagination.value.page = 1;
    await loadScheduledTaskTableData();
    resultByInit.value = false;
    if (queryData.value.ruleGroup) {
        // 筛选维度是规则组，做个过滤
        scheduledTaskTableData.value = scheduledTaskTableData.value.filter(
            item => item.rule_group_id === queryData.value.ruleGroup,
        );
    }
};
// 重置查询
const queryFilterReset = async (data) => {
    scheduledTaskTableData.value = [];
    queryData.value = data;
    pagination.value.page = 1;
    resultByInit.value = true;
    await loadScheduledTaskTableData();
};
// 关联调度任务
const formType = ref('');
const drawerShow = ref(false);
const associateScheduleTask = () => {
    formType.value = 'create';
    drawerShow.value = true;
};
const editScheduleTask = () => {
    formType.value = 'edit';
    drawerShow.value = true;
};
const showPubScheduledModal = ref(false);
const publishScheduledTask = () => {
    showPubScheduledModal.value = true;
    console.log('publishScheduledTask');
};

// 新增关联调度任务
const finishAssociate = async () => {
    try {
        await loadScheduledTaskTableData();
    } catch (err) {
        console.warn(err);
    }
};

// // 拉项目
// async function getProjectList(params) {
//     try {
//         const { projects = [] } = await getAllWtssProject(params);
//         projectList.value = projects.map(item => ({
//             value: item,
//             label: item,
//         }));
//     } catch (error) {
//         console.warn(error);
//     }
// }

// // 拉工作流
// async function getWorkFlowList(params) {
//     try {
//         const { flows = [] } = await getAllWtssWorkFlow(params);
//         workflowList.value = flows.map(item => ({
//             value: item.flowId,
//             label: item.flowId,
//         }));
//     } catch (error) {
//         console.warn(error);
//     }
// }
// // 拉任务
// async function getTaskList(params) {
//     try {
//         const { nodes = [] } = await getAllWtssTask(params);
//         taskList.value = nodes.map(item => ({
//             label: item.id,
//             value: item.id,
//         }));
//     } catch (error) {
//         console.warn(error);
//     }
// }
// 统一调用, 集群没有前置依赖

// 根据发布用户来获取项目列表，默认为登录用户
// function releaseUserChange(value) {
//     modalTask.project = '';
//     modalTask.workflow = '';
//     modalTask.task_name = '';
//     workflowList.value = [];
//     taskList.value = [];
//     if (value) {
//         getProjectList({ release_user: value });
//     } else {
//         // 清空项目、工作流、任务，拉取项目下拉框列表入参是登录用户
//         getProjectList({ release_user: '' });
//     }
// }
// 拉发布用户列表

function clusterChange(value) {
    modalTask.project = '';
    modalTask.workflow = '';
    modalTask.task_name = '';
    releaseUserList.value = [];
    workflowList.value = [];
    taskList.value = [];
    if (value) {
        const { cluster } = modalTask;
        getReleaseUserList(cluster);
        getQualitisProject(projectId.value, 1, cluster);
    }
}
function projectChange(value) {
    modalTask.workflow = '';
    modalTask.task_name = '';
    workflowList.value = [];
    taskList.value = [];
    if (value) {
        const { cluster, project } = modalTask;
        getQualitisWorkflow(1, cluster, project);
    }
}
// 根据项目和工作流来获取任务列表
function workflowChange(value) {
    modalTask.task_name = '';
    taskList.value = [];
    if (value) {
        const { cluster, project, workflow } = modalTask;
        getQualitisTask(1, cluster, project, workflow);
    }
}
function clickRuleGroup(row) {
    router.push(
        `/projects/${row.is_table_group}?ruleGroupId=${row.rule_group_id}&workflowProject=false&projectId=${row.project_id}`,
    );
}
loadScheduledTaskTableData();

</script>
<style lang="less" scoped>
@import '@/style/varible';

.pub {
    color: #00cb91;
}

.unpub {
    color: #f29360;
}

.delete {
    cursor: pointer;
    color: @red-color;
}

.edit {
    cursor: pointer;
    display: inline-block;
    color: @blue-color;
    margin-right: 10px;
}

.division {
    width: 100%;
    height: 1px;
    background-color: rgba(15, 18, 34, 0.06);
}

.button {
    margin-right: 16px;
    margin-top: 24px;
}

.drawer-form {
    .form-select {
        width: 800px;
    }

    .rules-select {
        width: 800px;
    }
}

.linkProject {
    color: @blue-color;
    cursor: pointer;
}

:deep(.label-text) {
    text-align: right;
    margin-top: 3px;
}
</style>
