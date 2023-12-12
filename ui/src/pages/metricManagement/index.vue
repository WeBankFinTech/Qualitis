<template>
    <div style="padding: 16px 16px 32px" class="dashboard">
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }" :isDivider="false">
            <template v-slot:search>
                <Operation
                    ref="metricManagementOperation"
                    :metric-categories="metricCategories"
                    :booleans="booleans"
                    :en-codes="enCodes"
                    :sub-system-names="subSystemNames"
                    :form-model="queryFormModel"
                    :advanceQueryModel="advanceQueryFormModel"
                    @on-advance-search="handleAdvanceSearch"
                    @on-cancel-advance-search="handleCancelAdvanceSearch"
                    @on-show-advance-search="handleShowAdvanceSearch"
                    @on-search="search"
                    @on-reset="reset" />
            </template>
            <template v-slot:operate>
                <MetricActionBar
                    v-model:is-exporting="isExporting"
                    v-model:is-visual="isVisual"
                    v-model:is-delete="isDelete"
                    :is-importing-files="isImportingFiles"
                    :is-downloading="isDownloading"
                    :actions="metricActions"
                    @cancel-download="clear"
                    @confirm-download="exportMetric" />
            </template>
            <template v-slot:table>
                <f-table ref="metricTable" row-key="id" :data="metrics" @selectionChange="changeMetricTableSelection">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :visible="isExporting" type="selection" :width="32" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="name" :visible="checkTColShow('name')" :label="$t('indexManagement.indexName')" :width="160" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="metric_desc" :visible="checkTColShow('metric_desc')" :label="$t('indexManagement.indexDesc')" :width="160">
                        <template #default="{ row }">
                            <FEllipsis>
                                {{row.metric_desc || '--'}}
                                <template #tooltip>
                                    <div style="width:300px;word-wrap:break-word">
                                        {{row.metric_desc}}
                                    </div>
                                </template>
                            </FEllipsis>
                        </template>
                    </f-table-column>
                    <f-table-column ellipsis prop="sub_system_id" :visible="checkTColShow('sub_system_id')" :formatter="subSystemFormatter" :label="$t('indexManagement.multiIndexType')" :width="140" />
                    <f-table-column ellipsis prop="type" :visible="checkTColShow('type')" :formatter="typeFormatter" :label="$t('indexManagement.indexCategory')" :width="88" />
                    <f-table-column ellipsis prop="available" :visible="checkTColShow('available')" :formatter="availableFormatter" :label="$t('indexManagement.inUse')" :width="88" />
                    <f-table-column ellipsis prop="multi_env" :visible="checkTColShow('multi_env')" :formatter="multiEnvFormatter" :label="$t('indexManagement.inMultiDCN')" :width="100" />
                    <f-table-column ellipsis prop="frequency" :visible="checkTColShow('frequency')" :formatter="frequencyFormatter" :label="$t('indexManagement.indexFrequency')" :width="88" />
                    <f-table-column ellipsis prop="dev_department_name" :visible="checkTColShow('department_name')" :label="$t('indexManagement.developDepartment')" :width="200">
                        <template #default="{ row }">{{row.dev_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="ops_department_name" :visible="checkTColShow('ops_department_name')" :label="$t('indexManagement.maintainDepartment')" :width="200">
                        <template #default="{ row }">{{row.ops_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="visibility_department_list" :visible="checkTColShow('visibility_department_list')" :label="$t('indexManagement.visibilityDepartment')" :width="200">
                        <template #default="{ row }">
                            <FEllipsis v-if="row?.visibility_department_list">
                                {{getvisibilityDepartment(row.visibility_department_list)}}
                                <template #tooltip>
                                    <div style="text-align:center">可见范围</div>
                                    <div style="max-width:300px;word-wrap:break-word">
                                        {{getvisibilityDepartment(row.visibility_department_list)}}
                                    </div>
                                </template>
                            </FEllipsis>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="create_user" :visible="checkTColShow('create_user')" :label="$t('common.founder')" :width="120" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="create_time" :visible="checkTColShow('create_time')" :label="$t('common.createTime')" :width="184" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('indexManagement.reviser')" :width="120" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('common.changeTheTime')" :width="184" />
                    <f-table-column #default="{ row } = {}" :label="$t('common.operate')" align="center" fixed="right" :width="60">
                        <FDropdown trigger="focus" :options="tableOperations" @click="handleTableOperation($event, row)">
                            <FButton class="table-operation-item">
                                <MoreCircleOutlined />
                            </FButton>
                        </FDropdown>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="metrics.length > 0"
                    v-model:currentPage="pagination.page"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="changePage"
                    @pageSizeChange="changePage"></FPagination>
            </template>
        </BTablePage>
        <!-- 指标列表表头配置 -->
        <BTableHeaderConfig
            v-model:headers="metricTableHeaders"
            v-model:show="showMetricTableColConfig"
            :originHeaders="originProjectHeaders"
            type="metric_list" />
        <!-- 关联规则 弹框 -->
        <FModal
            v-model:show="isRuleModalVisible"
            :title="$t('common.associationRules')"
            displayDirective="if"
            contentClass="rule-modal"
            @ok="isDetailModalVisible = false"
        >
            <div v-if="Array.isArray(ruleList) && ruleList.length > 0">
                <div v-for="item in ruleList" :key="item.rule_id" class="rule-item">
                    <div class="detail-modal-item"><span class="label">规则名称</span><span :style="{ color: '#5384FF' }" @click="jumptoRuleDetail(item)">{{item.rule_name}}</span></div>
                    <div class="detail-modal-item"><span class="label">校验模版</span>{{item.template_name}}</div>
                    <div class="detail-modal-item"><span class="label">规则类型</span>{{getType(item.rule_type)}}</div>
                    <div class="detail-modal-item"><span class="label">过滤条件</span>{{item.filter}}</div>
                </div>
            </div>
            <div v-else>
                <div>无关联规则</div>
            </div>
            <template #footer>
                <div v-if="Array.isArray(ruleList) && ruleList.length > 0">
                    <FButton style="margin-right: 15px" @click="exportRule">导出规则</FButton>
                    <FButton type="primary" @click="executeRule">执行规则</FButton>
                </div>
            </template>
        </FModal>
        <!-- 任务执行 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            @ok="clickExecutationOk" />
        <FDrawer v-model:show="showHistory" :title="$t('common.historicalValue')" width="800px" @cancel="showHistory = false">
            <History :ready="showHistory" :is-multi="isMulti" :selected="selectedKeys" />
        </FDrawer>
        <MetricEditModal
            v-model:show="isDetailModalVisible"
            v-model:mode="metricMode"
            :metricCategories="metricCategories"
            :metricFrequencies="metricFrequencies"
            @on-delete="handleDeleteMetric"
            @on-add-success="handleAddMetricSuccess"
            @on-edit-success="handleEditMetricSuccess" />
    </div>
