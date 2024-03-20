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
            <FFormItem prop="role_id" :label="$t('optionManagePage.roleName')">
                <FSelect
                    v-model="formModel.role_id"
                    valueField="role_id"
                    labelField="role_name"
                    filterable
                    :options="roleNameList"></FSelect>
            </FFormItem>
            <FFormItem prop="permission_id" :label="$t('optionManagePage.authorizationName')">
                <FSelect
                    v-model="formModel.permission_id"
                    valueField="permission_id"
                    labelField="splitName"
                    filterable
                    :options="permissionNameList"></FSelect>
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
import { FORM_MODE, FORM_MODES, MAX_PAGE_SIZE } from '@/assets/js/const';
import {
    addRolePermission, updateRolePermission, getPermissionNameList, getRoleNameList,
} from './api';

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
    [FORM_MODE.ADD]: `${$t('common.add')}${$t('optionManagePage.authorityManagement')}`,
    [FORM_MODE.EDIT]: `${$t('common.edit')}${$t('optionManagePage.authorityManagement')}`,
};
const title = computed(() => titleMapping[props.mode] || $t('common.prompt'));

// 表单对象
const formModel = reactive({
    uuid: '', // 角色权限id
    role_id: '', // 角色名id
    permission_id: '', // 权限名id
});
const formRule = {
    role_id: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: ['blur'],
        },
    ],
    permission_id: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: ['blur'],
        },
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
        action: addRolePermission,
        data: formModel,
        successEventName: 'on-add-success',
        successMessage: $t('toastSuccess.addSuccess'),
    },
    [FORM_MODE.EDIT]: {
        action: updateRolePermission,
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

const permissionNameList = ref([]);
const roleNameList = ref([]);

const initSelectOptions = () => {
    getRoleNameList({ page: 0, size: MAX_PAGE_SIZE }).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        roleNameList.value = data;
    });
    getPermissionNameList({ page: 0, size: MAX_PAGE_SIZE }).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        permissionNameList.value = data;
    });
};

onMounted(() => {
    // 事件监听注册
    eventbus.on('rolePermissionManagement:set-form-model', ({ uuid, role_id: roleID, permission_id: permissionID }) => {
        formModel.uuid = String(uuid) || '';
        formModel.role_id = roleID || null;
        formModel.permission_id = permissionID || null;
    });
    initSelectOptions();
});

onUnmounted(() => {
    eventbus.off('rolePermissionManagement:set-form-model');
});
</script>
