<template>
    <div class="wd-content">
        <div class="wd-project-header">
            <LeftOutlined class="back" @click="backToProjectList" />
            <div class="name">{{$t('_.发布调度任务')}}</div>
        </div>
        <div class="wd-content-body">
            <query-filter
                :queryFromProjectID="projectId"
                :type="2"
                @search="queryFilterSearch"
                @reset="queryFilterReset"
            ></query-filter>
            <div class="division"></div>
            <FButton type="primary" class="button" @click="aboutTimingTask('create')">
                <PlusOutlined />{{$t('_.新建调度任务')}}
            </FButton>
            <FButton
                type="primary"
                class="button"
                @click="publishScheduledTask"
            >
                <fes-icon
                    type="publish"
                    class="button-icon"
                />{{$t('_.发布调度任务')}}
            </FButton>
            <FButton class="button" @click="aboutTimingTask('edit')">
                <EditOutlined />{{$t('_.编辑调度任务')}}
            </FButton>
            <div class="table-container">
                <div v-if="isLoading">
                    <BPageLoading :loadingText="{ loading: '' }" />
                </div>
                <template v-else>
                    <f-table
                        ref="taskTable"
                        rowKey="scheduledTaskId"
                        :data="timingTaskTableData"
                        :no-data-text="$t('common.noData')"
                    >
                        <template #empty>
                            <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                        </template>
                        <f-table-column
                            v-slot="{ row }"
                            :label="$t('schedulingTask.ruleGroupName')"
                            align="left"
                            :colStyle="ruleColStyle"
                            :width="160"
                            ellipsis
                        >
                            <span
                                class="linkProject"
                                @click="clickRuleGroup(row)"
                            >{{row.ruleGroupName}}</span>
                        </f-table-column>
                        <f-table-column
                            v-slot="{ row }"
                            :label="$t('schedulingTask.database')"
                            align="left"
                            :width="200"
                            ellipsis
                        >
                            <span>{{`${row.database} / ${row.tableName}`}}</span>
                        </f-table-column>
                        <f-table-column
                            prop="scheduling_system_type"
                            :label="$t('schedulingTask.schedulingSystemType')"
                            align="left"
                            :width="116"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="cluster"
                            :label="$t('schedulingTask.cluster')"
                            align="left"
                            :width="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            v-slot="{ row }"
                            :label="$t('schedulingTask.project')"
                            align="left"
                            :width="140"
                            ellipsis
                        >
                            <span
                                class="linkProject"
                                @click="clickProject(row)"
                            >{{row.scheduledProjectName}}</span>
                        </f-table-column>

                        <f-table-column
                            prop="workflow"
                            :label="$t('schedulingTask.workflow')"
                            align="left"
                            :width="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="task"
                            :label="$t('schedulingTask.task')"
                            align="left"
                            :width="140"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            v-slot="{ row }"
                            prop="pubStatus"
                            :label="$t('schedulingTask.pubStatus')"
                            align="left"
                            :width="88"
                            ellipsis
                        >
                            <span :class="row.pubStatus ? 'pub' : 'unpub'">
                                {{formmaterPubStatud(row.pubStatus)}}
                            </span>
                        </f-table-column>
                        <f-table-column
                            prop="workflow_business_name"
                            :label="$t('schedulingTask.workflowBusinessName')"
                            align="left"
                            :width="120"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="scheduleType"
                            :label="$t('timingTask.scheduleType')"
                            align="left"
                            :width="88"
                            ellipsis
                        ></f-table-column>
                        <f-table-column
                            prop="scheduleConfig"
                            :label="$t('timingTask.scheduleConfig')"
                            align="left"
                            :width="156"
                            ellipsis
                        ></f-table-column>
                    </f-table>
                </template>
            </div>
            <FPagination
                v-model:pageSize="pagination.size"
                v-model:currentPage="pagination.page"
                show-size-changer
                show-total
                :total-count="pagination.total"
                class="pagination"
                @change="paginationChange"
            ></FPagination>
        </div>
        <TimingTaskDrawer
            v-model:show="drawerShow"
            v-model:mode="mode"
            v-model:drawerTitle="drawerTitle"
            :projectId="projectId"
            :scheduledProjectID="drawerScheduledProjectID"
            @deleteTask="loadTimingTaskTableData"
            @modifyTask="loadTimingTaskTableData"
            @publishTask="loadTimingTaskTableData"
        ></TimingTaskDrawer>
        <PubScheduledModal
            v-model:show="showPubScheduledModal"
            :projectId="projectId"
            :sysTypeList="sysTypeList"
            :type="2"
            @finishPub="loadTimingTaskTableData"
        />
    </div>
</template>
<script setup>

import { FTimePicker } from '@fesjs/fes-design';
import {
    LeftOutlined,
    PlusOutlined,
    EditOutlined,
    PlusCircleOutlined,
    MinusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    useI18n, useRouter, useRoute, request as FRequest,
} from '@fesjs/fes';
import {
    computed,
    onMounted,
    reactive,
    ref,
} from 'vue';
import { cloneDeep } from 'lodash-es';
import { getLabelByValue } from '@/common/utils';
import { BPageLoading } from '@fesjs/traction-widget';
import queryFilter from '../components/queryFilter.vue';
import selector from '../components/selector.vue';
import TimingTaskDrawer from './TimingTaskDrawer.vue';
import { fomatPreviewExecutionTime } from './util';
import PubScheduledModal from './pubScheduleModal.vue';

