<template>
    <FModal
        :show="show"
        :title="title"
        :width="484"
        :top="244"
        :maskClosable="false"
        @cancel="handleCancel">
        <FForm
            ref="formRef"
            class="modal-form"
            style="padding: 0;"
            :model="formModel"
            :rules="formRule"
            :labelWidth="54">
            <FFormItem prop="user_id" :label="$t('optionManagePage.userID')">
                <FInput
                    v-model="formModel.user_id"
                    clearable
                    :placehodler="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem prop="permission_id" :label="$t('optionManagePage.authorizationId')">
                <FInput
                    v-model="formModel.permission_id"
                    clearable
                    :placehodler="$t('common.pleaseEnter')" />
            </FFormItem>
        </FForm>
        <template #footer>
            <div class="btn-list" style="justify-content: flex-end;">
                <FButton class="btn-item" @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton
                    class="btn-item"
                    type="primary"
                    :disabled="isSubmitting"
                    @click="handleSubmit">
                    {{$t('common.ok')}}
                </FButton>
            </div>
        </template>
    </FModal>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, reactive, computed, onMounted, onUnmounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import { FORM_MODE, FORM_MODES } from '@/assets/js/const';
import { addUserSpecialPermission, updateUserSpecialPermission } from './api';

const { t: $t } = useI18n();

const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    mode: {
        validator: value => value === '' || FORM_MODES.includes(value),
        required: true,
    },
});

const emit = defineEmits([
    'update:show',
    'on-add-success',
    'on-edit-success',
]);

const formRef = ref(null);
const isSubmitting = ref(false);

// 弹窗标题
const titleMapping = {
    [FORM_MODE.ADD]: `${$t('common.add')}${$t('optionManagePage.userSpecialPermission')}`,
    [FORM_MODE.EDIT]: `${$t('common.edit')}${$t('optionManagePage.userSpecialPermission')}`,
};
const title = computed(() => titleMapping[props.mode] || $t('common.prompt'));

// 表单对象
const formModel = reactive({
    uuid: '', // 用户特殊权限id
    user_id: '', // 用户id
    permission_id: '', // 权限id
});
const formRule = {
    user_id: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur'] },
    ],
    permission_id: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur'] },
    ],
};

// 重置表单
const resetForm = () => {
    formRef.value.resetFields();
};

// 取消相关
const closeModal = () => {
    emit('update:show', false);
};
const handleCancel = () => {
    resetForm();
    closeModal();
};

// 新增、修改 提交相关
const requestDataMapping = {
    [FORM_MODE.ADD]: {
        action: addUserSpecialPermission,
        data: formModel,
        successEventName: 'on-add-success',
        successMessage: $t('toastSuccess.addSuccess'),
    },
    [FORM_MODE.EDIT]: {
        action: updateUserSpecialPermission,
        data: formModel,
        successEventName: 'on-edit-success',
        successMessage: $t('toastSuccess.editSuccess'),
    },
};
const handleSubmit = debounce(() => {
    formRef.value.validate().then(() => {
        const requestData = requestDataMapping[props.mode];
        if (!requestData) return;
        const {
            action,
            data,
            successEventName,
            successMessage,
        } = requestData;
        action(data).then(() => {
            FMessage.success(successMessage);
            handleCancel();
            emit(successEventName);
        }).finally(() => {
            isSubmitting.value = false;
        });
        isSubmitting.value = true;
    });
}, 300);

onMounted(() => {
    // 事件监听注册
    eventbus.on('userSpecialPermissionManagement:set-form-model', ({ uuid, user_id: userId, permission_id: permissionId }) => {
        formModel.uuid = String(uuid) || '';
        formModel.user_id = String(userId) || '';
        formModel.permission_id = String(permissionId) || '';
    });
});

onUnmounted(() => {
    eventbus.off('userSpecialPermissionManagement:set-form-model');
});
</script>
