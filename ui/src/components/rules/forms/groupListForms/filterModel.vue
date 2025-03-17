<template>
    <!-- 筛选弹框 -->
    <FModal
        :show="showFilterSearchModal"
        :width="572"
        :title="$t('_.规则筛选')"
        :maskClosable="false"
        @ok="() => emit('onSearchFilterData')"
        @cancel="() => emit('cancelSearchFilterData')"
    >
        <FForm
            ref="filterQueryForm"
            :label-width="70"
            labelPosition="right"
            class="filter-modal-form"
            :model="filterQueryData"
        >
            <FFormItem :label="$t('common.ruleEnName')" prop="rule_name">
                <FInput
                    v-model="filterQueryData.rule_name"
                    class="form-edit-input"
                    :placeholder="$t('common.searchEnKeyWords')"
                    clearable
                />
            </FFormItem>
            <FFormItem :label="$t('common.ruleCnName')" prop="rule_cn_name">
                <FInput
                    v-model="filterQueryData.rule_cn_name"
                    class="form-edit-input"
                    :placeholder="$t('common.searchCnKeyWords')"
                    clearable
                />
            </FFormItem>
            <FFormItem :label="$t('_.规则类型')" prop="rule_type">
                <FSelect
                    v-model="filterQueryData.rule_type"
                    class="form-edit-input"
                    filterable
                    :placeholder="$t('_.请选择规则类型')"
                    clearable
                >
                    <FOption
                        v-for="item in ruleTypeListOptions"
                        :key="item.value"
                        :value="item.value"
                        :label="item.label"
                    ></FOption>
                </FSelect>
            </FFormItem>
            <FFormItem :label="$t('common.verificationTemplate')" prop="template_id">
                <FSelect
                    v-model="filterQueryData.template_id"
                    class="form-edit-input"
                    filterable
                    :placeholder="$t('_.请选择校验模板')"
                    clearable
                >
                    <FOption
                        v-for="item in checkTemplateList"
                        :key="item.template_id"
                        :value="item.template_id"
                        :label="item.template_name"
                    ></FOption>
                </FSelect>
            </FFormItem>
            <FFormItem :label="$t('_.字段')" prop="col_names">
                <FSelect
                    v-model="filterQueryData.col_names"
                    multiple
                    class="form-edit-input"
                    clearable
                    filterable
                >
                    <FOption
                        v-for="col in columns"
                        :key="col.column_name"
                        :value="`${col.column_name}:${col.data_type}`"
                        :label="`${col.column_name} (${col.data_type})`"
                    ></FOption>
                </FSelect>
            </FFormItem>
        </FForm>
        <template #footer>
            <FSpace justify="end">
                <FButton @click="() => emit('cancelSearchFilterData')">{{$t('common.cancel')}}</FButton>
                <FButton @click="() => emit('resetFilterQueryForm')">{{$t('common.reset')}}</FButton>
                <FButton type="primary" @click="() => emit('onSearchFilterData')">{{$t('common.ok')}}</FButton>
            </FSpace>
        </template>
    </FModal>
</template>
<script setup>

import {
    computed,
    ref,
    defineEmits,
    defineProps,
    onUnmounted,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import { useNewFormModel } from '@/common/useModel';
import { useListener } from '@/components/rules/utils';
import { ruleTypeList, DOCUMENT_CHECK_RULE_TYPE, SINGLE_CHECK_RULE_TYPE } from '@/common/utils';

const store = useStore();
const ruleData = computed(() => store.state.rule);

const singleCheckTemplateList = computed(() => ruleData.value.singleCheckTemplateList);
const documentCheckTemplateList = computed(() => ruleData.value.documentCheckTemplateList);
const columns = computed(() => ruleData.value.verifyColumns);
const filterQueryForm = ref();
const { t: $t } = useI18n();
const ruleTypeListOptions = ref(ruleTypeList);
const props = defineProps({
    filterQueryData: {
        type: Object,
        default: () => { },
    },
    showFilterSearchModal: {
        type: Boolean,
        default: false,
    },
});

const emit = defineEmits([
    'update:filterQueryData',
    'update:showFilterSearchModal',
    'onSearchFilterData',
    'cancelSearchFilterData',
    'resetFilterQueryForm',
]);

const filterData = useNewFormModel(props, emit, ['filterQueryData']);
const filterQueryData = computed(() => filterData.filterQueryData);

const checkTemplateList = computed(() => {
    if (filterQueryData.value.rule_type === DOCUMENT_CHECK_RULE_TYPE) return documentCheckTemplateList.value;
    if (filterQueryData.value.rule_type === SINGLE_CHECK_RULE_TYPE) return singleCheckTemplateList.value;
    return [];
});
useListener('RESET_FILTER_FORM', () => {
    filterQueryForm.value?.resetFields();
});
</script>