const { t: $t } = useI18n();
const formmaterPubStatud = value => (value ? $t('_.已发布') : $t('_.未发布'));
const scheduleTypeList = [
    { value: 'interval', label: $t('_.定时调度') },
    { value: 'signal', label: $t('_.信号调度') },
];
const sysTypeList = ref([{ label: 'WTSS', value: 'WTSS' }]);

const router = useRouter();
const backToProjectList = () => {
    router.back();
};
const projectId = computed(() => parseInt(router.currentRoute.value.query.projectId) || '');

// 表格样式
const ruleColStyle = {
    color: '#5384FF',
};
const projectColStyle = {
    color: '#5384FF',
};

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
// 是否加载
const isLoading = ref(true);
// 分页
const pagination = ref({
    page: 1,
    size: 10,
    total: 30,
});
// 数据源
const timingTaskTableData = ref([]);
// 加载表格数据
const loadTimingTaskTableData = async () => {
    try {
        isLoading.value = true;
        const result = await FRequest('/api/v1/projector/scheduledTask/publish/query', {
            schedule_system: queryData.value?.sysType || '',
            scheduled_project_name: queryData.value?.project || '',
            scheduled_workflow_name: queryData.value?.workflow || '',
            rule_group_id: queryData.value?.ruleGroup || '',
            task_name: queryData.value?.task || '',
            cluster_name: queryData.value?.cluster || '',
            db_name: queryData.value?.database || '',
            table_name: queryData.value?.dataTable || '',
            project_id: projectId.value,
            page: pagination.value.page - 1,
            size: pagination.value.size,
        });
        timingTaskTableData.value = result.data.map(item => ({
            ruleGroupId: item.rule_group_id,
            ruleGroupName: item.rule_group_name,
            isTableGroup: item.table_group,
            ruleGroupProjectId: item.project_id,
            ruleGroupProjectWorkflow: item.project_type === 2 ? 'true' : 'false',
            database: item.db_name,
            tableName: item.table_name,
            scheduling_system_type: item.schedule_system,
            cluster: item.cluster_name,
            scheduledProjectName: item.scheduled_project_name,
            scheduledProjectID: item.scheduled_project_id,
            workflow: item.scheduled_workflow_name,
            task: item.scheduled_task_name,
            scheduledTaskId: item.scheduled_task_id,
            pubStatus: item.release_status,
            scheduleType: getLabelByValue(scheduleTypeList, item.scheduled_type) || '--',
            scheduleConfig: fomatPreviewExecutionTime(item.execute_interval, item.execute_date_in_interval, item.execute_time_in_date).complete,
        }));
        pagination.value.total = result.total;
        isLoading.value = false;
    } catch (error) {
        console.log('loadTimingTaskTableData Error: ', error);
    }
};

const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const queryFilterSearch = async (data) => {
    Object.assign(queryData.value, data);
    pagination.value.page = 1;
    await loadTimingTaskTableData();
    resultByInit.value = false;
};
const queryFilterReset = async (data) => {
    Object.assign(queryData.value, data);
    pagination.value.page = 1;
    await loadTimingTaskTableData();
    resultByInit.value = true;
};

// Modal 设置
const showPubScheduledModal = ref(false);
const publishScheduledTask = () => {
    showPubScheduledModal.value = true;
    console.log('publishScheduledTask');
};

// 抽屉设置
const drawerTitle = ref('');
const drawerShow = ref(false);
// 查看状态或者编辑状态flag
const mode = ref('view'); // [view:任务详情, edit：编辑调度任务, publish：发布调度任务]
// 发布和编辑调度任务
const aboutTimingTask = async (type) => {
    mode.value = type;
    if (type === 'edit') {
        drawerTitle.value = $t('_.编辑调度任务');
    } else if (type === 'create') {
        drawerTitle.value = $t('_.新建调度任务');
    } else {
        drawerTitle.value = $t('_.项目详情');
    }
    drawerShow.value = true;
};

const paginationChange = async (currentPage, pageSize) => {
    console.log(currentPage, pageSize);
    await loadTimingTaskTableData();
};
const drawerScheduledProjectID = ref(null);
const clickProject = (row) => {
    mode.value = 'view';
    drawerTitle.value = $t('_.项目详情');
    drawerScheduledProjectID.value = row.scheduledProjectID;
    drawerShow.value = true;
};
// 点击规则组
const clickRuleGroup = (row) => {
    router.push(`/projects/${row.isTableGroup ? 'tableGroupRules' : 'rules'}?ruleGroupId=${row.ruleGroupId}&workflowProject=${row.ruleGroupProjectWorkflow}&projectId=${row.ruleGroupProjectId}`);
};
onMounted(async () => {
    await loadTimingTaskTableData();
});

</script>
<style lang="less" scoped>
@import "@/style/varible";
.pub {
    color: #00CB91;
}

.unpub {
    color: #F29360;
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
.pagination {
    justify-content: end;
    margin-top: 12px;
}
.button {
    margin-right: 16px;
    margin-top: 24px;
}
.form-select {
    width: 832px;
}

.linkProject {
    color: @blue-color;
    cursor: pointer;
}
.fes-popper {
    .linkProject {
        color: var(--f-white);
    }
}
.view-font {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0f1222;
    line-height: 22px;
    font-weight: 400;
}
</style>
