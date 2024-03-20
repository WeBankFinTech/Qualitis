<template>
    <FDrawer
        displayDirective="if"
        contentClass="related-rule-drawer"
        :show="show"
        :width="940"
        :title="title"
        @cancel="handleClose">
        <div class="page-header-condition" style="margin:24px 0">
            <div v-if="mode === 'table'" class="condition-item">
                <span class="condition-label">{{$t('common.relationObject')}}</span>
                <FSelect v-model="searchForm.relation_object_type" filterable clearable :width="160">
                    <FOption v-for="item in relationObjectTypeList" :key="item.label" :value="item.value">{{item.label}}</FOption>
                </FSelect>
            </div>
            <div v-if="mode === 'table' && searchForm.relation_object_type === '2'" class="condition-item">
                <span class="condition-label">{{$t('common.column')}}</span>
                <FSelect v-model="searchForm.column" filterable clearable :width="160">
                    <FOption v-for="item in columList" :key="item.label" :value="item.value">{{item.label}}</FOption>
                </FSelect>
            </div>
            <div class="condition-item">
                <span class="condition-label">{{$t('common.verificationTemplate')}}</span>
                <FSelect v-model="searchForm.rule_template_id" filterable clearable :width="160">
                    <FOption v-for="(item,index) in ruleTemplateDataList"
                             :key="index" :value="item.value" :label="item.label">
                        <FEllipsis>{{item.label}}</FEllipsis>
                    </FOption>
                </FSelect>
            </div>
            <div class="condition-item">
                <FSpace :size="CONDITIONBUTTONSPACE">
                    <FButton type="primary" @click="search">查询</FButton>
                </FSpace>
            </div>
        </div>
        <div v-show="!showSelection" class="btn-list topbar">
            <FButton type="primary" class="btn-item" :disabled="!(rules.length > 0)" @click="handleExecuteRules">{{$t('myProject.run')}}</FButton>
            <FButton class="btn-item" :disabled="!(rules.length > 0)" @click="handleExport">{{$t('myProject.DownloadRules')}}</FButton>
        </div>
        <div v-show="showSelection && isHandleExport" class="btn-list topbar">
            <FButton type="primary" class="btn-item" :disabled="isDownloading" :loading="isDownloading" @click="handleExportConfirm">{{$t('myProject.confirmExport')}}</FButton>
            <FButton class="btn-item" @click="handleCancelExport">{{$t('common.cancel')}}</FButton>
        </div>
        <div v-show="showSelection && isHandleExecuteRules" class="btn-list topbar">
            <FButton type="primary" class="btn-item" :disabled="isDownloading" :loading="isDownloading" @click="handleExecuteRulesConfirm">{{$t('myProject.confirmExecute')}}</FButton>
            <FButton class="btn-item" @click="handleCancelExecuteRules">{{$t('common.cancel')}}</FButton>
        </div>
        <div v-if="showLoading">
            <BPageLoading :loadingText="{ loading: '' }" />
        </div>
        <f-table v-else ref="relatedRuleTable" row-key="rule_id" :data="rules" @selectionChange="changeRulesSelection">
            <template #empty>
                <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
            </template>
            <f-table-column type="selection" :visible="showSelection" :width="32" :selectable="selectable" />
            <f-table-column ellipsis prop="project_name" :label="$t('ruleQuery.projectName')" :width="102" />
            <f-table-column ellipsis prop="rule_group_name" :label="$t('ruleQuery.ruleGroupName')" :width="102" />
            <f-table-column #default="{ row = {}}" ellipsis prop="rule_name" :label="$t('ruleQueryPage.technicalRuleName')" :width="134">
                <span class="a-link" @click="handleRowClick(row)">{{row.rule_name}}</span>
            </f-table-column>
            <f-table-column ellipsis prop="template_name" :label="$t('common.verificationTemplate')" :width="76" />
            <f-table-column ellipsis prop="rule_type" :formatter="ruleTypeFilter" :label="$t('common.ruleType')" :width="96" />
            <f-table-column ellipsis prop="relation_object" :label="$t('common.relationObject')" :width="95" />
        </f-table>
        <div class="table-pagination-container">
            <FPagination
                v-show="rules.length > 0"
                v-model:currentPage="pagination.current"
                v-model:pageSize="pagination.size"
                show-size-changer
                show-total
                :total-count="pagination.total"
                @change="changePage(false)"
                @pageSizeChange="changePage(true)"></FPagination>
        </div>
        <!-- 任务执行 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            @ok="excuteRuleInProjectDetail" />
    </FDrawer>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, reactive, onBeforeUpdate,
} from 'vue';
import { useI18n, useRouter } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import useExport from '@/pages/projects/hooks/useExport';
import useExecutation from '@/pages/projects/hooks/useExecutation';
import { CONDITIONBUTTONSPACE, MAX_PAGE_SIZE } from '@/assets/js/const';
import { BPageLoading } from '@fesjs/traction-widget';
import { fetctRelatedRulesOfTableDetail, fetctRuleTemplateList, fetchColumnsOfTableDetail } from '../api';

const router = useRouter();

const { t: $t } = useI18n();
const props = defineProps({
    // mode分为table - 表规则，column - 字段规则
    mode: {
        type: String,
        default: 'table',
    },
    title: {
        type: String,
        default: '',
    },
    show: Boolean,
    cluster: String,
    db: String,
    table: String,
    datasource_id: {
        type: String,
        default: '',
    },
    column: {
        columnName: String,
        dataType: String,
    },
});
const relationObjectTypeList = [
    {
        value: '1',
        label: '表',
    },
    {
        value: '2',
        label: '字段',
    },
];
const emit = defineEmits(['update:show']);

