<template>
    <FForm ref="templateFormRef" labelWidth="90px" labelPosition="right" :model="templateForm">
        <FFormItem :label="`${$t('ruleTemplatelist.templateName')}`" prop="templateName">{{templateForm.templateName}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.clusterNum')}`" prop="clusterNum">{{templateForm.clusterNum}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.dbNum')}`" prop="dbNum">{{templateForm.dbNum}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.tableNum')}`" prop="tableNum">{{templateForm.tableNum}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.fieldNum')}`" prop="fieldNum">{{templateForm.fieldNum}}</FFormItem>
        <FFormItem :label="$t('ruleTemplatelist.dataSourceType')" prop="datasourceType">{{dataSourceTypeFormatter({ cellValue: templateForm.datasourceType })}}</FFormItem>
        <FFormItem :label="$t('ruleTemplatelist.parseFormat')" prop="actionType">{{templateTypeFormatter({ cellValue: templateForm.actionType })}}</FFormItem>
        <FFormItem :label="$t('ruleTemplatelist.developDepartment')" prop="dev_department_name">{{departmentDetail(templateForm.dev_department_name)}}</FFormItem>
        <FFormItem :label="$t('ruleTemplatelist.maintainDepartment')" prop="ops_department_name">{{departmentDetail(templateForm.ops_department_name)}}</FFormItem>
        <FFormItem :label="$t('ruleTemplatelist.visibleRange')" prop="action_range">
            <FEllipsis v-if="templateForm.action_range.length" :line="2">
                {{actionRangeDetail(templateForm.action_range)}}
                <template #tooltip>
                    <div style="text-align:center">可见范围</div>
                    <div style="width:300px;word-wrap:break-word">
                        {{actionRangeDetail(templateForm.action_range)}}
                    </div>
                </template>
            </FEllipsis>
            <div v-else>--</div>
        </FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.templateStatisticalFunctions')}`" prop="statisticalFunctions">{{statisticalFunctionsLabel}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.numberType')}`" prop="numberType">{{numberTypeLabel}}</FFormItem>
        <FFormItem :label="`${$t('ruleTemplatelist.templateStatisticsName')}`" prop="statisticsName">{{caculateForm.statisticsName}}</FFormItem>
        <FFormItem :label="`中间表`" prop="statisticsName">{{tableForm.templateSql}}</FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, computed,
} from 'vue';
import { getMoreActionRangeString, actionRangeDetail, departmentDetail } from '@/common/utils';
import {
    useI18n,
} from '@fesjs/fes';

const { t: $t } = useI18n();

// 参数type代表不同的模版类型
// 参数id代表需要编辑的模版id，尽在mode===edit的时候有效
const props = defineProps({
    templateForm: {
        type: Object,
        default: {},
    },
    caculateForm: {
        type: Object,
        default: {},
    },
    tableForm: {
        type: Object,
        default: {},
    },
    statisticalFunctionsLabel: {
        type: String,
        default: '',
    },
    numberTypeLabel: {
        type: String,
        default: '',
    },
    dataSourceTypeFormatter: {
        type: Function,
        default: () => {},
    },
    templateTypeFormatter: {
        type: Function,
        default: () => {},
    },
});
</script>
