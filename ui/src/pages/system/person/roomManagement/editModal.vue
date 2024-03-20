<template>
    <FModal
        :show="show"
        :title="type === 'edit' ? $t('optionManagePage.editRoom') : $t('optionManagePage.addRoom')"
        width="484px"
        :maskClosable="false"
        @ok="handleOk"
        @cancel="handleCancel"
    >
        <FForm
            ref="formRef"
            class="modal-form"
            :model="formModel"
            :rules="formRule"
            labelPosition="right"
            :labelWidth="72"
        >
            <FFormItem
                prop="source_type"
                :label="$t('optionManagePage.roomFrom')"
            >
                <FSelect
                    v-model="formModel.source_type"
                    :disabled="type === 'edit'"
                    filterable
                    :options="fromList"
                    @change="onSourceTypeChange"
                />
            </FFormItem>
            <FFormItem
                prop="department_code"
                :label="$t('optionManagePage.beDepartment')"
            >
                <FSelect
                    v-model="formModel.department_code"
                    filterable
                    :options="departmentNameList"
                    valueField="code"
                    @change="onDepartmentChange"
                />
            </FFormItem>
            <FFormItem
                prop="sub_department_name"
                :label="$t('optionManagePage.roomName')"
            >
                <FSelect
                    v-if="formModel.source_type === 0"
                    v-model="formModel.sub_department_code"
                    filterable
                    clearable
                    :options="roomList"
                    @change="onRoomChange"
                />
                <FInput
                    v-else
                    v-model="formModel.sub_department_name"
                    :maxlength="50"
                    clearable
                    showWordLimit
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
            <FFormItem
                prop="sub_department_code"
                :label="$t('optionManagePage.roomCode')"
            >
                <FInput
                    v-model="formModel.sub_department_code"
                    :disabled="formModel.source_type === 0 || type === 'edit'"
                    clearable
                    :maxlength="50"
                    showWordLimit
                    :placeholder="$t('common.pleaseEnter')"
                />
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import { computed, ref, onUpdated } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { COMMON_REG } from '@/assets/js/const';
import { addRoom, editRoom, fetchRoomListByCode } from './api';
import { getDepartments } from '../departmentManagement/api';

const { t: $t } = useI18n();
const fromList = [
    { value: 0, label: 'HR系统' },
    { value: 1, label: '自定义' },
];
const roomList = ref([]);
// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    modelData: {
        type: Object,
        default: () => { },
    },
    type: {
        type: String,
        default: 'add', // 1. add 2.edit
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:modelData', 'update:show', 'save']);

const formRef = ref(null);
const formModel = computed({
    get: () => props.modelData,
    set: (value) => {
        emit('update:modelData', value);
    },
});
const formRule = ref({
    source_type: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: ['change'],
        },
    ],
    department_code: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
    ],
    sub_department_code: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
        { pattern: COMMON_REG.ONLY_EN_NAME, message: $t('common.lettersNumbers'), trigger: 'blur' },
    ],
    sub_department_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
        { pattern: COMMON_REG.CN_NAME2, message: $t('common.lettersNumbersCN'), trigger: 'blur' },
    ],
});
const departmentNameList = ref([]);
const getDepartmentNameList = async () => {
    try {
        const res = await getDepartments({
            page: 0,
            size: 100000000,
            source_type: formModel.value.source_type || 0,
        });
        departmentNameList.value = res.data.map(item => ({
            value: item.department_name,
            label: item.department_name,
            code: item.department_code,
        }));
        // console.log(departmentNameList.value)
    } catch (error) {
        console.log('getDepartmentNameList error:', error);
    }
};
const onSourceTypeChange = () => {
    Object.assign(formModel.value, {
        department_name: '',
        department_code: '',
        sub_department_code: '',
        sub_department_name: '',
    });
    getDepartmentNameList();
    roomList.value = [];
};
const roomListBak = ref([]);
const onDepartmentChange = async () => {
    formModel.value.department_name = departmentNameList.value.find(item => item.code === formModel.value.department_code)?.label || '';
    if (formModel.value.source_type === 0) {
        try {
            roomListBak.value = roomList.value;
            const result = await fetchRoomListByCode({ department_code: formModel.value.department_code });
            roomList.value = result.map(item => ({
                ...item,
                value: item.department_sub_id,
                label: item.department_sub_name,
            }));
            if (props.type === 'edit') {
                if (roomList.value.findIndex(item => item.value === formModel.value.sub_department_code) > -1) {
                    roomList.value = roomList.value.filter(item => item.value === formModel.value.sub_department_code);
                } else {
                    FMessage.warn('当前部门下不存在该科室');
                    formModel.value.department_code = '';
                    roomList.value = roomListBak.value;
                }
            }
        } catch (error) {
            console.log('fetchRoomListByCode error:', error);
        }
    }
};
const onRoomChange = () => {
    formModel.value.sub_department_name = roomList.value.find(item => item.value === formModel.value.sub_department_code)?.label || '';
};
const handleCancel = () => {
    emit('update:show', false);
    formRef.value.clearValidate();
    formRef.value.resetFields();
};
const handleOk = async () => {
    try {
        await formRef.value.validate();
        if (props.type === 'add') {
            await addRoom({
                source_type: formModel.value.source_type,
                department_name: formModel.value.department_name,
                department_code: formModel.value.department_code,
                sub_department_code: formModel.value.sub_department_code,
                sub_department_name: formModel.value.sub_department_name,
            });
            FMessage.success($t('toastSuccess.addSuccess'));
        } else if (props.type === 'edit') {
            await editRoom({
                source_type: formModel.value.source_type,
                department_name: formModel.value.department_name,
                department_code: formModel.value.department_code,
                sub_department_code: formModel.value.sub_department_code,
                sub_department_name: formModel.value.sub_department_name,
                sub_department_id: formModel.value.sub_department_id,
            });
            FMessage.success($t('toastSuccess.editSuccess'));
        }
        console.log(formModel.value);
        emit('update:show', false);
        emit('save');
        formRef.value.resetFields();
    } catch (e) {
        console.log('EditDrawer submit error:', e);
    }
};
onUpdated(async () => {
    if (props.show) {
        if (formModel.value.department_code) {
            const result = await fetchRoomListByCode({ department_code: formModel.value.department_code });
            roomList.value = result.map(item => ({
                ...item,
                value: item.department_sub_id,
                label: item.department_sub_name,
            }));
            roomList.value = roomList.value.filter(item => item.value === formModel.value.sub_department_code);
        }
        getDepartmentNameList();
    }
});
</script>
<style lang='less' scoped></style>