const showLoading = ref(false);
const ruleTypes = ['', $t('ruleTemplatelist.singleTableType'), $t('ruleTemplatelist.singleOrMultipleIndexType'), $t('common.crossTableType'), $t('common.fileType')];
const rules = ref([]);
const rulesSelection = ref([]);
const changeRulesSelection = (selection) => {
    rulesSelection.value = Object.values(selection);
};
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const searchForm = reactive({
    rule_template_id: '',
    relation_object_type: '',
    column: '',
});
const resetSearchForm = () => {
    searchForm.rule_template_id = '';
    searchForm.relation_object_type = '';
    searchForm.column = '';
};
const relatedRuleTable = ref();
const ruleTypeFilter = ({ cellValue }) => ruleTypes[cellValue];
const selectType = ref('execute'); // execute 工作流项目不可选 ，export 全部可选
const selectable = ({ row }) => {
    if (!row.rule_enable || (selectType.value === 'execute' && row.project_type === 2)) {
        return false;
    } return true;
};

const getParams = () => {
    const {
        datasource_id,
        cluster,
        db,
        table,
        column = {},
    } = props;
    const params = {
        rule_template_id: searchForm.rule_template_id,
        relation_object_type: searchForm.relation_object_type,
        cluster,
        datasource_id,
        db,
        table,
        page: pagination.current - 1,
        size: pagination.size,
    };
    // column字段为空时会查不到数据
    if (searchForm.relation_object_type === '2' && searchForm.column) {
        params.column = searchForm.column;
    }
    if (Object.keys(column).length > 0) {
        const { columnName, dataType } = column;
        params.column = `${columnName}:${dataType}`;
    }
    return params;
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const search = async () => {
    try {
        const params = getParams();
        rules.value = [];
        pagination.total = 0;
        showLoading.value = true;
        const { totalCount, content } = await fetctRelatedRulesOfTableDetail(params);
        if (Array.isArray(content)) {
            rules.value = content;
            pagination.total = totalCount;
        }
        showLoading.value = false;
        resultByInit.value = false;
    } catch (e) {
        console.error(e);
        showLoading.value = false;
    }
};
const columList = ref([]);
const getcolumnList = async () => {
    const {
        cluster,
        db,
        table,
        datasource_id,
    } = props;
    const params = {
        datasource_id,
        cluster,
        db,
        table,
        page: 0,
        size: MAX_PAGE_SIZE,
    };
    try {
        const { content } = await fetchColumnsOfTableDetail(params);
        console.log('getcolumnList-res', content);
        // eslint-disable-next-line camelcase
        columList.value = content.map(({ column_name, data_type }) => ({ value: `${column_name}:${data_type}`, label: column_name }));
    } catch (e) {
        console.error(e);
    }
};
const ruleTemplateDataList = ref([]);
const getRuleTemplateList = async () => {
    try {
        const res = await fetctRuleTemplateList();
        ruleTemplateDataList.value = res.map(({ template_id, template_name }) => ({ value: template_id, label: template_name }));
    } catch (e) {
        console.error(e);
    }
};
const changePage = (flag) => {
    if (flag) pagination.current = 1;
    search();
};

// 项目导出
const {
    isExporting: showSelection,
    isDownloading,
    exportFileByParams,
} = useExport('ruleQuery');

const handleClose = () => {
    showSelection.value = false;
    emit('update:show');
};

// 处理导出规则
const isHandleExport = ref(false);
const handleExport = () => {
    selectType.value = 'export';
    showSelection.value = true;
    isHandleExport.value = true;
};
const handleCancelExport = () => {
    showSelection.value = false;
    isHandleExport.value = false;
    relatedRuleTable.value.clearSelection();
};
const handleExportConfirm = () => {
    const ruleIds = rulesSelection.value;
    if (ruleIds.length === 0) {
        FMessage.warn($t('common.selectOne'));
        return;
    }
    exportFileByParams({ rule_ids: ruleIds });
    handleCancelExport();
};

// 处理执行规则
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPreInRuleQuery,
    excuteRuleInProjectDetail,
} = useExecutation();
const isHandleExecuteRules = ref(false);
const handleExecuteRules = () => {
    selectType.value = 'execute';
    showSelection.value = true;
    isHandleExecuteRules.value = true;
};
const handleCancelExecuteRules = () => {
    showSelection.value = false;
    isHandleExecuteRules.value = false;
    relatedRuleTable.value.clearSelection();
};
const handleExecuteRulesConfirm = () => {
    const ruleIds = rulesSelection.value;
    if (ruleIds.length === 0) {
        FMessage.warn($t('common.selectOne'));
        return;
    }
    const selectedRuleArray = ruleIds.map(item => ({ rule_id: item }));
    executeTaskPreInRuleQuery(rules.value, selectedRuleArray);
    handleCancelExecuteRules();
};
const handleRowClick = (row) => {
    console.log('handleRowClick', row);
    let ruleType = 'rules';
    if (row.table_group) ruleType = 'tableGroupRules';
    router.push(`/projects/${ruleType}?ruleGroupId=${row.rule_group_id}&id=${row.rule_id}&workflowProject=${row.project_type === 2}&projectId=${row.project_id}`);
};
onBeforeUpdate(async () => {
    resetSearchForm();
    getRuleTemplateList();
    getcolumnList();
    await search();
    resultByInit.value = true;
});
</script>
<style lang="less" scoped>
.related-rule-drawer {
    .topbar {
        margin: 8px 0 24px;
    }
}
</style>