</template>
<script setup>
import { ref } from 'vue';
import { useI18n, request as FRequest, useRouter } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import {
    PlusOutlined, UploadOutlined, DownloadOutlined, MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { forceDownload } from '@/assets/js/utils';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import { getMoreActionRangeString, formatterEmptyValue, getvisibilityDepartment } from '@/common/utils';
import { FORM_MODE } from '@/assets/js/const';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import Operation from './components/operation';
import History from './components/history';
import MetricActionBar from '../projects/components/projectActionBar';
import MetricEditModal from './editModal';
import useMetricInit from './hooks/useMetricInit';
import useMetricBase from './hooks/useMetricBase';
import useImport from '../projects/hooks/useImport';
import useExport from '../projects/hooks/useExport';
import useExecutation from '../projects/hooks/useExecutation';

import {
    deleteMetric, deleteMetrics, findRule,
} from './api';

const metricManagementOperation = ref(null);
const router = useRouter();
const metricTable = ref();
const { t: $t } = useI18n();
const showLoading = ref(false);
const {
    // boolean值列表
    booleans,
    // 指标分类列表
    metricCategories,
    // 指标英文名列表
    enCodes,
    // 子系统列表
    subSystemNames,
    // 指标频率列表
    metricFrequencies,

    // 子系统formatter
    subSystemFormatter,
    // 指标分类formatter
    typeFormatter,
    // 指标是否可用formatter
    availableFormatter,
    // 指标频率formatter
    frequencyFormatter,
    // 是否为多DCN指标formatter
    multiEnvFormatter,
} = useMetricInit();

const {
    // 高级筛选对象
    advanceQueryFormModel,
    // 查询表单对象
    queryFormModel,
    // 已勾选指标列表
    metricTableSelection,
    // 指标列表
    metrics,
    // 指标列表分页对象
    pagination,
    // 表格数据是否是初始化结果
    resultByInit,

    // 高级筛选指标列表
    handleAdvanceSearch,
    // 取消高级筛选
    handleCancelAdvanceSearch,
    // 展示高级筛选弹窗
    handleShowAdvanceSearch,
    // 查询指标列表
    search,
    // 指标列表翻页
    changePage,
    // 重置查询条件
    reset,
    // 已选指标勾选更新
    changeMetricTableSelection,
    updateCurrentAfterAdd,
    updateCurrentAfterDelete,
} = useMetricBase(showLoading, metricManagementOperation);

// 指标导入
const { importDatas, isImportingFiles } = useImport('metric');
const importMetric = async (file) => {
    try {
        await importDatas(file);
        changePage();
    } catch (err) {
        console.warn(err);
    }
};

const isVisual = ref(false);
const isDelete = ref(false);
const isMulti = ref(false);
const selectedKeys = ref([]);
const showHistory = ref(false);

function clear() {
    metricTable.value?.clearSelection();
}

// 指标导出
const {
    isExporting,
    isDownloading,
    exportFileByParams,
} = useExport('metric');
const exportMetric = async () => {
    const selectedMetrics = metricTableSelection.value;
    if (selectedMetrics.length === 0) return FMessage.warn($t('common.selectOne'));
    if (isVisual.value) {
        selectedKeys.value = metricTableSelection.value.map(id => ({
            name: metrics.value.find(ele => +ele.id === +id)?.name,
            id: Number(id),
        }));
        console.log('selected', selectedKeys.value);
        isMulti.value = true;
        showHistory.value = true;
        isVisual.value = false;
        isDelete.value = false;
        isExporting.value = false;
        clear();
    } else if (isDelete.value) {
        isDownloading.value = true;
        FModal.confirm({
            title: '提示',
            content: $t('indexManagement.comfirmDeleteCurrentMetric'),
            okText: $t('common.delete'),
            async onOk() {
                try {
                    await deleteMetrics({ rule_metric_ids: metricTableSelection.value });
                    FMessage.success($t('toastSuccess.deleteSuccess'));
                    updateCurrentAfterDelete(metricTableSelection.value.length);
                } catch (e) {
                    console.error(e.message);
                }
                isDownloading.value = false;
                isVisual.value = false;
                isDelete.value = false;
                isExporting.value = false;
                clear();
            },
            async onCancel() {
                isDownloading.value = false;
                isVisual.value = false;
                isDelete.value = false;
                isExporting.value = false;
                clear();
            },
        });
    } else {
        await exportFileByParams({
            rule_metric_ids: metricTableSelection.value.map(Number),
        });
        clear();
    }
};

// 指标管理actions
const metricActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('indexManagement.addIndicator'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
            handleAddMetric();
        },
    },
    {
        actionType: 'upload',
        type: 'default',
        icon: UploadOutlined,
        label: $t('indexManagement.import'),
        handler: (payload) => {
            // eslint-disable-next-line no-use-before-define
            importMetric(payload);
        },
    },
    {
        actionType: 'download',
        type: 'default',
        icon: DownloadOutlined,
        label: $t('indexManagement.export'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
        },
    },
    {
        actionType: 'dropdown',
        type: 'default',
        icon: MoreCircleOutlined,
        label: $t('myProject.more'),
        trigger: 'hover',
        options: [
            {
                label: $t('common.setTableHeaderConfig'),
                value: '1',
                handler: () => {
                    // eslint-disable-next-line no-use-before-define
                    toggleTColConfig();
                },
            },
            {
                label: $t('indexManagement.deleteBatch'),
                value: '2',
                handler: () => {
                    // eslint-disable-next-line no-use-before-define
                    console.log('批量删除...');
                    isDelete.value = true;
                    isExporting.value = true;
                },
            },
            {
                label: $t('indexManagement.visualizationBatch'),
                value: '3',
                handler: () => {
                    // eslint-disable-next-line no-use-before-define
                    console.log('批量可视化...');
                    isVisual.value = true;
                    isExporting.value = true;
                },
            },
        ],
    },
];

