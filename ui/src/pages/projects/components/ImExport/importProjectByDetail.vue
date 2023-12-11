<template>
    <FModal :show="show" :width="600"
            :title="$t('myProject.importProject')"
            :maskClosable="false"
            @ok="handleSave"
            @cancel="handleCancel"
    >
        <FForm
            ref="importFormRef"
            :model="importForm"
            :rules="importFormRule"
            labelPosition="right"
            :labelWidth="108"
        >
            <FFormItem v-if="type === 'table'" prop="update" :label="`操作类型`">
                <FRadioGroup v-model="importForm.update" :cancelable="false" @change="changeUpdate">
                    <FRadio :value="false">新建项目</FRadio>
                    <FRadio :value="true">更新已有项目</FRadio>
                </FRadioGroup>
            </FFormItem>
            <FFormItem v-if="importForm.update" prop="project_id" :label="`项目名称`">
                <FSelect v-model="importForm.project_id" :placeholder="`请选择项目`" :options="projectNameList" filterable @change="changeImportProject" />
            </FFormItem>
            <FFormItem prop="upload_type" :label="`导入方式`">
                <FSelect v-model="importForm.upload_type" :placeholder="`请选择导入方式`" :options="importTypeList" @change="changeUploadType" />
            </FFormItem>
            <template v-if="importForm.upload_type === 1">
                <FFormItem prop="zip_path" :label="`上传文件`" style="align-items: center">
                    <div class="form-edit-input">
                        <FButton v-if="!importForm.zip_path && !uploading" @click="uploadFile"><UploadOutlined />上传文件</FButton>
                        <div v-else-if="uploading" class="uploading"><FSpin />上传中</div>
                        <FileBtn v-else :file="importForm.zip_path" @delFile="delFile" />
                    </div>
                    <div class="file-tooltip">支持.zip文件格式，不超过10M，请按照目录规范上传 excel 的压缩包。</div>
                </FFormItem>
            </template>
            <FFormItem :label="`差异化变量`" class="diff-form-label">
                <DifVariables ref="difVariablesRef" parentType="import"></DifVariables>
            </FFormItem>
        </FForm>
        <template #footer>
            <FSpace :size="CONDITIONBUTTONSPACE" justify="end">
                <FButton @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" :loading="submitLoading" @click="handleSave">{{$t('common.save')}}</FButton>
            </FSpace>
        </template>
        <input
            v-show="false"
            :ref="el => { if (el) fileInputRefs = el }"
            type="file"
            class="btn-file-input"
            :accept="acceptType"
            @change="importFunc(file)"
        />
    </FModal>
</template>
<script setup>
import {
    toRaw, ref, defineProps, defineEmits, onMounted, computed, watchEffect,
} from 'vue';
import { useI18n, request, useModel } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import DifVariables from '@/pages/projects/components/ImExport/difVariables';
import { UploadOutlined } from '@fesjs/fes-design/es/icon';
import FileBtn from '@/pages/rules/functionManagement/components/fileBtn';
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { uploadProject } from '@/pages/projects/api';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { cloneDeep } from 'lodash-es';

const { t: $t } = useI18n();
const {
    projectNameList,
    importTypeList,
    loadProjectNameList,
} = useDataList();
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    // type有两种，detail-项目详情页的，table-项目表格页的
    type: {
        type: String,
        required: true,
    },
    project: {
        type: Object,
        required: false,
    },
});
const emit = defineEmits(['update:show', 'uploadSucess']);
const initImportForm = () => {
    const initForm = {
        upload_type: 1,
        zip_path: '',
        dif_array: [],
    };
    if (props.type === 'table') {
        initForm.update = false;
        initForm.project_id = '';
    }
    return initForm;
};
const submitLoading = ref(false);
const importForm = ref(initImportForm());
const importFormRef = ref(null);
const difVariablesRef = ref(null);
const isfirstOpen = ref(true);
const initialState = useModel('@@initialState');
const isPermissionFunc = ref(null);
const curPid = computed(() => props.project?.project_id || importForm.value.project_id);
const cacheProject = ref({});
watchEffect(async () => {
    console.log(`curId changed to ${curPid.value}`);
    const {
        isPermissionCompute,
    } = useProjectAuth(curPid.value, initialState.userName);
    isPermissionFunc.value = isPermissionCompute;
});
// 上传文件
const fileInputRefs = ref(null);
const acceptType = ref('.zip');
const uploading = ref(false);
const uploadFile = () => {
    console.log('uploadFile', uploadFile);
    fileInputRefs.value?.click();
};
const importFunc = async () => {
    console.log('importFunc', importFunc);
    try {
        const fileInputRef = fileInputRefs.value;
        if (!fileInputRef) return;
        const maxSize = 10 * 1024 * 1024; // 10M
        const file = fileInputRef.files[0];
        console.log('file size:', file.size);
        if (file.size > maxSize) {
            FMessage.warning('文件大小超过10M,请重新上传。');
            return;
        }
        const formData = new FormData();
        formData.append('file', file);
        uploading.value = true;
        const res = await request('/api/v1/projector/project/batch/upload_zip_from_local', formData, 'post');
        uploading.value = false;
        importForm.value.zip_path = res;
        importFormRef.value.clearValidate();
    } catch (err) {
        console.log('importFunc-error');
        console.warn(err);
        uploading.value = false;
    }
};

