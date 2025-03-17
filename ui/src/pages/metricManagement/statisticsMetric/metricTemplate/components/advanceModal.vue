<template>
    <FModal v-model:show="showModal" :title="$t('common.advanceSearch')" :ok-text="$t('common.ok')" :mask-closable="false" @ok="handleOk">
        <FForm ref="filterForm" :label-width="60" label-position="right">
            <!-- 模板id -->
            <!-- <FFormItem label="算子ID">
                <FInput
                    v-model="formData.template_id"
                    class="query-input"
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem> -->
            <!-- 模板中文名 -->
            <FFormItem :label="$t('ruleTemplatelist.templateCNFullName')">
                <FInput
                    v-model="formData.cn_name"
                    class="query-input"
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <!-- 模板英文名 -->
            <FFormItem :label="$t('ruleTemplatelist.templateENFullName')">
                <FInput
                    v-model="formData.en_name"
                    class="query-input"
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <!-- 模板描述 -->
            <!-- <FFormItem :label="$t('ruleTemplatelist.templateDesc')">
                <FInput
                    v-model="formData.description"
                    class="query-input"
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem> -->
            <FFormItem :label="$t('ruleTemplatelist.creator')" prop="create_name">
                <FSelect v-model="formData.create_name" :options="formatProjectUserList" clearable filterable />
            </FFormItem>
            <!-- <FFormItem :label="$t('ruleTemplatelist.createTime')" prop="create_name">
                <FDatePicker
                    v-model="formData.createTimeRange"
                    type="datetimerange"
                    clearable
                    format="yyyy-MM-dd HH:mm:ss"
                    :placeholder="$t('common.pleaseSelect')"
                />
            </FFormItem> -->
            <FFormItem :label="$t('ruleTemplatelist.updator')" prop="modify_name">
                <FSelect v-model="formData.modify_name" :options="formatProjectUserList" clearable filterable />
            </FFormItem>
            <!-- <FFormItem :label="$t('ruleTemplatelist.updateTime')" prop="create_name">
                <FDatePicker
                    v-model="formData.updateTimeRange"
                    type="datetimerange"
                    clearable
                    format="yyyy-MM-dd HH:mm:ss"
                    :placeholder="$t('common.pleaseSelect')"
                />
            </FFormItem> -->
            <!-- 开发科室 -->
            <FFormItem :label="$t('ruleTemplatelist.developDepartment')">
                <FSelectCascader
                    v-model="formData.dev_department_id"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    valueField="code"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                ></FSelectCascader>
            </FFormItem>
            <!-- 运维科室 -->
            <FFormItem :label="$t('ruleTemplatelist.maintainDepartment')">
                <FSelectCascader
                    v-model="formData.ops_department_id"
                    :data="opsDivisions"
                    :loadData="loadOpsDivisions"
                    valueField="code"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                ></FSelectCascader>
            </FFormItem>
            <!-- 可见范围 -->
            <FFormItem :label="$t('ruleTemplatelist.visibleRange')" prop="action_range">
                <FSelectCascader
                    v-model="formData.action_range"
                    :data="visDivisions"
                    :loadData="loadVisDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    multiple
                    checkStrictly="all"
                    collapseTags
                    :collapseTagsLimit="3"
                    @change="visSelectChange" />
            </FFormItem>
        </FForm>
    </FModal>
</template>

<script setup>
import { cloneDeep } from 'lodash-es';
import {
    ref, computed, defineProps, defineEmits, defineExpose, watch, inject,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';

const { t: $t } = useI18n();
const emits = defineEmits(['success', 'update:show']);
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
});

const init = () => ({
    template_id: '',
    cn_name: '',
    en_name: '',
    dev_department_id: '',
    ops_department_id: '',
    action_range: [],
    create_name: '',
    modify_name: '',
    createTimeRange: [],
    updateTimeRange: [],
});
const formData = ref(init());
const backupFormData = ref(init()); // 备份表单数据
const backupSelectVisDate = ref([]); // 备份可见范围
const showModal = computed({
    get: () => props.show,
    set: (val) => {
        emits('update:show', val);
    },
});

watch(() => showModal.value, (show) => {
    if (show) {
        backupFormData.value = cloneDeep(formData.value);
    } else {
        formData.value = cloneDeep(backupFormData.value);
    }
});

const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData: devCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: opsDivisions,
    loadDivisions: loadOpsDivisions,
    curSubDepartData: opsCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: visDivisions,
    loadDivisions: loadVisDivisions,
    curSubDepartData: visCurSubDepartData,
} = useDivisions();
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);

const formatProjectUserList = inject('formatProjectUserList');

function isArrayNotEmpty(arr) {
    const bool = Array.isArray(arr) && arr.length > 0;
    return bool;
}

function handleOk() {
    showModal.value = false;
    backupFormData.value = cloneDeep(formData.value);
    const advanceTemp = cloneDeep(formData.value);
    if (isArrayNotEmpty(advanceTemp.action_range)) {
        const current = isArrayNotEmpty(selectVisDate.value) ? selectVisDate.value : backupSelectVisDate.value;
        advanceTemp.action_range = current.map(item => item.id);
        backupSelectVisDate.value = cloneDeep(current);
    } else {
        delete advanceTemp.action_range;
        backupSelectVisDate.value = [];
    }
    emits('success', advanceTemp);
}

function reset() {
    formData.value = init();
    backupFormData.value = init();
    backupSelectVisDate.value = [];
}

defineExpose({ reset });
</script>
<style lang="less" scoped></style>