const originProjectHeaders = [
    { prop: 'name', label: $t('indexManagement.indexName') },
    { prop: 'metric_desc', label: $t('indexManagement.indexDesc') },
    { prop: 'sub_system_id', label: $t('indexManagement.subsystem') },
    { prop: 'type', label: $t('indexManagement.indexCategory') },
    { prop: 'available', label: $t('indexManagement.inUse') },
    { prop: 'frequency', label: $t('indexManagement.indexFrequency') },
    { prop: 'department_name', label: $t('indexManagement.developDepartment') },
    { prop: 'ops_department_name', label: $t('indexManagement.maintainDepartment') },
    { prop: 'create_user', label: $t('common.founder') },
    { prop: 'create_time', label: $t('common.createTime'), hidden: true },
    { prop: 'modify_time', label: $t('common.changeTheTime'), hidden: true },
    { prop: 'multi_env', label: $t('indexManagement.inMultiDCN') },
    { prop: 'modify_user', label: $t('common.modifier') },
    { prop: 'visibility_department_list', label: $t('indexManagement.visibilityDepartment'), hidden: true },
];
// 未设置过缓存，默认不展示创建时间/修改时间/可见范围
// if (!localStorage.getItem('metric_list_table_config')) {
//     localStorage.setItem('metric_list_table_config', JSON.stringify({
//         active: [
//             { prop: 'name', label: 'indexManagement.indexName', index: 1 },
//             { prop: 'metric_desc', label: 'indexManagement.indexDesc', index: 2 },
//             { prop: 'sub_system_id', label: 'indexManagement.subsystem', index: 3 },
//             { prop: 'type', label: 'indexManagement.indexCategory', index: 4 },
//             { prop: 'available', label: 'indexManagement.inUse', index: 5 },
//             { prop: 'frequency', label: 'indexManagement.indexFrequency', index: 6 },
//             { prop: 'department_name', label: 'indexManagement.developDepartment', index: 7 },
//             { prop: 'ops_department_name', label: 'indexManagement.maintainDepartment', index: 8 },
//             { prop: 'create_user', label: 'common.founder', index: 9 },
//             { prop: 'multi_env', label: 'indexManagement.inMultiDCN', index: 12 },
//             { prop: 'modify_user', label: 'common.modifier', index: 13 },
//         ],
//         inActive: [
//             { prop: 'create_time', label: 'common.createTime', index: 10 },
//             { prop: 'modify_time', label: 'common.changeTheTime', index: 11 },
//             { prop: 'visibility_department_list', label: 'indexManagement.visibilityDepartment', index: 14 },
//         ],
//     }));
// }

