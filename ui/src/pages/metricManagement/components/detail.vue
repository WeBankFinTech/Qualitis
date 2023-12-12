<template>
    <div v-if="data">
        <FForm class="index-detail-form" labelWidth="90px" labelPosition="left" :model="data">
            <FFormItem :label="$t('indexManagement.indexEnName')" prop="name">{{data.name}}</FFormItem>
            <FFormItem :label="$t('indexManagement.indexCnName')" prop="cn_name">{{data.cn_name}}</FFormItem>
            <FFormItem :label="$t('indexManagement.indexDesc')" prop="metric_desc">
                <FEllipsis :line="2">
                    {{data.metric_desc}}
                    <template #tooltip>
                        <div style="width:300px;word-wrap:break-word">
                            {{data.metric_desc}}
                        </div>
                    </template>
                </FEllipsis>
            </FFormItem>
            <FFormItem :label="$t('indexManagement.indexCategory')" prop="type">{{getCategoryContent(data.type)}}</FFormItem>
            <FFormItem :label="$t('indexManagement.indexFrequency')" prop="indexEnName">{{getFrequencyConetnt(data.frequency)}}</FFormItem>
            <FFormItem :label="$t('indexManagement.en_code')" prop="en_code">{{data.en_code}}</FFormItem>
            <FFormItem :label="$t('indexManagement.bussinessDimension')" prop="buss_code">{{getBussinessDimensionContent(data.buss_code)}}</FFormItem>
            <FFormItem v-if="data.buss_code === 1" :label="$t('indexManagement.subsystem')" prop="sub_system_name">{{data.sub_system_name}}</FFormItem>
            <FFormItem :label="$t('indexManagement.inMultiDCN')" prop="multi_env">{{data.multi_env ? $t('common.yes') : $t('common.no')}}</FFormItem>
            <FFormItem v-if="data.buss_code === 2" :label="$t('indexManagement.product')" prop="product_name">{{data.product_name}}</FFormItem>
            <FFormItem v-if="data.buss_code === 3" :label="$t('indexManagement.customize')" prop="buss_custom">{{data.buss_custom}}</FFormItem>
            <FFormItem :label="$t('indexManagement.developDepartment')" prop="dev_department_name">{{departmentDetail(data.dev_department_name)}}</FFormItem>
            <FFormItem :label="$t('indexManagement.maintainDepartment')" prop="ops_department_name">{{departmentDetail(data.ops_department_name)}}</FFormItem>
            <FFormItem :label="$t('indexManagement.visibleRange')" prop="action_range">
                <FEllipsis v-if="data.action_range?.length" :line="2">
                    {{actionRangeDetail(data.action_range)}}
                    <template #tooltip>
                        <div style="text-align:center">可见范围</div>
                        <div style="width:300px;word-wrap:break-word">
                            {{actionRangeDetail(data.action_range)}}
                        </div>
                    </template>
                </FEllipsis>
                <div v-else>--</div>
            </FFormItem>
            <FFormItem :label="$t('indexManagement.inUse')" prop="available">{{data.available ? $t('common.yes') : $t('common.no')}}</FFormItem>
        </FForm>
    </div>
</template>
<script setup>
import { defineProps } from 'vue';
import { useI18n } from '@fesjs/fes';
import { getLabelFromList } from '@/assets/js/utils';
import { getMoreActionRangeString, actionRangeDetail, departmentDetail } from '@/common/utils';

const { t: $t } = useI18n();

const props = defineProps({
    data: {
        type: Object,
        required: true,
    },
    metricCategories: {
        type: Array,
        required: true,
    },
    metricFrequencies: {
        type: Array,
        required: true,
    },
    bussinessDimensions: {
        type: Array,
        required: true,
    },
    subSystems: {
        type: Array,
        required: true,
    },
    products: {
        type: Array,
        required: true,
    },
});

const getCategoryContent = value => getLabelFromList(props.metricCategories, value);
const getFrequencyConetnt = value => getLabelFromList(props.metricFrequencies, value);
const getBussinessDimensionContent = value => getLabelFromList(props.bussinessDimensions, value);
const getSubSystemContent = value => getLabelFromList(props.subSystems, value);
const getProductContent = value => getLabelFromList(props.products, value);
</script>
<style lang="less" scoped>
.detail-modal-item {
    margin-bottom: 16px;
    color: #0F1222;
    &:last-child {
        margin-bottom: 0;
    }
    .label {
        display: inline-block;
        width: 70px;
        text-align: right;
        margin-right: 16px;
        color: #63656f;
    }
}
</style>
