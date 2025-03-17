<template>
    <div>
        <FModal
            v-if="show"
            :show="show"
            :title="modelType === 'add' ? $t('_.新增订阅') : $t('_.编辑订阅')"
            :width="600"
            :maskClosable="false"
            @ok="handleSubmit"
            @cancel="handleCancel"
        >
            <FForm
                ref="formRef"
                class="project-form"
                labelPosition="right"
                :model="formModel"
                :rules="formRule"
                :labelWidth="68"
            >
                <FFormItem prop="project_ids" :label="$t('_.项目名称')">
                    <FSelect
                        v-model="formModel.project_ids"
                        clearable
                        filterable
                        multiple
                        :options="projectNameList"
                    ></FSelect>
                </FFormItem>
                <FFormItem prop="receiver" :label="$t('_.接收人')">
                    <FInput
                        v-model="formModel.receiver"
                        clearable
                        :maxlength="128"
                        :placeholder="$t('common.pleaseEnter')"
                    />
                </FFormItem>
                <FFormItem prop="execution_frequency" :label="$t('_.执行频率')">
                    <FSelect
                        v-model="formModel.execution_frequency"
                        clearable
                        filterable
                        :options="frequencyOptions"
                    ></FSelect>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>

<script setup>

import {
    ref, reactive, defineProps, defineEmits, onMounted, computed, nextTick, inject,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { FMessage } from '@fesjs/fes-design';
import { addOprReport, editOprReport } from '../api.js';

const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    modelType: String,
    modelData: {
        type: Object,
        default: () => { },
    },
});
const emit = defineEmits(['update:show', 'save']);
const { t: $t } = useI18n();
const {
    projectNameList,
    loadProjectNameList,
} = useDataList();
const projectType = inject('projectType');
const formRef = ref(null);
const formModel = computed({
    get: () => props.modelData,
});
const formRule = {
    project_ids: [
        { required: true, message: $t('common.notEmpty') },
    ],
    receiver: [
        { required: true, message: $t('common.notEmpty') },
    ],
    execution_frequency: [
        { required: true, message: $t('common.notEmpty') },
    ],
};
const resetForm = () => {
    formRef.value.clearValidate();
    formModel.value.project_ids = [];
    formModel.value.receiver = '';
    formModel.value.execution_frequency = null;
};
const handleSubmit = async () => {
    if (props.modelType === 'add') {
        try {
            await formRef.value.validate();
            await addOprReport(formModel.value);
            FMessage.success($t('_.新增订阅成功'));
            emit('save');
            emit('update:show', false);
            resetForm();
        } catch (err) {
            if (err.code === '400') {
                FMessage.error(err.message);
            }
        }
    } else {
        const params = {
            execution_frequency: formModel.value.execution_frequency,
            project_ids: formModel.value.project_ids,
            receiver: formModel.value.receiver,
            id: formModel.value.id,
        };
        try {
            await formRef.value.validate();
            await editOprReport(params);
            FMessage.success($t('_.编辑订阅成功'));
            emit('save');
            emit('update:show', false);
            resetForm();
        } catch (err) {
            if (err.code === '400') {
                FMessage.error(err.message);
            }
        }
    }
};
const handleCancel = () => {
    resetForm();
    emit('update:show', false);
};
const frequencyOptions = reactive([
    { value: '1', label: $t('_.每天') },
    { value: '2', label: $t('_.每周') },
]);
onMounted(() => {
    // 普通项目传1  工作流项目传2
    loadProjectNameList(projectType);
});

</script>

<style>

</style>