const delFile = () => {
    importForm.value.zip_path = '';
    fileInputRefs.value.value = '';
};

const handleCancel = () => {
    emit('update:show', false);
    importForm.value = initImportForm();
    difVariablesRef.value.clearData();
    isfirstOpen.value = true;
};
const handleParams = () => {
    const {
        upload_type, zip_path,
    } = importForm.value;
    const params = {
        upload_type, zip_path,
    };
    if (props.type === 'detail' || importForm.value.update) {
        params.project_id = props.project?.project_id || importForm.value.project_id;
    }
    params.diff_variables = toRaw(difVariablesRef.value.getDifData());
    return params;
};
const afterConfirmSave = async () => {
    const params = handleParams();
    try {
        submitLoading.value = true;
        await uploadProject(params);
        FMessage.success($t('engineConfig.saveSuccess'));
        emit('uploadSucess');
        submitLoading.value = false;
    } catch (e) {
        console.error(e);
        submitLoading.value = false;
    }
    handleCancel();
};

const handleSave = async () => {
    try {
        let r = [];
        if (importForm.value.upload_type === 1) {
            r = await Promise.all([importFormRef.value.validate(), difVariablesRef.value.valid()]);
        }
        let isImport = true;
        if (props.type === 'table' && importForm.value.update) { // 只对已有项目做权限校验
            isImport = await isPermissionFunc.value();
        }
        if (!r.includes(false) && isImport) {
            importForm.value.dif_array = difVariablesRef.value.getDifData();
            // 项目列表页，导入且为更新已有项目时，1：本地导入方式 -提示导入会完全替换项目信息
            if (importForm.value.upload_type === 1 && importForm.value.update && props.type === 'table') {
                FModal.confirm({
                    title: '系统提示',
                    content: '导入的项目信息将完全替换现有项目的信息，是否确认导入并替换',
                    okText: '确认导入',
                    cancelText: '取消',
                    async onOk() {
                        console.log('确认导入');
                        afterConfirmSave();
                    },
                    async onCancel() {
                        console.log('取消导入');
                        handleCancel();
                    },
                });
            } else {
                afterConfirmSave();
            }
        }
        if (!isImport) {
            FMessage.warning('导入权限不足');
        }
    } catch (e) {
        console.log(e);
    }
};
const importFormRule = ref({
    project_id: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: '不能为空',
        },
    ],
    update: [
        {
            type: 'boolean', required: true, message: '不能为空',
        },
    ],
    upload_type: [
        {
            type: 'number', required: true, message: '不能为空',
        },
    ],
    zip_path: [
        {
            required: true, message: '不能为空',
        },
    ],
});
const changeUpdate = (v) => {
    importForm.value = initImportForm();
    importForm.value.update = v;
    cacheProject.value = cloneDeep(importForm.value);
};
const changeImportProject = async () => {
    cacheProject.value = cloneDeep(importForm.value);
    difVariablesRef.value.clearData();
};
const changeUploadType = (e) => {
    importForm.value.zip_path = '';
    difVariablesRef.value.clearData();
};
onMounted(async () => {
    await loadProjectNameList();
    if (props.project) {
        cacheProject.value = props.project;
    }
});
</script>
<style lang="less" scoped>
:deep(.diff-form-label){
    .fes-form-item-content{
        display: flow-root;
    }
}
.file-tooltip {
    margin-left:16px;
    color: #93949B;
}
</style>
