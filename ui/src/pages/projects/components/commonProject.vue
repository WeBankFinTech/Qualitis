<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <SearchFilterBar workSpace="commonProjectFilter" :subSystemList="subSystemList" />
            </template>
            <template v-slot:operate>
                <ProjectActionBar
                    v-model:isExporting="isExporting"
                    :actions="projectActions"
                    :isDownloading="isDownloading"
                    :isImportingFiles="isImportingFiles"
                    parentType="project"
                    @confirmDownload="exportProject"
                />
            </template>
            <template v-slot:table>
                <f-table
                    v-if="projectTableHeaders.length"
                    rowKey="project_id"
                    :data="projects"
                    @selectionChange="projectSelectionChange"
                >
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :visible="isExporting" type="selection" :width="32" />
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        :visible="checkTColShow('project_id')"
                        prop="project_id"
                        :label="$t('label.projectId')"
                        :width="78"
                        ellipsis
                    ></f-table-column>
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('project_name')"
                        prop="project_name"
                        :label="$t('common.projectName')"
                        :width="160"
                    >
                        <template #default="{ row = {}}">
                            <span class="a-link"
                                  href="javascript:void(0);"
                                  @click="navigateToProjectDetail(row)"
                            >{{row.project_name}}</span>
                        </template>
                    </f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        ellipsis
                        :visible="checkTColShow('cn_name')"
                        prop="cn_name"
                        :label="$t('common.cnName')"
                        :width="160"
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        ellipsis
                        :visible="checkTColShow('description')"
                        prop="description"
                        :label="$t('label.projectDesc')"
                        :width="180"
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        ellipsis
                        :visible="checkTColShow('sub_system_name')"
                        prop="sub_system_name"
                        :label="$t('dataSourceManagement.subSystem')"
                        :width="180"
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        :visible="checkTColShow('create_user')"
                        prop="create_user"
                        :label="$t('common.founder')"
                        :width="120"
                        ellipsis
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        :visible="checkTColShow('create_time')"
                        prop="create_time"
                        :label="$t('common.createTime')"
                        :width="184"
                        ellipsis
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        :visible="checkTColShow('modify_user')"
                        prop="modify_user"
                        :label="$t('myProject.operationUser')"
                        :width="120"
                        ellipsis
                    ></f-table-column>
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        :visible="checkTColShow('modify_time')"
                        prop="modify_time"
                        :label="$t('myProject.operationTime')"
                        :width="184"
                        ellipsis
                    ></f-table-column>
                    <f-table-column
                        #default="{ row = {}}"
                        fixed="right"
                        :label="$t('common.operate')"
                        :width="150"
                    >
                        <ul class="wd-table-operate-btns">
                            <li
                                v-for="item in projectOperationBtns"
                                :key="item.value"
                                class="btn-item"
                                :class="{ red: item.isDelete }"
                                @click="projectOperationBtnHandler(item, row)"
                            >
                                {{item.label}}
                            </li>
                        </ul>
                    </f-table-column>
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
                    @pageSizeChange="pageChange"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 新增普通项目弹窗 -->
        <ProjectFormModal
            v-model:show="showProjectFormModal"
            :mode="projectFormMode"
            :subSystemList="subSystemList"
            @on-success="handleProjectFormModalSuccess"
        />
        <!-- 普通项目表头配置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originProjectHeaders"
            type="project_list"
        />
        <!-- 任务执行 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            @ok="executeTask"
        />
    </div>
</template>
<script setup>
import {
    ref, defineProps, onMounted, onUnmounted, watch,
} from 'vue';
import { useI18n, useRouter } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import {
    PlusOutlined, UploadOutlined, DownloadOutlined, MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';

import eventbus from '@/common/useEvents';
import { formatterEmptyValue } from '@/common/utils';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import ProjectActionBar from './projectActionBar';
import ProjectFormModal from './projectFormModal';
import useProjects from '../hooks/useProject';
import useImport from '../hooks/useImport';
import useExport from '../hooks/useExport';
import useExecutation from '../hooks/useExecutation';
import { fetchProjects } from '../api';
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
// 项目topbar操作配置
const projectActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('common.newProject'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
            handleAddProject();
        },
    },
    {
        actionType: 'upload',
        type: 'default',
        icon: UploadOutlined,
        label: $t('myProject.importProject'),
        handler: (payload) => {
            // eslint-disable-next-line no-use-before-define
            importProject(payload);
        },
    },
    {
        actionType: 'download',
        type: 'default',
        icon: DownloadOutlined,
        label: $t('myProject.downloadProject'),
        // handler: () => {
        //     // eslint-disable-next-line no-use-before-define
        // },
    },
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

// 项目表格按钮相关
const projectOperationBtns = [
    {
        label: $t('myProject.run'),
        value: 'executeTask',
    },
    {
        label: $t('common.delete'),
        value: 'deleteProject',
        isDelete: true,
    },
];
const projectOperationBtnHandler = (btn, data) => {
    const map = {
        // eslint-disable-next-line no-use-before-define
        executeTask: params => executeTaskPre(params, false),
        // eslint-disable-next-line no-use-before-define
        deleteProject: handleDeleteProject,
    };
    const handler = map[btn.value];
    if (handler instanceof Function) {
        handler(data);
    }
};

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
    projectSelection,
    projectSelectionChange,
    // 删除项目
    handleDeleteProject,
    // 新增项目相关
    showProjectFormModal,
    projectFormMode,
    handleAddProject,
    handleProjectFormModalSuccess,
} = useProjects(fetchProjects, showLoading, 'commonProjectFilter');

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

// 项目导入
const { importDatas, isImportingFiles } = useImport('project');
const importProject = async (file) => {
    try {
        // await importDatas(file);
        pageChange(true);
    } catch (err) {
        console.warn(err);
    }
};

// 项目导出
const {
    isExporting,
    isDownloading,
    exportFileByParams,
} = useExport('project');
const exportProject = async () => {
    const selectedProjects = projectSelection.value;
    if (selectedProjects.length === 0) return FMessage.warn($t('common.selectOne'));
    exportFileByParams({
        project_ids: projectSelection.value,
    });
};

// 执行项目相关
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPre,
    executeTask,
} = useExecutation(showLoading);

// 跳转到项目详情
const navigateToProjectDetail = (row) => {
    if (!row.project_id) return;
    router.push({
        path: '/projects/detail',
        query: {
            projectId: row.project_id,
        },
    });
};

// 获取数据源相关数据
const {
    subSystemList,
} = useDataSource(['subSystemList']);
</script>
<style lang="less" scoped>
.loading-box {
    width: 100%;
    height: 100%;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 9999;
}
</style>
<style lang="less">
.fes-tooltip-text {
    a {
        color: #ffffff;
    }
}
</style>
