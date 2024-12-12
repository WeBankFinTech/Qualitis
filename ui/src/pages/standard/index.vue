<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="condition" :isReset="false" @search="search">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">标准值ID</span>
                            <FInput v-model="condition.edition_id" placeholder="请输入标准值ID"></FInput>
                        </div>
                        <div>
                            <span class="condition-label">中文名称</span>
                            <FInput v-model="condition.cn_name" placeholder="请输入中文名关键词"></FInput>
                        </div>
                        <div>
                            <span class="condition-label">英文名称</span>
                            <FInput v-model="condition.en_name" placeholder="请输入英文名关键词"></FInput>
                        </div>
                    </template>
                    <template v-slot:exButton>
                        <FButton
                            :class="advanceQuerySelectedCount > 0 ? 'selected-count' : ''"
                            @click="toggleAdvanceQuery">
                            高级筛选{{advanceQuerySelectedCount > 0 ? `（已选${advanceQuerySelectedCount}项）` : ''}}
                        </FButton>
                        <FButton @click="reset">重置</FButton>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <FSpace :size="CONDITIONBUTTONSPACE">
                    <template v-if="!isExporting">
                        <FButton type="primary" @click="openEditPanel('add')"><PlusOutlined />新增标准值</FButton>
                        <FDropdown trigger="hover" :options="moreMenus" @click="clickMore">
                            <FButton><MoreCircleOutlined />{{$t('myProject.more')}}</FButton>
                        </FDropdown>
                    <!-- <FButton :disabled="isImportingFiles">
                        <UploadOutlined />导入标准值
                        <input
                            :ref="el => { if (el) fileInputRefs = el }"
                            type="file"
                            class="btn-file-input"
                            accept=".xlsx"
                            @change="importRules(file)" />
                    </FButton>
                    <FButton @click="toggleExport"><DownloadOutlined />导出标准值</FButton> -->
                    </template>
                    <template v-else>
                        <FButton :disabled="isDownloading" type="primary" class="btn-item" @click="exportProject">{{$t('myProject.confirmExport')}}</FButton>
                        <FButton :disabled="isDownloading" class="btn-item" @click="toggleExport">{{$t('common.cancel')}}</FButton>
                    </template>
                </FSpace>
            </template>
            <template v-slot:table>
                <f-table :data="standardLists">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <f-table-column :visible="isExporting" type="selection" :width="32" />
                    <f-table-column :visible="checkTColShow('edition_id')" prop="edition_id" label="标准值ID" :width="90" ellipsis />
                    <f-table-column :visible="checkTColShow('cn_name')" prop="cn_name" label="中文名称" :width="120" ellipsis />
                    <f-table-column :visible="checkTColShow('en_name')" prop="en_name" label="英文名称" :width="140" ellipsis />
                    <f-table-column :visible="checkTColShow('content')" prop="content" label="标准值内容" :width="180">
                        <template #default="{ row }">
                            <FEllipsis v-if="row.content">
                                {{row.content}}
                                <template #tooltip>
                                    <div style="max-width:300px;word-wrap:break-word">
                                        {{row.content}}
                                    </div>
                                </template>
                            </FEllipsis>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('source_value_name')" prop="source_value_name" label="来源类型" :width="106" ellipsis>
                        <template #default="{ row }">
                            {{row.source_value_name || '--'}}
                        </template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('source')" prop="source" label="来源" :width="180" ellipsis>
                        <template #default="{ row }">
                            {{row.source || '--'}}
                        </template>
                    </f-table-column>
                    <!-- <f-table-column :visible="checkTColShow('total')" prop="total" label="版本数量" :width="100" ellipsis>
                            <template #default="{ row }">
                                <div class="wd-table-operate-btns" @click="showDataVersionList(row.edition_id)"><span class="btn-item">{{row.total || '--'}}</span></div>
                            </template>
                        </f-table-column>
                        <f-table-column :visible="checkTColShow('label_name')" prop="label_name" label="标签" :width="120" ellipsis>
                            <template #default="{ row }">
                                <div v-if="row.label_name">
                                    <FEllipsis>
                                        {{getActionRangeString(row.label_name)}}
                                        <template #tooltip>
                                            <div style="max-width:300px;word-wrap:break-word">
                                                {{getActionRangeString(row.label_name)}}
                                            </div>
                                        </template>
                                    </FEllipsis>
                                </div>
                                <div v-else>--</div>
                            </template>
                        </f-table-column> -->
                    <f-table-column :visible="checkTColShow('create_user')" prop="create_user" label="创建人" :width="120" ellipsis />
                    <f-table-column prop="create_time" :visible="checkTColShow('create_time')" label="创建时间" align="left" :width="182" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('modify_user')" prop="modify_user" label="修改人" :width="120" ellipsis>
                        <template #default="{ row }">
                            {{row.modify_user || '--'}}
                        </template>
                    </f-table-column>
                    <f-table-column prop="modify_time" :visible="checkTColShow('modify_time')" label="修改时间" align="left" :width="182" ellipsis></f-table-column>
                    <f-table-column ellipsis prop="dev_department_name" :visible="checkTColShow('dev_department_name')" label="开发科室" :width="152">
                        <template #default="{ row }">{{row.dev_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="ops_department_name" :visible="checkTColShow('ops_department_name')" label="运维科室" :width="152">
                        <template #default="{ row }">{{row.ops_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('action_range')" prop="action_range" label="可见范围" :width="152" ellipsis>
                        <template #default="{ row }">
                            <FEllipsis v-if="row.action_range">
                                {{getActionRangeString(row.action_range)}}
                                <template #tooltip>
                                    <div style="text-align:center">可见范围</div>
                                    <div style="max-width:300px;word-wrap:break-word">
                                        {{getActionRangeString(row.action_range)}}
                                    </div>
                                </template>
                            </FEllipsis>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>
                    <f-table-column :label="$t('common.operate')" :width="104" fixed="right" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li
                                    v-for="(action, index) in ruleTemplateTableActions"
                                    :key="index"
                                    class="btn-item"
                                    :class="action.class"
                                    @click="action.func(row, $event)">
                                    {{action.label}}
                                </li>
                            </ul>
                        </template>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:currentPage="pagination.page"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="loadListData"
                    @pageSizeChange="loadListData"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 高级筛选操作 -->
        <AdvanceQuery
            ref="advanceQueryRef"
            v-model:show="showAdvanceQuery"
            v-model:parentCondition="condition"
            @loadData="updateQueryConditionAndLoad">
        </AdvanceQuery>
        <!-- 新增以及编辑操作 -->
        <EditTemplate
            ref="editTemplateRef"
            v-model:show="showEditPanel"
            :tid="selectedDataId"
            :mode="editMode"
            :type="1"
            @loadData="loadListData"
        ></EditTemplate>
        <!-- 表格设置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originTableHeaders"
            type="standlist_table" />
            <!-- 版本列表 -->
            <!-- <Versions
                v-model:show="showVersionList"
                v-model:sid="currentVersionId"
            /> -->
    </div>
