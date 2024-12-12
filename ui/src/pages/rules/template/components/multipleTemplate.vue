<template>
    <div class="wd-content-body">
        <RuleTemplateActionBar :actions="ruleTemplateActions" />
        <section class="table-container">
            <div v-if="showLoading">
                <BPageLoading :loadingText="{ loading: '' }" />
            </div>
            <f-table v-else-if="ruleTemplateTableHeaders.length" rowKey="template_id" :data="ruleTemplates">
                <f-table-column :visible="checkTColShow('template_id')" prop="template_id" :label="$t('ruleTemplatelist.templateId')" :width="78" ellipsis />
                <f-table-column :visible="checkTColShow('template_name')" prop="template_name" :label="$t('ruleTemplatelist.templateName')" :width="160" ellipsis />
                <f-table-column :visible="checkTColShow('datasource_type')" prop="datasource_type" :formatter="dataSourceTypeFormatter" :label="$t('ruleTemplatelist.dataSourceType')" :width="102" ellipsis />
                <f-table-column ellipsis :visible="checkTColShow('action_type')" prop="action_type" :formatter="templateTypeFormatter" :label="$t('ruleTemplatelist.templateType')" :width="88" />
                <f-table-column :visible="checkTColShow('save_mid_table')" prop="save_mid_table" :formatter="booleanFormatter" :label="$t('ruleTemplatelist.saveData')" :width="144" ellipsis />
                <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="60" ellipsis>
                    <ul class="btn-list">
                        <li
                            v-for="(action, index) in ruleTemplateTableActions"
                            :key="index"
                            class="btn-item btn-link"
                            @click="action.func(row.template_id, $event)">
                            {{action.label}}
                        </li>
                    </ul>
                </f-table-column>
            </f-table>
        </section>
    </div>
    <div class="table-pagination-container">
        <FPagination
            v-show="ruleTemplates.length > 0"
            v-model:currentPage="pagination.current"
            v-model:pageSize="pagination.size"
            show-size-changer
            show-total
            :total-count="pagination.total"
            @change="pageChange(false)"
            @pageSizeChange="pageChange(true)"></FPagination>
    </div>
    <!-- 新增以及编辑操作 -->
    <EditTemplate
        v-model:show="showEditPanel"
        :tid="selectedDataId"
        :type="3"
        :mode="editMode"
        :dataSourceTypeFormatter="dataSourceTypeFormatter"
        :templateTypeFormatter="templateTypeFormatter"
        @loadData="pageChange(false)"
    ></EditTemplate>
    <!-- 跨表校验模板表头配置弹窗 -->
    <BTableHeaderConfig
        v-model:headers="ruleTemplateTableHeaders"
        v-model:show="showRuleTemplateTableColConfig"
        :originHeaders="originRuleTemplateHeaders"
        type="multiple_template_list" />
</template>
<script setup>
import { ref, defineProps } from 'vue';
import { useI18n } from '@fesjs/fes';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { BTableHeaderConfig, BPageLoading } from '@fesjs/traction-widget';
import RuleTemplateActionBar from '@/pages/projects/components/projectActionBar';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import useProjects from '@/pages/projects/hooks/useProject';
import EditTemplate from './editTemplate';
import useEditTemplate from '../hooks/useEditTemplate';
import {
    fetchMultipleTemplates,
} from '../api';

const props = defineProps({
    originRuleTemplateHeaders: {
        type: Array,
        required: true,
    },
    dataSourceTypeFormatter: {
        type: Function,
        requred: true,
    },
    templateTypeFormatter: {
        type: Function,
        requred: true,
    },
    booleanFormatter: {
        type: Function,
        required: true,
    },
});
const { t: $t } = useI18n();

const showLoading = ref(false);
// 规则模板 topbar按钮配置
const ruleTemplateActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('ruleTemplatelist.addTemplate'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
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
    editMode,
    showEditPanel,
    openEditPanel,
    selectedDataId,
    ruleTemplateTableActions,
} = useEditTemplate();

const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: ruleTemplateTableHeaders,
    showTableHeaderConfig: showRuleTemplateTableColConfig,
} = useTableHeaderConfig();

// 跨表校验模板表格相关
const {
    pagination,
    projects: ruleTemplates,
    getProjects: getRuleTemplates,
    pageChange,
} = useProjects(fetchMultipleTemplates, showLoading);
</script>
<style lang="less" scoped>
@import "@/style/varible";

.btn-link {
    color: @blue-color;
    cursor: pointer;
}
</style>
