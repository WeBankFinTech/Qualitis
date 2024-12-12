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
            labelPosition="right"
            :model="formModel"
            :rules="formRule"
            :labelWidth="70">
            <FFormItem prop="role_name" :label="$t('optionManagePage.characterName')">
                <FInput
                    v-model="formModel.role_name"
                    clearable
                    :placehodler="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem prop="zn_name" label="中文名">
                <FInput
                    v-model="formModel.zn_name"
                    clearable
                    :placehodler="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem prop="role_type" label="角色类型">
                <FSelect
                    v-model="formModel.role_type"
                    clearable
                    filterable
                    :placeholder="$t('common.pleaseSelect')"
                    @change="handleRoleTypeChange">
                    <FOption
                        v-for="item in roleTypes"
                        :key="item.code"
                        :label="item.message"
                        :value="item.code" />
                </FSelect>
            </FFormItem>
            <!-- 角色类型为职位角色的时候，禁用所属部门下拉选择 -->
            <FFormItem prop="department_name" label="所属部门">
                <FSelect
                    v-model="formModel.department_name"
                    clearable
                    filterable
                    :disabled="formModel.role_type === 2"
                    :placeholder="$t('common.pleaseSelect')">
                    <FOption
                        v-for="item in departments"
                        :key="item.department_id"
                        :label="item.department_name"
                        :value="item.department_name" />
                </FSelect>
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
import { getDepartments } from '@/pages/system/person/departmentManagement/api';
import { number } from 'echarts/core';
import { addRole, updateRole, getRoleTypes } from './api';

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
// 部门列表
const departments = ref([]);
// 角色类型列表
const roleTypes = ref([]);
const handleGetPreparedData = () => Promise.all([
    getDepartments({ page: 0, size: MAX_PAGE_SIZE }),
    getRoleTypes({}),
]);

// 弹窗标题
const titleMapping = {
    [FORM_MODE.ADD]: `${$t('common.add')}${$t('optionManagePage.role')}`,
    [FORM_MODE.EDIT]: `${$t('common.edit')}${$t('optionManagePage.role')}`,
};
const title = computed(() => titleMapping[props.mode] || $t('common.prompt'));

// 表单对象
const formModel = reactive({
    role_id: '', // 角色id
    role_name: '', // 角色名
    zn_name: '',
    role_type: null,
    department_name: '', // 部门名
});
const formRule = {
    role_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur'] },
    ],
    zn_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur'] },
    ],
    role_type: [
        {
            required: true, type: number, message: $t('common.notEmpty'), trigger: ['blur'],
        },
    ],
};

const handleRoleTypeChange = () => {
    if (formModel.role_type === 2) {
        formModel.department_name = '';
    }
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
        action: addRole,
        data: formModel,
        successEventName: 'on-add-success',
        successMessage: $t('toastSuccess.addSuccess'),
    },
    [FORM_MODE.EDIT]: {
        action: updateRole,
        data: formModel,
        successEventName: 'on-edit-success',
        successMessage: $t('toastSuccess.editSuccess'),
    },
};
const handleSubmit = debounce(() => {
    console.log(formModel);
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
    handleGetPreparedData().then(([departmentsRes, roleTypeRes]) => {
        departments.value = departmentsRes.data;
        roleTypes.value = roleTypeRes;
    });

    // 事件监听注册
    eventbus.on('roleManagement:set-form-model', ({
        role_id: roleId, role_name: roleName, zn_name, role_type, department_name: departmentName,
    }) => {
        formModel.role_id = roleId || '';
        formModel.role_name = roleName || '';
        formModel.department_name = departmentName || '';
        // eslint-disable-next-line camelcase
        formModel.zn_name = zn_name || '';
        // eslint-disable-next-line camelcase
        formModel.role_type = role_type || '';
    });
});

onUnmounted(() => {
    eventbus.off('roleManagement:set-form-model');
});
</script>
