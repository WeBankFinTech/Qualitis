<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <SearchFilterBar workSpace="workflowProjectFilter" :subSystemList="subSystemList" />
            </template>
            <template v-slot:operate>
                <ProjectActionBar :actions="projectActions" />
            </template>
            <template v-slot:table>
                <f-table
                    v-if="projectTableHeaders.length"
                    rowKey="project_id"
                    :data="projects">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('project_id')" prop="project_id" :label="$t('label.projectId')" :width="78" ellipsis></f-table-column>
                    <f-table-column ellipsis :visible="checkTColShow('project_name')" prop="project_name" :label="$t('common.projectName')" :width="160">
                        <template #default="{ row = {}}">
                            <span class="a-link" href="javascript:void(0);" @click="navigateToProjectDetail(row)">{{row.project_name}}</span>
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis :visible="checkTColShow('description')" prop="description" :label="$t('label.projectDesc')" :width="180"></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis :visible="checkTColShow('sub_system_name')" prop="sub_system_name" :label="$t('dataSourceManagement.subSystem')" :width="180"></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('create_user')" prop="create_user" :label="$t('common.founder')" :width="120" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('create_time')" prop="create_time" :label="$t('common.createTime')" :width="184" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('modify_user')" prop="modify_user" :label="$t('myProject.operationUser')" :width="120" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('modify_time')" prop="modify_time" :label="$t('myProject.operationTime')" :width="184" ellipsis></f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="projects.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="pageChange"
                    @pageSizeChange="pageChange"></FPagination>
            </template>
        </BTablePage>
        <!-- 工作流项目表头配置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originProjectHeaders"
            type="workflow_project_list" />
    </div>
</template>
<script setup>
import {
    ref, defineProps, onMounted, onUnmounted,
} from 'vue';
import { useI18n, useRouter } from '@fesjs/fes';
import { MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import eventbus from '@/common/useEvents';
import { formatterEmptyValue } from '@/common/utils';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import ProjectActionBar from './projectActionBar';
import useProjects from '../hooks/useProject';
import {
    fetchWorkflowProjects,
} from '../api';
import SearchFilterBar from './searchFilterBar';
import useDataSource from '../hooks/useDataSource';

const props = defineProps({
    originProjectHeaders: {
        type: Array,
        required: true,
    },
});
const { t: $t } = useI18n();
const router = useRouter();

const showLoading = ref(false);
// 项目topbar按钮配置
const projectActions = [
    {
        actionType: 'dropdown',
        type: 'default',
        icon: MoreCircleOutlined,
        label: $t('myProject.more'),
        trigger: 'click',
        options: [
            {
                label: $t('common.setTableHeaderConfig'),
                value: '1',
                handler: () => {
                    // eslint-disable-next-line no-use-before-define
                    toggleTColConfig();
                },
            },
        ],
    },
];

const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: projectTableHeaders,
    showTableHeaderConfig: showProjectTableColConfig,
} = useTableHeaderConfig();

// 项目表格相关
const {
    pagination,
    projects,
    queryCondition,
    getProjects,
    pageChange,
} = useProjects(fetchWorkflowProjects, showLoading, 'workflowProjectFilter');

const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查询相关操作
onMounted(() => {
    eventbus.on('project:searchfilter', ({ searchType, ...data }) => {
        queryCondition.value = { ...data };
        pagination.current = 1;
        resultByInit.value = searchType === 'reset';
        getProjects();
    });
});

onUnmounted(() => {
    eventbus.off('project:searchfilter');
});

// 跳转到项目详情
const navigateToProjectDetail = (row) => {
    if (!row.project_id) return;
    router.push({
        path: '/projects/detail',
        query: {
            projectId: row.project_id,
            workflowProject: true,
        },
    });
};

// 获取数据源相关数据
const {
    subSystemList,
} = useDataSource(['subSystemList']);
</script>