const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: metricTableHeaders,
    showTableHeaderConfig: showMetricTableColConfig,
} = useTableHeaderConfig();

const isDetailModalVisible = ref(false);
const metricMode = ref('');
const handleAddMetric = () => {
    metricMode.value = FORM_MODE.ADD;
    isDetailModalVisible.value = true;
};

// 指标详情 删除
const confirmDeleteMetric = async (metric) => {
    deleteMetric(metric.id).then(() => {
        FMessage.success($t('toastSuccess.deleteSuccess'));
        metricMode.value = '';
        isDetailModalVisible.value = false;
        updateCurrentAfterDelete(1);
        search();
    });
};

const handleDeleteMetric = (metric) => {
    if (metric.is_editable) {
        FModal.confirm({
            title: '提示',
            content: $t('indexManagement.comfirmDeleteCurrentMetric'),
            okText: $t('common.delete'),
            onOk() {
                confirmDeleteMetric(metric);
            },
        });
    } else {
        FMessage.info('没有权限删除');
    }
};

const handleAddMetricSuccess = () => {
    updateCurrentAfterAdd();
};

const handleEditMetricSuccess = () => {
    search();
};

// 关联规则 相关 开始
const ruleList = ref();
const isRuleModalVisible = ref(false);

// 获取关联规则
const getRuleList = async (id) => {
    if (!id) FMessage.error($t('common.invalidMetric'));
    const res = await findRule(id);
    ruleList.value = res.content || [];
};

