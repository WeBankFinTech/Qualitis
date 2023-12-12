<template>
    <FModal
        :show="show"
        :title="$t('common.newProject')"
        :top="244"
        :width="484"
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
            <FFormItem prop="project_name" :label="$t('common.projectName')">
                <FInput
                    v-model="formModel.project_name"
                    clearable
                    showWordLimit
                    :maxlength="64"
                    :placehodler="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem prop="cn_name" :label="$t('common.cnName')">
                <FInput
                    v-model="formModel.cn_name"
                    clearable
                    :maxlength="128"
                    :placehodler="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem :label="$t('dataSourceManagement.subSystem')" prop="sub_system_name">
                <FInput
                    v-model="formModel.sub_system_name"
                    clearable
                    :maxlength="128"
                    :placehodler="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem prop="description" :label="$t('label.projectIntro')">
                <FInput
                    v-model="formModel.description"
                    clearable
                    type="textarea"
                    :maxlength="500"
                    :placehodler="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem :label="$t('common.itemTag')">
                <TagEditor :tags="formModel.project_label" />
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    ref, reactive, defineProps, defineEmits, inject,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { COMMON_REG, FORM_MODE, FORM_MODES } from '@/assets/js/const';
import TagEditor from './TagEditor';
import { addProject } from '../api';
import useDataSource from '../hooks/useDataSource';

const props = defineProps({
    show: Boolean,
    mode: {
        validator: value => value === '' || FORM_MODES.includes(value),
        required: true,
    },
    subSystemList: {
        type: Array,
        required: true,
    },
});
const emit = defineEmits(['update:show', 'on-success']);
const { t: $t } = useI18n();

const formRef = ref(null);
const formModel = reactive({
    project_name: '',
    cn_name: '',
    sub_system_id: '',
    sub_system_name: '',
    description: '',
    project_label: [],
});
const formRule = {
    project_name: [
        { required: true, message: $t('common.notEmpty') },
        { pattern: COMMON_REG.EN_NAME_64, message: $t('myProject.projectEnNameRegTips') },
    ],
    description: [
        { required: true, message: $t('common.notEmpty') },
    ],
};

const closeModal = () => {
    emit('update:show', false);
};

const resetForm = () => {
    formRef.value.resetFields();
    formModel.project_label = [];
    formModel.sub_system_name = '';
};

const handleCancel = () => {
    resetForm();
    closeModal();
};

const handleSubmit = async () => {
    const validation = await formRef.value.validate();
    if (validation && !validation.valid) return;
    let message;
    if (props.mode === FORM_MODE.ADD) {
        message = $t('toastSuccess.addSuccess');
        await addProject(formModel);
    } else if (props.mode === FORM_MODE.EDIT) {
        // TODO: 实现编辑项目接口调用
        message = $t('toastSuccess.editSuccess');
    }
    FMessage.success(message);
    handleCancel();
    emit('on-success', props.mode);
};

// 设置子系统名
const setSubSystemName = (val) => {
    const enName = props.subSystemList.find(v => v.value === val)?.enName || '';
    formModel.sub_system_name = enName;
};
</script>
<style lang="less" scoped>
.fes-form {
    &.project-form {
        padding: 0;
        .fes-form-item {
            :deep(.fes-form-item-label) {
                justify-content: flex-end;
            }
            &:last-of-type {
                margin-bottom: 0;
            }
        }
    }
}
</style>
