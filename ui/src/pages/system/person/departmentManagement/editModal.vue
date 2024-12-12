<template>
    <FModal
        :show="show"
        :title="title"
        :width="484"
        :maskClosable="false"
        @cancel="handleCancel"
    >
        <FForm
            ref="formRef"
            class="modal-form"
            style="padding: 0;"
            :model="formModel"
            :rules="formRule"
            :labelWidth="72"
        >
            <FFormItem
                prop="source_type"
                :label="$t('optionManagePage.departmentFrom')"
            >
                <FSelect
                    v-model="formModel.source_type"
                    :disabled="mode === 'EDIT'"
                    filterable
                    :options="fromList"
                    @change="onSourceTypeChange"
                />
            </FFormItem>
            <FFormItem
                prop="department_name"
                :label="$t('optionManagePage.departmentName')"
            >
                <FSelect
                    v-if="formModel.source_type === 0"
                    v-model="formModel.department_code"
                    filterable
                    clearable
                    labelField="department_name"
                    valueField="department_code"
                    :options="departmentList"
                    @change="onDepartmentChange"
                />
                <FInput
                    v-else
                    v-model="formModel.department_name"
                    :maxlength="50"
                    clearable
                    showWordLimit
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem
                prop="department_code"
                :label="$t('optionManagePage.departmentCode')"
            >
                <FInput
                    v-model="formModel.department_code"
                    :disabled="formModel.source_type === 0 || mode === 'EDIT'"
                    clearable
                    :maxlength="50"
                    showWordLimit
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
        </FForm>
        <template #footer>
            <div
                class="btn-list"
                style="justify-content: flex-end;"
            >
                <FButton
                    class="btn-item"
                    @click="handleCancel"
                >
                    {{$t('common.cancel')}}
                </FButton>
                <FButton
                    class="btn-item"
                    type="primary"
                    :disabled="isSubmitting"
                    @click="handleSubmit"
                >
                    {{$t('common.ok')}}
                </FButton>
            </div>
        </template>
    </FModal>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, computed, onMounted, onUnmounted, onUpdated, nextTick,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FInput, FMessage } from '@fesjs/fes-design';
import { debounce, cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import { FORM_MODE, FORM_MODES, COMMON_REG } from '@/assets/js/const';
import { addDepartment, updateDepartment, fetchDepartments } from './api';


const { t: $t } = useI18n();

const fromList = [
    { value: 0, label: 'HR系统' },
    { value: 1, label: '自定义' },
];

const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    mode: {
        validator: value => value === '' || FORM_MODES.includes(value),
        required: true,
    },
    departmentNameList: {
        type: Array,
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
    [FORM_MODE.ADD]: `${$t('common.add')}${$t('personnelManagePage.department')}`,
    [FORM_MODE.EDIT]: `${$t('common.edit')}${$t('personnelManagePage.department')}`,
};
const title = computed(() => titleMapping[props.mode] || $t('common.prompt'));

// 表单对象
const formModel = ref({
    source_type: 0,
    department_code: '', // 部门code
});
const formRule = {
    source_type: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: ['change'],
        },
    ],
    department_code: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
        { pattern: COMMON_REG.ONLY_EN_NAME, message: $t('common.lettersNumbers'), trigger: 'blur' },
    ],
    department_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
        { pattern: COMMON_REG.CN_NAME2, message: $t('common.lettersNumbersCN'), trigger: 'blur' },
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

const onSourceTypeChange = () => {
    formModel.value.department_code = '';
    formModel.value.department_name = '';
};

const departmentList = ref([]);

const onDepartmentChange = async (key) => {
    const target = departmentList.value.filter(item => item.department_code === key);
    if (target.length > 0 && props.mode === 'ADD' && props.departmentNameList.findIndex(item => item.value === target[0].department_name) > -1) {
        FMessage.warn('部门名已存在');
        await nextTick();
        formModel.value.department_code = '';
        formModel.value.department_name = '';
        console.log(formModel.value);
        return false;
    }
    if (target.length > 0) {
        const { department_code, department_name } = target[0];
        // eslint-disable-next-line camelcase
        formModel.value.department_code = department_code;
        // eslint-disable-next-line camelcase
        formModel.value.department_name = department_name;
    }
};

// 新增、修改 提交相关
const requestDataMapping = {
    [FORM_MODE.ADD]: {
        action: addDepartment,
        successEventName: 'on-add-success',
        successMessage: $t('toastSuccess.addSuccess'),
    },
    [FORM_MODE.EDIT]: {
        action: updateDepartment,
        successEventName: 'on-edit-success',
        successMessage: $t('toastSuccess.editSuccess'),
    },
};
const handleSubmit = debounce(() => {
    formRef.value.validate().then(() => {
        const requestData = requestDataMapping[props.mode];
        if (!requestData) return;
        requestData.data = formModel.value;
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
const departmentListBak = ref([]);
onMounted(async () => {
    // 事件监听注册
    eventbus.on('departmentManagement:set-form-model', (data = {}) => {
        formModel.value = cloneDeep(data);
    });
    const dplist = await fetchDepartments();
    departmentList.value = (dplist || []).map((item) => {
        // 1是不可选择，0是可以选择
        item.disabled = item.disable === '1';
        return item;
    });
    departmentListBak.value = departmentList.value;
});

onUpdated(() => {
    if (props.show && props.mode === 'EDIT') {
        departmentList.value = departmentList.value.filter(item => item.department_code === formModel.value.department_code);
        console.log(formModel.value, departmentList.value);
    } else {
        departmentList.value = departmentListBak.value;
    }
});

onUnmounted(() => {
    eventbus.off('departmentManagement:set-form-model');
});
</script>