const getType = (type) => {
    const arr = [' ', $t('common.singleTemplateRule'), $t('common.customRule'), $t('common.multiTemplateRule'), $t('common.fileTemplateRule'), $t('common.checkAlertRule')];
    return arr[type];
};

// 关联规则跳转到规则详情
const jumptoRuleDetail = (item) => {
    console.log('jumptoRuleDetail', item);
    let ruleType = 'rules';
    if (item.table_group) ruleType = 'tableGroupRules';
    router.push(`/projects/${ruleType}?ruleGroupId=${item.rule_group_id}&id=${item.rule_id}&workflowProject=${item.project_type === 2}&projectId=${item.project_id}`);
};

// 导出规则
const exportRule = () => {
    let fileName = '';
    const params = {};
    const ruleId = ruleList.value.map(item => item.rule_id);
    params.rule_ids = ruleId;
    FRequest('/api/v1/projector/rule/batch/download', params, {
        useResponse: true,
        responseType: 'blob',
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
        credentials: 'include',
    }, 'get').then((res) => {
        const contentDisposition = res?.headers['content-disposition'] || '';
        const fileNameUnicode = contentDisposition.split('filename*=')[1];
        fileName = fileNameUnicode ? decodeURIComponent(fileNameUnicode.split("''")[1]) : '技术规则.xlsx';
        return res?.data;
    }).then((blob) => {
        const blobUrl = window.URL.createObjectURL(blob);
        forceDownload(blobUrl, fileName, () => {
            window.URL.revokeObjectURL(blobUrl);
        });
    }).catch((err) => {
        FMessage.error(`${$t('toastError.importFail')}:${err.message}`);
    });
};

// 执行规则相关
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPreInRuleQuery,
    excuteRuleInProjectDetail,
} = useExecutation();

const clickExecutationOk = () => {
    excuteRuleInProjectDetail();
};

// 执行规则
const executeRule = () => {
    executeTaskPreInRuleQuery(ruleList.value, ruleList.value);
};

// 关联规则 相关 结束

// 表格操作 相关 开始
const tableOperations = [
    { label: $t('indexManagement.indexDetail'), value: '1' },
    { label: $t('common.associationRules'), value: '2' },
    { label: $t('common.historicalValue'), value: '3' },
];

const handleTableOperation = (value, row) => {
    if (value === '1') {
        // 指标详情
        eventbus.emit('metric:set-form-model', row);
        metricMode.value = FORM_MODE.REVIEW;
        isDetailModalVisible.value = true;
    } else if (value === '2') {
        // 关联规则
        getRuleList(row.id);
        isRuleModalVisible.value = true;
    } else if (value === '3') {
        // 历史值
        console.log('value: ', value);
        console.log('row: ', row);
        console.log('历史值');
        selectedKeys.value = [{ name: row.name, id: +row.id }];
        // console.log('selectedKeys', selectedKeys.value);
        isMulti.value = false;
        showHistory.value = true;
    }
};
// 表格操作 相关 结束
</script>

<config>
{
    "name": "metricManagement",
    "title": "$indexManagement.title"
}
</config>

<style lang="less" scoped>

.dashboard {
    .table-container {
        .mr10 {
            margin-right: 10px;
        }
        .table-operation-item {
            padding: 0;
            width: 100%;
            min-width: 100%;
            border: 0;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #646670;
            letter-spacing: 0;
            line-height: 22px;
            font-weight: 400;
            &:hover {
                color: #5384FF;
            }
        }
    }
}

.detail-modal-item {
    margin-bottom: 16px;
    &:last-child {
        margin-bottom: 0;
    }
    .label {
        display: inline-block;
        width: 56px;
        text-align: right;
        margin-right: 16px;
    }
}
</style>

<style lang="less">
.rule-modal {
    max-height: 550px;
    .fes-modal-body {
        max-height: 424px;
        overflow: auto;
    }
    .rule-item {
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid #F1F1F2;
        &:first-child {
            margin-top: 0;
            padding-top: 0;
            border: 0;
        }
    }
}
</style>

<config>
{
    "name": "metricManagement",
    "title": "指标管理"
}
</config>