</template>
<script setup>
import {
    onMounted, ref, computed, provide,
} from 'vue';
import { request as FRequest, useRoute, useI18n } from '@fesjs/fes';
import {
    DownloadOutlined, UploadOutlined, MoreCircleOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import { FDivider, FMessage, FModal } from '@fesjs/fes-design';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import useEditTemplate from '@/pages/standard/hooks/useEditTemplate';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { cloneDeep } from 'lodash-es';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import EditTemplate from './components/editTemplate';
import AdvanceQuery from './components/advanceQuery.vue';
// import Versions from './components/versions.vue';
import { getActionRangeString } from './utils';


const { t: $t } = useI18n();
const editTemplateRef = ref(null);
const route = useRoute();
const urlQuery = computed(() => route.query);
const selectedDataId = ref(-1);

const {
    editMode,
    showEditPanel,
    openEditPanel,
} = useEditTemplate();

const isExporting = ref(false);

const pagination = ref({
    page: urlQuery.value.page ? +urlQuery.value.page : 1,
    size: urlQuery.value.pageSize ? +urlQuery.value.pageSize : 10,
    total: 0,
});

const originTableHeaders = [
    { prop: 'edition_id', label: '标准值ID' },
    { prop: 'cn_name', label: '中文名称' },
    { prop: 'en_name', label: '英文名称' },
    { prop: 'content', label: '标准值内容' },
    { prop: 'source_value_name', label: '来源类型' },
    { prop: 'source', label: '来源' },
    // { prop: 'total', label: '版本数量' },
    { prop: 'create_user', label: '创建人' },
    { prop: 'create_time', label: '创建时间', hidden: true },
    { prop: 'modify_user', label: '修改人' },
    { prop: 'modify_time', label: '修改时间', hidden: true },
    { prop: 'dev_department_name', label: '开发科室' },
    { prop: 'ops_department_name', label: '运维科室' },
    { prop: 'action_range', label: '可见范围', hidden: true },
    // { prop: 'label_name', label: '标签' },
];
// 未设置过缓存，默认不展示创建时间/修改时间/可见范围


// 表头设置
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: projectTableHeaders,
    showTableHeaderConfig: showProjectTableColConfig,
} = useTableHeaderConfig();

