<template>
    <FForm
        ref="feildRef"
        :model="feildData"
        :labelWidth="labelWidth"
        :rules="rules"
        :labelPosition="editMode !== 'display' ? 'right' : 'left'"
    >
        <FFormItem v-if="showFields" label="校验字段" prop="colNamesStrArr">
            <FSelect
                v-model="feildData.colNamesStrArr"
                collapseTags
                :collapseTagsLimit="2"
                filterable
                multiple
                class="form-edit-input"
                @change="onColumnChange">
                <FOption
                    v-for="col in columns"
                    :key="col.column_name"
                    :value="col.column_name"
                    :label="`${col.column_name} (${col.data_type})`"
                ></FOption>
            </FSelect>
            <div class="form-preview-label">
                <FSpace>
                    <FTag v-for="(item, index) in feildData.colNamesStrArr" :key="index" type="info" size="small">
                        <FEllipsis style="max-width: 240px">{{item}}</FEllipsis>
                    </FTag>
                </FSpace>
            </div>
        </FFormItem>
        <FFormItem v-if="showFilterFields" label="过滤字段" prop="filterColNamesStrArr">
            <FSelect
                v-model="feildData.filterColNamesStrArr"
                collapseTags
                :collapseTagsLimit="2"
                filterable
                multiple
                class="form-edit-input"
                @change="onColumnChange">
                <FOption
                    v-for="col in columns"
                    :key="col.column_name"
                    :value="col.column_name"
                    :label="`${col.column_name} (${col.data_type})`"
                ></FOption>
            </FSelect>
            <div class="form-preview-label">
                <FSpace>
                    <FTag v-for="(item, index) in feildData.filterColNamesStrArr" :key="index" type="info" size="small">
                        <FEllipsis style="max-width: 240px">{{item}}</FEllipsis>
                    </FTag>
                </FSpace>
            </div>
        </FFormItem>
    </FForm>
</template>

<script setup>
import {
    ref, computed, provide, nextTick, watch, inject, unref, onMounted, defineProps, defineEmits,
} from 'vue';
import { useI18n, request } from '@fesjs/fes';
import { useStore } from 'vuex';
import { NUMBER_TYPES } from '@/common/utils';

const { t: $t } = useI18n();
const store = useStore();
const ruleData = computed(() => store.state.rule);
const feildRef = ref(null);

const props = defineProps({
    colNamesStrArr: {
        type: Object,
        default: () => [],
    },
    filterColNamesStrArr: {
        type: Object,
        default: () => [],
    },
    columns: {
        type: Object,
    },
    showFields: {
        type: Boolean,
    },
    showFilterFields: {
        type: Boolean,
    },
    // 页面的类型 普通规则组：commonList，表规则组：groupList
    lisType: {
        type: String,
        default: 'commonList',
    },
    rules: {
        type: Object,
    },
});

const emit = defineEmits(['update:colNamesStrArr', 'update:filterColNamesStrArr', 'onColumnChange']);

const listType = computed(() => props.lisType);
// label宽度
const labelWidth = computed(() => {
    if (listType.value === 'groupList') {
        return 70;
    }
    return 96;
});
// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (listType.value === 'groupList') {
        return ruleData.value.currentProject.groupRuleEditMode;
    }
    if (listType.value === 'listByTemplate') {
        return 'edit';
    }
    return ruleData.value.currentProject.editMode;
});

const rules = computed(() => props.rules);
const colNamesStrArr = computed(() => props.colNamesStrArr);
const filterColNamesStrArr = computed(() => props.filterColNamesStrArr);
const columns = computed(() => props.columns);
const showFields = computed(() => props.showFields);
const showFilterFields = computed(() => props.showFilterFields);

const feildData = ref({
    colNamesStrArr,
    filterColNamesStrArr,
});

const onColumnChange = (selectedCol = []) => {
    emit('onColumnChange', selectedCol);
};

const valid = async () => {
    try {
        const result = await feildRef.value.validate();
        return true;
    } catch (e) {
        console.warn(e);
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid });

</script>
