<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <SearchFilterBar
                    ref="searchFilterBar"
                    :creatorList="creatorList"
                    :modifierList="modifierList"
                    @search="search"
                />
            </template>
            <template v-slot:operate>
                <RuleTemplateActionBar :actions="ruleTemplateActions" />
            </template>
            <template v-slot:table>
                <f-table
                    rowKey="template_id"
                    :data="ruleTemplates"
                >
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <!-- 模板id -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('template_id')"
                        prop="template_id"
                        :label="$t('ruleTemplatelist.templateId')"
                        :width="84"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 模板中文名 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('template_name')"
                        prop="template_name"
                        :label="$t('ruleTemplatelist.templateCNName')"
                        :width="172"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 模板英文名 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('en_name')"
                        prop="en_name"
                        :label="$t('ruleTemplatelist.templateENName')"
                        :width="159"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 模板描述 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('description')"
                        prop="description"
                        :label="$t('ruleTemplatelist.templateDesc')"
                        :width="186"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 数据源类型 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('datasource_type')"
                        prop="datasource_type"
                        :formatter="dataSourceTypeFormatter"
                        :label="$t('ruleTemplatelist.dataSourceType')"
                        :width="173"
                    />
                    <!-- 校验级别 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('verification_level')"
                        prop="verification_level"
                        :formatter="verificationLevelFormatter"
                        :label="$t('ruleTemplatelist.verificationLevel')"
                        :width="88"
                    />
                    <!-- 校验类型 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('verification_type')"
                        prop="verification_type"
                        :formatter="verificationTypeFormatter"
                        :label="$t('ruleTemplatelist.verificationType')"
                        :width="88"
                    />
                    <!-- 采样方式 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('action_type')"
                        prop="action_type"
                        :formatter="actionTypeFormatter"
                        :label="$t('ruleTemplatelist.actionType')"
                        :width="88"
                    />
                    <!-- 是否保存异常数据 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('save_mid_table')"
                        prop="save_mid_table"
                        :formatter="booleanFormatter"
                        :label="$t('ruleTemplatelist.exceptionDatabase')"
                        :width="144"
                    />
                    <!-- 创建人 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('creator')"
                        prop="creator"
                        :label="$t('ruleTemplatelist.creator')"
                        :width="120"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 创建时间 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('create_time')"
                        prop="create_time"
                        :label="$t('ruleTemplatelist.createTime')"
                        :width="144"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 修改人 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('modify_name')"
                        prop="modify_name"
                        :label="$t('ruleTemplatelist.modifyName')"
                        :width="120"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 修改时间 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('modify_time')"
                        prop="modify_time"
                        :label="$t('ruleTemplatelist.modifyTime')"
                        :width="144"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 开发科室 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('dev_department_name')"
                        prop="dev_department_name"
                        :label="$t('ruleTemplatelist.developDepartment')"
                        :width="240"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 运维科室 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('ops_department_name')"
                        prop="ops_department_name"
                        :label="$t('ruleTemplatelist.maintainDepartment')"
                        :width="240"
                        :formatter="formatterEmptyValue"
                    />
                    <!-- 可见范围 -->
                    <f-table-column
                        ellipsis
                        :visible="checkTColShow('visibility_department_list')"
                        prop="visibility_department_list"
                        :formatter="visibilityDepartmentFormatter"
                        :label="$t('ruleTemplatelist.visibleRange')"
                        :width="144"
                    />
                    <!-- 操作 -->
                    <f-table-column
                        #default="{ row = {}}"
                        ellipsis
                        :label="$t('common.operate')"
                        :width="60"
                        fixed="right"
                    >
                        <ul class="btn-list">
                            <li
                                v-for="(action, index) in ruleTemplateTableActions"
                                :key="index"
                                class="btn-item btn-link"
                                @click="action.func(row.template_id, $event)"
                            >
                                {{action.label}}
                            </li>
                        </ul>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="ruleTemplates.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="pageChange(false)"
                    @pageSizeChange="pageChange(true)"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 新增以及编辑操作 -->
        <TemplateDrawer
            v-model:show="showEditPanel"
            v-model:mode="editMode"
            :templateType="templateType"
            :tid="selectedDataId"
            @addTemplate="pageChange(false)"
            @editTemplate="pageChange(false)"
            @deleteTemplate="pageChange(false)"
        ></TemplateDrawer>
        <!-- 单表模板表头配置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="ruleTemplateTableHeaders"
            v-model:show="showRuleTemplateTableColConfig"
            :originHeaders="originRuleTemplateHeaders"
            type="single_template_list"
        />
    </div>