const advanceQuerySelectedCount = ref(0);

// 查询条件
const condition = ref({});

const standardLists = ref([]);
const isLoading = ref(false);

const loadListData = async () => {
    try {
        if (isLoading.value) {
            return;
        }
        const tempCondition = cloneDeep(condition);
        // eslint-disable-next-line no-use-before-define
        tempCondition.value.action_range = advanceQueryRef.value.selectVisDate.map(item => item.id);
        isLoading.value = true;
        const { data = [], total = 0 } = await FRequest('/api/v1/projector/standardValue/query', Object.assign({
            page: pagination.value.page - 1,
            size: pagination.value.size,
        }, tempCondition.value));
        pagination.value.total = total;
        standardLists.value = data;
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        pagination.value.total = 0;
        standardLists.value = [];
        isLoading.value = false;
    }
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const updateQueryConditionAndLoad = (selectParamsCount) => {
    console.log('params: ', selectParamsCount);
    advanceQuerySelectedCount.value = selectParamsCount;
    pagination.value.page = 1;
    loadListData();
    resultByInit.value = false;
};

// “更多”下拉弹框
const moreMenus = ref([
    { label: $t('common.setTableHeaderConfig'), value: 'setTable' },
]);
const search = async () => {
    try {
        pagination.value.page = 1;
        condition.value.version = '';
        condition.value.create_user = '';
        condition.value.action_range = [];
        // eslint-disable-next-line no-use-before-define
        advanceQueryRef.value.selectVisDate = [];
        advanceQuerySelectedCount.value = 0;
        loadListData();
        resultByInit.value = false;
    } catch (err) {
        console.warn(err);
    }
};

const advanceQueryRef = ref(null);
const reset = () => {
    condition.value = {};
    advanceQuerySelectedCount.value = 0;
    advanceQueryRef.value.reset();
    search();
    resultByInit.value = true;
};

// 查看版本列表
const showVersionList = ref(false);
const currentVersionId = ref('');
const showDataVersionList = (data) => {
    console.log(data);
    currentVersionId.value = +data;
    showVersionList.value = true;
};

// 高级筛选
const showAdvanceQuery = ref(false);
const toggleAdvanceQuery = () => {
    showAdvanceQuery.value = true;
};

// 点击更多里的操作
const clickMore = (value) => {
    if (value === 'setTable') {
        toggleTColConfig();
    }
};

// const systemList = ref([]);
// const getSystemList = async () => {
//     try {
//         const result = await FRequest('/api/v1/projector/standardValue/approve/all');
//         console.log(result);
//         systemList.value = result || [];
//     } catch (err) {
//         console.warn(err);
//     }
// };

// 表格列操作
const ruleTemplateTableActions = [
    {
        label: $t('common.edit'),
        func: async (row) => {
            console.log('编辑： ', row.edition_id);
            selectedDataId.value = row.edition_id;
            let currentDetail = {};
            // 编辑和详情模式mode均为view,组件内通过 isPreviewMode 再去区分编辑、详情
            if (selectedDataId.value && selectedDataId.value !== -1) {
                currentDetail = await editTemplateRef.value.getDetailByTable(selectedDataId.value);
                if (!currentDetail.editable) {
                    return FMessage.error('没有编辑权限');
                }
                openEditPanel('view');
            }
        },
    },
    {
        label: $t('common.delete'),
        class: 'btn-delete',
        func: async (row) => {
            console.log('删除 ', row.edition_id);
            selectedDataId.value = row.edition_id;
            let currentDetail = {};
            if (selectedDataId.value && selectedDataId.value !== -1) {
                currentDetail = await editTemplateRef.value.getDetailByTable(selectedDataId.value);
                if (!currentDetail.editable) {
                    return FMessage.error('没有删除权限');
                }
            }
            FModal.confirm({
                title: $t('common.prompt'),
                content: `确认删除中文名称为【${row.cn_name}】的标准值？`,
                async onOk() {
                    console.log('信息', row);
                    await FRequest('api/v1/projector/standardValue/delete', {
                        edition_id: row.edition_id,
                        type: 1,
                    });
                    FMessage.success($t('toastSuccess.deleteSuccess'));
                    loadListData();
                },
            });
        },
    },
];

// provide('systemList', computed(() => systemList.value || []));

onMounted(() => {
    loadListData();
    // getSystemList();
});
</script>