</template>
<script setup>
import {
    ref, defineProps, onMounted, inject, watch,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import RuleTemplateActionBar from '@/pages/projects/components/projectActionBar';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import useProjects from '@/pages/projects/hooks/useProject';
import { MAX_PAGE_SIZE, DATASOURCE_TYPE_MAP, TEMPLATE_FORMAT_LIST } from '@/assets/js/const';
import SearchFilterBar from './searchFilterBar.vue';
import EditTemplate from './editTemplate';
import TemplateDrawer from './templateDrawer.vue';
import useEditTemplate from '../hooks/useEditTemplate';
import {
    fetchTemplates,
} from '../api';

const props = defineProps({
    originRuleTemplateHeaders: {
        type: Array,
        required: true,
    },
    templateType: {
        type: Number,
        required: true,
        default: 1,
    },
});
const { t: $t } = useI18n();


const showLoading = ref(false);

const {
    editMode,
    showEditPanel,
    openEditPanel,
    selectedDataId,
    ruleTemplateTableActions,
} = useEditTemplate();

// 规则模板 topbar按钮配置
const ruleTemplateActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('ruleTemplatelist.addTemplate'),
        handler: () => {
            selectedDataId.value = -1;
            openEditPanel('add');
        },
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

const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: ruleTemplateTableHeaders,
    showTableHeaderConfig: showRuleTemplateTableColConfig,
} = useTableHeaderConfig();

// 单表模板表格相关
const {
    pagination,
    projects: ruleTemplates,
    getProjects: getRuleTemplates,
    pageChange,
    queryCondition,
} = useProjects(fetchTemplates, showLoading);

const visibilityDepartmentFormatter = ({ cellValue }) => (cellValue ? cellValue.map(item => item.name).join(', ') : '--');
const verificationTypeList = inject('verificationTypeList', ref([]));
const verificationLevelList = inject('verificationLevelList', ref([]));
const verificationTypeFormatter = ({ cellValue }) => {
    const target = verificationTypeList.value.find(item => item.value === cellValue);
    return target ? target.label : '--';
};
const verificationLevelFormatter = ({ cellValue }) => {
    const target = verificationLevelList.value.find(item => item.value === cellValue);
    return target ? target.label : '--';
};
const formatterEmptyValue = ({ cellValue }) => cellValue || '--';
const booleanFormatter = ({ cellValue }) => (cellValue ? $t('common.save') : $t('common.dontSave'));
// 数据源类型formatter
const dataSourceTypeFormatter = ({ cellValue }) => {
    const tempData = Array.isArray(cellValue) ? cellValue : [cellValue];
    return tempData.map(item => DATASOURCE_TYPE_MAP[item] || item).join(', ') || '--';
};
// 采样方式formatter
const actionTypeFormatter = ({ cellValue }) => {
    const target = TEMPLATE_FORMAT_LIST.find(item => item.key === cellValue);
    return target ? target.value : '--';
};

const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const search = (value) => {
    console.log('search', value);
    queryCondition.value = { template_type: props.templateType, ...value };
    getRuleTemplates();
    resultByInit.value = value.searchType === 'reset';
};

const creatorList = ref([]);
const modifierList = ref([]);
const searchFilterBar = ref(null);
const init = async () => {
    try {
        queryCondition.value = { template_type: props.templateType };
        await getRuleTemplates();
        searchFilterBar.value.reset();
        resultByInit.value = true;
    } catch (error) {
        console.log('init data error:', error);
    }
};
watch(() => props.templateType, async () => {
    await init();
});
</script>
<style lang="less" scoped>
@import "@/style/varible";

.btn-link {
    color: @blue-color;
    cursor: pointer;
}
</style>
